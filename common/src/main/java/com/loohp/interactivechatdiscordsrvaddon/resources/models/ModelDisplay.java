/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
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

package com.loohp.interactivechatdiscordsrvaddon.resources.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ModelDisplay {

    private final ModelDisplayPosition position;
    private final Coordinates3D rotation;
    private final Coordinates3D translation;
    private final Coordinates3D scale;

    public ModelDisplay(ModelDisplayPosition position, Coordinates3D rotation, Coordinates3D translation, Coordinates3D scale) {
        this.position = position;
        this.rotation = rotation;
        this.translation = translation;
        this.scale = scale;
    }

    public ModelDisplayPosition getPosition() {
        return position;
    }

    public Coordinates3D getRotation() {
        return rotation;
    }

    public Coordinates3D getTranslation() {
        return translation;
    }

    public Coordinates3D getScale() {
        return scale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModelDisplay that = (ModelDisplay) o;
        return position == that.position && Objects.equals(rotation, that.rotation) && Objects.equals(translation, that.translation) && Objects.equals(scale, that.scale);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, rotation, translation, scale);
    }

    public enum ModelDisplayPosition {

        NONE("none"),
        THIRDPERSON_RIGHTHAND("thirdperson_righthand", "thirdperson"),
        THIRDPERSON_LEFTHAND(THIRDPERSON_RIGHTHAND, "thirdperson_lefthand"),
        FIRSTPERSON_RIGHTHAND("firstperson_righthand", "firstperson"),
        FIRSTPERSON_LEFTHAND(FIRSTPERSON_RIGHTHAND, "firstperson_lefthand"),
        GUI("gui"),
        HEAD("head"),
        GROUND("ground"),
        FIXED("fixed");

        public static ModelDisplayPosition fromKey(String key) {
            for (ModelDisplayPosition position : values()) {
                if (position.getKeys().stream().anyMatch(each -> each.equalsIgnoreCase(key))) {
                    return position;
                }
            }
            return null;
        }

        private ModelDisplayPosition fallback;
        private List<String> keys;

        ModelDisplayPosition(ModelDisplayPosition fallback, String... keys) {
            this.keys = Collections.unmodifiableList(Arrays.asList(keys));
            this.fallback = fallback;
        }

        ModelDisplayPosition(String... keys) {
            this(null, keys);
        }

        public String getMainKey() {
            return keys.get(0);
        }

        public List<String> getKeys() {
            return keys;
        }

        public boolean hasFallback() {
            return fallback != null;
        }

        public ModelDisplayPosition getFallback() {
            return fallback;
        }

    }

}
