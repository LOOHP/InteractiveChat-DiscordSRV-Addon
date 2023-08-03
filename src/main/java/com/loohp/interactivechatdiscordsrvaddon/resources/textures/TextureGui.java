/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2023. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2023. Contributors
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

    }

    public static class StretchScalingProperty extends ScalingProperty {

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
    }

    public static class NineSliceScalingProperty extends ScalingProperty {

        private final int width;
        private final int height;
        private final int borderLeft;
        private final int borderTop;
        private final int borderRight;
        private final int borderBottom;

        public NineSliceScalingProperty(int width, int height, int borderLeft, int borderTop, int borderRight, int borderBottom) {
            this.width = width;
            this.height = height;
            this.borderLeft = borderLeft;
            this.borderTop = borderTop;
            this.borderRight = borderRight;
            this.borderBottom = borderBottom;
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
    }

}
