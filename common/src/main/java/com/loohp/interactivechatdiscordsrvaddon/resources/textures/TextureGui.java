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

package com.loohp.interactivechatdiscordsrvaddon.resources.textures;

import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.graphics.NineSliceImage;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TextureGui {

    private final Scaling<?> scaling;

    public TextureGui(Scaling<?> scaling) {
        this.scaling = scaling;
    }

    public Scaling<?> getScaling() {
        return scaling;
    }

    public static class Scaling<T extends ScalingProperty> {

        public static final Scaling<StretchScalingProperty> DEFAULT_SCALING = new Scaling<>(ScalingType.STRETCH, new StretchScalingProperty());

        private final ScalingType<T> scalingType;
        private final T scalingProperty;

        public Scaling(ScalingType<T> scalingType, T scalingProperty) {
            this.scalingType = scalingType;
            this.scalingProperty = scalingProperty;
        }

        public Scaling(T scalingProperty) {
            this((ScalingType<T>) ScalingType.fromClass(scalingProperty.getClass()), scalingProperty);
        }

        public ScalingType<T> getScalingType() {
            return scalingType;
        }

        public T getScalingProperty() {
            return scalingProperty;
        }

        public T getScalingProperty(ScalingType<T> type) {
            return (T) scalingProperty;
        }

        public T getScalingProperty(Class<T> type) {
            return (T) scalingProperty;
        }

    }

    public static class ScalingType<T extends ScalingProperty> {

        public static final ScalingType<StretchScalingProperty> STRETCH = new ScalingType<>("stretch", StretchScalingProperty.class);
        public static final ScalingType<TileScalingProperty> TILE = new ScalingType<>("tile", TileScalingProperty.class);
        public static final ScalingType<NineSliceScalingProperty> NINE_SLICE = new ScalingType<>("nine_slice", NineSliceScalingProperty.class);

        private static final Map<String, ScalingType<?>> TYPES;

        static {
            Map<String, ScalingType<?>> types = new HashMap<>();
            types.put("stretch", STRETCH);
            types.put("tile", TILE);
            types.put("nine_slice", NINE_SLICE);
            TYPES = Collections.unmodifiableMap(types);
        }

        public static ScalingType<?> fromName(String name) {
            return TYPES.get(name.toLowerCase());
        }

        public static <T extends ScalingProperty> ScalingType<T> fromClass(Class<T> typeClass) {
            return (ScalingType<T>) TYPES.values().stream().filter(e -> e.getTypeClass().isAssignableFrom(typeClass)).findFirst().orElse(null);
        }

        private final String name;
        private final Class<T> typeClass;

        private ScalingType(String name, Class<T> typeClass) {
            this.name = name;
            this.typeClass = typeClass;
        }

        public String getName() {
            return name;
        }

        public Class<T> getTypeClass() {
            return typeClass;
        }
    }

    public static abstract class ScalingProperty {

        public abstract BufferedImage apply(BufferedImage source, int dstWidth, int dstHeight);

    }

    public static class StretchScalingProperty extends ScalingProperty {

        @Override
        public BufferedImage apply(BufferedImage source, int dstWidth, int dstHeight) {
            if (source.getWidth() != dstWidth || source.getHeight() != dstHeight) {
                return ImageUtils.resizeImageAbs(source, dstWidth, dstHeight);
            }
            return ImageUtils.copyImage(source);
        }
    }

    public static class TileScalingProperty extends ScalingProperty {

        private final int width;
        private final int height;

        public TileScalingProperty(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        @Override
        public BufferedImage apply(BufferedImage source, int dstWidth, int dstHeight) {
            if (source.getWidth() != dstWidth || source.getHeight() != dstHeight) {
                BufferedImage image = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = image.createGraphics();
                for (int y = 0; y < dstHeight; y += source.getHeight()) {
                    for (int x = 0; x < dstWidth; x += source.getWidth()) {
                        g.drawImage(source, x, y, null);
                    }
                }
                g.dispose();
                return image;
            }
            return ImageUtils.copyImage(source);
        }
    }

    public static class NineSliceScalingProperty extends ScalingProperty {

        private final int width;
        private final int height;
        private final int borderLeft;
        private final int borderTop;
        private final int borderRight;
        private final int borderBottom;
        private final boolean stretchInner;

        public NineSliceScalingProperty(int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom, boolean stretchInner) {
            this.width = width;
            this.height = height;
            this.borderLeft = borderLeft;
            this.borderTop = borderTop;
            this.borderRight = borderRight;
            this.borderBottom = borderBottom;
            this.stretchInner = stretchInner;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getBorderLeft() {
            return borderLeft;
        }

        public int getBorderTop() {
            return borderTop;
        }

        public int getBorderRight() {
            return borderRight;
        }

        public int getBorderBottom() {
            return borderBottom;
        }

        public boolean isStretchInner() {
            return stretchInner;
        }

        @Override
        public BufferedImage apply(BufferedImage source, int dstWidth, int dstHeight) {
            if (source.getWidth() != dstWidth || source.getHeight() != dstHeight) {
                return NineSliceImage.createExpanded(source, this, dstWidth, dstHeight);
            }
            return ImageUtils.copyImage(source);
        }
    }

}
