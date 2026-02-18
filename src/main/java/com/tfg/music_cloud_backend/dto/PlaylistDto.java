package com.tfg.music_cloud_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDto {

    private Long id;
    private String name;
    private String owner;
    private String coverPath;
    private List<PlaylistSongDto> playlistSongs = new ArrayList<>();

}