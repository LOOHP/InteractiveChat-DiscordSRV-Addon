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

package com.loohp.interactivechatdiscordsrvaddon.resources.fonts;

import java.awt.image.BufferedImage;
import java.util.BitSet;

public class FontBitmapResource extends FontResource {

    private final BitSet pixels;

    public FontBitmapResource(char width, char height, BitSet pixels) {
        super(width, height);
        this.pixels = (BitSet) pixels.clone();
    }

    public FontBitmapResource(int width, int height, BitSet pixels) {
        this((char) width, (char) height, pixels);
    }

    @Override
    public BufferedImage getFontImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < pixels.size(); i++) {
            if (pixels.get(i)) {
                image.setRGB(i % width, i / width, 0xFFFFFFFF);
            }
        }
        return image;
    }

}
