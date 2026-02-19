package com.tfg.music_cloud_backend.entity;

public enum SongStatus {
    PENDING, // Metadatos en BDD, sin archivo local
    DOWNLOADING, // yt-dlp en proceso
    READY, // Archivo disponible en HDD
    ERROR // Falló la descarga
}
