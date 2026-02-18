package com.tfg.music_cloud_backend.service.impl;

import com.tfg.music_cloud_backend.dto.PlaylistDto;
import com.tfg.music_cloud_backend.entity.Playlist;
import com.tfg.music_cloud_backend.exception.ResourceNotFoundException;
import com.tfg.music_cloud_backend.mapper.PlaylistMapper;
import com.tfg.music_cloud_backend.repository.PlaylistRepository;
import com.tfg.music_cloud_backend.service.PlaylistService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private PlaylistRepository playlistRepository;

    @Override
    public PlaylistDto createPlaylist(PlaylistDto playlistDto) {
        Playlist playlist = PlaylistMapper.mapToPlaylist(playlistDto);
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return PlaylistMapper.mapToPlaylistDto(savedPlaylist);
    }

    @Override
    public PlaylistDto getPlaylistById(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Playlist not found with id: " + playlistId
                ));
        return PlaylistMapper.mapToPlaylistDto(playlist);
    }

    @Override
    public List<PlaylistDto> getAllPlaylists() {
        List<Playlist> playlists = playlistRepository.findAll();
        return playlists.stream()
                .map(PlaylistMapper::mapToPlaylistDto)
                .collect(Collectors.toList());
    }

    @Override
    public PlaylistDto updatePlaylist(Long playlistId, PlaylistDto updatedPlaylistDto) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Playlist not found with id: " + playlistId));

        playlist.setName(updatedPlaylistDto.getName());
        playlist.setOwner(updatedPlaylistDto.getOwner()); //Esto se supone que no será modificable
        playlist.setCoverPath(updatedPlaylistDto.getCoverPath());

        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return PlaylistMapper.mapToPlaylistDto(updatedPlaylist);
    }

    @Override
    public void deletePlaylist(Long playlistId) {
        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Playlist not found with id: " + playlistId));

        playlistRepository.delete(playlist);
    }
}
