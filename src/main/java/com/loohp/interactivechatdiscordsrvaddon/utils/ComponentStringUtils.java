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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TextComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TextReplacementConfig;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TranslatableComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent.ShowItem;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration.State;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.libs.org.apache.commons.lang3.RandomStringUtils;
import com.loohp.interactivechat.utils.ComponentCompacting;
import com.loohp.interactivechat.utils.ComponentFlattening;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.ItemNBTUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.fonts.FontProvider;
import it.unimi.dsi.fastutil.ints.IntList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class ComponentStringUtils {

    public static List<Component> applyWordWrap(Component component, Function<String, String> translateFunction, int lineLengthLimit, ToIntFunction<CharacterLengthProviderData> characterLengthProvider) {
        List<Component> result = new ArrayList<>();
        int x = 0;
        List<Component> child = ComponentFlattening.flatten(component).children();
        Component currentLine = Component.empty();
        for (Component each : child) {
            Key font = each.font();
            if (font == null) {
                font = Key.key("minecraft:default");
            }
            List<TextDecoration> decorations = each.decorations().entrySet().stream().filter(entry -> entry.getValue().equals(State.TRUE)).map(entry -> entry.getKey()).collect(Collectors.toList());
            if (each instanceof TextComponent) {
                TextComponent textComponent = (TextComponent) each;
                String[] sections = textComponent.content().split(" ", -1);
                int u = 0;
                for (String section : sections) {
                    u++;
                    if (u < sections.length) {
                        section += " ";
                    }
                    int length = 0;
                    for (int i = 0; i < section.length(); ) {
                        String c = new String(Character.toChars(section.codePointAt(i)));
                        i += c.length();
                        length += characterLengthProvider.applyAsInt(new CharacterLengthProviderData(c, font, decorations));
                    }
                    if (x + length > lineLengthLimit) {
                        if (!PlainTextComponentSerializer.plainText().serialize(currentLine).isEmpty()) {
                            result.add(currentLine);
                        }
                        currentLine = textComponent.content(section);
                        x = length;
                        while (length > lineLengthLimit) {
                            StringBuilder sb = new StringBuilder();
                            int subLength = 0;
                            for (int i = 0; i < section.length(); ) {
                                String c = new String(Character.toChars(section.codePointAt(i)));
                                i += c.length();
                                subLength += characterLengthProvider.applyAsInt(new CharacterLengthProviderData(c, font, decorations));
                                if (subLength > lineLengthLimit) {
                                    result.add(textComponent.content(sb.toString()));
                                    section = section.substring(i);
                                    currentLine = textComponent.content(section);
                                    length -= subLength;
                                    x = length;
                                    break;
                                }
                                sb.append(c);
                            }
                        }
                    } else {
                        currentLine = currentLine.append(textComponent.content(section));
                        x += length;
                    }
                }
            } else if (each instanceof TranslatableComponent) {
                TranslatableComponent translatableComponent = (TranslatableComponent) each;
                TextComponent textComponent = convertSingleTranslatable(translatableComponent, translateFunction);
                String content = textComponent.content();
                int length = 0;
                for (int i = 0; i < content.length(); ) {
                    int codePoint = content.codePointAt(i);
                    String c = new String(Character.toChars(codePoint));
                    i += c.length();
                    length += characterLengthProvider.applyAsInt(new CharacterLengthProviderData(c, font, decorations));
                }
                if (x + length > lineLengthLimit) {
                    if (!PlainTextComponentSerializer.plainText().serialize(currentLine).isEmpty()) {
                        result.add(currentLine);
                    }
                    currentLine = translatableComponent;
                    x = 0;
                } else {
                    currentLine = currentLine.append(translatableComponent);
                    x += length;
                }
            } else {
                String content = PlainTextComponentSerializer.plainText().serialize(each);
                int length = 0;
                for (int i = 0; i < content.length(); ) {
                    int codePoint = content.codePointAt(i);
                    String c = new String(Character.toChars(codePoint));
                    i += c.length();
                    length += characterLengthProvider.applyAsInt(new CharacterLengthProviderData(c, font, decorations));
                }
                if (x + length > lineLengthLimit) {
                    if (!PlainTextComponentSerializer.plainText().serialize(currentLine).isEmpty()) {
                        result.add(currentLine);
                    }
                    currentLine = each;
                    x = 0;
                } else {
                    currentLine = currentLine.append(each);
                    x += length;
                }
            }
        }
        if (!PlainTextComponentSerializer.plainText().serialize(currentLine).isEmpty()) {
            result.add(currentLine);
        }
        return result;
    }

    public static Component convertTranslatables(Component component, Function<String, String> translateFunction) {
        component = ComponentFlattening.flatten(component);
        List<Component> children = new ArrayList<>(component.children());
        for (int i = 0; i < children.size(); i++) {
            Component current = children.get(i);
            if (current instanceof TranslatableComponent) {
                TranslatableComponent trans = (TranslatableComponent) current;
                Component translated = Component.text(translateFunction.apply(trans.key())).style(trans.style());
                for (Component arg : trans.args()) {
                    translated = translated.replaceText(TextReplacementConfig.builder().match("%+(?:[0-9]+\\$)?(?:s|d)").replacement(convertTranslatables(arg, translateFunction)).once().build());
                }
                children.set(i, translated);
            }
        }
        return ComponentCompacting.optimize(component.children(children));
    }

    public static TextComponent convertSingleTranslatable(TranslatableComponent component, Function<String, String> translateFunction) {
        TextComponent translated = Component.text(translateFunction.apply(component.key())).style(component.style());
        for (Component arg : component.args()) {
            translated = (TextComponent) translated.replaceText(TextReplacementConfig.builder().match("%+(?:[0-9]+\\$)?(?:s|d)").replacement(convertTranslatables(arg, translateFunction)).once().build());
        }
        return translated;
    }

    public static String toMagic(String str) {
        return toMagic(null, str);
    }

    public static String toMagic(FontProvider provider, String str) {
        if (provider == null) {
            return RandomStringUtils.random(str.length());
        }
        IntList list = provider.getDisplayableCharacters();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            sb.append(Character.toChars(list.getInt(ThreadLocalRandom.current().nextInt(list.size()))));
        }
        return sb.toString();
    }

    public static String stripColorAndConvertMagic(String str) {
        return stripColorAndConvertMagic(null, str);
    }

    public static String stripColorAndConvertMagic(FontProvider provider, String str) {
        StringBuilder sb = new StringBuilder();
        str = str.replaceAll(ChatColor.COLOR_CHAR + "[l-o]", "").replaceAll(ChatColor.COLOR_CHAR + "[0-9a-fxA-F]", ChatColor.COLOR_CHAR + "r");
        boolean magic = false;
        for (int i = 0; i < str.length(); i++) {
            String current = str.substring(i, i + 1);
            if (current.equals(ChatColor.COLOR_CHAR + "")) {
                String next = str.substring(i + 1, i + 2);
                if (next.equalsIgnoreCase("r")) {
                    magic = false;
                    i++;
                } else if (next.equalsIgnoreCase("k")) {
                    magic = true;
                    i++;
                } else {
                    sb.append(magic ? toMagic(provider, current) : current);
                }
            } else {
                sb.append(magic ? toMagic(provider, current) : current);
            }
        }
        return sb.toString();
    }

    @SuppressWarnings("deprecation")
    public static ItemStack extractItemStack(Component component) {
        component = ComponentFlattening.flatten(component);
        List<Component> children = new ArrayList<>(component.children());
        for (int i = 0; i < children.size(); i++) {
            Component child = children.get(i);
            HoverEvent<?> hoverEvent = child.hoverEvent();
            if (hoverEvent != null && hoverEvent.action().equals(HoverEvent.Action.SHOW_ITEM)) {
                ShowItem showItem = (ShowItem) hoverEvent.value();
                Key key = showItem.item();
                int count = showItem.count();
                String simpleNbt = "{id:\"" + key.asString() + "\", Count: " + count + "b}";
                String longNbt = showItem.nbt() == null ? null : showItem.nbt().string();
                ItemStack itemstack = null;
                try {
                    itemstack = ItemNBTUtils.getItemFromNBTJson(simpleNbt);
                } catch (Throwable e) {
                }
                if (longNbt != null) {
                    try {
                        itemstack = Bukkit.getUnsafe().modifyItemStack(itemstack, longNbt);
                    } catch (Throwable e) {
                    }
                }
                if (itemstack != null) {
                    return itemstack;
                }
            }
            if (child instanceof TranslatableComponent) {
                TranslatableComponent trans = (TranslatableComponent) child;
                List<Component> withs = new ArrayList<>(trans.args());
                for (Component with : withs) {
                    ItemStack itemstack = extractItemStack(with);
                    if (itemstack != null) {
                        return itemstack;
                    }
                }
                trans = trans.args(withs);
                children.set(i, trans);
            }
        }
        return null;
    }

    public static github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component toDiscordSRVComponent(Component component) {
        return github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().deserialize(InteractiveChatComponentSerializer.gson().serialize(component));
    }

    public static Component toRegularComponent(github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component component) {
        return InteractiveChatComponentSerializer.gson().deserialize(github.scarsz.discordsrv.dependencies.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(component));
    }

    public static class CharacterLengthProviderData {

        private String character;
        private Key font;
        private List<TextDecoration> decorations;

        public CharacterLengthProviderData(String character, Key font, List<TextDecoration> decorations) {
            this.character = character;
            this.font = font;
            this.decorations = decorations;
        }

        public String getCharacter() {
            return character;
        }

        public Key getFont() {
            return font;
        }

        public List<TextDecoration> getDecorations() {
            return decorations;
        }

    }

}
