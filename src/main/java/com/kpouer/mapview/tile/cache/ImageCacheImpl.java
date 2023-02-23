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
package com.kpouer.mapview.tile.cache;

import com.kpouer.mapview.tile.Tile;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Matthieu Casanova
 */
public class ImageCacheImpl implements ImageCache {
    private final Map<Tile, Image> map;
    private       Path             cachePath;

    /**
     * Create an image cache
     * @param tileServerName the name of the tile server
     * @param cachePath the cache path where the downloaded tiles will be stored on disk
     * @param capacity the cahe capacity
     * @throws IOException
     */
    public ImageCacheImpl(String tileServerName, String cachePath, int capacity) throws IOException {
        if (cachePath != null) {
            this.cachePath = Path.of(cachePath, tileServerName);
            Files.createDirectories(this.cachePath);
        }

        map = new TileImageLinkedHashMap(capacity);
    }

    @Nullable
    @Override
    public Image getTile(Tile tile) throws IOException {
        var image = map.get(tile);
        if (image == null) {
            image = getFromDisk(tile);
            if (image != null) {
                map.put(tile, image);
            }
        }
        return image;
    }

    @Nullable
    private Image getFromDisk(Tile tile) throws IOException {
        if (cachePath == null) {
            return null;
        }
        var keyPath = getPath(tile);
        if (Files.exists(keyPath)) {
            return ImageIO.read(keyPath.toFile());
        }
        return null;
    }

    @Override
    public void setTile(Tile tile, Image image) throws IOException {
        map.put(tile, image);
        var path = getPath(tile);
        ImageIO.write((RenderedImage) image, "png", path.toFile());
    }

    private Path getPath(Tile tile) {
        return cachePath.resolve(tile.getKey() + ".png");
    }

    private static class TileImageLinkedHashMap extends LinkedHashMap<Tile, Image> {
        private final int capacity;

        TileImageLinkedHashMap(int capacity) {
            super(capacity + 1);
            this.capacity = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Tile, Image> eldest) {
            return size() > capacity;
        }
    }
}
