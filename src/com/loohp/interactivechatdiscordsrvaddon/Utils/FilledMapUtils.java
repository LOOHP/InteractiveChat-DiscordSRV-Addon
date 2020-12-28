package com.loohp.interactivechatdiscordsrvaddon.Utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

public class FilledMapUtils {
	
	public static boolean isFilledMap(ItemStack itemStack) {
		return (itemStack.getItemMeta() != null) && (itemStack.getItemMeta() instanceof MapMeta);
	}

}
