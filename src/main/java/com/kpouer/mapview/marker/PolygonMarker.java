/*
 * Copyright 2023 Matthieu Casanova
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kpouer.mapview.marker;

import com.kpouer.mapview.MapView;
import com.kpouer.wkt.shape.Polygon;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

/**
 * @since 1.2.0
 * @author Matthieu Casanova
 */
public class PolygonMarker extends Marker {
    private final Polygon polygon;
    private final int[] xpoints;
    private final int[] ypoints;
    private final int   width;
    @Setter
    @Getter
    private Color borderColor;
    @Setter
    @Getter
    private boolean filled;

    /**
     * Create a new PolygonMarker
     * @param polygon a polygon with latitude &amp; longitude coordinates
     * @param width the width of the line
     * @param color the color of the polygon
     * @param filled true if the polygon is filled
     */
    public PolygonMarker(Polygon polygon, int width, Color color, boolean filled) {
        super(polygon.getBarycenter().getX(), polygon.getBarycenter().getY(), color);
        this.polygon = polygon;
        xpoints = new int[polygon.getNpoints()];
        ypoints = new int[polygon.getNpoints()];
        this.width = width;
        borderColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
        this.filled = filled;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(color);

        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.setStroke(new BasicStroke(width));
        graphics2D.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        if (filled) {
            g.fillPolygon(xpoints, ypoints, xpoints.length);
            g.setColor(borderColor);
            g.drawPolygon(xpoints, ypoints, xpoints.length);
        } else {
            g.drawPolygon(xpoints, ypoints, xpoints.length);
        }
    }

    @Override
    public boolean contains(Point e) {
        var tmpPolygon = new java.awt.Polygon(xpoints, ypoints, xpoints.length);
        return tmpPolygon.contains(e.x, e.y);
    }

    @Override
    public void computeLocation(MapView mapView) {
        super.computeLocation(mapView);
        int x = 0;
        int y = 0;
        for (int i = 0;i<xpoints.length;i++) {
            xpoints[i] = mapView.longitudeToPointScreen(polygon.getXpoints()[i]);
            ypoints[i] = mapView.latitudeToPointScreen(polygon.getYpoints()[i]);
            x += xpoints[i];
            y += ypoints[i];
        }
        setLocation(x / xpoints.length, y / ypoints.length);
    }
}
