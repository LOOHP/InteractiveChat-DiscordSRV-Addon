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
import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.fonts.LegacyUnicodeFont.GlyphSize;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @SuppressWarnings("deprecation")
    public static MinecraftFont fromJson(ResourceManager manager, IFontManager fontManager, FontProvider provider, JSONObject fontJson) throws Exception {
        String typeStr = fontJson.get("type").toString();
        switch (typeStr) {
            case SpaceFont.TYPE_KEY: {
                Int2IntMap charAdvances = new Int2IntOpenHashMap();
                JSONObject advancesJson = (JSONObject) fontJson.get("advances");
                for (Object obj1 : advancesJson.keySet()) {
                    String character = (String) obj1;
                    int advance = ((Number) advancesJson.get(character)).intValue();
                    charAdvances.put(character.codePointAt(0), advance);
                }
                return new SpaceFont(manager, provider, charAdvances);
            }
            case BitmapFont.TYPE_KEY: {
                String resourceLocation = fontJson.get("file").toString();
                int height = ((Number) fontJson.getOrDefault("height", 8)).intValue();
                int ascent = ((Number) fontJson.get("ascent")).intValue();
                List<String> chars = (List<String>) ((JSONArray) fontJson.get("chars")).stream().map(each -> each.toString()).collect(Collectors.toList());
                return new BitmapFont(manager, provider, resourceLocation, height, ascent, chars);
            }
            case LegacyUnicodeFont.TYPE_KEY: {
                String template = fontJson.get("template").toString();
                DataInputStream sizesInput = new DataInputStream(new BufferedInputStream(fontManager.getFontResource(fontJson.get("sizes").toString()).getFile().getInputStream()));
                Int2ObjectOpenHashMap<GlyphSize> sizes = new Int2ObjectOpenHashMap<>();
                for (int i = 0; ; i++) {
                    try {
                        byte b = sizesInput.readByte();
                        byte start = (byte) ((b >> 4) & 15);
                        byte end = (byte) (b & 15);
                        sizes.put(i, new GlyphSize(start, end));
                    } catch (EOFException e) {
                        break;
                    }
                }
                sizesInput.close();
                return new LegacyUnicodeFont(manager, provider, sizes, template);
            }
            case TrueTypeFont.TYPE_KEY: {
                String resourceLocation = fontJson.get("file").toString();
                JSONArray shiftArray = (JSONArray) fontJson.get("shift");
                float leftShift = ((Number) shiftArray.get(0)).floatValue();
                float downShift = ((Number) shiftArray.get(1)).floatValue();
                AffineTransform shift = AffineTransform.getTranslateInstance(-leftShift, downShift);
                float size = ((Number) fontJson.get("size")).floatValue();
                float oversample = ((Number) fontJson.get("oversample")).floatValue();
                String skip = fontJson.getOrDefault("skip", "").toString();
                return new TrueTypeFont(manager, provider, resourceLocation, shift, size, oversample, skip);
            }
            case ReferenceFont.TYPE_KEY: {
                String id = fontJson.get("id").toString();
                return new ReferenceFont(manager, provider, id);
            }
            case UnihexFont.TYPE_KEY: {
                String hexFile = fontJson.get("hex_file").toString();
                List<UnihexFont.SizeOverride> sizeOverrides;
                JSONArray sizeOverridesArray = (JSONArray) fontJson.get("size_overrides");
                if (sizeOverridesArray == null) {
                    sizeOverrides = Collections.emptyList();
                } else {
                    sizeOverrides = new ArrayList<>(sizeOverridesArray.size());
                    for (Object obj : sizeOverridesArray) {
                        JSONObject sizeOverride = (JSONObject) obj;
                        int from = ((String) sizeOverride.get("from")).codePointAt(0);
                        int to = ((String) sizeOverride.get("to")).codePointAt(0);
                        byte left = ((Number) sizeOverride.get("left")).byteValue();
                        byte right = ((Number) sizeOverride.get("right")).byteValue();
                        sizeOverrides.add(new UnihexFont.SizeOverride(from, to, left, right));
                    }
                }
                return new UnihexFont(manager, provider, sizeOverrides, hexFile);
            }
            default: {
                throw new ResourceLoadingException("Unknown font type \"" + typeStr + "\"");
            }
        }
    }

    protected final ResourceManager manager;

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

    public abstract void reloadFonts();

    public abstract boolean canDisplayCharacter(String character);

    public abstract FontRenderResult printCharacter(BufferedImage image, String character, int x, int y, float fontSize, int lastItalicExtraWidth, int color, List<TextDecoration> decorations);

    public abstract Optional<BufferedImage> getCharacterImage(String character, float fontSize, int color);

    public abstract int getCharacterWidth(String character);

    public abstract IntSet getDisplayableCharacters();

    public static class FontRenderResult {

        private final BufferedImage image;
        private final int width;
        private final int height;
        private final int spaceWidth;
        private final int italicExtraWidth;

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
