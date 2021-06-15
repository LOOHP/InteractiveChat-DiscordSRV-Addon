package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TranslatableComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.org.apache.commons.text.WordUtils;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ColorUtils;
import com.loohp.interactivechat.utils.FilledMapUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.JsonUtils;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NBTUtils;
import com.loohp.interactivechat.utils.RarityUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.registies.DiscordDataRegistry;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.mcdiscordreserializer.discord.DiscordSerializer;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public class DiscordItemStackUtils {
	
	public static final String DISCORD_EMPTY = "\u200e";
	
	private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().extractUrls().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
	
	private static Method bukkitBukkitClassGetMapShortMethod = null;
	private static Method bukkitMapViewClassGetIdMethod = null;
	private static boolean chatColorHasGetColor = false;
	private static boolean itemMetaHasUnbreakable = false;
	
	static {
		try {
			try {
				bukkitBukkitClassGetMapShortMethod = Bukkit.class.getMethod("getMap", short.class);
			} catch (NoSuchMethodException e1) {}
			try {
				bukkitMapViewClassGetIdMethod = MapView.class.getMethod("getId");
			} catch (NoSuchMethodException e1) {}
			chatColorHasGetColor = Stream.of(ChatColor.class.getMethods()).anyMatch(each -> each.getName().equalsIgnoreCase("getColor") && each.getReturnType().equals(Color.class));
			itemMetaHasUnbreakable = Stream.of(ItemMeta.class.getMethods()).anyMatch(each -> each.getName().equalsIgnoreCase("isUnbreakable") && each.getReturnType().equals(boolean.class));
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
						return chatColorHasGetColor ? chatColor.getColor() : ColorUtils.getColor(chatColor);
					}
				}
			}
		}
		return chatColorHasGetColor ? RarityUtils.getRarityColor(item).getColor() : ColorUtils.getColor(RarityUtils.getRarityColor(item));
	}
	
	public static class DiscordDescription {
		private String name;
		private Optional<String> description;
		
		public DiscordDescription(String name, String description) {
			this.name = name.trim().isEmpty() ? DISCORD_EMPTY : name;
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
		String language = InteractiveChatDiscordSrvAddon.plugin.language;
		
		if (item == null) {
			item = new ItemStack(Material.AIR);
		}
		XMaterial xMaterial = XMaterial.matchXMaterial(item);
		String name;
		if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && !item.getItemMeta().getDisplayName().equals("")) {
			name = item.getItemMeta().getDisplayName();
		} else {
			String itemKey = LanguageUtils.getTranslationKey(item);
			name = LanguageUtils.getTranslation(itemKey, InteractiveChatDiscordSrvAddon.plugin.language);
			if (xMaterial.equals(XMaterial.PLAYER_HEAD)) {
				String owner = NBTUtils.getString(item, "SkullOwner", "Name");
				if (owner != null) {
					name = name.replaceFirst("%s", owner);
				}
			}
		}
		if (item.getAmount() == 1 || item == null || item.getType().equals(Material.AIR)) {
			name = InteractiveChatDiscordSrvAddon.plugin.itemDisplaySingle.replace("{Item}", ComponentStringUtils.stripColorAndConvertMagic(name)).replace("{Amount}", String.valueOf(item.getAmount()));
		} else {
			name = InteractiveChatDiscordSrvAddon.plugin.itemDisplayMultiple.replace("{Item}", ComponentStringUtils.stripColorAndConvertMagic(name)).replace("{Amount}", String.valueOf(item.getAmount()));
		}
		
		boolean hasMeta = item.hasItemMeta();
		String description = "";
		
		if (xMaterial.equals(XMaterial.TROPICAL_FISH_BUCKET)) {
			List<String> translations = TranslationKeyUtils.getTropicalFishBucketName(item);
			if (translations.size() > 0) {
				description += LanguageUtils.getTranslation(translations.get(0), language) + "\n";
				if (translations.size() > 1) {
					description += translations.stream().skip(1).map(each -> LanguageUtils.getTranslation(each, language)).collect(Collectors.joining(", ")) + "\n";
				}
				description += "\n";
			}
		}
		
		if (xMaterial.isOneOf(Arrays.asList("CONTAINS:Music_Disc"))) {
			description += LanguageUtils.getTranslation(TranslationKeyUtils.getMusicDiscName(item), language) + "\n";
		}
		
		if (xMaterial.equals(XMaterial.FIREWORK_ROCKET)) {
			if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && NBTUtils.contains(item, "Fireworks", "Flight")) {
				int flight = NBTUtils.getByte(item, "Fireworks", "Flight");
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getRocketFlightDuration(), language) + " " + flight + "\n";
			}
		}
		
		if (xMaterial.equals(XMaterial.CROSSBOW)) {
			CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
			List<ItemStack> charged = meta.getChargedProjectiles();
			if (charged != null && !charged.isEmpty()) {
				ItemStack charge = charged.get(0);
				String chargeItemName = getDiscordDescription(charge).getName();
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getCrossbowProjectile(), language) + " [**" + chargeItemName + "**]\n\n";
			}
		}
		
		if (FilledMapUtils.isFilledMap(item)) {
			MapMeta map = (MapMeta) item.getItemMeta();
			MapView mapView;
			int id;
			if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_13_1)) {
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
			if (!InteractiveChat.version.isLegacy()) {
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getFilledMapId(), language).replaceFirst("%s", id + "") + "\n";
			} else {
				name += " (#" + id + ")";
			}
			description += LanguageUtils.getTranslation(TranslationKeyUtils.getFilledMapScale(), language).replaceFirst("%s", (int) Math.pow(2, scale) + "") + "\n";
			description += LanguageUtils.getTranslation(TranslationKeyUtils.getFilledMapLevel(), language).replaceFirst("%s", scale + "").replaceFirst("%s", "4") + "\n";
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
					description += "**" + LanguageUtils.getTranslation(TranslationKeyUtils.getNoEffect(), language) + "**\n";
				} else {
					for (PotionEffect effect : effects) {
						String key = TranslationKeyUtils.getEffect(effect.getType());
						String translation = LanguageUtils.getTranslation(key, language);
						if (key.equals(translation)) {
							description += "**" + WordUtils.capitalize(effect.getType().getName().toLowerCase().replace("_", " "));
						} else {
							description += "**" + translation;
						}
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
					String key = TranslationKeyUtils.getEnchantment(ench);
					String translation = LanguageUtils.getTranslation(key, language);
					String enchName;
					if (key.equals(translation)) {
						enchName = WordUtils.capitalize(ench.getName().toLowerCase().replace("_", " "));
					} else {
						enchName = translation;
					}
					if (enchName != null) {
						description += "**" + enchName + (ench.getMaxLevel() == 1 && level == 1 ? "" : " " + RomanNumberUtils.toRomanIfUnder(level, 11)) + "**\n";
					}
				}
			} else {
				for (Entry<Enchantment, Integer> entry : CustomMapUtils.sortMapByValue(item.getEnchantments()).entrySet()) {
					Enchantment ench = entry.getKey();
					int level = entry.getValue();
					String key = TranslationKeyUtils.getEnchantment(ench);
					String translation = LanguageUtils.getTranslation(key, language);
					String enchName;
					if (key.equals(translation)) {
						enchName = WordUtils.capitalize(ench.getName().toLowerCase().replace("_", " "));
					} else {
						enchName = translation;
					}
					if (enchName != null) {
						description += "**" + enchName + (ench.getMaxLevel() == 1 && level == 1 ? "" : " " + RomanNumberUtils.toRomanIfUnder(level, 11)) + "**\n";
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
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getDyeColor(), language).replaceFirst("%s", hex) + "\n";
			}
		}
		
		if (hasMeta) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasLore()) {
				if (!description.equals("")) {
					description += "\n";
				}
				String lore = String.join("\n", meta.getLore());
				if (DiscordSRV.config().getBoolean("Experiment_MCDiscordReserializer_ToDiscord")) {
					if (InteractiveChatDiscordSrvAddon.plugin.escapeDiscordMarkdownInItems) {
						lore = lore.replaceAll(DiscordDataRegistry.getMarkdownSpecialPattern(), "\\\\$1");
					}
					lore = DiscordSerializer.INSTANCE.serialize(ComponentStringUtils.toDiscordSRVComponent(LEGACY_SERIALIZER.deserialize(String.join("\n", meta.getLore()))));
				} else {
					lore = ComponentStringUtils.stripColorAndConvertMagic(String.join("\n", meta.getLore()));
					if (InteractiveChatDiscordSrvAddon.plugin.escapeDiscordMarkdownInItems) {
						lore = lore.replaceAll(DiscordDataRegistry.getMarkdownSpecialPattern(), "\\\\$1");
					}
				}
				description += lore + "\n";
			}
		}
		
		if (hasMeta && isUnbreakble(item) && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
			if (!description.equals("")) {
				description += "\n";
			}
			description += "**" + LanguageUtils.getTranslation(TranslationKeyUtils.getUnbreakable(), language) + "**\n";
		}
		
		if (item.getType().getMaxDurability() > 0) {
			int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
			int maxDur = item.getType().getMaxDurability();
			if (durability < maxDur) {
				if (!description.equals("")) {
					description += "\n";
				}
				description += "**" + LanguageUtils.getTranslation(TranslationKeyUtils.getDurability(), language).replaceFirst("%s", String.valueOf(durability)).replaceFirst("%s", String.valueOf(maxDur)) + "**\n";
			}
		}
		
		return new DiscordDescription(name, description.trim().isEmpty() ? null : description);
	}
	
	public static class DiscordToolTip {
		private List<Component> components;
		private boolean isBaseItem;
		
		public DiscordToolTip(List<Component> components, boolean isBaseItem) {
			this.components = components;
			this.isBaseItem = isBaseItem;
		}

		public List<Component> getComponents() {
			return components;
		}

		public boolean isBaseItem() {
			return isBaseItem;
		}
	}
	
	public static DiscordToolTip getToolTip(ItemStack item) throws Exception {
		String language = InteractiveChatDiscordSrvAddon.plugin.language;
		
		List<Component> prints = new ArrayList<>();
		boolean hasCustomName = true;
		
		if (item == null) {
			item = new ItemStack(Material.AIR);
		}
		XMaterial xMaterial = XMaterial.matchXMaterial(item);
		
		Component itemDisplayNameComponent = null;
		ChatColor rarityChatColor = RarityUtils.getRarityColor(item);
	    NamedTextColor rarityColor = ColorUtils.toNamedTextColor(rarityChatColor);
	    
	    String rawDisplayName = item.hasItemMeta() && item.getItemMeta() != null ? NBTUtils.getString(item, "display", "Name") : null;
	    if (rawDisplayName != null && JsonUtils.isValid(rawDisplayName)) {
	    	try {
	    		itemDisplayNameComponent = InteractiveChatComponentSerializer.gson().deserialize(rawDisplayName);
	    		itemDisplayNameComponent = itemDisplayNameComponent.colorIfAbsent(rarityColor);
	    	} catch (Throwable e) {
	    		itemDisplayNameComponent = null;
	    	}
	    }
	    
	    if (itemDisplayNameComponent == null) {
		    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && !item.getItemMeta().getDisplayName().equals("")) {
		    	itemDisplayNameComponent = LegacyComponentSerializer.legacySection().deserialize(rarityChatColor + item.getItemMeta().getDisplayName());
		    } else {
		    	itemDisplayNameComponent = Component.translatable(LanguageUtils.getTranslationKey(item));
		    	itemDisplayNameComponent = itemDisplayNameComponent.color(rarityColor);
		    	if (xMaterial.equals(XMaterial.PLAYER_HEAD)) {
					String owner = NBTUtils.getString(item, "SkullOwner", "Name");
					if (owner != null) {
						itemDisplayNameComponent = ((TranslatableComponent) itemDisplayNameComponent).args(Component.text(owner));
					}
				}
		    }
	    }
	    
	    prints.add(itemDisplayNameComponent);
		
		boolean hasMeta = item.hasItemMeta();
		
		if (xMaterial.equals(XMaterial.TROPICAL_FISH_BUCKET)) {
			List<String> translations = TranslationKeyUtils.getTropicalFishBucketName(item);
			if (translations.size() > 0) {
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + "" + ChatColor.ITALIC + LanguageUtils.getTranslation(translations.get(0), language)));
				if (translations.size() > 1) {
					prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + "" + ChatColor.ITALIC + translations.stream().skip(1).map(each -> LanguageUtils.getTranslation(each, language)).collect(Collectors.joining(", "))));
				}
			}
		}
		
		if (xMaterial.isOneOf(Arrays.asList("CONTAINS:Music_Disc"))) {
			prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getMusicDiscName(item), language)));
		}
		
		if (xMaterial.equals(XMaterial.FIREWORK_ROCKET)) {
			if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && NBTUtils.contains(item, "Fireworks", "Flight")) {
				int flight = NBTUtils.getByte(item, "Fireworks", "Flight");
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getRocketFlightDuration(), language) + " " + flight));
			}
		}
		
		if (xMaterial.equals(XMaterial.CROSSBOW)) {
			CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
			List<ItemStack> charged = meta.getChargedProjectiles();
			if (charged != null && !charged.isEmpty()) {
				ItemStack charge = charged.get(0);
				Component chargeItemName = getToolTip(charge).getComponents().get(0);
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.WHITE + LanguageUtils.getTranslation(TranslationKeyUtils.getCrossbowProjectile(), language) + " [" + InteractiveChatComponentSerializer.bungeecordApiLegacy().serialize(chargeItemName) + ChatColor.WHITE + "]"));
			}
		}
		
		if (FilledMapUtils.isFilledMap(item)) {
			MapMeta map = (MapMeta) item.getItemMeta();
			MapView mapView;
			int id;
			if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_13_1)) {
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
			if (!InteractiveChat.version.isLegacy()) {
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getFilledMapId(), language).replaceFirst("%s", id + "")));
			} else {
				prints.set(0, prints.get(0).children(Arrays.asList(LegacyComponentSerializer.legacySection().deserialize(ChatColor.WHITE + " (#" + id + ")"))));
			}
			prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getFilledMapScale(), language).replaceFirst("%s", (int) Math.pow(2, scale) + "")));
			prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getFilledMapLevel(), language).replaceFirst("%s", scale + "").replaceFirst("%s", "4")));
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
					prints.add(LegacyComponentSerializer.legacySection().deserialize(LanguageUtils.getTranslation(TranslationKeyUtils.getNoEffect(), language)));
				} else {
					for (PotionEffect effect : effects) {
						String key = TranslationKeyUtils.getEffect(effect.getType());
						String translation = LanguageUtils.getTranslation(key, language);
						String description = "";
						if (key.equals(translation)) {
							description += WordUtils.capitalize(effect.getType().getName().toLowerCase().replace("_", " "));
						} else {
							description += translation;
						}
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
						ChatColor color = PotionUtils.isPositive(effect.getType()) ? ChatColor.BLUE : ChatColor.RED;
						prints.add(LegacyComponentSerializer.legacySection().deserialize(color + description));
					}
				}
			}
		}
		
		if (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))) {
			if (hasMeta && item.getItemMeta() instanceof EnchantmentStorageMeta) {
				for (Entry<Enchantment, Integer> entry : CustomMapUtils.sortMapByValue(((EnchantmentStorageMeta) item.getItemMeta()).getStoredEnchants()).entrySet()) {
					Enchantment ench = entry.getKey();
					int level = entry.getValue();
					String key = TranslationKeyUtils.getEnchantment(ench);
					String translation = LanguageUtils.getTranslation(key, language);
					String enchName;
					if (key.equals(translation)) {
						enchName = WordUtils.capitalize(ench.getName().toLowerCase().replace("_", " "));
					} else {
						enchName = translation;
					}
					if (enchName != null) {
						prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + enchName + (ench.getMaxLevel() == 1 && level == 1 ? "" : " " + RomanNumberUtils.toRomanIfUnder(level, 11))));
					}
				}
			} else {
				for (Entry<Enchantment, Integer> entry : CustomMapUtils.sortMapByValue(item.getEnchantments()).entrySet()) {
					Enchantment ench = entry.getKey();
					int level = entry.getValue();
					String key = TranslationKeyUtils.getEnchantment(ench);
					String translation = LanguageUtils.getTranslation(key, language);
					String enchName;
					if (key.equals(translation)) {
						enchName = WordUtils.capitalize(ench.getName().toLowerCase().replace("_", " "));
					} else {
						enchName = translation;
					}
					if (enchName != null) {
						prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + enchName + (ench.getMaxLevel() == 1 && level == 1 ? "" : " " + RomanNumberUtils.toRomanIfUnder(level, 11))));
					}
				}
			}
		}
		
		if (hasMeta && item.getItemMeta() instanceof LeatherArmorMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DYE)) {
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			if (NBTUtils.contains(item, "display", "color")) {
				Color color = new Color(meta.getColor().asRGB());
				String hex = ColorUtils.rgb2Hex(color).toUpperCase();
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getDyeColor(), language).replaceFirst("%s", hex)));
			}
		}
		
		if (hasMeta) {
			ItemMeta meta = item.getItemMeta();
			if (meta.hasLore()) {
				for (String lore : meta.getLore()) {
					prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + lore));
				}
			}
		}
		
		if (hasMeta && isUnbreakble(item) && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
			prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.BLUE + LanguageUtils.getTranslation(TranslationKeyUtils.getUnbreakable(), language)));
		}
		
		if (item.getType().getMaxDurability() > 0) {
			int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
			int maxDur = item.getType().getMaxDurability();
			if (durability < maxDur) {
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.WHITE + LanguageUtils.getTranslation(TranslationKeyUtils.getDurability(), language).replaceFirst("%s", String.valueOf(durability)).replaceFirst("%s", String.valueOf(maxDur))));
			}
		}
		
		return new DiscordToolTip(prints, !hasCustomName && prints.size() <= 1);
	}
	
	public static boolean isUnbreakble(ItemStack item) {
		if (itemMetaHasUnbreakable) {
			if (item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();
				if (meta != null) {
					return meta.isUnbreakable();
				}
			}
			return false;
		} else {
			return NBTUtils.getByte(item, "Unbreakable") > 0;
		}
	}

}
