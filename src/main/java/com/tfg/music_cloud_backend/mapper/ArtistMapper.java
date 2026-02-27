package com.tfg.music_cloud_backend.mapper;

import com.tfg.music_cloud_backend.dto.ArtistDto;
import com.tfg.music_cloud_backend.entity.Artist;

public class ArtistMapper {

    public static ArtistDto mapToArtistDto(Artist artist) {
        if (artist == null) return null;

        return new ArtistDto(
                artist.getId(),
                artist.getName(),
                artist.getThumbnail(),
                artist.getBanner(),
                artist.getMonthlyListeners()
        );
    }

    public static Artist mapToArtist(ArtistDto artistDto) {
        if (artistDto == null) return null;

        Artist artist = new Artist();
        artist.setId(artistDto.getId());
        artist.setName(artistDto.getName());
        artist.setThumbnail(artistDto.getThumbnail());
        artist.setBanner(artistDto.getBanner());
        artist.setMonthlyListeners(artistDto.getMonthlyListeners());
        // No seteamos albums, se manejan con repositorios
        return artist;
    }
}
