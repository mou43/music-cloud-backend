package com.tfg.music_cloud_backend.repository;

import com.tfg.music_cloud_backend.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
}
