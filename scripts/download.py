import sys
import json
import yt_dlp

FFMPEG_PATH = "C:/Users/marta/Documents/ffmpeg-2026-02-18-git-52b676bb29-essentials_build/bin"

def download_song(video_id, output_path, artist, album, title):
    url = f"https://music.youtube.com/watch?v={video_id}"

    ydl_opts = {
        "format": "bestaudio/best",
        "outtmpl": f"{output_path}/{artist}/{album}/{title}.%(ext)s",
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
        file_path = f"{output_path}/{artist}/{album}/{title}.mp3"
        return file_path

if __name__ == "__main__":
    video_id = sys.argv[1]
    output_path = sys.argv[2]
    artist = sys.argv[3]
    album = sys.argv[4]
    title = sys.argv[5]

    file_path = download_song(video_id, output_path, artist, album, title)
    print(json.dumps({
        "status": "ok",
        "videoId": video_id,
        "filePath": file_path
    }))