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

package com.loohp.interactivechatdiscordsrvaddon.utils;

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
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.GeneratedTextureResource;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;
import com.loohp.interactivechatdiscordsrvaddon.utils.TintUtils.SpawnEggTintData;
import com.loohp.interactivechatdiscordsrvaddon.utils.TintUtils.TintIndexData;
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

        TintIndexData tintIndexData = TintUtils.getTintData(xMaterial);
        Map<ModelOverrideType, Float> predicates = new EnumMap<>(ModelOverrideType.class);
        Map<String, TextureResource> providedTextures = new HashMap<>();
        if (NBTEditor.contains(item, "CustomModelData")) {
            int customModelData = NBTEditor.getInt(item, "CustomModelData");
            predicates.put(ModelOverrideType.CUSTOM_MODEL_DATA, (float) customModelData);
        }
        if (xMaterial.equals(XMaterial.CHEST) || xMaterial.equals(XMaterial.TRAPPED_CHEST)) {
            LocalDate time = LocalDate.now();
            if (time.getMonth().equals(Month.DECEMBER) && (time.getDayOfMonth() == 24 || time.getDayOfMonth() == 25 || time.getDayOfMonth() == 26)) {
                directLocation = ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + "christmas_chest";
            }
        } else if (xMaterial.isOneOf(Arrays.asList("CONTAINS:banner"))) {
            BannerAssetResult bannerAsset = BannerGraphics.generateBannerAssets(item);
            providedTextures.put(ResourceRegistry.BANNER_BASE_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(bannerAsset.getBase()));
            providedTextures.put(ResourceRegistry.BANNER_PATTERNS_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(bannerAsset.getPatterns()));
        } else if (xMaterial.equals(XMaterial.SHIELD)) {
            BannerAssetResult shieldAsset = BannerGraphics.generateShieldAssets(item);
            providedTextures.put(ResourceRegistry.SHIELD_BASE_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(shieldAsset.getBase()));
            providedTextures.put(ResourceRegistry.SHIELD_PATTERNS_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(shieldAsset.getPatterns()));
        } else if (xMaterial.equals(XMaterial.PLAYER_HEAD)) {
            BufferedImage skinImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ENTITY_TEXTURE_LOCATION + "steve").getTexture();
            try {
                String base64 = SkinUtils.getSkinValue(item.getItemMeta());
                if (base64 != null) {
                    JSONObject json = (JSONObject) new JSONParser().parse(new String(Base64.getDecoder().decode(base64)));
                    String value = (String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url");
                    skinImage = ImageUtils.downloadImage(value);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            providedTextures.put(ResourceRegistry.SKIN_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(ModelUtils.convertToModernSkinTexture(skinImage)));
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
            ICPlayer onlinePlayer = player.getPlayer();
            long time = ((onlinePlayer != null && onlinePlayer.isLocal() ? ((ICPlayer) player).getLocalPlayer().getPlayerTime() : Bukkit.getWorlds().get(0).getTime()) % 24000) - 6000;
            if (time < 0) {
                time += 24000;
            }
            double timePhase = (double) time / 24000;
            predicates.put(ModelOverrideType.TIME, (float) (timePhase - 0.0078125));
        } else if (xMaterial.equals(XMaterial.COMPASS)) {
            double angle;
            ICPlayer icplayer = player.getPlayer();
            if (icplayer != null && icplayer.isLocal()) {
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
                Object levelObj = ((Map<?, Object>) blockStateObj).get("level");
                if (levelObj != null) {
                    try {
                        level = Integer.parseInt(levelObj.toString().replace("i", ""));
                    } catch (NumberFormatException e) {
                    }
                }
            }
            predicates.put(ModelOverrideType.LEVEL, (float) level / 16F);
        } else if (item.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            Color color = new Color(meta.getColor().asRGB());
            if (xMaterial.equals(XMaterial.LEATHER_HORSE_ARMOR)) {
                BufferedImage itemImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + xMaterial.name().toLowerCase()).getTexture(32, 32);
                BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(itemImage), color);
                itemImage = ImageUtils.multiply(itemImage, colorOverlay);
                providedTextures.put(ResourceRegistry.LEATHER_HORSE_ARMOR_PLACEHOLDER, new GeneratedTextureResource(itemImage));
            } else {
                BufferedImage itemImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + xMaterial.name().toLowerCase()).getTexture(32, 32);
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
                BufferedImage tippedArrowHead = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "tipped_arrow_head").getTexture(32, 32);

                int color;
                try {
                    if (meta.hasColor()) {
                        color = meta.getColor().asRGB();
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
                BufferedImage potionOverlay = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "potion_overlay").getTexture(32, 32);

                int color;
                try {
                    if (meta.hasColor()) {
                        color = meta.getColor().asRGB();
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
        } else if (xMaterial.isOneOf(Arrays.asList("CONTAINS:spawn_egg"))) {
            SpawnEggTintData tintData = TintUtils.getSpawnEggTint(xMaterial);
            if (tintData != null) {
                BufferedImage baseImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "spawn_egg").getTexture();
                BufferedImage overlayImage = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "spawn_egg_overlay").getTexture(baseImage.getWidth(), baseImage.getHeight());

                BufferedImage colorBase = ImageUtils.changeColorTo(ImageUtils.copyImage(baseImage), tintData.getBase());
                BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(overlayImage), tintData.getOverlay());

                baseImage = ImageUtils.multiply(baseImage, colorBase);
                overlayImage = ImageUtils.multiply(overlayImage, colorOverlay);

                providedTextures.put(ResourceRegistry.SPAWN_EGG_PLACEHOLDER, new GeneratedTextureResource(baseImage));
                providedTextures.put(ResourceRegistry.SPAWN_EGG_OVERLAY_PLACEHOLDER, new GeneratedTextureResource(overlayImage));
            }
        } else if (InteractiveChat.version.isLegacy() && xMaterial.isOneOf(Arrays.asList("CONTAINS:bed"))) {
            String colorName = xMaterial.name().replace("_BED", "").toLowerCase();
            BufferedImage bedTexture = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.ENTITY_TEXTURE_LOCATION + "bed/" + colorName).getTexture();
            providedTextures.put(ResourceRegistry.LEGACY_BED_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(bedTexture));
        }
        return new ItemStackProcessResult(requiresEnchantmentGlint, predicates, providedTextures, tintIndexData, directLocation);
    }

    public static class ItemStackProcessResult {

        private boolean requiresEnchantmentGlint;
        private Map<ModelOverrideType, Float> predicates;
        private Map<String, TextureResource> providedTextures;
        private TintIndexData tintIndexData;
        private String directLocation;

        public ItemStackProcessResult(boolean requiresEnchantmentGlint, Map<ModelOverrideType, Float> predicates, Map<String, TextureResource> providedTextures, TintIndexData tintIndexData, String directLocation) {
            this.requiresEnchantmentGlint = requiresEnchantmentGlint;
            this.predicates = predicates;
            this.providedTextures = providedTextures;
            this.directLocation = directLocation;
            this.tintIndexData = tintIndexData;
        }

        public boolean requiresEnchantmentGlint() {
            return requiresEnchantmentGlint;
        }

        public Map<ModelOverrideType, Float> getPredicates() {
            return predicates;
        }

        public Map<String, TextureResource> getProvidedTextures() {
            return providedTextures;
        }

        public TintIndexData getTintIndexData() {
            return tintIndexData;
        }

        public String getDirectLocation() {
            return directLocation;
        }

    }

}
