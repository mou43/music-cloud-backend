import sys
import json
from ytmusicapi import YTMusic


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
                "albumBrowseId": item["album"]["id"] if item.get("album") else None,
                "duration": item.get("duration_seconds"),
                "thumbnail": item["thumbnails"][-1]["url"].split("=")[0] + "=w500-h500-l90-rj" if item.get(
                    "thumbnails") else None
            })
        except Exception:
            continue

    print(json.dumps(songs))


def search_albums(query):
    ytmusic = YTMusic()
    results = ytmusic.search(query, filter="albums", limit=10)

    albums = []
    for item in results:
        try:
            albums.append({
                "browseId": item.get("browseId"),
                "title": item.get("title"),
                "artist": item["artists"][0]["name"] if item.get("artists") else None,
                "year": item.get("year"),
                "thumbnail": item["thumbnails"][-1]["url"].split("=")[0] + "=w500-h500-l90-rj" if item.get(
                    "thumbnails") else None
            })
        except Exception:
            continue

    print(json.dumps(albums))

def search_artists(query):
    ytmusic = YTMusic()
    results = ytmusic.search(query, filter="artists", limit=10)

    artists = []
    for item in results:
        try:
            artists.append({
                "browseId": item.get("browseId"),
                "name": item.get("artist"),
                "thumbnail": item["thumbnails"][-1]["url"].split("=")[0] + "=w120-h120-l90-rj" if item.get("thumbnails") else None
            })
        except Exception:
            continue

    print(json.dumps(artists))


if __name__ == "__main__":
    query = sys.argv[1]
    search_type = sys.argv[2] if len(sys.argv) > 2 else "songs"

    if search_type == "albums":
        search_albums(query)
    elif search_type == "artists":
        search_artists(query)
    else:
        search_songs(query)