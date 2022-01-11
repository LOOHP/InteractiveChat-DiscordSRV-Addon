package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.awt.Color;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
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
import com.loohp.interactivechat.hooks.ecoenchants.EcoHook;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.io.github.bananapuncher714.nbteditor.NBTEditor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.libs.net.querz.nbt.io.SNBTDeserializer;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.CompoundTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.ListTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.StringTag;
import com.loohp.interactivechat.libs.org.apache.commons.text.WordUtils;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ColorUtils;
import com.loohp.interactivechat.utils.FilledMapUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.ItemStackUtils;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.RarityUtils;
import com.loohp.interactivechat.utils.XMaterialUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.registies.DiscordDataRegistry;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.PatternTypeWrapper;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.mcdiscordreserializer.discord.DiscordSerializer;
import net.md_5.bungee.api.ChatColor;

@SuppressWarnings("deprecation")
public class DiscordItemStackUtils {
	
	public static final String DISCORD_EMPTY = "\u200e";
	
	private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().extractUrls().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
	private static final DecimalFormat ATTRIBUTE_FORMAT = new DecimalFormat("0");
	
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
	
	public static DiscordDescription getDiscordDescription(ItemStack item, Player player) throws Exception {
		String language = InteractiveChatDiscordSrvAddon.plugin.language;
		if (!item.getType().equals(Material.AIR) && InteractiveChat.ecoHook) {
			item = EcoHook.setEcoLores(item, player);
		}
		
		if (item == null) {
			item = new ItemStack(Material.AIR);
		}
		XMaterial xMaterial = XMaterialUtils.matchXMaterial(item);
		String name = PlainTextComponentSerializer.plainText().serialize(LegacyComponentSerializer.legacySection().deserialize(InteractiveChatComponentSerializer.bungeecordApiLegacy().serialize(ItemStackUtils.getDisplayName(item), language)));
		if (item.getAmount() == 1 || item == null || item.getType().equals(Material.AIR)) {
			name = InteractiveChatDiscordSrvAddon.plugin.itemDisplaySingle.replace("{Item}", ComponentStringUtils.stripColorAndConvertMagic(name)).replace("{Amount}", String.valueOf(item.getAmount()));
		} else {
			name = InteractiveChatDiscordSrvAddon.plugin.itemDisplayMultiple.replace("{Item}", ComponentStringUtils.stripColorAndConvertMagic(name)).replace("{Amount}", String.valueOf(item.getAmount()));
		}
		
		boolean hasMeta = item.hasItemMeta();
		String description = "";
		
		if (xMaterial.equals(XMaterial.WRITTEN_BOOK) && hasMeta && item.getItemMeta() instanceof BookMeta) {
			BookMeta meta = (BookMeta) item.getItemMeta();
			String author = meta.getAuthor();
			if (author != null) {
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getBookAuthor(), language).replaceFirst("%1\\$s", ChatColorUtils.stripColor(author)) + "\n";
			}
			Generation generation = meta.getGeneration();
			if (generation == null) {
				generation = Generation.ORIGINAL;
			}
			description += LanguageUtils.getTranslation(TranslationKeyUtils.getBookGeneration(generation), language) + "\n";
			description += "\n";
		}
		
		if (xMaterial.equals(XMaterial.SHIELD) && (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)))) {
			if (NBTEditor.contains(item, "BlockEntityTag")) {
				List<Pattern> patterns = Collections.emptyList();
				if (!(item.getItemMeta() instanceof BannerMeta)) {
					if (item.getItemMeta() instanceof BlockStateMeta) {
						BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
						if (bmeta.hasBlockState()) {
							Banner bannerBlockMeta = (Banner) bmeta.getBlockState();
				            patterns = bannerBlockMeta.getPatterns();
						}			       
					}
				} else {
					BannerMeta meta = (BannerMeta) item.getItemMeta();
					patterns = meta.getPatterns();
				}

	            for (Pattern pattern : patterns) {
	            	PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
	            	description += LanguageUtils.getTranslation(TranslationKeyUtils.getBannerPatternName(type, pattern.getColor()), language) + "\n";
	            }
			}
		}
		
		if (xMaterial.isOneOf(Arrays.asList("CONTAINS:Banner")) && (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)))) {
			List<Pattern> patterns = Collections.emptyList();
			if (!(item.getItemMeta() instanceof BannerMeta)) {
				if (item.getItemMeta() instanceof BlockStateMeta) {
					BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
		            Banner bannerBlockMeta = (Banner) bmeta.getBlockState();
		            patterns = bannerBlockMeta.getPatterns();
				}
			} else {
				BannerMeta meta = (BannerMeta) item.getItemMeta();
				patterns = meta.getPatterns();
			}	

            for (Pattern pattern : patterns) {
            	PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
            	description += LanguageUtils.getTranslation(TranslationKeyUtils.getBannerPatternName(type, pattern.getColor()), language) + "\n";
            }
		}
		
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
			if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && NBTEditor.contains(item, "Fireworks", "Flight")) {
				int flight = NBTEditor.getByte(item, "Fireworks", "Flight");
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getRocketFlightDuration(), language) + " " + flight + "\n";
			}
		}
		
		if (xMaterial.equals(XMaterial.CROSSBOW)) {
			CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
			List<ItemStack> charged = meta.getChargedProjectiles();
			if (charged != null && !charged.isEmpty()) {
				ItemStack charge = charged.get(0);
				String chargeItemName = getDiscordDescription(charge, player).getName();
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
						String effectLevelTranslation = LanguageUtils.getTranslation(TranslationKeyUtils.getEffectLevel(amplifier), language);
						if (effectLevelTranslation.length() > 0) {
							description += " " + effectLevelTranslation;
						}
						if (!effect.getType().isInstant()) {
							if (xMaterial.equals(XMaterial.LINGERING_POTION)) {
								description += " (" + TimeUtils.getReadableTimeBetween(0, effect.getDuration() / 4 * 50, ":", ChronoUnit.MINUTES, ChronoUnit.SECONDS, true) + ")";
							} else {
								description += " (" + TimeUtils.getReadableTimeBetween(0, effect.getDuration() * 50, ":", ChronoUnit.MINUTES, ChronoUnit.SECONDS, true) + ")";
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
						description += "**" + enchName + (ench.getMaxLevel() == 1 && level == 1 ? "" : " " + LanguageUtils.getTranslation(TranslationKeyUtils.getEnchantmentLevel(level), language)) + "**\n";
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
						description += "**" + enchName + (ench.getMaxLevel() == 1 && level == 1 ? "" : " " + LanguageUtils.getTranslation(TranslationKeyUtils.getEnchantmentLevel(level), language)) + "**\n";
					}
				}
			}
		}
		
		if (hasMeta && item.getItemMeta() instanceof LeatherArmorMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DYE)) {
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			if (NBTEditor.contains(item, "display", "color")) {
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
		
		if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && NBTEditor.contains(item, "AttributeModifiers") && NBTEditor.getSize(item, "AttributeModifiers") > 0 && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
			boolean useMainHand = false;
			List<String> mainHand = new LinkedList<>();
			boolean useOffhand = false;
			List<String> offHand = new LinkedList<>();
			boolean useFeet = false;
			List<String> feet = new LinkedList<>();
			boolean useLegs = false;
			List<String> legs = new LinkedList<>();
			boolean useChest = false;
			List<String> chest = new LinkedList<>();
			boolean useHead = false;
			List<String> head = new LinkedList<>();
			@SuppressWarnings("unchecked")
			ListTag<CompoundTag> attributeList = (ListTag<CompoundTag>) new SNBTDeserializer().fromString(NBTEditor.getNBTCompound(item, "tag", "AttributeModifiers").toJson());
			for (CompoundTag attributeTag : attributeList) {
				String attributeName = attributeTag.getString("AttributeName").replace("minecraft:", "");
				double amount = attributeTag.getDouble("Amount");
				int operation = attributeTag.containsKey("Operation") ? attributeTag.getInt("Operation") : 0;
				String attributeComponent = LanguageUtils.getTranslation(TranslationKeyUtils.getAttributeModifierKey(amount, operation), language).replaceFirst("%s", ATTRIBUTE_FORMAT.format(Math.abs(amount)) + "").replaceFirst("%s", LanguageUtils.getTranslation(TranslationKeyUtils.getAttributeKey(attributeName), language)).replace("%%", "%");
				if (attributeTag.containsKey("Slot")) {
					String slot = attributeTag.getString("Slot");
					if (slot.equals("mainhand")) {
						if (amount != 0) {
							mainHand.add(attributeComponent);
						}
						useMainHand = true;
					} else if (slot.equals("offhand")) {
						if (amount != 0) {
							offHand.add(attributeComponent);
						}
						useOffhand = true;
					} else if (slot.equals("feet")) {
						if (amount != 0) {
							feet.add(attributeComponent);
						}
						useFeet = true;
					} else if (slot.equals("legs")) {
						if (amount != 0) {
							legs.add(attributeComponent);
						}
						useLegs = true;
					} else if (slot.equals("chest")) {
						if (amount != 0) {
							chest.add(attributeComponent);
						}
						useChest = true;
					} else if (slot.equals("head")) {
						if (amount != 0) {
							head.add(attributeComponent);
						}
						useHead = true;
					}
				} else {
					if (amount != 0) {
						mainHand.add(attributeComponent);
						offHand.add(attributeComponent);
						feet.add(attributeComponent);
						legs.add(attributeComponent);
						chest.add(attributeComponent);
						head.add(attributeComponent);
					}
					useMainHand = true;
					useOffhand = true;
					useFeet = true;
					useLegs = true;
					useChest = true;
					useHead = true;
				}
			}
			if (useMainHand) {
				description += "\n";
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.HAND), language) + "\n";
				for (String each : mainHand) {
					description += each + "\n";
				}
			}
			if (useOffhand) {
				description += "\n";
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.OFF_HAND), language) + "\n";
				for (String each : offHand) {
					description += each + "\n";
				}
			}
			if (useFeet) {
				description += "\n";
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.FEET), language) + "\n";
				for (String each : feet) {
					description += each + "\n";
				}
			}
			if (useLegs) {
				description += "\n";
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.LEGS), language) + "\n";
				for (String each : legs) {
					description += each + "\n";
				}
			}
			if (useChest) {
				description += "\n";
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.CHEST), language) + "\n";
				for (String each : chest) {
					description += each + "\n";
				}
			}
			if (useHead) {
				description += "\n";
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.HEAD), language) + "\n";
				for (String each : head) {
					description += each + "\n";
				}
			}
		}
		
		if (hasMeta && isUnbreakble(item) && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
			if (!description.equals("")) {
				description += "\n";
			}
			description += "**" + LanguageUtils.getTranslation(TranslationKeyUtils.getUnbreakable(), language) + "**\n";
		}
		
		if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DESTROYS)) {
			if (NBTEditor.contains(item, "CanDestroy") && NBTEditor.getSize(item, "CanDestroy") > 0) {
				description += "\n";
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getCanDestroy(), language) + "\n";
				@SuppressWarnings("unchecked")
				ListTag<StringTag> materialList = (ListTag<StringTag>) new SNBTDeserializer().fromString(NBTEditor.getNBTCompound(item, "tag", "CanDestroy").toJson());
				for (StringTag materialTag : materialList) {
					XMaterial parsedXMaterial = XMaterialUtils.matchXMaterial(materialTag.getValue().replace("minecraft:", "").toUpperCase());
					if (parsedXMaterial == null) {
						description += WordUtils.capitalizeFully(materialTag.getValue().replace("_", " ").toLowerCase()) + "\n";
					} else {
						description += LanguageUtils.getTranslation(LanguageUtils.getTranslationKey(parsedXMaterial.parseItem()), language) + "\n";
					}
				}
			}
		}
		
		if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_PLACED_ON)) {
			if (NBTEditor.contains(item, "CanPlaceOn") && NBTEditor.getSize(item, "CanPlaceOn") > 0) {
				description += "\n";
				description += LanguageUtils.getTranslation(TranslationKeyUtils.getCanPlace(), language) + "\n";
				@SuppressWarnings("unchecked")
				ListTag<StringTag> materialList = (ListTag<StringTag>) new SNBTDeserializer().fromString(NBTEditor.getNBTCompound(item, "tag", "CanPlaceOn").toJson());
				for (StringTag materialTag : materialList) {
					XMaterial parsedXMaterial = XMaterialUtils.matchXMaterial(materialTag.getValue().replace("minecraft:", "").toUpperCase());
					if (parsedXMaterial == null) {
						description += WordUtils.capitalizeFully(materialTag.getValue().replace("_", " ").toLowerCase()) + "\n";
					} else {
						description += LanguageUtils.getTranslation(LanguageUtils.getTranslationKey(parsedXMaterial.parseItem()), language) + "\n";
					}
				}
			}
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
	
	public static DiscordToolTip getToolTip(ItemStack item, Player player) throws Exception {
		String language = InteractiveChatDiscordSrvAddon.plugin.language;
		if (!item.getType().equals(Material.AIR) && InteractiveChat.ecoHook) {
			item = EcoHook.setEcoLores(item, player);
		}
		
		List<Component> prints = new ArrayList<>();
		boolean hasCustomName = true;
		
		if (item == null) {
			item = new ItemStack(Material.AIR);
		}
		XMaterial xMaterial = XMaterialUtils.matchXMaterial(item);
		
		Component itemDisplayNameComponent = ItemStackUtils.getDisplayName(item);
	    prints.add(itemDisplayNameComponent);
		
		boolean hasMeta = item.hasItemMeta();
		
		if (xMaterial.equals(XMaterial.WRITTEN_BOOK) && hasMeta && item.getItemMeta() instanceof BookMeta) {
			BookMeta meta = (BookMeta) item.getItemMeta();
			String author = meta.getAuthor();
			if (author != null) {
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getBookAuthor(), language).replaceFirst("%1\\$s", author)));
			}
			Generation generation = meta.getGeneration();
			if (generation == null) {
				generation = Generation.ORIGINAL;
			}
			prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getBookGeneration(generation), language)));
		}
		
		if (xMaterial.equals(XMaterial.SHIELD) && (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)))) {
			if (NBTEditor.contains(item, "BlockEntityTag")) {
				List<Pattern> patterns = Collections.emptyList();
				if (!(item.getItemMeta() instanceof BannerMeta)) {
					if (item.getItemMeta() instanceof BlockStateMeta) {
						BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
						if (bmeta.hasBlockState()) {
							Banner bannerBlockMeta = (Banner) bmeta.getBlockState();
				            patterns = bannerBlockMeta.getPatterns();
						}			       
					}
				} else {
					BannerMeta meta = (BannerMeta) item.getItemMeta();
					patterns = meta.getPatterns();
				}

	            for (Pattern pattern : patterns) {
	            	PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
	            	prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getBannerPatternName(type, pattern.getColor()), language)));
	            }
			}
		}
		
		if (xMaterial.isOneOf(Arrays.asList("CONTAINS:Banner")) && (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)))) {
			List<Pattern> patterns = Collections.emptyList();
			if (!(item.getItemMeta() instanceof BannerMeta)) {
				if (item.getItemMeta() instanceof BlockStateMeta) {
					BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
		            Banner bannerBlockMeta = (Banner) bmeta.getBlockState();
		            patterns = bannerBlockMeta.getPatterns();
				}
			} else {
				BannerMeta meta = (BannerMeta) item.getItemMeta();
				patterns = meta.getPatterns();
			}	

            for (Pattern pattern : patterns) {
            	PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
            	prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getBannerPatternName(type, pattern.getColor()), language)));
            }
		}
		
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
			if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && NBTEditor.contains(item, "Fireworks", "Flight")) {
				int flight = NBTEditor.getByte(item, "Fireworks", "Flight");
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getRocketFlightDuration(), language) + " " + flight));
			}
		}
		
		if (xMaterial.equals(XMaterial.CROSSBOW)) {
			CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
			List<ItemStack> charged = meta.getChargedProjectiles();
			if (charged != null && !charged.isEmpty()) {
				ItemStack charge = charged.get(0);
				Component chargeItemName = getToolTip(charge, player).getComponents().get(0);
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
						String effectLevelTranslation = LanguageUtils.getTranslation(TranslationKeyUtils.getEffectLevel(amplifier), language);
						if (effectLevelTranslation.length() > 0) {
							description += " " + effectLevelTranslation;
						}
						if (!effect.getType().isInstant()) {
							if (xMaterial.equals(XMaterial.LINGERING_POTION)) {
								description += " (" + TimeUtils.getReadableTimeBetween(0, effect.getDuration() / 4 * 50, ":", ChronoUnit.MINUTES, ChronoUnit.SECONDS, true) + ")";
							} else {
								description += " (" + TimeUtils.getReadableTimeBetween(0, effect.getDuration() * 50, ":", ChronoUnit.MINUTES, ChronoUnit.SECONDS, true) + ")";
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
						prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + enchName + (ench.getMaxLevel() == 1 && level == 1 ? "" : " " + LanguageUtils.getTranslation(TranslationKeyUtils.getEnchantmentLevel(level), language))));
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
						prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + enchName + (ench.getMaxLevel() == 1 && level == 1 ? "" : " " + LanguageUtils.getTranslation(TranslationKeyUtils.getEnchantmentLevel(level), language))));
					}
				}
			}
		}
		
		if (hasMeta && item.getItemMeta() instanceof LeatherArmorMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DYE)) {
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			if (NBTEditor.contains(item, "display", "color")) {
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
		
		if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && NBTEditor.contains(item, "AttributeModifiers") && NBTEditor.getSize(item, "AttributeModifiers") > 0 && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
			boolean useMainHand = false;
			List<Component> mainHand = new LinkedList<>();
			boolean useOffhand = false;
			List<Component> offHand = new LinkedList<>();
			boolean useFeet = false;
			List<Component> feet = new LinkedList<>();
			boolean useLegs = false;
			List<Component> legs = new LinkedList<>();
			boolean useChest = false;
			List<Component> chest = new LinkedList<>();
			boolean useHead = false;
			List<Component> head = new LinkedList<>();
			@SuppressWarnings("unchecked")
			ListTag<CompoundTag> attributeList = (ListTag<CompoundTag>) new SNBTDeserializer().fromString(NBTEditor.getNBTCompound(item, "tag", "AttributeModifiers").toJson());
			for (CompoundTag attributeTag : attributeList) {
				String attributeName = attributeTag.getString("AttributeName").replace("minecraft:", "");
				double amount = attributeTag.getDouble("Amount");
				int operation = attributeTag.containsKey("Operation") ? attributeTag.getInt("Operation") : 0;
				Component attributeComponent = LegacyComponentSerializer.legacySection().deserialize((amount < 0 ? ChatColor.RED : ChatColor.BLUE) + LanguageUtils.getTranslation(TranslationKeyUtils.getAttributeModifierKey(amount, operation), language).replaceFirst("%s", ATTRIBUTE_FORMAT.format(Math.abs(amount)) + "").replaceFirst("%s", LanguageUtils.getTranslation(TranslationKeyUtils.getAttributeKey(attributeName), language)).replace("%%", "%"));
				if (attributeTag.containsKey("Slot")) {
					String slot = attributeTag.getString("Slot");
					if (slot.equals("mainhand")) {
						if (amount != 0) {
							mainHand.add(attributeComponent);
						}
						useMainHand = true;
					} else if (slot.equals("offhand")) {
						if (amount != 0) {
							offHand.add(attributeComponent);
						}
						useOffhand = true;
					} else if (slot.equals("feet")) {
						if (amount != 0) {
							feet.add(attributeComponent);
						}
						useFeet = true;
					} else if (slot.equals("legs")) {
						if (amount != 0) {
							legs.add(attributeComponent);
						}
						useLegs = true;
					} else if (slot.equals("chest")) {
						if (amount != 0) {
							chest.add(attributeComponent);
						}
						useChest = true;
					} else if (slot.equals("head")) {
						if (amount != 0) {
							head.add(attributeComponent);
						}
						useHead = true;
					}
				} else {
					if (amount != 0) {
						mainHand.add(attributeComponent);
						offHand.add(attributeComponent);
						feet.add(attributeComponent);
						legs.add(attributeComponent);
						chest.add(attributeComponent);
						head.add(attributeComponent);
					}
					useMainHand = true;
					useOffhand = true;
					useFeet = true;
					useLegs = true;
					useChest = true;
					useHead = true;
				}
			}
			if (useMainHand) {
				prints.add(Component.empty());
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.HAND), language)));
				for (Component each : mainHand) {
					prints.add(each);
				}
			}
			if (useOffhand) {
				prints.add(Component.empty());
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.OFF_HAND), language)));
				for (Component each : offHand) {
					prints.add(each);
				}
			}
			if (useFeet) {
				prints.add(Component.empty());
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.FEET), language)));
				for (Component each : feet) {
					prints.add(each);
				}
			}
			if (useLegs) {
				prints.add(Component.empty());
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.LEGS), language)));
				for (Component each : legs) {
					prints.add(each);
				}
			}
			if (useChest) {
				prints.add(Component.empty());
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.CHEST), language)));
				for (Component each : chest) {
					prints.add(each);
				}
			}
			if (useHead) {
				prints.add(Component.empty());
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.HEAD), language)));
				for (Component each : head) {
					prints.add(each);
				}
			}
		}
		
		if (hasMeta && isUnbreakble(item) && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
			prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.BLUE + LanguageUtils.getTranslation(TranslationKeyUtils.getUnbreakable(), language)));
		}
		
		if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DESTROYS)) {
			if (NBTEditor.contains(item, "CanDestroy") && NBTEditor.getSize(item, "CanDestroy") > 0) {
				prints.add(Component.empty());
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getCanDestroy(), language)));
				@SuppressWarnings("unchecked")
				ListTag<StringTag> materialList = (ListTag<StringTag>) new SNBTDeserializer().fromString(NBTEditor.getNBTCompound(item, "tag", "CanDestroy").toJson());
				for (StringTag materialTag : materialList) {
					XMaterial parsedXMaterial = XMaterialUtils.matchXMaterial(materialTag.getValue().replace("minecraft:", "").toUpperCase());
					if (parsedXMaterial == null) {
						prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.DARK_GRAY + WordUtils.capitalizeFully(materialTag.getValue().replace("_", " ").toLowerCase())));
					} else {
						prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.DARK_GRAY + LanguageUtils.getTranslation(LanguageUtils.getTranslationKey(parsedXMaterial.parseItem()), language)));
					}
				}
			}
		}
		
		if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_PLACED_ON)) {
			if (NBTEditor.contains(item, "CanPlaceOn") && NBTEditor.getSize(item, "CanPlaceOn") > 0) {
				prints.add(Component.empty());
				prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getCanPlace(), language)));
				@SuppressWarnings("unchecked")
				ListTag<StringTag> materialList = (ListTag<StringTag>) new SNBTDeserializer().fromString(NBTEditor.getNBTCompound(item, "tag", "CanPlaceOn").toJson());
				for (StringTag materialTag : materialList) {
					XMaterial parsedXMaterial = XMaterialUtils.matchXMaterial(materialTag.getValue().replace("minecraft:", "").toUpperCase());
					if (parsedXMaterial == null) {
						prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.DARK_GRAY + WordUtils.capitalizeFully(materialTag.getValue().replace("_", " ").toLowerCase())));
					} else {
						prints.add(LegacyComponentSerializer.legacySection().deserialize(ChatColor.DARK_GRAY + LanguageUtils.getTranslation(LanguageUtils.getTranslationKey(parsedXMaterial.parseItem()), language)));
					}
				}
			}
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
			return NBTEditor.getByte(item, "Unbreakable") > 0;
		}
	}

}
