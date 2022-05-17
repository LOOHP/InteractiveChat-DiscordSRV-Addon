/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
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

import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public class SpaceFont extends MinecraftFont {

    public static final String TYPE_KEY = "space";
    public static final int DEFAULT_HEIGHT = 8;

    public static SpaceFont generateLegacyHardcodedInstance(ResourceManager manager, FontProvider provider) {
        return new SpaceFont(manager, provider, Int2IntMaps.singleton(' ', 4));
    }

    private Int2IntMap charAdvances;

    public SpaceFont(ResourceManager manager, FontProvider provider, Int2IntMap charAdvances) {
        super(manager, provider);
        this.charAdvances = charAdvances;
    }

    @Override
    public void reloadFonts() {

    }

    @Override
    public boolean canDisplayCharacter(String character) {
        return charAdvances.containsKey(character.codePointAt(0));
    }

    @Override
    public FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, int lastItalicExtraWidth, TextColor color, List<TextDecoration> decorations) {
        decorations = sortDecorations(decorations);
        int advance = charAdvances.get(character.codePointAt(0)) - 1;
        int sign = advance < 0 ? -1 : 1;
        advance = Math.abs(advance);
        int originalW = advance;
        int w = (int) Math.round(originalW * ((double) Math.round(fontSize) / (double) DEFAULT_HEIGHT));
        int h = Math.round(fontSize);
        int beforeTransformW = w;
        int pixelSize = Math.round((float) beforeTransformW / (float) originalW);
        int strikeSize = (int) (fontSize / 8);
        int boldSize = (int) (fontSize / 16.0 * 2);
        int italicExtraWidth = 0;
        boolean italic = false;
        for (TextDecoration decoration : decorations) {
            switch (decoration) {
                case BOLD:
                    w += boldSize - 1;
                    break;
                case ITALIC:
                    italicExtraWidth = (int) Math.round(-ITALIC_SHEAR_X * h);
                    italic = true;
                    break;
                default:
                    break;
            }
        }
        int extraWidth = italic ? 0 : lastItalicExtraWidth;
        return new FontRenderResult(image, w * sign + extraWidth, h, pixelSize, italicExtraWidth);
    }

    @Override
    public Optional<BufferedImage> getCharacterImage(String character, float fontSize, TextColor color) {
        int advance = charAdvances.get(character.codePointAt(0));
        if (advance <= 1) {
            return Optional.empty();
        }
        BufferedImage charImage = new BufferedImage(advance - 1, DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        charImage = ImageUtils.resizeImageFillHeight(charImage, Math.round(fontSize));
        return Optional.of(charImage);
    }

    @Override
    public int getCharacterWidth(String character) {
        return charAdvances.get(character.codePointAt(0)) - 1;
    }

    @Override
    public IntSet getDisplayableCharacters() {
        return IntSets.unmodifiable(charAdvances.keySet());
    }

}
