package com.loohp.interactivechatdiscordsrvaddon.Utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.potion.PotionType;

public class PotionUtils {
	
	private static Map<String, Color> potionColor = new HashMap<>();
	
	static {
		potionColor.put("AWKWARD", Color.decode("#385dc6"));
		potionColor.put("FIRE_RESISTANCE", Color.decode("#e49a3a"));
		potionColor.put("INSTANT_DAMAGE", Color.decode("#430a09"));
		potionColor.put("INSTANT_HEAL", Color.decode("#f82423"));
		potionColor.put("INVISIBILITY", Color.decode("#7f8392"));
		potionColor.put("JUMP", Color.decode("#00ff2e"));
		potionColor.put("LUCK", Color.decode("#329700"));
		potionColor.put("MUNDANE", Color.decode("#385dc6"));
		potionColor.put("NIGHT_VISION", Color.decode("#1f1f23"));
		potionColor.put("POISON", Color.decode("#4e9331"));
		potionColor.put("REGEN", Color.decode("#cd5cab"));
		potionColor.put("SLOW_FALLING", Color.decode("#daccc6"));
		potionColor.put("SLOWNESS", Color.decode("#5a6c81"));
		potionColor.put("SPEED", Color.decode("#7cafc6"));
		potionColor.put("STRENGTH", Color.decode("#932423"));
		potionColor.put("THICK", Color.decode("#385dc6"));
		potionColor.put("TURTLE_MASTER", Color.decode("#8f6aaa"));
		potionColor.put("UNCRAFTABLE", Color.decode("#ff5bde"));
		potionColor.put("WATER", Color.decode("#385dc6"));
		potionColor.put("WATER_BREATHING", Color.decode("#2e5299"));
		potionColor.put("WEAKNESS", Color.decode("#484d48"));
	}
	
	public static Color getPotionBaseColor(PotionType type) {
		Color color = potionColor.get(type.name().toUpperCase());
		return color == null ? Color.white : color;
	}

}
