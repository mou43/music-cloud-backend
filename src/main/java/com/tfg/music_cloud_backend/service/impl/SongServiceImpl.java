package com.tfg.music_cloud_backend.service.impl;

import com.tfg.music_cloud_backend.dto.SongDto;
import com.tfg.music_cloud_backend.entity.Album;
import com.tfg.music_cloud_backend.entity.Song;
import com.tfg.music_cloud_backend.entity.SongStatus;
import com.tfg.music_cloud_backend.exception.ResourceNotFoundException;
import com.tfg.music_cloud_backend.mapper.SongMapper;
import com.tfg.music_cloud_backend.repository.AlbumRepository;
import com.tfg.music_cloud_backend.repository.SongRepository;
import com.tfg.music_cloud_backend.service.SongService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
/*
Explicacion de la Interfaz:
    SE DEFINEN (O IMPLEMENTAN) ESOS METODOS
*/
@Service
public class SongServiceImpl implements SongService {

    /*
    NOTA SOBRE LA INYECCIÓN DE DEPENDENCIAS:
    Se usa constructor manual en lugar de @AllArgsConstructor de Lombok porque
    esta clase mezcla dos tipos de inyección distintos:
        - Beans de Spring (SongRepository, AlbumRepository) → Spring los inyecta automáticamente
        - @Value (storagePath) → inyecta un valor del application.properties, NO un bean
    Lombok no sabe anotar @Value al generar el constructor automático, lo que provoca
    que Spring no encuentre un bean de tipo String y falle al arrancar.
    La solución es definir el constructor manualmente y anotar el parámetro con @Value.
    Si en el futuro se elimina storagePath, se puede volver a usar @AllArgsConstructor.
*/

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final String storagePath;

    public SongServiceImpl(SongRepository songRepository,
                           AlbumRepository albumRepository,
                           @Value("${music.storage.path}") String storagePath) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.storagePath = storagePath;
    }

    @Override
    public SongDto createSong(SongDto songDto) {
        // Validar que Album exista
        Album album = albumRepository.findById(songDto.getAlbumId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Album does not exist with given id: " + songDto.getAlbumId()
                ));

        // Mapear DTO -> Entity
        Song song = SongMapper.mapToSong(songDto, album);

        // Guardar en DB
        Song savedSong = songRepository.save(song);

        // Mapear Entity -> DTO
        return SongMapper.mapToSongDto(savedSong);
    }

    @Override
    public SongDto getSongById(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song does not exist with given id: " + songId
                ));
        return SongMapper.mapToSongDto(song);
    }

    @Override
    public List<SongDto> getAllSongs() {
        List<Song> songs = songRepository.findAll();
        return songs.stream()
                .map((song) -> SongMapper.mapToSongDto(song))
                .collect(Collectors.toList());
    }

    @Override
    public SongDto updateSong(Long songId, SongDto updatedSongDto) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song does not exist with given id: " + songId
                ));

        song.setTitle(updatedSongDto.getTitle());
        song.setTrackNumber(updatedSongDto.getTrackNumber());
        song.setDuration(updatedSongDto.getDuration());
        song.setFilePath(updatedSongDto.getFilePath());
        if (updatedSongDto.getStatus() != null) song.setStatus(updatedSongDto.getStatus());

        // Actualizar Album si viene un albumId diferente
        if (updatedSongDto.getAlbumId() != null
                && (song.getAlbum() == null || !song.getAlbum().getId().equals(updatedSongDto.getAlbumId()))) {

            Album album = albumRepository.findById(updatedSongDto.getAlbumId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Album does not exist with given id: " + updatedSongDto.getAlbumId()
                    ));

            song.setAlbum(album);
        }

        Song savedSong = songRepository.save(song);
        return SongMapper.mapToSongDto(savedSong);
    }

    @Override
    public void deleteSong(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song does not exist with given id: " + songId));

        // Si tiene archivo físico lo borramos también
        if (song.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(song.getFilePath()));
            } catch (IOException e) {
                // Si falla el borrado físico no interrumpimos el borrado de BDD
                System.out.println("No se pudo borrar el archivo: " + song.getFilePath());
            }
        }

        songRepository.deleteById(songId);
    }

    @Override
    public ResponseEntity<Resource> streamSong(Long songId, String rangeHeader) {
        // 1. Buscar la canción
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song does not exist with given id: " + songId));

        // 2. Verificar que está lista
        if (song.getStatus() != SongStatus.READY || song.getFilePath() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // 3. Obtener el archivo
        Path filePath = Paths.get(song.getFilePath());
        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            long fileSize = Files.size(filePath);
            long start = 0;
            long end = fileSize - 1;

            // 4. Procesar Range header si existe
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring(6).split("-");
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
            }

            long contentLength = end - start + 1;

            // 5. Leer solo el rango solicitado
            InputStream inputStream = Files.newInputStream(filePath);
            inputStream.skip(start);
            byte[] data = inputStream.readNBytes((int) contentLength);
            inputStream.close();

            // 6. Construir respuesta
            HttpStatus status = rangeHeader != null ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;

            return ResponseEntity.status(status)
                    .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                    .header(HttpHeaders.CONTENT_RANGE,
                            "bytes " + start + "-" + end + "/" + fileSize)
                    .body(new ByteArrayResource(data));

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public List<SongDto> getSongsByAlbumId(Long albumId) {
        albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Album does not exist with given id: " + albumId
                ));

        List<Song> songs = songRepository.findByAlbumId(albumId);
        return songs.stream()
                .map(SongMapper::mapToSongDto)
                .collect(Collectors.toList());
    }
}