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

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ToolTipComponent<T> {

    public static ToolTipComponent<Component> text(Component component) {
        return new ToolTipComponent<>(component, ToolTipType.TEXT);
    }

    public static ToolTipComponent<BufferedImage> image(BufferedImage image) {
        return new ToolTipComponent<>(image, ToolTipType.IMAGE);
    }

    private final T toolTipComponent;
    private final ToolTipType<T> type;

    private ToolTipComponent(T toolTipComponent, ToolTipType<T> type) {
        this.toolTipComponent = toolTipComponent;
        this.type = type;
    }

    public T getToolTipComponent() {
        return toolTipComponent;
    }

    public ToolTipType<T> getType() {
        return type;
    }

    public static final class ToolTipType<V> {

        public static final ToolTipType<Component> TEXT = new ToolTipType<>("TEXT", Component.class);
        public static final ToolTipType<BufferedImage> IMAGE = new ToolTipType<>("IMAGE", BufferedImage.class);

        private static final Map<String, ToolTipType<?>> TYPES;

        static {
            Map<String, ToolTipType<?>> types = new HashMap<>();
            types.put(TEXT.name(), TEXT);
            types.put(IMAGE.name(), IMAGE);
            TYPES = Collections.unmodifiableMap(types);
        }

        public static Map<String, ToolTipType<?>> values() {
            return TYPES;
        }

        private final String name;
        private final Class<V> typeClass;

        private ToolTipType(String name, Class<V> typeClass) {
            this.name = name;
            this.typeClass = typeClass;
        }

        public Class<V> getTypeClass() {
            return typeClass;
        }

        public String name() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

    }

}
