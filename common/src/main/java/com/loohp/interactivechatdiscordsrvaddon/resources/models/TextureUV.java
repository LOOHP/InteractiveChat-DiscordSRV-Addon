/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.interactivechatdiscordsrvaddon.resources.models;

public class TextureUV {

    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;

    public TextureUV(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public double getX1() {
        return x1;
    }

    public double getY1() {
        return y1;
    }

    public double getX2() {
        return x2;
    }

    public double getY2() {
        return y2;
    }

    public TextureUV getScaled(double scale) {
        return new TextureUV(x1 * scale, y1 * scale, x2 * scale, y2 * scale);
    }

    public TextureUV getScaled(double scaleX, double scaleY) {
        return new TextureUV(x1 * scaleX, y1 * scaleY, x2 * scaleX, y2 * scaleY);
    }

    public double getXDiff() {
        return x2 - x1;
    }

    public double getYDiff() {
        return y2 - y1;
    }

    public boolean isVerticallyFlipped() {
        return getYDiff() < 0;
    }

    public boolean isHorizontallyFlipped() {
        return getXDiff() < 0;
    }

    @Override
    public String toString() {
        return "TextureUV{" +
            "x1=" + x1 +
            ", y1=" + y1 +
            ", x2=" + x2 +
            ", y2=" + y2 +
            '}';
    }

}
