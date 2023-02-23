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

import com.kpouer.mapview.tile.cache.ImageCache;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Matthieu Casanova
 */
public class DefaultTileServer implements TileServer {
    private final int tilesSize;
    private final int minZoom;
    private final int maxZoom;

    private final ImageCache imageCache;
    private final String[]   urlPatterns;

    private final ExecutorService executorService;
    private       int             urlPatternIndex;
    private final Set<Tile>       retrieveQueue;

    public DefaultTileServer(int tilesSize,
                             int minZoom,
                             int maxZoom,
                             int threadCount,
                             ImageCache imageCache,
                             String... urlPatterns) {
        this.tilesSize   = tilesSize;
        this.minZoom     = minZoom;
        this.maxZoom     = maxZoom;
        this.urlPatterns = urlPatterns.clone();
        this.imageCache  = imageCache;
        executorService  = Executors.newFixedThreadPool(threadCount);
        retrieveQueue    = new HashSet<>();
    }

    @Override
    @Nullable
    public Image getTile(int x, int y, int zoom) throws IOException {
        var tile = new Tile(x, y, zoom);

        var image = imageCache.getTile(tile);
        if (image != null) {
            return image;
        }
        boolean added;
        synchronized (retrieveQueue) {
            added = retrieveQueue.add(tile);
        }

        if (added) {
            executorService.submit(() -> {
                try {
                    if (retrieveQueue.contains(tile)) {
                        var tileUrl = getTileUrl(tile);
                        imageCache.setTile(tile, getImage(tileUrl));
                        synchronized (retrieveQueue) {
                            retrieveQueue.remove(tile);
                        }
                    }
                } catch (IOException ignored) {
                }
            });
        }
        return null;
    }

    @Override
    public void cancelPendingZoom(int zoom) {
        synchronized (retrieveQueue) {
            retrieveQueue.removeIf(tile -> tile.getZoom() != zoom);
        }
    }

    private static Image getImage(String urlString) throws IOException {
        var url = new URL(urlString);
        var conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "K-Mapview");
        conn.setRequestMethod("GET");
        try (var inputStream = new BufferedInputStream(conn.getInputStream())) {
            return ImageIO.read(inputStream);
        }
    }

    @Override
    public int getTilesSize() {
        return tilesSize;
    }

    @Override
    public int getMinZoom() {
        return minZoom;
    }

    @Override
    public int getMaxZoom() {
        return maxZoom;
    }

    private String getTileUrl(Tile tile) {
        var nextUrlPattern = urlPatterns[urlPatternIndex++ % urlPatterns.length];
        return nextUrlPattern.replace("${z}", Integer.toString(tile.getZoom()))
                             .replace("${x}", Integer.toString(tile.getX()))
                             .replace("${y}", Integer.toString(tile.getY()));
    }
}
