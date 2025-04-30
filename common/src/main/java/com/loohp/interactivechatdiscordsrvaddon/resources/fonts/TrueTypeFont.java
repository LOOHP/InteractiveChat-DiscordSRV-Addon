/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
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

import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;
import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TrueTypeFont extends MinecraftFont {

    public static final String TYPE_KEY = "ttf";

    private static final BufferedImage INTERNAL_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    private String resourceLocation;
    private AffineTransform shift;
    private float size;
    private float oversample;
    private String exclude;

    private IntSet displayableCharacters;
    private Graphics2D internalGraphics;
    private Font font;

    public TrueTypeFont(ResourceManager manager, FontProvider provider, String resourceLocation, AffineTransform shift, float size, float oversample, String exclude) {
        super(manager, provider);
        this.resourceLocation = resourceLocation;
        this.shift = shift;
        this.size = size;
        this.oversample = oversample;
        this.exclude = exclude;
        this.internalGraphics = null;

        try {
            GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        } catch (Throwable e) {
            throw new ResourceLoadingException("No fonts provided by the JVM or the Operating System!\nCheck the Q&A section in https://www.spigotmc.org/resources/83917/ for more information", e);
        }
    }

    @Override
    public void reloadFonts() {
        this.displayableCharacters = IntSets.EMPTY_SET;
        if (this.internalGraphics != null) {
            this.internalGraphics.dispose();
            this.internalGraphics = null;
        }
        try {
            this.font = Font.createFont(Font.TRUETYPE_FONT, manager.getFontManager().getFontResource(resourceLocation).getFile().getInputStream()).deriveFont(shift);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(this.font);
            this.internalGraphics = INTERNAL_IMAGE.createGraphics();
            this.internalGraphics.setFont(font.deriveFont(size));

            IntSet displayableCharacters = new IntLinkedOpenHashSet();
            for (int i = 0; i < 0x10F800; i += 1) {
                String character = new String(Character.toChars(i));
                if (!this.exclude.contains(character) && canDisplayCharacter(character)) {
                    displayableCharacters.add(i);
                }
            }
            this.displayableCharacters = IntSets.unmodifiable(displayableCharacters);
        } catch (FontFormatException | IOException e) {
            if (provider == null) {
                throw new ResourceLoadingException("Unable to load TrueTypeFont provider at " + resourceLocation, e);
            } else {
                throw new ResourceLoadingException("Unable to load TrueTypeFont provider at " + resourceLocation + " (Defined in " + provider.getNamespacedKey() + ")", e);
            }
        }
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    public AffineTransform getShift() {
        return shift;
    }

    public float getSize() {
        return size;
    }

    public float getOversample() {
        return oversample;
    }

    public String getExclude() {
        return exclude;
    }

    public Font getFont() {
        return font;
    }

    @Override
    public boolean canDisplayCharacter(String character) {
        if (internalGraphics == null) {
            return false;
        }
        if (exclude.contains(character)) {
            return false;
        }
        return font.canDisplayUpTo(character) < 0 && internalGraphics.getFontMetrics().stringWidth(character) != 0;
    }

    @Override
    public IntSet getDisplayableCharacters() {
        return displayableCharacters;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, int lastItalicExtraWidth, int color, List<TextDecoration> decorations) {
        float scale = fontSize / 16;
        fontSize = fontSize - (13 - this.size);
        decorations = sortDecorations(decorations);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setFont(font.deriveFont(this.size));
        int w = g.getFontMetrics().stringWidth(character);
        Font fontToPrint = font.deriveFont(fontSize);
        BufferedImage[] magicCharImages = null;
        boolean bold = false;
        boolean italic = false;
        for (TextDecoration decoration : decorations) {
            switch (decoration) {
                case OBFUSCATED:
                    magicCharImages = new BufferedImage[OBFUSCATE_OVERLAP_COUNT];
                    for (int i = 0; i < magicCharImages.length; i++) {
                        String magicCharacter = ComponentStringUtils.toMagic(provider, character);
                        magicCharImages[i] = provider.forCharacter(magicCharacter).getCharacterImage(magicCharacter, fontSize, color).orElse(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB));
                    }
                    break;
                case BOLD:
                    bold = true;
                    break;
                case ITALIC:
                    fontToPrint = fontToPrint.deriveFont(Font.ITALIC);
                    italic = true;
                    break;
                case STRIKETHROUGH:
                    Map attributes = fontToPrint.getAttributes();
                    attributes.put(TextAttribute.STRIKETHROUGH, true);
                    fontToPrint = new Font(attributes);
                    break;
                case UNDERLINED:
                    attributes = fontToPrint.getAttributes();
                    attributes.put(TextAttribute.UNDERLINE, true);
                    fontToPrint = new Font(attributes);
                    break;
                default:
                    break;
            }
        }
        g.setColor(new Color(color));
        g.setFont(fontToPrint);
        int height = g.getFontMetrics().getHeight() / 2;
        int newW = g.getFontMetrics().stringWidth(character);
        int finalWidth = newW;
        int extraWidth = italic ? 0 : lastItalicExtraWidth;
        if (magicCharImages == null) {
            g.drawString(character, x, y + height);
            if (bold) {
                g.drawString(character, x + (scale * 2) + extraWidth, y + height);
                finalWidth += scale * 2;
            }
        } else {
            for (BufferedImage magicCharImage : magicCharImages) {
                g.drawImage(magicCharImage, x + extraWidth, y, newW, height, null);
            }
            if (bold) {
                for (BufferedImage magicCharImage : magicCharImages) {
                    g.drawImage(magicCharImage, (int) (x + (scale * 2)) + extraWidth, y, newW, height, null);
                }
                finalWidth += scale * 2;
            }
        }
        g.dispose();
        float spaceWidth = (float) newW / (float) w;
        return new FontRenderResult(image, finalWidth + extraWidth, height + (int) Math.round(shift.getTranslateY()), (int) Math.round(spaceWidth + shift.getTranslateX()), 0);
    }

    @Override
    public Optional<BufferedImage> getCharacterImage(String character, float fontSize, int color) {
        BufferedImage image = new BufferedImage((int) (10 * fontSize), (int) (10 * fontSize), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        Font fontToPrint = font.deriveFont(fontSize);
        g.setColor(new Color(color));
        g.setFont(fontToPrint);
        int height = g.getFontMetrics().getHeight() / 2;
        g.drawString(character, 0, height);
        image = ImageUtils.copyAndGetSubImage(image, 0, 0, g.getFontMetrics().stringWidth(character), height);
        g.dispose();
        return Optional.of(image);
    }

    @Override
    public int getCharacterWidth(String character) {
        return internalGraphics.getFontMetrics().stringWidth(character);
    }

}
