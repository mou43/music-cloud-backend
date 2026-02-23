package com.tfg.music_cloud_backend.repository;

import com.tfg.music_cloud_backend.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    // Busca artista por nombre para evitar duplicados
    Optional<Artist> findByName(String name);
}