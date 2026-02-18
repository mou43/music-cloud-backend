package com.tfg.music_cloud_backend.repository;

import com.tfg.music_cloud_backend.entity.PlaylistSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {

    List<PlaylistSong> findByPlaylistId(Long playlistId);
}
