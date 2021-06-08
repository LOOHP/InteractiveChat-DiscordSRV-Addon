package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Collection;

import org.bukkit.entity.Player;

import com.loohp.interactivechat.utils.NMSUtils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class SkinUtils {
	
	private static Class<?> craftPlayerClass;
	private static Class<?> nmsEntityPlayerClass;
	private static Method craftPlayerGetHandleMethod;
	private static Method nmsEntityPlayerGetProfileMethod;

	static {
		try {
			craftPlayerClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.", "entity.CraftPlayer");
			nmsEntityPlayerClass = NMSUtils.getNMSClass("net.minecraft.server.", "EntityPlayer");
			craftPlayerGetHandleMethod = craftPlayerClass.getMethod("getHandle");
			nmsEntityPlayerGetProfileMethod = nmsEntityPlayerClass.getMethod("getProfile");
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	public static String getSkinJsonFromProfile(Player player) throws Exception {
		Object playerNMS = craftPlayerGetHandleMethod.invoke(craftPlayerClass.cast(player));
		GameProfile profile = (GameProfile) nmsEntityPlayerGetProfileMethod.invoke(playerNMS);
		Collection<Property> textures = profile.getProperties().get("textures");
		if (textures == null || textures.isEmpty()) {
			return null;
		}
		Property property = textures.iterator().next();
		return new String(Base64.getDecoder().decode(property.getValue()));
	}

}
