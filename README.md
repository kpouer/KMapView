![Java CI with Maven](https://github.com/kpouer/KMapView/workflows/Java%20CI%20with%20Maven/badge.svg)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.kpouer/k-mapview/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.kpouer/k-mapview)
# K-MapView

A Swing component able to show a map

## Introduction

This component will download from a tile server and show them.
It is a simple component able to add markers. But there is no search ability included.

## Dependency

Available through Maven central

```xml
<dependency>
    <groupId>com.kpouer</groupId>
    <artifactId>k-mapview</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Tiles servers

You will have to find a tile server. One of the most common choice is OSM tiles.
You can find various tiles servers here https://wiki.openstreetmap.org/wiki/Tile_servers
Beware, the term of use varies and most of them will not allow you to use them in a commercial product.

Tiles will be stored in a disk cache, but that cache is not managed by this component, it is up to you
to clean it when you want.

Retrieving queries can be load balanced between multiple servers.

## Example

```java
import com.kpouer.mapview.tile.DefaultTileServer;
import com.kpouer.mapview.tile.cache.ImageCacheImpl;

import javax.swing.*;
import java.io.IOException;

public class Sample {
    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame();
        MapView contentPane = new MapView(new DefaultTileServer(256,
                                                                1,
                                                                18,
                                                                2,
                                                                new ImageCacheImpl("Waze", "cache", 1000),
                                                                "https://a.tile.openstreetmap.org/${z}/${x}/${y}.png",
                                                                "https://b.tile.openstreetmap.org/${z}/${x}/${y}.png",
                                                                "https://c.tile.openstreetmap.org/${z}/${x}/${y}.png")));
        contentPane.setMouseLocationLabelVisible(false);
        frame.setContentPane(contentPane);

        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
```

## Note

The api is still under development and things might change

## Licence
K-MapView is open source and licensed under the Apache License 2.0.
