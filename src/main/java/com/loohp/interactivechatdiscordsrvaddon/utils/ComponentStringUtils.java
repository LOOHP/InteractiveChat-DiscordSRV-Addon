package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.CharMatcher;
import com.loohp.interactivechat.utils.ComponentFlattening;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.ItemNBTUtils;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;
import net.md_5.bungee.api.ChatColor;

public class ComponentStringUtils {
	
	public static String toMagic(String str) {
		Random random = ThreadLocalRandom.current();
		StringBuilder sb = new StringBuilder();
		CharMatcher matcher = CharMatcher.ascii();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (matcher.matches(c)) {
				sb.append((char) (random.nextInt(93) + 33));
			} else {
				sb.append(RandomStringUtils.random(1));
			}
		}
		return sb.toString();
	}
	
	public static String stripColorAndConvertMagic(String str) {
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
					sb.append(magic ? toMagic(current) : current);
				}
			} else {
				sb.append(magic ? toMagic(current) : current);
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
				} catch (Throwable e) {}
				if (longNbt != null) {
					try {
						itemstack = Bukkit.getUnsafe().modifyItemStack(itemstack, longNbt);
					} catch (Throwable e) {}
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
