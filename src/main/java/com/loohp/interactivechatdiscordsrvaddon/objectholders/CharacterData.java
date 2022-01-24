package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

public class CharacterData {

    private Key font;
    private TextColor color;
    private List<TextDecoration> decorations;

    public CharacterData(Key font, TextColor color, List<TextDecoration> decorations) {
        this.font = font;
        this.color = color;
        this.decorations = decorations;
    }

    public Key getFont() {
        return font;
    }

    public TextColor getColor() {
        return color;
    }

    public List<TextDecoration> getDecorations() {
        return decorations;
    }

}