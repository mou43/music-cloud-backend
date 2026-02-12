package com.tfg.music_cloud_backend.service.impl;

import com.tfg.music_cloud_backend.dto.SongDto;
import com.tfg.music_cloud_backend.entity.Song;
import com.tfg.music_cloud_backend.exception.ResourceNotFoundException;
import com.tfg.music_cloud_backend.mapper.SongMapper;
import com.tfg.music_cloud_backend.repository.SongRepository;
import com.tfg.music_cloud_backend.service.SongService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;
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
    private SongRepository songRepository;

    @Override
    public SongDto createSong(SongDto songDto) {

        Song song = SongMapper.mapToSong(songDto);
        Song savedSong = songRepository.save(song);

        return SongMapper.mapToSongDto(savedSong);
    }

    @Override
    public SongDto getSongById(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song does not exist with given id: " + songId));
        return SongMapper.mapToSongDto(song);
    }

    @Override
    public List<SongDto> getAllSongs() {
        List<Song> songs = songRepository.findAll();
        return songs.stream().map((song) -> SongMapper.mapToSongDto(song))
                .collect(Collectors.toList());
    }

    @Override
    public SongDto updateSong(Long songId, SongDto updatedSong) {
        Song song = songRepository.findById(songId).orElseThrow(
                () -> new ResourceNotFoundException("Song does not exist with given id: " + songId)
        );

        song.setTitle(updatedSong.getTitle());
        song.setArtist(updatedSong.getArtist());
        song.setAlbum(updatedSong.getAlbum());
        song.setTrackNumber(updatedSong.getTrackNumber());
        song.setDuration(updatedSong.getDuration());
        song.setYear(updatedSong.getYear());
        song.setCoverPath(updatedSong.getCoverPath());
        song.setFilePath(updatedSong.getFilePath());

        Song updatedSongObj = songRepository.save(song);


        return SongMapper.mapToSongDto(updatedSongObj);
    }

    @Override
    public void deleteSong(Long songId) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song does not exist with given id: " + songId));

        songRepository.deleteById(songId);
    }
}
