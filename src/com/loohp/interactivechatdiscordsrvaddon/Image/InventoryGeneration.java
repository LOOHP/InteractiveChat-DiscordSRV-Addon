package com.loohp.interactivechatdiscordsrvaddon.Image;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cryptomorin.xseries.SkullUtils;
import com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.Utils.BannerUtils;
import com.loohp.interactivechatdiscordsrvaddon.Utils.CustomImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.Utils.PotionUtils;

@SuppressWarnings("deprecation")
public class InventoryGeneration {
	
	private static final int spacing = 36;
	
	public static BufferedImage getImage(Inventory inventory) throws IOException {
		InteractiveChatDiscordSrvAddon.plugin.imageCounter.incrementAndGet();
		int rows = inventory.getSize() / 9;
		BufferedImage background = InteractiveChatDiscordSrvAddon.plugin.getGUITexture(rows + "_rows");
		
		BufferedImage target = new BufferedImage(background.getWidth(), background.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) target.getGraphics();
		g.drawImage(background, 0, 0, null);
		
		for (int i = 0; i < inventory.getSize(); i++) {
			ItemStack item = inventory.getItem(i);
			if (item == null || item.getType().equals(Material.AIR)) {
				continue;
			}
			
			int amount = item.getAmount();
			XMaterial xMaterial = XMaterial.matchXMaterial(item);
			String key = xMaterial.name().toLowerCase();
			
			BufferedImage itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture(key);
			if (itemImage == null) {
				itemImage = InteractiveChatDiscordSrvAddon.plugin.getBlockTexture(key);
				if (itemImage == null) {
					continue;
				}
			}
			
			if (xMaterial.isOneOf(Arrays.asList("CONTAINS:Banner"))) {
				BufferedImage banner = BannerUtils.generateBannerImage(item);
				
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
				BufferedImage banner = BannerUtils.generateShieldImage(item);
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
					String base64 = SkullUtils.getSkinValue(item);
					if (base64 != null) {
						JSONObject json = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(base64)));
						String value = ((String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url")).replace("http://textures.minecraft.net/texture/", "");					
						String url = "https://mc-heads.net/head/" + value + "/96";
						BufferedImage newSkull = ImageIO.read(new URL(url));
						
						BufferedImage newImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
						Graphics2D g2 = newImage.createGraphics();
						g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
						g2.drawImage(newSkull, 5, 3, 24, 28, null);
						g2.dispose();
						
						itemImage = newImage;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if (xMaterial.equals(XMaterial.LEATHER_HELMET) || xMaterial.equals(XMaterial.LEATHER_CHESTPLATE) || xMaterial.equals(XMaterial.LEATHER_LEGGINGS) || xMaterial.equals(XMaterial.LEATHER_BOOTS)) {
				LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
				Color color = new Color(meta.getColor().asRGB());
				BufferedImage armorOverlay = InteractiveChatDiscordSrvAddon.plugin.getItemTexture(xMaterial.name().toLowerCase() + "_overlay");
				BufferedImage colorOverlay = CustomImageUtils.changeColorTo(CustomImageUtils.copyImage(itemImage), color);
				
				itemImage = CustomImageUtils.multiply(itemImage, colorOverlay);
				
				Graphics2D g2 = itemImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(armorOverlay, 0, 0, null);
				g2.dispose();
			} else if (xMaterial.equals(XMaterial.ELYTRA)) {
				int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
				if (durability <= 1) {
					itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture("broken_elytra");
				}
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
				
				BufferedImage colorOverlay = CustomImageUtils.changeColorTo(CustomImageUtils.copyImage(potionOverlay), color);
				potionOverlay = CustomImageUtils.multiply(potionOverlay, colorOverlay);
				
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
				
				itemImage = CustomImageUtils.additionNonTransparent(itemImage, tintImage);
			}
			
			if (xMaterial.isDamageable()) {
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
					g4.drawImage(CustomImageUtils.darken(CustomImageUtils.copyImage(firstChar_ori), 180), 10, 21, null);
					g4.drawImage(firstChar_ori, 8, 19, null);
				}
				g4.drawImage(CustomImageUtils.darken(CustomImageUtils.copyImage(secondChar_ori), 180), 22, 21, null);
				g4.drawImage(secondChar_ori, 20, 19, null);
				g4.dispose();
				
				itemImage = newItemImage;
			}
			
			g.drawImage(itemImage, 18 + (spacing * (i % 9)), 18 + (spacing * (i / 9)), null);
		}
		g.dispose();
		
		return target;
	}

}
