package com.tfg.music_cloud_backend.service;

import com.tfg.music_cloud_backend.dto.PlaylistSongDto;

import java.util.List;

public interface PlaylistSongService {

    PlaylistSongDto addSongToPlaylist(Long playlistId, Long songId);

    void removeSongFromPlaylist(Long playlistSongId);

    List<PlaylistSongDto> getSongsInPlaylist(Long playlistId, String sortBy);
}

