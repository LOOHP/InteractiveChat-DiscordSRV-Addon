package com.loohp.interactivechatdiscordsrvaddon.Utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.Utils.ChatColorUtils;
import com.loohp.interactivechat.Utils.MaterialUtils;
import com.loohp.interactivechat.Utils.NBTUtils;

import net.md_5.bungee.api.chat.TranslatableComponent;

public class ItemStackUtils {
	
	public static String getDiscordDescription(ItemStack item) {
		if (item == null || item.getType().equals(Material.AIR)) {
			return "";
		}
		XMaterial xMaterial = XMaterial.matchXMaterial(item);
		String itemStr;
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && !item.getItemMeta().getDisplayName().equals("")) {
			itemStr = item.getItemMeta().getDisplayName();
		} else {
			TranslatableComponent component = new TranslatableComponent(MaterialUtils.getMinecraftLangName(item));
			if (xMaterial.equals(XMaterial.PLAYER_HEAD)) {
				String owner = NBTUtils.getString(item, "SkullOwner", "Name");
				if (owner != null) {
					component.addWith(owner);
				}
			}
			itemStr = component.toLegacyText();
		}
		
		String description = "**" + ChatColorUtils.stripColor(itemStr) + "**";
		if (!item.hasItemMeta()) {
			return description;
		}
		
		ItemMeta meta = item.getItemMeta();
		if (meta.hasLore()) {
			description += ChatColorUtils.stripColor("\n" + String.join("\n", meta.getLore())); 
		}
		
		return description;
	}

}
