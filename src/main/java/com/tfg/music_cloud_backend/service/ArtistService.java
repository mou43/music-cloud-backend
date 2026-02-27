package com.tfg.music_cloud_backend.service;

import com.tfg.music_cloud_backend.dto.AlbumDto;
import com.tfg.music_cloud_backend.dto.ArtistDto;

import java.util.List;

public interface ArtistService {

    ArtistDto createArtist(ArtistDto artistDto);

    ArtistDto getArtistById(Long artistId);

    List<ArtistDto> getAllArtists();

    ArtistDto updateArtist(Long artistId, ArtistDto updatedArtistDto);

    void deleteArtist(Long artistId);

    List<AlbumDto> getAlbumsByArtistId(Long artistId); // Usamos este metodo para buscar mostrar albumes de x artista

}
