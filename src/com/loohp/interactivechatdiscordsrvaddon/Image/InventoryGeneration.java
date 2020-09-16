package com.loohp.interactivechatdiscordsrvaddon.Image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
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
			
			Material material = item.getType();
			int amount = item.getAmount();
			XMaterial xMaterial = XMaterial.matchXMaterial(material);
			String key = xMaterial.name().toLowerCase();
			
			BufferedImage itemImage = InteractiveChatDiscordSrvAddon.plugin.getItemTexture(key);
			if (itemImage == null) {
				itemImage = InteractiveChatDiscordSrvAddon.plugin.getBlockTexture(key);
				if (itemImage == null) {
					continue;
				}
			}
			
			if (xMaterial.equals(XMaterial.PLAYER_HEAD)) {
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
						g2.drawImage(newSkull, 5, 4, 24, 27, null);
						g2.dispose();
						
						itemImage = newImage;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			boolean tintedPotion = false;
			if (xMaterial.equals(XMaterial.POTION)) {
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
				
				potionOverlay = CustomImageUtils.darken(CustomImageUtils.tint(potionOverlay, color), 150);
				
				BufferedImage newItemImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = newItemImage.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				g2.drawImage(itemImage, 0, 0, null);
				g2.drawImage(potionOverlay, 0, 0, null);
				g2.dispose();
				
				itemImage = newItemImage;
				
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
					
					BufferedImage newItemImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g4 = newItemImage.createGraphics();
					g4.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
					g4.drawImage(itemImage, 0, 0, null);
					g4.setColor(Color.black);
					g4.fillPolygon(new int[] {4, 30, 30, 4}, new int[] {26, 26, 30, 30}, 4);
					g4.setColor(color);
					g4.fillPolygon(new int[] {4, 4 + length, 4 + length, 4}, new int[] {26, 26, 28, 28}, 4);
					g4.dispose();
					
					itemImage = newItemImage;
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
		return target;
	}

}
