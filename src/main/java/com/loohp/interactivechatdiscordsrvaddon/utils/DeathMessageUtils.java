package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import org.bukkit.entity.Player;

import com.loohp.interactivechat.utils.ChatComponentType;
import com.loohp.interactivechat.utils.NMSUtils;

import net.kyori.adventure.text.Component;

public class DeathMessageUtils {
	
	private static Class<?> craftPlayerClass;
	private static Class<?> nmsEntityPlayerClass;
	private static Class<?> nmsCombatTrackerClass;
	private static Class<?> nmsIChatBaseComponentClass;
	private static Method getNmsEntityPlayerMethod;
	private static Field nmsCombatTrackerField;
	private static Method getDeathMessageMethod;
	
	static {
		try {
			craftPlayerClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.", "entity.CraftPlayer");
			nmsEntityPlayerClass = NMSUtils.getNMSClass("net.minecraft.server.", "EntityPlayer");
			nmsCombatTrackerClass = NMSUtils.getNMSClass("net.minecraft.server.", "CombatTracker");
			nmsIChatBaseComponentClass = NMSUtils.getNMSClass("net.minecraft.server.", "IChatBaseComponent");
			getNmsEntityPlayerMethod = craftPlayerClass.getMethod("getHandle");
			nmsCombatTrackerField = nmsEntityPlayerClass.getField("combatTracker");
			getDeathMessageMethod = Stream.of(nmsCombatTrackerClass.getMethods()).filter(each -> each.getReturnType().equals(nmsIChatBaseComponentClass)).findFirst().get();
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	public static Component getDeathMessage(Player player) {
		try {
			Object craftPlayerObject = craftPlayerClass.cast(player);
			Object nmsEntityPlayerObject = getNmsEntityPlayerMethod.invoke(craftPlayerObject);
			Object nmsCombatTrackerObject = nmsCombatTrackerField.get(nmsEntityPlayerObject);
			Object nsmIChatBaseComponentObject = getDeathMessageMethod.invoke(nmsCombatTrackerObject);
			return ChatComponentType.IChatBaseComponent.convertFrom(nsmIChatBaseComponentObject);
		} catch (Throwable e) {
			return Component.text("");
		}
	}

}
