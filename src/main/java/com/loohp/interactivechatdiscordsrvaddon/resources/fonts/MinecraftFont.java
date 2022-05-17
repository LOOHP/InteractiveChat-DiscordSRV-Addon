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

import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class MinecraftFont {

    public static final double ITALIC_SHEAR_X = -4.0 / 14.0;
    public static final int OBFUSCATE_OVERLAP_COUNT = 3;

    private static final List<TextDecoration> DECORATIONS_ORDER = new ArrayList<>();

    static {
        DECORATIONS_ORDER.add(TextDecoration.OBFUSCATED);
        DECORATIONS_ORDER.add(TextDecoration.BOLD);
        DECORATIONS_ORDER.add(TextDecoration.ITALIC);
        DECORATIONS_ORDER.add(TextDecoration.STRIKETHROUGH);
        DECORATIONS_ORDER.add(TextDecoration.UNDERLINED);
    }

    public static List<TextDecoration> sortDecorations(List<TextDecoration> decorations) {
        List<TextDecoration> list = new ArrayList<>(DECORATIONS_ORDER.size());
        for (TextDecoration decoration : DECORATIONS_ORDER) {
            if (decorations.contains(decoration)) {
                list.add(decoration);
            }
        }
        return list;
    }

    protected ResourceManager manager;
    protected FontProvider provider;

    public MinecraftFont(ResourceManager manager, FontProvider provider) {
        this.manager = manager;
        this.provider = provider;
    }

    public ResourceManager getManager() {
        return manager;
    }

    public FontProvider getProvider() {
        return provider;
    }

    protected void setProvider(FontProvider provider) {
        this.provider = provider;
    }

    public abstract boolean canDisplayCharacter(String character);

    public abstract FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, int lastItalicExtraWidth, TextColor color, List<TextDecoration> decorations);

    public abstract Optional<BufferedImage> getCharacterImage(String character, float fontSize, TextColor color);

    public abstract int getCharacterWidth(String character);

    public abstract void reloadFonts();

    public abstract IntSet getDisplayableCharacters();

    public static class FontRenderResult {

        private BufferedImage image;
        private int width;
        private int height;
        private int spaceWidth;
        private int italicExtraWidth;

        public FontRenderResult(BufferedImage image, int width, int height, int spaceWidth, int italicExtraWidth) {
            this.image = image;
            this.width = width;
            this.height = height;
            this.spaceWidth = spaceWidth;
            this.italicExtraWidth = italicExtraWidth;
        }

        public BufferedImage getImage() {
            return image;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getSpaceWidth() {
            return spaceWidth;
        }

        public int getItalicExtraWidth() {
            return italicExtraWidth;
        }

    }

}
