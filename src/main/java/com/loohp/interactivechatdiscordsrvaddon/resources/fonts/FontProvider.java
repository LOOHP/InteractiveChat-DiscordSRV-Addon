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
