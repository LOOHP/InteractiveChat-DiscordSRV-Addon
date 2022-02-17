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

package com.loohp.interactivechatdiscordsrvaddon.resources.textures;

import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;

public class TextureMeta extends TextureResource {

    private TextureAnimation animation;
    private TextureProperties properties;

    public TextureMeta(TextureManager manager, String resourceKey, ResourcePackFile file, TextureAnimation animation, TextureProperties properties) {
        super(manager, resourceKey, file, false);
        this.animation = animation;
        this.properties = properties;
    }

    public TextureAnimation getAnimation() {
        return animation;
    }

    public TextureProperties getProperties() {
        return properties;
    }

    public boolean hasAnimation() {
        return animation != null;
    }

    public boolean hasProperties() {
        return properties != null;
    }

    @Override
    public boolean isTextureMeta() {
        return true;
    }

    @Override
    public boolean hasTextureMeta() {
        return false;
    }

    @Override
    public TextureMeta getTextureMeta() {
        return null;
    }

}
