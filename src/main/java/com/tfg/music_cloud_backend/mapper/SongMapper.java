package com.tfg.music_cloud_backend.mapper;

import com.tfg.music_cloud_backend.dto.SongDto;
import com.tfg.music_cloud_backend.entity.Song;

public class SongMapper {
    /*
    Explicacion de la clase:
        Hace la convension de:
            Entity → DTO
            DTO → Entity
        Porque son objetos distintos y no deben mezclarse.
    */
    public static SongDto mapToSongDto(Song song) {
        return new SongDto(
                song.getId(),
                song.getTitle(),
                song.getArtist(),
                song.getAlbum(),
                song.getTrackNumber(),
                song.getDuration(),
                song.getYear(),
                song.getCoverPath(),
                song.getFilePath()
        );
    }

    public static Song mapToSong(SongDto songDto) {
        return new Song(
                songDto.getId(),
                songDto.getTitle(),
                songDto.getArtist(),
                songDto.getAlbum(),
                songDto.getTrackNumber(),
                songDto.getDuration(),
                songDto.getYear(),
                songDto.getCoverPath(),
                songDto.getFilePath()
        );
    }
}
