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
import com.loohp.interactivechatdiscordsrvaddon.utils.TintUtils.TintColorProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.DecoratedPot;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

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
        } else if (!item.getEnchantments().isEmpty()) {
            requiresEnchantmentGlint = true;
        }

        List<ValuePairs<TextureResource, OpenGLBlending>> enchantmentGlintResource = manager.getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getEnchantmentGlintOverrideTextures(null, item, () -> ImageGeneration.getDefaultEnchantmentTint(EnchantmentGlintType.ITEM), manager.getLanguageManager().getTranslateFunction().ofLanguage(language));
        BiFunction<BufferedImage, EnchantmentGlintType, BufferedImage> enchantmentGlintFunction = (image, type) -> ImageGeneration.getEnchantedImage(enchantmentGlintResource, image);
        BiFunction<BufferedImage, EnchantmentGlintType, RawEnchantmentGlintData> rawEnchantmentGlintFunction = (image, type) -> new RawEnchantmentGlintData(enchantmentGlintResource.stream().map(each -> ImageGeneration.getRawEnchantedImage(each.getFirst(), image)).collect(Collectors.toList()), enchantmentGlintResource.stream().map(each -> each.getSecond()).collect(Collectors.toList()));

        TintColorProvider tintColorProvider = TintUtils.getTintProvider(icMaterial);
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
            tintColorProvider = new TintUtils.DyeTintProvider(tintIndex -> tintIndex > 0 ? -1 : meta.getColor().asRGB());
        } else if (item.getItemMeta() instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            PotionType potiontype = InteractiveChat.version.isOld() ? Potion.fromItemStack(item).getType() : meta.getBasePotionData().getType();
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
            int finalColor = color;
            tintColorProvider = new TintUtils.DyeTintProvider(tintIndex -> tintIndex == 0 ? finalColor : -1);

            if (!icMaterial.isMaterial(XMaterial.TIPPED_ARROW) && potiontype != null && InteractiveChat.version.isOlderThan(MCVersion.V1_19_4)) {
                if (!(potiontype.name().equals("WATER") || potiontype.name().equals("AWKWARD") || potiontype.name().equals("MUNDANE") || potiontype.name().equals("THICK") || potiontype.name().equals("UNCRAFTABLE"))) {
                    requiresEnchantmentGlint = true;
                }
            }
        } else if (icMaterial.isOneOf(Collections.singletonList("CONTAINS:spawn_egg"))) {
            SpawnEggTintData tintData = TintUtils.getSpawnEggTint(icMaterial);
            if (tintData != null) {
                tintColorProvider = tintData;
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

            tintColorProvider = new TintUtils.DyeTintProvider(tintIndex -> tintIndex != 1 ? -1 : overlayColor);
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
        } else if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_20) && item.getItemMeta() instanceof ArmorMeta) {
            ArmorMeta armorMeta = (ArmorMeta) item.getItemMeta();
            ArmorTrim armorTrim = armorMeta.getTrim();
            TrimMaterial trimMaterial = armorTrim == null ? null : armorTrim.getMaterial();
            float trimIndex = ArmorTrimUtils.getTrimMaterialItemModelData(trimMaterial).leftFloat();
            predicates.put(ModelOverrideType.TRIM_TYPE, trimIndex);
        } else if (icMaterial.isMaterial(XMaterial.DECORATED_POT) && item.getItemMeta() instanceof BlockStateMeta) {
            BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
            BlockState state = meta.getBlockState();
            if (state instanceof DecoratedPot) {
                DecoratedPot pot = (DecoratedPot) state;
                List<Material> materials = pot.getShards();
                for (int i = 0; i < materials.size(); i++) {
                    TextureResource textureResource = null;
                    ItemStack sherd = new ItemStack(materials.get(i));
                    String type = DecoratedPotPatternsUtils.getPatternName(sherd);
                    if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_20_3)) {
                        String namespace;
                        String key;
                        if (type.contains(":")) {
                            int sep = type.indexOf(":");
                            namespace = type.substring(0, sep);
                            key = type.substring(sep + 1);
                        } else {
                            namespace = ResourceRegistry.DEFAULT_NAMESPACE;
                            key = type;
                        }
                        textureResource = manager.getTextureManager().getTexture(ResourceRegistry.DECORATED_POT_SHERD_LOCATION.replaceFirst("%s", namespace).replaceFirst("%s", key));
                        providedTextures.put(ResourceRegistry.DECORATED_POT_FACE_PLACEHOLDER.replace("%s", String.valueOf(i)), textureResource);
                    } else {
                        if (type.contains(":")) {
                            type = type.substring(type.indexOf(":") + 1);
                        }
                        textureResource = manager.getTextureManager().getTexture(ResourceRegistry.DECORATED_POT_SHERD_MINECRAFT_LOCATION.replace("%s", type));
                        providedTextures.put(ResourceRegistry.DECORATED_POT_FACE_PLACEHOLDER.replace("%s", String.valueOf(i)), textureResource);
                    }
                }
            }
        }

        String modelKey;
        if (directLocation == null) {
            modelKey = ModelUtils.getNamespace(icMaterial) + ":" + ResourceRegistry.ITEM_MODEL_LOCATION + ModelUtils.getItemModelKey(icMaterial);
        } else {
            modelKey = directLocation;
        }

        Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> postResolveFunction = manager.getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getItemPostResolveFunction(modelKey, slot, item, is1_8, predicates, player, world, livingEntity, manager.getLanguageManager().getTranslateFunction().ofLanguage(language)).orElse(null);

        return new ItemStackProcessResult(requiresEnchantmentGlint, predicates, providedTextures, tintColorProvider, modelKey, postResolveFunction, enchantmentGlintFunction, rawEnchantmentGlintFunction);
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
        private final TintColorProvider tintColorProvider;
        private final String modelKey;
        private final Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> postResolveFunction;
        private final BiFunction<BufferedImage, EnchantmentGlintType, BufferedImage> enchantmentGlintFunction;
        private final BiFunction<BufferedImage, EnchantmentGlintType, RawEnchantmentGlintData> rawEnchantmentGlintFunction;

        public ItemStackProcessResult(boolean requiresEnchantmentGlint, Map<ModelOverrideType, Float> predicates, Map<String, TextureResource> providedTextures, TintColorProvider tintColorProvider, String modelKey, Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> postResolveFunction, BiFunction<BufferedImage, EnchantmentGlintType, BufferedImage> enchantmentGlintFunction, BiFunction<BufferedImage, EnchantmentGlintType, RawEnchantmentGlintData> rawEnchantmentGlintFunction) {
            this.requiresEnchantmentGlint = requiresEnchantmentGlint;
            this.predicates = predicates;
            this.providedTextures = providedTextures;
            this.tintColorProvider = tintColorProvider;
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

        public TintColorProvider getTintColorProvider() {
            return tintColorProvider;
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
