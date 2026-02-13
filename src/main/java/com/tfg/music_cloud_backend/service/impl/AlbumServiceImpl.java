package com.tfg.music_cloud_backend.service.impl;

import com.tfg.music_cloud_backend.dto.AlbumDto;
import com.tfg.music_cloud_backend.entity.Album;
import com.tfg.music_cloud_backend.entity.Artist;
import com.tfg.music_cloud_backend.exception.ResourceNotFoundException;
import com.tfg.music_cloud_backend.mapper.AlbumMapper;
import com.tfg.music_cloud_backend.repository.AlbumRepository;
import com.tfg.music_cloud_backend.repository.ArtistRepository;
import com.tfg.music_cloud_backend.service.AlbumService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    @Override
    public AlbumDto createAlbum(AlbumDto albumDto) {
        Artist artist = artistRepository.findById(albumDto.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Artist does not exist with given id: " + albumDto.getArtistId()
                ));

        Album album = AlbumMapper.mapToAlbum(albumDto);
        album.setArtist(artist);

        Album savedAlbum = albumRepository.save(album);

        return AlbumMapper.mapToAlbumDto(savedAlbum);
    }

    @Override
    public AlbumDto getAlbumById(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Album does not exist with given id: " + albumId
                ));

        return AlbumMapper.mapToAlbumDto(album);
    }

    @Override
    public List<AlbumDto> getAllAlbums() {
        List<Album> albums = albumRepository.findAll();
        return albums.stream()
                .map(AlbumMapper::mapToAlbumDto)
                .collect(Collectors.toList());
    }

    @Override
    public AlbumDto updateAlbum(Long albumId, AlbumDto updatedAlbumDto) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Album does not exist with given id: " + albumId
                ));

        // Actualizar campos simples
        album.setTitle(updatedAlbumDto.getTitle());
        album.setYear(updatedAlbumDto.getYear());
        album.setCoverPath(updatedAlbumDto.getCoverPath());

        // Actualizar Artist si es diferente
        if (updatedAlbumDto.getArtistId() != null
                && (album.getArtist() == null || !album.getArtist().getId().equals(updatedAlbumDto.getArtistId()))) {

            Artist artist = artistRepository.findById(updatedAlbumDto.getArtistId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Artist does not exist with given id: " + updatedAlbumDto.getArtistId()
                    ));
            album.setArtist(artist);
        }

        Album savedAlbum = albumRepository.save(album);
        return AlbumMapper.mapToAlbumDto(savedAlbum);
    }

    @Override
    public void deleteAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Album does not exist with given id: " + albumId
                ));

        albumRepository.delete(album);
    }
}
