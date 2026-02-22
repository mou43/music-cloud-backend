package com.tfg.music_cloud_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.music_cloud_backend.dto.DownloadRequest;
import com.tfg.music_cloud_backend.repository.SongRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/*
    Se encarga de buscar canciones llamando al script search.py a través
    de PythonService. Devuelve los resultados como una lista de mapas
    clave-valor, donde cada mapa representa una canción con sus metadatos.

    Separamos esta lógica en su propio servicio porque la búsqueda
    es una responsabilidad distinta al CRUD de canciones (SongService).
    Así cada clase tiene una única responsabilidad.
*/

@Service
public class MusicSearchService {


    private final PythonService pythonService; // PythonService para ejecutar el script
    private final ObjectMapper objectMapper; // ObjectMapper se usa para convertir JSON en objetos Java
    private final String storagePath;
    private final SongRepository songRepository;

    public MusicSearchService(PythonService pythonService,
                              ObjectMapper objectMapper,
                              SongRepository songRepository,
                              @Value("${music.storage.path}") String storagePath) {
        this.pythonService = pythonService;
        this.objectMapper = objectMapper;
        this.storagePath = storagePath;
        this.songRepository = songRepository;
    }
    /*
    Usamos Map<String, Object> en vez de un DTO específico porque los
    resultados de búsqueda son datos temporales que no se guardan en BDD,
    solo se muestran al cliente para que seleccione una canción.
    */

    public List<Map<String, Object>> searchSongs(String query) {
        try {
            List<String> command = List.of(
                    "python",
                    pythonService.getScriptsPath() + "search.py", query // Construye el comando: ["python", "scripts/search.py", "query"]
            );

            String json = pythonService.executeScript(command); // PythonService ejecuta el comando y devuelve el JSON como String
            return objectMapper.readValue(json, new TypeReference<>() {});
            //ObjectMapper convierte ese JSON a List<Map<String, Object>> donde cada Map es una canción con sus campos (videoId, title, etc.)

        } catch (Exception e) {
            throw new RuntimeException("Error searching songs: " + e.getMessage());
        }
    }

    public void downloadSong(DownloadRequest request) {
        try {
            if ("album".equals(request.getMode())) {

                // 1. Obtener lista de tracks del álbum
                List<String> albumCommand = List.of(
                        "python",
                        pythonService.getScriptsPath() + "get_album.py",
                        request.getAlbumBrowseId()
                );
                String albumJson = pythonService.executeScript(albumCommand);
                Map<String, Object> albumData = objectMapper.readValue(albumJson, new TypeReference<>() {});
                List<Map<String, Object>> tracks = (List<Map<String, Object>>) albumData.get("tracks");

                // 2. Canción prioritaria primero
                tracks.sort((a, b) -> {
                    String id = request.getVideoId();
                    if (id.equals(a.get("videoId"))) return -1;
                    if (id.equals(b.get("videoId"))) return 1;
                    return 0;
                });

                // 3. Por cada track comprobar si existe y descargar si no
                for (Map<String, Object> track : tracks) {
                    String videoId = (String) track.get("videoId");
                    String title = (String) track.get("title");

                    if (videoId == null) continue;

                    // Comprobación por videoId
                    if (songRepository.findByVideoId(videoId).isPresent()) {
                        System.out.println("Ya existe por videoId, omitiendo: " + title);
                        continue;
                    }

                    // Comprobación por title + album + artista
                    if (songRepository.findByTitleAndAlbum_TitleAndAlbum_Artist_Name(
                            title, request.getAlbumTitle(), request.getArtist()).isPresent()) {
                        System.out.println("Ya existe por título+álbum+artista, omitiendo: " + title);
                        continue;
                    }

                    // Descarga individual de cada canción pendiente
                    List<String> command = List.of(
                            "python",
                            pythonService.getScriptsPath() + "download.py",
                            videoId,
                            storagePath,
                            request.getArtist(),
                            request.getAlbumTitle()
                    );
                    String result = pythonService.executeScript(command); // ← command, no songCommand
                    System.out.println("Descargada: " + title + " → " + result);
                }

            } else {
                // Modo canción individual
                if (songRepository.findByVideoId(request.getVideoId()).isPresent()) {
                    System.out.println("Canción ya existe por videoId, omitiendo.");
                    return;
                }

                if (songRepository.findByTitleAndAlbum_TitleAndAlbum_Artist_Name(
                        request.getTitle(), request.getAlbumTitle(), request.getArtist()).isPresent()) {
                    System.out.println("Canción ya existe por título+álbum+artista, omitiendo.");
                    return;
                }

                List<String> command = List.of(
                        "python",
                        pythonService.getScriptsPath() + "download.py",
                        request.getVideoId(),    // sys.argv[1]
                        storagePath,             // sys.argv[2]
                        request.getArtist(),     // sys.argv[3]
                        request.getAlbumTitle()  // sys.argv[4]
                );
                String json = pythonService.executeScript(command);
                System.out.println("Download result: " + json);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error downloading song: " + e.getMessage());
        }
    }

}