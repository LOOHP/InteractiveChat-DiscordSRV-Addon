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
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.GeneratedTextureResource;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnihexFont extends MinecraftFont {

    public static final String TYPE_KEY = "unihex";
    public static final double ITALIC_SHEAR_X = -4.0 / 14.0;
    public static final int HEIGHT = 16;

    private static boolean readUntilDelimiter(InputStream inputStream, ByteList data, int delimiter) throws IOException {
        int i;
        while ((i = inputStream.read()) != -1) {
            if (i == delimiter) {
                return true;
            }
            data.add((byte) i);
        }
        return false;
    }

    private static BitSet toBitSet(int lineNum, ByteList data) {
        BitSet bitSet = new BitSet(data.size() * 4);
        int c = 0;
        for (byte i : data) {
            int value = getHexDigitValue(lineNum, i);
            bitSet.set(c++, ((value >> 3) & 1) == 1);
            bitSet.set(c++, ((value >> 2) & 1) == 1);
            bitSet.set(c++, ((value >> 1) & 1) == 1);
            bitSet.set(c++, (value & 1) == 1);
        }
        return bitSet;
    }

    private static int getHexDigitValue(int lineNum, byte digit) {
        switch (digit) {
            case 48: return 0;
            case 49: return 1;
            case 50: return 2;
            case 51: return 3;
            case 52: return 4;
            case 53: return 5;
            case 54: return 6;
            case 55: return 7;
            case 56: return 8;
            case 57: return 9;
            case 65: return 10;
            case 66: return 11;
            case 67: return 12;
            case 68: return 13;
            case 69: return 14;
            case 70: return 15;
            default: throw new IllegalArgumentException("Invalid entry at line " + lineNum + ": expected hex digit, got " + (char) digit);
        }
    }

    protected final Optional<FontResource> missingCharacter;
    private Int2ObjectMap<Optional<FontResource>> charImages;
    private Int2IntMap charWidth;
    private final List<SizeOverride> sizeOverrides;
    private final String hexFileResourceLocation;

    public UnihexFont(ResourceManager manager, FontProvider provider, List<SizeOverride> sizeOverrides, String hexFileResourceLocation) {
        super(manager, provider);
        this.sizeOverrides = Collections.unmodifiableList(sizeOverrides);
        this.hexFileResourceLocation = hexFileResourceLocation;

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
        this.charWidth = new Int2IntOpenHashMap();
        if (!hasHexFileResourceLocation()) {
            return;
        }
        try (ZipInputStream zipInputStream = getHexFileInputStream()) {
            for (ZipEntry zipEntry = zipInputStream.getNextEntry(); zipEntry != null; zipEntry = zipInputStream.getNextEntry()) {
                if (!zipEntry.getName().endsWith(".hex")) {
                    continue;
                }
                int line = 0;
                while (true) {
                    ByteList byteList = new ByteArrayList(128);
                    if (!readUntilDelimiter(zipInputStream, byteList, 58) || byteList.size() == 0) {
                        break;
                    }
                    int byteListSize = byteList.size();
                    if (byteListSize != 4 && byteListSize != 5 && byteListSize != 6) {
                        throw new IllegalArgumentException("Invalid entry at line " + line + ": expected 4, 5 or 6 hex digits followed by a colon");
                    }
                    int codePoint = 0;
                    for (int u = 0; u < byteListSize; u++) {
                        codePoint = codePoint << 4 | getHexDigitValue(line, byteList.getByte(u));
                    }

                    byteList.clear();
                    readUntilDelimiter(zipInputStream, byteList, 10);
                    BitSet bits = toBitSet(line, byteList);

                    int rawWidth;
                    switch (byteList.size()) {
                        case 32: {rawWidth = 8; break;}
                        case 64: {rawWidth = 16; break;}
                        case 96: {rawWidth = 24; break;}
                        case 128: {rawWidth = 32; break;}
                        default: throw new IllegalArgumentException("Invalid entry at line " + line + ": expected hex number describing (8,16,24,32) x 16 bitmap, followed by a new line");
                    };
                    int left = rawWidth;
                    int right = 0;
                    SizeOverride sizeOverride = getSizeOverride(new String(Character.toChars(codePoint)));
                    if (sizeOverride == null) {
                        outer: for (int column = 0; column < rawWidth; column++) {
                            for (int u = column; u < bits.size(); u += rawWidth) {
                                if (bits.get(u)) {
                                    left = column;
                                    break outer;
                                }
                            }
                        }
                        outer: for (int column = rawWidth - 1; column >= 0; column--) {
                            for (int u = column; u < bits.size(); u += rawWidth) {
                                if (bits.get(u)) {
                                    right = column;
                                    break outer;
                                }
                            }
                        }
                    } else {
                        left = sizeOverride.getLeft();
                        right = sizeOverride.getRight();
                    }
                    if (left >= right) {
                        charWidth.put(codePoint, 0);
                        charImages.put(codePoint, Optional.empty());
                    } else {
                        int width = right - left + 1;
                        BitSet pixels = new BitSet(width * HEIGHT);
                        for (int y = 0; y < HEIGHT; y++) {
                            for (int x = left; x <= right; x++) {
                                pixels.set(y * width + (x - left), bits.get(y * rawWidth + x));
                            }
                        }
                        charWidth.put(codePoint, width);
                        charImages.put(codePoint, Optional.of(new FontBitmapResource(width, HEIGHT, pixels)));
                    }
                    line++;
                }
            }
        } catch (Exception e) {
            throw new ResourceLoadingException("Invalid unihex zip entries " + getHexFile().getAbsolutePath(), e);
        }
    }

    public IntSet getCharacterSets() {
        return charImages.keySet();
    }

    public List<SizeOverride> getSizeOverrides() {
        return sizeOverrides;
    }

    public SizeOverride getSizeOverride(String character) {
        return sizeOverrides.stream().filter(e -> e.characterIncluded(character)).findFirst().orElse(null);
    }

    public boolean hasHexFileResourceLocation() {
        return hexFileResourceLocation != null;
    }

    public String getHexFileResourceLocation() {
        return hexFileResourceLocation;
    }

    public ResourcePackFile getHexFile() {
        if (!hasHexFileResourceLocation()) {
            return null;
        }
        return manager.findResource(hexFileResourceLocation);
    }

    public ZipInputStream getHexFileInputStream() throws IOException {
        return new ZipInputStream(getHexFile().getInputStream());
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
        return charWidth.get(character.codePointAt(0));
    }

    @Override
    public IntSet getDisplayableCharacters() {
        return IntSets.unmodifiable(charImages.keySet());
    }

    public static class SizeOverride {

        private final int from;
        private final int to;
        private final byte left;
        private final byte right;

        public SizeOverride(int from, int to, byte left, byte right) {
            this.from = from;
            this.to = to;
            this.left = left;
            this.right = right;
        }

        public boolean characterIncluded(String character) {
            int codePoint = character.codePointAt(0);
            return codePoint >= from && codePoint <= to;
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }

        public byte getLeft() {
            return left;
        }

        public byte getRight() {
            return right;
        }

    }

}
