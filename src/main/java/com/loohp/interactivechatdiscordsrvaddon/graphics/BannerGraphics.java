package com.loohp.interactivechatdiscordsrvaddon.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;

import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.PatternTypeWrapper;

public class BannerGraphics {
	
	public static BufferedImage generateBannerImage(ItemStack item) {		
		BufferedImage banner = new BufferedImage(20, 40, BufferedImage.TYPE_INT_ARGB);
		
		if (!item.hasItemMeta()) {
			return banner;
		}
		
		List<Pattern> patterns;
		if (!(item.getItemMeta() instanceof BannerMeta)) {
			if (!(item.getItemMeta() instanceof BlockStateMeta)) {
				return banner;
			}
			BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
            Banner bannerBlockMeta = (Banner) bmeta.getBlockState();
            patterns = bannerBlockMeta.getPatterns();
		} else {
			BannerMeta meta = (BannerMeta) item.getItemMeta();
			patterns = meta.getPatterns();
		}	
		
		Graphics2D g = banner.createGraphics();
		
		for (Pattern pattern : patterns) {
			PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
			Color color = new Color(pattern.getColor().getColor().asRGB());
			BufferedImage image = ImageUtils.copyAndGetSubImage(InteractiveChatDiscordSrvAddon.plugin.getBannerTexture(type.getAssetName()), 1, 1, 20, 40);
			BufferedImage colored = ImageUtils.changeColorTo(ImageUtils.copyImage(image), color);
			
			BufferedImage output = ImageUtils.multiply(image, colored);
			g.drawImage(output, 0, 0, null);
		}
		g.dispose();
		
		return banner;
	}
	
	@SuppressWarnings("deprecation")
	public static BufferedImage generateShieldImage(ItemStack item) {		
		BufferedImage banner = new BufferedImage(20, 40, BufferedImage.TYPE_INT_ARGB);
		
		if (!item.hasItemMeta()) {
			return null;
		}
		
		List<Pattern> patterns;
		Color base;
		if (!(item.getItemMeta() instanceof BannerMeta)) {
			if (!(item.getItemMeta() instanceof BlockStateMeta)) {
				return null;
			}
			BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
			if (!bmeta.hasBlockState()) {
				return null;
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
			PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
			Color color = new Color(pattern.getColor().getColor().asRGB());
			BufferedImage image = ImageUtils.copyAndGetSubImage(InteractiveChatDiscordSrvAddon.plugin.getBannerTexture(type.getAssetName()), 1, 1, 20, 40);
			BufferedImage colored = ImageUtils.changeColorTo(ImageUtils.copyImage(image), color);
			
			BufferedImage output = ImageUtils.multiply(image, colored);
			g.drawImage(output, 0, 0, null);
		}
		g.dispose();
		
		return banner;
	}

}
