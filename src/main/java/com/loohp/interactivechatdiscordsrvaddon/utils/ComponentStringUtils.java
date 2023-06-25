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

import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.BlockNBTComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.EntityNBTComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.KeybindComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.ScoreComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.StorageNBTComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TextComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TranslatableComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent.ShowItem;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration.State;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.libs.org.apache.commons.lang3.RandomStringUtils;
import com.loohp.interactivechat.objectholders.LegacyIdKey;
import com.loohp.interactivechat.utils.ComponentCompacting;
import com.loohp.interactivechat.utils.ComponentFlattening;
import com.loohp.interactivechat.utils.ComponentModernizing;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.ItemNBTUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.fonts.FontProvider;
import it.unimi.dsi.fastutil.ints.IntList;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentStringUtils {

    private static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public static List<Component> applyWordWrap(Component component, UnaryOperator<String> translateFunction, int lineLengthLimit, ToIntFunction<CharacterLengthProviderData> characterLengthProvider) {
        List<Component> result = new ArrayList<>();
        int x = 0;
        List<Component> child = ComponentFlattening.flatten(component).children();
        Component currentLine = Component.empty();
        boolean nullCurrentLine = true;
        for (Component each : child) {
            Key font = each.font();
            List<TextDecoration> decorations = each.decorations().entrySet().stream().filter(entry -> entry.getValue().equals(State.TRUE)).map(entry -> entry.getKey()).collect(Collectors.toList());
            if (each instanceof TextComponent) {
                TextComponent textComponent = (TextComponent) each;
                String content = textComponent.content();
                String[] parts = content.split("\\R", -1);
                List<TextComponent> split = Arrays.stream(parts).map(p -> textComponent.content(p)).collect(Collectors.toList());
                int j = 0;
                for (TextComponent part : split) {
                    j++;
                    if (j >= split.size() && part.content().isEmpty()) {
                        continue;
                    }
                    String[] sections = part.content().split(" ", -1);
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
                            if (!nullCurrentLine) {
                                result.add(currentLine);
                            }
                            currentLine = textComponent.content(section);
                            nullCurrentLine = false;
                            x = length;
                            while (length > lineLengthLimit) {
                                StringBuilder sb = new StringBuilder();
                                int subLength = 0;
                                for (int i = 0; i < section.length(); ) {
                                    String c = new String(Character.toChars(section.codePointAt(i)));
                                    i += c.length();
                                    int currentCharLength = characterLengthProvider.applyAsInt(new CharacterLengthProviderData(c, font, decorations));
                                    subLength += currentCharLength;
                                    if (subLength > lineLengthLimit) {
                                        result.add(textComponent.content(sb.toString()));
                                        section = section.substring(i - c.length());
                                        length -= (subLength - currentCharLength);
                                        if (section.isEmpty()) {
                                            nullCurrentLine = true;
                                            currentLine = Component.empty();
                                            x = 0;
                                        } else {
                                            nullCurrentLine = false;
                                            currentLine = textComponent.content(section);
                                            x = length;
                                        }
                                        break;
                                    }
                                    sb.append(c);
                                }
                            }
                        } else {
                            currentLine = currentLine.append(textComponent.content(section));
                            nullCurrentLine = false;
                            x += length;
                        }
                    }
                    if (j < split.size()) {
                        if (nullCurrentLine) {
                            result.add(Component.empty());
                        } else {
                            result.add(currentLine);
                        }
                        nullCurrentLine = true;
                        currentLine = Component.empty();
                        x = 0;
                    }
                }
            } else if (each instanceof TranslatableComponent) {
                TranslatableComponent translatableComponent = (TranslatableComponent) each;
                Component textComponent = convertSingleTranslatable(translatableComponent, translateFunction);
                String content = PlainTextComponentSerializer.plainText().serialize(textComponent);
                int length = 0;
                for (int i = 0; i < content.length(); ) {
                    int codePoint = content.codePointAt(i);
                    String c = new String(Character.toChars(codePoint));
                    i += c.length();
                    length += characterLengthProvider.applyAsInt(new CharacterLengthProviderData(c, font, decorations));
                }
                if (x + length > lineLengthLimit) {
                    if (!nullCurrentLine) {
                        result.add(currentLine);
                    }
                    currentLine = translatableComponent;
                    nullCurrentLine = false;
                    x = 0;
                } else {
                    currentLine = currentLine.append(translatableComponent);
                    nullCurrentLine = false;
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
                    if (!nullCurrentLine) {
                        result.add(currentLine);
                    }
                    nullCurrentLine = false;
                    currentLine = each;
                    x = 0;
                } else {
                    currentLine = currentLine.append(each);
                    nullCurrentLine = false;
                    x += length;
                }
            }
        }
        if (!nullCurrentLine) {
            result.add(currentLine);
        }
        return result;
    }

    public static Component resolve(Component component, UnaryOperator<String> translateFunction) {
        component = ComponentFlattening.flatten(component);
        List<Component> children = new ArrayList<>(component.children());
        for (int i = 0; i < children.size(); i++) {
            Component current = children.get(i);
            if (current instanceof TranslatableComponent) {
                TranslatableComponent translatable = (TranslatableComponent) current;
                current = convertSingleTranslatable(translatable, translateFunction);
            } else if (current instanceof KeybindComponent) {
                KeybindComponent keybinding = (KeybindComponent) current;
                current = convertSingleTranslatable(Component.translatable(keybinding.keybind()), translateFunction);
            } else if (current instanceof ScoreComponent) {
                ScoreComponent score = (ScoreComponent) current;
                current = Component.text("{" + score.name() + ": " + score.objective() + "}");
            } else if (current instanceof BlockNBTComponent) {
                BlockNBTComponent nbt = (BlockNBTComponent) current;
                current = Component.text(nbt.nbtPath() + "@[" + nbt.pos().asString() + "]");
            } else if (current instanceof EntityNBTComponent) {
                EntityNBTComponent nbt = (EntityNBTComponent) current;
                current = Component.text(nbt.nbtPath() + "@[" + nbt.selector() + "]");
            } else if (current instanceof StorageNBTComponent) {
                StorageNBTComponent nbt = (StorageNBTComponent) current;
                current = Component.text(nbt.nbtPath() + "@[" + nbt.storage().asString() + "]");
            }
            children.set(i, current);
        }
        return ComponentCompacting.optimize(component.children(children));
    }

    public static Component convertSingleTranslatable(TranslatableComponent component, UnaryOperator<String> translateFunction) {
        String translation = translateFunction.apply(component.key());
        List<Component> args = component.args();

        List<Component> parts = new ArrayList<>();
        Matcher matcher = ARG_FORMAT.matcher(translation);
        try {
            int i = 0;
            int j = 0;
            while (matcher.find(j)) {
                String string;
                int k = matcher.start();
                int l = matcher.end();
                if (k > j) {
                    string = translation.substring(j, k);
                    if (string.indexOf(37) != -1) {
                        throw new IllegalArgumentException();
                    }
                    parts.add(Component.text(string));
                }
                string = matcher.group(2);
                String string2 = translation.substring(k, l);
                if ("%".equals(string) && "%%".equals(string2)) {
                    parts.add(Component.text("%"));
                } else if ("s".equals(string) || "d".equals(string)) {
                    int m;
                    String string3 = matcher.group(1);
                    int n = m = string3 != null ? Integer.parseInt(string3) - 1 : i++;
                    if (m < args.size()) {
                        parts.add(resolve(args.get(m), translateFunction));
                    }
                } else {
                    throw new RuntimeException("Unsupported format: '" + string2 + "'");
                }
                j = l;
            }
            if (j < translation.length()) {
                String string4 = translation.substring(j);
                if (string4.indexOf(37) != -1) {
                    throw new IllegalArgumentException();
                }
                parts.add(Component.text(string4));
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new RuntimeException(illegalArgumentException);
        }

        return ComponentModernizing.modernize(Component.empty().style(component.style()).children(parts));
    }

    public static String convertFormattedString(String translation, Object... args) {
        List<String> parts = new ArrayList<>();
        Matcher matcher = ARG_FORMAT.matcher(translation);
        try {
            int i = 0;
            int j = 0;
            while (matcher.find(j)) {
                String string;
                int k = matcher.start();
                int l = matcher.end();
                if (k > j) {
                    string = translation.substring(j, k);
                    if (string.indexOf(37) != -1) {
                        throw new IllegalArgumentException();
                    }
                    parts.add(string);
                }
                string = matcher.group(2);
                String string2 = translation.substring(k, l);
                if ("%".equals(string) && "%%".equals(string2)) {
                    parts.add("%");
                } else if ("s".equals(string) || "d".equals(string)) {
                    int m;
                    String string3 = matcher.group(1);
                    int n = m = string3 != null ? Integer.parseInt(string3) - 1 : i++;
                    if (m < args.length) {
                        parts.add(args[m].toString());
                    }
                } else {
                    throw new RuntimeException("Unsupported format: '" + string2 + "'");
                }
                j = l;
            }
            if (j < translation.length()) {
                String string4 = translation.substring(j);
                if (string4.indexOf(37) != -1) {
                    throw new IllegalArgumentException();
                }
                parts.add(string4);
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new RuntimeException(illegalArgumentException);
        }
        return String.join("", parts);
    }

    public static Component join(Component deliminator, Component... components) {
        return join(Component.empty(), deliminator, components);
    }

    public static Component join(Component deliminator, List<? extends Component> components) {
        return join(Component.empty(), deliminator, components);
    }

    public static Component join(Component parent, Component deliminator, Component... components) {
        return join(parent, deliminator, Arrays.stream(components));
    }

    public static Component join(Component parent, Component deliminator, Collection<? extends Component> components) {
        if (components.size() == 0) {
            return parent;
        }
        if (components.size() == 1) {
            return parent.append(components.iterator().next());
        }
        return join(parent, deliminator, components.iterator());
    }

    public static Component join(Component parent, Component deliminator, Stream<? extends Component> stream) {
        return join(parent, deliminator, stream.iterator());
    }

    public static Component join(Component parent, Component deliminator, Iterator<? extends Component> itr) {
        if (!itr.hasNext()) {
            return parent;
        }
        List<Component> children = new ArrayList<>(parent.children());
        children.add(itr.next());
        while (itr.hasNext()) {
            children.add(deliminator);
            children.add(itr.next());
        }
        return parent.children(children);
    }

    public static String toMagic(String str) {
        return toMagic(null, str);
    }

    public static String toMagic(FontProvider provider, String str) {
        if (provider == null) {
            return RandomStringUtils.random(str.length());
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            String currentChar = str.substring(i, i + 1);
            int width = provider.forCharacter(currentChar).getCharacterWidth(currentChar);
            IntList list = provider.getDisplayableCharactersByWidth().get(width);
            sb.append(new String(Character.toChars(list.getInt(ThreadLocalRandom.current().nextInt(list.size())))));
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
                ItemStack itemstack = null;
                LegacyIdKey legacyId = InteractiveChatComponentSerializer.interactiveChatKeyToLegacyId(key);
                if (legacyId == null) {
                    String simpleNbt = "{id:\"" + key.asString() + "\", Count: " + count + "b}";
                    try {
                        itemstack = ItemNBTUtils.getItemFromNBTJson(simpleNbt);
                    } catch (Throwable ignored) {
                    }
                } else {
                    Optional<XMaterial> optXMaterial;
                    if (legacyId.hasByteId()) {
                        optXMaterial = XMaterial.matchXMaterial(legacyId.getByteId(), legacyId.isDamageDataValue() ? (byte) legacyId.getDamage() : 0);
                        if (optXMaterial.isPresent()) {
                            itemstack = optXMaterial.get().parseItem();
                        }
                    } else {
                        String materialId = legacyId.getStringId();
                        if (materialId.contains(":")) {
                            materialId = materialId.substring(materialId.indexOf(":") + 1);
                        }
                        optXMaterial = XMaterial.matchXMaterial(materialId.toUpperCase());
                        if (optXMaterial.isPresent()) {
                            itemstack = optXMaterial.get().parseItem();
                            itemstack.setDurability(legacyId.getDamage());
                        }
                    }
                }
                String longNbt = showItem.nbt() == null ? null : showItem.nbt().string();
                if (itemstack != null && longNbt != null) {
                    try {
                        itemstack = Bukkit.getUnsafe().modifyItemStack(itemstack, longNbt);
                    } catch (Throwable ignored) {
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
