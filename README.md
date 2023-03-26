# K-MapView
![Java CI with Maven](https://github.com/kpouer/KMapView/workflows/Java%20CI%20with%20Maven/badge.svg)
[![Maven Central](https://img.shields.io/maven-central/v/com.kpouer/k-mapview)](https://central.sonatype.com/artifact/com.kpouer/k-mapview/1.2.0/versions)

A Swing component able to show a map

## Introduction

This component will download from a tile server and show them.
It is a simple component able to add markers. But there is no search ability included.

![OSM Map with widgets](/doc/OSMWithWidget.jpg)

## Features

The map depends on a tile server. It can be OSM, but not necessary. You can find some examples here see https://wiki.openstreetmap.org/wiki/Tile_servers
It supports adding widgets to the map (dot, circles, or any other you create).

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
        MapView mapView = new MapView(new DefaultTileServer(256,
                                                                1,
                                                                18,
                                                                2,
                                                                new ImageCacheImpl("Waze", "cache", 1000),
                                                                "https://a.tile.openstreetmap.org/${z}/${x}/${y}.png",
                                                                "https://b.tile.openstreetmap.org/${z}/${x}/${y}.png",
                                                                "https://c.tile.openstreetmap.org/${z}/${x}/${y}.png")));
        String wkt2 = "POLYGON((12.493134831869419 41.91785801245087,12.559739446127232 41.88796130217409,12.4316800589202 41.874669382200494,12.469788884603794 41.96101743431704,12.493134831869419 41.91785801245087))";
        Polygon polygon = WKT.parseShape(wkt2);
        PolygonMarker marker = new PolygonMarker(polygon, 5, new Color(1.0f, 0.0f, 0.0f, 0.3f), true);
        mapView.addMarker(marker);
        contentPane.setMouseLocationLabelVisible(false);
        frame.setContentPane(contentPane);

        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
```

## Dependency

Available through Maven central

```xml
<dependency>
    <groupId>com.kpouer</groupId>
    <artifactId>k-mapview</artifactId>
    <version>1.0.1</version>
</dependency>
```

## Note

The api is still under development and things might change

## Licence
K-MapView is open source and licensed under the Apache License 2.0.
