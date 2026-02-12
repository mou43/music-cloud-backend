package com.tfg.music_cloud_backend.repository;

import com.tfg.music_cloud_backend.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Long> {
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
