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
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FontProvider {

    private String key;
    private List<MinecraftFont> providers;
    private IntList displayableCharacters;

    public FontProvider(String key, List<MinecraftFont> providers) {
        this.key = key;
        this.providers = new ArrayList<>(providers);
        reload();
    }

    public void prependProviders(List<MinecraftFont> newProviders) {
        providers.addAll(0, newProviders);
        reload();
    }

    private void reload() {
        IntList displayableCharacters = new IntArrayList();
        for (MinecraftFont font : this.providers) {
            displayableCharacters.addAll(font.getDisplayableCharacters());
        }
        this.displayableCharacters = displayableCharacters;
    }

    public String getNamespacedKey() {
        return key;
    }

    public List<MinecraftFont> getProviders() {
        return Collections.unmodifiableList(providers);
    }

    public IntList getDisplayableCharacters() {
        return IntLists.unmodifiable(displayableCharacters);
    }

    public void reloadFonts() {
        for (MinecraftFont fonts : providers) {
            fonts.reloadFonts();
        }
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
