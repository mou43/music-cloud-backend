package com.tfg.music_cloud_backend.repository;

import com.tfg.music_cloud_backend.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlbumRepository extends JpaRepository<Album, Long> {
}
