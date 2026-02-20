import sys
import json
from ytmusicapi import YTMusic

#Búsqueda rápida para el cliente
def search_songs(query):
    ytmusic = YTMusic()
    results = ytmusic.search(query, filter="songs", limit=10)

    songs = []
    for item in results:
        try:
            songs.append({
                "videoId": item.get("videoId"),
                "title": item.get("title"),
                "artist": item["artists"][0]["name"] if item.get("artists") else None,
                "albumTitle": item["album"]["name"] if item.get("album") else None,
                "albumBrowseId": item["album"]["id"] if item.get("album") else None, # clave para get_album()
                "duration": item.get("duration_seconds"),
                "thumbnail": item["thumbnails"][-1]["url"].split("=")[0] + "=w500-h500-l90-rj" if item.get("thumbnails") else None
            })
        except Exception:
            continue

    print(json.dumps(songs))

if __name__ == "__main__":
    query = sys.argv[1]
    search_songs(query)