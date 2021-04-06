package com.loohp.interactivechatdiscordsrvaddon.utils;

import org.bukkit.inventory.ItemStack;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.utils.MCVersion;

public class ItemStackUtils {

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
		if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_13)) {
			return typeNameString.equals("CARVED_PUMPKIN");
		} else {
			return typeNameString.equals("PUMPKIN");
		}
	}

}
