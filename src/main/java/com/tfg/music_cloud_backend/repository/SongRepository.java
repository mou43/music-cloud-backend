package com.tfg.music_cloud_backend.repository;

import com.tfg.music_cloud_backend.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SongRepository extends JpaRepository<Song, Long> {

    // Busca por videoId para evitar descargar la misma fuente dos veces
    Optional<Song> findByVideoId(String videoId);

    // Busca por título + álbum + artista para evitar duplicados de distinta fuente
    Optional<Song> findByTitleAndAlbum_TitleAndAlbum_Artist_Name(String title, String albumTitle, String artistName);

    /*
    Explicacion de la Interfaz:
        Es la capa que habla directamente con la base de datos.
        Spring Data JPA genera automáticamente métodos como:
            save()
            findById()
            findAll()
            deleteById()
            etc
     */

}
