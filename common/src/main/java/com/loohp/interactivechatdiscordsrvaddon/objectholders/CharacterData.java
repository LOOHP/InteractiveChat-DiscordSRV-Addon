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

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TextComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.ShadowColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration.State;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechat.utils.ComponentCompacting;
import com.loohp.interactivechat.utils.ComponentFlattening;
import it.unimi.dsi.fastutil.chars.CharObjectImmutablePair;
import it.unimi.dsi.fastutil.chars.CharObjectPair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class CharacterData {

    public static ValuePairs<String, List<CharObjectPair<CharacterData>>> fromComponent(Component component, UnaryOperator<String> shaper) {
        List<CharacterData> data = new LinkedList<>();
        StringBuilder sb = new StringBuilder();
        component = ComponentFlattening.flatten(component);
        for (Component each : component.children()) {
            Key font = each.font();
            TextColor textColor = each.color();
            if (textColor == null) {
                textColor = NamedTextColor.WHITE;
            }
            int color = textColor.value();
            OptionalInt shadowColor = each.shadowColor() == null ? OptionalInt.empty() : OptionalInt.of(each.shadowColor().value());
            List<TextDecoration> decorations = each.decorations().entrySet().stream().filter(entry -> entry.getValue().equals(State.TRUE)).map(entry -> entry.getKey()).collect(Collectors.toList());
            String content;
            if (each instanceof TextComponent) {
                content = ((TextComponent) each).content();
            } else {
                content = PlainTextComponentSerializer.plainText().serialize(each);
            }
            if (content.isEmpty()) {
                continue;
            }
            CharacterData characterData = new CharacterData(font, color, shadowColor, decorations);
            for (char c : content.toCharArray()) {
                sb.append(c);
                data.add(characterData);
            }
        }
        String resultStr = shaper.apply(sb.toString());
        List<CharObjectPair<CharacterData>> result = new ArrayList<>(resultStr.length());
        for (int i = 0; i < resultStr.length(); i++) {
            result.add(new CharObjectImmutablePair<>(resultStr.charAt(i), data.get(i)));
        }
        return new ValuePairs<>(resultStr, result);
    }

    public static Component toComponent(List<CharObjectPair<CharacterData>> dataList) {
        List<Component> components = new ArrayList<>(dataList.size());
        for (CharObjectPair<CharacterData> data : dataList) {
            CharacterData characterData = data.right();
            Component component = Component.text(data.firstChar()).font(characterData.getFont()).color(TextColor.color(characterData.getColor()));
            if (characterData.getShadowColor().isPresent()) {
                component = component.shadowColor(ShadowColor.shadowColor(characterData.getShadowColor().getAsInt()));
            }
            for (TextDecoration textDecoration : characterData.getDecorations()) {
                component = component.decorate(textDecoration);
            }
            components.add(component);
        }
        return ComponentCompacting.optimize(Component.empty().children(components));
    }

    private final Key font;
    private final int color;
    private final OptionalInt shadowColor;
    private final List<TextDecoration> decorations;

    public CharacterData(Key font, int color, OptionalInt shadowColor, List<TextDecoration> decorations) {
        this.font = font;
        this.color = color;
        this.shadowColor = shadowColor;
        this.decorations = decorations;
    }

    public Key getFont() {
        return font;
    }

    public int getColor() {
        return color;
    }

    public OptionalInt getShadowColor() {
        return shadowColor;
    }

    public List<TextDecoration> getDecorations() {
        return decorations;
    }

}