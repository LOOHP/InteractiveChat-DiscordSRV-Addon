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

import com.loohp.interactivechat.libs.org.apache.commons.lang3.StringEscapeUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FontProvider {

    private String key;
    private List<MinecraftFont> providers;
    private Int2ObjectMap<IntList> displayableCharactersByWidth;

    public FontProvider(String key, List<MinecraftFont> providers) {
        this.key = key;
        this.providers = new ArrayList<>(providers);
    }

    public void prependProviders(List<MinecraftFont> newProviders) {
        providers.addAll(0, newProviders);
    }

    public String getNamespacedKey() {
        return key;
    }

    public List<MinecraftFont> getProviders() {
        return Collections.unmodifiableList(providers);
    }

    public Int2ObjectMap<IntList> getDisplayableCharactersByWidth() {
        return Int2ObjectMaps.unmodifiable(displayableCharactersByWidth);
    }

    public void reloadFonts() {
        for (MinecraftFont fonts : providers) {
            fonts.reloadFonts();
        }
        IntSet set = new IntOpenHashSet();
        Int2ObjectMap<IntList> displayableCharactersByWidth = new Int2ObjectOpenHashMap<>();
        for (MinecraftFont font : this.providers) {
            for (int codePoint : font.getDisplayableCharacters()) {
                if (set.contains(codePoint)) {
                    continue;
                }
                int width = font.getCharacterWidth(new String(Character.toChars(codePoint)));
                IntList list = displayableCharactersByWidth.get(width);
                if (list == null) {
                    displayableCharactersByWidth.put(width, list = new IntArrayList());
                }
                list.add(codePoint);
                set.add(codePoint);
            }
        }
        for (Entry<IntList> entry : displayableCharactersByWidth.int2ObjectEntrySet()) {
            entry.setValue(IntLists.unmodifiable(entry.getValue()));
        }
        this.displayableCharactersByWidth = displayableCharactersByWidth;
    }

    public MinecraftFont forCharacterOrNull(String character) {
        for (MinecraftFont font : providers) {
            if (font.canDisplayCharacter(character)) {
                return font;
            }
        }
        return null;
    }

    @SuppressWarnings("deprecation")
    public MinecraftFont forCharacter(String character) {
        MinecraftFont font = forCharacterOrNull(character);
        if (font != null) {
            return font;
        }
        throw new ResourceLoadingException("No font provider can display the character \"" + character + "\" (" + StringEscapeUtils.escapeJava(character) + ") for the font \"" + key + "\", this is likely due to an issue with your resource pack setup.");
    }

}
