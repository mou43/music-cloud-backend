package com.tfg.music_cloud_backend.mapper;

import com.tfg.music_cloud_backend.dto.PlaylistSongDto;
import com.tfg.music_cloud_backend.entity.Playlist;
import com.tfg.music_cloud_backend.entity.PlaylistSong;

public class PlaylistSongMapper {

    public static PlaylistSongDto mapToPlaylistSongDto(PlaylistSong playlistSong) {
        if (playlistSong == null) return null;

        return  new PlaylistSongDto(
                playlistSong.getId(),
                playlistSong.getPlaylist() != null ? playlistSong.getPlaylist().getId() : null,
                playlistSong.getSong() != null ? playlistSong.getSong().getId() : null,
                playlistSong.getAddedAt()
        );

    }



    public static PlaylistSong mapToPlaylistSong(PlaylistSongDto playlistSongDto) {
        if (playlistSongDto == null) return null;

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setId(playlistSongDto.getId());
        playlistSong.setAddedAt(playlistSongDto.getAddedAt());

        // Las relaciones con Playlist y Song se deben establecer desde el servicio
        // porque necesitamos cargar las entidades desde la base de datos

        return playlistSong;


    }

}