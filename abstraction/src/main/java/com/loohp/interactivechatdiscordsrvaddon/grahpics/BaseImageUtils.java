/*
 * This file is part of InteractiveChatDiscordSrvAddon-Abstraction.
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

package com.loohp.interactivechatdiscordsrvaddon.grahpics;

import com.loohp.blockmodelrenderer.utils.ColorUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class BaseImageUtils {

    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public static BufferedImage multiply(BufferedImage image, BufferedImage imageOnTop) {
        return multiply(image, imageOnTop, false);
    }

    public static BufferedImage multiply(BufferedImage image, BufferedImage imageOnTop, boolean ignoreTransparent) {
        for (int y = 0; y < image.getHeight() && y < imageOnTop.getHeight(); y++) {
            for (int x = 0; x < image.getWidth() && x < imageOnTop.getWidth(); x++) {
                int value = image.getRGB(x, y);
                int multiplyValue = imageOnTop.getRGB(x, y);
                if (!ignoreTransparent || ColorUtils.getAlpha(multiplyValue) > 0) {
                    int red = (int) Math.round((double) ColorUtils.getRed(value) / 255 * (double) ColorUtils.getRed(multiplyValue));
                    int green = (int) Math.round((double) ColorUtils.getGreen(value) / 255 * (double) ColorUtils.getGreen(multiplyValue));
                    int blue = (int) Math.round((double) ColorUtils.getBlue(value) / 255 * (double) ColorUtils.getBlue(multiplyValue));
                    int color = ColorUtils.getIntFromColor(red, green, blue, ColorUtils.getAlpha(value));
                    image.setRGB(x, y, color);
                }
            }
        }

        return image;
    }

    public static BufferedImage changeColorTo(BufferedImage image, Color color) {
        return changeColorTo(image, color.getRGB());
    }

    public static BufferedImage changeColorTo(BufferedImage image, int color) {
        color = color & 0x00FFFFFF;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int colorValue = image.getRGB(x, y);
                int newColor = color | (colorValue & 0xFF000000);
                image.setRGB(x, y, newColor);
            }
        }
        return image;
    }

}
