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
package com.kpouer.mapview.marker;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * @author Matthieu Casanova
 */
@Getter
@Setter
public class Circle extends Marker {
    private int   radius;

    public Circle(double latitude, double longitude, int radius, Color color) {
        super(latitude, longitude, color);
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }


    @Override
    public void paint(Graphics g) {
        g.setColor(color);
        g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
    }

    @Override
    public boolean contains(Point point) {
        return point.getX() >= x - radius &&
            point.getX() < x + radius &&
            point.getY() >= y - radius &&
            point.getY() < y + radius;
    }

    @Override
    public String toString() {
        return "Circle{" +
            "longitude=" + longitude +
            ", latitude=" + latitude +
            ", radius=" + radius +
            ", x=" + x +
            ", y=" + y +
            '}';
    }
}
