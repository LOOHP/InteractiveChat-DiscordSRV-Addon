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

package com.loohp.interactivechatdiscordsrvaddon.graphics;

import com.loohp.blockmodelrenderer.utils.MathUtils;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ComponentFlattening;
import com.loohp.interactivechat.utils.ComponentModernizing;
import com.loohp.interactivechat.utils.HashUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.CharacterData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.CharacterDataArray;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.fonts.MinecraftFont;
import com.loohp.interactivechatdiscordsrvaddon.resources.fonts.MinecraftFont.FontRenderResult;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.IntToIntFunction;
import com.loohp.interactivechatdiscordsrvaddon.utils.UnicodeUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.loohp.blockmodelrenderer.utils.ColorUtils.getAlpha;
import static com.loohp.blockmodelrenderer.utils.ColorUtils.getBlue;
import static com.loohp.blockmodelrenderer.utils.ColorUtils.getGreen;
import static com.loohp.blockmodelrenderer.utils.ColorUtils.getIntFromColor;
import static com.loohp.blockmodelrenderer.utils.ColorUtils.getRed;

public class ImageUtils {

    public static final Color TEXT_BACKGROUND_COLOR = new Color(0, 0, 0, 180);
    public static final double CHAT_COLOR_BACKGROUND_FACTOR = 0.19;

    public static String hash(BufferedImage image) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int[] colors = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        for (int color : colors) {
            out.write((byte) (color >>> 24));
            out.write((byte) (color >>> 16));
            out.write((byte) (color >>> 8));
            out.write((byte) color);
        }
        try {
            return HashUtils.createSha1String(new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage toCompatibleImage(BufferedImage image) {
        try {
            GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

            if (image.getColorModel().equals(gfxConfig.getColorModel())) {
                return image;
            }

            BufferedImage newImage = gfxConfig.createCompatibleImage(image.getWidth(), image.getHeight(), image.getTransparency());

            Graphics2D g2d = newImage.createGraphics();

            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            return newImage;
        } catch (Exception e) {
            return image;
        }
    }

    public static int getRGB(BufferedImage image, int x, int y) {
        if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
            return 0;
        }
        return image.getRGB(x, y);
    }

    public static int getRGB(int[] colors, int x, int y, int w, int h) {
        if (x < 0 || y < 0 || x >= w || y >= h) {
            return 0;
        }
        return colors[y * w + x];
    }

    public static BufferedImage downloadImage(String link) throws IOException {
        URL url = new URL(link);
        URLConnection connection = url.openConnection();
        connection.setUseCaches(false);
        connection.setDefaultUseCaches(false);
        connection.addRequestProperty("User-Agent", "Mozilla/5.0");
        connection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
        connection.addRequestProperty("Pragma", "no-cache");
        InputStream in = connection.getInputStream();
        BufferedImage image = ImageIO.read(in);
        in.close();
        return image;
    }

    public static BufferedImage transformRGB(BufferedImage image, IntToIntFunction function) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int colorValue = image.getRGB(x, y);
                int newValue = function.apply(colorValue);
                image.setRGB(x, y, newValue);
            }
        }
        return image;
    }

    public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
        if (MathUtils.equals(angle % 360, 0)) {
            return img;
        }
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads));
        double cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2.0, (newHeight - h) / 2.0);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g.setTransform(at);
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return rotated;
    }

    public static BufferedImage flipHorizontal(BufferedImage image) {
        BufferedImage b = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.drawImage(image, image.getWidth(), 0, -image.getWidth(), image.getHeight(), null);
        g.dispose();
        return b;
    }

    public static BufferedImage flipVertically(BufferedImage image) {
        BufferedImage b = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.drawImage(image, 0, image.getHeight(), image.getWidth(), -image.getHeight(), null);
        g.dispose();
        return b;
    }

    public static BufferedImage expandCenterAligned(BufferedImage image, int pixels) {
        BufferedImage b = new BufferedImage(image.getWidth() + pixels + pixels, image.getHeight() + pixels + pixels, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.drawImage(image, pixels, pixels, null);
        g.dispose();
        return b;
    }

    public static BufferedImage expandCenterAligned(BufferedImage image, int up, int down, int left, int right) {
        BufferedImage b = new BufferedImage(image.getWidth() + left + right, image.getHeight() + up + down, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.drawImage(image, left, up, null);
        g.dispose();
        return b;
    }

    public static BufferedImage additionNonTransparent(BufferedImage image, BufferedImage imageToAdd) {
        return additionNonTransparent(image, imageToAdd, 1);
    }

    public static BufferedImage additionNonTransparent(BufferedImage image, BufferedImage imageToAdd, double factor) {
        if (factor < 0 || factor > 1) {
            throw new IllegalArgumentException("factor cannot be smaller than 0 or greater than 1");
        }
        for (int y = 0; y < image.getHeight() && y < imageToAdd.getHeight(); y++) {
            for (int x = 0; x < image.getWidth() && x < imageToAdd.getWidth(); x++) {
                int value = image.getRGB(x, y);
                int addValue = imageToAdd.getRGB(x, y);
                int alpha = getAlpha(value);
                if (alpha != 0) {
                    int red = getRed(value) + (int) (getRed(addValue) * factor);
                    int green = getGreen(value) + (int) (getGreen(addValue) * factor);
                    int blue = getBlue(value) + (int) (getBlue(addValue) * factor);
                    int color = getIntFromColor(Math.min(red, 255), Math.min(green, 255), Math.min(blue, 255), alpha);
                    image.setRGB(x, y, color);
                }
            }
        }
        return image;
    }

    public static BufferedImage drawTransparent(BufferedImage image, BufferedImage imageToAdd, int posX, int posY) {
        for (int y = 0; y + posY < image.getHeight() && y < imageToAdd.getHeight(); y++) {
            for (int x = 0; x + posX < image.getWidth() && x < imageToAdd.getWidth(); x++) {
                if (x + posX >= 0 && y + posY >= 0) {
                    int value = image.getRGB(x + posX, y + posY);
                    int addValue = imageToAdd.getRGB(x, y);
                    if (getAlpha(value) == 0) {
                        image.setRGB(x + posX, y + posY, addValue);
                    }
                }
            }
        }
        return image;
    }

    public static BufferedImage darken(BufferedImage image, int value) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int colorValue = image.getRGB(x, y);
                int alpha = getAlpha(colorValue);
                if (alpha != 0) {
                    int red = getRed(colorValue) - value;
                    int green = getGreen(colorValue) - value;
                    int blue = getBlue(colorValue) - value;
                    int color = getIntFromColor(Math.max(red, 0), Math.max(green, 0), Math.max(blue, 0), alpha);
                    image.setRGB(x, y, color);
                }
            }
        }
        return image;
    }

    public static BufferedImage add(BufferedImage image, int value) {
        return add(image, value, value, value);
    }

    public static BufferedImage add(BufferedImage image, int xValue, int yValue, int zValue) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int colorValue = image.getRGB(x, y);
                int alpha = getAlpha(colorValue);
                if (alpha != 0) {
                    int red = getRed(colorValue) + xValue;
                    int green = getGreen(colorValue) + yValue;
                    int blue = getBlue(colorValue) + zValue;
                    int color = getIntFromColor(red < 0 ? 0 : (Math.min(red, 255)), green < 0 ? 0 : (Math.min(green, 255)), blue < 0 ? 0 : (Math.min(blue, 255)), alpha);
                    image.setRGB(x, y, color);
                }
            }
        }
        return image;
    }

    public static BufferedImage multiply(BufferedImage image, double value) {
        return multiply(image, value, value, value);
    }

    public static BufferedImage multiply(BufferedImage image, double xValue, double yValue, double zValue) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int colorValue = image.getRGB(x, y);
                int alpha = getAlpha(colorValue);
                if (alpha != 0) {
                    int red = (int) (getRed(colorValue) * xValue);
                    int green = (int) (getGreen(colorValue) * yValue);
                    int blue = (int) (getBlue(colorValue) * zValue);
                    int color = getIntFromColor(red < 0 ? 0 : (Math.min(red, 255)), green < 0 ? 0 : (Math.min(green, 255)), blue < 0 ? 0 : (Math.min(blue, 255)), alpha);
                    image.setRGB(x, y, color);
                }
            }
        }
        return image;
    }

    public static BufferedImage multiply(BufferedImage image, BufferedImage imageOnTop) {
        for (int y = 0; y < image.getHeight() && y < imageOnTop.getHeight(); y++) {
            for (int x = 0; x < image.getWidth() && x < imageOnTop.getWidth(); x++) {
                int value = image.getRGB(x, y);
                int multiplyValue = imageOnTop.getRGB(x, y);

                int red = (int) Math.round((double) getRed(value) / 255 * (double) getRed(multiplyValue));
                int green = (int) Math.round((double) getGreen(value) / 255 * (double) getGreen(multiplyValue));
                int blue = (int) Math.round((double) getBlue(value) / 255 * (double) getBlue(multiplyValue));
                int color = getIntFromColor(red, green, blue, getAlpha(value));
                image.setRGB(x, y, color);
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

    public static BufferedImage raiseAlpha(BufferedImage image, int value) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int colorValue = image.getRGB(x, y);
                int alpha = getAlpha(colorValue) + value;
                int color = getIntFromColor(getRed(colorValue), getGreen(colorValue), getBlue(colorValue), Math.min(Math.max(alpha, 0), 255));
                image.setRGB(x, y, color);
            }
        }
        return image;
    }

    public static BufferedImage squarify(BufferedImage image) {
        if (image.getHeight() == image.getWidth()) {
            return image;
        }
        int size = Math.max(image.getHeight(), image.getWidth());
        int offsetX = (size - image.getWidth()) / 2;
        int offsetY = (size - image.getHeight()) / 2;

        BufferedImage newImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int colorValue = image.getRGB(x, y);
                newImage.setRGB(x + offsetX, y + offsetY, colorValue);
            }
        }
        return newImage;
    }

    public static BufferedImage xor(BufferedImage bottom, BufferedImage top, int alpha) {
        for (int y = 0; y < bottom.getHeight(); y++) {
            for (int x = 0; x < bottom.getWidth(); x++) {
                int bottomValue = bottom.getRGB(x, y);
                int topValue = top.getRGB(x, y);
                int color = getIntFromColor(getRed(bottomValue) ^ getRed(topValue), getGreen(bottomValue) ^ getGreen(topValue), getBlue(bottomValue) ^ getBlue(topValue), getAlpha(bottomValue) ^ (getAlpha(topValue) * alpha / 255));
                bottom.setRGB(x, y, color);
            }
        }
        return bottom;
    }

    public static BufferedImage appendImageRight(BufferedImage source, BufferedImage append, int middleGap, int rightSpace) {
        BufferedImage b = new BufferedImage(source.getWidth() + append.getWidth() + middleGap + rightSpace, Math.max(source.getHeight(), append.getHeight()), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.drawImage(append, source.getWidth() + middleGap, 0, null);
        g.dispose();
        return b;
    }

    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public static BufferedImage copyAndGetSubImage(BufferedImage source, int x, int y, int w, int h) {
        BufferedImage copyOfImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copyOfImage.createGraphics();
        g.drawImage(source, -x, -y, null);
        g.dispose();
        return copyOfImage;
    }

    public static BufferedImage resizeImage(BufferedImage source, double factor) {
        int w = (int) Math.round(source.getWidth() * factor);
        int h = (int) Math.round(source.getHeight() * factor);
        return resizeImageAbs(source, w, h);
    }

    public static BufferedImage resizeImageQuality(BufferedImage source, int width, int height) {
        BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();
        return b;
    }

    public static BufferedImage resizeImageAbs(BufferedImage source, int width, int height) {
        BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = b.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();
        return b;
    }

    public static BufferedImage resizeImageFillWidth(BufferedImage source, int width) {
        int height = (int) Math.round(source.getHeight() * ((double) width / (double) source.getWidth()));
        return resizeImageAbs(source, width, height);
    }

    public static BufferedImage resizeImageFillHeight(BufferedImage source, int height) {
        int width = (int) Math.round(source.getWidth() * ((double) height / (double) source.getHeight()));
        return resizeImageAbs(source, width, height);
    }

    public static BufferedImage resizeImageStretch(BufferedImage source, int pixels) {
        int w = source.getWidth() + pixels;
        int h = source.getHeight() + pixels;
        return resizeImageAbs(source, w, h);
    }

    public static BufferedImage printComponentNoShadow(ResourceManager manager, BufferedImage image, Component component, String language, boolean legacyRGB, int centerX, int topY, float fontSize, boolean dynamicFontSize) {
        Component text = ComponentFlattening.flatten(ComponentStringUtils.convertTranslatables(ComponentModernizing.modernize(component), manager.getLanguageManager().getTranslateFunction().ofLanguage(language)));
        String striped = ChatColorUtils.stripColor(ChatColorUtils.filterIllegalColorCodes(PlainTextComponentSerializer.plainText().serialize(text)));

        if (dynamicFontSize) {
            fontSize = Math.round(Math.max(2, fontSize - (float) striped.length() / 3) * 10) / 8.0F;
        }

        BufferedImage textImage = new BufferedImage(image.getWidth() + centerX, image.getHeight() * 2, BufferedImage.TYPE_INT_ARGB);
        CharacterDataArray characterDataArray = CharacterDataArray.fromComponent(text, legacyRGB);
        char[] chars = characterDataArray.getChars();
        CharacterData[] data = characterDataArray.getData();
        if (UnicodeUtils.icu4JAvailable()) {
            String shaped = UnicodeUtils.shaping(new String(chars));
            if (shaped.length() == chars.length) {
                chars = shaped.toCharArray();
            }
            byte[] levels = UnicodeUtils.getBidirectionalLevels(chars);
            UnicodeUtils.bidirectionalReorderVisually(levels, data);
            UnicodeUtils.bidirectionalReorderVisually(levels, chars);
        }

        int x = centerX;
        int lastItalicExtraWidth = 0;
        int height = 0;
        String character = null;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (character == null) {
                character = String.valueOf(c);
                if (Character.isHighSurrogate(c)) {
                    continue;
                } else if (Character.isLowSurrogate(c) && i + 1 < chars.length) {
                    character = String.valueOf(chars[++i]) + character;
                }
            } else {
                character += String.valueOf(c);
            }
            CharacterData characterData = data[i];
            MinecraftFont fontProvider = manager.getFontManager().getFontProviders(characterData.getFont().asString()).forCharacter(character);
            FontRenderResult result = fontProvider.printCharacter(textImage, character, x, 1 + image.getHeight(), fontSize, lastItalicExtraWidth, characterData.getColor(), characterData.getDecorations());
            textImage = result.getImage();
            x += result.getWidth() + result.getSpaceWidth();
            lastItalicExtraWidth = result.getItalicExtraWidth();
            if (height < result.getHeight()) {
                height = result.getHeight();
            }
            character = null;
        }

        int width = x - centerX;
        x = centerX - width / 2;
        int border = (int) Math.ceil(height / 6.0);
        int y = topY + border;

        BufferedImage background = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = background.createGraphics();
        g2.setColor(TEXT_BACKGROUND_COLOR);
        g2.fillRect(x - border, y - border, width + border * 2, height + border);
        g2.setColor(Color.white);
        g2.dispose();

        Graphics2D g3 = image.createGraphics();
        g3.drawImage(background, 0, 0, null);
        g3.drawImage(textImage, x - centerX, (int) (y - (height / 5) + Math.max(1, 1 * (fontSize / 8))) - image.getHeight(), null);
        g3.dispose();
        return image;
    }

    public static BufferedImage printComponentRightAligned(ResourceManager manager, BufferedImage image, Component component, String language, boolean legacyRGB, int topX, int topY, float fontSize) {
        return printComponentRightAligned(manager, image, component, language, legacyRGB, topX, topY, fontSize, CHAT_COLOR_BACKGROUND_FACTOR);
    }

    public static BufferedImage printComponentRightAligned(ResourceManager manager, BufferedImage image, Component component, String language, boolean legacyRGB, int topX, int topY, float fontSize, double shadowFactor) {
        BufferedImage textImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        textImage = printComponent(manager, textImage, component, language, legacyRGB, 0, 0, fontSize, shadowFactor);
        int lastX = 0;
        for (int x = 0; x < textImage.getWidth() - 9; x++) {
            for (int y = 0; y < textImage.getHeight(); y++) {
                if (textImage.getRGB(x, y) != 0) {
                    lastX = x;
                    break;
                }
            }
        }
        Graphics2D g = image.createGraphics();
        g.drawImage(textImage, topX - lastX, topY, null);
        g.dispose();
        return image;
    }

    public static BufferedImage printComponentGlowing(ResourceManager manager, BufferedImage image, Component component, String language, boolean legacyRGB, int topX, int topY, float fontSize) {
        BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        temp = printComponent0(manager, temp, component, language, legacyRGB, topX, topY, fontSize, 1);
        Graphics2D g = image.createGraphics();
        BufferedImage shadow = transformRGB(copyImage(temp), color -> {
            int alpha = getAlpha(color);
            if (alpha <= 0) {
                return color;
            }
            if ((color & 0x00ffffff) == 0) {
                return -988212;
            }
            int red = (int) (getRed(color) * 0.4);
            int green = (int) (getGreen(color) * 0.4);
            int blue = (int) (getBlue(color) * 0.4);
            return getIntFromColor(red, green, blue, alpha);
        });
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x != 0 || y != 0) {
                    g.drawImage(shadow, (int) (fontSize * 0.15) * x, (int) (fontSize * 0.15) * y, null);
                }
            }
        }
        g.drawImage(temp, 0, 0, null);
        g.dispose();
        return image;
    }

    public static BufferedImage printComponent(ResourceManager manager, BufferedImage image, Component component, String language, boolean legacyRGB, int topX, int topY, float fontSize) {
        return printComponent(manager, image, component, language, legacyRGB, topX, topY, fontSize, CHAT_COLOR_BACKGROUND_FACTOR);
    }

    public static BufferedImage printComponent(ResourceManager manager, BufferedImage image, Component component, String language, boolean legacyRGB, int topX, int topY, float fontSize, double shadowFactor) {
        BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        temp = printComponent0(manager, temp, component, language, legacyRGB, topX, topY, fontSize, 1);
        BufferedImage shadow = multiply(copyImage(temp), shadowFactor);
        Graphics2D g = image.createGraphics();
        g.drawImage(shadow, (int) (fontSize * 0.15), (int) (fontSize * 0.15), null);
        g.drawImage(temp, 0, 0, null);
        g.dispose();
        return image;
    }

    private static BufferedImage printComponent0(ResourceManager manager, BufferedImage image, Component component, String language, boolean legacyRGB, int topX, int topY, float fontSize, double factor) {
        Component text = ComponentFlattening.flatten(ComponentStringUtils.convertTranslatables(ComponentModernizing.modernize(component), manager.getLanguageManager().getTranslateFunction().ofLanguage(language)));
        BufferedImage textImage = new BufferedImage(image.getWidth(), image.getHeight() * 2, BufferedImage.TYPE_INT_ARGB);
        CharacterDataArray characterDataArray = CharacterDataArray.fromComponent(text, legacyRGB);
        char[] chars = characterDataArray.getChars();
        CharacterData[] data = characterDataArray.getData();
        if (UnicodeUtils.icu4JAvailable()) {
            String shaped = UnicodeUtils.shaping(new String(chars));
            if (shaped.length() == chars.length) {
                chars = shaped.toCharArray();
            }
            byte[] levels = UnicodeUtils.getBidirectionalLevels(chars);
            UnicodeUtils.bidirectionalReorderVisually(levels, data);
            UnicodeUtils.bidirectionalReorderVisually(levels, chars);
        }

        int x = topX;
        int lastItalicExtraWidth = 0;
        String character = null;
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (character == null) {
                character = String.valueOf(c);
                if (Character.isHighSurrogate(c)) {
                    continue;
                } else if (Character.isLowSurrogate(c) && i + 1 < chars.length) {
                    character = String.valueOf(chars[++i]) + character;
                }
            } else {
                character += String.valueOf(c);
            }
            CharacterData characterData = data[i];
            MinecraftFont fontProvider = manager.getFontManager().getFontProviders(characterData.getFont().asString()).forCharacter(character);
            FontRenderResult result = fontProvider.printCharacter(textImage, character, x, 1 + image.getHeight(), fontSize, lastItalicExtraWidth, characterData.getColor(), characterData.getDecorations());
            textImage = result.getImage();
            x += result.getWidth() + result.getSpaceWidth();
            lastItalicExtraWidth = result.getItalicExtraWidth();
            character = null;
        }
        Graphics2D g = image.createGraphics();
        g.drawImage(textImage, 0, topY - image.getHeight(), null);
        g.dispose();
        return image;
    }

}
