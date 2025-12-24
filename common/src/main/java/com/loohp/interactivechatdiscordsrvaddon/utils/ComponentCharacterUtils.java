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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.ObjectComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TextComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.ShadowColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechat.utils.ComponentCompacting;
import com.loohp.interactivechat.utils.ComponentFlattening;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.CharacterData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ComponentCharacter;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ComponentCharacterUtils {

    public static ValuePairs<String, List<ValuePairs<ComponentCharacter, CharacterData>>> fromComponent(Component component, UnaryOperator<String> shaper) {
        List<ValuePairs<ComponentCharacter, CharacterData>> data = new ArrayList<>();
        component = ComponentFlattening.flatten(component);
        for (Component each : component.children()) {
            Key font = each.font();
            TextColor textColor = each.color();
            if (textColor == null) {
                textColor = NamedTextColor.WHITE;
            }
            int color = textColor.value();
            OptionalInt shadowColor = each.shadowColor() == null ? OptionalInt.empty() : OptionalInt.of(each.shadowColor().value());
            List<TextDecoration> decorations = each.decorations().entrySet().stream().filter(entry -> entry.getValue().equals(TextDecoration.State.TRUE)).map(entry -> entry.getKey()).collect(Collectors.toList());
            CharacterData characterData = new CharacterData(font, color, shadowColor, decorations);
            if (each instanceof TextComponent) {
                String content = ((TextComponent) each).content();
                for (char c : content.toCharArray()) {
                    data.add(new ValuePairs<>(ComponentCharacter.text(c), characterData));
                }
            } else if (each instanceof ObjectComponent) {
                data.add(new ValuePairs<>(ComponentCharacter.object(((ObjectComponent) each).contents()), characterData));
            } else {
                String content = PlainTextComponentSerializer.plainText().serialize(each);
                for (char c : content.toCharArray()) {
                    data.add(new ValuePairs<>(ComponentCharacter.text(c), characterData));
                }
            }
        }
        List<ValuePairs<ComponentCharacter, CharacterData>> result = new ArrayList<>(data.size());
        StringBuilder resultSb = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        int sbIndex = 0;
        for (int i = 0; i < data.size(); i++) {
            ValuePairs<ComponentCharacter, CharacterData> element = data.get(i);
            ComponentCharacter character = element.getFirst();
            OptionalInt optChar = character.getCharForShaper();
            if (optChar.isPresent()) {
                sb.append((char) optChar.getAsInt());
            } else {
                if (sb.length() > 0) {
                    String shaped = shaper.apply(sb.toString());
                    for (int u = 0; u < shaped.length(); u++) {
                        int index = u + sbIndex;
                        char c = shaped.charAt(u);
                        resultSb.append(c);
                        result.add(new ValuePairs<>(ComponentCharacter.text(c), data.get(index).getSecond()));
                    }
                    sb = new StringBuilder();
                }
                sbIndex = i + 1;
                resultSb.append('\0');
                result.add(new ValuePairs<>(character, element.getSecond()));
            }
        }
        if (sb.length() > 0) {
            String shaped = shaper.apply(sb.toString());
            for (int u = 0; u < shaped.length(); u++) {
                int index = u + sbIndex;
                char c = shaped.charAt(u);
                resultSb.append(c);
                result.add(new ValuePairs<>(ComponentCharacter.text(c), data.get(index).getSecond()));
            }
        }
        return new ValuePairs<>(resultSb.toString(), result);
    }

    public static Component toComponent(List<ValuePairs<ComponentCharacter, CharacterData>> dataList) {
        List<Component> components = new ArrayList<>(dataList.size());
        for (ValuePairs<ComponentCharacter, CharacterData> data : dataList) {
            ComponentCharacter character = data.getFirst();
            CharacterData characterData = data.getSecond();
            Component component;
            if (character instanceof ComponentCharacter.ComponentCharacterText) {
                component = Component.text(((ComponentCharacter.ComponentCharacterText) character).getText());
            } else if (character instanceof ComponentCharacter.ComponentCharacterObject) {
                component = Component.object(((ComponentCharacter.ComponentCharacterObject) character).getObjectContents());
            } else {
                throw new IllegalStateException("Unknown ComponentCharacter type " + character.getClass());
            }
            component = component.font(characterData.getFont()).color(TextColor.color(characterData.getColor()));
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

}
