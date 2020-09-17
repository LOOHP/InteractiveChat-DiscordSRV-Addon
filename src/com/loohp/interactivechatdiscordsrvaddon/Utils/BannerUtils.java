package com.loohp.interactivechatdiscordsrvaddon.Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;

import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;

public class BannerUtils {
	
	public static BufferedImage generateBannerImage(ItemStack item) {		
		BufferedImage banner = new BufferedImage(20, 40, BufferedImage.TYPE_INT_ARGB);
		
		if (!item.hasItemMeta()) {
			return banner;
		}
		
		List<Pattern> patterns;
		if (!(item.getItemMeta() instanceof BannerMeta)) {
			BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
			if (!(bmeta instanceof BlockStateMeta)) {
				return banner;
			}
            Banner bannerBlockMeta = (Banner) bmeta.getBlockState();
            patterns = bannerBlockMeta.getPatterns();
		} else {
			BannerMeta meta = (BannerMeta) item.getItemMeta();
			patterns = meta.getPatterns();
		}	
		
		Graphics2D g = banner.createGraphics();
		
		for (Pattern pattern : patterns) {
			PatternType type = pattern.getPattern();
			Color color = new Color(pattern.getColor().getColor().asRGB());
			BufferedImage image = CustomImageUtils.copyAndGetSubImage(InteractiveChatDiscordSrvAddon.plugin.getBannerTexture(type.name().toLowerCase()), 1, 1, 20, 40);
			BufferedImage colored = CustomImageUtils.changeColorTo(CustomImageUtils.copyImage(image), color);
			
			BufferedImage output = CustomImageUtils.multiply(image, colored);
			g.drawImage(output, 0, 0, null);
		}
		g.dispose();
		
		return banner;
	}
	
	@SuppressWarnings("deprecation")
	public static BufferedImage generateShieldImage(ItemStack item) {		
		BufferedImage banner = new BufferedImage(20, 40, BufferedImage.TYPE_INT_ARGB);
		
		if (!item.hasItemMeta()) {
			return banner;
		}
		
		List<Pattern> patterns;
		Color base;
		if (!(item.getItemMeta() instanceof BannerMeta)) {
			BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
			if (!(bmeta instanceof BlockStateMeta)) {
				return banner;
			}
            Banner bannerBlockMeta = (Banner) bmeta.getBlockState();
            patterns = bannerBlockMeta.getPatterns();
            base = new Color(bannerBlockMeta.getBaseColor().getColor().asRGB());
		} else {
			BannerMeta meta = (BannerMeta) item.getItemMeta();
			patterns = meta.getPatterns();
			base = new Color(meta.getBaseColor().getColor().asRGB());
		}
		
		Graphics2D g = banner.createGraphics();
		g.setColor(base);
		g.fillPolygon(new int[] {0, 20, 20, 0}, new int[] {0, 0, 40, 40}, 4);
		
		for (Pattern pattern : patterns) {
			PatternType type = pattern.getPattern();
			Color color = new Color(pattern.getColor().getColor().asRGB());
			BufferedImage image = CustomImageUtils.copyAndGetSubImage(InteractiveChatDiscordSrvAddon.plugin.getBannerTexture(type.name().toLowerCase()), 1, 1, 20, 40);
			BufferedImage colored = CustomImageUtils.changeColorTo(CustomImageUtils.copyImage(image), color);
			
			BufferedImage output = CustomImageUtils.multiply(image, colored);
			g.drawImage(output, 0, 0, null);
		}
		g.dispose();
		
		return banner;
	}

}
