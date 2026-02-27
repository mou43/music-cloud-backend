import sys
import json
from ytmusicapi import YTMusic

def get_artist(channel_id):
    ytmusic = YTMusic()
    artist = ytmusic.get_artist(channel_id)

    thumbnails = artist.get("thumbnails", [])

    # Banner → el thumbnail más grande disponible
    banner = thumbnails[-1]["url"].split("=")[0] + "=w1440-h600-p-l90-rj" if thumbnails else None

    # Thumbnail cuadrado → el segundo thumbnail
    thumbnail = thumbnails[1]["url"].split("=")[0] + "=w120-h120-l90-rj" if len(thumbnails) > 1 else None

    albums = []
    for album in artist.get("albums", {}).get("results", []):
        albums.append({
            "browseId": album.get("browseId"),
            "title": album.get("title"),
            "year": album.get("type"),
            "thumbnail": album["thumbnails"][-1]["url"].split("=")[0] + "=w500-h500-l90-rj" if album.get("thumbnails") else None
        })

    print(json.dumps({
        "name": artist.get("name"),
        "channelId": artist.get("channelId"),
        "thumbnail": thumbnail,
        "banner": banner,
        "monthlyListeners": artist.get("monthlyListeners"),
        "albums": albums
    }))

if __name__ == "__main__":
    channel_id = sys.argv[1]
    get_artist(channel_id)