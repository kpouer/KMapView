![Java CI with Maven](https://github.com/kpouer/KMapView/workflows/Java%20CI%20with%20Maven/badge.svg)

# K-MapView

A Swing component able to show a map

## Introduction

This component will download from a tile server and show them.

## Tiles servers

You will have to find a tile server. One of the most common choice is OSM tiles.
You can find various tiles servers here https://wiki.openstreetmap.org/wiki/Tile_servers
Beware, the term of use varies and most of them will not allow you to use them in a commercial product.

Tiles can be stored in a disk cache (recommended), but that cache is not managed by this component, it is up to you
to clean it when you want.

Retrieving queries can be load balanced between multiple servers.

## Example

```java
JFrame frame = new JFrame();
    frame.setContentPane(new MapView(new DefaultTileServer(256,
    1,
    18,
    2,
    new ImageCacheImpl("OSM", "cache", 1000),
    "https://a.tile.openstreetmap.org/${z}/${x}/${y}.png",
    "https://b.tile.openstreetmap.org/${z}/${x}/${y}.png",
    "https://c.tile.openstreetmap.org/${z}/${x}/${y}.png")));

    frame.setSize(800, 600);
    frame.setVisible(true);
```

## Note

The api is still under development and things might change
