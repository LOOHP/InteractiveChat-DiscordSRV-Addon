package com.loohp.interactivechatdiscordsrvaddon.Utils;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public class PotionUtils {
	
	private static Class<?> craftItemStackClass;
	private static Class<?> nmsItemStackClass;
	private static Method asNMSCopyMethod;
	private static Method nmsItemHasTagMethod;
	private static Method nmsItemHasGetMethod;
	private static Class<?> nmsNbtTagCompoundClass;
	private static Method nmsNbtTagGetStringMethod;
	private static Class<?> nmsPotionRegistryClass;
	private static Method nmsPotionRegistryA1Method;
	private static Method nmsPotionRegistryA2Method;
	private static Class<?> craftPotionUtilClass;
	private static Class<?> nmsMobEffectClass;
	private static Method craftPotionUtilToBukkitMethod;

	private static Map<String, Color> potionColor = new HashMap<>();

	static {
		try {
			craftItemStackClass = getNMSClass("org.bukkit.craftbukkit.", "inventory.CraftItemStack");
			nmsItemStackClass = getNMSClass("net.minecraft.server.", "ItemStack");
			asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
			nmsItemHasTagMethod = nmsItemStackClass.getMethod("hasTag");
			nmsItemHasGetMethod = nmsItemStackClass.getMethod("getTag");
			nmsNbtTagCompoundClass = getNMSClass("net.minecraft.server.", "NBTTagCompound");
			nmsNbtTagGetStringMethod = nmsNbtTagCompoundClass.getMethod("getString", String.class);
			nmsPotionRegistryClass = getNMSClass("net.minecraft.server.", "PotionRegistry");
			nmsPotionRegistryA1Method = nmsPotionRegistryClass.getMethod("a", String.class);
			nmsPotionRegistryA2Method = nmsPotionRegistryClass.getMethod("a");
			craftPotionUtilClass = getNMSClass("org.bukkit.craftbukkit.", "potion.CraftPotionUtil");
			nmsMobEffectClass = getNMSClass("net.minecraft.server.", "MobEffect");
			craftPotionUtilToBukkitMethod = craftPotionUtilClass.getMethod("toBukkit", nmsMobEffectClass);
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
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
	
	private static Class<?> getNMSClass(String prefix, String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = prefix + version + nmsClassString;
        return Class.forName(name);
    }

	public static Color getPotionBaseColor(PotionType type) {
		Color color = potionColor.get(type.name().toUpperCase());
		return color == null ? Color.white : color;
	}

	public static List<PotionEffect> getBasePotionEffect(ItemStack potion) throws Exception {
		Object nmsStack = asNMSCopyMethod.invoke(null, potion);
		if (!(Boolean) nmsItemHasTagMethod.invoke(nmsStack)) {
			return null;
		}
		String pName = (String) nmsNbtTagGetStringMethod.invoke(nmsItemHasGetMethod.invoke(nmsStack), "Potion");
		if (pName == null) {
			return null;
		}
		String[] split = pName.split(":");
		if (split.length == 2) {
			pName = split[1];
		}
		Object reg = nmsPotionRegistryA1Method.invoke(null, pName);
		if (reg == null) {
			return null;
		}
		List<PotionEffect> effects = new ArrayList<>();
		for (Object me : (List<?>) nmsPotionRegistryA2Method.invoke(reg)) {
			effects.add((PotionEffect) craftPotionUtilToBukkitMethod.invoke(null, me));
		}
		return effects;
	}

}
