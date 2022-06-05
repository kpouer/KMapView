/*
 * Copyright 2021 Matthieu Casanova
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
package com.kpouer.mapview;

import com.kpouer.mapview.marker.Marker;
import com.kpouer.mapview.tile.TileServer;
import com.kpouer.mapview.tile.TilesTools;
import com.kpouer.mapview.widget.MouseLocationLabel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @author Matthieu Casanova
 */
public class MapView extends JPanel {
    private final TileServer         tileServer;
    private final MouseLocationLabel mouseLocationLabel;

    private int xPos;
    private int yPos;
    private int zoom = 13;

    private final TilesTools             tilesTools;
    private final java.util.List<Marker> markers;

    public MapView(TileServer tileServer) {
        super(null);
        this.tileServer    = tileServer;
        tilesTools         = new TilesTools(tileServer.getTilesSize());
        markers            = new ArrayList<>();
        mouseLocationLabel = new MouseLocationLabel();

        setCenter(48.85337, 2.34847, zoom);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                markers.forEach(marker -> marker.computeLocation(MapView.this));
                mouseLocationLabel.setLocation(getWidth() - mouseLocationLabel.getWidth(),
                                               getHeight() - mouseLocationLabel.getHeight());
            }
        });
        MyMouseAdapter mouseAdapter = new MyMouseAdapter();
        addMouseWheelListener(mouseAdapter);
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        add(mouseLocationLabel);
    }

    /**
     * Show/hide the mouse location label.
     *
     * @param visible true to make the component visible; false to make it invisible
     */
    public void setMouseLocationLabelVisible(boolean visible) {
        mouseLocationLabel.setVisible(visible);
    }

    public void addMarker(Marker marker) {
        markers.add(marker);
        marker.computeLocation(this);
    }

    public void removeMarker(Marker marker) {
        markers.remove(marker);
    }

    public void removeAllMarkers() {
        markers.clear();
    }

    public boolean areMarkersVisible(java.util.List<? extends Marker> markers) {
        double minLatitude  = pointScreenToLatitude(getHeight());
        double minLongitude = pointScreenToLongitude(0);
        double maxLatitude  = pointScreenToLatitude(0);
        double maxLongitude = pointScreenToLongitude(getWidth());
        return markers
            .stream()
            .filter(marker -> !isMarkerWithinBounds(marker, minLatitude, minLongitude, maxLatitude, maxLongitude))
            .findFirst().isEmpty();
    }

    public boolean isMarkerVisible(Marker marker) {
        double minLatitude  = pointScreenToLatitude(0);
        double minLongitude = pointScreenToLongitude(0);
        double maxLatitude  = pointScreenToLatitude(getHeight());
        double maxLongitude = pointScreenToLongitude(getWidth());
        return isMarkerWithinBounds(marker, minLatitude, minLongitude, maxLatitude, maxLongitude);
    }

    public boolean isMarkerWithinBounds(Marker marker, double minLatitude, double minLongitude, double maxLatitude, double maxLongitude) {
        return marker.getLatitude() > minLatitude &&
            marker.getLatitude() < maxLatitude &&
            marker.getLongitude() > minLongitude &&
            marker.getLongitude() < maxLongitude;
    }

    public void fitToMarkersIfNecessary() {
        fitToMarkersIfNecessary(markers);
    }

    public void fitToMarkersIfNecessary(java.util.List<? extends Marker> markers) {
        if (!areMarkersVisible(markers)) {
            fitToMarkers(markers);
        }
    }

    public void fitToMarkers() {
        fitToMarkers(markers);
    }

    private void moveMarker(Marker marker, int x, int y) {
        marker.setLongitude(tilesTools.position2longitude(x + getShiftX(), zoom));
        marker.setLatitude(tilesTools.position2latitude(y + getShiftY(), zoom));
        marker.computeLocation(this);
    }

    public void fitToMarkers(java.util.List<? extends Marker> markers) {
        if (markers.isEmpty()) {
            return;
        }
        Marker firstMarker = markers.get(0);
        double minLat      = firstMarker.getLatitude();
        double minLon      = firstMarker.getLongitude();
        double maxLat      = firstMarker.getLatitude();
        double maxLon      = firstMarker.getLongitude();
        for (int i = 1; i < markers.size(); i++) {
            Marker marker = markers.get(i);
            minLat = Math.min(minLat, marker.getLatitude());
            minLon = Math.min(minLon, marker.getLongitude());
            maxLat = Math.max(maxLat, marker.getLatitude());
            maxLon = Math.max(maxLon, marker.getLongitude());
        }
        int width  = getWidth();
        int height = getHeight();
        int zoom;
        for (zoom = tileServer.getMinZoom(); zoom < tileServer.getMaxZoom(); zoom++) {
            int minX  = tilesTools.longitudeToPoint(minLon, zoom);
            int minY  = tilesTools.latitudeToPoint(minLat, zoom);
            int maxX  = tilesTools.longitudeToPoint(maxLon, zoom);
            int maxY  = tilesTools.latitudeToPoint(maxLat, zoom);
            int diffX = Math.abs(maxX - minX);
            int diffY = Math.abs(maxY - minY);
            if (2 * diffX > width || 2 * diffY > height) {
                break;
            }
        }
        setCenter(minLat + (maxLat - minLat) / 2, minLon + (maxLon - minLon) / 2, zoom);
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        xPos      = tilesTools.zoom(xPos, this.zoom, zoom);
        yPos      = tilesTools.zoom(yPos, this.zoom, zoom);
        this.zoom = clipZoom(zoom);
        markers.forEach(marker -> marker.computeLocation(this));
    }

    public void setCenter(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        markers.forEach(marker -> marker.computeLocation(this));
    }

    public void setCenter(int xPos, int yPos, int zoom) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.zoom = zoom;
        markers.forEach(marker -> marker.computeLocation(this));
    }

    public double getLatitude() {
        return tilesTools.position2latitude(yPos, zoom);
    }

    public double getLongitude() {
        return tilesTools.position2longitude(xPos, zoom);
    }

    public LatLng getCenter() {
        return new LatLng(getLatitude(), getLongitude());
    }

    public void setCenter(LatLng latLng) {
        setCenter(latLng.getLat(), latLng.getLon());
    }

    public void setCenter(LatLng latLng, int zoom) {
        setCenter(latLng.getLat(), latLng.getLon(), zoom);
    }

    public void setCenter(double latitude, double longitude) {
        setCenter(tilesTools.longitudeToPoint(longitude, zoom), tilesTools.latitudeToPoint(latitude, zoom));
    }

    public void setCenter(double latitude, double longitude, int zoom) {
        setCenter(tilesTools.longitudeToPoint(longitude, zoom), tilesTools.latitudeToPoint(latitude, zoom), zoom);
    }

    public MapPoint getCenterPoint() {
        return new MapPoint(xPos, yPos, zoom);
    }

    private LatLng mapPointToLatLng(MapPoint mapPoint) {
        return new LatLng(tilesTools.position2latitude(mapPoint.getY(), mapPoint.getZoom()),
                          tilesTools.position2longitude(mapPoint.getX(), mapPoint.getZoom()));
    }

    private MapPoint latLngToMapPoint(LatLng latLng, int newZoom) {
        return new MapPoint(tilesTools.longitudeToPoint(latLng.getLon(), newZoom),
                            tilesTools.latitudeToPoint(latLng.getLat(), newZoom),
                            newZoom);
    }

    public int latitudeToPointScreen(double latitude) {
        return tilesTools.latitudeToPoint(latitude, zoom) - getShiftY();
    }

    public int longitudeToPointScreen(double longitude) {
        return tilesTools.longitudeToPoint(longitude, zoom) - getShiftX();
    }

    private double pointScreenToLatitude(int y) {
        return tilesTools.position2latitude(y + getShiftY(), zoom);
    }

    private double pointScreenToLongitude(int x) {
        return tilesTools.position2longitude(x + getShiftX(), zoom);
    }

    public LatLng pointScreenToLatLng(Point point) {
        return pointScreenToLatLng(point, new LatLng());
    }

    public LatLng pointScreenToLatLng(Point point, LatLng target) {
        target.setLat(pointScreenToLatitude(point.y));
        target.setLon(pointScreenToLongitude(point.x));
        return target;
    }

    public MapPoint pointScreenToMapPoint(Point point) {
        return new MapPoint(point.x + getShiftX(), point.y + getShiftY(), zoom);
    }

    public void translate(int x, int y) {
        setCenter(xPos + x, yPos + y);
    }

    private int getShiftX() {
        return xPos - getWidth() / 2;
    }

    private int getShiftY() {
        return yPos - getHeight() / 2;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int realCenterX = getWidth() / 2;
        int realCenterY = getHeight() / 2;

        int shiftX      = xPos - realCenterX;
        int shiftY      = yPos - realCenterY;
        int tilesSize   = tileServer.getTilesSize();
        int centerTileX = realCenterX - (xPos % tilesSize);
        int centerTileY = realCenterY - (yPos % tilesSize);
        int leftXTiles  = centerTileX / tilesSize + 1;
        int topYTiles   = centerTileY / tilesSize + 1;

        int     startX       = centerTileX - leftXTiles * tilesSize;
        int     startY       = centerTileY - topYTiles * tilesSize;
        boolean hasToRepaint = false;
        for (int x = startX; x < getWidth(); x += tilesSize) {
            for (int y = startY; y < getWidth(); y += tilesSize) {
                try {
                    Image image = tileServer.getTile((x + shiftX) / tilesSize, (y + shiftY) / tilesSize, zoom);
                    if (image != null) {
                        g.drawImage(image, x, y, null);
                    } else {
                        hasToRepaint = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (hasToRepaint) {
            repaint();
        }
        markers.forEach(marker -> marker.paint(g));
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        Point point = e.getPoint();
        markers.stream()
               .filter(marker -> marker.contains(point))
               .forEach(marker -> marker.processMouseEvent(e));
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e) {
        super.processMouseMotionEvent(e);
        Point point = e.getPoint();
        markers.stream()
               .filter(marker -> marker.contains(point))
               .forEach(marker -> marker.processMouseMotionEvent(e));
    }

    private int clipZoom(int zoom) {
        return tilesTools.clip(zoom, tileServer.getMinZoom(), tileServer.getMaxZoom());
    }

    private class MyMouseAdapter extends MouseAdapter {

        @Nullable
        private Point  startDrag;
        private int    pendingRepaint;
        @Nullable
        private Marker draggingMarker;

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseLocationLabel.setLatLng(pointScreenToLatLng(e.getPoint(), mouseLocationLabel.getLatLng()));
            repaint();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Point point = e.getPoint();
            Optional<Marker> first = markers
                .stream()
                .filter(Marker::isDragable)
                .filter(marker -> marker.contains(point))
                .findFirst();
            first.ifPresent(marker -> draggingMarker = marker);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            startDrag      = null;
            draggingMarker = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (startDrag == null) {
                startDrag = e.getPoint();
            } else {
                if (draggingMarker == null) {
                    translate(startDrag.x - e.getX(), startDrag.y - e.getY());
                } else {
                    moveMarker(draggingMarker, e.getX(), e.getY());
                }
                startDrag = e.getPoint();
                repaint();
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int newZoom = clipZoom(zoom - e.getWheelRotation());
            if (zoom == newZoom) {
                return;
            }
            tileServer.cancelPendingZoom(newZoom);
            MapPoint mousePoint    = pointScreenToMapPoint(e.getPoint());
            MapPoint newMousePoint = tilesTools.zoom(mousePoint, zoom, newZoom);

            setCenter(newMousePoint.getX() - e.getX() + getWidth() / 2,
                      newMousePoint.getY() - e.getY() + getHeight() / 2,
                      newZoom);
            repaint();
        }
    }
}
