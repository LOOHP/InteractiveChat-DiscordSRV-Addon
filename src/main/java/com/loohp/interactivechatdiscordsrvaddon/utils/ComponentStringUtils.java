package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TextReplacementConfig;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TranslatableComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent.ShowItem;
import com.loohp.interactivechat.libs.org.apache.commons.lang3.RandomStringUtils;
import com.loohp.interactivechat.utils.ComponentCompacting;
import com.loohp.interactivechat.utils.ComponentFlattening;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.ItemNBTUtils;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.fonts.FontProvider;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ComponentStringUtils {

    public static Component convertTranslatables(Component component, String language) {
        component = ComponentFlattening.flatten(component);
        List<Component> children = new ArrayList<>(component.children());
        for (int i = 0; i < children.size(); i++) {
            Component current = children.get(i);
            if (current instanceof TranslatableComponent) {
                TranslatableComponent trans = (TranslatableComponent) current;
                Component translated = Component.text(LanguageUtils.getTranslation(trans.key(), language)).style(trans.style());
                for (Component arg : trans.args()) {
                    translated = translated.replaceText(TextReplacementConfig.builder().matchLiteral("%s").replacement(convertTranslatables(arg, language)).once().build());
                }
                children.set(i, translated);
            }
        }
        return ComponentCompacting.optimize(component.children(children));
    }

    public static String toMagic(String str) {
        return toMagic(null, str);
    }

    public static String toMagic(FontProvider provider, String str) {
        if (provider == null) {
            return RandomStringUtils.random(str.length());
        }
        List<String> list = provider.getDisplayableCharacters();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            sb.append(list.get(ThreadLocalRandom.current().nextInt(list.size())));
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
                for (int u = 0; u < withs.size(); u++) {
                    Component with = withs.get(u);
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

}
