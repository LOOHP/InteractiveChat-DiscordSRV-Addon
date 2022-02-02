package com.loohp.interactivechatdiscordsrvaddon.graphics;

import com.loohp.blockmodelrenderer.utils.MathUtils;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ComponentFlattening;
import com.loohp.interactivechat.utils.ComponentModernizing;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.CharacterData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.CharacterDataArray;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.fonts.MinecraftFont;
import com.loohp.interactivechatdiscordsrvaddon.resources.fonts.MinecraftFont.FontRenderResult;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.UnicodeUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageUtils {

    public static final Color TEXT_BACKGROUND_COLOR = new Color(0, 0, 0, 180);
    public static final double CHAT_COLOR_BACKGROUND_FACTOR = 0.19;

    public static String hash(BufferedImage image) {
        StringBuilder sb = new StringBuilder();
        int[] colors = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        for (int i = 0; i < colors.length; i++) {
            sb.append(Integer.toHexString(colors[i]));
        }
        return sb.toString();
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
                Color color = new Color(value, true);

                int addValue = imageToAdd.getRGB(x, y);
                Color addColor = new Color(addValue, true);
                if (color.getAlpha() != 0) {
                    int red = color.getRed() + (int) (addColor.getRed() * factor);
                    int green = color.getGreen() + (int) (addColor.getGreen() * factor);
                    int blue = color.getBlue() + (int) (addColor.getBlue() * factor);
                    color = new Color(Math.min(red, 255), Math.min(green, 255), Math.min(blue, 255), color.getAlpha());
                    image.setRGB(x, y, color.getRGB());
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
                    Color color = new Color(value, true);

                    int addValue = imageToAdd.getRGB(x, y);
                    Color addColor = new Color(addValue, true);
                    if (color.getAlpha() == 0) {
                        color = new Color(addColor.getRed(), addColor.getGreen(), addColor.getBlue(), addColor.getAlpha());
                        image.setRGB(x + posX, y + posY, color.getRGB());
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
                Color color = new Color(colorValue, true);

                if (color.getAlpha() != 0) {
                    int red = color.getRed() - value;
                    int green = color.getGreen() - value;
                    int blue = color.getBlue() - value;
                    color = new Color(Math.max(red, 0), Math.max(green, 0), Math.max(blue, 0), color.getAlpha());
                    image.setRGB(x, y, color.getRGB());
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
                Color color = new Color(colorValue, true);

                if (color.getAlpha() != 0) {
                    int red = color.getRed() + xValue;
                    int green = color.getGreen() + yValue;
                    int blue = color.getBlue() + zValue;
                    color = new Color(red < 0 ? 0 : (Math.min(red, 255)), green < 0 ? 0 : (Math.min(green, 255)), blue < 0 ? 0 : (Math.min(blue, 255)), color.getAlpha());
                    image.setRGB(x, y, color.getRGB());
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
                Color color = new Color(colorValue, true);

                if (color.getAlpha() != 0) {
                    int red = (int) (color.getRed() * xValue);
                    int green = (int) (color.getGreen() * yValue);
                    int blue = (int) (color.getBlue() * zValue);
                    color = new Color(red < 0 ? 0 : (Math.min(red, 255)), green < 0 ? 0 : (Math.min(green, 255)), blue < 0 ? 0 : (Math.min(blue, 255)), color.getAlpha());
                    image.setRGB(x, y, color.getRGB());
                }
            }
        }
        return image;
    }

    public static BufferedImage multiply(BufferedImage image, BufferedImage imageOnTop) {
        for (int y = 0; y < image.getHeight() && y < imageOnTop.getHeight(); y++) {
            for (int x = 0; x < image.getWidth() && x < imageOnTop.getWidth(); x++) {
                int value = image.getRGB(x, y);
                Color color = new Color(value, true);

                int multiplyValue = imageOnTop.getRGB(x, y);
                Color multiplyColor = new Color(multiplyValue, true);

                int red = (int) Math.round((double) color.getRed() / 255 * (double) multiplyColor.getRed());
                int green = (int) Math.round((double) color.getGreen() / 255 * (double) multiplyColor.getGreen());
                int blue = (int) Math.round((double) color.getBlue() / 255 * (double) multiplyColor.getBlue());
                color = new Color(red, green, blue, color.getAlpha());
                image.setRGB(x, y, color.getRGB());
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
                Color color = new Color(colorValue, true);

                int alpha = color.getAlpha() + value;
                color = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha > 255 ? 255 : (Math.max(alpha, 0)));
                image.setRGB(x, y, color.getRGB());
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
                Color bottomColor = new Color(bottomValue, true);

                int topValue = top.getRGB(x, y);
                Color topColor = new Color(topValue, true);

                Color color = new Color(bottomColor.getRed() ^ topColor.getRed(), bottomColor.getGreen() ^ topColor.getGreen(), bottomColor.getBlue() ^ topColor.getBlue(), bottomColor.getAlpha() ^ (topColor.getAlpha() * alpha / 255));
                bottom.setRGB(x, y, color.getRGB());
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
        BufferedImage img = source.getSubimage(x, y, w, h); //fill in the corners of the desired crop location here
        BufferedImage copyOfImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = copyOfImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return copyOfImage;
    }

    public static BufferedImage resizeImage(BufferedImage source, double factor) {
        int w = (int) (source.getWidth() * factor);
        int h = (int) (source.getHeight() * factor);
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
        int height = (int) (source.getHeight() * ((double) width / (double) source.getWidth()));
        return resizeImageAbs(source, width, height);
    }

    public static BufferedImage resizeImageFillHeight(BufferedImage source, int height) {
        int width = (int) (source.getWidth() * ((double) height / (double) source.getHeight()));
        return resizeImageAbs(source, width, height);
    }

    public static BufferedImage resizeImageStretch(BufferedImage source, int pixels) {
        int w = source.getWidth() + pixels;
        int h = source.getHeight() + pixels;
        return resizeImageAbs(source, w, h);
    }

    public static BufferedImage printComponentNoShadow(ResourceManager manager, BufferedImage image, Component component, int centerX, int topY, float fontSize, boolean dynamicFontSize) {
        Component text = ComponentFlattening.flatten(ComponentStringUtils.convertTranslatables(ComponentModernizing.modernize(component), InteractiveChatDiscordSrvAddon.plugin.language));
        String striped = ChatColorUtils.stripColor(ChatColorUtils.filterIllegalColorCodes(PlainTextComponentSerializer.plainText().serialize(text)));

        if (dynamicFontSize) {
            fontSize = Math.round(Math.max(2, fontSize - (float) striped.length() / 3) * 10) / 8.0F;
        }

        BufferedImage textImage = new BufferedImage(image.getWidth(), image.getHeight() * 2, BufferedImage.TYPE_INT_ARGB);
        CharacterDataArray characterDataArray = CharacterDataArray.fromComponent(text);
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

        int x = 0;
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

        int width = x;
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
        g3.drawImage(textImage, x, (int) (y - (height / 5) + Math.max(1, 1 * (fontSize / 8))) - image.getHeight(), null);
        g3.dispose();
        return image;
    }

    public static BufferedImage printComponentRightAligned(ResourceManager manager, BufferedImage image, Component component, int topX, int topY, float fontSize) {
        return printComponentRightAligned(manager, image, component, topX, topY, fontSize, CHAT_COLOR_BACKGROUND_FACTOR);
    }

    public static BufferedImage printComponentRightAligned(ResourceManager manager, BufferedImage image, Component component, int topX, int topY, float fontSize, double shadowFactor) {
        BufferedImage textImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        textImage = printComponent(manager, textImage, component, 0, 0, fontSize, shadowFactor);
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

    public static BufferedImage printComponent(ResourceManager manager, BufferedImage image, Component component, int topX, int topY, float fontSize) {
        return printComponent(manager, image, component, topX, topY, fontSize, CHAT_COLOR_BACKGROUND_FACTOR);
    }

    public static BufferedImage printComponent(ResourceManager manager, BufferedImage image, Component component, int topX, int topY, float fontSize, double shadowFactor) {
        BufferedImage temp = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        temp = printComponent0(manager, temp, component, topX, topY, fontSize, 1);
        BufferedImage shadow = multiply(copyImage(temp), shadowFactor);
        Graphics2D g = image.createGraphics();
        g.drawImage(shadow, (int) (fontSize * 0.15), (int) (fontSize * 0.15), null);
        g.drawImage(temp, 0, 0, null);
        g.dispose();
        return image;
    }

    private static BufferedImage printComponent0(ResourceManager manager, BufferedImage image, Component component, int topX, int topY, float fontSize, double factor) {
        Component text = ComponentFlattening.flatten(ComponentStringUtils.convertTranslatables(ComponentModernizing.modernize(component), InteractiveChatDiscordSrvAddon.plugin.language));
        BufferedImage textImage = new BufferedImage(image.getWidth(), image.getHeight() * 2, BufferedImage.TYPE_INT_ARGB);
        CharacterDataArray characterDataArray = CharacterDataArray.fromComponent(text);
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

        int x = 0;
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
        g.drawImage(textImage, topX, topY - image.getHeight(), null);
        g.dispose();
        return image;
    }

    private static TextColor darker(TextColor color, double factor) {
        return TextColor.color(Math.max((int) (color.red() * factor), 0), Math.max((int) (color.green() * factor), 0), Math.max((int) (color.blue() * factor), 0));
    }

}
