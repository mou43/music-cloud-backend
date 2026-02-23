package com.tfg.music_cloud_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.music_cloud_backend.dto.DownloadRequest;
import com.tfg.music_cloud_backend.entity.Album;
import com.tfg.music_cloud_backend.entity.Artist;
import com.tfg.music_cloud_backend.entity.Song;
import com.tfg.music_cloud_backend.entity.SongStatus;
import com.tfg.music_cloud_backend.repository.AlbumRepository;
import com.tfg.music_cloud_backend.repository.ArtistRepository;
import com.tfg.music_cloud_backend.repository.SongRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/*
    MusicSearchService — Servicio de búsqueda y descarga de música
    --------------------------------------------------------------
    Tiene dos responsabilidades principales:
        1. searchSongs()    → busca canciones en YouTube Music via search.py
        2. downloadSong()   → descarga canciones y guarda sus metadatos en BDD

    Usa PythonService para ejecutar los scripts Python externos.
    Usa los repositorios para comprobar duplicados y guardar datos.

    No tiene interfaz porque es un servicio de orquestación, no de dominio puro.
    El constructor es manual porque mezcla beans de Spring con @Value.
*/
@Service
public class MusicSearchService {

    private final PythonService pythonService;      // Ejecuta los scripts Python
    private final ObjectMapper objectMapper;         // Convierte JSON ↔ objetos Java
    private final SongRepository songRepository;     // Para comprobar/guardar canciones
    private final ArtistRepository artistRepository; // Para comprobar/guardar artistas
    private final AlbumRepository albumRepository;   // Para comprobar/guardar álbumes
    private final String storagePath;                // Ruta del HDD desde application.properties

    /*
        Constructor manual obligatorio porque @Value no es compatible con
        @AllArgsConstructor de Lombok. Ver nota en SongServiceImpl.
    */
    public MusicSearchService(PythonService pythonService,
                              ObjectMapper objectMapper,
                              SongRepository songRepository,
                              ArtistRepository artistRepository,
                              AlbumRepository albumRepository,
                              @Value("${music.storage.path}") String storagePath) {
        this.pythonService = pythonService;
        this.objectMapper = objectMapper;
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.albumRepository = albumRepository;
        this.storagePath = storagePath;
    }

    /*
        searchSongs() — Búsqueda en tiempo real para el cliente
        --------------------------------------------------------
        Recibe el texto que el usuario escribe en el buscador.
        Llama a search.py que consulta YouTube Music y devuelve resultados.

        Devuelve List<Map<String, Object>> en vez de un DTO específico porque
        los resultados son datos temporales que no se guardan en BDD,
        solo se muestran al cliente para que seleccione una canción.
    */
    public List<Map<String, Object>> searchSongs(String query) {
        try {
            List<String> command = List.of(
                    "python",
                    pythonService.getScriptsPath() + "search.py",
                    query
            );

            String json = pythonService.executeScript(command);
            return objectMapper.readValue(json, new TypeReference<>() {});

        } catch (Exception e) {
            throw new RuntimeException("Error searching songs: " + e.getMessage());
        }
    }

    /*
        downloadSong() — Descarga una canción o álbum completo
        -------------------------------------------------------
        Recibe un DownloadRequest con los metadatos de la canción seleccionada
        y el modo elegido por el usuario ("song" o "album").

        Flujo modo "song":
            1. Comprueba si la canción ya existe en BDD
            2. Si no existe, descarga con download.py
            3. Guarda los metadatos en BDD

        Flujo modo "album":
            1. Llama a get_album.py para obtener todos los tracks del álbum
            2. Ordena los tracks poniendo la canción seleccionada primero (prioritaria)
            3. Por cada track comprueba si existe en BDD
            4. Descarga solo los que faltan
            5. Guarda los metadatos de cada uno en BDD
    */
    public void downloadSong(DownloadRequest request) {
        try {
            if ("album".equals(request.getMode())) {

                // 1. Obtener lista de tracks del álbum via get_album.py
                List<String> albumCommand = List.of(
                        "python",
                        pythonService.getScriptsPath() + "get_album.py",
                        request.getAlbumBrowseId()
                );
                String albumJson = pythonService.executeScript(albumCommand);

                // Parseamos el JSON del álbum a un Map para acceder a sus campos
                Map<String, Object> albumData = objectMapper.readValue(albumJson, new TypeReference<>() {});
                List<Map<String, Object>> tracks = (List<Map<String, Object>>) albumData.get("tracks");

                // 2. Canción seleccionada por el usuario va primero (prioridad de descarga)
                tracks.sort((a, b) -> {
                    String id = request.getVideoId();
                    if (id.equals(a.get("videoId"))) return -1;
                    if (id.equals(b.get("videoId"))) return 1;
                    return 0;
                });
                System.out.println("VideoId prioritario: " + request.getVideoId());
                tracks.forEach(t -> System.out.println("Track: " + t.get("title") + " → " + t.get("videoId")));

                // Año del álbum, viene como String de ytmusicapi, lo convertimos a Integer
                Integer albumYear = null;
                if (albumData.get("year") != null) {
                    albumYear = Integer.parseInt((String) albumData.get("year"));
                }
                String albumThumbnail = (String) albumData.get("thumbnail");

                // 3. Por cada track del álbum
                for (Map<String, Object> track : tracks) {
                    String videoId = (String) track.get("videoId");
                    String title = (String) track.get("title");

                    if (videoId == null) continue;

                    // Comprobación 1 — mismo videoId
                    if (songRepository.findByVideoId(videoId).isPresent()) {
                        System.out.println("Ya existe por videoId, omitiendo: " + title);
                        continue;
                    }

                    // Comprobación 2 — mismo title + album + artista (distinta fuente)
                    if (songRepository.findByTitleAndAlbum_TitleAndAlbum_Artist_Name(
                            title, request.getAlbumTitle(), request.getArtist()).isPresent()) {
                        System.out.println("Ya existe por título+álbum+artista, omitiendo: " + title);
                        continue;
                    }

                    // 4. Descargar la canción
                    List<String> command = List.of(
                            "python",
                            pythonService.getScriptsPath() + "download.py",
                            videoId,
                            storagePath,
                            request.getArtist(),
                            request.getAlbumTitle()
                    );
                    String resultJson = pythonService.executeScript(command);
                    Map<String, Object> resultMap = objectMapper.readValue(resultJson, new TypeReference<>() {});

                    // 5. Si la descarga fue exitosa guardamos los metadatos en BDD
                    if ("ok".equals(resultMap.get("status"))) {
                        saveMetadata(
                                title,
                                (Integer) track.get("trackNumber"),
                                (Integer) track.get("duration"),
                                (String) resultMap.get("filePath"),
                                videoId,
                                request.getArtist(),
                                request.getAlbumTitle(),
                                albumYear,
                                albumThumbnail
                        );
                    }
                }

            } else {
                // Modo canción individual

                // Comprobación 1 — mismo videoId
                if (songRepository.findByVideoId(request.getVideoId()).isPresent()) {
                    System.out.println("Canción ya existe por videoId, omitiendo.");
                    return;
                }

                // Comprobación 2 — mismo title + album + artista (distinta fuente)
                if (songRepository.findByTitleAndAlbum_TitleAndAlbum_Artist_Name(
                        request.getTitle(), request.getAlbumTitle(), request.getArtist()).isPresent()) {
                    System.out.println("Canción ya existe por título+álbum+artista, omitiendo.");
                    return;
                }

                // Obtener metadatos completos del álbum para tener year y trackNumber
                List<String> albumCommand = List.of(
                        "python",
                        pythonService.getScriptsPath() + "get_album.py",
                        request.getAlbumBrowseId()
                );
                String albumJson = pythonService.executeScript(albumCommand);
                Map<String, Object> albumData = objectMapper.readValue(albumJson, new TypeReference<>() {});

                // Buscar el track específico dentro del álbum
                List<Map<String, Object>> tracks = (List<Map<String, Object>>) albumData.get("tracks");
                Map<String, Object> trackData = tracks.stream()
                        .filter(t -> request.getVideoId().equals(t.get("videoId")))
                        .findFirst()
                        .orElse(null);

                Integer trackNumber = trackData != null ? (Integer) trackData.get("trackNumber") : null;
                Integer duration = trackData != null ? (Integer) trackData.get("duration") : request.getDuration();
                Integer albumYear = albumData.get("year") != null ?
                        Integer.parseInt((String) albumData.get("year")) : null;
                String albumThumbnail = (String) albumData.get("thumbnail");

                // Descargar la canción
                List<String> command = List.of(
                        "python",
                        pythonService.getScriptsPath() + "download.py",
                        request.getVideoId(),
                        storagePath,
                        request.getArtist(),
                        request.getAlbumTitle()
                );
                String json = pythonService.executeScript(command);
                Map<String, Object> resultMap = objectMapper.readValue(json, new TypeReference<>() {});

                // Si la descarga fue exitosa guardamos los metadatos en BDD
                saveMetadata(
                        request.getTitle(),
                        trackNumber,
                        duration, // ← variable calculada desde get_album.py
                        (String) resultMap.get("filePath"),
                        request.getVideoId(),
                        request.getArtist(),
                        request.getAlbumTitle(),
                        albumYear,
                        albumThumbnail // ← también usa albumThumbnail en vez de request.getThumbnail()
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Error downloading song: " + e.getMessage());
        }
    }

    /*
        saveMetadata() — Guarda Artist, Album y Song en BDD
        ----------------------------------------------------
        Sigue el patrón "buscar o crear":
            - Si el Artist ya existe en BDD lo reutiliza, si no lo crea
            - Si el Album ya existe en BDD lo reutiliza, si no lo crea
            - Siempre crea una Song nueva con status READY y el filePath del HDD

        Este método es privado porque solo lo usa esta clase internamente,
        no debe ser accesible desde fuera del servicio.
    */
    private void saveMetadata(String title, Integer trackNumber, Integer duration,
                              String filePath, String videoId,
                              String artistName, String albumTitle,
                              Integer year, String coverPath) {

        // 1. Buscar o crear Artist
        // orElseGet() ejecuta el bloque solo si el Optional está vacío (no encontrado)
        Artist artist = artistRepository.findByName(artistName)
                .orElseGet(() -> {
                    Artist newArtist = new Artist();
                    newArtist.setName(artistName);
                    return artistRepository.save(newArtist);
                });

        // 2. Buscar o crear Album
        Album album = albumRepository.findByTitleAndArtist_Name(albumTitle, artistName)
                .orElseGet(() -> {
                    Album newAlbum = new Album();
                    newAlbum.setTitle(albumTitle);
                    newAlbum.setYear(year);
                    newAlbum.setCoverPath(coverPath);
                    newAlbum.setArtist(artist);
                    return albumRepository.save(newAlbum);
                });

        // 3. Crear Song con todos sus metadatos y status READY
        // Status READY significa que el archivo está disponible en el HDD
        Song song = new Song();
        song.setTitle(title);
        song.setTrackNumber(trackNumber);
        song.setDuration(duration);
        song.setFilePath(filePath);
        song.setVideoId(videoId);
        song.setStatus(SongStatus.READY);
        song.setAlbum(album);
        songRepository.save(song);

        System.out.println("Metadatos guardados en BDD: " + title + " → " + filePath);
    }
}