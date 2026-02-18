package com.tfg.music_cloud_backend.controller;

import com.tfg.music_cloud_backend.dto.PlaylistSongDto;
import com.tfg.music_cloud_backend.service.PlaylistSongService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlistSongs")
@AllArgsConstructor
public class PlaylistSongController {

    private final PlaylistSongService playlistSongService;

    // Añadir canción a playlist
    @PostMapping
    public ResponseEntity<PlaylistSongDto> addSongToPlaylist(
            @RequestParam Long playlistId,
            @RequestParam Long songId) {

        PlaylistSongDto saved =
                playlistSongService.addSongToPlaylist(playlistId, songId);

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // Eliminar canción de playlist (relación)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeSongFromPlaylist(@PathVariable Long id) {

        playlistSongService.removeSongFromPlaylist(id);
        return ResponseEntity.noContent().build();
    }

    // Listar canciones de una playlist con orden opcional
    @GetMapping("/playlist/{playlistId}")
    public ResponseEntity<List<PlaylistSongDto>> getSongsInPlaylist(
            @PathVariable Long playlistId,
            @RequestParam(required = false) String sortBy) {

        return ResponseEntity.ok(
                playlistSongService.getSongsInPlaylist(playlistId, sortBy)
        );
    }
}
