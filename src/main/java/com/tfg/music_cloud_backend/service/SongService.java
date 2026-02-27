package com.tfg.music_cloud_backend.service;

import com.tfg.music_cloud_backend.dto.SongDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;
/*
Explicacion de la Interfaz:
    SE DECLARAN LOS METODOS
    Separa el qué se puede hacer del cómo se hace.
    Esto permite cambiar implementación sin romper nada.
*/
public interface SongService {

    SongDto createSong(SongDto songDto);

    SongDto getSongById(Long songId);

    List<SongDto> getAllSongs();

    SongDto updateSong(Long songId, SongDto updatedSongDto);

    void deleteSong(Long songId);

    ResponseEntity<Resource> streamSong(Long songId, String rangeHeader);

    List<SongDto> getSongsByAlbumId(Long albumId);

}
