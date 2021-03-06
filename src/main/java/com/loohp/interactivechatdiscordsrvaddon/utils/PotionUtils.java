package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.loohp.interactivechat.utils.NMSUtils;

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
	
	private static final Set<String> POSITIVE_EFFECTS = new HashSet<>();  
	
	static {
		try {
			craftItemStackClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.inventory.CraftItemStack");
			nmsItemStackClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ItemStack", "net.minecraft.world.item.ItemStack");
			asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
			nmsItemHasTagMethod = nmsItemStackClass.getMethod("hasTag");
			nmsItemHasGetMethod = nmsItemStackClass.getMethod("getTag");
			nmsNbtTagCompoundClass = NMSUtils.getNMSClass("net.minecraft.server.%s.NBTTagCompound", "net.minecraft.nbt.NBTTagCompound");
			nmsNbtTagGetStringMethod = nmsNbtTagCompoundClass.getMethod("getString", String.class);
			nmsPotionRegistryClass = NMSUtils.getNMSClass("net.minecraft.server.%s.PotionRegistry", "net.minecraft.world.item.alchemy.PotionRegistry");
			nmsPotionRegistryA1Method = nmsPotionRegistryClass.getMethod("a", String.class);
			nmsPotionRegistryA2Method = nmsPotionRegistryClass.getMethod("a");
			craftPotionUtilClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.potion.CraftPotionUtil");
			nmsMobEffectClass = NMSUtils.getNMSClass("net.minecraft.server.%s.MobEffect", "net.minecraft.world.effect.MobEffect");
			craftPotionUtilToBukkitMethod = craftPotionUtilClass.getMethod("toBukkit", nmsMobEffectClass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		POSITIVE_EFFECTS.add("SPEED");
		POSITIVE_EFFECTS.add("FAST_DIGGING");
		POSITIVE_EFFECTS.add("INCREASE_DAMAGE");
		POSITIVE_EFFECTS.add("JUMP");
		POSITIVE_EFFECTS.add("REGENERATION");
		POSITIVE_EFFECTS.add("DAMAGE_RESISTANCE");
		POSITIVE_EFFECTS.add("FIRE_RESISTANCE");
		POSITIVE_EFFECTS.add("WATER_BREATHING");
		POSITIVE_EFFECTS.add("INVISIBILITY");
		POSITIVE_EFFECTS.add("NIGHT_VISION");
		POSITIVE_EFFECTS.add("HEALTH_BOOST");
		POSITIVE_EFFECTS.add("ABSORPTION");
		POSITIVE_EFFECTS.add("SATURATION");
		POSITIVE_EFFECTS.add("LUCK");
		POSITIVE_EFFECTS.add("SLOW_FALLING");
		POSITIVE_EFFECTS.add("CONDUIT_POWER");
		POSITIVE_EFFECTS.add("DOLPHINS_GRACE");
		POSITIVE_EFFECTS.add("HERO_OF_THE_VILLAGE");
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
	
	public static boolean isPositive(PotionEffectType type) {
		return POSITIVE_EFFECTS.contains(type.getName());
	}

}
