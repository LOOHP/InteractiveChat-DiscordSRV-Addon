package com.loohp.interactivechatdiscordsrvaddon.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapPalette;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.io.github.bananapuncher714.nbteditor.NBTEditor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.libs.org.json.simple.parser.ParseException;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechat.objectholders.ValueTrios;
import com.loohp.interactivechat.utils.ComponentStyling;
import com.loohp.interactivechat.utils.CustomStringUtils;
import com.loohp.interactivechat.utils.FilledMapUtils;
import com.loohp.interactivechat.utils.HTTPRequestUtils;
import com.loohp.interactivechat.utils.HashUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.ItemNBTUtils;
import com.loohp.interactivechat.utils.ItemStackUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.SkinUtils;
import com.loohp.interactivechat.utils.XMaterialUtils;
import com.loohp.interactivechatdiscordsrvaddon.Cache;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.graphics.BannerGraphics.BannerAssetResult;
import com.loohp.interactivechatdiscordsrvaddon.registies.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resource.ModelRenderer.RenderResult;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.resource.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resource.texture.GeneratedTextureResource;
import com.loohp.interactivechatdiscordsrvaddon.resource.texture.TextureResource;
import com.loohp.interactivechatdiscordsrvaddon.utils.PotionUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.VectorUtils;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.ItemMapWrapper;

@SuppressWarnings("deprecation")
public class ImageGeneration {
	
	private static final String OPTIFINE_CAPE_URL = "https://optifine.net/capes/%s.png";
	private static final String TEXTURE_MINECRAFT_URL = "http://textures.minecraft.net/texture/";
	private static final String PLAYER_RENDER_URL = "https://mc-heads.net/player/%s/64";
	private static final String HEAD_2D_RENDER_URL = "https://mc-heads.net/avatar/%s/%s";
	private static final String PLAYER_INFO_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s";
	
	private static final int MAP_ICON_PER_ROLE = 16;
	private static final int SPACING = 36;
	
	private static final double ITEM_AMOUNT_TEXT_DARKEN_FACTOR = 75.0 / 255.0;
	
	private static final double ENCHANTMENT_GLINT_FACTOR = 190.0 / 255.0;
	
	private static final Color MISSING_TEXTURE_0 = new Color(0, 0, 0);
	private static final Color MISSING_TEXTURE_1 = new Color(248, 0, 248);
	
	private static final String PLAYER_CAPE_KEY = "PlayerCapeTexture";
	private static final String FULL_BODY_IMAGE_KEY = "FullBodyImage";
	private static final String PLAYER_HEAD_2D_KEY = "PlayerHead2DImage";
	private static final String INVENTORY_KEY = "Inventory";
	private static final String PLAYER_INVENTORY_KEY = "PlayerInventory";
	
	private static final int TABLIST_SINGLE_COLUMN_LIMIT = 10;
	private static final Color TABLIST_BACKGROUND = new Color(68, 71, 68);
	private static final Color TABLIST_PLAYER_BACKGROUND = new Color(91, 94, 91);

	private static final Random RANDOM = new Random();
	
	public static BufferedImage getMissingImage(int width, int length) {
		Debug.debug("ImageGeneration creating missing texture image");
		BufferedImage image = new BufferedImage(width, length, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(MISSING_TEXTURE_0);
		g.fillRect(0, 0, width, length);
		g.setColor(MISSING_TEXTURE_1);
		g.fillRect(0, 0, width / 2, length / 2);
		g.fillRect(width / 2, length / 2, width / 2, length / 2);
		g.dispose();
		return image;
	}
	
	public static BufferedImage getItemStackImage(ItemStack item, OfflineICPlayer player) throws IOException {
		return getItemStackImage(item, player, false);
	}
	
	public static BufferedImage getItemStackImage(ItemStack item, OfflineICPlayer player, boolean alternateAir) throws IOException {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		Debug.debug("ImageGeneration creating item stack image " + item);
		
		BufferedImage background = new BufferedImage(36, 36, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = background.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		
		BufferedImage itemImage;
		if (item == null || item.getType().equals(Material.AIR)) {
			if (alternateAir) {
				itemImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_BLOCK_LOCATION + "air_alternate").getTexture(32, 32);
			} else {
				g.dispose();
				return background;
			}
		} else {
			itemImage = getRawItemImage(item, player);
		}
		
		if (itemImage != null) {
			g.drawImage(itemImage, 0, 0, null);
		}
		g.dispose();
		
		return background;
	}
	
	public static BufferedImage getInventoryImage(Inventory inventory, OfflineICPlayer player) throws Exception {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		InteractiveChatDiscordSrvAddon.plugin.inventoryImageCounter.incrementAndGet();		
		Debug.debug("ImageGeneration creating inventory image of " + player.getName());
		
		String key = INVENTORY_KEY + HashUtils.createSha1("Inventory", inventory);
		if (!inventory.contains(XMaterial.COMPASS.parseMaterial()) && !inventory.contains(XMaterial.CLOCK.parseMaterial()) && !Stream.of(inventory.getContents()).noneMatch(each -> each != null && NBTEditor.contains(each, "CustomModelData"))) {
			Cache<?> cache = Cache.getCache(key);
			if (cache != null) {
				return ImageUtils.copyImage((BufferedImage) cache.getObject());
			}
		}
		
		int rows = inventory.getSize() / 9;
		BufferedImage background = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_GUI_LOCATION + rows + "_rows").getTexture();
		
		BufferedImage target = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = target.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(background, 0, 0, null);
		
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null || item.getType().equals(Material.AIR)) {
				continue;
			}
			
			BufferedImage itemImage = getRawItemImage(item, player);
			
			if (itemImage != null) {
				g.drawImage(itemImage, 18 + (SPACING * (i % 9)), 18 + (SPACING * (i / 9)), null);
			}
		}
		g.dispose();
		
		Cache.putCache(key, target, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
		
		return target;
	}
	
	public static BufferedImage getPlayerInventoryImage(Inventory inventory, OfflineICPlayer player) throws Exception {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		InteractiveChatDiscordSrvAddon.plugin.inventoryImageCounter.incrementAndGet();
		Debug.debug("ImageGeneration creating player inventory image of " + player.getName());
		
		String key = PLAYER_INVENTORY_KEY + HashUtils.createSha1(player.getUniqueId().toString(), inventory);
		if (!inventory.contains(XMaterial.COMPASS.parseMaterial()) && !inventory.contains(XMaterial.CLOCK.parseMaterial()) && !Stream.of(inventory.getContents()).noneMatch(each -> each != null && NBTEditor.contains(each, "CustomModelData"))) {
			Cache<?> cache = Cache.getCache(key);
			if (cache != null) {
				return ImageUtils.copyImage((BufferedImage) cache.getObject());
			}
		}
		
		BufferedImage background;
		if (player.getName().equalsIgnoreCase("LOOHP") || player.getName().equalsIgnoreCase("NARLIAR")) {
			try {
				background = ImageIO.read(new ByteArrayInputStream(InteractiveChatDiscordSrvAddon.plugin.getExtras("player_inventory_painting")));
			} catch (Throwable e) {
				background = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_GUI_LOCATION + "player_inventory").getTexture();
			}
		} else {
			background = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_GUI_LOCATION + "player_inventory").getTexture();
		}
		
		BufferedImage target = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = target.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(background, 0, 0, null);
		
		int i = 0;
		//hotbar
		for (; i < 9; i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null || item.getType().equals(Material.AIR)) {
				continue;
			}
			
			BufferedImage itemImage = getRawItemImage(item, player);
			
			if (itemImage != null) {
				g.drawImage(itemImage, 18 + (SPACING * (i % 9)), 286 + (SPACING * (i / 9)), null);
			}
		}
		
		//inv
		for (; i < 36; i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null || item.getType().equals(Material.AIR)) {
				continue;
			}
			
			BufferedImage itemImage = getRawItemImage(item, player);
			
			if (itemImage != null) {
				g.drawImage(itemImage, 18 + (SPACING * (i % 9)), 170 + (SPACING * ((i - 9) / 9)), null);
			}
		}
		
		//boots
		ItemStack boots = inventory.getItem(i);
		if (boots == null || boots.getType().equals(Material.AIR)) {
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_LOCATION + "empty_armor_slot_boots").getTexture(32, 32), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
		} else {
			BufferedImage itemImage = getRawItemImage(boots, player);			
			if (itemImage != null) {
				g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
			}
		}
		i++;
		
		//leggings
		ItemStack leggings = inventory.getItem(i);
		if (leggings == null || leggings.getType().equals(Material.AIR)) {
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_LOCATION + "empty_armor_slot_leggings").getTexture(32, 32), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
		} else {
			BufferedImage itemImage = getRawItemImage(leggings, player);			
			if (itemImage != null) {
				g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
			}
		}
		i++;
		
		//chestplate
		ItemStack chestplate = inventory.getItem(i);
		if (chestplate == null || chestplate.getType().equals(Material.AIR)) {
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_LOCATION + "empty_armor_slot_chestplate").getTexture(32, 32), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
		} else {
			BufferedImage itemImage = getRawItemImage(chestplate, player);			
			if (itemImage != null) {
				g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
			}
		}
		i++;
		
		//helmet
		ItemStack helmet = inventory.getItem(i);
		if (helmet == null || helmet.getType().equals(Material.AIR)) {
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_LOCATION + "empty_armor_slot_helmet").getTexture(32, 32), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
		} else {
			BufferedImage itemImage = getRawItemImage(helmet, player);			
			if (itemImage != null) {
				g.drawImage(itemImage, 18, 126 - (SPACING * (i - 36)), null);
			}
		}
		i++;
		
		//offhand
		ItemStack offhand = inventory.getItem(i);
		if (offhand == null || offhand.getType().equals(Material.AIR)) {
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_LOCATION + "empty_armor_slot_shield").getTexture(32, 32), 162, 126, 32, 32, null);
		} else {				
			BufferedImage itemImage = getRawItemImage(offhand, player);				
			if (itemImage != null) {
				g.drawImage(itemImage, 162, 126, null);
			}
		}
		
		//puppet
		EntityEquipment equipment = player.getEquipment();
		BufferedImage puppet = getFullBodyImage(player, equipment.getHelmet(), equipment.getChestplate(), equipment.getLeggings(), equipment.getBoots());
		g.drawImage(puppet, 65, -10, null);
		
		g.dispose();
		
		Cache.putCache(key, target, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
		
		return target;
	}
	
	private static BufferedImage getFullBodyImage(OfflineICPlayer player, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		Debug.debug("ImageGeneration creating puppet image of " + player.getName());
		
		BufferedImage image;
		BufferedImage cape;
		try {
			JSONObject json;
			if (player instanceof ICPlayer && ((ICPlayer) player).isLocal()) {
				json = (JSONObject) new JSONParser().parse(SkinUtils.getSkinJsonFromProfile(((ICPlayer) player).getLocalPlayer()));
			} else {
				json = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(((JSONObject) ((JSONArray) HTTPRequestUtils.getJSONResponse(PLAYER_INFO_URL.replace("%s", player.getUniqueId().toString())).get("properties")).get(0)).get("value").toString())));
			}
			if (json == null) {
				cape = null;
				image = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_PUPPET_LOCATION + "default").getTexture();
			} else {
				try {
					if (((JSONObject) json.get("textures")).containsKey("CAPE")) {
						String url = (String) ((JSONObject) ((JSONObject) json.get("textures")).get("CAPE")).get("url");
						Cache<?> cache = Cache.getCache(player.getUniqueId().toString() + url + PLAYER_CAPE_KEY);
						if (cache == null) {
							cape = ImageUtils.downloadImage(url);
							Cache.putCache(player.getUniqueId().toString() + url + PLAYER_CAPE_KEY, cape, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
						} else {
							cape = (BufferedImage) cache.getObject();
						}
					} else {
						String url = OPTIFINE_CAPE_URL.replaceAll("%s", CustomStringUtils.escapeReplaceAllMetaCharacters(player.getName()));
						Cache<?> cache = Cache.getCache(player.getUniqueId().toString() + url + PLAYER_CAPE_KEY);
						if (cache == null) {
							try {
								cape = ImageUtils.downloadImage(url);
								Cache.putCache(player.getUniqueId().toString() + url + PLAYER_CAPE_KEY, cape, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
							} catch (Throwable ignore) {
								cape = null;
							}
						} else {
							cape = (BufferedImage) cache.getObject();
						}
					}
				} catch (Throwable e) {
					cape = null;
				}
				
				try {
					String value = ((String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url")).replace(TEXTURE_MINECRAFT_URL, "");
					boolean slim = false;
					if (((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).containsKey("metadata")) {
						slim = ((JSONObject) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("metadata")).get("model").toString().equals("slim");
					}
					Cache<?> cache = Cache.getCache(player.getUniqueId().toString() + value + FULL_BODY_IMAGE_KEY);
					if (cache == null) {
						String url = PLAYER_RENDER_URL.replaceFirst("%s", value);
						image = ImageUtils.downloadImage(url);
						Cache.putCache(player.getUniqueId().toString() + value + FULL_BODY_IMAGE_KEY, image, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
					} else {
						image = (BufferedImage) cache.getObject();
					}
					image = ImageUtils.copyImage(image);
					if (slim) {
						BufferedImage rightArm = ImageUtils.copyAndGetSubImage(image, 0, 32, 12, 48);
						BufferedImage leftArm = ImageUtils.copyAndGetSubImage(image, 52, 32, 12, 48);
						Color color = new Color(0, 0, 0, 255);
						for (int x = 0; x < 16; x++) {
							for (int y = 32; y < 80; y++) {
								image.setRGB(x, y, color.getRGB());
							}
						}
						for (int x = 48; x < 64; x++) {
							for (int y = 32; y < 80; y++) {
								image.setRGB(x, y, color.getRGB());
							}
						}
						Graphics2D g = image.createGraphics();
						g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
						g.drawImage(rightArm, 4, 32, null);
						g.drawImage(leftArm, 48, 32, null);
						g.dispose();
					}
				} catch (Throwable e1) {
					image = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_PUPPET_LOCATION + "default").getTexture();
				}
			}
		} catch (Exception e) {
			cape = null;
			image = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_PUPPET_LOCATION + "default").getTexture();
		}
		
		image = ImageUtils.expandCenterAligned(ImageUtils.multiply(image, 0.7), 36, 4, 6, 6);
		
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		
		if (ItemStackUtils.isWearable(leggings)) {
			XMaterial type = XMaterialUtils.matchXMaterial(leggings);
			BufferedImage leggingsImage = null;
			BufferedImage leggingsImage1 = null;
			BufferedImage leggingsImage2 = null;
			int scale = 1;
			switch (type) {
			case LEATHER_LEGGINGS:
				leggingsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "leather_layer_2").getTexture();
				scale = (leggingsImage.getWidth() / 8) / 8;
				LeatherArmorMeta meta = (LeatherArmorMeta) leggings.getItemMeta();
				Color color = new Color(meta.getColor().asRGB());
				BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "leather_layer_2_overlay").getTexture();
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(leggingsImage), color);
				leggingsImage = ImageUtils.multiply(leggingsImage, colorOverlay);
				
				Graphics2D g2 = leggingsImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(armorOverlay, 0, 0, null);
				g2.dispose();
				break;
			case CHAINMAIL_LEGGINGS:
				leggingsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "chainmail_layer_2").getTexture();
				scale = (leggingsImage.getWidth() / 8) / 8;
				break;
			case GOLDEN_LEGGINGS:
				leggingsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "gold_layer_2").getTexture();
				scale = (leggingsImage.getWidth() / 8) / 8;
				break;
			case IRON_LEGGINGS:
				leggingsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "iron_layer_2").getTexture();
				scale = (leggingsImage.getWidth() / 8) / 8;
				break;
			case DIAMOND_LEGGINGS:
				leggingsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "diamond_layer_2").getTexture();
				scale = (leggingsImage.getWidth() / 8) / 8;
				break;
			case NETHERITE_LEGGINGS:
				leggingsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "netherite_layer_2").getTexture();
				scale = (leggingsImage.getWidth() / 8) / 8;
				break;
			default:
				break;
			}
			leggingsImage1 = ImageUtils.copyAndGetSubImage(leggingsImage, scale * 20, scale * 27, 8 * scale, 5 * scale);
			BufferedImage leggingsLeft = ImageUtils.copyAndGetSubImage(leggingsImage, scale * 4, scale * 20, 4 * scale, 9 * scale);
			BufferedImage leggingsRight = ImageUtils.flipHorizontal(leggingsLeft);
			leggingsImage2 = new BufferedImage(leggingsLeft.getWidth() + leggingsRight.getWidth(), leggingsLeft.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = leggingsImage2.createGraphics();
			g2.drawImage(leggingsLeft, 0, 0, null);
			g2.drawImage(leggingsRight, leggingsLeft.getWidth(), 0, null);
			g2.dispose();
			
			leggingsImage1 = ImageUtils.multiply(ImageUtils.resizeImageStretch(ImageUtils.resizeImage(leggingsImage1, Math.pow(scale, -1) * 4), 4), 0.7);
			leggingsImage2 = ImageUtils.multiply(ImageUtils.resizeImageStretch(ImageUtils.resizeImage(leggingsImage2, Math.pow(scale, -1) * 4), 4), 0.7);
			if (leggings.getEnchantments().size() > 0) {
				BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.MISC_LOCATION + "enchanted_item_glint").getTexture();
				BufferedImage tintImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);				
				Graphics2D g3 = tintImage.createGraphics();
				g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g3.drawImage(tint_ori, 0, 0, 512, 512, null);
				g3.dispose();
				
				leggingsImage1 = ImageUtils.additionNonTransparent(leggingsImage1, tintImage, ENCHANTMENT_GLINT_FACTOR);
				leggingsImage2 = ImageUtils.additionNonTransparent(leggingsImage2, tintImage, ENCHANTMENT_GLINT_FACTOR);
			}
			
			g.drawImage(leggingsImage1, 20, 98, 36, 24, null);
			g.drawImage(leggingsImage2, 20, 114, 36, 30, null);
		}
		
		if (ItemStackUtils.isWearable(boots)) {
			XMaterial type = XMaterialUtils.matchXMaterial(boots);
			BufferedImage bootsImage = null;
			int scale = 1;
			switch (type) {
			case LEATHER_BOOTS:
				bootsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "leather_layer_1").getTexture();
				scale = (bootsImage.getWidth() / 8) / 8;
				LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
				Color color = new Color(meta.getColor().asRGB());
				BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "leather_layer_1_overlay").getTexture();
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(bootsImage), color);
				
				bootsImage = ImageUtils.multiply(bootsImage, colorOverlay);
				
				Graphics2D g2 = bootsImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(armorOverlay, 0, 0, null);
				g2.dispose();
				break;
			case CHAINMAIL_BOOTS:
				bootsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "chainmail_layer_1").getTexture();
				scale = (bootsImage.getWidth() / 8) / 8;
				break;
			case GOLDEN_BOOTS:
				bootsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "gold_layer_1").getTexture();
				scale = (bootsImage.getWidth() / 8) / 8;
				break;
			case IRON_BOOTS:
				bootsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "iron_layer_1").getTexture();
				scale = (bootsImage.getWidth() / 8) / 8;
				break;
			case DIAMOND_BOOTS:
				bootsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "diamond_layer_1").getTexture();
				scale = (bootsImage.getWidth() / 8) / 8;
				break;
			case NETHERITE_BOOTS:
				bootsImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "netherite_layer_1").getTexture();
				scale = (bootsImage.getWidth() / 8) / 8;
				break;
			default:
				break;
			}
			BufferedImage bootsLeft = ImageUtils.copyAndGetSubImage(bootsImage, 4 * scale, 26 * scale, 4 * scale, 4 * scale);
			BufferedImage bootsRight = ImageUtils.flipHorizontal(bootsLeft);
			bootsImage = new BufferedImage(bootsLeft.getWidth() + bootsRight.getWidth(), bootsLeft.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = bootsImage.createGraphics();
			g2.drawImage(bootsLeft, 0, 0, null);
			g2.drawImage(bootsRight, bootsLeft.getWidth(), 0, null);
			g2.dispose();
			
			bootsImage = ImageUtils.multiply(ImageUtils.resizeImageStretch(ImageUtils.resizeImage(bootsImage, Math.pow(scale, -1) * 4), 8), 0.7);
			if (boots.getEnchantments().size() > 0) {
				BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.MISC_LOCATION + "enchanted_item_glint").getTexture();
				BufferedImage tintImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);				
				Graphics2D g3 = tintImage.createGraphics();
				g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g3.drawImage(tint_ori, 0, 0, 512, 512, null);
				g3.dispose();
				
				bootsImage = ImageUtils.additionNonTransparent(bootsImage, tintImage, ENCHANTMENT_GLINT_FACTOR);
			}

			g.drawImage(bootsImage, 18, 144, 40, 32, null);
		}
		
		if (ItemStackUtils.isWearable(chestplate)) {
			XMaterial type = XMaterialUtils.matchXMaterial(chestplate);
			BufferedImage chestplateImage = null;
			BufferedImage chestplateImage1 = null;
			BufferedImage chestplateImage2 = null;
			BufferedImage chestplateImage3 = null;
			boolean isArmor = true;
			int scale = 1;
			switch (type) {
			case LEATHER_CHESTPLATE:
				chestplateImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "leather_layer_1").getTexture();
				scale = (chestplateImage.getWidth() / 8) / 8;
				LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
				Color color = new Color(meta.getColor().asRGB());
				BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "leather_layer_1_overlay").getTexture();
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(chestplateImage), color);
				chestplateImage = ImageUtils.multiply(chestplateImage, colorOverlay);
				
				Graphics2D g2 = chestplateImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(armorOverlay, 0, 0, null);
				g2.dispose();
				break;
			case CHAINMAIL_CHESTPLATE:
				chestplateImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "chainmail_layer_1").getTexture();
				scale = (chestplateImage.getWidth() / 8) / 8;
				break;
			case GOLDEN_CHESTPLATE:
				chestplateImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "gold_layer_1").getTexture();
				scale = (chestplateImage.getWidth() / 8) / 8;
				break;
			case IRON_CHESTPLATE:
				chestplateImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "iron_layer_1").getTexture();
				scale = (chestplateImage.getWidth() / 8) / 8;
				break;
			case DIAMOND_CHESTPLATE:
				chestplateImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "diamond_layer_1").getTexture();
				scale = (chestplateImage.getWidth() / 8) / 8;
				break;
			case NETHERITE_CHESTPLATE:
				chestplateImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "netherite_layer_1").getTexture();
				scale = (chestplateImage.getWidth() / 8) / 8;
				break;
			case ELYTRA:
				isArmor = false;
				chestplateImage = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
				BufferedImage wing = cape == null ? InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ENTITY_LOCATION + "elytra").getTexture() : cape;
				if (wing.getWidth() % 64 != 0 || wing.getHeight() % 32 != 0) {
					int w = 0;
					int h = 0;
					while (w < wing.getWidth()) {
						w += 64;
						h += 32;
					}
					BufferedImage resize = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g3 = resize.createGraphics();
					g3.drawImage(wing, 0, 0, null);
					g3.dispose();
					wing = resize;
				}
				scale = wing.getWidth() / 64;
				wing = ImageUtils.copyAndGetSubImage(wing, 34 * scale, 2 * scale, 12 * scale, 20 * scale);
				wing = ImageUtils.multiply(ImageUtils.resizeImage(wing, Math.pow(scale, -1) * 3.75), 0.7);
				BufferedImage leftWing = ImageUtils.rotateImageByDegrees(wing, 23.41);
				Graphics2D g3 = chestplateImage.createGraphics();
				g3.drawImage(leftWing, 0, 0, null);
				wing = ImageUtils.flipHorizontal(wing);
				BufferedImage rightWing = ImageUtils.rotateImageByDegrees(wing, 360.0 - 23.41);
				g3.drawImage(rightWing, 26, 0, null);
				g3.dispose();
				
				if (chestplate.getEnchantments().size() > 0) {
					BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.MISC_LOCATION + "enchanted_item_glint").getTexture();
					BufferedImage tintImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g4 = tintImage.createGraphics();
					g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
					g4.drawImage(tint_ori, 0, 0, 512, 512, null);
					g4.dispose();
					
					chestplateImage = ImageUtils.additionNonTransparent(chestplateImage, tintImage, ENCHANTMENT_GLINT_FACTOR);
				}
				
				ImageUtils.drawTransparent(image, chestplateImage, -10, 58);
			default:
				break;
			}
			if (isArmor) {
				chestplateImage1 = ImageUtils.copyAndGetSubImage(chestplateImage, scale * 20, scale * 20, 8 * scale, 12 * scale);				
				chestplateImage2 = ImageUtils.copyAndGetSubImage(chestplateImage, scale * 44, scale * 20, 4 * scale, 12 * scale);
				chestplateImage3 = ImageUtils.flipHorizontal(chestplateImage2);
				
				chestplateImage1 = ImageUtils.multiply(ImageUtils.resizeImageStretch(ImageUtils.resizeImage(chestplateImage1, Math.pow(scale, -1) * 4), 8), 0.7);
				chestplateImage2 = ImageUtils.multiply(ImageUtils.resizeImageStretch(ImageUtils.resizeImage(chestplateImage2, Math.pow(scale, -1) * 4), 8), 0.7);
				chestplateImage3 = ImageUtils.multiply(ImageUtils.resizeImageStretch(ImageUtils.resizeImage(chestplateImage3, Math.pow(scale, -1) * 4), 8), 0.7);
				
				if (chestplate.getEnchantments().size() > 0) {
					BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.MISC_LOCATION + "enchanted_item_glint").getTexture();
					BufferedImage tintImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);				
					Graphics2D g2 = tintImage.createGraphics();
					g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
					g2.drawImage(tint_ori, 0, 0, 512, 512, null);
					g2.dispose();
					
					chestplateImage1 = ImageUtils.additionNonTransparent(chestplateImage1, tintImage, ENCHANTMENT_GLINT_FACTOR);
					chestplateImage2 = ImageUtils.additionNonTransparent(chestplateImage2, tintImage, ENCHANTMENT_GLINT_FACTOR);
					chestplateImage3 = ImageUtils.additionNonTransparent(chestplateImage3, tintImage, ENCHANTMENT_GLINT_FACTOR);
				}
				
				g.drawImage(chestplateImage1, 18, 64, 40, 56, null);
				g.drawImage(chestplateImage2, 2, 64, 24, 56, null);
				g.drawImage(chestplateImage3, 50, 64, 24, 56, null);
			}
		}
		
		if (ItemStackUtils.isWearable(helmet)) {
			XMaterial type = XMaterialUtils.matchXMaterial(helmet);
			BufferedImage helmetImage = null;
			int scale = 1;
			int offsetX = 0;
			int offsetY = 0;
			boolean shouldResize = true;
			boolean isArmor = true;
			boolean skip = false;
			switch (type) {
			case LEATHER_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "leather_layer_1").getTexture();
				scale = (helmetImage.getWidth() / 8) / 8;
				LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
				Color color = new Color(meta.getColor().asRGB());
				BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "leather_layer_1_overlay").getTexture();
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(helmetImage), color);
				
				helmetImage = ImageUtils.multiply(helmetImage, colorOverlay);
				
				Graphics2D g2 = helmetImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(armorOverlay, 0, 0, null);
				g2.dispose();
				break;
			case CHAINMAIL_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "chainmail_layer_1").getTexture();
				scale = (helmetImage.getWidth() / 8) / 8;
				break;
			case GOLDEN_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "gold_layer_1").getTexture();
				scale = (helmetImage.getWidth() / 8) / 8;
				break;
			case IRON_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "iron_layer_1").getTexture();
				scale = (helmetImage.getWidth() / 8) / 8;
				break;
			case DIAMOND_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "diamond_layer_1").getTexture();
				scale = (helmetImage.getWidth() / 8) / 8;
				break;
			case NETHERITE_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "netherite_layer_1").getTexture();
				scale = (helmetImage.getWidth() / 8) / 8;
				break;
			case TURTLE_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ARMOR_LOCATION + "turtle_layer_1").getTexture();
				scale = (helmetImage.getWidth() / 8) / 8;
				break;
			case PLAYER_HEAD:
				try {
					String base64 = SkinUtils.getSkinValue(helmet.getItemMeta());
					if (base64 != null) {
						Cache<?> cache = Cache.getCache(base64 + PLAYER_HEAD_2D_KEY);
						if (cache == null) {
							JSONObject json = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(base64)));
							String value = ((String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url")).replace(TEXTURE_MINECRAFT_URL, "");
							String url = HEAD_2D_RENDER_URL.replaceFirst("%s", value).replaceFirst("%s", "32");
							helmetImage = ImageUtils.multiply(ImageUtils.downloadImage(url), 0.9);
							helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
							Cache.putCache(base64 + PLAYER_HEAD_2D_KEY + "32", helmetImage, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
							helmetImage = ImageUtils.copyImage(helmetImage);
						} else {
							helmetImage = ImageUtils.copyImage((BufferedImage) cache.getObject());
						}
					} else {
						helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_PUPPET_LOCATION + "default_head_2d").getTexture();
					}
				} catch (ParseException | IOException e) {
					helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_PUPPET_LOCATION + "default_head_2d").getTexture();
				}								
				scale = 1;
				isArmor = false;
				helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
				break;
			case SKELETON_SKULL:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_PUPPET_LOCATION + "skeleton_skull_2d").getTexture();
				scale = 1;
				isArmor = false;
				helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
				break;
			case CREEPER_HEAD:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_PUPPET_LOCATION + "creeper_head_2d").getTexture();
				scale = 1;
				isArmor = false;
				helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
				break;
			case ZOMBIE_HEAD:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_PUPPET_LOCATION + "zombie_head_2d").getTexture();
				scale = 1;
				isArmor = false;
				helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
				break;
			case WITHER_SKELETON_SKULL:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_PUPPET_LOCATION + "wither_skeleton_skull_2d").getTexture();
				scale = 1;
				isArmor = false;
				helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
				break;
			case DRAGON_HEAD:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_PUPPET_LOCATION + "dragon_head_2d").getTexture();
				scale = 1;
				isArmor = false;
				helmetImage = ImageUtils.resizeImageAbs(helmetImage, 48, 60);
				offsetX = -4;
				offsetY = -21;
				shouldResize = false;
				break;
			case CARVED_PUMPKIN:
				if (NBTEditor.contains(helmet, "CustomModelData")) {
					skip = true;
				} else {
					helmetImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.BLOCK_LOCATION + "carved_pumpkin").getTexture();
					scale = 1;
					isArmor = false;
					helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
				}
				break;
			default:
				skip = true;
				break;
			}
			if (!skip) {
				if (isArmor) {
					helmetImage = ImageUtils.resizeImage(ImageUtils.copyAndGetSubImage(helmetImage, 8 * scale, 8 * scale, 8 * scale, 8 * scale), Math.pow(scale, -1) * 4);
				}
				
				if (shouldResize) {
					helmetImage = ImageUtils.multiply(ImageUtils.resizeImageStretch(helmetImage, 8), 0.7);
				}
				if (helmet.getEnchantments().size() > 0) {
					BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.MISC_LOCATION + "enchanted_item_glint").getTexture();
					BufferedImage tintImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g2 = tintImage.createGraphics();
					g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
					g2.drawImage(tint_ori, 0, 0, 512, 512, null);
					g2.dispose();
					helmetImage = ImageUtils.additionNonTransparent(helmetImage, tintImage, ENCHANTMENT_GLINT_FACTOR);
				}
				
				g.drawImage(helmetImage, 18 + offsetX, 32 + offsetY, shouldResize ? 40 : helmetImage.getWidth(), shouldResize ? 40 : helmetImage.getHeight(), null);
			}
		}
		
		g.dispose();
		
		return image;
	}
	
	private static BufferedImage getRawItemImage(ItemStack item, OfflineICPlayer player) throws IOException {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		Debug.debug("ImageGeneration creating raw item stack image " + (item == null ? "null" : ItemNBTUtils.getNMSItemStackJson(item)));
		
		boolean requiresEnchantmentGlint = false;
		int amount = item.getAmount();
		XMaterial xMaterial = XMaterialUtils.matchXMaterial(item);
		String key = xMaterial.name().toLowerCase();
		String directLocation = null;
		if (xMaterial.equals(XMaterial.DEBUG_STICK)) {
			requiresEnchantmentGlint = true;
		} else if (xMaterial.equals(XMaterial.ENCHANTED_GOLDEN_APPLE)) {
			requiresEnchantmentGlint = true;
		} else if (xMaterial.equals(XMaterial.WRITTEN_BOOK)) {
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
		}
		
		BufferedImage itemImage;
		RenderResult renderResult = InteractiveChatDiscordSrvAddon.plugin.modelRenderer.render(32, 32, InteractiveChatDiscordSrvAddon.plugin.resourceManager, directLocation == null ? "minecraft:item/" + key : directLocation, ModelDisplayPosition.GUI, predicates, providedTextures);
		if (renderResult.isSuccessful() && !xMaterial.isOneOf(Arrays.asList("CONTAINS:spawn_egg"))) {
			itemImage = renderResult.getImage();
		} else {
			TextureResource texture = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_BLOCK_LOCATION + key, false);
			if (texture != null && texture.isTexture()) {
				itemImage = texture.getTexture(32, 32);
			} else {
				texture = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_ITEM_LOCATION + key, false);
				if (texture != null && texture.isTexture()) {
					itemImage = texture.getTexture(32, 32);
				} else {
					return null;
				}
			}
		}
		if (item.getItemMeta() instanceof LeatherArmorMeta) {
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			Color color = new Color(meta.getColor().asRGB());
			if (xMaterial.equals(XMaterial.LEATHER_HORSE_ARMOR)) {
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(itemImage), color);
				itemImage = ImageUtils.multiply(itemImage, colorOverlay);
			} else {
				BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_LOCATION + xMaterial.name().toLowerCase() + "_overlay").getTexture(32, 32);
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(itemImage), color);
				itemImage = ImageUtils.multiply(itemImage, colorOverlay);
				Graphics2D g2 = itemImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(armorOverlay, 0, 0, null);
				g2.dispose();
			}
		} else if (FilledMapUtils.isFilledMap(item)) {
			BufferedImage filled = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_LOCATION + "filled_map_markings").getTexture(32, 32);
			ImageUtils.xor(itemImage, filled, 200);
		}
		
		if (item.getItemMeta() instanceof PotionMeta) {
			if (xMaterial.equals(XMaterial.TIPPED_ARROW)) {
				itemImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_LOCATION + "tipped_arrow_base").getTexture(32, 32);
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
				
				Graphics2D g2 = itemImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(tippedArrowHead, 0, 0, null);
				g2.dispose();
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
				
				Graphics2D g2 = itemImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(potionOverlay, 0, 0, null);
				g2.dispose();
				
				if (potiontype != null) {
					if (!(potiontype.name().equals("WATER") || potiontype.name().equals("AWKWARD") || potiontype.name().equals("MUNDANE") || potiontype.name().equals("THICK") || potiontype.name().equals("UNCRAFTABLE"))) {
						requiresEnchantmentGlint = true;
					}
				}
			}
		}
		
		if (requiresEnchantmentGlint || xMaterial.equals(XMaterial.ENCHANTED_BOOK) || item.getEnchantments().size() > 0) {
			BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.MISC_LOCATION + "enchanted_item_glint").getTexture();
			BufferedImage tintImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);				
			Graphics2D g3 = tintImage.createGraphics();
			g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g3.drawImage(tint_ori, 0, 0, 128, 128, null);
			g3.dispose();
			
			itemImage = ImageUtils.additionNonTransparent(itemImage, tintImage, ENCHANTMENT_GLINT_FACTOR);
		}
		
		if (item.getType().getMaxDurability() > 0) {
			int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
			int maxDur = item.getType().getMaxDurability();
			double percentage = ((double) durability / (double) maxDur);
			if (percentage < 1) {
				int hue = (int) (125 * percentage);
				int length = (int) (26 * percentage);
				Color color = Color.getHSBColor((float) hue / 360, 1, 1);
				
				Graphics2D g4 = itemImage.createGraphics();
				g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g4.setColor(Color.black);
				g4.fillPolygon(new int[] {4, 30, 30, 4}, new int[] {26, 26, 30, 30}, 4);
				g4.setColor(color);
				g4.fillPolygon(new int[] {4, 4 + length, 4 + length, 4}, new int[] {26, 26, 28, 28}, 4);
				g4.dispose();
			}
		}
		
		if (amount > 1) {
			BufferedImage newItemImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g4 = newItemImage.createGraphics();
			g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g4.drawImage(itemImage, 0, 0, null);
			if (MCFont.isWorking()) {
				newItemImage = ImageUtils.printComponentRightAligned(newItemImage, Component.text(amount), 33, 18, 16, ITEM_AMOUNT_TEXT_DARKEN_FACTOR);
			} else {
				String str = Integer.toString(amount);
				int x = 22;
				for (int i = str.length() - 1; i >= 0; i--) {
					BufferedImage charImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_FONT_LOCATION + str.substring(i, i + 1)).getTexture();
					g4.drawImage(ImageUtils.darken(ImageUtils.copyImage(charImage), 180), x, 21, null);
					g4.drawImage(charImage, x - 2, 19, null);
					x -= 12;
				}
			}
			g4.dispose();
			itemImage = newItemImage;
		}
		
		return itemImage;
	}
	
	public static BufferedImage getMapImage(ItemStack item, Player player) throws Exception {
		if (!FilledMapUtils.isFilledMap(item)) {
			throw new IllegalArgumentException("Provided item is not a filled map");
		}
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		Debug.debug("ImageGeneration creating map image");
		
		BufferedImage background = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.MAP_LOCATION + "map_background").getTexture();
		
		BufferedImage image = new BufferedImage(1120, 1120, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(background, 0, 0, 1120, 1120, null);
		g.dispose();
		
		int borderOffset = (int) (image.getWidth() / 23.3333333333333333333);
		int ratio = (image.getWidth() - borderOffset * 2) / 128;
		
		ItemMapWrapper data = new ItemMapWrapper(item, player);
		for (int widthOffset = 0; widthOffset < 128; widthOffset++) {
			for (int heightOffset = 0; heightOffset < 128; heightOffset++) {
				byte index = data.getColors()[widthOffset + heightOffset * 128];
				if (MapPalette.TRANSPARENT != index) {
					Color color = MapPalette.getColor(index);
					for (int x = 0; x < ratio; x++) {
						for (int y = 0; y < ratio; y++) {
							image.setRGB(widthOffset * ratio + borderOffset + x, heightOffset * ratio + borderOffset + y, color.getRGB());
						}
					}
				}
			}
		}
		
		Graphics2D g2 = image.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		
		BufferedImage asset = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.MAP_LOCATION + "map_icons").getTexture();
		int iconWidth = asset.getWidth() / MAP_ICON_PER_ROLE;
		
		for (MapCursor icon : data.getMapCursors()) {
			int x = icon.getX() + 128;
			int y = icon.getY() + 128;
			double rotation = (360.0 / 16.0 * (double) icon.getDirection()) + 180.0;
			int type = icon.getType().ordinal();
			Component component = LegacyComponentSerializer.legacySection().deserializeOrNull(icon.getCaption());
			
			//String name
			BufferedImage iconImage = ImageUtils.copyAndGetSubImage(asset, type % MAP_ICON_PER_ROLE * iconWidth, type / MAP_ICON_PER_ROLE * iconWidth, iconWidth, iconWidth);
			BufferedImage iconImageBig = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g3 = iconImageBig.createGraphics();
			g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g3.drawImage(iconImage, iconImageBig.getWidth() / 6, iconImageBig.getHeight() / 6, 64, 64, null);
			g3.dispose();
			iconImage = iconImageBig;
			
			BufferedImage iconCan = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
			
			AffineTransform at = new AffineTransform();
            at.rotate(Math.toRadians(rotation), iconImage.getWidth() / 2, iconImage.getHeight() / 2);
            Graphics2D g2d = iconCan.createGraphics();
            g2d.drawImage(iconImage, at, null);
            g2d.dispose();
            
            int imageX = x * ratio / 2 + borderOffset;
            int imageY = y * ratio / 2 + borderOffset;
            
            g2.drawImage(iconCan, imageX - (iconCan.getWidth() / 2), imageY - (iconCan.getHeight() / 2), 96, 96, null);
            
            if (component != null && MCFont.isWorking()) {
            	ImageUtils.printComponentNoShadow(image, component, imageX, imageY + 32, 30, true);
            }
		}
		g2.dispose();
		
		return image;
	}
	
	public static BufferedImage getToolTipImage(Component print) throws Exception {
		return getToolTipImage(Arrays.asList(print), false);
	}
	
	public static BufferedImage getToolTipImage(Component print, boolean allowLineBreaks) throws Exception {
		return getToolTipImage(Arrays.asList(print), allowLineBreaks);
	}
	
	public static BufferedImage getToolTipImage(List<Component> prints) throws Exception {
		return getToolTipImage(prints, false);
	}

	public static BufferedImage getToolTipImage(List<Component> prints, boolean allowLineBreaks) throws Exception {
		if (prints.isEmpty()) {
			Debug.debug("ImageGeneration creating tooltip image");
		} else {
			Debug.debug("ImageGeneration creating tooltip image of " + InteractiveChatComponentSerializer.bungeecordApiLegacy().serialize(prints.get(0)));
		}
		
		if (allowLineBreaks) {
			List<Component> newList = new ArrayList<>();
			for (Component component : prints) {
				newList.addAll(ComponentStyling.splitAtLineBreaks(component));
			}
			prints = newList;
		}
		
		BufferedImage image = new BufferedImage(1120, prints.size() * 20 + 15, BufferedImage.TYPE_INT_ARGB);
		
		if (!MCFont.isWorking()) {
			return getMissingImage(image.getWidth(), image.getHeight());
		}
		
		for (int i = 0; i < prints.size(); i++) {
			Component text = prints.get(i);
			ImageUtils.printComponent(image, text, 8, 8 + 20 * i, 16);
		}
		
		int lastX = 0;
		for (int x = 0; x < image.getWidth() - 9; x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				if (image.getRGB(x, y) != 0) {
					lastX = x;
					break;
				}
			}
		}
		
		BufferedImage background = new BufferedImage(lastX + 9, image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = background.createGraphics();
		g.setColor(new Color(36, 1, 92));
		g.fillRect(2, 2, background.getWidth() - 4, background.getHeight() - 4);
		g.setColor(new Color(16, 1, 16));
		g.fillRect(4, 4, background.getWidth() - 8, background.getHeight() - 8);
		g.fillRect(0, 2, 2, background.getHeight() - 4);
		g.fillRect(background.getWidth() - 2, 2, 2, background.getHeight() - 4);
		g.fillRect(2, 0, background.getWidth() - 4, 2);
		g.fillRect(2, background.getHeight() - 2, background.getWidth() - 4, 2);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		
		return background;
	}
	
	public static BufferedImage getTabListImage(List<Component> header, List<Component> footer, List<ValueTrios<UUID, Component, Integer>> players, boolean showAvatar, boolean showPing) throws Exception {
		List<ValuePairs<BufferedImage, Integer>> playerImages = new ArrayList<>(players.size());
		int masterOffsetX = 0;
		for (ValueTrios<UUID, Component, Integer> trio : players) {
			UUID uuid = trio.getFirst();
			Component name = trio.getSecond();
			int ping = trio.getThird();
			BufferedImage image = new BufferedImage(4096, 18, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			int offsetX = 0;
			if (showAvatar) {
				BufferedImage avatar;
				try {
					Player onlinePlayer = Bukkit.getPlayer(uuid);
					if (onlinePlayer == null) {
						Cache<?> cache = Cache.getCache(uuid + PLAYER_HEAD_2D_KEY);
						if (cache == null) {
							String url = HEAD_2D_RENDER_URL.replaceFirst("%s", uuid.toString()).replaceFirst("%s", "16");
							avatar = ImageUtils.multiply(ImageUtils.downloadImage(url), 0.9);
							Cache.putCache(uuid + PLAYER_HEAD_2D_KEY + "16", avatar, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
							avatar = ImageUtils.copyImage(avatar);
						} else {
							avatar = ImageUtils.copyImage((BufferedImage) cache.getObject());
						}
					} else {
						try {
							JSONObject json = (JSONObject) new JSONParser().parse(SkinUtils.getSkinJsonFromProfile(onlinePlayer));
							String value = ((String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url")).replace(TEXTURE_MINECRAFT_URL, "");
							Cache<?> cache = Cache.getCache(onlinePlayer.getUniqueId() + value + PLAYER_HEAD_2D_KEY + "16");
							if (cache == null) {
								String url = HEAD_2D_RENDER_URL.replaceFirst("%s", value).replaceFirst("%s", "16");
								avatar = ImageUtils.downloadImage(url);
								Cache.putCache(onlinePlayer.getUniqueId() + value + PLAYER_HEAD_2D_KEY + "16", avatar, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
							} else {
								avatar = (BufferedImage) cache.getObject();
							}
							avatar = ImageUtils.copyImage(avatar);
						} catch (Exception e) {
							String url = HEAD_2D_RENDER_URL.replaceFirst("%s", uuid.toString()).replaceFirst("%s", "16");
							avatar = ImageUtils.downloadImage(url);
							Cache.putCache(uuid + "e" + PLAYER_HEAD_2D_KEY + "16", avatar, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
							avatar = ImageUtils.copyImage(avatar);
						}
					}
				} catch (Exception e) {
					avatar = ImageUtils.resizeImageAbs(InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_PUPPET_LOCATION + "default_head_2d").getTexture(), 16, 16);
				}
				g.drawImage(avatar, offsetX, 0, 16, 16, null);
				offsetX += 18;
			} else {
				offsetX += 2;
			}
			g.dispose();
			ImageUtils.printComponent(image, name, offsetX, -1, 16);
			int lastX = 0;
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					if (image.getRGB(x, y) != 0) {
						lastX = x;
						break;
					}
				}
			}
			if (lastX > masterOffsetX) {
				masterOffsetX = lastX;
			}
			playerImages.add(new ValuePairs<>(image, ping));
		}
		List<BufferedImage> playerRows = new ArrayList<>(playerImages.size());
		if (showPing) {
			masterOffsetX += 26;
		} else {
			masterOffsetX += 2;
		}
		for (ValuePairs<BufferedImage, Integer> pair : playerImages) {
			BufferedImage image = pair.getFirst();
			if (showPing) {
				BufferedImage ping = getPingIcon(pair.getSecond());
				Graphics2D g = image.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g.drawImage(ImageUtils.resizeImageAbs(ping, 20, 14), masterOffsetX - 22, 3, null);
				g.dispose();
			}
			BufferedImage cropped = new BufferedImage(masterOffsetX, 18, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = cropped.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.setColor(TABLIST_PLAYER_BACKGROUND);
			g.fillRect(0, 0, cropped.getWidth(), 16);
			g.drawImage(image, 0, 0, null);
			g.dispose();
			playerRows.add(cropped);
		}
		Map<BufferedImage, Integer> headerLines = new LinkedHashMap<>(header.size());
		for (Component line : header) {
			BufferedImage image = new BufferedImage(4096, 18, BufferedImage.TYPE_INT_ARGB);
			ImageUtils.printComponent(image, line, 0, -1, 16);
			int lastX = 0;
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					if (image.getRGB(x, y) != 0) {
						lastX = x;
						break;
					}
				}
			}
			BufferedImage cropped = new BufferedImage(lastX, 18, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = cropped.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.drawImage(image, 0, 0, null);
			g.dispose();
			headerLines.put(cropped, lastX);
		}
		Map<BufferedImage, Integer> footerLines = new LinkedHashMap<>(footer.size());
		for (Component line : footer) {
			BufferedImage image = new BufferedImage(4096, 18, BufferedImage.TYPE_INT_ARGB);
			ImageUtils.printComponent(image, line, 0, -1, 16);
			int lastX = 0;
			for (int x = 0; x < image.getWidth(); x++) {
				for (int y = 0; y < image.getHeight(); y++) {
					if (image.getRGB(x, y) != 0) {
						lastX = x;
						break;
					}
				}
			}
			BufferedImage cropped = new BufferedImage(lastX, 18, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = cropped.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.drawImage(image, 0, 0, null);
			g.dispose();
			footerLines.put(cropped, lastX);
		}
		BufferedImage image;
		if (playerRows.size() <= TABLIST_SINGLE_COLUMN_LIMIT) {
			image = new BufferedImage(masterOffsetX + 4, playerRows.size() * 18 + 2, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.setColor(TABLIST_BACKGROUND);
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
			int offsetY = 2;
			for (BufferedImage each : playerRows) {
				g.drawImage(each, 2, offsetY, null);
				offsetY += 18;
			}
			g.dispose();
		} else {
			image = new BufferedImage(masterOffsetX * 2 + 6, (int) Math.ceil((double) playerRows.size() / 2) * 18 + 2, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.setColor(TABLIST_BACKGROUND);
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
			int offsetY = 2;
			int half = (int) Math.ceil((double) playerRows.size() / 2);
			for (int i = 0; i < half; i++) {
				g.drawImage(playerRows.get(i), 2, offsetY, null);
				offsetY += 18;
			}
			offsetY = 2;
			for (int i = half; i < playerRows.size(); i++) {
				g.drawImage(playerRows.get(i), masterOffsetX + 4, offsetY, null);
				offsetY += 18;
			}
			if (playerRows.size() % 2 == 1) {
				g.fillRect(masterOffsetX + 4, offsetY, masterOffsetX, 16);
			}
			g.dispose();
		}
		int maxOffsetX = Stream.concat(headerLines.values().stream(), footerLines.values().stream()).mapToInt(each -> each).max().orElse(0);
		if (maxOffsetX <= 0) {
			return image;
		} else {
			BufferedImage decoration = new BufferedImage(Math.max(image.getWidth(), maxOffsetX + 4), (headerLines.isEmpty() ? 0 : headerLines.size() * 18 + 2) + image.getHeight() + (footerLines.isEmpty() ? 2 : footerLines.size() * 18 + 2), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = decoration.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g.setColor(TABLIST_BACKGROUND);
			g.fillRect(0, 0, decoration.getWidth(), decoration.getHeight());
			int offsetY = 2;
			for (BufferedImage each : headerLines.keySet()) {
				g.drawImage(each, (decoration.getWidth() / 2) - (each.getWidth() / 2), offsetY, null);
				offsetY += 18;
			}
			g.drawImage(image, (decoration.getWidth() / 2) - (image.getWidth() / 2), offsetY, null);
			offsetY += image.getHeight();
			for (BufferedImage each : footerLines.keySet()) {
				g.drawImage(each, (decoration.getWidth() / 2) - (each.getWidth() / 2), offsetY, null);
				offsetY += 18;
			}
			g.dispose();
			return decoration;
		}
	}
	
	public static BufferedImage getPingIcon(int ms) {
		BufferedImage icons = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.GUI_LOCATION + "icons").getTexture();
		if (ms < 0) {
			return ImageUtils.copyAndGetSubImage(icons, 0, 56, 10, 7);
		} else if (ms < 150) {
			return ImageUtils.copyAndGetSubImage(icons, 0, 16, 10, 7);
		} else if (ms < 300) {
			return ImageUtils.copyAndGetSubImage(icons, 0, 24, 10, 7);
		} else if (ms < 600) {
			return ImageUtils.copyAndGetSubImage(icons, 0, 32, 10, 7);
		} else if (ms < 1000) {
			return ImageUtils.copyAndGetSubImage(icons, 0, 40, 10, 7);
		} else {
			return ImageUtils.copyAndGetSubImage(icons, 0, 48, 10, 7);
		}
	}
	
}
