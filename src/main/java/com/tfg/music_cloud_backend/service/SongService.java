package com.tfg.music_cloud_backend.service;

import com.tfg.music_cloud_backend.dto.SongDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SongService {
    /*
    Explicacion de la Interfaz:
        Define qué operaciones existen.
        Separa el qué se puede hacer del cómo se hace.
        Esto permite cambiar implementación sin romper nada.
    */
    SongDto createSong(SongDto songDto);

    SongDto getSongById(Long songId);

    List<SongDto> getAllSongs();

    SongDto updateSong(Long songId, SongDto updatedSongDto);

    void deleteSong(Long songId);

    ResponseEntity<Resource> streamSong(Long songId, String rangeHeader);

    // IMPLEMENTAR EN EL FUTURO
    //SongDto updateSongStatus(Long songId, SongStatus status);

}
