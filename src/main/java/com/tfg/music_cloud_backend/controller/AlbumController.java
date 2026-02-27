package com.tfg.music_cloud_backend.controller;


import com.tfg.music_cloud_backend.dto.AlbumDto;
import com.tfg.music_cloud_backend.dto.SongDto;
import com.tfg.music_cloud_backend.service.AlbumService;
import com.tfg.music_cloud_backend.service.SongService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final SongService songService;

    @PostMapping
    public ResponseEntity<AlbumDto> createAlbum(@RequestBody AlbumDto albumDto) {
        AlbumDto savedAlbum = albumService.createAlbum(albumDto);
        return new ResponseEntity<>(savedAlbum, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<AlbumDto> getAlbumById(@PathVariable Long id) {
        AlbumDto album = albumService.getAlbumById(id);
        return ResponseEntity.ok(album);
    }

    @GetMapping
    public ResponseEntity<List<AlbumDto>> getAllAlbums() {
        List<AlbumDto> albums = albumService.getAllAlbums();
        return ResponseEntity.ok(albums);
    }

    @PutMapping("{id}")
    public ResponseEntity<AlbumDto> updateAlbum(
            @PathVariable Long id,
            @RequestBody AlbumDto updatedAlbum) {

        AlbumDto album = albumService.updateAlbum(id, updatedAlbum);
        return ResponseEntity.ok(album);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteAlbum(@PathVariable Long id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/songs")
    public ResponseEntity<List<SongDto>> getSongsByAlbumId(@PathVariable Long id) {
        return ResponseEntity.ok(songService.getSongsByAlbumId(id));
    }
}
