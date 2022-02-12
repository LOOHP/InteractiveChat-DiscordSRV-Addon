package com.loohp.interactivechatdiscordsrvaddon.resources.fonts;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

public class BitmapFont extends MinecraftFont {

    public static final double ITALIC_SHEAR_X = -4.0 / 14.0;
    protected Int2ObjectMap<BufferedImage> charImages;
    private String resourceLocation;
    private int height;
    private int ascent;
    private int scale;
    private List<String> chars;

    public BitmapFont(ResourceManager manager, FontProvider provider, String resourceLocation, int height, int ascent, List<String> chars) {
        super(manager, provider);
        this.resourceLocation = resourceLocation;
        this.height = height;
        this.ascent = ascent;
        this.chars = chars;
        reloadFonts();
    }

    @Override
    public void reloadFonts() {
        this.charImages = new Int2ObjectOpenHashMap<>();
        if (chars.isEmpty()) {
            return;
        }

        TextureResource resourceFile = manager.getFontManager().getFontResource(resourceLocation);
        if (resourceFile == null || !resourceFile.isTexture()) {
            throw new ResourceLoadingException(resourceLocation + " is not a valid font resource");
        }
        BufferedImage fontFileImage = resourceFile.getTexture();

        int yIncrement = fontFileImage.getHeight() / chars.size();
        this.scale = Math.abs(height == 0 ? 0 : yIncrement / height);
        int y = 0;
        for (String line : chars) {
            if (!line.isEmpty()) {
                int xIncrement = fontFileImage.getWidth() / line.codePointCount(0, line.length());
                int x = 0;
                for (int i = 0; i < line.length(); ) {
                    int character = line.codePointAt(i);
                    i += character < 0x10000 ? 1 : 2;
                    int lastX = 3 * scale;
                    for (int x0 = x; x0 < x + xIncrement; x0++) {
                        for (int y0 = y; y0 < y + yIncrement; y0++) {
                            int alpha = (fontFileImage.getRGB(x0, y0) >> 24) & 0xff;
                            if (alpha != 0) {
                                lastX = x0 - x + 1;
                                break;
                            }
                        }
                    }
                    if (x + lastX >= fontFileImage.getWidth()) {
                        lastX = fontFileImage.getWidth() - x;
                    }
                    if (lastX > 0) {
                        charImages.put(character, fontFileImage.getSubimage(x, y, lastX, yIncrement));
                    }
                    x += xIncrement;
                }
            }
            y += yIncrement;
        }
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    public int getHeight() {
        return height;
    }

    public int getAscent() {
        return ascent;
    }

    public int getScale() {
        return scale;
    }

    public List<String> getChars() {
        return chars;
    }

    @Override
    public boolean canDisplayCharacter(String character) {
        return charImages.containsKey(character.codePointAt(0));
    }

    @Override
    public FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, int lastItalicExtraWidth, TextColor color, List<TextDecoration> decorations) {
        decorations = sortDecorations(decorations);
        Color awtColor = new Color(color.value());
        BufferedImage charImage = ImageUtils.copyImage(charImages.get(character.codePointAt(0)));
        int originalW = charImage.getWidth();
        float scale = fontSize / 8;
        float ascent = this.ascent - 7;
        float descent = height - this.ascent - 1;
        int fillHeight = Math.round(fontSize + (ascent + descent) * scale);
        charImage = ImageUtils.resizeImageFillHeight(charImage, Math.abs(fillHeight));
        int w = charImage.getWidth();
        int h = charImage.getHeight();
        charImage = ImageUtils.multiply(charImage, ImageUtils.changeColorTo(ImageUtils.copyImage(charImage), awtColor));
        int beforeTransformW = w;
        int pixelSize = Math.round((float) beforeTransformW / (float) originalW);
        int strikeSize = (int) (fontSize / 8.0);
        int boldSize = (int) (fontSize / 16.0 * 3);
        int italicExtraWidth = 0;
        boolean italic = false;
        for (TextDecoration decoration : decorations) {
            switch (decoration) {
                case OBFUSCATED:
                    charImage = new BufferedImage(charImage.getWidth(), charImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = charImage.createGraphics();
                    for (int i = 0; i < OBFUSCATE_OVERLAP_COUNT; i++) {
                        String magicCharacter = ComponentStringUtils.toMagic(provider, character);
                        BufferedImage magicImage = provider.forCharacter(magicCharacter).getCharacterImage(magicCharacter, fontSize, color).orElse(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
                        g.drawImage(magicImage, 0, 0, charImage.getWidth(), charImage.getHeight(), null);
                    }
                    g.dispose();
                    break;
                case BOLD:
                    BufferedImage boldImage = new BufferedImage(charImage.getWidth() + 2, charImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    for (int x0 = 0; x0 < charImage.getWidth(); x0++) {
                        for (int y0 = 0; y0 < charImage.getHeight(); y0++) {
                            int pixelColor = charImage.getRGB(x0, y0);
                            int alpha = (pixelColor >> 24) & 0xff;
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
                    g = italicImage.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.transform(AffineTransform.getShearInstance(ITALIC_SHEAR_X, 0));
                    g.drawImage(charImage, extraWidth, 0, null);
                    g.dispose();
                    charImage = italicImage;
                    italicExtraWidth = (int) Math.round(-ITALIC_SHEAR_X * h);
                    italic = true;
                    break;
                case STRIKETHROUGH:
                    charImage = ImageUtils.expandCenterAligned(charImage, 0, 0, 0, pixelSize * this.scale);
                    g = charImage.createGraphics();
                    g.setColor(awtColor);
                    g.fillRect(0, (int) (fontSize / 2), charImage.getWidth(), strikeSize);
                    g.dispose();
                    break;
                case UNDERLINED:
                    charImage = ImageUtils.expandCenterAligned(charImage, 0, strikeSize * 2, 0, pixelSize * this.scale);
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
        int sign = fillHeight >= 0 ? 1 : -1;
        int spaceWidth = pixelSize * this.scale;
        if (sign > 0) {
            g.drawImage(charImage, x + extraWidth, (int) (y - ascent * scale), null);
        } else {
            g.drawImage(charImage, x + extraWidth, (int) (y - ascent * scale), -x, charImage.getHeight(), null);
            spaceWidth += scale * 2;
        }
        g.dispose();
        return new FontRenderResult(image, (w + extraWidth) * sign, h, spaceWidth, italicExtraWidth);
    }

    @Override
    public Optional<BufferedImage> getCharacterImage(String character, float fontSize, TextColor color) {
        Color awtColor = new Color(color.value());
        BufferedImage charImage = ImageUtils.copyImage(charImages.get(character.codePointAt(0)));
        float descent = height - this.ascent - 1;
        charImage = ImageUtils.resizeImageFillHeight(charImage, Math.abs(Math.round(fontSize + (ascent + descent) * scale)));
        charImage = ImageUtils.multiply(charImage, ImageUtils.changeColorTo(ImageUtils.copyImage(charImage), awtColor));
        return Optional.of(charImage);
    }

    @Override
    public IntSet getDisplayableCharacters() {
        return IntSets.unmodifiable(charImages.keySet());
    }

}
