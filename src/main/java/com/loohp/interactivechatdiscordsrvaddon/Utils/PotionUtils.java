package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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

	public static final Color WATER_COLOR = Color.decode("#385dc6");
	public static final Color UNCRAFTABLE_COLOR = Color.decode("#ff5bde");

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
	}
	
	private static Class<?> getNMSClass(String prefix, String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = prefix + version + nmsClassString;
        return Class.forName(name);
    }

	public static Color getPotionBaseColor(PotionType type) {
		PotionEffectType effect = type.getEffectType();
		if (effect == null) {
			if (type.equals(PotionType.UNCRAFTABLE)) {
				return UNCRAFTABLE_COLOR;
			} else {
				return WATER_COLOR;
			}
		} else {
			return new Color(effect.getColor().asRGB());
		}
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
