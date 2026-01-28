package com.tfg.music_cloud_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "artist", nullable = false) // DE MOMENTO STRING PUEDE CONVERTIRSE EN TABLA
    private String artist;

    @Column(name = "album")
    private String album;

    @Column(name = "trackNumber")
    private Integer trackNumber; // USAR INTEGER PARA CAMPOS OPCIONALES // INT NO PUEDE SER NULL

    @Column(name = "duration", nullable = false)
    private int duration; // EN SEGUNDOS

    @Column(name = "year")
    private Integer year;

    @Column(name = "coverPath")
    private String coverPath; //MOSTRAR CARATULA

    @Column(name = "filePath")
    private String filePath;

    //METADATOS OPCIONALES? JSON?
    /*
    genres
    explicit
    popularity
    lyrics
    publisher
    isrc
     */
}
