package com.loohp.interactivechatdiscordsrvaddon.Graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.map.MapPalette;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.Utils.FilledMapUtils;
import com.loohp.interactivechat.Utils.HashUtils;
import com.loohp.interactivechatdiscordsrvaddon.Cache;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ItemStackUtils;
import com.loohp.interactivechatdiscordsrvaddon.Utils.PotionUtils;
import com.loohp.interactivechatdiscordsrvaddon.Utils.SkinUtils;
import com.loohp.interactivechatdiscordsrvaddon.Wrappers.ItemMapWrapper;
import com.loohp.interactivechatdiscordsrvaddon.Wrappers.ItemMapWrapper.MapIcon;

import net.md_5.bungee.api.chat.BaseComponent;

@SuppressWarnings("deprecation")
public class ImageGeneration {
	
	private static final String TEXTURE_MINECRAFT_URL = "http://textures.minecraft.net/texture/";
	private static final String PLAYER_RENDER_URL = "https://mc-heads.net/player/%s/64";
	private static final String SKULL_RENDER_URL = "https://mc-heads.net/head/%s/96";
	
	private static final int MAP_ICON_PER_ROLE = 16;
	private static final int SPACING = 36;
	
	private static final double ENCHANTMENT_GLINT_FACTOR = 190.0 / 255.0;
	
	private static final String FULL_BODY_IMAGE_KEY = "FullBodyImage";
	private static final String PLAYER_HEAD_KEY = "PlayerHeadImage";
	private static final String INVENTORY_KEY = "Inventory";
	private static final String PLAYER_INVENTORY_KEY = "PlayerInventory";
	
	public static BufferedImage getItemStackImage(ItemStack item) throws IOException {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		BufferedImage background = new BufferedImage(36, 36, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = background.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		if (item == null || item.getType().equals(Material.AIR)) {
			return background;
		}
		
		BufferedImage itemImage = getRawItemImage(item);
		
		if (itemImage != null) {
			g.drawImage(itemImage, 0, 0, null);
		}
		g.dispose();
		
		return background;
	}
	
	public static BufferedImage getInventoryImage(Inventory inventory) throws Exception {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		InteractiveChatDiscordSrvAddon.plugin.inventoryImageCounter.incrementAndGet();
		
		String key = INVENTORY_KEY + HashUtils.createSha1(inventory);
		Cache<?> cache = Cache.getCache(key);
		if (cache != null) {
			return ImageUtils.copyImage((BufferedImage) cache.getObject());
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
			
			BufferedImage itemImage = getRawItemImage(item);
			
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
		
		String key = PLAYER_INVENTORY_KEY + player.getUniqueId().toString() + HashUtils.createSha1(inventory);
		Cache<?> cache = Cache.getCache(key);
		if (cache != null) {
			return ImageUtils.copyImage((BufferedImage) cache.getObject());
		}
		
		BufferedImage background = InteractiveChatDiscordSrvAddon.plugin.getGUITexture("player_inventory");
		
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
			
			BufferedImage itemImage = getRawItemImage(item);
			
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
			
			BufferedImage itemImage = getRawItemImage(item);
			
			if (itemImage != null) {
				g.drawImage(itemImage, 18 + (SPACING * (i % 9)), 170 + (SPACING * ((i - 9) / 9)), null);
			}
		}
		
		//boots
		ItemStack boots = inventory.getItem(i);
		if (boots == null || boots.getType().equals(Material.AIR)) {
			g.drawImage(InteractiveChatDiscordSrvAddon.plugin.getItemTexture("empty_armor_slot_boots"), 18, 126 - (SPACING * (i - 36)), 32, 32, null);
		} else {
			BufferedImage itemImage = getRawItemImage(boots);			
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
			BufferedImage itemImage = getRawItemImage(leggings);			
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
			BufferedImage itemImage = getRawItemImage(chestplate);			
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
			BufferedImage itemImage = getRawItemImage(helmet);			
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
			BufferedImage itemImage = getRawItemImage(offhand);				
			if (itemImage != null) {
				g.drawImage(itemImage, 162, 126, null);
			}
		}
		
		//puppet
		EntityEquipment equipment = player.getEquipment();
		BufferedImage puppet = getFullBodyImage(player, equipment.getHelmet(), equipment.getChestplate(), equipment.getLeggings(), equipment.getBoots());
		g.drawImage(puppet, 67, 22, null);
		
		g.dispose();
		
		Cache.putCache(key, target, InteractiveChatDiscordSrvAddon.plugin.cacheTimeout);
		
		return target;
	}
	
	private static BufferedImage getFullBodyImage(Player player, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		BufferedImage image;
		try {
			JSONObject json = (JSONObject) new JSONParser().parse(SkinUtils.getSkinJsonFromProfile(player));
			String value = ((String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url")).replace(TEXTURE_MINECRAFT_URL, "");
			boolean slim = false;
			if (((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).containsKey("metadata")) {
				slim = ((JSONObject) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("metadata")).get("model").toString().equals("slim");
			}
			Cache<?> cache = Cache.getCache(player.getUniqueId().toString() + value + FULL_BODY_IMAGE_KEY);
			if (cache == null) {
				String url = PLAYER_RENDER_URL.replaceFirst("%s", value);
				image = ImageIO.read(new URL(url));
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
		} catch (Throwable e) {
			image = InteractiveChatDiscordSrvAddon.plugin.getPuppetTexture("default");
		}
		
		image = ImageUtils.expandCenterAligned(ImageUtils.multiply(image, 0.7), 4);
		
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
			leggingsImage2 = ImageUtils.copyAndGetSubImage(leggingsImage, scale * 4, scale * 20, 8 * scale, 9 * scale);
			
			leggingsImage1 = ImageUtils.multiply(ImageUtils.resizeImageStretch(ImageUtils.resizeImage(leggingsImage1, Math.pow(scale, -1) * 4), 4), 0.7);
			leggingsImage2 = ImageUtils.multiply(ImageUtils.resizeImageStretch(ImageUtils.resizeImage(leggingsImage2, Math.pow(scale, -1) * 4), 4), 0.7);
			if (leggings.getEnchantments().size() > 0) {
				BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.getMiscTexture("enchanted_item_glint");
				BufferedImage tintImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);				
				Graphics2D g2 = tintImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(tint_ori, 0, 0, 512, 512, null);
				g2.dispose();
				
				leggingsImage1 = ImageUtils.additionNonTransparent(leggingsImage1, tintImage, ENCHANTMENT_GLINT_FACTOR);
				leggingsImage2 = ImageUtils.additionNonTransparent(leggingsImage2, tintImage, ENCHANTMENT_GLINT_FACTOR);
			}
			
			g.drawImage(leggingsImage1, 18, 66, 36, 24, null);
			g.drawImage(leggingsImage2, 18, 82, 36, 30, null);
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
			bootsImage = ImageUtils.copyAndGetSubImage(bootsImage, 4 * scale, 26 * scale, 8 * scale, 4 * scale);
			
			bootsImage = ImageUtils.multiply(ImageUtils.resizeImageStretch(ImageUtils.resizeImage(bootsImage, Math.pow(scale, -1) * 4), 8), 0.7);
			if (boots.getEnchantments().size() > 0) {
				BufferedImage tint_ori = InteractiveChatDiscordSrvAddon.plugin.getMiscTexture("enchanted_item_glint");
				BufferedImage tintImage = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);				
				Graphics2D g2 = tintImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(tint_ori, 0, 0, 512, 512, null);
				g2.dispose();
				
				bootsImage = ImageUtils.additionNonTransparent(bootsImage, tintImage, ENCHANTMENT_GLINT_FACTOR);
			}

			g.drawImage(bootsImage, 16, 112, 40, 32, null);
		}
		
		if (ItemStackUtils.isWearable(chestplate)) {
			XMaterial type = XMaterial.matchXMaterial(chestplate);
			BufferedImage chestplateImage = null;
			BufferedImage chestplateImage1 = null;
			BufferedImage chestplateImage2 = null;
			BufferedImage chestplateImage3 = null;
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
			default:
				break;
			}
			chestplateImage1 = ImageUtils.copyAndGetSubImage(chestplateImage, scale * 20, scale * 20, 8 * scale, 12 * scale);				
			chestplateImage2 = ImageUtils.copyAndGetSubImage(chestplateImage, scale * 44, scale * 20, 4 * scale, 12 * scale);
			chestplateImage3 = ImageUtils.copyAndGetSubImage(chestplateImage, scale * 52, scale * 20, 4 * scale, 12 * scale);
			
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
			
			g.drawImage(chestplateImage1, 16, 32, 40, 56, null);
			g.drawImage(chestplateImage2, 0, 32, 24, 56, null);
			g.drawImage(chestplateImage3, 48, 32, 24, 56, null);
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
			
			g.drawImage(helmetImage, 16, 0, 40, 40, null);
		}
		
		g.dispose();
		
		return image;
	}
	
	private static BufferedImage getRawItemImage(ItemStack item) throws IOException {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		int amount = item.getAmount();
		XMaterial xMaterial = XMaterial.matchXMaterial(item);
		String key = xMaterial.name().toLowerCase();
		BufferedImage itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture(key);
		if (itemImage == null) {
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
		} else if (xMaterial.equals(XMaterial.PLAYER_HEAD)) {
			try {
				String base64 = SkullUtils.getSkinValue(item.getItemMeta());
				if (base64 != null) {
					Cache<?> cache = Cache.getCache(base64 + PLAYER_HEAD_KEY);
					if (cache == null) {
						JSONObject json = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(base64)));
						String value = ((String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url")).replace(TEXTURE_MINECRAFT_URL, "");
						String url = SKULL_RENDER_URL.replaceFirst("%s", value);
						BufferedImage newSkull = ImageUtils.multiply(ImageIO.read(new URL(url)), 0.9);
						
						BufferedImage newImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
						Graphics2D g2 = newImage.createGraphics();
						g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
						g2.drawImage(newSkull, 5, 3, 24, 28, null);
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
			BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.getItemTexture(xMaterial.name().toLowerCase() + "_overlay");
			BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(itemImage), color);
			
			itemImage = ImageUtils.multiply(itemImage, colorOverlay);
			
			Graphics2D g2 = itemImage.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.drawImage(armorOverlay, 0, 0, null);
			g2.dispose();
		} else if (xMaterial.equals(XMaterial.ELYTRA)) {
			int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
			if (durability <= 1) {
				itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("broken_elytra");
			}
		} else if (FilledMapUtils.isFilledMap(item)) {
			BufferedImage filled = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("filled_map_markings");
			ImageUtils.xor(itemImage, filled, 200);
		}
		
		boolean tintedPotion = false;
		if (item.getItemMeta() instanceof PotionMeta) {
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
					tintedPotion = true;
				}
			}
		}
		
		if (xMaterial.equals(XMaterial.ENCHANTED_GOLDEN_APPLE) || xMaterial.equals(XMaterial.ENCHANTED_BOOK) || item.getEnchantments().size() > 0 || tintedPotion) {
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
			String tenth = String.valueOf(amount / 10);
			String single = String.valueOf(amount % 10);
			
			BufferedImage firstChar_ori = InteractiveChatDiscordSrvAddon.plugin.getFontTexture(tenth);
			BufferedImage secondChar_ori = InteractiveChatDiscordSrvAddon.plugin.getFontTexture(single);
			
			BufferedImage newItemImage = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g4 = newItemImage.createGraphics();
			g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g4.drawImage(itemImage, 0, 0, null);
			if (amount > 9) {
				g4.drawImage(ImageUtils.darken(ImageUtils.copyImage(firstChar_ori), 180), 10, 21, null);
				g4.drawImage(firstChar_ori, 8, 19, null);
			}
			g4.drawImage(ImageUtils.darken(ImageUtils.copyImage(secondChar_ori), 180), 22, 21, null);
			g4.drawImage(secondChar_ori, 20, 19, null);
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
			BaseComponent baseComponent = icon.getName();
			
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
            
            if (baseComponent != null && MCFont.isWorking()) {
            	ImageUtils.printComponentNoShadow(image, baseComponent, imageX, imageY + 32, 30, true);
            }
		}
		g2.dispose();
		
		return image;
	}

}
