package com.loohp.interactivechatdiscordsrvaddon.Registies;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.loohp.interactivechat.Utils.CustomStringUtils;

public class DiscordDataRegistry {

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
