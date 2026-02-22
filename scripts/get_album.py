import sys
import json
from ytmusicapi import YTMusic

# Obtiene metadatos del álbum
def get_album(browse_id):
    ytmusic = YTMusic()
    album = ytmusic.get_album(browse_id)

    tracks = []
    for track in album.get("tracks", []):
        tracks.append({
            "videoId": track.get("videoId"),
            "title": track.get("title"),
            "trackNumber": track.get("trackNumber"),
            "duration": track.get("duration_seconds")
        })

    print(json.dumps({
        "title": album.get("title"),
        "year": album.get("year"),
        "thumbnail": album["thumbnails"][-1]["url"].split("=")[0] + "=w500-h500-l90-rj" if album.get("thumbnails") else None,
        "tracks": tracks
    }))

if __name__ == "__main__":
    browse_id = sys.argv[1]
    get_album(browse_id)