package com.tfg.music_cloud_backend.controller;

import com.tfg.music_cloud_backend.dto.AlbumDto;
import com.tfg.music_cloud_backend.dto.ArtistDto;
import com.tfg.music_cloud_backend.service.ArtistService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/artists")
public class ArtistController {


    private final ArtistService artistService;

    @PostMapping
    public ResponseEntity<ArtistDto> createArtist(@RequestBody ArtistDto artistDto) {
        ArtistDto savedArtist = artistService.createArtist(artistDto);
        return new ResponseEntity<>(savedArtist, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDto> getArtistById(@PathVariable Long id) {
        ArtistDto artist = artistService.getArtistById(id);
        return ResponseEntity.ok(artist);
    }

    @GetMapping
    public ResponseEntity<List<ArtistDto>> getAllArtists() {
        List<ArtistDto> artists = artistService.getAllArtists();
        return ResponseEntity.ok(artists);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistDto> updateArtist(
            @PathVariable Long id,
            @RequestBody ArtistDto updatedArtist) {

        ArtistDto artist = artistService.updateArtist(id, updatedArtist);
        return ResponseEntity.ok(artist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }

    // ArtistController
    @GetMapping("/{id}/albums")
    public ResponseEntity<List<AlbumDto>> getAlbumsByArtistId(@PathVariable Long id) {
        return ResponseEntity.ok(artistService.getAlbumsByArtistId(id));
    }
}
