package com.tfg.music_cloud_backend.controller;

import com.tfg.music_cloud_backend.dto.SongDto;
import com.tfg.music_cloud_backend.entity.Song;
import com.tfg.music_cloud_backend.service.SongService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/songs")
public class SongController {
    /*
    Explicacion de la Clase:
        Maneja peticiones HTTP.
        Escucha peticiones como:
            POST /api/songs
            GET /api/songs
        El flujo es:
            1- Cliente envía JSON
            2- Controller recibe JSON
            3- Spring lo convierte en SongDto
            4- Controller llama al Service
            5- Service usa el Mapper
            6- Repository guarda en DB
            7- DB responde
            8- Service convierte a DTO
            9- Controller devuelve JSON


    */
    private SongService songService;

    // Build Add Song REST API
    @PostMapping
    public ResponseEntity<SongDto> createSong(@RequestBody SongDto songDto) {
        SongDto savedSong = songService.createSong(songDto);
        return new ResponseEntity<>(savedSong, HttpStatus.CREATED);
    }

    // Build Get Song REST API
    @GetMapping("{id}")
    public ResponseEntity<SongDto> getSongById(@PathVariable("id") Long songId) {
        SongDto songDto = songService.getSongById(songId);
        return ResponseEntity.ok(songDto);
    }

    // Build GetAll Songs REST API
    @GetMapping
    public ResponseEntity<List<SongDto>> getAllSongs(){
        List<SongDto> songs = songService.getAllSongs();
        return ResponseEntity.ok(songs);
    }

    // Build Update Song REST API
    @PutMapping("{id}")
    public ResponseEntity<SongDto> updateSong(@PathVariable("id") Long songId,
                                              @RequestBody SongDto updatedSong) {
        SongDto songDto = songService.updateSong(songId, updatedSong);
        return ResponseEntity.ok(songDto);
    }

    // Build Delete Song REST API
    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteSong(@PathVariable("id") Long songId) {
        songService.deleteSong(songId);
        return ResponseEntity.ok("Song deleted successfully!");
    }


}
