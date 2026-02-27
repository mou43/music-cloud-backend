package com.tfg.music_cloud_backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/*
    AlbumDownloadRequest — DTO de petición de descarga de álbum completo
    ---------------------------------------------------------------------
    Contiene los datos que la app Android envía cuando el usuario
    selecciona un álbum y quiere descargarlo completo.
    Viene del resultado de search.py con type=albums o de get_artist.py.
*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDownloadRequest {
    private String browseId;    // ID del álbum en YouTube Music para get_album.py
    private String title;       //NO USAR ALBUMTITLE Título del álbum para la ruta del HDD
    private String artist;      // Nombre del artista para la ruta del HDD
}