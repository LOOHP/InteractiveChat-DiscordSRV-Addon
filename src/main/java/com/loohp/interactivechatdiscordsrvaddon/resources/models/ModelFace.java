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

import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ModelFace {

    private ModelFaceSide side;
    private TextureUV uv;
    private String texture;
    private ModelFaceSide cullface;
    private int rotation;
    private int tintindex;

    public ModelFace(ModelFaceSide side, TextureUV uv, String texture, ModelFaceSide cullface, int rotation, int tintindex) {
        this.side = side;
        this.uv = uv;
        this.texture = texture;
        this.cullface = cullface;
        this.rotation = rotation;
        this.tintindex = tintindex;
    }

    public ModelFaceSide getSide() {
        return side;
    }

    public TextureUV getUV() {
        return uv;
    }

    public String getRawTexture() {
        return texture;
    }

    public String getTexture() {
        if (texture.startsWith("#")) {
            return texture;
        }
        return texture == null ? null : (texture.contains(":") ? texture : ResourceRegistry.DEFAULT_NAMESPACE + ":" + texture);
    }

    public ModelFace cloneWithNewTexture(String texture) {
        return new ModelFace(side, uv, texture, cullface, rotation, tintindex);
    }

    public ModelFaceSide getCullface() {
        return cullface;
    }

    public int getRotation() {
        return rotation;
    }

    public int getTintindex() {
        return tintindex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModelFace modelFace = (ModelFace) o;
        return rotation == modelFace.rotation && tintindex == modelFace.tintindex && side == modelFace.side && Objects.equals(uv, modelFace.uv) && Objects.equals(texture, modelFace.texture) && cullface == modelFace.cullface;
    }

    @Override
    public int hashCode() {
        return Objects.hash(side, uv, texture, cullface, rotation, tintindex);
    }

    public enum ModelFaceSide {

        UP,
        DOWN("BOTTOM"),
        NORTH,
        EAST,
        SOUTH,
        WEST;

        public static ModelFaceSide fromKey(String key) {
            for (ModelFaceSide face : values()) {
                if (key.equalsIgnoreCase(face.toString()) || face.aliases.stream().anyMatch(each -> each.equalsIgnoreCase(key))) {
                    return face;
                }
            }
            return null;
        }

        private Set<String> aliases;

        ModelFaceSide(String... aliases) {
            this.aliases = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(aliases)));
        }

        public Set<String> getAliases() {
            return aliases;
        }

    }

}
