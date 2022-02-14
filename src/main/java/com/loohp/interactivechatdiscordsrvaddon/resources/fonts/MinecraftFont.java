package com.loohp.interactivechatdiscordsrvaddon.resources.fonts;

import com.loohp.blockmodelrenderer.utils.ColorUtils;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class MinecraftFont {

    public static final double ITALIC_SHEAR_X = -4.0 / 14.0;
    public static final int OBFUSCATE_OVERLAP_COUNT = 3;
    private static final BufferedImage SPACE_CHAR = new BufferedImage(3, 8, BufferedImage.TYPE_INT_ARGB);
    private static final List<TextDecoration> DECORATIONS_ORDER = new ArrayList<>();

    static {
        DECORATIONS_ORDER.add(TextDecoration.OBFUSCATED);
        DECORATIONS_ORDER.add(TextDecoration.BOLD);
        DECORATIONS_ORDER.add(TextDecoration.ITALIC);
        DECORATIONS_ORDER.add(TextDecoration.STRIKETHROUGH);
        DECORATIONS_ORDER.add(TextDecoration.UNDERLINED);
    }

    public static List<TextDecoration> sortDecorations(List<TextDecoration> decorations) {
        List<TextDecoration> list = new ArrayList<>(decorations.size());
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

    protected void setProvider(FontProvider provider) {
        this.provider = provider;
    }

    public abstract boolean canDisplayCharacter(String character);

    public abstract FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, int lastItalicExtraWidth, TextColor color, List<TextDecoration> decorations);

    public abstract Optional<BufferedImage> getCharacterImage(String character, float fontSize, TextColor color);

    public abstract void reloadFonts();

    public abstract IntSet getDisplayableCharacters();

    protected final FontRenderResult printSpace(BufferedImage image, int x, int y, float fontSize, int lastItalicExtraWidth, TextColor color, List<TextDecoration> decorations) {
        decorations = sortDecorations(decorations);
        Color awtColor = new Color(color.value());
        BufferedImage charImage = SPACE_CHAR;
        int originalW = charImage.getWidth();
        charImage = ImageUtils.resizeImageFillHeight(charImage, Math.round(fontSize));
        int w = charImage.getWidth();
        int h = charImage.getHeight();
        charImage = ImageUtils.multiply(charImage, ImageUtils.changeColorTo(ImageUtils.copyImage(charImage), awtColor));
        int beforeTransformW = w;
        int pixelSize = Math.round((float) beforeTransformW / (float) originalW);
        int strikeSize = (int) (fontSize / 8);
        int boldSize = (int) (fontSize / 16.0 * 2);
        int italicExtraWidth = 0;
        boolean italic = false;
        for (TextDecoration decoration : decorations) {
            switch (decoration) {
                case BOLD:
                    BufferedImage boldImage = new BufferedImage(charImage.getWidth() + 2, charImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    for (int x0 = 0; x0 < charImage.getWidth(); x0++) {
                        for (int y0 = 0; y0 < charImage.getHeight(); y0++) {
                            int pixelColor = charImage.getRGB(x0, y0);
                            int alpha = ColorUtils.getAlpha(pixelColor);
                            if (alpha != 0) {
                                for (int i = 0; i < boldSize; i++) {
                                    boldImage.setRGB(x0 + i, y0, pixelColor);
                                }
                            }
                        }
                    }
                    charImage = boldImage;
                    w += boldSize - 1;
                    break;
                case ITALIC:
                    int extraWidth = (int) ((double) charImage.getHeight() * (4.0 / 14.0));
                    BufferedImage italicImage = new BufferedImage(charImage.getWidth() + extraWidth * 2, charImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = italicImage.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.transform(AffineTransform.getShearInstance(ITALIC_SHEAR_X, 0));
                    g.drawImage(charImage, extraWidth, 0, null);
                    g.dispose();
                    charImage = italicImage;
                    italicExtraWidth = (int) Math.round(-ITALIC_SHEAR_X * h);
                    italic = true;
                    break;
                case STRIKETHROUGH:
                    charImage = ImageUtils.expandCenterAligned(charImage, 0, 0, 0, pixelSize);
                    g = charImage.createGraphics();
                    g.setColor(awtColor);
                    g.fillRect(0, (int) (fontSize / 2), charImage.getWidth(), strikeSize);
                    g.dispose();
                    break;
                case UNDERLINED:
                    charImage = ImageUtils.expandCenterAligned(charImage, 0, strikeSize * 2, 0, pixelSize);
                    g = charImage.createGraphics();
                    g.setColor(awtColor);
                    g.fillRect(0, (int) (fontSize), charImage.getWidth(), strikeSize);
                    g.dispose();
                    break;
                default:
                    break;
            }
        }
        Graphics2D g = image.createGraphics();
        int extraWidth = italic ? 0 : lastItalicExtraWidth;
        g.drawImage(charImage, x + extraWidth, y, null);
        g.dispose();
        return new FontRenderResult(image, w + extraWidth, h, pixelSize, italicExtraWidth);
    }

    protected final BufferedImage getSpaceImage(float fontSize) {
        BufferedImage charImage = SPACE_CHAR;
        charImage = ImageUtils.resizeImageFillHeight(charImage, Math.round(fontSize));
        return charImage;
    }

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
