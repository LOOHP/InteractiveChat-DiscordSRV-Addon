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

package com.loohp.interactivechatdiscordsrvaddon.resources.fonts;

import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;

public class BackingEmptyFont extends LegacyUnicodeFont {

    private int emptyFontWidth;

    public BackingEmptyFont(ResourceManager manager, FontProvider provider) {
        super(manager, provider, Int2ObjectMaps.emptyMap(), null);
        this.emptyFontWidth = MISSING_CHARACTER.get().getFontImage().getWidth() * 2;
    }

    @Override
    public boolean canDisplayCharacter(String character) {
        return true;
    }

    @Override
    public int getCharacterWidth(String character) {
        return emptyFontWidth;
    }

}
