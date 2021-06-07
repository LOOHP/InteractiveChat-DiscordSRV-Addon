package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.utils.MCVersion;

public class ItemStackUtils {
	
	private static Class<?> craftItemStackClass;
	private static Class<?> nmsItemStackClass;
	private static Method asBukkitCopyMethod;
	
	static {
		try {
			craftItemStackClass = getNMSClass("org.bukkit.craftbukkit.", "inventory.CraftItemStack");
			nmsItemStackClass = getNMSClass("net.minecraft.server.", "ItemStack");
			asBukkitCopyMethod = craftItemStackClass.getMethod("asBukkitCopy", nmsItemStackClass);
		} catch (ClassNotFoundException | SecurityException | NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	private static Class<?> getNMSClass(String prefix, String nmsClassString) throws ClassNotFoundException {	
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = prefix + version + nmsClassString;
        return Class.forName(name);
    }

	public static boolean isArmor(ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		}
		String typeNameString = itemStack.getType().name();
		return typeNameString.endsWith("_HELMET") || typeNameString.endsWith("_CHESTPLATE") || typeNameString.endsWith("_LEGGINGS") || typeNameString.endsWith("_BOOTS");
	}
	
	public static boolean isWearable(ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		}
		if (isArmor(itemStack)) {
			return true;
		}
		String typeNameString = itemStack.getType().name();
		if (typeNameString.equals("ELYTRA")) {
			return true;
		}
		if (typeNameString.contains("HEAD") || typeNameString.contains("SKULL")) {
			return true;
		}
		if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_13)) {
			return typeNameString.equals("CARVED_PUMPKIN");
		} else {
			return typeNameString.equals("PUMPKIN");
		}
	}
	
	public static ItemStack toBukkitCopy(Object handle) {
		try {
			return (ItemStack) asBukkitCopyMethod.invoke(null, handle);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return null;
		}
	}

}
