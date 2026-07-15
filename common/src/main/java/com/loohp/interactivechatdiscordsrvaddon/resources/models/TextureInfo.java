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

import java.util.Objects;

public class TextureInfo {

    private final String sprite;
    private final boolean forceTranslucent;

    public TextureInfo(String sprite, boolean forceTranslucent) {
        this.sprite = sprite;
        this.forceTranslucent = forceTranslucent;
    }

    public String getSprite() {
        return sprite;
    }

    public boolean isForceTranslucent() {
        return forceTranslucent;
    }

    public TextureInfo withSprite(String sprite) {
        return new TextureInfo(sprite, forceTranslucent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TextureInfo that = (TextureInfo) o;
        return forceTranslucent == that.forceTranslucent && Objects.equals(sprite, that.sprite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sprite, forceTranslucent);
    }

}
