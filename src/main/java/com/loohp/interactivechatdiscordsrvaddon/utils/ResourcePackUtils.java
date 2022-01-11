package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

import com.loohp.interactivechat.utils.NMSUtils;

public class ResourcePackUtils {
	
	private static Class<?> craftServerClass;
	private static Method craftServerGetServerMethod;
	private static Method nmsGetResourcePackMethod;
	
	static {
		try {
			craftServerClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.CraftServer");
			craftServerGetServerMethod = craftServerClass.getMethod("getServer");
			try {
				nmsGetResourcePackMethod = craftServerGetServerMethod.getReturnType().getMethod("getResourcePack");
			} catch (Exception e) {
				nmsGetResourcePackMethod = craftServerGetServerMethod.getReturnType().getMethod("T");
			}
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	public static String getServerResourcePack() {
		try {
			Object craftServerObject = craftServerClass.cast(Bukkit.getServer());
			Object nmsMinecraftServerObject = craftServerGetServerMethod.invoke(craftServerObject);
			Object resourcePackStringObject = nmsGetResourcePackMethod.invoke(nmsMinecraftServerObject);
			return resourcePackStringObject == null ? "" : resourcePackStringObject.toString();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return "";
	}

}
