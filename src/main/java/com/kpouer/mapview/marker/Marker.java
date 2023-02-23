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

import com.kpouer.mapview.MapView;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * @author Matthieu Casanova
 */
@Getter
@Setter
public abstract class Marker {
    protected double              longitude;
    protected double              latitude;
    protected int                 x;
    protected int                 y;
    private   MouseListener       mouseListener;
    private   MouseMotionListener mouseMotionListener;
    private   boolean             dragable;

    protected Marker(double latitude, double longitude) {
        this.longitude = longitude;
        this.latitude  = latitude;
    }

    public void addMouseListener(MouseListener mouseListener) {
        this.mouseListener = AWTEventMulticaster.add(this.mouseListener, mouseListener);
    }

    public void removeMouseListener(MouseListener mouseListener) {
        AWTEventMulticaster.remove(this.mouseListener, mouseListener);
    }

    public void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
        this.mouseMotionListener = AWTEventMulticaster.add(this.mouseMotionListener, mouseMotionListener);
    }

    public void removeMouseMotionListener(MouseMotionListener mouseMotionListener) {
        AWTEventMulticaster.remove(this.mouseMotionListener, mouseMotionListener);
    }

    public void processMouseEvent(MouseEvent e) {
        e.setSource(this);
        var listener = mouseListener;
        if (listener != null) {
            int id = e.getID();
            switch (id) {
                case MouseEvent.MOUSE_PRESSED:
                    listener.mousePressed(e);
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    listener.mouseReleased(e);
                    break;
                case MouseEvent.MOUSE_CLICKED:
                    listener.mouseClicked(e);
                    break;
                case MouseEvent.MOUSE_EXITED:
                    listener.mouseExited(e);
                    break;
                case MouseEvent.MOUSE_ENTERED:
                    listener.mouseEntered(e);
                    break;
            }
        }
    }

    public void processMouseMotionEvent(MouseEvent e) {
        e.setSource(this);
        var listener = mouseMotionListener;
        if (listener != null) {
            int id = e.getID();
            switch (id) {
                case MouseEvent.MOUSE_DRAGGED:
                    listener.mouseDragged(e);
                    break;
                case MouseEvent.MOUSE_MOVED:
                    listener.mouseMoved(e);
                    break;
            }
        }
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void paint(Graphics g);

    public abstract boolean contains(Point e);

    public void computeLocation(MapView mapView) {
        int x = mapView.longitudeToPointScreen(longitude);
        int y = mapView.latitudeToPointScreen(latitude);
        setLocation(x, y);
    }
}
