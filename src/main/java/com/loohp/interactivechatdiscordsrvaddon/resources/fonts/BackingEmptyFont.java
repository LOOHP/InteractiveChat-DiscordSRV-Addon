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
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.GeneratedTextureResource;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;

import java.awt.image.BufferedImage;
import java.util.Optional;

public class BackingEmptyFont extends LegacyUnicodeFont {

    private static final Int2ObjectMap<GlyphSize> EMPTY_SIZES = Int2ObjectMaps.singleton(' ', new GlyphSize((byte) 0, (byte) 6));

    public BackingEmptyFont(ResourceManager manager, FontProvider provider) {
        super(manager, provider, EMPTY_SIZES, null);
    }

    @Override
    public void reloadFonts() {
        super.reloadFonts();
        for (Entry<GlyphSize> entry : getSizes().int2ObjectEntrySet()) {
            int character = entry.getIntKey();
            GlyphSize size = entry.getValue();
            if (size.getEnd() - size.getStart() > 0) {
                charImages.put(character, Optional.of(new FontTextureResource(new GeneratedTextureResource(new BufferedImage(size.getEnd() - size.getStart() + 1, 16, BufferedImage.TYPE_INT_ARGB)))));
            }
        }
    }

    @Override
    public boolean canDisplayCharacter(String character) {
        return true;
    }

}
