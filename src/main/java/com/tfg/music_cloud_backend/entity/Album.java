package com.tfg.music_cloud_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "albums")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "year")
    private Integer year;

    @Column(name = "coverPath")
    private String coverPath; //MOSTRAR CARATULA


    @ManyToOne(fetch = FetchType.LAZY)
    // El lado que tiene @JoinColumn es el dueño de la relación.
    @JoinColumn(name = "artist_id", nullable = false) // Creamos una columna artist_id en la tabla albums y la usamos como clave foranea hacia artists.id
    private Artist artist; // No significa que JPA guarde un objeto completo, Hibernate lo traduce a artist_id (FK)

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL)
    private List<Song> songs; //El campo que controla la relación está en la entidad Song y se llama "songs".
}
