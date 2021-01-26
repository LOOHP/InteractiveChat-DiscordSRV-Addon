package com.loohp.interactivechatdiscordsrvaddon.Utils;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.RandomStringUtils;

import com.google.common.base.CharMatcher;
import com.loohp.interactivechat.Utils.CustomStringUtils;
import com.loohp.interactivechat.Utils.LanguageUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class ComponentStringUtils {
	
	public static String toLegacyString(BaseComponent baseComponent) {
		StringBuilder sb = new StringBuilder();
		for (BaseComponent each : CustomStringUtils.loadExtras(baseComponent)) {
			if (each instanceof TranslatableComponent) {
				TranslatableComponent trans = (TranslatableComponent) each;
				String translated = LanguageUtils.getTranslation(trans.getTranslate(), InteractiveChatDiscordSrvAddon.plugin.language);
				for (BaseComponent with : trans.getWith()) {
					translated = translated.replaceFirst("%s", toLegacyString(with));
				}
				sb.append(translated);
			} else {
				sb.append(each.toLegacyText());
			}
		}
		
		return sb.toString();
	}
	
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

}
