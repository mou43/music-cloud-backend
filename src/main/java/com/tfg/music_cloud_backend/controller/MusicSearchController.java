package com.tfg.music_cloud_backend.controller;

import com.tfg.music_cloud_backend.dto.DownloadRequest;
import com.tfg.music_cloud_backend.service.MusicSearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/*

    Expone el endpoint de búsqueda al exterior. Es el punto de entrada
    desde la app Android cuando el usuario escribe en el buscador.

    No contiene lógica, solo recibe la petición y delega en MusicSearchService.

    Endpoint expuesto:
        GET /api/search?q=Like Jennie
*/
@RestController
@RequestMapping("/api/search")
@AllArgsConstructor
public class MusicSearchController {

    private final MusicSearchService musicSearchService;

    /*
        La app Android llamará a este endpoint cada vez que el usuario
        escriba en el buscador (con debounce de 500ms para no saturar).
    */

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> searchSongs(
            @RequestParam String q) { // @RequestParam String q recibe el texto de búsqueda de la URL
        return ResponseEntity.ok(musicSearchService.searchSongs(q)); // Devuelve 200 OK con la lista de canciones encontradas en YouTube Music
    }

    @PostMapping("/download")
    public ResponseEntity<Void> downloadSong(@RequestBody DownloadRequest request) {
        musicSearchService.downloadSong(request);
        return ResponseEntity.accepted().build(); // 202 → la descarga se procesa
    }

}