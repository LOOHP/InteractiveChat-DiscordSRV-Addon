package com.loohp.interactivechatdiscordsrvaddon.Registies;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.loohp.interactivechat.Utils.CustomStringUtils;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ColorUtils;

public class DiscordDataRegistry {

	public static final Color DISCORD_HOVER_COLOR = ColorUtils.hex2Rgb("#1F083A"); 
	private static Set<String> markdownChars;
	private static String markdownPattern;

	static {
		markdownChars = new HashSet<>();
		markdownChars.add("\\");
		markdownChars.add("`");
		markdownChars.add("*");
		markdownChars.add("_");
		markdownChars.add("{");
		markdownChars.add("}");
		markdownChars.add("[");
		markdownChars.add("]");
		markdownChars.add("(");
		markdownChars.add(")");
		markdownChars.add("#");
		markdownChars.add("+");
		markdownChars.add("-");
		markdownChars.add(".");
		markdownChars.add("!");
		markdownChars.add(">");
		markdownChars.add("~");
		markdownChars.add(":");
		
		markdownPattern = "([" + CustomStringUtils.escapeMetaCharacters(markdownChars.stream().collect(Collectors.joining())) + "])";
	}
	
	public static Set<String> getMarkdownSpecialChars() {
		return Collections.unmodifiableSet(markdownChars);
	}
	
	public static String getMarkdownSpecialPattern() {
		return markdownPattern;
	}

}
