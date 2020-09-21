package com.loohp.interactivechatdiscordsrvaddon.Utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ChatColor;

public class ColorUtils {
	
	private static Map<ChatColor, Color> colors = new HashMap<>();
	
	static {
		colors.put(ChatColor.BLACK, new Color(0x000000));
	    colors.put(ChatColor.DARK_BLUE, new Color(0x0000AA));
	    colors.put(ChatColor.DARK_GREEN, new Color(0x00AA00));
	    colors.put(ChatColor.DARK_AQUA, new Color(0x00AAAA));
	    colors.put(ChatColor.DARK_RED, new Color(0xAA0000));
	    colors.put(ChatColor.DARK_PURPLE, new Color(0xAA00AA));
	    colors.put(ChatColor.GOLD, new Color(0xFFAA00));
	    colors.put(ChatColor.GRAY, new Color(0xAAAAAA));
	    colors.put(ChatColor.DARK_GRAY, new Color(0x555555));
	    colors.put(ChatColor.BLUE, new Color(0x05555FF));
	    colors.put(ChatColor.GREEN, new Color(0x55FF55));
	    colors.put(ChatColor.AQUA, new Color(0x55FFFF));
	    colors.put(ChatColor.RED, new Color(0xFF5555));
	    colors.put(ChatColor.LIGHT_PURPLE, new Color(0xFF55FF));
	    colors.put(ChatColor.YELLOW, new Color(0xFFFF55));
	    colors.put(ChatColor.WHITE, new Color(0xFFFFFF));
	}
	
	public static Color getColor(ChatColor chatcolor) {
		Color color = colors.get(chatcolor);
		return color == null ? Color.white : color;
	}

	public static Color hex2Rgb(String colorStr) {
		return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16),
				Integer.valueOf(colorStr.substring(5, 7), 16));
	}
	
	public static String rgb2Hex(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}
}
