package com.loohp.interactivechatdiscordsrvaddon.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

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
import org.bukkit.map.MapPalette;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.utils.ComponentStyling;
import com.loohp.interactivechat.utils.FilledMapUtils;
import com.loohp.interactivechat.utils.HashUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.SkinUtils;
import com.loohp.interactivechatdiscordsrvaddon.Cache;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.utils.ItemStackUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.PotionUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.VectorUtils;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.ItemMapWrapper;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.ItemMapWrapper.MapIcon;

@SuppressWarnings("deprecation")
public class ImageGeneration {
	
	private static final String OPTIFINE_CAPE_URL = "https://optifine.net/capes/%s.png";
	private static final String TEXTURE_MINECRAFT_URL = "http://textures.minecraft.net/texture/";
	private static final String PLAYER_RENDER_URL = "https://mc-heads.net/player/%s/64";
	private static final String SKULL_RENDER_URL = "https://mc-heads.net/head/%s/96";
	private static final String HEAD_2D_RENDER_URL = "https://mc-heads.net/avatar/%s/32";
	
	private static final int MAP_ICON_PER_ROLE = 16;
	private static final int SPACING = 36;
	
	private static final double ENCHANTMENT_GLINT_FACTOR = 190.0 / 255.0;
	
	private static final String PLAYER_CAPE_KEY = "PlayerCapeTexture";
	private static final String FULL_BODY_IMAGE_KEY = "FullBodyImage";
	private static final String PLAYER_HEAD_KEY = "PlayerHeadImage";
	private static final String PLAYER_HEAD_2D_KEY = "PlayerHead2DImage";
	private static final String INVENTORY_KEY = "Inventory";
	private static final String PLAYER_INVENTORY_KEY = "PlayerInventory";
	
	private static final DecimalFormat PHASE_FORMAT = new DecimalFormat("00");
	private static final Random RANDOM = new Random();
	
	public static BufferedImage getMissingImage(int width, int length) {
		Debug.debug("ImageGeneration creating missing texture image");
		BufferedImage image = new BufferedImage(width, length, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, width, length);
		g.setColor(new Color(198, 0, 198));
		g.fillRect(0, 0, width / 2, length / 2);
		g.fillRect(width / 2, length / 2, width / 2, length / 2);
		g.dispose();
		return image;
	}
	
	public static BufferedImage getItemStackImage(ItemStack item, Player player) throws IOException {
		return getItemStackImage(item, player, false);
	}
	
	public static BufferedImage getItemStackImage(ItemStack item, Player player, boolean alternateAir) throws IOException {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		Debug.debug("ImageGeneration creating item stack image " + item);
		
		BufferedImage background = new BufferedImage(36, 36, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = background.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		
		BufferedImage itemImage;
		if (item == null || item.getType().equals(Material.AIR)) {
			if (alternateAir) {
				itemImage = InteractiveChatDiscordSrvAddon.plugin.getBlockTexture("air_alternate");
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
	
	public static BufferedImage getInventoryImage(Inventory inventory, Player player) throws Exception {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		InteractiveChatDiscordSrvAddon.plugin.inventoryImageCounter.incrementAndGet();		
		Debug.debug("ImageGeneration creating inventory image of " + player.getName());
		
		String key = INVENTORY_KEY + HashUtils.createSha1("Inventory", inventory);
		if (!inventory.contains(XMaterial.COMPASS.parseMaterial()) && !inventory.contains(XMaterial.CLOCK.parseMaterial())) {
			Cache<?> cache = Cache.getCache(key);
			if (cache != null) {
				return ImageUtils.copyImage((BufferedImage) cache.getObject());
			}
		}
		
		int rows = inventory.getSize() / 9;
		BufferedImage background = InteractiveChatDiscordSrvAddon.plugin.getGUITexture(rows + "_rows");
		
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
	
	public static BufferedImage getPlayerInventoryImage(Inventory inventory, Player player) throws Exception {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		InteractiveChatDiscordSrvAddon.plugin.inventoryImageCounter.incrementAndGet();
		Debug.debug("ImageGeneration creating player inventory image of " + player.getName());
		
		String key = PLAYER_INVENTORY_KEY + HashUtils.createSha1(player.getUniqueId().toString(), inventory);
		if (!inventory.contains(XMaterial.COMPASS.parseMaterial()) && !inventory.contains(XMaterial.CLOCK.parseMaterial())) {
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
				background = InteractiveChatDiscordSrvAddon.plugin.getGUITexture("player_inventory");
			}
		} else {
			background = InteractiveChatDiscordSrvAddon.plugin.getGUITexture("player_inventory");
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
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.getItemTexture("empty_armor_slot_boots"), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
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
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.getItemTexture("empty_armor_slot_leggings"), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
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
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.getItemTexture("empty_armor_slot_chestplate"), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
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
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.getItemTexture("empty_armor_slot_helmet"), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
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
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.getItemTexture("empty_armor_slot_shield"), 162, 126, 32, 32, null);
		} else {				
			BufferedImage itemImage = getRawItemImage(offhand, player);				
			if (itemImage != null) {
				g.drawImage(itemImage, 162, 126, null);
			}
		}
		
		//puppet
		EntityEquipment equipment = player.getEquipment();
		BufferedImage puppet = getFullBodyImage(player, equipment.getHelmet(), equipment.getChestplate(), equipment.getLeggings(), equipment.getBoots());
		g.drawImage(puppet, 65, 20, null);
		
		g.dispose();
		
		Cache.putCache(key, target, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
		
		return target;
	}
	
	private static BufferedImage getFullBodyImage(Player player, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		Debug.debug("ImageGeneration creating puppet image of " + player.getName());
		
		BufferedImage image;
		BufferedImage cape;
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(SkinUtils.getSkinJsonFromProfile(player));
			if (json == null) {
				cape = null;
				image = InteractiveChatDiscordSrvAddon.plugin.getPuppetTexture("default");
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
						String url = OPTIFINE_CAPE_URL.replaceAll("%s", player.getName());
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
					e1.printStackTrace();
					image = InteractiveChatDiscordSrvAddon.plugin.getPuppetTexture("default");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			cape = null;
			image = InteractiveChatDiscordSrvAddon.plugin.getPuppetTexture("default");
		}
		
		image = ImageUtils.expandCenterAligned(ImageUtils.multiply(image, 0.7), 6, 4, 6, 6);
		
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		
		if (ItemStackUtils.isWearable(leggings)) {
			XMaterial type = XMaterial.matchXMaterial(leggings);
			BufferedImage leggingsImage = null;
			BufferedImage leggingsImage1 = null;
			BufferedImage leggingsImage2 = null;
			int scale = 1;
			switch (type) {
			case LEATHER_LEGGINGS:
				leggingsImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("leather_layer_2");
				scale = (leggingsImage.getWidth() / 8) / 8;
				LeatherArmorMeta meta = (LeatherArmorMeta) leggings.getItemMeta();
				Color color = new Color(meta.getColor().asRGB());
				BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("leather_layer_2_overlay");
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(leggingsImage), color);
				leggingsImage = ImageUtils.multiply(leggingsImage, colorOverlay);
				
				Graphics2D g2 = leggingsImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(armorOverlay, 0, 0, null);
				g2.dispose();
				break;
			case CHAINMAIL_LEGGINGS:
				leggingsImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("chainmail_layer_2");
				scale = (leggingsImage.getWidth() / 8) / 8;
				break;
			case GOLDEN_LEGGINGS:
				leggingsImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("gold_layer_2");
				scale = (leggingsImage.getWidth() / 8) / 8;
				break;
			case IRON_LEGGINGS:
				leggingsImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("iron_layer_2");
				scale = (leggingsImage.getWidth() / 8) / 8;
				break;
			case DIAMOND_LEGGINGS:
				leggingsImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("diamond_layer_2");
				scale = (leggingsImage.getWidth() / 8) / 8;
				break;
			case NETHERITE_LEGGINGS:
				leggingsImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("netherite_layer_2");
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
				BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.getMiscTexture("enchanted_item_glint");
				BufferedImage tintImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);				
				Graphics2D g3 = tintImage.createGraphics();
				g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g3.drawImage(tint_ori, 0, 0, 512, 512, null);
				g3.dispose();
				
				leggingsImage1 = ImageUtils.additionNonTransparent(leggingsImage1, tintImage, ENCHANTMENT_GLINT_FACTOR);
				leggingsImage2 = ImageUtils.additionNonTransparent(leggingsImage2, tintImage, ENCHANTMENT_GLINT_FACTOR);
			}
			
			g.drawImage(leggingsImage1, 20, 68, 36, 24, null);
			g.drawImage(leggingsImage2, 20, 84, 36, 30, null);
		}
		
		if (ItemStackUtils.isWearable(boots)) {
			XMaterial type = XMaterial.matchXMaterial(boots);
			BufferedImage bootsImage = null;
			int scale = 1;
			switch (type) {
			case LEATHER_BOOTS:
				bootsImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("leather_layer_1");
				scale = (bootsImage.getWidth() / 8) / 8;
				LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
				Color color = new Color(meta.getColor().asRGB());
				BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("leather_layer_1_overlay");
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(bootsImage), color);
				
				bootsImage = ImageUtils.multiply(bootsImage, colorOverlay);
				
				Graphics2D g2 = bootsImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(armorOverlay, 0, 0, null);
				g2.dispose();
				break;
			case CHAINMAIL_BOOTS:
				bootsImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("chainmail_layer_1");
				scale = (bootsImage.getWidth() / 8) / 8;
				break;
			case GOLDEN_BOOTS:
				bootsImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("gold_layer_1");
				scale = (bootsImage.getWidth() / 8) / 8;
				break;
			case IRON_BOOTS:
				bootsImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("iron_layer_1");
				scale = (bootsImage.getWidth() / 8) / 8;
				break;
			case DIAMOND_BOOTS:
				bootsImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("diamond_layer_1");
				scale = (bootsImage.getWidth() / 8) / 8;
				break;
			case NETHERITE_BOOTS:
				bootsImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("netherite_layer_1");
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
				BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.getMiscTexture("enchanted_item_glint");
				BufferedImage tintImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);				
				Graphics2D g3 = tintImage.createGraphics();
				g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g3.drawImage(tint_ori, 0, 0, 512, 512, null);
				g3.dispose();
				
				bootsImage = ImageUtils.additionNonTransparent(bootsImage, tintImage, ENCHANTMENT_GLINT_FACTOR);
			}

			g.drawImage(bootsImage, 18, 114, 40, 32, null);
		}
		
		if (ItemStackUtils.isWearable(chestplate)) {
			XMaterial type = XMaterial.matchXMaterial(chestplate);
			BufferedImage chestplateImage = null;
			BufferedImage chestplateImage1 = null;
			BufferedImage chestplateImage2 = null;
			BufferedImage chestplateImage3 = null;
			boolean isArmor = true;
			int scale = 1;
			switch (type) {
			case LEATHER_CHESTPLATE:
				chestplateImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("leather_layer_1");
				scale = (chestplateImage.getWidth() / 8) / 8;
				LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
				Color color = new Color(meta.getColor().asRGB());
				BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("leather_layer_1_overlay");
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(chestplateImage), color);
				chestplateImage = ImageUtils.multiply(chestplateImage, colorOverlay);
				
				Graphics2D g2 = chestplateImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(armorOverlay, 0, 0, null);
				g2.dispose();
				break;
			case CHAINMAIL_CHESTPLATE:
				chestplateImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("chainmail_layer_1");
				scale = (chestplateImage.getWidth() / 8) / 8;
				break;
			case GOLDEN_CHESTPLATE:
				chestplateImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("gold_layer_1");
				scale = (chestplateImage.getWidth() / 8) / 8;
				break;
			case IRON_CHESTPLATE:
				chestplateImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("iron_layer_1");
				scale = (chestplateImage.getWidth() / 8) / 8;
				break;
			case DIAMOND_CHESTPLATE:
				chestplateImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("diamond_layer_1");
				scale = (chestplateImage.getWidth() / 8) / 8;
				break;
			case NETHERITE_CHESTPLATE:
				chestplateImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("netherite_layer_1");
				scale = (chestplateImage.getWidth() / 8) / 8;
				break;
			case ELYTRA:
				isArmor = false;
				chestplateImage = new BufferedImage(150, 150, BufferedImage.TYPE_INT_ARGB);
				BufferedImage wing = cape == null ? InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("elytra") : cape;
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
					BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.getMiscTexture("enchanted_item_glint");
					BufferedImage tintImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g4 = tintImage.createGraphics();
					g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
					g4.drawImage(tint_ori, 0, 0, 512, 512, null);
					g4.dispose();
					
					chestplateImage = ImageUtils.additionNonTransparent(chestplateImage, tintImage, ENCHANTMENT_GLINT_FACTOR);
				}
				
				ImageUtils.drawTransparent(image, chestplateImage, -10, 28);
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
					BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.getMiscTexture("enchanted_item_glint");
					BufferedImage tintImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);				
					Graphics2D g2 = tintImage.createGraphics();
					g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
					g2.drawImage(tint_ori, 0, 0, 512, 512, null);
					g2.dispose();
					
					chestplateImage1 = ImageUtils.additionNonTransparent(chestplateImage1, tintImage, ENCHANTMENT_GLINT_FACTOR);
					chestplateImage2 = ImageUtils.additionNonTransparent(chestplateImage2, tintImage, ENCHANTMENT_GLINT_FACTOR);
					chestplateImage3 = ImageUtils.additionNonTransparent(chestplateImage3, tintImage, ENCHANTMENT_GLINT_FACTOR);
				}
				
				g.drawImage(chestplateImage1, 18, 34, 40, 56, null);
				g.drawImage(chestplateImage2, 2, 34, 24, 56, null);
				g.drawImage(chestplateImage3, 50, 34, 24, 56, null);
			}
		}
		
		if (ItemStackUtils.isWearable(helmet)) {
			XMaterial type = XMaterial.matchXMaterial(helmet);
			BufferedImage helmetImage = null;
			int scale = 1;
			boolean isArmor = true;
			switch (type) {
			case LEATHER_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("leather_layer_1");
				scale = (helmetImage.getWidth() / 8) / 8;
				LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
				Color color = new Color(meta.getColor().asRGB());
				BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("leather_layer_1_overlay");
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(helmetImage), color);
				
				helmetImage = ImageUtils.multiply(helmetImage, colorOverlay);
				
				Graphics2D g2 = helmetImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(armorOverlay, 0, 0, null);
				g2.dispose();
				break;
			case CHAINMAIL_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("chainmail_layer_1");
				scale = (helmetImage.getWidth() / 8) / 8;
				break;
			case GOLDEN_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("gold_layer_1");
				scale = (helmetImage.getWidth() / 8) / 8;
				break;
			case IRON_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("iron_layer_1");
				scale = (helmetImage.getWidth() / 8) / 8;
				break;
			case DIAMOND_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("diamond_layer_1");
				scale = (helmetImage.getWidth() / 8) / 8;
				break;
			case NETHERITE_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("netherite_layer_1");
				scale = (helmetImage.getWidth() / 8) / 8;
				break;
			case TURTLE_HELMET:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("turtle_layer_1");
				scale = (helmetImage.getWidth() / 8) / 8;
				break;
			case CARVED_PUMPKIN:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.getArmorTexture("carved_pumpkin");
				scale = 1;
				isArmor = false;
				helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
				break;
			case PLAYER_HEAD:
				try {
					String base64 = SkinUtils.getSkinValue(helmet.getItemMeta());
					if (base64 != null) {
						Cache<?> cache = Cache.getCache(base64 + PLAYER_HEAD_2D_KEY);
						if (cache == null) {
							JSONObject json = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(base64)));
							String value = ((String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url")).replace(TEXTURE_MINECRAFT_URL, "");
							String url = HEAD_2D_RENDER_URL.replaceFirst("%s", value);
							helmetImage = ImageUtils.multiply(ImageUtils.downloadImage(url), 0.9);
							helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
							Cache.putCache(base64 + PLAYER_HEAD_2D_KEY, helmetImage, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
							helmetImage = ImageUtils.copyImage(helmetImage);
						} else {
							helmetImage = ImageUtils.copyImage((BufferedImage) cache.getObject());
						}
					} else {
						helmetImage = InteractiveChatDiscordSrvAddon.plugin.getPuppetTexture("default_head_2d");
					}
				} catch (ParseException | IOException e) {
					helmetImage = InteractiveChatDiscordSrvAddon.plugin.getPuppetTexture("default_head_2d");
				}								
				scale = 1;
				isArmor = false;
				helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
				break;
			case SKELETON_SKULL:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.getPuppetTexture("skeleton_skull_2d");
				scale = 1;
				isArmor = false;
				helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
				break;
			case CREEPER_HEAD:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.getPuppetTexture("creeper_head_2d");
				scale = 1;
				isArmor = false;
				helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
				break;
			case ZOMBIE_HEAD:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.getPuppetTexture("zombie_head_2d");
				scale = 1;
				isArmor = false;
				helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
				break;
			case WITHER_SKELETON_SKULL:
				helmetImage = InteractiveChatDiscordSrvAddon.plugin.getPuppetTexture("wither_skeleton_skull_2d");
				scale = 1;
				isArmor = false;
				helmetImage = ImageUtils.resizeImageAbs(helmetImage, 32, 32);
				break;
			default:
				break;
			}
			if (isArmor) {
				helmetImage = ImageUtils.resizeImage(ImageUtils.copyAndGetSubImage(helmetImage, 8 * scale, 8 * scale, 8 * scale, 8 * scale), Math.pow(scale, -1) * 4);
			}
			
			helmetImage = ImageUtils.multiply(ImageUtils.resizeImageStretch(helmetImage, 8), 0.7);
			if (helmet.getEnchantments().size() > 0) {
				BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.getMiscTexture("enchanted_item_glint");
				BufferedImage tintImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = tintImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(tint_ori, 0, 0, 512, 512, null);
				g2.dispose();
				helmetImage = ImageUtils.additionNonTransparent(helmetImage, tintImage, ENCHANTMENT_GLINT_FACTOR);
			}
			
			g.drawImage(helmetImage, 18, 2, 40, 40, null);
		}
		
		g.dispose();
		
		return image;
	}
	
	private static BufferedImage getRawItemImage(ItemStack item, Player player) throws IOException {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		Debug.debug("ImageGeneration creating raw item stack image " + item);
		
		boolean requiresEnchantmentGlint = false;
		int amount = item.getAmount();
		XMaterial xMaterial = XMaterial.matchXMaterial(item);
		String key = xMaterial.name().toLowerCase();
		if (xMaterial.equals(XMaterial.CROSSBOW)) {
			key = "crossbow_standby";
		} else if (xMaterial.equals(XMaterial.CLOCK)) {
			key = "clock_00";
		} else if (xMaterial.equals(XMaterial.COMPASS)) {
			key = "compass_00";
		} else if (xMaterial.equals(XMaterial.DEBUG_STICK)) {
			key = "stick";
			requiresEnchantmentGlint = true;
		} else if (xMaterial.equals(XMaterial.ENCHANTED_GOLDEN_APPLE)) {
			key = "golden_apple";
			requiresEnchantmentGlint = true;
		} else if (key.contains("infested_")) {
			key = key.replace("infested_", "");
		} else if (key.contains("waxed_")) {
			key = key.replace("waxed_", "");
		}
		BufferedImage itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture(key);
		if (!InteractiveChatDiscordSrvAddon.plugin.hasItemTexture(key)) {
			itemImage = InteractiveChatDiscordSrvAddon.plugin.getBlockTexture(key);
			if (itemImage == null) {
				return null;
			}
		}
		if (xMaterial.isOneOf(Arrays.asList("CONTAINS:Banner"))) {
			BufferedImage banner = BannerGraphics.generateBannerImage(item);
			
			BufferedImage sizedBanner = new BufferedImage(13, 24, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = sizedBanner.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.drawImage(banner, 0, 0, 13, 24, null);
			g2.dispose();
			
			BufferedImage shearBanner = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g3 = shearBanner.createGraphics();
			g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			AffineTransform t = AffineTransform.getShearInstance(0, 2.5 / 13.0 * -1);
			g3.setTransform(t);
			g3.drawImage(sizedBanner, 0, 3, null);
			g3.dispose();
			
			Graphics2D g4 = itemImage.createGraphics();
			g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g4.drawImage(shearBanner, 10, 2, null);
			g4.dispose();
		} else if (xMaterial.equals(XMaterial.SHIELD)) {
			BufferedImage banner = BannerGraphics.generateShieldImage(item);
			if (banner != null) {
				itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("shield_banner");
				
				BufferedImage sizedBanner = new BufferedImage(11, 24, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = sizedBanner.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(banner, 0, 0, 11, 24, null);
				g2.dispose();
				
				BufferedImage shearBanner = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g3 = shearBanner.createGraphics();
				g3.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				AffineTransform t = AffineTransform.getShearInstance(1.5 / 24.0 * -1, 2.5 / 11.0);
				g3.setTransform(t);
				g3.drawImage(sizedBanner, 3, 2, null);
				g3.dispose();
				
				Graphics2D g4 = itemImage.createGraphics();
				g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g4.drawImage(shearBanner, 8, 0, null);
				g4.dispose();
			}
		} else if (xMaterial.equals(XMaterial.PLAYER_HEAD)) {
			try {
				String base64 = SkinUtils.getSkinValue(item.getItemMeta());
				if (base64 != null) {
					Cache<?> cache = Cache.getCache(base64 + PLAYER_HEAD_KEY);
					if (cache == null) {
						JSONObject json = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(base64)));
						String value = ((String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url")).replace(TEXTURE_MINECRAFT_URL, "");
						String url = SKULL_RENDER_URL.replaceFirst("%s", value);
						BufferedImage newSkull = ImageUtils.multiply(ImageUtils.downloadImage(url), 0.9);
						
						BufferedImage newImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
						Graphics2D g2 = newImage.createGraphics();
						g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
						g2.drawImage(newSkull, 4, 3, 23, 28, null);
						g2.dispose();
						
						Cache.putCache(base64 + PLAYER_HEAD_KEY, newImage, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
						itemImage = ImageUtils.copyImage(newImage);
					} else {
						itemImage = ImageUtils.copyImage((BufferedImage) cache.getObject());
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (item.getItemMeta() instanceof LeatherArmorMeta) {
			LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
			Color color = new Color(meta.getColor().asRGB());
			if (xMaterial.equals(XMaterial.LEATHER_HORSE_ARMOR)) {
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(itemImage), color);
				itemImage = ImageUtils.multiply(itemImage, colorOverlay);
			} else {
				BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.getItemTexture(xMaterial.name().toLowerCase() + "_overlay");
				BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(itemImage), color);
				itemImage = ImageUtils.multiply(itemImage, colorOverlay);
				Graphics2D g2 = itemImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(armorOverlay, 0, 0, null);
				g2.dispose();
			}
		} else if (xMaterial.equals(XMaterial.ELYTRA)) {
			int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
			if (durability <= 1) {
				itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("broken_elytra");
			}
		} else if (FilledMapUtils.isFilledMap(item)) {
			BufferedImage filled = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("filled_map_markings");
			ImageUtils.xor(itemImage, filled, 200);
		} else if (xMaterial.equals(XMaterial.CROSSBOW)) {
			CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
			List<ItemStack> charged = meta.getChargedProjectiles();
			if (charged != null && !charged.isEmpty()) {
				ItemStack charge = charged.get(0);
				XMaterial chargeType = XMaterial.matchXMaterial(charge);
				if (chargeType.equals(XMaterial.FIREWORK_ROCKET)) {
					itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("crossbow_firework");
				} else {
					itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("crossbow_arrow");
				}
			}
		} else if (xMaterial.equals(XMaterial.CLOCK)) {
			long time = (player.getPlayerTime() % 24000) - 6000;
			if (time < 0) {
				time += 24000;
			}
			int phase = (int) ((double) time / 24000 * 64);
			itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("clock_" + PHASE_FORMAT.format(phase));
		} else if (xMaterial.equals(XMaterial.COMPASS)) {
			int phase;
			if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16)) {
				CompassMeta meta = (CompassMeta) item.getItemMeta();
				Location target;
				if (meta.hasLodestone()) {
					Location lodestone = meta.getLodestone();
					target = new Location(lodestone.getWorld(), lodestone.getBlockX() + 0.5, lodestone.getY(), lodestone.getZ() + 0.5, lodestone.getYaw(), lodestone.getPitch());
					requiresEnchantmentGlint = true;
				} else if (player.getWorld().getEnvironment().equals(Environment.NORMAL)) {
					Location spawn = player.getWorld().getSpawnLocation();
					target = new Location(spawn.getWorld(), spawn.getBlockX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, spawn.getYaw(), spawn.getPitch());
				} else {
					target = null;
				}
				if (target != null && target.getWorld().equals(player.getWorld())) {
					Location playerLocation = player.getEyeLocation();
					playerLocation.setPitch(0);
					Vector looking = playerLocation.getDirection();
					Vector pointing = target.toVector().subtract(playerLocation.toVector());
					pointing.setY(0);
					double degree = VectorUtils.getBearing(looking, pointing) - 180;
					if (degree < 0) {
						degree += 360;
					}
					phase = (int) (degree / 360 * 32);
				} else {
					phase = RANDOM.nextInt(32);
				}
			} else {
				if (player.getWorld().getEnvironment().equals(Environment.NORMAL)) {
					Location spawn = player.getWorld().getSpawnLocation();
					Location target = new Location(spawn.getWorld(), spawn.getBlockX() + 0.5, spawn.getY(), spawn.getZ() + 0.5, spawn.getYaw(), spawn.getPitch());
					Location playerLocation = player.getEyeLocation();
					playerLocation.setPitch(0);
					Vector looking = playerLocation.getDirection();
					Vector pointing = target.toVector().subtract(playerLocation.toVector());
					pointing.setY(0);
					double degree = VectorUtils.getBearing(looking, pointing) - 180;
					if (degree < 0) {
						degree += 360;
					}
					phase = (int) (degree / 360 * 32);
				} else {
					phase = RANDOM.nextInt(32);
				}
			}
			itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("compass_" + PHASE_FORMAT.format(phase));
		}
		
		if (item.getItemMeta() instanceof PotionMeta) {
			if (xMaterial.equals(XMaterial.TIPPED_ARROW)) {
				itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("tipped_arrow_base");
				PotionMeta meta = (PotionMeta) item.getItemMeta();
				PotionType potiontype = InteractiveChat.version.isOld() ? Potion.fromItemStack(item).getType() : meta.getBasePotionData().getType();
				BufferedImage tippedArrowHead = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("tipped_arrow_head");
				
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
				BufferedImage potionOverlay = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("potion_overlay");
				
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
			BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.getMiscTexture("enchanted_item_glint");
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
			
			String str = Integer.toString(amount);
			int x = 22;
			for (int i = str.length() - 1; i >= 0; i--) {
				BufferedImage charImage = InteractiveChatDiscordSrvAddon.plugin.getFontTexture(str.substring(i, i + 1));
				g4.drawImage(ImageUtils.darken(ImageUtils.copyImage(charImage), 180), x, 21, null);
				g4.drawImage(charImage, x - 2, 19, null);
				x -= 12;
			}
			
			g4.dispose();
			
			itemImage = newItemImage;
		}
		
		return itemImage;
	}
	
	public static BufferedImage getMapImage(ItemStack item) throws Exception {
		if (!FilledMapUtils.isFilledMap(item)) {
			throw new IllegalArgumentException("Provided item is not a filled map");
		}
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		Debug.debug("ImageGeneration creating map image");
		
		BufferedImage background = InteractiveChatDiscordSrvAddon.plugin.getGUITexture("map_background");
		
		BufferedImage image = new BufferedImage(1120, 1120, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g.drawImage(background, 0, 0, 1120, 1120, null);
		g.dispose();
		
		int borderOffset = (int) (image.getWidth() / 23.3333333333333333333);
		int ratio = (image.getWidth() - borderOffset * 2) / 128;
		
		ItemMapWrapper data = new ItemMapWrapper(item);
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
		
		BufferedImage asset = InteractiveChatDiscordSrvAddon.plugin.getGUITexture("map_icons");
		int iconWidth = asset.getWidth() / MAP_ICON_PER_ROLE;
		
		for (MapIcon icon : data.getMapIcons()) {
			int x = icon.getX() + 128;
			int y = icon.getY() + 128;
			double rotation = (360.0 / 16.0 * (double) icon.getRotation()) + 180.0;
			int type = icon.getType().ordinal();
			Component component = icon.getName();
			
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
	
}
