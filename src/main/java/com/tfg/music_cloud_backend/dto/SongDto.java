package com.tfg.music_cloud_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongDto {
    /*
    Explicacion de la clase:
        Representa un objeto que se usa para enviar y recibir datos en la API.
        Usamos la clase SongDto para comunicación con el exterior
        Usamos entity en la clase Song para comunicarnos con la bdd.
        Usos:
            Ocultar campos
            Cambiar estructura sin afectar DB
            Seguridad
            Escalabilidad
    */

    private Long id;
    private String title;
    private String artist;
    private String album;
    private Integer trackNumber;
    private Integer duration;
    private Integer year;
    private String coverPath;
    private String filePath;
}
