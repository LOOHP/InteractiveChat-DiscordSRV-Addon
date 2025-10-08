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

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;

import java.util.List;
import java.util.OptionalInt;

public class CharacterData {

    private final Key font;
    private final int color;
    private final OptionalInt shadowColor;
    private final List<TextDecoration> decorations;

    public CharacterData(Key font, int color, OptionalInt shadowColor, List<TextDecoration> decorations) {
        this.font = font;
        this.color = color;
        this.shadowColor = shadowColor;
        this.decorations = decorations;
    }

    public Key getFont() {
        return font;
    }

    public int getColor() {
        return color;
    }

    public OptionalInt getShadowColor() {
        return shadowColor;
    }

    public List<TextDecoration> getDecorations() {
        return decorations;
    }

}