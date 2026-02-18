package com.tfg.music_cloud_backend.service;

import com.tfg.music_cloud_backend.dto.PlaylistDto;

import java.util.List;

public interface PlaylistService {

    PlaylistDto createPlaylist(PlaylistDto playlistDto);

    PlaylistDto getPlaylistById(Long playlistId);

    List<PlaylistDto> getAllPlaylists();

    PlaylistDto updatePlaylist(Long playlistId, PlaylistDto updatedPlaylistDto);

    void deletePlaylist(Long playlistId);

}
