/*
 * Copyright 2021-2023 Matthieu Casanova
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
package com.kpouer.mapview.tile;

import com.kpouer.mapview.MapPoint;

/**
 * @author Matthieu Casanova
 */
public class TilesTools {
    private static final double MIN_LATITUDE = -85.05112878;
    private static final double MAX_LATITUDE  = 85.05112878;
    private static final double MIN_LONGITUDE = -180;
    private static final double MAX_LONGITUDE = 180;

    private final int tileSize;

    public TilesTools(int tileSize) {
        this.tileSize = tileSize;
    }

    public double position2longitude(int x, int zoom) {
        double maxX = tileSize * (1 << zoom);
        return x / maxX * 360.0 - 180;
    }

    public double position2latitude(int y, int zoom) {
        double maxY = tileSize * (1 << zoom);
        return Math.toDegrees(StrictMath.atan(StrictMath.sinh(Math.PI - (2.0 * Math.PI * y) / maxY)));
    }

    public int longitudeToPoint(double longitude, int zoom) {
        double maxX = tileSize * (1 << zoom);
        return (int) Math.floor((longitude + 180) / 360 * maxX);
    }

    public int latitudeToPoint(double latitude, int zoom) {
        double maxY = tileSize * (1 << zoom);
        return (int) Math.floor((1 - StrictMath.log(StrictMath.tan(Math.toRadians(latitude)) + 1 / StrictMath.cos(Math.toRadians(latitude))) / Math.PI) / 2 * maxY);
    }

    public int clip(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }

    public double clipLatitude(double latitude) {
        return clip(latitude, MIN_LATITUDE, MAX_LATITUDE);
    }

    public double clipLongitude(double longitude) {
        return clip(longitude, MIN_LONGITUDE, MAX_LONGITUDE);
    }

    private double clip(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public int zoom(int pos, int zoom, int newZoom) {
        if (zoom == newZoom) {
            return pos;
        }
        int i = newZoom - zoom;
        if (i > 0) {
            return pos << i;
        }
        return pos >> -i;
    }

    public MapPoint zoom(MapPoint mapPoint, int zoom, int newZoom) {
        return new MapPoint(zoom(mapPoint.getX(), zoom, newZoom), zoom(mapPoint.getY(), zoom, newZoom), newZoom);
    }
}
