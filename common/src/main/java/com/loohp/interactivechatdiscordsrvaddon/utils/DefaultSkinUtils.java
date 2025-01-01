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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;

import java.util.UUID;

public class DefaultSkinUtils {

    private static final Skin[] SKINS = new Skin[] {
            new Skin("slim/alex", true),
            new Skin("slim/ari", true),
            new Skin("slim/efe", true),
            new Skin("slim/kai", true),
            new Skin("slim/makena", true),
            new Skin("slim/noor", true),
            new Skin("slim/steve", true),
            new Skin("slim/sunny", true),
            new Skin("slim/zuri", true),
            new Skin("wide/alex", false),
            new Skin("wide/ari", false),
            new Skin("wide/efe", false),
            new Skin("wide/kai", false),
            new Skin("wide/makena", false),
            new Skin("wide/noor", false),
            new Skin("wide/steve", false),
            new Skin("wide/sunny", false),
            new Skin("wide/zuri", false)
    };

    public static String getTexture() {
        return SKINS[6].getTexture();
    }

    public static String getTexture(UUID uuid) {
        return getSkin(uuid).texture;
    }

    public static boolean isModelSlim(UUID uuid) {
        return getSkin(uuid).isSlim();
    }

    private static Skin getSkin(UUID uuid) {
        return SKINS[Math.floorMod(uuid.hashCode(), SKINS.length)];
    }

    private static class Skin {

        private final String texture;
        private final boolean slim;

        private Skin(String texture, boolean slim) {
            this.texture = ResourceRegistry.ENTITY_TEXTURE_LOCATION + "player/" + texture;
            this.slim = slim;
        }

        public String getTexture() {
            return texture;
        }

        public boolean isSlim() {
            return slim;
        }
    }

}
