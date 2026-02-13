package com.tfg.music_cloud_backend.service.impl;

import com.tfg.music_cloud_backend.dto.SongDto;
import com.tfg.music_cloud_backend.entity.Album;
import com.tfg.music_cloud_backend.entity.Song;
import com.tfg.music_cloud_backend.exception.ResourceNotFoundException;
import com.tfg.music_cloud_backend.mapper.SongMapper;
import com.tfg.music_cloud_backend.repository.AlbumRepository;
import com.tfg.music_cloud_backend.repository.SongRepository;
import com.tfg.music_cloud_backend.service.SongService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SongServiceImpl implements SongService {
    /*
    Explicacion de la Clase:
        Aquí está la lógica del programa.
        El flujo es:
            1- Recibe DTO
            2- Lo convierte a Entity
            3- Lo guarda en DB
            4- Lo convierte otra vez a DTO
            5- Lo devuelve
    */
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;

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
                .orElseThrow(() -> new ResourceNotFoundException("Song does not exist with given id: " + songId));

        songRepository.deleteById(songId);
    }
}
