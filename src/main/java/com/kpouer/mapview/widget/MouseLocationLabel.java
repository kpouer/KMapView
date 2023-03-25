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
package com.kpouer.mapview.widget;

import com.kpouer.mapview.LatLng;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;

/**
 * @author Matthieu Casanova
 */
@Getter
public class MouseLocationLabel extends JLabel {
    private final LatLng latLng;

    public MouseLocationLabel() {
        latLng = new LatLng();
        setForeground(Color.WHITE);
        setBackground(new Color(0, 0, 0, 180));
        setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
    }

    public void setLatLng(LatLng latLng) {
        setText(String.format("%.5f %.5f", latLng.getLat(), latLng.getLon()));
    }

    @Override
    public int getWidth() {
        return getGraphics().getFontMetrics().stringWidth("000.00000 000.00000") + 4;
    }

    @Override
    public int getHeight() {
        return getGraphics().getFontMetrics().getHeight();
    }
}
