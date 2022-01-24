package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TextComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration.State;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ComponentFlattening;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CharacterDataArray {

    public static CharacterDataArray fromComponent(Component component) {
        List<CharacterData> data = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        component = ComponentFlattening.flatten(component);
        for (Component each : component.children()) {
            Key font = each.style().font();
            if (font == null) {
                font = Key.key("minecraft:default");
            }
            TextColor color = each.color();
            if (color == null) {
                color = NamedTextColor.WHITE;
            }
            List<TextDecoration> decorations = each.decorations().entrySet().stream().filter(entry -> entry.getValue().equals(State.TRUE)).map(entry -> entry.getKey()).collect(Collectors.toList());
            String content;
            if (each instanceof TextComponent) {
                content = ChatColorUtils.filterIllegalColorCodes(((TextComponent) each).content());
            } else {
                content = ChatColorUtils.filterIllegalColorCodes(PlainTextComponentSerializer.plainText().serialize(each));
            }
            if (content.isEmpty()) {
                continue;
            }
            for (char c : content.toCharArray()) {
                data.add(new CharacterData(font, color, decorations));
                sb.append(c);
            }
        }
        return new CharacterDataArray(sb.toString().toCharArray(), data.toArray(new CharacterData[data.size()]));
    }

    private char[] chars;
    private CharacterData[] data;

    public CharacterDataArray(char[] chars, CharacterData[] data) {
        this.chars = chars;
        this.data = data;
    }

    public char[] getChars() {
        return chars;
    }

    public CharacterData[] getData() {
        return data;
    }

}
