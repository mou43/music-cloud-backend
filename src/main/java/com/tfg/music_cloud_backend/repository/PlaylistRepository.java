package com.tfg.music_cloud_backend.repository;

import com.tfg.music_cloud_backend.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
}
