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

import com.loohp.interactivechat.libs.org.apache.commons.text.StringEscapeUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.ints.IntSets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

public class FontProvider {

    private final ResourceManager manager;
    private final String key;
    private final List<MinecraftFont> providers;

    private Int2ObjectMap<IntList> getDisplayableCharactersByWidth;

    public FontProvider(ResourceManager manager, String key, List<MinecraftFont> providers) {
        this.manager = manager;
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

    public IntSet getDisplayableCharacters() {
        IntSet set = new IntOpenHashSet();
        getDisplayableCharactersAsStream().forEach(i -> set.add(i));
        return IntSets.unmodifiable(set);
    }

    public IntStream getDisplayableCharactersAsStream() {
        return this.providers.stream().flatMapToInt(p -> p.getDisplayableCharacters().intStream());
    }

    public Int2ObjectMap<IntList> getDisplayableCharactersByWidth() {
        if (getDisplayableCharactersByWidth == null) {
            Int2ObjectMap<IntList> charactersByWidth = new Int2ObjectOpenHashMap<>();
            getDisplayableCharactersAsStream().forEach(i -> {
                String c = new String(Character.toChars(i));
                int width = forCharacter(c).getCharacterWidth(c);
                charactersByWidth.computeIfAbsent(width, k -> new IntArrayList()).add(i);
            });
            this.getDisplayableCharactersByWidth = charactersByWidth;
        }
        return getDisplayableCharactersByWidth;
    }

    public void reloadFonts() {
        this.getDisplayableCharactersByWidth = null;

        int i = 0;
        if (manager.hasFlag(ResourceManager.Flag.LEGACY_HARDCODED_SPACE_FONT) && (providers.isEmpty() || !(providers.get(0) instanceof SpaceFont))) {
            providers.add(0, SpaceFont.generateLegacyHardcodedInstance(manager, this));
            i--;
        }
        if (providers.isEmpty() || !(providers.get(providers.size() - 1) instanceof MissingFont)) {
            providers.add(new MissingFont(manager, this));
        }

        Iterator<MinecraftFont> itr = providers.iterator();
        while (itr.hasNext()) {
            MinecraftFont font = itr.next();
            try {
                font.reloadFonts();
            } catch (Exception e) {
                new ResourceLoadingException("Unable to load " + font.getClass().getSimpleName() + " provider " + i + " in " + key, e).printStackTrace();
                itr.remove();
            }
            i++;
        }
    }

    public MinecraftFont forCharacterOrNull(String character) {
        return forCharacterOrNull(character, false);
    }

    public MinecraftFont forCharacterOrNull(String character, boolean excludeMissing) {
        for (MinecraftFont font : providers) {
            if ((!excludeMissing || !(font instanceof MissingFont)) && font.canDisplayCharacter(character)) {
                return font;
            }
        }
        return null;
    }

    public MinecraftFont forCharacter(String character) {
        return forCharacter(character, false);
    }

    public MinecraftFont forCharacter(String character, boolean excludeMissing) {
        MinecraftFont font = forCharacterOrNull(character, excludeMissing);
        if (font != null) {
            return font;
        }
        throw new ResourceLoadingException("No font provider can display the character \"" + character + "\" (" + StringEscapeUtils.escapeJava(character) + ") for the font \"" + key + "\", this is likely due to an issue with your resource pack setup.");
    }

}
