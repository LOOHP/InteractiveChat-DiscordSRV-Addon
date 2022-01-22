package com.loohp.interactivechatdiscordsrvaddon.resources.fonts;

import com.loohp.interactivechat.libs.org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FontProvider {

    private String key;
    private List<MinecraftFont> providers;
    private List<String> displayableCharacters;

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
        Set<String> displayableCharacters = new HashSet<>();
        for (MinecraftFont font : this.providers) {
            displayableCharacters.addAll(font.getDisplayableCharacters());
        }
        this.displayableCharacters = new ArrayList<>(displayableCharacters);
    }

    public String getNamespacedKey() {
        return key;
    }

    public List<MinecraftFont> getProviders() {
        return Collections.unmodifiableList(providers);
    }

    public List<String> getDisplayableCharacters() {
        return Collections.unmodifiableList(displayableCharacters);
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

    public MinecraftFont forCharacter(String character) {
        MinecraftFont font = forCharacterOrNull(character);
        if (font != null) {
            return font;
        }
        throw new RuntimeException("No font provider can display the character \"" + character + "\" (" + StringEscapeUtils.escapeJava(character) + ") for the font \"" + key + "\", this is likely due to an issue with your resource pack setup.");
    }

}
