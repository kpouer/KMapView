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
package com.kpouer.mapview.marker;

import java.awt.*;

/**
 * @author Matthieu Casanova
 */
public class Rectangle extends Marker {

    private int   width;
    private int   height;
    private Color color;

    public Rectangle(double latitude, double longitude, int width, int height, Color color) {
        super(latitude, longitude);
        this.width  = width;
        this.height = height;
        this.color  = color;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(color);
        g.fillRect(x - width / 2, y - height / 2, width, height);
    }

    @Override
    public boolean contains(Point point) {
        return point.getX() >= x - width / 2 &&
            point.getX() < x + width / 2 &&
            point.getY() >= y - height / 2 &&
            point.getY() < y + height / 2;
    }

    @Override
    public String toString() {
        return "Rectangle{" +
            "longitude=" + longitude +
            ", latitude=" + latitude +
            ", x=" + x +
            ", y=" + y +
            ", width=" + width +
            ", height=" + height +
            ", color=" + color +
            '}';
    }
}
