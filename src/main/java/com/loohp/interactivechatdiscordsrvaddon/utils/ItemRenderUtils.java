package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.Base64;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World.Environment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.io.github.bananapuncher714.nbteditor.NBTEditor;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.libs.org.json.simple.parser.ParseException;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.SkinUtils;
import com.loohp.interactivechat.utils.XMaterialUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.graphics.BannerGraphics;
import com.loohp.interactivechatdiscordsrvaddon.graphics.BannerGraphics.BannerAssetResult;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.registies.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resource.textures.GeneratedTextureResource;
import com.loohp.interactivechatdiscordsrvaddon.resource.textures.TextureResource;

@SuppressWarnings("deprecation")
public class ItemRenderUtils {

	private static final Random RANDOM = new Random();
	
	public static ItemStackProcessResult processItemForRendering(OfflineICPlayer player, ItemStack item) throws IOException {
		boolean requiresEnchantmentGlint = false;
		XMaterial xMaterial = XMaterialUtils.matchXMaterial(item);
		String directLocation = null;
		if (xMaterial.equals(XMaterial.DEBUG_STICK)) {
			requiresEnchantmentGlint = true;
		} else if (xMaterial.equals(XMaterial.ENCHANTED_GOLDEN_APPLE)) {
			requiresEnchantmentGlint = true;
		} else if (xMaterial.equals(XMaterial.WRITTEN_BOOK)) {
			requiresEnchantmentGlint = true;
		} else if (xMaterial.equals(XMaterial.ENCHANTED_BOOK)) {
			requiresEnchantmentGlint = true;
		} else if (item.getEnchantments().size() > 0) {
			requiresEnchantmentGlint = true;
		}
		
		Map<ModelOverrideType, Float> predicates = new EnumMap<>(ModelOverrideType.class);
		Map<String, TextureResource> providedTextures = new HashMap<>();
		if (NBTEditor.contains(item, "CustomModelData")) {
			int customModelData = NBTEditor.getInt(item, "CustomModelData");
			predicates.put(ModelOverrideType.CUSTOM_MODEL_DATA, (float) customModelData);
		}
		if (xMaterial.equals(XMaterial.CHEST) || xMaterial.equals(XMaterial.TRAPPED_CHEST) || xMaterial.equals(XMaterial.ENDER_CHEST)) {
			LocalDate time = LocalDate.now();
			if (time.getMonth().equals(Month.DECEMBER) && (time.getDayOfMonth() == 24 || time.getDayOfMonth() == 25 || time.getDayOfMonth() == 26)) {
				directLocation = ResourceRegistry.BUILTIN_ENTITY_LOCATION + "christmas_chest";
			}
		} else if (xMaterial.isOneOf(Arrays.asList("CONTAINS:Banner"))) {
			BannerAssetResult bannerAsset = BannerGraphics.generateBannerAssets(item);
			providedTextures.put(ResourceRegistry.BANNER_BASE_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(bannerAsset.getBase()));
			providedTextures.put(ResourceRegistry.BANNER_PATTERNS_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(bannerAsset.getPatterns()));
		} else if (xMaterial.equals(XMaterial.SHIELD)) {
			BannerAssetResult shieldAsset = BannerGraphics.generateShieldAssets(item);
			providedTextures.put(ResourceRegistry.SHIELD_BASE_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(shieldAsset.getBase()));
			providedTextures.put(ResourceRegistry.SHIELD_PATTERNS_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(shieldAsset.getPatterns()));
		} else if (xMaterial.equals(XMaterial.PLAYER_HEAD)) {
			providedTextures.put(ResourceRegistry.SKIN_TEXTURE_PLACEHOLDER, InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ENTITY_LOCATION + "steve"));
			try {
				String base64 = SkinUtils.getSkinValue(item.getItemMeta());
				if (base64 != null) {
					JSONObject json = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(base64)));
					String value = (String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url");
					BufferedImage skinImage = ImageUtils.downloadImage(value);
					providedTextures.put(ResourceRegistry.SKIN_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(skinImage));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (xMaterial.equals(XMaterial.ELYTRA)) {
			int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
			if (durability <= 1) {
				predicates.put(ModelOverrideType.BROKEN, 1F);
			}
		} else if (xMaterial.equals(XMaterial.CROSSBOW)) {
			CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
			List<ItemStack> charged = meta.getChargedProjectiles();
			if (charged != null && !charged.isEmpty()) {
				predicates.put(ModelOverrideType.CHARGED, 1F);
				ItemStack charge = charged.get(0);
				XMaterial chargeType = XMaterialUtils.matchXMaterial(charge);
				if (chargeType.equals(XMaterial.FIREWORK_ROCKET)) {
					predicates.put(ModelOverrideType.FIREWORK, 1F);
				}
			}
		} else if (xMaterial.equals(XMaterial.CLOCK)) {
			long time = ((player instanceof ICPlayer && ((ICPlayer) player).isLocal() ? ((ICPlayer) player).getLocalPlayer().getPlayerTime() : Bukkit.getWorlds().get(0).getTime()) % 24000) - 6000;
			if (time < 0) {
				time += 24000;
			}
			double timePhase = (double) time / 24000;
			predicates.put(ModelOverrideType.TIME, (float) (timePhase - 0.0078125));
		} else if (xMaterial.equals(XMaterial.COMPASS)) {
			double angle;
			ICPlayer icplayer;
			if (player instanceof ICPlayer && (icplayer = (ICPlayer) player).isLocal()) {
				if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16)) {
					CompassMeta meta = (CompassMeta) item.getItemMeta();
					Location target;
					if (meta.hasLodestone()) {
						Location lodestone = meta.getLodestone();
						target = new Location(lodestone.getWorld(), lodestone.getBlockX() + 0.5, lodestone.getBlockY(), lodestone.getBlockZ() + 0.5, lodestone.getYaw(), lodestone.getPitch());
						requiresEnchantmentGlint = true;
					} else if (icplayer.getLocalPlayer().getWorld().getEnvironment().equals(Environment.NORMAL)) {
						Location spawn = icplayer.getLocalPlayer().getWorld().getSpawnLocation();
						target = new Location(spawn.getWorld(), spawn.getBlockX() + 0.5, spawn.getBlockY(), spawn.getBlockZ() + 0.5, spawn.getYaw(), spawn.getPitch());
					} else {
						target = null;
					}
					if (target != null && target.getWorld().equals(icplayer.getLocalPlayer().getWorld())) {
						Location playerLocation = icplayer.getLocalPlayer().getEyeLocation();
						playerLocation.setPitch(0);
						Vector looking = playerLocation.getDirection();
						Vector pointing = target.toVector().subtract(playerLocation.toVector());
						pointing.setY(0);
						double degree = VectorUtils.getBearing(looking, pointing);
						if (degree < 0) {
							degree += 360;
						}
						angle = degree / 360;
					} else {
						angle = RANDOM.nextDouble();
					}
				} else {
					if (icplayer.getLocalPlayer().getWorld().getEnvironment().equals(Environment.NORMAL)) {
						Location spawn = icplayer.getLocalPlayer().getWorld().getSpawnLocation();
						Location target = new Location(spawn.getWorld(), spawn.getBlockX() + 0.5, spawn.getBlockY(), spawn.getBlockZ() + 0.5, spawn.getYaw(), spawn.getPitch());
						Location playerLocation = icplayer.getLocalPlayer().getEyeLocation();
						playerLocation.setPitch(0);
						Vector looking = playerLocation.getDirection();
						Vector pointing = target.toVector().subtract(playerLocation.toVector());
						pointing.setY(0);
						double degree = VectorUtils.getBearing(looking, pointing);
						if (degree < 0) {
							degree += 360;
						}
						angle = degree / 360;
					} else {
						angle = RANDOM.nextDouble();
					}
				}
			} else {
				if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16)) {
					CompassMeta meta = (CompassMeta) item.getItemMeta();
					if (meta.hasLodestone()) {
						requiresEnchantmentGlint = true;
					}
				}
				angle = 0;
			}
			
			predicates.put(ModelOverrideType.ANGLE, (float) (angle - 0.015625));
		} else if (xMaterial.equals(XMaterial.LIGHT)) {
			int level = 15;
			Object blockStateObj = item.getItemMeta().serialize().get("BlockStateTag");
			if (blockStateObj != null && blockStateObj instanceof Map) {
				@SuppressWarnings("unchecked")
				Object levelObj = ((Map<?, Object>) blockStateObj).get("level");
				if (levelObj != null) {
					try {
						level = Integer.parseInt(levelObj.toString().replace("i", ""));
					} catch (NumberFormatException e) {}
				}
			}
			predicates.put(ModelOverrideType.LEVEL, (float) level / 16F);
		} else if (item.getItemMeta() instanceof LeatherArmorMeta) {
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			Color color = new Color(meta.getColor().asRGB());
			if (xMaterial.equals(XMaterial.LEATHER_HORSE_ARMOR)) {
				BufferedImage itemImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_LOCATION + xMaterial.name().toLowerCase()).getTexture(32, 32);
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(itemImage), color);
				itemImage = ImageUtils.multiply(itemImage, colorOverlay);
				providedTextures.put(ResourceRegistry.LEATHER_HORSE_ARMOR_PLACEHOLDER, new GeneratedTextureResource(itemImage));
			} else {
				BufferedImage itemImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_LOCATION + xMaterial.name().toLowerCase()).getTexture(32, 32);
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(itemImage), color);
				itemImage = ImageUtils.multiply(itemImage, colorOverlay);
				if (xMaterial.name().contains("HELMET")) {
					providedTextures.put(ResourceRegistry.LEATHER_HELMET_PLACEHOLDER, new GeneratedTextureResource(itemImage));
				} else if (xMaterial.name().contains("CHESTPLATE")) {
					providedTextures.put(ResourceRegistry.LEATHER_CHESTPLATE_PLACEHOLDER, new GeneratedTextureResource(itemImage));
				} else if (xMaterial.name().contains("LEGGINGS")) {
					providedTextures.put(ResourceRegistry.LEATHER_LEGGINGS_PLACEHOLDER, new GeneratedTextureResource(itemImage));
				} else if (xMaterial.name().contains("BOOTS")) {
					providedTextures.put(ResourceRegistry.LEATHER_BOOTS_PLACEHOLDER, new GeneratedTextureResource(itemImage));
				}
			}
		} else if (item.getItemMeta() instanceof PotionMeta) {
			if (xMaterial.equals(XMaterial.TIPPED_ARROW)) {
				PotionMeta meta = (PotionMeta) item.getItemMeta();
				PotionType potiontype = InteractiveChat.version.isOld() ? Potion.fromItemStack(item).getType() : meta.getBasePotionData().getType();
				BufferedImage tippedArrowHead = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_LOCATION + "tipped_arrow_head").getTexture(32, 32);
				
				Color color;
				try {
					if (meta.hasColor()) {
						color = new Color(meta.getColor().asRGB());
					} else {
						color = PotionUtils.getPotionBaseColor(potiontype);
					}
				} catch (Throwable e) {
					color = PotionUtils.getPotionBaseColor(PotionType.WATER);
				}
				
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(tippedArrowHead), color);
				tippedArrowHead = ImageUtils.multiply(tippedArrowHead, colorOverlay);
				
				providedTextures.put(ResourceRegistry.TIPPED_ARROW_HEAD_PLACEHOLDER, new GeneratedTextureResource(tippedArrowHead));
			} else {
				PotionMeta meta = (PotionMeta) item.getItemMeta();
				PotionType potiontype = InteractiveChat.version.isOld() ? Potion.fromItemStack(item).getType() : meta.getBasePotionData().getType();
				BufferedImage potionOverlay = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_LOCATION + "potion_overlay").getTexture(32, 32);
				
				Color color;
				try {
					if (meta.hasColor()) {
						color = new Color(meta.getColor().asRGB());
					} else {
						color = PotionUtils.getPotionBaseColor(potiontype);
					}
				} catch (Throwable e) {
					color = PotionUtils.getPotionBaseColor(PotionType.WATER);
				}
				
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(potionOverlay), color);
				potionOverlay = ImageUtils.multiply(potionOverlay, colorOverlay);
				
				providedTextures.put(ResourceRegistry.POTION_OVERLAY_PLACEHOLDER, new GeneratedTextureResource(potionOverlay));
				if (potiontype != null) {
					if (!(potiontype.name().equals("WATER") || potiontype.name().equals("AWKWARD") || potiontype.name().equals("MUNDANE") || potiontype.name().equals("THICK") || potiontype.name().equals("UNCRAFTABLE"))) {
						requiresEnchantmentGlint = true;
					}
				}
			}
		}
		return new ItemStackProcessResult(requiresEnchantmentGlint, predicates, providedTextures, directLocation);
	}
	
	public static class ItemStackProcessResult {
		
		private boolean requiresEnchantmentGlint;
		private Map<ModelOverrideType, Float> predicates;
		private Map<String, TextureResource> providedTextures;
		private String directLocation;
		
		public ItemStackProcessResult(boolean requiresEnchantmentGlint, Map<ModelOverrideType, Float> predicates, Map<String, TextureResource> providedTextures, String directLocation) {
			this.requiresEnchantmentGlint = requiresEnchantmentGlint;
			this.predicates = predicates;
			this.providedTextures = providedTextures;
			this.directLocation = directLocation;
		}
		
		public boolean isRequiresEnchantmentGlint() {
			return requiresEnchantmentGlint;
		}
		
		public Map<ModelOverrideType, Float> getPredicates() {
			return predicates;
		}
		
		public Map<String, TextureResource> getProvidedTextures() {
			return providedTextures;
		}

		public String getDirectLocation() {
			return directLocation;
		}
		
	}

}
