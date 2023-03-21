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
import com.loohp.interactivechat.libs.net.querz.nbt.io.SNBTUtil;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.CompoundTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.IntArrayTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.ListTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.StringTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.Tag;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.libs.org.json.simple.parser.ParseException;
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechat.utils.CompassUtils;
import com.loohp.interactivechat.utils.FilledMapUtils;
import com.loohp.interactivechat.utils.ItemNBTUtils;
import com.loohp.interactivechat.utils.ItemStackUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NBTParsingUtils;
import com.loohp.interactivechat.utils.SkinUtils;
import com.loohp.interactivechatdiscordsrvaddon.graphics.BannerGraphics;
import com.loohp.interactivechatdiscordsrvaddon.graphics.BannerGraphics.BannerAssetResult;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.CustomItemTextureRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer.RawEnchantmentGlintData;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.BlockModel;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.EnchantmentProperties.OpenGLBlending;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.EnchantmentGlintType;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.GeneratedTextureResource;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;
import com.loohp.interactivechatdiscordsrvaddon.utils.TintUtils.SpawnEggTintData;
import com.loohp.interactivechatdiscordsrvaddon.utils.TintUtils.TintIndexData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
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
import java.util.Base64;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class ItemRenderUtils {

    public static final Pattern WEIRD_SKULL_TEXTURE_PATTERN = Pattern.compile("\\{(\\\"?textures\\\"?):\\{(?:(\\\"?SKIN\\\"?):\\{(?:(\\\"?url\\\"?):\\\".*:\\/\\/.*\\\")?})?}}");
    public static final UnaryOperator<String> FIX_WEIRD_SKULL_TEXTURE = str -> {
        str = str.trim();
        StringBuilder sb = new StringBuilder(str);
        Matcher matcher = WEIRD_SKULL_TEXTURE_PATTERN.matcher(str);
        if (matcher.find()) {
            int offset = 0;
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                if (group != null) {
                    if (!group.startsWith("\"")) {
                        sb.insert(matcher.start(i) + offset++, "\"");
                    }
                    if (!group.endsWith("\"")) {
                        sb.insert(matcher.end(i) + offset++, "\"");
                    }
                }
            }
        }
        return sb.toString();
    };

    private static final Random RANDOM = new Random();

    public static ItemStackProcessResult processItemForRendering(ResourceManager manager, OfflineICPlayer player, ItemStack item, EquipmentSlot slot, boolean is1_8, String language) throws IOException {
        World world = null;
        LivingEntity livingEntity = null;
        if (player.isOnline() && player.getPlayer().isLocal()) {
            livingEntity = player.getPlayer().getLocalPlayer();
            world = livingEntity.getWorld();
        }

        boolean requiresEnchantmentGlint = false;
        ICMaterial icMaterial = ICMaterial.from(item);
        String directLocation = null;
        if (icMaterial.isMaterial(XMaterial.DEBUG_STICK)) {
            requiresEnchantmentGlint = true;
        } else if (icMaterial.isMaterial(XMaterial.ENCHANTED_GOLDEN_APPLE)) {
            requiresEnchantmentGlint = true;
        } else if (icMaterial.isMaterial(XMaterial.WRITTEN_BOOK)) {
            requiresEnchantmentGlint = true;
        } else if (icMaterial.isMaterial(XMaterial.ENCHANTED_BOOK)) {
            requiresEnchantmentGlint = true;
        } else if (item.getEnchantments().size() > 0) {
            requiresEnchantmentGlint = true;
        }

        List<ValuePairs<TextureResource, OpenGLBlending>> enchantmentGlintResource = manager.getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getEnchantmentGlintOverrideTextures(null, item, () -> ImageGeneration.getDefaultEnchantmentTint(EnchantmentGlintType.ITEM), manager.getLanguageManager().getTranslateFunction().ofLanguage(language));
        BiFunction<BufferedImage, EnchantmentGlintType, BufferedImage> enchantmentGlintFunction = (image, type) -> ImageGeneration.getEnchantedImage(enchantmentGlintResource, image);
        BiFunction<BufferedImage, EnchantmentGlintType, RawEnchantmentGlintData> rawEnchantmentGlintFunction = (image, type) -> new RawEnchantmentGlintData(enchantmentGlintResource.stream().map(each -> ImageGeneration.getRawEnchantedImage(each.getFirst(), image)).collect(Collectors.toList()), enchantmentGlintResource.stream().map(each -> each.getSecond()).collect(Collectors.toList()));

        TintIndexData tintIndexData = TintUtils.getTintData(icMaterial);
        Map<ModelOverrideType, Float> predicates = new EnumMap<>(ModelOverrideType.class);
        Map<String, TextureResource> providedTextures = new HashMap<>();

        if (!player.isRightHanded()) {
            predicates.put(ModelOverrideType.LEFTHANDED, 1F);
        }
        if (NBTEditor.contains(item, "CustomModelData")) {
            int customModelData = NBTEditor.getInt(item, "CustomModelData");
            predicates.put(ModelOverrideType.CUSTOM_MODEL_DATA, (float) customModelData);
        }
        if (item.getType().getMaxDurability() > 0) {
            int maxDur = item.getType().getMaxDurability();
            int damage = InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage();
            predicates.put(ModelOverrideType.DAMAGE, (float) damage / (float) maxDur);
            predicates.put(ModelOverrideType.DAMAGED, DiscordItemStackUtils.isUnbreakable(item) || (damage <= 0) ? 0F : 1F);
        }

        if (icMaterial.isMaterial(XMaterial.CHEST) || icMaterial.isMaterial(XMaterial.TRAPPED_CHEST)) {
            LocalDate time = LocalDate.now();
            Month month = time.getMonth();
            int day = time.getDayOfMonth();
            if (month.equals(Month.DECEMBER) && (day == 24 || day == 25 || day == 26)) {
                directLocation = ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + "christmas_chest";
            }
        } else if (icMaterial.isOneOf(Collections.singletonList("CONTAINS:banner")) && !icMaterial.isOneOf(Collections.singletonList("CONTAINS:banner_pattern"))) {
            BannerAssetResult bannerAsset = BannerGraphics.generateBannerAssets(item);
            providedTextures.put(ResourceRegistry.BANNER_BASE_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(manager, bannerAsset.getBase()));
            providedTextures.put(ResourceRegistry.BANNER_PATTERNS_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(manager, bannerAsset.getPatterns()));
        } else if (icMaterial.isMaterial(XMaterial.SHIELD)) {
            BannerAssetResult shieldAsset = BannerGraphics.generateShieldAssets(item);
            providedTextures.put(ResourceRegistry.SHIELD_BASE_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(manager, shieldAsset.getBase()));
            providedTextures.put(ResourceRegistry.SHIELD_PATTERNS_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(manager, shieldAsset.getPatterns()));
            predicates.put(ModelOverrideType.BLOCKING, 0F);
            ICPlayer icplayer = player.getPlayer();
            if (icplayer != null && icplayer.isLocal()) {
                int cooldown = icplayer.getLocalPlayer().getCooldown(item.getType());
                predicates.put(ModelOverrideType.COOLDOWN, (float) cooldown / (float) ResourceRegistry.SHIELD_COOLDOWN);
            }
        } else if (icMaterial.isMaterial(XMaterial.PLAYER_HEAD)) {
            BufferedImage skinImage = manager.getTextureManager().getTexture(ResourceRegistry.DEFAULT_WIDE_SKIN_LOCATION).getTexture();
            if (item.getItemMeta() != null) {
                CompoundTag itemCompoundTag = ((CompoundTag) NBTParsingUtils.fromSNBT(ItemNBTUtils.getNMSItemStackJson(item))).getCompoundTag("tag");
                if (itemCompoundTag != null) {
                    Tag<?> skullOwnerTag = itemCompoundTag.get("SkullOwner");
                    try {
                        String skinURL = null;
                        if (skullOwnerTag instanceof StringTag) {
                            skinURL = SkinUtils.getSkinURLFromUUID(Bukkit.getOfflinePlayer(((StringTag) skullOwnerTag).getValue()).getUniqueId());
                        } else if (skullOwnerTag instanceof CompoundTag) {
                            CompoundTag skullOwnerCompoundTag = (CompoundTag) skullOwnerTag;
                            if (ResourceRegistry.RESOURCE_PACK_VERSION >= 12 && skullOwnerCompoundTag.containsKey("Id")) {
                                Tag<?> uuidTag = ((CompoundTag) skullOwnerTag).get("Id");
                                UUID uuid = null;
                                try {
                                    if (uuidTag instanceof StringTag) {
                                        uuid = UUID.fromString(((StringTag) uuidTag).getValue());
                                    } else if (uuidTag instanceof IntArrayTag) {
                                        int[] array = ((IntArrayTag) uuidTag).getValue();
                                        uuid = new UUID((long) array[0] << 32 | (long) array[1] & 4294967295L, (long) array[2] << 32 | (long) array[3] & 4294967295L);
                                    }
                                } catch (Throwable e) {
                                    new IllegalArgumentException("Skull contains invalid UUID: \n" + SNBTUtil.toSNBT(uuidTag), e).printStackTrace();
                                }
                                if (uuid != null) {
                                    skinImage = manager.getTextureManager().getTexture(DefaultSkinUtils.getTexture(uuid)).getTexture();
                                }
                            }
                            CompoundTag propertiesTag = (CompoundTag) skullOwnerCompoundTag.get("Properties");
                            if (propertiesTag != null) {
                                ListTag<?> texturesTag = (ListTag<?>) propertiesTag.get("textures");
                                if (texturesTag != null && texturesTag.size() > 0) {
                                    StringTag valueTag = (StringTag) ((CompoundTag) texturesTag.get(0)).get("Value");
                                    if (valueTag != null) {
                                        String json = FIX_WEIRD_SKULL_TEXTURE.apply(new String(Base64.getDecoder().decode(valueTag.getValue())));
                                        try {
                                            JSONObject texturesJson = (JSONObject) ((JSONObject) new JSONParser().parse(json)).get("textures");
                                            if (texturesJson != null) {
                                                JSONObject skinJson = (JSONObject) texturesJson.get("SKIN");
                                                if (skinJson != null) {
                                                    skinURL = (String) skinJson.get("url");
                                                }
                                            }
                                        } catch (ParseException e) {
                                            new IllegalArgumentException("Skull contains illegal texture data: \n" + json, e).printStackTrace();
                                        }
                                    }
                                }
                            }
                            if (skinURL == null) {
                                Tag<?> uuidTag = ((CompoundTag) skullOwnerTag).get("Id");
                                if (uuidTag != null) {
                                    if (uuidTag instanceof StringTag) {
                                        try {
                                            skinURL = SkinUtils.getSkinURLFromUUID(UUID.fromString(((StringTag) uuidTag).getValue()));
                                        } catch (IllegalArgumentException ignore) {
                                        }
                                    } else if (uuidTag instanceof IntArrayTag) {
                                        int[] array = ((IntArrayTag) uuidTag).getValue();
                                        if (array.length == 4) {
                                            UUID uuid = new UUID((long) array[0] << 32 | (long) array[1] & 4294967295L, (long) array[2] << 32 | (long) array[3] & 4294967295L);
                                            skinURL = SkinUtils.getSkinURLFromUUID(uuid);
                                        }
                                    }
                                }
                            }
                            if (skinURL == null) {
                                StringTag nameTag = (StringTag) ((CompoundTag) skullOwnerTag).get("Name");
                                skinURL = SkinUtils.getSkinURLFromUUID(Bukkit.getOfflinePlayer(nameTag.getValue()).getUniqueId());
                            }
                        }
                        if (skinURL != null) {
                            skinImage = ImageUtils.downloadImage(skinURL);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            providedTextures.put(ResourceRegistry.SKIN_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(manager, ModelUtils.convertToModernSkinTexture(skinImage)));
        } else if (icMaterial.isMaterial(XMaterial.ELYTRA)) {
            int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
            if (durability <= 1) {
                predicates.put(ModelOverrideType.BROKEN, 1F);
            }
        } else if (icMaterial.isMaterial(XMaterial.CROSSBOW)) {
            CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
            List<ItemStack> charged = meta.getChargedProjectiles();
            if (charged != null && !charged.isEmpty()) {
                predicates.put(ModelOverrideType.CHARGED, 1F);
                ItemStack charge = charged.get(0);
                ICMaterial chargeType = ICMaterial.from(charge);
                if (chargeType.isMaterial(XMaterial.FIREWORK_ROCKET)) {
                    predicates.put(ModelOverrideType.FIREWORK, 1F);
                }
            }
        } else if (icMaterial.isMaterial(XMaterial.CLOCK)) {
            long time;
            ICPlayer onlinePlayer = player.getPlayer();
            if (onlinePlayer != null && onlinePlayer.isLocal()) {
                Player bukkitPlayer = onlinePlayer.getLocalPlayer();
                if (WorldUtils.isNatural(bukkitPlayer.getWorld())) {
                    time = (onlinePlayer.getLocalPlayer().getPlayerTime() % 24000) - 6000;
                } else {
                    time = RANDOM.nextInt(24000) - 6000;
                }
            } else {
                time = (Bukkit.getWorlds().get(0).getTime() % 24000) - 6000;
            }
            if (time < 0) {
                time += 24000;
            }
            double timePhase = (double) time / 24000;
            predicates.put(ModelOverrideType.TIME, (float) (timePhase - 0.0078125));
        } else if (icMaterial.isMaterial(XMaterial.COMPASS)) {
            ItemStack compass = item;

            if (CompassUtils.isLodestoneCompass(compass)) {
                requiresEnchantmentGlint = true;
                if (InteractiveChat.hideLodestoneCompassPos) {
                    compass = CompassUtils.hideLodestoneCompassPosition(compass);
                }
            }

            double angle;
            ICPlayer icplayer = player.getPlayer();
            if (icplayer != null && icplayer.isLocal()) {
                Player bukkitPlayer = icplayer.getLocalPlayer();
                if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16)) {
                    CompassMeta meta = (CompassMeta) compass.getItemMeta();
                    Location target;
                    if (meta.hasLodestone()) {
                        Location lodestone = meta.getLodestone();
                        target = new Location(lodestone.getWorld(), lodestone.getBlockX() + 0.5, lodestone.getBlockY(), lodestone.getBlockZ() + 0.5, lodestone.getYaw(), lodestone.getPitch());
                    } else if (WorldUtils.isNatural(bukkitPlayer.getWorld())) {
                        Location spawn = bukkitPlayer.getCompassTarget();
                        target = new Location(spawn.getWorld(), spawn.getBlockX() + 0.5, spawn.getBlockY(), spawn.getBlockZ() + 0.5, spawn.getYaw(), spawn.getPitch());
                    } else {
                        target = null;
                    }
                    if (target != null && target.getWorld().equals(bukkitPlayer.getWorld())) {
                        Location playerLocation = bukkitPlayer.getEyeLocation();
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
                    if (WorldUtils.isNatural(bukkitPlayer.getWorld())) {
                        Location spawn = bukkitPlayer.getCompassTarget();
                        Location target = new Location(spawn.getWorld(), spawn.getBlockX() + 0.5, spawn.getBlockY(), spawn.getBlockZ() + 0.5, spawn.getYaw(), spawn.getPitch());
                        Location playerLocation = bukkitPlayer.getEyeLocation();
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
                angle = 0;
            }

            predicates.put(ModelOverrideType.ANGLE, (float) (angle - 0.015625));
        } else if (icMaterial.isMaterial(XMaterial.RECOVERY_COMPASS)) {
            double angle;
            ICPlayer icplayer = player.getPlayer();
            if (icplayer != null && icplayer.isLocal()) {
                Player bukkitPlayer = icplayer.getLocalPlayer();
                Location target = bukkitPlayer.getLastDeathLocation();
                if (target != null && target.getWorld().equals(bukkitPlayer.getWorld())) {
                    Location playerLocation = bukkitPlayer.getEyeLocation();
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
                angle = 0;
            }

            predicates.put(ModelOverrideType.ANGLE, (float) (angle - 0.015625));
        } else if (icMaterial.isMaterial(XMaterial.LIGHT)) {
            float level = 1F;
            CompoundTag itemTag = (CompoundTag) NBTParsingUtils.fromSNBT(ItemNBTUtils.getNMSItemStackJson(item));
            if (itemTag != null && itemTag.containsKey("tag")) {
                CompoundTag itemTagTag = itemTag.getCompoundTag("tag");
                if (itemTagTag.containsKey("BlockStateTag")) {
                    CompoundTag blockStateTag = itemTagTag.getCompoundTag("BlockStateTag");
                    if (blockStateTag.containsKey("level")) {
                        try {
                            level = (float) Integer.parseInt(tagToString(blockStateTag.get("level"))) / 16F;
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            }
            predicates.put(ModelOverrideType.LEVEL, level);
        } else if (item.getItemMeta() instanceof LeatherArmorMeta) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            Color color = new Color(meta.getColor().asRGB());
            if (icMaterial.isMaterial(XMaterial.LEATHER_HORSE_ARMOR)) {
                BufferedImage itemImage = manager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + icMaterial.name().toLowerCase()).getTexture(32, 32);
                BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(itemImage), color);
                itemImage = ImageUtils.multiply(itemImage, colorOverlay);
                providedTextures.put(ResourceRegistry.LEATHER_HORSE_ARMOR_PLACEHOLDER, new GeneratedTextureResource(manager, itemImage));
            } else {
                BufferedImage itemImage = manager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + icMaterial.name().toLowerCase()).getTexture(32, 32);
                BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(itemImage), color);
                itemImage = ImageUtils.multiply(itemImage, colorOverlay);
                if (icMaterial.name().contains("HELMET")) {
                    providedTextures.put(ResourceRegistry.LEATHER_HELMET_PLACEHOLDER, new GeneratedTextureResource(manager, itemImage));
                } else if (icMaterial.name().contains("CHESTPLATE")) {
                    providedTextures.put(ResourceRegistry.LEATHER_CHESTPLATE_PLACEHOLDER, new GeneratedTextureResource(manager, itemImage));
                } else if (icMaterial.name().contains("LEGGINGS")) {
                    providedTextures.put(ResourceRegistry.LEATHER_LEGGINGS_PLACEHOLDER, new GeneratedTextureResource(manager, itemImage));
                } else if (icMaterial.name().contains("BOOTS")) {
                    providedTextures.put(ResourceRegistry.LEATHER_BOOTS_PLACEHOLDER, new GeneratedTextureResource(manager, itemImage));
                }
            }
        } else if (item.getItemMeta() instanceof PotionMeta) {
            if (icMaterial.isMaterial(XMaterial.TIPPED_ARROW)) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                PotionType potiontype = InteractiveChat.version.isOld() ? Potion.fromItemStack(item).getType() : meta.getBasePotionData().getType();
                BufferedImage tippedArrowHead = manager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "tipped_arrow_head").getTexture(32, 32);

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

                providedTextures.put(ResourceRegistry.TIPPED_ARROW_HEAD_PLACEHOLDER, new GeneratedTextureResource(manager, tippedArrowHead));
            } else {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                PotionType potiontype = InteractiveChat.version.isOld() ? Potion.fromItemStack(item).getType() : meta.getBasePotionData().getType();
                BufferedImage potionOverlay = manager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "potion_overlay").getTexture(32, 32);

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

                providedTextures.put(ResourceRegistry.POTION_OVERLAY_PLACEHOLDER, new GeneratedTextureResource(manager, potionOverlay));
                if (potiontype != null && InteractiveChat.version.isOlderThan(MCVersion.V1_19_4)) {
                    if (!(potiontype.name().equals("WATER") || potiontype.name().equals("AWKWARD") || potiontype.name().equals("MUNDANE") || potiontype.name().equals("THICK") || potiontype.name().equals("UNCRAFTABLE"))) {
                        requiresEnchantmentGlint = true;
                    }
                }
            }
        } else if (icMaterial.isOneOf(Collections.singletonList("CONTAINS:spawn_egg"))) {
            SpawnEggTintData tintData = TintUtils.getSpawnEggTint(icMaterial);
            if (tintData != null) {
                BufferedImage baseImage = manager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "spawn_egg").getTexture();
                BufferedImage overlayImage = manager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "spawn_egg_overlay").getTexture(baseImage.getWidth(), baseImage.getHeight());

                BufferedImage colorBase = ImageUtils.changeColorTo(ImageUtils.copyImage(baseImage), tintData.getBase());
                BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(overlayImage), tintData.getOverlay());

                baseImage = ImageUtils.multiply(baseImage, colorBase);
                overlayImage = ImageUtils.multiply(overlayImage, colorOverlay);

                providedTextures.put(ResourceRegistry.SPAWN_EGG_PLACEHOLDER, new GeneratedTextureResource(manager, baseImage));
                providedTextures.put(ResourceRegistry.SPAWN_EGG_OVERLAY_PLACEHOLDER, new GeneratedTextureResource(manager, overlayImage));
            }
        } else if (icMaterial.isMaterial(XMaterial.FIREWORK_STAR)) {
            int overlayColor;

            int[] is;
            int[] nArray = is = NBTEditor.contains(item, "Explosion", "Colors") ? NBTEditor.getIntArray(item, "Explosion", "Colors") : null;
            if (is == null || is.length == 0) {
                overlayColor = 0x8A8A8A;
            } else if (is.length == 1) {
                overlayColor = is[0];
            } else {
                int i = 0;
                int j = 0;
                int k = 0;
                for (int l : is) {
                    i += (l & 0xFF0000) >> 16;
                    j += (l & 0xFF00) >> 8;
                    k += (l & 0xFF);
                }
                overlayColor = (i /= is.length) << 16 | (j /= is.length) << 8 | (k /= is.length);
            }

            BufferedImage fireworkStarOverlay = manager.getTextureManager().getTexture(ResourceRegistry.FIREWORK_STAR_OVERLAY_LOCATION).getTexture();
            BufferedImage tint = ImageUtils.changeColorTo(ImageUtils.copyImage(fireworkStarOverlay), overlayColor);
            fireworkStarOverlay = ImageUtils.multiply(fireworkStarOverlay, tint);

            providedTextures.put(ResourceRegistry.FIREWORK_STAR_OVERLAY_LOCATION, new GeneratedTextureResource(manager, fireworkStarOverlay));
        } else if (InteractiveChat.version.isLegacy() && icMaterial.isOneOf(Collections.singletonList("CONTAINS:bed"))) {
            String colorName = icMaterial.name().replace("_BED", "").toLowerCase();
            BufferedImage bedTexture = manager.getTextureManager().getTexture(ResourceRegistry.ENTITY_TEXTURE_LOCATION + "bed/" + colorName).getTexture();
            providedTextures.put(ResourceRegistry.LEGACY_BED_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(manager, bedTexture));
        } else if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_9) && icMaterial.isMaterial(XMaterial.ENDER_PEARL)) {
            ICPlayer icplayer = player.getPlayer();
            if (icplayer != null && icplayer.isLocal()) {
                int cooldown = icplayer.getLocalPlayer().getCooldown(item.getType());
                predicates.put(ModelOverrideType.COOLDOWN, (float) cooldown / (float) ResourceRegistry.ENDER_PEARL_COOLDOWN);
            }
        } else if (icMaterial.isMaterial(XMaterial.CHORUS_FRUIT)) {
            ICPlayer icplayer = player.getPlayer();
            if (icplayer != null && icplayer.isLocal()) {
                int cooldown = icplayer.getLocalPlayer().getCooldown(item.getType());
                predicates.put(ModelOverrideType.COOLDOWN, (float) cooldown / (float) ResourceRegistry.CHORUS_FRUIT_COOLDOWN);
            }
        } else if (icMaterial.isMaterial(XMaterial.FISHING_ROD)) {
            predicates.put(ModelOverrideType.CAST, 0F);
            ICPlayer icplayer = player.getPlayer();
            if (icplayer != null && icplayer.isLocal()) {
                Player bukkitPlayer = icplayer.getLocalPlayer();
                if (FishUtils.getPlayerFishingHook(bukkitPlayer) != null) {
                    ItemStack mainHandItem = bukkitPlayer.getEquipment().getItemInHand();
                    if (InteractiveChat.version.isOld()) {
                        if (mainHandItem != null && mainHandItem.equals(item)) {
                            predicates.put(ModelOverrideType.CAST, 1F);
                        }
                    } else {
                        ItemStack offHandItem = bukkitPlayer.getEquipment().getItemInOffHand();
                        if ((mainHandItem != null && mainHandItem.equals(item)) || ((offHandItem != null && offHandItem.equals(item)) && (mainHandItem == null || !XMaterial.matchXMaterial(mainHandItem).equals(XMaterial.FISHING_ROD)))) {
                            predicates.put(ModelOverrideType.CAST, 1F);
                        }
                    }
                }
            }
        } else if (icMaterial.isMaterial(XMaterial.BUNDLE)) {
            float fullness = BundleUtils.getFullnessPercentage(((BundleMeta) item.getItemMeta()).getItems());
            predicates.put(ModelOverrideType.FILLED, fullness);
        } else if (FilledMapUtils.isFilledMap(item)) {
            int markingColor;
            if (NBTEditor.contains(item, "display", "MapColor")) {
                markingColor = -16777216 | NBTEditor.getInt(item, "display", "MapColor") & 16777215;
            } else {
                markingColor = -12173266;
            }
            BufferedImage filledMapMarkings = manager.getTextureManager().getTexture(ResourceRegistry.MAP_MARKINGS_LOCATION).getTexture();
            BufferedImage tint = ImageUtils.changeColorTo(ImageUtils.copyImage(filledMapMarkings), markingColor);
            filledMapMarkings = ImageUtils.multiply(filledMapMarkings, tint);

            providedTextures.put(ResourceRegistry.MAP_MARKINGS_LOCATION, new GeneratedTextureResource(manager, filledMapMarkings));
        } else if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_19_4) && ItemStackUtils.isArmor(item)) {
            float trimIndex = ArmorTrimUtils.getArmorTrimIndex(world, item).leftFloat();
            predicates.put(ModelOverrideType.TRIM_TYPE, trimIndex);
        } else if (icMaterial.isMaterial(XMaterial.DECORATED_POT)) {
            for (int i = 0; i < 4; i++) {
                TextureResource textureResource = null;
                String shard = NBTEditor.getString(item, "BlockEntityTag", "shards", i);
                if (shard != null) {
                    if (shard.contains(":")) {
                        shard = shard.substring(shard.indexOf(":") + 1);
                    }
                    if (shard.startsWith("pottery_shard_")) {
                        String type = shard.substring("pottery_shard_".length());
                        textureResource = manager.getTextureManager().getTexture(ResourceRegistry.DECORATED_POT_SHARD_LOCATION.replace("%s", type));
                    }
                }
                if (textureResource == null) {
                    textureResource = manager.getTextureManager().getTexture(ResourceRegistry.DECORATED_POT_SIDE_LOCATION);
                }
                providedTextures.put(ResourceRegistry.DECORATED_POT_FACE_PLACEHOLDER.replace("%s", String.valueOf(i)), textureResource);
            }
        }

        String modelKey = directLocation == null ? ResourceRegistry.ITEM_MODEL_LOCATION + ModelUtils.getItemModelKey(icMaterial) : directLocation;

        Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> postResolveFunction = manager.getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getItemPostResolveFunction(modelKey, slot, item, is1_8, predicates, player, world, livingEntity, manager.getLanguageManager().getTranslateFunction().ofLanguage(language)).map(function -> {
            return function.andThen(result -> {
                Map<String, TextureResource> overrideTextures = result.getSecond();
                for (Entry<String, TextureResource> entry : overrideTextures.entrySet()) {
                    String overriddenResource = result.getFirst().getTextures().get(entry.getKey());
                    if (overriddenResource == null) {
                        continue;
                    }
                    if (!overriddenResource.contains(":")) {
                        String namespace = result.getFirst().getResourceLocation();
                        if (namespace.contains(":")) {
                            namespace = namespace.substring(0, namespace.indexOf(":"));
                        } else {
                            namespace = ResourceRegistry.DEFAULT_NAMESPACE;
                        }
                        overriddenResource = namespace + ":" + overriddenResource;
                    }
                    if (item.getItemMeta() instanceof PotionMeta) {
                        if (icMaterial.isMaterial(XMaterial.TIPPED_ARROW)) {
                            if (overriddenResource.equalsIgnoreCase(ResourceRegistry.TIPPED_ARROW_HEAD_PLACEHOLDER)) {
                                PotionMeta meta = (PotionMeta) item.getItemMeta();
                                PotionType potiontype = InteractiveChat.version.isOld() ? Potion.fromItemStack(item).getType() : meta.getBasePotionData().getType();
                                BufferedImage tippedArrowHead = manager.getTextureManager().getTexture(ResourceRegistry.ITEM_TEXTURE_LOCATION + "tipped_arrow_head").getTexture(32, 32);

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

                                entry.setValue(new GeneratedTextureResource(manager, tippedArrowHead));
                            }
                        } else {
                            if (overriddenResource.equalsIgnoreCase(ResourceRegistry.POTION_OVERLAY_PLACEHOLDER)) {
                                PotionMeta meta = (PotionMeta) item.getItemMeta();
                                PotionType potiontype = InteractiveChat.version.isOld() ? Potion.fromItemStack(item).getType() : meta.getBasePotionData().getType();
                                BufferedImage potionOverlay = entry.getValue().getTexture(32, 32);

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

                                entry.setValue(new GeneratedTextureResource(manager, potionOverlay));
                            }
                        }
                    } else if (icMaterial.isOneOf(Collections.singletonList("CONTAINS:spawn_egg"))) {
                        if (overriddenResource.equalsIgnoreCase(ResourceRegistry.SPAWN_EGG_PLACEHOLDER)) {
                            SpawnEggTintData tintData = TintUtils.getSpawnEggTint(icMaterial);
                            if (tintData != null) {
                                BufferedImage baseImage = entry.getValue().getTexture();
                                BufferedImage colorBase = ImageUtils.changeColorTo(ImageUtils.copyImage(baseImage), tintData.getBase());
                                baseImage = ImageUtils.multiply(baseImage, colorBase);
                                entry.setValue(new GeneratedTextureResource(manager, baseImage));
                            }
                        } else if (overriddenResource.equalsIgnoreCase(ResourceRegistry.SPAWN_EGG_OVERLAY_PLACEHOLDER)) {
                            SpawnEggTintData tintData = TintUtils.getSpawnEggTint(icMaterial);
                            if (tintData != null) {
                                BufferedImage overlayImage = entry.getValue().getTexture();
                                BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(overlayImage), tintData.getOverlay());
                                overlayImage = ImageUtils.multiply(overlayImage, colorOverlay);
                                entry.setValue(new GeneratedTextureResource(manager, overlayImage));
                            }
                        }
                    } else if (item.getItemMeta() instanceof LeatherArmorMeta) {
                        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
                        Color color = new Color(meta.getColor().asRGB());
                        if (icMaterial.isMaterial(XMaterial.LEATHER_HORSE_ARMOR)) {
                            if (overriddenResource.equalsIgnoreCase(ResourceRegistry.LEATHER_HORSE_ARMOR_PLACEHOLDER)) {
                                BufferedImage itemImage = entry.getValue().getTexture(32, 32);
                                BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(itemImage), color);
                                itemImage = ImageUtils.multiply(itemImage, colorOverlay);
                                entry.setValue(new GeneratedTextureResource(manager, itemImage));
                            }
                        } else {
                            if (overriddenResource.equalsIgnoreCase(ResourceRegistry.LEATHER_HELMET_PLACEHOLDER) || overriddenResource.equalsIgnoreCase(ResourceRegistry.LEATHER_CHESTPLATE_PLACEHOLDER) || overriddenResource.equalsIgnoreCase(ResourceRegistry.LEATHER_LEGGINGS_PLACEHOLDER) || overriddenResource.equalsIgnoreCase(ResourceRegistry.LEATHER_BOOTS_PLACEHOLDER)) {
                                BufferedImage itemImage = entry.getValue().getTexture(32, 32);
                                BufferedImage colorOverlay = ImageUtils.changeColorTo(ImageUtils.copyImage(itemImage), color);
                                itemImage = ImageUtils.multiply(itemImage, colorOverlay);
                                entry.setValue(new GeneratedTextureResource(manager, itemImage));
                            }
                        }
                    }
                }

                return result;
            });
        }).orElse(null);

        return new ItemStackProcessResult(requiresEnchantmentGlint, predicates, providedTextures, tintIndexData, modelKey, postResolveFunction, enchantmentGlintFunction, rawEnchantmentGlintFunction);
    }

    private static String tagToString(Tag<?> tag) {
        if (tag instanceof StringTag) {
            return ((StringTag) tag).getValue();
        } else {
            return tag.valueToString();
        }
    }

    public static class ItemStackProcessResult {

        private final boolean requiresEnchantmentGlint;
        private final Map<ModelOverrideType, Float> predicates;
        private final Map<String, TextureResource> providedTextures;
        private final TintIndexData tintIndexData;
        private final String modelKey;
        private final Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> postResolveFunction;
        private final BiFunction<BufferedImage, EnchantmentGlintType, BufferedImage> enchantmentGlintFunction;
        private final BiFunction<BufferedImage, EnchantmentGlintType, RawEnchantmentGlintData> rawEnchantmentGlintFunction;

        public ItemStackProcessResult(boolean requiresEnchantmentGlint, Map<ModelOverrideType, Float> predicates, Map<String, TextureResource> providedTextures, TintIndexData tintIndexData, String modelKey, Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> postResolveFunction, BiFunction<BufferedImage, EnchantmentGlintType, BufferedImage> enchantmentGlintFunction, BiFunction<BufferedImage, EnchantmentGlintType, RawEnchantmentGlintData> rawEnchantmentGlintFunction) {
            this.requiresEnchantmentGlint = requiresEnchantmentGlint;
            this.predicates = predicates;
            this.providedTextures = providedTextures;
            this.tintIndexData = tintIndexData;
            this.modelKey = modelKey;
            this.postResolveFunction = postResolveFunction;
            this.enchantmentGlintFunction = enchantmentGlintFunction;
            this.rawEnchantmentGlintFunction = rawEnchantmentGlintFunction;
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

        public String getModelKey() {
            return modelKey;
        }

        public Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> getPostResolveFunction() {
            return postResolveFunction;
        }

        public BiFunction<BufferedImage, EnchantmentGlintType, BufferedImage> getEnchantmentGlintFunction() {
            return enchantmentGlintFunction;
        }

        public BiFunction<BufferedImage, EnchantmentGlintType, RawEnchantmentGlintData> getRawEnchantmentGlintFunction() {
            return rawEnchantmentGlintFunction;
        }

    }

}
