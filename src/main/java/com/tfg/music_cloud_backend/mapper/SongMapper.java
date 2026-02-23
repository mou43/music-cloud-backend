package com.tfg.music_cloud_backend.mapper;

import com.tfg.music_cloud_backend.dto.SongDto;
import com.tfg.music_cloud_backend.entity.Album;
import com.tfg.music_cloud_backend.entity.Song;
import com.tfg.music_cloud_backend.entity.SongStatus;

public class SongMapper {
    /*
    Explicacion de la clase:
        Hace la convension de:
            Entity → DTO
            DTO → Entity
        Porque son objetos distintos y no deben mezclarse.
    */

    // SONG -> SONGDTO
    public static SongDto mapToSongDto(Song song) {
        if (song == null) return null;

        Album album = song.getAlbum(); // creamos album para obtener artistName ya que accedes a dos niveles de relaciones

        return new SongDto(
                song.getId(),
                song.getTitle(),
                song.getTrackNumber(),
                song.getDuration(),
                song.getFilePath(),
                song.getVideoId(),
                song.getStatus(),
                album != null ? album.getId() : null, // Si hay album devuelveme su id, si no devuelve null
                album != null ? album.getTitle() : null,
                album != null && album.getArtist() != null ? album.getArtist().getName() : null
        );
    }

    // SONGDTO -> SONG
    public static Song mapToSong(SongDto songDto, Album album) { // Recibimos album como parametro para song.setAlbum(album);
        if (songDto == null) return null;

        Song song = new Song();
        song.setId(songDto.getId());
        song.setTitle(songDto.getTitle());
        song.setTrackNumber(songDto.getTrackNumber());
        song.setDuration(songDto.getDuration());
        song.setFilePath(songDto.getFilePath());
        song.setVideoId(songDto.getVideoId());
        song.setStatus(songDto.getStatus() != null ? songDto.getStatus() : SongStatus.PENDING); // Si el DTO trae estado se usa, si no se pone PENDING
        song.setAlbum(album); // ahora asociamos la entidad Album directamente

        return song;
    }
}
