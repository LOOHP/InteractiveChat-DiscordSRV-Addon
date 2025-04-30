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

package com.loohp.interactivechatdiscordsrvaddon.resources.fonts;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public class ReferenceFont extends MinecraftFont {

    public static final String TYPE_KEY = "reference";

    private final String id;

    public ReferenceFont(ResourceManager manager, FontProvider provider, String id) {
        super(manager, provider);
        this.id = id;
    }

    public FontProvider getReferencedFontProvider() {
        return manager.getFontManager().getFontProviders(id);
    }

    @Override
    public void reloadFonts() {

    }

    @Override
    public boolean canDisplayCharacter(String character) {
        return getReferencedFontProvider().forCharacterOrNull(character, true) != null;
    }

    @Override
    public FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, int lastItalicExtraWidth, int color, List<TextDecoration> decorations) {
        return getReferencedFontProvider().forCharacter(character).printCharacter(image, character, x, y, fontSize, lastItalicExtraWidth, color, decorations);
    }

    @Override
    public Optional<BufferedImage> getCharacterImage(String character, float fontSize, int color) {
        return getReferencedFontProvider().forCharacter(character).getCharacterImage(character, fontSize, color);
    }

    @Override
    public int getCharacterWidth(String character) {
        return getReferencedFontProvider().forCharacter(character).getCharacterWidth(character);
    }

    @Override
    public IntSet getDisplayableCharacters() {
        return getReferencedFontProvider().getDisplayableCharacters();
    }
}
