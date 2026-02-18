package com.tfg.music_cloud_backend.mapper;

import com.tfg.music_cloud_backend.dto.PlaylistDto;
import com.tfg.music_cloud_backend.entity.Playlist;

import java.util.stream.Collectors;

public class PlaylistMapper {

    public static PlaylistDto mapToPlaylistDto(Playlist playlist) {
        if (playlist == null) return null;

        return new PlaylistDto(
                playlist.getId(),
                playlist.getName(),
                playlist.getOwner(),
                playlist.getCoverPath(),
                playlist.getPlaylistSongs().stream()
                        .map(PlaylistSongMapper::mapToPlaylistSongDto)
                        .collect(Collectors.toList())
        );

    }

    public static Playlist mapToPlaylist(PlaylistDto playlistDto) {
        if (playlistDto == null) return null;

        Playlist playlist = new Playlist();
        playlist.setId(playlistDto.getId());
        playlist.setName(playlistDto.getName());
        playlist.setOwner(playlistDto.getOwner());
        playlist.setCoverPath(playlistDto.getCoverPath());

        return playlist;

    }

}
