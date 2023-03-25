package com.kpouer.mapview;

import com.kpouer.mapview.marker.PolygonMarker;
import com.kpouer.mapview.tile.DefaultTileServer;
import com.kpouer.mapview.tile.cache.ImageCacheImpl;
import com.kpouer.wkt.ParseException;
import com.kpouer.wkt.WKT;
import com.kpouer.wkt.shape.Polygon;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Sample {
    public static void main(String[] args) throws IOException, ParseException {
        JFrame frame = new JFrame();
        MapView mapView = new MapView(new DefaultTileServer(256,
                                                                1,
                                                                18,
                                                                2,
                                                                new ImageCacheImpl("Waze", "cache", 1000),
                                                                "https://worldtiles1.waze.com/tiles/${z}/${x}/${y}.png",
                                                                "https://worldtiles2.waze.com/tiles/${z}/${x}/${y}.png",
                                                                "https://worldtiles3.waze.com/tiles/${z}/${x}/${y}.png"));
        String wkt2 = "POLYGON((12.493134831869419 41.91785801245087,12.559739446127232 41.88796130217409,12.4316800589202 41.874669382200494,12.469788884603794 41.96101743431704,12.493134831869419 41.91785801245087))";
        Polygon polygon = WKT.parseShape(wkt2);
        PolygonMarker marker = new PolygonMarker(polygon, 5, new Color(1.0f, 0.0f, 0.0f, 0.3f), true);
        mapView.addMarker(marker);
        mapView.setMouseLocationLabelVisible(false);
        mapView.fitToMarkers();
        frame.setContentPane(mapView);

        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
