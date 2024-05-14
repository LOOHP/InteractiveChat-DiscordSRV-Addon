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

import com.loohp.blockmodelrenderer.utils.ColorUtils;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.GeneratedTextureResource;
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

@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public class LegacyUnicodeFont extends MinecraftFont {

    public static final String TYPE_KEY = "legacy_unicode";
    public static final double ITALIC_SHEAR_X = -4.0 / 14.0;

    public static String getSectionSubstring(int i) {
        return String.format("%04x", i).substring(0, 2);
    }

    protected Optional<FontResource> missingCharacter;
    private Int2ObjectMap<Optional<FontResource>> charImages;
    private Int2ObjectMap<GlyphSize> sizes;
    private String template;

    public LegacyUnicodeFont(ResourceManager manager, FontProvider provider, Int2ObjectMap<GlyphSize> sizes, String template) {
        super(manager, provider);
        this.sizes = sizes;
        this.template = template;

        BufferedImage missingCharacter = new BufferedImage(5, 8, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 5; ++j) {
                boolean flag = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
                missingCharacter.setRGB(j, i, flag ? 0xFFFFFFFF : 0);
            }
        }
        this.missingCharacter = Optional.of(new FontTextureResource(new GeneratedTextureResource(manager, missingCharacter)));
    }

    @Override
    public void reloadFonts() {
        this.charImages = new Int2ObjectOpenHashMap<>();

        if (!hasTemplate()) {
            return;
        }

        for (int i = 0; i < 65536; i += 256) {
            TextureResource resource = manager.getFontManager().getFontResource(template.replaceFirst("%s", getSectionSubstring(i)));
            if (resource == null) {
                continue;
            }
            BufferedImage fontBaseImage = resource.getTexture(256, 256);
            int u = 0;
            for (int y = 0; y < 256; y += 16) {
                for (int x = 0; x < 256; x += 16) {
                    int character = i + u;
                    if (character != 0) {
                        GlyphSize size = sizes.get(character);
                        if (size.getEnd() - size.getStart() > 0) {
                            charImages.put(character, Optional.of(new FontTextureResource(resource, 256, 256, x + size.getStart(), y, size.getEnd() - size.getStart() + 1, 16)));
                        } else {
                            charImages.put(character, Optional.empty());
                        }
                    }
                    u++;
                }
            }
        }
    }

    public IntSet getCharacterSets() {
        return charImages.keySet();
    }

    public Int2ObjectMap<GlyphSize> getSizes() {
        return sizes;
    }

    public boolean hasTemplate() {
        return template != null;
    }

    public String getTemplate() {
        return template;
    }

    @Override
    public boolean canDisplayCharacter(String character) {
        return charImages.containsKey(character.codePointAt(0));
    }

    @Override
    public FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, int lastItalicExtraWidth, TextColor color, List<TextDecoration> decorations) {
        decorations = sortDecorations(decorations);
        Color awtColor = new Color(color.value());
        Optional<FontResource> optCharImage = charImages.get(character.codePointAt(0));
        if (optCharImage == null) {
            optCharImage = missingCharacter;
        }
        if (optCharImage.isPresent()) {
            BufferedImage charImage = optCharImage.get().getFontImage();
            int originalW = charImage.getWidth();
            charImage = ImageUtils.resizeImageFillHeight(charImage, (int) Math.floor(fontSize));
            int w = charImage.getWidth();
            int h = charImage.getHeight();
            charImage = ImageUtils.multiply(charImage, ImageUtils.changeColorTo(ImageUtils.copyImage(charImage), awtColor));
            int beforeTransformW = w;
            double accuratePixelSize = (double) beforeTransformW / (double) originalW;
            int pixelSize = (int) Math.round(accuratePixelSize);
            int strikeSize = (int) (fontSize / 8);
            int boldSize = (int) (fontSize / 16.0 * 2);
            int italicExtraWidth = 0;
            boolean italic = false;
            boolean underlineStrikethroughExpanded = false;
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
                        BufferedImage boldImage = new BufferedImage(charImage.getWidth() + boldSize, charImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
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
                        charImage = ImageUtils.expandCenterAligned(charImage, 0, 0, 0, underlineStrikethroughExpanded ? 0 : (pixelSize + 1));
                        underlineStrikethroughExpanded = true;
                        g = charImage.createGraphics();
                        g.setColor(awtColor);
                        g.fillRect(0, Math.round((fontSize / 2) - ((float) strikeSize / 2)), w + pixelSize + 1, strikeSize);
                        g.dispose();
                        break;
                    case UNDERLINED:
                        charImage = ImageUtils.expandCenterAligned(charImage, 0, strikeSize * 2, 0, underlineStrikethroughExpanded ? 0 : (pixelSize + 1));
                        underlineStrikethroughExpanded = true;
                        g = charImage.createGraphics();
                        g.setColor(awtColor);
                        g.fillRect(0, Math.round(fontSize), w + pixelSize + 1, strikeSize);
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
            return new FontRenderResult(image, w + extraWidth, h, (int) Math.floor(accuratePixelSize + 1), italicExtraWidth);
        } else {
            return new FontRenderResult(image, 0, 0, 0, lastItalicExtraWidth);
        }
    }

    @Override
    public Optional<BufferedImage> getCharacterImage(String character, float fontSize, TextColor color) {
        Color awtColor = new Color(color.value());
        Optional<FontResource> optCharImage = charImages.get(character.codePointAt(0));
        if (optCharImage == null) {
            optCharImage = missingCharacter;
        }
        if (optCharImage.isPresent()) {
            BufferedImage charImage = optCharImage.get().getFontImage();
            charImage = ImageUtils.resizeImageFillHeight(charImage, Math.round(fontSize));
            charImage = ImageUtils.multiply(charImage, ImageUtils.changeColorTo(ImageUtils.copyImage(charImage), awtColor));
            return Optional.of(charImage);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int getCharacterWidth(String character) {
        GlyphSize size = sizes.get(character.codePointAt(0));
        return size.getEnd() - size.getStart() + 1;
    }

    @Override
    public IntSet getDisplayableCharacters() {
        return IntSets.unmodifiable(charImages.keySet());
    }

    public static class GlyphSize {

        private byte start;
        private byte end;

        public GlyphSize(byte start, byte end) {
            this.start = start;
            this.end = end;
        }

        public byte getStart() {
            return start;
        }

        public byte getEnd() {
            return end;
        }

    }

}
