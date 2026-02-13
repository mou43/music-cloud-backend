package com.tfg.music_cloud_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDto {

    private Long id;
    private String title;
    private Integer year;
    private String coverPath;
    private Long artistId;
    private String artistName;
}
