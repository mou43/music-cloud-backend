package com.tfg.music_cloud_backend.service.impl;

import com.tfg.music_cloud_backend.dto.ArtistDto;
import com.tfg.music_cloud_backend.entity.Artist;
import com.tfg.music_cloud_backend.exception.ResourceNotFoundException;
import com.tfg.music_cloud_backend.mapper.ArtistMapper;
import com.tfg.music_cloud_backend.repository.ArtistRepository;
import com.tfg.music_cloud_backend.service.ArtistService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final ArtistRepository artistRepository;

    @Override
    public ArtistDto createArtist(ArtistDto artistDto) {
        // Mapear DTO -> Entity
        Artist artist = ArtistMapper.mapToArtist(artistDto);

        // Guardar en DB
        Artist savedArtist = artistRepository.save(artist);

        // Mapear Entity -> DTO
        return ArtistMapper.mapToArtistDto(savedArtist);
    }

    @Override
    public ArtistDto getArtistById(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist does not exist with given id: " + artistId
                ));

        return ArtistMapper.mapToArtistDto(artist);
    }

    @Override
    public List<ArtistDto> getAllArtists() {
        List<Artist> artists = artistRepository.findAll();
        return artists.stream()
                .map(ArtistMapper::mapToArtistDto)
                .collect(Collectors.toList());
    }

    @Override
    public ArtistDto updateArtist(Long artistId, ArtistDto updatedArtistDto) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist does not exist with given id: " + artistId
                ));

        // Actualizar campos
        artist.setName(updatedArtistDto.getName());

        Artist savedArtist = artistRepository.save(artist);
        return ArtistMapper.mapToArtistDto(savedArtist);
    }

    @Override
    public void deleteArtist(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist does not exist with given id: " + artistId
                ));

        artistRepository.delete(artist);
    }
}
