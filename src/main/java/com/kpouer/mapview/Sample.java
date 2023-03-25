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
        String wkt = "POLYGON((2.315955448997058 48.87015612860361,2.347541142356433 48.88359100758554,2.382045079124011 48.86021877124384,2.3578408249736205 48.839095334613894,2.3482277878642455 48.8582988270549,2.322993565452136 48.85411987042565,2.3138955124736205 48.86179984645585,2.3250535019755736 48.86767197425977,2.315955448997058 48.87015612860361))";
        String wkt2 = "POLYGON((12.493134831869419 41.91785801245087,12.559739446127232 41.88796130217409,12.4316800589202 41.874669382200494,12.469788884603794 41.96101743431704,12.493134831869419 41.91785801245087))";
        Polygon polygon = WKT.parseShape(wkt);
        Polygon polygon2 = WKT.parseShape(wkt2);
//        mapView.addMarker(new Circle(48.87015612860361, 2.315955448997058, 5, Color.RED));
//        mapView.addMarker(new PolygonMarker(polygon, 5, new Color(1.0f, 0.0f, 0.0f, 0.3f), true));
        PolygonMarker marker = new PolygonMarker(polygon2, 5, new Color(1.0f, 0.0f, 0.0f, 0.3f), true);
        mapView.addMarker(marker);
        mapView.setMouseLocationLabelVisible(false);
//        mapView.setCenter(marker.getLatitude(), marker.getLongitude());
        mapView.fitToMarkers();
        frame.setContentPane(mapView);

        frame.setSize(800, 600);
        frame.setVisible(true);
    }
}
