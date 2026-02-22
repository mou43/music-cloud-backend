package com.tfg.music_cloud_backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/*
    Contiene los datos que la app Android envía cuando el usuario
    selecciona una canción del buscador y quiere reproducirla.
    Estos datos vienen del resultado de search.py.
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DownloadRequest {
    private String videoId;         // ID de la canción en YouTube Music
    private String albumBrowseId;   // ID del álbum para get_album()
    private String title;
    private String artist;
    private String albumTitle;
    private Integer duration;
    private String thumbnail;
    private String mode;            // "song" o "album"
}