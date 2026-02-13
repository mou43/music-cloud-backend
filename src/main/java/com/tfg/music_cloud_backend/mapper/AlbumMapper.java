package com.tfg.music_cloud_backend.mapper;

import com.tfg.music_cloud_backend.dto.AlbumDto;
import com.tfg.music_cloud_backend.entity.Album;


public class AlbumMapper {

    public static AlbumDto mapToAlbumDto(Album album) {
        if (album == null) return null;

        return new AlbumDto(
                album.getId(),
                album.getTitle(),
                album.getYear(),
                album.getCoverPath(),
                album.getArtist() != null ? album.getArtist().getId() : null,
                album.getArtist() != null ? album.getArtist().getName() : null
        );
    }

    public static Album mapToAlbum(AlbumDto albumDto) {
        if (albumDto == null) return null;

        Album album = new Album();
        album.setId(albumDto.getId());
        album.setTitle(albumDto.getTitle());
        album.setYear(albumDto.getYear());
        album.setCoverPath(albumDto.getCoverPath());
        // Aquí no seteamos Artist directamente; lo hará el Service
        return album;
    }

}
