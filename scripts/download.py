import sys
import json
import yt_dlp

FFMPEG_PATH = "C:/Users/marta/Documents/ffmpeg-2026-02-18-git-52b676bb29-essentials_build/bin"

# Descarga el archivo de audio
def download_song(video_id, output_path, artist, album):
    url = f"https://music.youtube.com/watch?v={video_id}"

    ydl_opts = {
        "format": "bestaudio/best",
        "outtmpl": f"{output_path}/{artist}/{album}/%(title)s.%(ext)s",
        "ffmpeg_location": FFMPEG_PATH,
        "postprocessors": [{
            "key": "FFmpegExtractAudio",
            "preferredcodec": "mp3",
            "preferredquality": "192",
        }],
        "quiet": True,
        "youtube_include_dash_manifest": False
    }

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info = ydl.extract_info(url, download=True)
        file_path = ydl.prepare_filename(info).rsplit(".", 1)[0] + ".mp3"
        return file_path

if __name__ == "__main__":
    video_id = sys.argv[1]
    output_path = sys.argv[2]
    artist = sys.argv[3]
    album = sys.argv[4]

    file_path = download_song(video_id, output_path, artist, album)
    print(json.dumps({
        "status": "ok",
        "videoId": video_id,
        "filePath": file_path
    }))