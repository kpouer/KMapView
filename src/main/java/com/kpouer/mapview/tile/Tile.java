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
package com.kpouer.mapview.tile;

/**
 * @author Matthieu Casanova
 */
public class Tile {
    private final int x;
    private final int y;
    private final int zoom;

    public Tile(int x, int y, int zoom) {
        this.x    = x;
        this.y    = y;
        this.zoom = zoom;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZoom() {
        return zoom;
    }

    public String getKey() {
        return zoom + "_" + x + '_' + y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (x != tile.x) return false;
        if (y != tile.y) return false;
        return zoom == tile.zoom;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + zoom;
        return result;
    }

    @Override
    public String toString() {
        return "Tile{x=" + x + ", y=" + y + ", zoom=" + zoom + '}';
    }
}
