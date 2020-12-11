package com.loohp.interactivechatdiscordsrvaddon.Utils;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;

import com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.Utils.ChatColorUtils;
import com.loohp.interactivechat.Utils.MCVersion;
import com.loohp.interactivechat.Utils.MaterialUtils;
import com.loohp.interactivechat.Utils.NBTUtils;
import com.loohp.interactivechat.Utils.RarityUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.Registies.DiscordDataRegistry;

import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public class ItemStackUtils {
	
	public static final String DISCORD_EMPTY = "\u200e";
	
	private static Method bukkitBukkitClassGetMapShortMethod;
	private static Method bukkitMapViewClassGetIdMethod;
	
	static {
		try {
			try {
				bukkitBukkitClassGetMapShortMethod = Bukkit.class.getMethod("getMap", short.class);
			} catch (NoSuchMethodException e1) {
				bukkitBukkitClassGetMapShortMethod = null;
			}
			try {
				bukkitMapViewClassGetIdMethod = MapView.class.getMethod("getId");
			} catch (NoSuchMethodException e1) {
				bukkitMapViewClassGetIdMethod = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Color getDiscordColor(ItemStack item) {
		if (item != null && item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasDisplayName() && !meta.getDisplayName().equals("")) {
				String colorStr = ChatColorUtils.getFirstColors(meta.getDisplayName());
				if (colorStr.length() > 1) {
					ChatColor chatColor = ColorUtils.toChatColor(colorStr);
					if (chatColor != null && ChatColorUtils.isColor(chatColor)) {
						try {
							return chatColor.getColor();
						} catch (Throwable e) {
							return ColorUtils.getColor(chatColor);
						}
					}
				}
			}
		}
		try {
			return RarityUtils.getRarityColor(item).getColor();
		} catch (Throwable e) {
			return ColorUtils.getColor(RarityUtils.getRarityColor(item));
		}
	}
	
	public static class DiscordDescription {
		private String name;
		private Optional<String> description;
		
		public DiscordDescription(String name, String description) {
			this.name = name.isBlank() ? DISCORD_EMPTY : name;
			this.description = Optional.ofNullable(description);
		}

		public String getName() {
			return name;
		}

		public Optional<String> getDescription() {
			return description;
		}
	}
	
	public static DiscordDescription getDiscordDescription(ItemStack item) throws Exception {
		if (item == null) {
			item = new ItemStack(Material.AIR);
		}
		XMaterial xMaterial = XMaterial.matchXMaterial(item);
		String name;
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && !item.getItemMeta().getDisplayName().equals("")) {
			name = item.getItemMeta().getDisplayName();
		} else {
			String itemKey = MaterialUtils.getMinecraftLangName(item);
			if (InteractiveChat.version.isLegacy()) {
				name = itemKey;
			} else {
				name = InteractiveChatDiscordSrvAddon.plugin.getModernItemTrans(itemKey);
				if (xMaterial.equals(XMaterial.PLAYER_HEAD)) {
					String owner = NBTUtils.getString(item, "SkullOwner", "Name");
					if (owner != null) {
						name = name.replaceFirst("%s", owner);
					}
				}
			}
		}
		if (item.getAmount() == 1) {
			name = InteractiveChatDiscordSrvAddon.plugin.itemDisplaySingle.replace("{Item}", ChatColorUtils.stripColor(name)).replace("{Amount}", String.valueOf(item.getAmount()));
		} else {
			name = InteractiveChatDiscordSrvAddon.plugin.itemDisplayMultiple.replace("{Item}", ChatColorUtils.stripColor(name)).replace("{Amount}", String.valueOf(item.getAmount()));
		}
		
		boolean hasMeta = item.hasItemMeta();
		String description = "";
		
		if (ItemMapWrapper.isFilledMap(item)) {
			MapMeta map = (MapMeta) item.getItemMeta();
			MapView mapView;
			int id;
			if (InteractiveChat.version.isPost1_14() || InteractiveChat.version.equals(MCVersion.V1_13_1)) {
				mapView = map.getMapView();
				id = mapView.getId();
			} else if (InteractiveChat.version.equals(MCVersion.V1_13)) {
				short shortId = (short) bukkitMapViewClassGetIdMethod.invoke(map);
				mapView = (MapView) bukkitBukkitClassGetMapShortMethod.invoke(null, shortId);
				id = shortId;
			} else {
				short shortId = item.getDurability();
				mapView = (MapView) bukkitBukkitClassGetMapShortMethod.invoke(null, shortId);
				id = shortId;
			}
			int scale = mapView.getScale().getValue();
			description += InteractiveChatDiscordSrvAddon.plugin.getTrans().getString("FilledMap.Id").replace("{Id}", id + "") + "\n";
			description += InteractiveChatDiscordSrvAddon.plugin.getTrans().getString("FilledMap.Scale").replace("{Scale}", (int) Math.pow(2, scale) + "") + "\n";
			description += InteractiveChatDiscordSrvAddon.plugin.getTrans().getString("FilledMap.Level").replace("{Level}", scale + "") + "\n";
			description += "\n";
		}
		
		if (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS))) {
			if (item.getItemMeta() instanceof PotionMeta) {
				PotionMeta meta = (PotionMeta) item.getItemMeta();
				List<PotionEffect> effects = new ArrayList<>();
				List<PotionEffect> base = PotionUtils.getBasePotionEffect(item);
				if (base != null) {
					effects.addAll(base);
				}
				effects.addAll(meta.getCustomEffects());
				
				if (effects.isEmpty()) {
					description += "**" + InteractiveChatDiscordSrvAddon.plugin.getTrans().getString("Effects.NoEffect") + "**\n";
				} else {
					for (PotionEffect effect : effects) {
						description += "**" + InteractiveChatDiscordSrvAddon.plugin.getTrans().getString("Effects.Mappings." + effect.getType().getName().toUpperCase());
						int amplifier = effect.getAmplifier();
						if (amplifier > 0) {
							description += " " + RomanNumberUtils.toRoman(amplifier + 1);
						}
						if (!effect.getType().isInstant()) {
							if (xMaterial.equals(XMaterial.LINGERING_POTION)) {
								description += " (" + TimeUtils.getReadableTimeBetween(0, effect.getDuration() / 4 * 50) + ")";
							} else {
								description += " (" + TimeUtils.getReadableTimeBetween(0, effect.getDuration() * 50) + ")";
							}
						}
						description += "**\n";
					}
				}
				
				if (!description.equals("")) {
					description += "\n";
				}
			}
		}
		
		if (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))) {
			if (hasMeta && item.getItemMeta() instanceof EnchantmentStorageMeta) {
				for (Entry<Enchantment, Integer> entry : CustomMapUtils.sortMapByValue(((EnchantmentStorageMeta) item.getItemMeta()).getStoredEnchants()).entrySet()) {
					Enchantment ench = entry.getKey();
					int level = entry.getValue();
					String enchName = InteractiveChatDiscordSrvAddon.plugin.getTrans().getString("Enchantments.Mappings." + ench.getName().toUpperCase());
					if (enchName != null) {
						description += "**" + enchName + (ench.getMaxLevel() == 1 && level == 1 ? "" : " " + RomanNumberUtils.toRoman(level)) + "**\n";
					}
				}
			} else {
				for (Entry<Enchantment, Integer> entry : CustomMapUtils.sortMapByValue(item.getEnchantments()).entrySet()) {
					Enchantment ench = entry.getKey();
					int level = entry.getValue();
					String enchName = InteractiveChatDiscordSrvAddon.plugin.getTrans().getString("Enchantments.Mappings." + ench.getName().toUpperCase());
					if (enchName != null) {
						description += "**" + enchName + (ench.getMaxLevel() == 1 && level == 1 ? "" : " " + RomanNumberUtils.toRoman(level)) + "**\n";
					}
				}
			}
		}
		
		if (hasMeta && item.getItemMeta() instanceof LeatherArmorMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DYE)) {
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			if (NBTUtils.contains(item, "display", "color")) {
				if (!description.equals("")) {
					description += "\n";
				}
				Color color = new Color(meta.getColor().asRGB());
				String hex = ColorUtils.rgb2Hex(color).toUpperCase();
				description += InteractiveChatDiscordSrvAddon.plugin.getTrans().getString("Dye.Color").replace("{Hex}", hex) + "\n";
			}
		}
		
		if (hasMeta) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasLore()) {
				if (!description.equals("")) {
					description += "\n";
				}
				description += ChatColorUtils.stripColor(String.join("\n", meta.getLore())) + "\n"; 
			}
		}
		
		if (hasMeta && isUnbreakble(item) && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
			if (!description.equals("")) {
				description += "\n";
			}
			description += "**" + InteractiveChatDiscordSrvAddon.plugin.getTrans().getString("Unbreakable.Name") + "**\n";
		}
		
		if (item.getType().getMaxDurability() > 0) {
			int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
			int maxDur = item.getType().getMaxDurability();
			if (durability < maxDur) {
				if (!description.equals("")) {
					description += "\n";
				}
				description += "**" + InteractiveChatDiscordSrvAddon.plugin.getTrans().getString("Durability.ToolTip").replace("{Remaining}", String.valueOf(durability)).replace("{Max}", String.valueOf(maxDur)) + "**\n";
			}
		}
		
		if (InteractiveChatDiscordSrvAddon.plugin.escapeDiscordMarkdownInItems) {
			name = name.replaceAll(DiscordDataRegistry.getMarkdownSpecialPattern(), "\\\\$1");
			if (description != null) {
				description = description.replaceAll(DiscordDataRegistry.getMarkdownSpecialPattern(), "\\\\$1");
			}
		}
		
		return new DiscordDescription(name, description.isBlank() ? null : description);
	}
	
	public static boolean isUnbreakble(ItemStack item) {
		try {
			if (item.hasItemMeta()) {
				return item.getItemMeta().isUnbreakable();
			} else {
				return false;
			}
		} catch (Throwable e) {
			return NBTUtils.getByte(item, "Unbreakable") > 0;
		}
	}

}
