package com.tfg.music_cloud_backend.service.impl;

import com.tfg.music_cloud_backend.dto.PlaylistSongDto;
import com.tfg.music_cloud_backend.entity.Playlist;
import com.tfg.music_cloud_backend.entity.PlaylistSong;
import com.tfg.music_cloud_backend.entity.Song;
import com.tfg.music_cloud_backend.mapper.PlaylistSongMapper;
import com.tfg.music_cloud_backend.repository.PlaylistRepository;
import com.tfg.music_cloud_backend.repository.PlaylistSongRepository;
import com.tfg.music_cloud_backend.repository.SongRepository;
import com.tfg.music_cloud_backend.service.PlaylistSongService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlaylistSongServiceImpl implements PlaylistSongService {

    private final PlaylistSongRepository playlistSongRepository;
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;

    @Override
    public PlaylistSongDto addSongToPlaylist(Long playlistId, Long songId) {

        Playlist playlist = playlistRepository.findById(playlistId)
                .orElseThrow(() -> new RuntimeException("Playlist not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylist(playlist);
        playlistSong.setSong(song);
        playlistSong.setAddedAt(LocalDateTime.now());

        PlaylistSong saved = playlistSongRepository.save(playlistSong);

        return PlaylistSongMapper.mapToPlaylistSongDto(saved);
    }

    @Override
    public void removeSongFromPlaylist(Long playlistSongId) {
        PlaylistSong playlistSong = playlistSongRepository.findById(playlistSongId)
                .orElseThrow(() -> new RuntimeException("PlaylistSong not found"));

        playlistSongRepository.delete(playlistSong);
    }

    @Override
    public List<PlaylistSongDto> getSongsInPlaylist(Long playlistId, String sortBy) {

        List<PlaylistSong> playlistSongs =
                playlistSongRepository.findByPlaylistId(playlistId);

        if (sortBy != null) {
            switch (sortBy) {

                case "addedAt":
                    playlistSongs.sort(
                            Comparator.comparing(PlaylistSong::getAddedAt)
                    );
                    break;

                case "title":
                    playlistSongs.sort(
                            Comparator.comparing(ps -> ps.getSong().getTitle())
                    );
                    break;

                case "album":
                    playlistSongs.sort(
                            Comparator.comparing(ps -> ps.getSong().getAlbum().getTitle())
                    );
                    break;

                default:
                    break;
            }
        }

        return playlistSongs.stream()
                .map(PlaylistSongMapper::mapToPlaylistSongDto)
                .collect(Collectors.toList());
    }
}
