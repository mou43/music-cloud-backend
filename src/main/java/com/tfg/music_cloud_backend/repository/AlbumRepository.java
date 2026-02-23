package com.tfg.music_cloud_backend.repository;

import com.tfg.music_cloud_backend.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Long> {
    // Busca álbum por título y artista para evitar duplicados
    Optional<Album> findByTitleAndArtist_Name(String title, String artistName);
}