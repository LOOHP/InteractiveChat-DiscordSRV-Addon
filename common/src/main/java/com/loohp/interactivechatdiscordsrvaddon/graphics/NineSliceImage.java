/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
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

package com.loohp.interactivechatdiscordsrvaddon.graphics;

import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureGui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class NineSliceImage {

    public static NineSliceImage wrap(BufferedImage image, TextureGui.NineSliceScalingProperty property) {
        return new NineSliceImage(image, property);
    }

    public static BufferedImage createExpanded(BufferedImage image, TextureGui.NineSliceScalingProperty property, int dstWidth, int dstHeight) {
        return wrap(image, property).createExpanded(dstWidth, dstHeight);
    }

    private final BufferedImage image;
    private final TextureGui.NineSliceScalingProperty property;

    private NineSliceImage(BufferedImage image, TextureGui.NineSliceScalingProperty property) {
        this.image = image;
        this.property = property;
    }

    public BufferedImage getPart(NineSlicePart part) {
        float scaleX = (float) image.getWidth() / (float) property.getWidth();
        float scaleY = (float) image.getHeight() / (float) property.getHeight();

        int borderLeft = Math.round(property.getBorderLeft() * scaleX);
        int borderTop = Math.round(property.getBorderTop() * scaleY);
        int borderRight = Math.round(property.getBorderRight() * scaleX);
        int borderBottom = Math.round(property.getBorderBottom() * scaleY);

        NineSlicePartArea area = part.findPosition(image, borderLeft, borderTop, borderRight, borderBottom);

        return ImageUtils.copyAndGetSubImage(image, area.getX(), area.getY(), area.getWidth(), area.getHeight());
    }

    public BufferedImage createExpanded(int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = result.createGraphics();

        // Get all the parts of the image
        BufferedImage topLeft = getPart(NineSlicePart.TOP_LEFT);
        BufferedImage topCenter = getPart(NineSlicePart.TOP_CENTER);
        BufferedImage topRight = getPart(NineSlicePart.TOP_RIGHT);
        BufferedImage centerLeft = getPart(NineSlicePart.CENTER_LEFT);
        BufferedImage centerCenter = getPart(NineSlicePart.CENTER_CENTER);
        BufferedImage centerRight = getPart(NineSlicePart.CENTER_RIGHT);
        BufferedImage bottomLeft = getPart(NineSlicePart.BOTTOM_LEFT);
        BufferedImage bottomCenter = getPart(NineSlicePart.BOTTOM_CENTER);
        BufferedImage bottomRight = getPart(NineSlicePart.BOTTOM_RIGHT);

        // Get dimensions of each part
        int topLeftWidth = topLeft.getWidth();
        int topLeftHeight = topLeft.getHeight();
        int topCenterWidth = topCenter.getWidth();
        int topCenterHeight = topCenter.getHeight();
        int topRightWidth = topRight.getWidth();
        int topRightHeight = topRight.getHeight();

        int centerLeftWidth = centerLeft.getWidth();
        int centerLeftHeight = centerLeft.getHeight();
        int centerCenterWidth = centerCenter.getWidth();
        int centerCenterHeight = centerCenter.getHeight();
        int centerRightWidth = centerRight.getWidth();
        int centerRightHeight = centerRight.getHeight();

        int bottomLeftWidth = bottomLeft.getWidth();
        int bottomLeftHeight = bottomLeft.getHeight();
        int bottomCenterWidth = bottomCenter.getWidth();
        int bottomCenterHeight = bottomCenter.getHeight();
        int bottomRightWidth = bottomRight.getWidth();
        int bottomRightHeight = bottomRight.getHeight();

        // Draw middle (center)
        if (property.isStretchInner()) {
            g.setClip(topLeftWidth, topLeftHeight, width - topRightWidth - topLeftWidth, height - bottomRightHeight - topLeftHeight);
            g.drawImage(centerCenter, topLeftWidth, topLeftHeight, width - topRightWidth - topLeftWidth, height - bottomRightHeight - topLeftHeight, null);
        } else {
            g.setClip(topLeftWidth, topLeftHeight, width - topRightWidth - topLeftWidth, height - bottomRightHeight - topLeftHeight);
            for (int x = topLeftWidth; x < width - topRightWidth; x += centerCenterWidth) {
                for (int y = topLeftHeight; y < height - bottomLeftHeight; y += centerCenterHeight) {
                    g.drawImage(centerCenter, x, y, null);
                }
            }
        }

        // Draw edges
        if (property.isStretchInner()) {
            g.setClip(topLeftWidth, 0, width - topRightWidth - topLeftWidth, topCenterHeight);
            g.drawImage(topCenter, topLeftWidth, 0, width - topRightWidth - topLeftWidth, topCenterHeight, null);

            g.setClip(bottomLeftWidth, height - bottomCenterHeight, width - bottomRightWidth - bottomLeftWidth, bottomCenterHeight);
            g.drawImage(bottomCenter, bottomLeftWidth, height - bottomCenterHeight, width - bottomRightWidth - bottomLeftWidth, bottomCenterHeight, null);

            g.setClip(0, topLeftHeight, centerLeftWidth, height - bottomLeftHeight - topLeftHeight);
            g.drawImage(centerLeft, 0, topLeftHeight, centerLeftWidth, height - bottomLeftHeight - topLeftHeight, null);

            g.setClip(width - centerRightWidth, topRightHeight, centerRightWidth, height - bottomRightHeight - topRightHeight);
            g.drawImage(centerRight, width - centerRightWidth, topRightHeight, centerRightWidth, height - bottomRightHeight - topRightHeight, null);
        } else {
            g.setClip(topLeftWidth, 0, width - topRightWidth - topLeftWidth, topCenterHeight);
            for (int x = topLeftWidth; x < width - topRightWidth; x += topCenterWidth) {
                g.drawImage(topCenter, x, 0, null);
            }

            g.setClip(bottomLeftWidth, height - bottomCenterHeight, width - bottomRightWidth - bottomLeftWidth, bottomCenterHeight);
            for (int x = bottomLeftWidth; x < width - bottomRightWidth; x += bottomCenterWidth) {
                g.drawImage(bottomCenter, x, height - bottomCenterHeight, null);
            }

            g.setClip(0, topLeftHeight, centerLeftWidth, height - bottomLeftHeight - topLeftHeight);
            for (int y = topLeftHeight; y < height - bottomLeftHeight; y += centerLeftHeight) {
                g.drawImage(centerLeft, 0, y, null);
            }

            g.setClip(width - centerRightWidth, topRightHeight, centerRightWidth, height - bottomRightHeight - topRightHeight);
            for (int y = topRightHeight; y < height - bottomRightHeight; y += centerRightHeight) {
                g.drawImage(centerRight, width - centerRightWidth, y, null);
            }
        }

        // Clear clipping
        g.setClip(null);

        // Draw corners
        g.drawImage(topLeft, 0, 0, null);
        g.drawImage(topRight, width - topRightWidth, 0, null);
        g.drawImage(bottomLeft, 0, height - bottomLeftHeight, null);
        g.drawImage(bottomRight, width - bottomRightWidth, height - bottomRightHeight, null);

        g.dispose();
        return result;
    }

    public static class NineSlicePartArea {

        private final int x;
        private final int y;
        private final int width;
        private final int height;

        private NineSlicePartArea(int x1, int y1, int x2, int y2) {
            this.x = x1;
            this.y = y1;
            this.width = x2 - x1;
            this.height = y2 - y1;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    @FunctionalInterface
    public interface NineSlicePartPositionFunction {

        NineSlicePartArea findPosition(BufferedImage source, int borderLeft, int borderTop, int borderRight, int borderBottom);

    }

    public enum NineSlicePart {
        TOP_LEFT((source, borderLeft, borderTop, borderRight, borderBottom) -> new NineSlicePartArea(0, 0, borderLeft, borderTop)),
        TOP_CENTER((source, borderLeft, borderTop, borderRight, borderBottom) -> new NineSlicePartArea(borderLeft, 0, source.getWidth() - borderRight, borderTop)),
        TOP_RIGHT((source, borderLeft, borderTop, borderRight, borderBottom) -> new NineSlicePartArea(source.getWidth() - borderRight, 0, source.getWidth(), borderTop)),
        CENTER_LEFT((source, borderLeft, borderTop, borderRight, borderBottom) -> new NineSlicePartArea(0, borderTop, borderLeft, source.getHeight() - borderBottom)),
        CENTER_CENTER((source, borderLeft, borderTop, borderRight, borderBottom) -> new NineSlicePartArea(borderLeft, borderTop, source.getWidth() - borderRight, source.getHeight() - borderBottom)),
        CENTER_RIGHT((source, borderLeft, borderTop, borderRight, borderBottom) -> new NineSlicePartArea(source.getWidth() - borderRight, borderTop, source.getWidth(), source.getHeight() - borderBottom)),
        BOTTOM_LEFT((source, borderLeft, borderTop, borderRight, borderBottom) -> new NineSlicePartArea(0, source.getHeight() - borderBottom, borderLeft, source.getHeight())),
        BOTTOM_CENTER((source, borderLeft, borderTop, borderRight, borderBottom) -> new NineSlicePartArea(borderLeft, source.getHeight() - borderBottom, source.getWidth() - borderRight, source.getHeight())),
        BOTTOM_RIGHT((source, borderLeft, borderTop, borderRight, borderBottom) -> new NineSlicePartArea(source.getWidth() - borderRight, source.getHeight() - borderBottom, source.getWidth(), source.getHeight()));

        private final NineSlicePartPositionFunction positionFunction;

        NineSlicePart(NineSlicePartPositionFunction positionFunction) {
            this.positionFunction = positionFunction;
        }

        public NineSlicePartArea findPosition(BufferedImage source, int borderLeft, int borderTop, int borderRight, int borderBottom) {
            return positionFunction.findPosition(source, borderLeft, borderTop, borderRight, borderBottom);
        }
    }

}
