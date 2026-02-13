package com.tfg.music_cloud_backend.service;

import com.tfg.music_cloud_backend.dto.AlbumDto;

import java.util.List;

public interface AlbumService {

    AlbumDto createAlbum(AlbumDto albumDto);

    AlbumDto getAlbumById(Long albumId);

    List<AlbumDto> getAllAlbums();

    AlbumDto updateAlbum(Long albumId, AlbumDto updatedAlbumDto);

    void deleteAlbum(Long albumId);
}

