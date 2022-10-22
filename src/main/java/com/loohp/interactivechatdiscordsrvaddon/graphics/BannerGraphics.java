/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.interactivechatdiscordsrvaddon.graphics;

import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.PatternTypeWrapper;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class BannerGraphics {

    public static BannerAssetResult generateBannerAssets(ItemStack item) {
        BufferedImage baseImage = InteractiveChatDiscordSrvAddon.plugin.getResourceManager().getTextureManager().getTexture(ResourceRegistry.ENTITY_TEXTURE_LOCATION + "banner_base").getTexture(64, 64);
        BufferedImage patternsImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);

        ICMaterial icMaterial = ICMaterial.from(item);
        String colorName = icMaterial.name().replace("_BANNER", "");
        Color baseColor = new Color(DyeColor.valueOf(colorName.toUpperCase()).getColor().asRGB());

        BufferedImage baseTint = new BufferedImage(42, 41, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = baseTint.createGraphics();
        g.setColor(baseColor);
        g.fillRect(0, 0, 42, 41);
        g.dispose();

        baseImage = ImageUtils.add(ImageUtils.multiply(baseImage, baseTint, true), 40);

        if (!item.hasItemMeta()) {
            return new BannerAssetResult(baseImage, patternsImage);
        }

        List<Pattern> patterns;
        if (!(item.getItemMeta() instanceof BannerMeta)) {
            if (!(item.getItemMeta() instanceof BlockStateMeta)) {
                return new BannerAssetResult(baseImage, patternsImage);
            }
            BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
            Banner bannerBlockMeta = (Banner) bmeta.getBlockState();
            patterns = bannerBlockMeta.getPatterns();
        } else {
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            patterns = meta.getPatterns();
        }

        Graphics2D g2 = patternsImage.createGraphics();

        for (Pattern pattern : patterns) {
            PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
            Color color = new Color(pattern.getColor().getColor().asRGB());
            BufferedImage image = InteractiveChatDiscordSrvAddon.plugin.getResourceManager().getTextureManager().getTexture(ResourceRegistry.BANNER_TEXTURE_LOCATION + type.getAssetName()).getTexture(64, 64);
            BufferedImage tint = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g3 = tint.createGraphics();
            g3.setColor(color);
            g3.fillRect(0, 0, 64, 64);
            g3.dispose();
            image = ImageUtils.add(ImageUtils.multiply(image, tint), 40);
            g2.drawImage(image, 0, 0, null);
        }
        g2.dispose();

        return new BannerAssetResult(baseImage, patternsImage);
    }

    @SuppressWarnings("deprecation")
    public static BannerAssetResult generateShieldAssets(ItemStack item) {
        if (!item.hasItemMeta()) {
            return getDefaultShieldAssets();
        }

        List<Pattern> patterns;
        Color baseColor;
        if (!(item.getItemMeta() instanceof BannerMeta)) {
            if (!(item.getItemMeta() instanceof BlockStateMeta)) {
                return getDefaultShieldAssets();
            }
            BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
            if (!bmeta.hasBlockState()) {
                return getDefaultShieldAssets();
            }
            Banner bannerBlockMeta = (Banner) bmeta.getBlockState();
            patterns = bannerBlockMeta.getPatterns();
            baseColor = new Color(bannerBlockMeta.getBaseColor().getColor().asRGB());
        } else {
            BannerMeta meta = (BannerMeta) item.getItemMeta();
            patterns = meta.getPatterns();
            baseColor = new Color(meta.getBaseColor().getColor().asRGB());
        }

        BufferedImage baseImage = InteractiveChatDiscordSrvAddon.plugin.getResourceManager().getTextureManager().getTexture(ResourceRegistry.ENTITY_TEXTURE_LOCATION + "shield_base").getTexture(64, 64);
        BufferedImage patternsImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);

        BufferedImage baseTint = new BufferedImage(12, 22, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = baseTint.createGraphics();
        g.setColor(baseColor);
        g.fillRect(2, 2, 10, 20);
        g.dispose();

        baseImage = ImageUtils.add(ImageUtils.multiply(baseImage, baseTint, true), 40);

        Graphics2D g2 = patternsImage.createGraphics();

        for (Pattern pattern : patterns) {
            PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
            Color color = new Color(pattern.getColor().getColor().asRGB());
            BufferedImage image = InteractiveChatDiscordSrvAddon.plugin.getResourceManager().getTextureManager().getTexture(ResourceRegistry.SHIELD_TEXTURE_LOCATION + type.getAssetName()).getTexture(64, 64);
            BufferedImage tint = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g3 = tint.createGraphics();
            g3.setColor(color);
            g3.fillRect(0, 0, 64, 64);
            g3.dispose();
            image = ImageUtils.add(ImageUtils.multiply(image, tint), 40);
            g2.drawImage(image, 0, 0, null);
        }
        g2.dispose();

        return new BannerAssetResult(baseImage, patternsImage);
    }

    private static BannerAssetResult getDefaultShieldAssets() {
        return new BannerAssetResult(InteractiveChatDiscordSrvAddon.plugin.getResourceManager().getTextureManager().getTexture(ResourceRegistry.ENTITY_TEXTURE_LOCATION + "shield_base_nopattern").getTexture(), new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB));
    }

    public static class BannerAssetResult {

        private BufferedImage base;
        private BufferedImage patterns;

        public BannerAssetResult(BufferedImage base, BufferedImage patterns) {
            this.base = base;
            this.patterns = patterns;
        }

        public BufferedImage getBase() {
            return base;
        }

        public BufferedImage getPatterns() {
            return patterns;
        }

    }

}
