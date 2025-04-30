/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
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

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.CompoundTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.StringTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.Tag;
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ColorUtils;
import com.loohp.interactivechat.utils.CompassUtils;
import com.loohp.interactivechat.utils.FilledMapUtils;
import com.loohp.interactivechat.utils.ItemNBTUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NBTParsingUtils;
import com.loohp.interactivechatdiscordsrvaddon.graphics.BannerGraphics;
import com.loohp.interactivechatdiscordsrvaddon.graphics.BannerGraphics.BannerAssetResult;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.nms.NMSAddon;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ChargeType;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.CustomModelData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ItemDamageInfo;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.TintColorProvider;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.CustomItemTextureRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelLayer;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer.RawEnchantmentGlintData;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.definitions.item.ItemModelDefinition;
import com.loohp.interactivechatdiscordsrvaddon.resources.definitions.item.ItemModelDefinitionManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.BlockModel;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelOverride.ModelOverrideType;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit.EnchantmentProperties.OpenGLBlending;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.EnchantmentGlintType;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.GeneratedTextureResource;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;
import com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.DecoratedPot;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.potion.PotionType;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public class ItemRenderUtils {

    private static final Random RANDOM = new Random();

    public static ItemStackProcessResult processItemForRendering(ResourceManager manager, OfflineICPlayer player, ItemStack item, EquipmentSlot slot, ModelDisplay.ModelDisplayPosition displayPosition, boolean is1_8, String language) throws IOException {
        World world;
        LivingEntity livingEntity;
        if (player.isOnline() && player.getPlayer().isLocal()) {
            livingEntity = player.getPlayer().getLocalPlayer();
            world = livingEntity.getWorld();
        } else {
            livingEntity = null;
            world = null;
        }

        String namespacedKey = NMSAddon.getInstance().getItemModelResourceLocation(item).asString();

        ItemModelDefinition itemModelDefinition = manager.getItemModelDefinitionManager().getItemModelDefinition(namespacedKey);
        ItemModelDefinition.ItemModelDefinitionType<?> itemModelDefinitionType = itemModelDefinition.getType();

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
        } else if (icMaterial.isMaterial(XMaterial.NETHER_STAR)) {
            requiresEnchantmentGlint = true;
        } else if (!item.getEnchantments().isEmpty()) {
            requiresEnchantmentGlint = true;
        }

        List<ValuePairs<TextureResource, OpenGLBlending>> enchantmentGlintResource = manager.getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getEnchantmentGlintOverrideTextures(null, item, () -> ImageGeneration.getDefaultEnchantmentTint(EnchantmentGlintType.ITEM), manager.getLanguageManager().getTranslateFunction().ofLanguage(language));
        Function<ModelRenderer.RawEnchantmentGlintParameters, BufferedImage> enchantmentGlintFunction = parameters -> ImageGeneration.getEnchantedImage(enchantmentGlintResource, parameters.getImage(), parameters.getTick());
        Function<ModelRenderer.RawEnchantmentGlintParameters, RawEnchantmentGlintData> rawEnchantmentGlintFunction = parameters -> new RawEnchantmentGlintData(enchantmentGlintResource.stream().map(each -> ImageGeneration.getRawEnchantedImage(each.getFirst(), parameters.getImage(), parameters.getTick())).collect(Collectors.toList()), enchantmentGlintResource.stream().map(each -> each.getSecond()).collect(Collectors.toList()));

        ItemMeta itemMeta = item.getItemMeta();
        boolean hasItemMeta = itemMeta != null;

        if (hasItemMeta && itemMeta instanceof PotionMeta) {
            PotionMeta meta = (PotionMeta) itemMeta;
            PotionType potiontype = NMSAddon.getInstance().getBasePotionType(item);
            if (!icMaterial.isMaterial(XMaterial.TIPPED_ARROW) && potiontype != null && InteractiveChat.version.isOlderThan(MCVersion.V1_19_4)) {
                if (!(potiontype.name().equals("WATER") || potiontype.name().equals("AWKWARD") || potiontype.name().equals("MUNDANE") || potiontype.name().equals("THICK") || potiontype.name().equals("UNCRAFTABLE"))) {
                    requiresEnchantmentGlint = true;
                }
            }
        }
        if (CompassUtils.isLodestoneCompass(item)) {
            requiresEnchantmentGlint = true;
        }

        Boolean enchantmentGlintOverride = NMSAddon.getInstance().getEnchantmentGlintOverride(item);
        if (enchantmentGlintOverride != null) {
            requiresEnchantmentGlint = enchantmentGlintOverride;
        }

        if (itemModelDefinitionType.equals(ItemModelDefinition.ItemModelDefinitionType.IC_LEGACY)) {
            TintColorProvider tintColorProvider = TintUtils.getTintProvider(icMaterial);
            Map<ModelOverrideType, Float> predicates = new EnumMap<>(ModelOverrideType.class);
            Map<String, TextureResource> providedTextures = new HashMap<>();

            if (!player.isRightHanded()) {
                predicates.put(ModelOverrideType.LEFTHANDED, 1F);
            }
            CustomModelData customModelData = NMSAddon.getInstance().getCustomModelData(item);
            if (customModelData != null && customModelData.hasLegacyIndex()) {
                predicates.put(ModelOverrideType.CUSTOM_MODEL_DATA, customModelData.getLegacyIndex());
            }
            if (item.getType().getMaxDurability() > 0) {
                int maxDur = item.getType().getMaxDurability();
                int damage = InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) itemMeta).getDamage();
                predicates.put(ModelOverrideType.DAMAGE, (float) damage / (float) maxDur);
                predicates.put(ModelOverrideType.DAMAGED, NMSAddon.getInstance().isItemUnbreakable(item) || (damage <= 0) ? 0F : 1F);
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
                if (hasItemMeta) {
                    GameProfile gameProfile = NMSAddon.getInstance().getPlayerHeadProfile(item);
                    String skinURL = GameProfileUtils.getSkinUrl(gameProfile);
                    if (skinURL != null) {
                        try {
                            skinImage = ImageUtils.downloadImage(skinURL);
                        } catch (Exception ignored) {
                        }
                    }
                    if (skinImage == null && GameProfileUtils.hasValidUUID(gameProfile)) {
                        skinImage = manager.getTextureManager().getTexture(DefaultSkinUtils.getTexture(gameProfile.getId())).getTexture();
                    }
                }
                providedTextures.put(ResourceRegistry.SKIN_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(manager, ModelUtils.convertToModernSkinTexture(skinImage)));
            } else if (icMaterial.isMaterial(XMaterial.ELYTRA)) {
                int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) itemMeta).getDamage());
                if (durability <= 1) {
                    predicates.put(ModelOverrideType.BROKEN, 1F);
                }
            } else if (icMaterial.isMaterial(XMaterial.CROSSBOW)) {
                CrossbowMeta meta = (CrossbowMeta) itemMeta;
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
                if (itemTag != null && itemTag.containsKey(ResourceRegistry.ITEM_COMPONENT_TAG)) {
                    CompoundTag itemTagTag = itemTag.getCompoundTag(ResourceRegistry.ITEM_COMPONENT_TAG);
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
            } else if (icMaterial.isMaterial(XMaterial.WOLF_ARMOR)) {
                int dyedColor = NMSAddon.getInstance().getLeatherArmorColor(item).orElse(ResourceRegistry.DEFAULT_DYE_COLOR);
                tintColorProvider = new TintColorProvider.DyeTintProvider(tintIndex -> tintIndex > 0 ? dyedColor : -1);
            } else if (itemMeta instanceof LeatherArmorMeta) {
                int dyedColor = NMSAddon.getInstance().getLeatherArmorColor(item).orElse(ResourceRegistry.DEFAULT_DYE_COLOR);
                tintColorProvider = new TintColorProvider.DyeTintProvider(tintIndex -> tintIndex > 0 ? -1 : dyedColor);
            } else if (itemMeta instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) itemMeta;
                PotionType potiontype = NMSAddon.getInstance().getBasePotionType(item);
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
                tintColorProvider = new TintColorProvider.DyeTintProvider(tintIndex -> tintIndex == 0 ? finalColor : -1);
            } else if (icMaterial.isOneOf(Collections.singletonList("CONTAINS:spawn_egg"))) {
                TintColorProvider.SpawnEggTintData tintData = TintUtils.getSpawnEggTint(icMaterial);
                if (tintData != null) {
                    tintColorProvider = tintData;
                }
            } else if (icMaterial.isMaterial(XMaterial.FIREWORK_STAR)) {
                FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) itemMeta;
                int overlayColor;

                int[] is;
                int[] nArray = is = fireworkEffectMeta.hasEffect() ? fireworkEffectMeta.getEffect().getColors().stream().mapToInt(c -> c.asRGB()).toArray() : null;
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

                tintColorProvider = new TintColorProvider.DyeTintProvider(tintIndex -> tintIndex != 1 ? -1 : overlayColor);
            } else if (InteractiveChat.version.isLegacy() && icMaterial.isOneOf(Collections.singletonList("CONTAINS:bed"))) {
                String colorName = icMaterial.name().replace("_BED", "").toLowerCase();
                if (colorName.equalsIgnoreCase("light_gray")) {
                    colorName = "silver";
                }
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
                    if (NMSAddon.getInstance().getFishHook(bukkitPlayer) != null) {
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
                @SuppressWarnings("UnstableApiUsage")
                float fullness = BundleUtils.getWeight(((BundleMeta) itemMeta).getItems()).floatValue();
                predicates.put(ModelOverrideType.FILLED, fullness);
            } else if (FilledMapUtils.isFilledMap(item)) {
                MapMeta mapMeta = (MapMeta) itemMeta;
                int markingColor;
                if (hasItemMeta && mapMeta.hasColor()) {
                    markingColor = -16777216 | mapMeta.getColor().asRGB() & 16777215;
                } else {
                    markingColor = -12173266;
                }
                BufferedImage filledMapMarkings = manager.getTextureManager().getTexture(ResourceRegistry.MAP_MARKINGS_LOCATION).getTexture();
                BufferedImage tint = ImageUtils.changeColorTo(ImageUtils.copyImage(filledMapMarkings), markingColor);
                filledMapMarkings = ImageUtils.multiply(filledMapMarkings, tint);

                providedTextures.put(ResourceRegistry.MAP_MARKINGS_LOCATION, new GeneratedTextureResource(manager, filledMapMarkings));
            } else if (icMaterial.isMaterial(XMaterial.DECORATED_POT) && itemMeta instanceof BlockStateMeta) {
                BlockStateMeta meta = (BlockStateMeta) itemMeta;
                BlockState state = meta.getBlockState();
                if (state instanceof DecoratedPot) {
                    DecoratedPot pot = (DecoratedPot) state;
                    List<Material> materials = pot.getShards();
                    for (int i = 0; i < materials.size(); i++) {
                        TextureResource textureResource = null;
                        ItemStack sherd = new ItemStack(materials.get(i));
                        Key type = NMSAddon.getInstance().getDecoratedPotSherdPatternName(sherd);
                        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_20_3)) {
                            String namespace = type.namespace();
                            String key = type.value();
                            String pattern = key.equals("blank") ? "decorated_pot_side" : key + "_pottery_pattern";
                            textureResource = manager.getTextureManager().getTexture(ResourceRegistry.DECORATED_POT_SHERD_LOCATION.replaceFirst("%s", namespace).replaceFirst("%s", pattern));
                            providedTextures.put(ResourceRegistry.DECORATED_POT_FACE_PLACEHOLDER.replace("%s", String.valueOf(i)), textureResource);
                        } else {
                            String key = type.value();
                            textureResource = manager.getTextureManager().getTexture(ResourceRegistry.DECORATED_POT_SHERD_MINECRAFT_LOCATION.replace("%s", key));
                            providedTextures.put(ResourceRegistry.DECORATED_POT_FACE_PLACEHOLDER.replace("%s", String.valueOf(i)), textureResource);
                        }
                    }
                }
            }
            if (InteractiveChat.version.isBetweenInclusively(MCVersion.V1_20, MCVersion.V1_21_3) && itemMeta instanceof ArmorMeta) {
                ArmorMeta armorMeta = (ArmorMeta) itemMeta;
                ArmorTrim armorTrim = armorMeta.getTrim();
                TrimMaterial trimMaterial = armorTrim == null ? null : armorTrim.getMaterial();
                float trimIndex = NMSAddon.getInstance().getLegacyTrimMaterialIndex(trimMaterial);
                predicates.put(ModelOverrideType.TRIM_TYPE, trimIndex);
            }

            String modelKey;
            if (directLocation == null) {
                modelKey = ModelUtils.getNamespace(icMaterial) + ":" + ResourceRegistry.ITEM_MODEL_LOCATION + ModelUtils.getItemModelKey(icMaterial);
            } else {
                modelKey = directLocation;
            }

            Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> postResolveFunction = manager.getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getItemPostResolveFunction(modelKey, slot, item, is1_8, predicates, player, world, livingEntity, manager.getLanguageManager().getTranslateFunction().ofLanguage(language)).orElse(null);

            return new ItemStackProcessResult(requiresEnchantmentGlint, Collections.singletonList(new ModelLayer(modelKey, predicates, providedTextures, tintColorProvider, postResolveFunction)), enchantmentGlintFunction, rawEnchantmentGlintFunction);
        } else {
            List<ModelLayer> modelLayers = resolveItemModelDefinition(manager, player, displayPosition, item, itemModelDefinition, (modelKey, predicates) -> {
                return manager.getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getItemPostResolveFunction(modelKey, slot, item, is1_8, predicates, player, world, livingEntity, manager.getLanguageManager().getTranslateFunction().ofLanguage(language)).orElse(null);
            });

            return new ItemStackProcessResult(requiresEnchantmentGlint, modelLayers, enchantmentGlintFunction, rawEnchantmentGlintFunction);
        }
    }

    public static List<ModelLayer> resolveItemModelDefinition(ResourceManager manager, OfflineICPlayer player, ModelDisplay.ModelDisplayPosition displayPosition, ItemStack itemStack, ItemModelDefinition itemModelDefinition, PostResolveFunctionGenerator postResolveFunctionGenerator) {
        ItemModelDefinition.ItemModelDefinitionType<?> itemModelDefinitionType = itemModelDefinition.getType();
        if (itemModelDefinitionType.equals(ItemModelDefinition.ItemModelDefinitionType.MODEL)) {
            ItemModelDefinition.ItemModelDefinitionModel model = (ItemModelDefinition.ItemModelDefinitionModel) itemModelDefinition;
            TintColorProvider tintColorProvider = TintColorProvider.EMPTY_INSTANCE;
            Map<String, TextureResource> providedTextures = new HashMap<>();
            String modelKey = model.getModel();
            if (!model.getTints().isEmpty()) {
                IntSupplier[] tintData = new IntSupplier[model.getTints().size()];
                for (int tintindex = 0; tintindex < model.getTints().size(); tintindex++) {
                    ItemModelDefinition.TintSource tintSource = model.getTints().get(tintindex);
                    ItemModelDefinition.TintSourceType<?> tintSourceType = tintSource.getType();
                    if (tintSourceType.equals(ItemModelDefinition.TintSourceType.CONSTANT)) {
                        ItemModelDefinition.ConstantTintSource constantTintSource = (ItemModelDefinition.ConstantTintSource) tintSource;
                        int value = constantTintSource.getValue();
                        tintData[tintindex] = () -> value;
                    } else if (tintSourceType.equals(ItemModelDefinition.TintSourceType.DYE)) {
                        ItemModelDefinition.DyeTintSource dyeTintSource = (ItemModelDefinition.DyeTintSource) tintSource;
                        int dyedColor = NMSAddon.getInstance().getLeatherArmorColor(itemStack).orElse(dyeTintSource.getDefaultColor());
                        tintData[tintindex] = () -> dyedColor;
                    } else if (tintSourceType.equals(ItemModelDefinition.TintSourceType.GRASS)) {
                        ItemModelDefinition.GrassTintSource grassTintSource = (ItemModelDefinition.GrassTintSource) tintSource;
                        int grassColor = TintUtils.getGrassTintColor(grassTintSource.getTemperature(), grassTintSource.getDownfall());
                        tintData[tintindex] = () -> grassColor;
                    } else if (tintSourceType.equals(ItemModelDefinition.TintSourceType.FIREWORK)) {
                        ItemModelDefinition.FireworkTintSource fireworkTintSource = (ItemModelDefinition.FireworkTintSource) tintSource;
                        int overlayColor;
                        if (itemStack.getItemMeta() instanceof FireworkEffectMeta) {
                            FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) itemStack.getItemMeta();
                            int[] is;
                            int[] nArray = is = fireworkEffectMeta.hasEffect() ? fireworkEffectMeta.getEffect().getColors().stream().mapToInt(c -> c.asRGB()).toArray() : null;
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
                        } else {
                            overlayColor = fireworkTintSource.getDefaultColor();
                        }
                        tintData[tintindex] = () -> overlayColor;
                    } else if (tintSourceType.equals(ItemModelDefinition.TintSourceType.POTION)) {
                        ItemModelDefinition.PotionTintSource potionTintSource = (ItemModelDefinition.PotionTintSource) tintSource;
                        int potionColor;
                        if (itemStack.getItemMeta() instanceof PotionMeta) {
                            PotionMeta meta = (PotionMeta) itemStack.getItemMeta();
                            PotionType potiontype = NMSAddon.getInstance().getBasePotionType(itemStack);
                            int color;
                            try {
                                if (meta.hasColor()) {
                                    color = meta.getColor().asRGB();
                                } else {
                                    color = PotionUtils.getPotionBaseColor(potiontype);
                                }
                            } catch (Throwable e) {
                                color = potionTintSource.getDefaultColor();
                            }
                            potionColor = color;
                        } else {
                            potionColor = potionTintSource.getDefaultColor();
                        }
                        tintData[tintindex] = () -> potionColor;
                    } else if (tintSourceType.equals(ItemModelDefinition.TintSourceType.MAP_COLOR)) {
                        ItemModelDefinition.MapColorTintSource mapColorTintSource = (ItemModelDefinition.MapColorTintSource) tintSource;
                        int markingColor;
                        if (itemStack.getItemMeta() instanceof MapMeta) {
                            MapMeta mapMeta = (MapMeta) itemStack.getItemMeta();
                            if (mapMeta.hasColor()) {
                                markingColor = -16777216 | mapMeta.getColor().asRGB() & 16777215;
                            } else {
                                markingColor = mapColorTintSource.getDefaultColor();
                            }
                        } else {
                            markingColor = mapColorTintSource.getDefaultColor();
                        }
                        BufferedImage filledMapMarkings = manager.getTextureManager().getTexture(ResourceRegistry.MAP_MARKINGS_LOCATION).getTexture();
                        BufferedImage tint = ImageUtils.changeColorTo(ImageUtils.copyImage(filledMapMarkings), markingColor);
                        filledMapMarkings = ImageUtils.multiply(filledMapMarkings, tint);
                        providedTextures.put(ResourceRegistry.MAP_MARKINGS_LOCATION, new GeneratedTextureResource(manager, filledMapMarkings));
                    } else if (tintSourceType.equals(ItemModelDefinition.TintSourceType.TEAM)) {
                        ItemModelDefinition.TeamTintSource teamTintSource = (ItemModelDefinition.TeamTintSource) tintSource;
                        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getEntryTeam(player.getName());
                        int color;
                        if (team == null || !team.getColor().isColor()) {
                            color = teamTintSource.getDefaultColor();
                        } else {
                            color = ColorUtils.getColor(ChatColorUtils.getColor(team.getColor().toString())).getRGB();
                        }
                        tintData[tintindex] = () -> color;
                    } else if (tintSourceType.equals(ItemModelDefinition.TintSourceType.CUSTOM_MODEL_DATA)) {
                        ItemModelDefinition.CustomModelDataTintSource customModelDataTintSource = (ItemModelDefinition.CustomModelDataTintSource) tintSource;
                        CustomModelData customModelData = NMSAddon.getInstance().getCustomModelData(itemStack);
                        int color;
                        if (customModelData == null || customModelData.getColor(customModelDataTintSource.getIndex()) == null) {
                            color = customModelDataTintSource.getDefaultColor();
                        } else {
                            color = customModelData.getColor(customModelDataTintSource.getIndex());
                        }
                        tintData[tintindex] = () -> color;
                    } else {
                        throw new IllegalArgumentException("Unknown tint source type: " + tintSourceType);
                    }
                }
                tintColorProvider = new TintColorProvider.TintIndexData(tintData);
            }
            return Collections.singletonList(new ModelLayer(modelKey, Collections.emptyMap(), providedTextures, tintColorProvider, postResolveFunctionGenerator.generate(modelKey, Collections.emptyMap())));
        } else if (itemModelDefinitionType.equals(ItemModelDefinition.ItemModelDefinitionType.COMPOSITE)) {
            ItemModelDefinition.ItemModelDefinitionComposite composite = (ItemModelDefinition.ItemModelDefinitionComposite) itemModelDefinition;
            return composite.getModels().stream()
                    .map(m -> resolveItemModelDefinition(manager, player, displayPosition, itemStack, itemModelDefinition, postResolveFunctionGenerator))
                    .flatMap(l -> l.stream())
                    .collect(Collectors.toList());
        } else if (itemModelDefinitionType.equals(ItemModelDefinition.ItemModelDefinitionType.CONDITION)) {
            ItemModelDefinition.ItemModelDefinitionCondition condition = (ItemModelDefinition.ItemModelDefinitionCondition) itemModelDefinition;
            ItemModelDefinition.ConditionPropertyType<?> propertyType = condition.getPropertyType();
            boolean evaluation;
            if (propertyType.equals(ItemModelDefinition.ConditionPropertyType.USING_ITEM)) {
                evaluation = false;
            } else if (propertyType.equals(ItemModelDefinition.ConditionPropertyType.BROKEN)) {
                if (itemStack.getType().getMaxDurability() > 0) {
                    int maxDur = itemStack.getType().getMaxDurability();
                    int damage = InteractiveChat.version.isLegacy() ? itemStack.getDurability() : ((Damageable) itemStack.getItemMeta()).getDamage();
                    evaluation = maxDur - damage == 1;
                } else {
                    evaluation = false;
                }
            } else if (propertyType.equals(ItemModelDefinition.ConditionPropertyType.DAMAGED)) {
                if (itemStack.getType().getMaxDurability() > 0 && NMSAddon.getInstance().isItemUnbreakable(itemStack)) {
                    int maxDur = itemStack.getType().getMaxDurability();
                    int damage = InteractiveChat.version.isLegacy() ? itemStack.getDurability() : ((Damageable) itemStack.getItemMeta()).getDamage();
                    evaluation = damage > 0;
                } else {
                    evaluation = false;
                }
            } else if (propertyType.equals(ItemModelDefinition.ConditionPropertyType.HAS_COMPONENT)) {
                ItemModelDefinition.HasComponentConditionProperty hasComponentConditionProperty = (ItemModelDefinition.HasComponentConditionProperty) condition;
                evaluation = NMSAddon.getInstance().hasDataComponent(itemStack, hasComponentConditionProperty.getComponent(), hasComponentConditionProperty.isIgnoreDefault());
            } else if (propertyType.equals(ItemModelDefinition.ConditionPropertyType.FISHING_ROD_CAST)) {
                ICPlayer icplayer = player.getPlayer();
                boolean evaluation0 = false;
                if (icplayer != null && icplayer.isLocal()) {
                    Player bukkitPlayer = icplayer.getLocalPlayer();
                    if (NMSAddon.getInstance().getFishHook(bukkitPlayer) != null) {
                        ItemStack mainHandItem = bukkitPlayer.getEquipment().getItemInHand();
                        ItemStack offHandItem = bukkitPlayer.getEquipment().getItemInOffHand();
                        if ((mainHandItem != null && mainHandItem.equals(itemStack)) || ((offHandItem != null && offHandItem.equals(itemStack)) && (mainHandItem == null || !XMaterial.matchXMaterial(mainHandItem).equals(XMaterial.FISHING_ROD)))) {
                            evaluation0 = true;
                        }
                    }
                }
                evaluation = evaluation0;
            } else if (propertyType.equals(ItemModelDefinition.ConditionPropertyType.BUNDLE_SELECTED_ITEM)) {
                evaluation = false;
            } else if (propertyType.equals(ItemModelDefinition.ConditionPropertyType.SELECTED)) {
                evaluation = false;
            } else if (propertyType.equals(ItemModelDefinition.ConditionPropertyType.CARRIED)) {
                evaluation = false;
            } else if (propertyType.equals(ItemModelDefinition.ConditionPropertyType.EXTENDED_VIEW)) {
                evaluation = false;
            } else if (propertyType.equals(ItemModelDefinition.ConditionPropertyType.KEYBIND_DOWN)) {
                evaluation = false;
            } else if (propertyType.equals(ItemModelDefinition.ConditionPropertyType.VIEW_ENTITY)) {
                evaluation = false;
            } else if (propertyType.equals(ItemModelDefinition.ConditionPropertyType.COMPONENT)) {
                ItemModelDefinition.ComponentConditionProperty componentConditionProperty = (ItemModelDefinition.ComponentConditionProperty) condition;
                String predicate = componentConditionProperty.getPredicate();
                String value = componentConditionProperty.getValue();
                evaluation = NMSAddon.getInstance().evaluateComponentPredicateOnItemStack(itemStack, predicate, value);
            } else if (propertyType.equals(ItemModelDefinition.ConditionPropertyType.CUSTOM_MODEL_DATA)) {
                ItemModelDefinition.CustomModelDataConditionProperty customModelDataConditionProperty = (ItemModelDefinition.CustomModelDataConditionProperty) condition;
                CustomModelData customModelData = NMSAddon.getInstance().getCustomModelData(itemStack);
                evaluation = customModelData != null && Boolean.TRUE.equals(customModelData.getFlag(customModelDataConditionProperty.getIndex()));
            } else {
                evaluation = false;
            }
            ItemModelDefinition evaluated = condition.getModel(evaluation);
            return resolveItemModelDefinition(manager, player, displayPosition, itemStack, evaluated, postResolveFunctionGenerator);
        } else if (itemModelDefinitionType.equals(ItemModelDefinition.ItemModelDefinitionType.SELECT)) {
            ItemModelDefinition.ItemModelDefinitionSelect<?> select = (ItemModelDefinition.ItemModelDefinitionSelect<?>) itemModelDefinition;
            ItemModelDefinition.SelectPropertyType<?> propertyType = select.getPropertyType();
            Object value;
            if (propertyType.equals(ItemModelDefinition.SelectPropertyType.MAIN_HAND)) {
                ICPlayer icPlayer = player.getPlayer();
                value = player.isRightHanded() ? MainHand.RIGHT : MainHand.LEFT;
            } else if (propertyType.equals(ItemModelDefinition.SelectPropertyType.CHARGE_TYPE)) {
                if (itemStack.getItemMeta() instanceof CrossbowMeta) {
                    CrossbowMeta meta = (CrossbowMeta) itemStack.getItemMeta();
                    List<ItemStack> charged = meta.getChargedProjectiles();
                    if (charged != null && !charged.isEmpty()) {
                        if (charged.stream().anyMatch(i -> ICMaterial.from(i).isMaterial(XMaterial.FIREWORK_ROCKET))) {
                            value = ChargeType.ROCKET;
                        } else {
                            value = ChargeType.ARROW;
                        }
                    } else {
                        value = ChargeType.NONE;
                    }
                } else {
                    value = ChargeType.NONE;
                }
            } else if (propertyType.equals(ItemModelDefinition.SelectPropertyType.COMPONENT)) {
                ItemModelDefinition.ComponentSelectProperty componentSelectProperty = (ItemModelDefinition.ComponentSelectProperty) select;
                Key component = componentSelectProperty.getComponent();
                value = NMSAddon.getInstance().getItemStackDataComponentValue(itemStack, component);
            } else if (propertyType.equals(ItemModelDefinition.SelectPropertyType.TRIM_MATERIAL)) {
                if (itemStack.getItemMeta() instanceof ArmorMeta) {
                    ArmorMeta armorMeta = (ArmorMeta) itemStack.getItemMeta();
                    ArmorTrim armorTrim = armorMeta.getTrim();
                    if (armorTrim != null) {
                        TrimMaterial trimMaterial = armorTrim.getMaterial();
                        value = KeyUtils.toKey(trimMaterial.getKey());
                    } else {
                        value = null;
                    }
                } else {
                    value = null;
                }
            } else if (propertyType.equals(ItemModelDefinition.SelectPropertyType.BLOCK_STATE)) {
                ItemModelDefinition.BlockStateSelectProperty blockStateSelectProperty = (ItemModelDefinition.BlockStateSelectProperty) select;
                value = NMSAddon.getInstance().getBlockStateProperty(itemStack, blockStateSelectProperty.getBlockStateProperty());
            } else if (propertyType.equals(ItemModelDefinition.SelectPropertyType.DISPLAY_CONTEXT)) {
                value = displayPosition;
            } else if (propertyType.equals(ItemModelDefinition.SelectPropertyType.LOCAL_TIME)) {
                ItemModelDefinition.LocalTimeSelectProperty localTimeSelectProperty = (ItemModelDefinition.LocalTimeSelectProperty) select;
                ULocale uLocale = new ULocale(localTimeSelectProperty.getLocale());
                Calendar calendar = localTimeSelectProperty.getTimeZone().map(timeZone -> Calendar.getInstance(timeZone, uLocale)).orElseGet(() -> Calendar.getInstance(uLocale));
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(localTimeSelectProperty.getPattern(), uLocale);
                simpleDateFormat.setCalendar(calendar);
                String dateMatch = null;
                try {
                    dateMatch = simpleDateFormat.format(new Date());
                } catch (Exception ignored) {
                }
                value = dateMatch;
            } else if (propertyType.equals(ItemModelDefinition.SelectPropertyType.CONTEXT_DIMENSION)) {
                ICPlayer icPlayer = player.getPlayer();
                if (icPlayer != null && icPlayer.isLocal()) {
                    value = NMSAddon.getInstance().getNamespacedKey(icPlayer.getLocalPlayer().getWorld());
                } else {
                    value = null;
                }
            } else if (propertyType.equals(ItemModelDefinition.SelectPropertyType.CONTEXT_ENTITY_TYPE)) {
                value = KeyUtils.toKey(EntityType.PLAYER.getKey());
            } else if (propertyType.equals(ItemModelDefinition.SelectPropertyType.CUSTOM_MODEL_DATA)) {
                ItemModelDefinition.CustomModelDataSelectProperty customModelDataSelectProperty = (ItemModelDefinition.CustomModelDataSelectProperty) select;
                CustomModelData customModelData = NMSAddon.getInstance().getCustomModelData(itemStack);
                if (customModelData != null) {
                    value = customModelData.getString(customModelDataSelectProperty.getIndex());
                } else {
                    value = null;
                }
            } else {
                value = null;
            }
            ItemModelDefinition evaluated = select.getEntryCase(value);
            if (evaluated == null) {
                evaluated = select.hasFallback() ? select.getFallback() : ItemModelDefinitionManager.MISSING_MODEL;
            }
            return resolveItemModelDefinition(manager, player, displayPosition, itemStack, evaluated, postResolveFunctionGenerator);
        } else if (itemModelDefinitionType.equals(ItemModelDefinition.ItemModelDefinitionType.RANGE_DISPATCH)) {
            ItemModelDefinition.ItemModelDefinitionRangeDispatch rangeDispatch = (ItemModelDefinition.ItemModelDefinitionRangeDispatch) itemModelDefinition;
            ItemModelDefinition.RangeDispatchPropertyType<?> propertyType = rangeDispatch.getPropertyType();
            float value;
            if (propertyType.equals(ItemModelDefinition.RangeDispatchPropertyType.BUNDLE_FULLNESS)) {
                if (ICMaterial.from(itemStack).isMaterial(XMaterial.BUNDLE)) {
                    @SuppressWarnings("UnstableApiUsage")
                    float weight = BundleUtils.getWeight(((BundleMeta) itemStack.getItemMeta()).getItems()).floatValue();
                    value = weight * rangeDispatch.getScale();
                } else {
                    value = 0;
                }
            } else if (propertyType.equals(ItemModelDefinition.RangeDispatchPropertyType.DAMAGE)) {
                ItemModelDefinition.DamageRangeDispatchProperty damageRangeDispatchProperty = (ItemModelDefinition.DamageRangeDispatchProperty) rangeDispatch;
                ItemDamageInfo itemDamageInfo = NMSAddon.getInstance().getItemDamageInfo(itemStack);
                float damage = (float) itemDamageInfo.getDamage();
                float maxDamage = (float) itemDamageInfo.getMaxDamage();
                if (damageRangeDispatchProperty.isNormalize()) {
                    value = Math.min(Math.max(damage / maxDamage, 0.0F), 1.0F) * damageRangeDispatchProperty.getScale();
                } else {
                    value = Math.min(Math.max(damage, 0.0F), maxDamage) * damageRangeDispatchProperty.getScale();
                }
            } else if (propertyType.equals(ItemModelDefinition.RangeDispatchPropertyType.COUNT)) {
                ItemModelDefinition.CountRangeDispatchProperty countRangeDispatchProperty = (ItemModelDefinition.CountRangeDispatchProperty) rangeDispatch;
                float count = (float) itemStack.getAmount();
                float maxStackSize = (float) itemStack.getMaxStackSize();
                if (countRangeDispatchProperty.isNormalize()) {
                    value = Math.min(Math.max(count / maxStackSize, 0.0F), 1.0F) * countRangeDispatchProperty.getScale();
                } else {
                    value = Math.min(Math.max(count, 0.0F), maxStackSize) * countRangeDispatchProperty.getScale();
                }
            } else if (propertyType.equals(ItemModelDefinition.RangeDispatchPropertyType.COOLDOWN)) {
                ICPlayer icPlayer = player.getPlayer();
                if (icPlayer != null && icPlayer.isLocal()) {
                    value = NMSAddon.getInstance().getItemCooldownProgress(icPlayer.getLocalPlayer(), itemStack) * rangeDispatch.getScale();
                } else {
                    value = 0F;
                }
            } else if (propertyType.equals(ItemModelDefinition.RangeDispatchPropertyType.TIME)) {
                ItemModelDefinition.TimeRangeDispatchProperty timeRangeDispatchProperty = (ItemModelDefinition.TimeRangeDispatchProperty) rangeDispatch;
                ICPlayer icPlayer = player.getPlayer();
                float angle = RANDOM.nextFloat();
                if (icPlayer != null && icPlayer.isLocal()) {
                    World world = icPlayer.getLocalPlayer().getWorld();
                    switch (timeRangeDispatchProperty.getSource()) {
                        case DAYTIME:
                            angle = NMSAddon.getInstance().getSkyAngle(world);
                            break;
                        case MOON_PHASE:
                            angle = (float) NMSAddon.getInstance().getMoonPhase(world) / 8.0f;
                            break;
                    }
                }
                value = angle * timeRangeDispatchProperty.getScale();
            } else if (propertyType.equals(ItemModelDefinition.RangeDispatchPropertyType.COMPASS)) {
                ItemModelDefinition.CompassRangeDispatchProperty compassRangeDispatchProperty = (ItemModelDefinition.CompassRangeDispatchProperty) rangeDispatch;
                ICPlayer icPlayer = player.getPlayer();
                ItemStack compass = itemStack;
                if (CompassUtils.isLodestoneCompass(compass)) {
                    if (InteractiveChat.hideLodestoneCompassPos) {
                        compass = CompassUtils.hideLodestoneCompassPosition(compass);
                    }
                }
                Location location = null;
                Player bukkitPlayer = null;
                if (icPlayer != null && icPlayer.isLocal()) {
                    bukkitPlayer = icPlayer.getLocalPlayer();
                    World world = bukkitPlayer.getWorld();
                    switch (compassRangeDispatchProperty.getTarget()) {
                        case SPAWN:
                            location = world.getSpawnLocation();
                            break;
                        case LODESTONE:
                            CompassMeta meta = (CompassMeta) compass.getItemMeta();
                            Location target;
                            if (meta.hasLodestone()) {
                                Location lodestone = meta.getLodestone();
                                target = new Location(lodestone.getWorld(), lodestone.getBlockX() + 0.5, lodestone.getBlockY(), lodestone.getBlockZ() + 0.5, lodestone.getYaw(), lodestone.getPitch());
                            }
                            break;
                        case RECOVERY:
                            location = bukkitPlayer.getLastDeathLocation();
                            break;
                    }
                }
                float angle;
                if (location != null && bukkitPlayer != null && location.getWorld().equals(bukkitPlayer.getWorld())) {
                    Location playerLocation = bukkitPlayer.getEyeLocation();
                    playerLocation.setPitch(0);
                    Vector looking = playerLocation.getDirection();
                    Vector pointing = location.toVector().subtract(playerLocation.toVector());
                    pointing.setY(0);
                    float degree = (float) VectorUtils.getBearing(looking, pointing);
                    angle = (degree % 360 + 360) % 360;
                } else {
                    angle = RANDOM.nextFloat();
                }
                value = angle * compassRangeDispatchProperty.getScale();
            } else if (propertyType.equals(ItemModelDefinition.RangeDispatchPropertyType.CROSSBOW_PULL)) {
                if (ICMaterial.from(itemStack).isMaterial(XMaterial.CROSSBOW)) {
                    CrossbowMeta crossbowMeta = (CrossbowMeta) itemStack.getItemMeta();
                    if (crossbowMeta.hasChargedProjectiles()) {
                        ICPlayer icPlayer = player.getPlayer();
                        int pullTime;
                        int tickUsedSoFar;
                        if (icPlayer != null && icPlayer.isLocal()) {
                            Player bukkitPlayer = icPlayer.getLocalPlayer();
                            pullTime = NMSAddon.getInstance().getCrossbowPullTime(itemStack, bukkitPlayer);
                            tickUsedSoFar = NMSAddon.getInstance().getTicksUsedSoFar(itemStack, bukkitPlayer);
                        } else {
                            pullTime = 0;
                            tickUsedSoFar = 0;
                        }
                        value = ((float) tickUsedSoFar / (float) pullTime) * rangeDispatch.getScale();
                    } else {
                        value = 0F;
                    }
                } else {
                    value = 0F;
                }
            } else if (propertyType.equals(ItemModelDefinition.RangeDispatchPropertyType.USE_DURATION)) {
                ItemModelDefinition.UseDurationRangeDispatchProperty useDurationRangeDispatchProperty = (ItemModelDefinition.UseDurationRangeDispatchProperty) rangeDispatch;
                ICPlayer icPlayer = player.getPlayer();
                if (icPlayer != null && icPlayer.isLocal() && player.getMainHandItem().equals(itemStack)) {
                    Player bukkitPlayer = icPlayer.getLocalPlayer();
                    if (useDurationRangeDispatchProperty.isRemaining()) {
                        value = NMSAddon.getInstance().getItemUseTimeLeft(bukkitPlayer) * rangeDispatch.getScale();
                    } else {
                        value = NMSAddon.getInstance().getTicksUsedSoFar(itemStack, bukkitPlayer) * rangeDispatch.getScale();
                    }
                } else {
                    value = 0F;
                }
            } else if (propertyType.equals(ItemModelDefinition.RangeDispatchPropertyType.USE_CYCLE)) {
                ItemModelDefinition.UseCycleRangeDispatchProperty useCycleRangeDispatchProperty = (ItemModelDefinition.UseCycleRangeDispatchProperty) rangeDispatch;
                ICPlayer icPlayer = player.getPlayer();
                if (icPlayer != null && icPlayer.isLocal() && player.getMainHandItem().equals(itemStack)) {
                    Player bukkitPlayer = icPlayer.getLocalPlayer();
                    value = (NMSAddon.getInstance().getItemUseTimeLeft(bukkitPlayer) % useCycleRangeDispatchProperty.getPeriod()) * useCycleRangeDispatchProperty.getScale();
                } else {
                    value = 0F;
                }
            } else if (propertyType.equals(ItemModelDefinition.RangeDispatchPropertyType.CUSTOM_MODEL_DATA)) {
                ItemModelDefinition.CustomModelDataRangeDispatchProperty customModelDataRangeDispatchProperty = (ItemModelDefinition.CustomModelDataRangeDispatchProperty) rangeDispatch;
                CustomModelData customModelData = NMSAddon.getInstance().getCustomModelData(itemStack);
                if (customModelData != null && customModelData.getFloat(customModelDataRangeDispatchProperty.getIndex()) != null) {
                    value = customModelData.getFloat(customModelDataRangeDispatchProperty.getIndex());
                } else {
                    value = 0F;
                }
            } else {
                value = 0F;
            }
            int entryIndex = rangeDispatch.getEntryIndex(value);
            ItemModelDefinition evaluated;
            if (entryIndex == -1) {
                evaluated = rangeDispatch.hasFallback() ? rangeDispatch.getFallback() : ItemModelDefinitionManager.MISSING_MODEL;
            } else {
                evaluated = rangeDispatch.getEntries().get(entryIndex).getModel();
            }
            return resolveItemModelDefinition(manager, player, displayPosition, itemStack, evaluated, postResolveFunctionGenerator);
        } else if (itemModelDefinitionType.equals(ItemModelDefinition.ItemModelDefinitionType.EMPTY)) {
            return Collections.emptyList();
        } else if (itemModelDefinitionType.equals(ItemModelDefinition.ItemModelDefinitionType.BUNDLE_SELECTED_ITEM)) {
            return Collections.emptyList();
        } else if (itemModelDefinitionType.equals(ItemModelDefinition.ItemModelDefinitionType.SPECIAL)) {
            ItemModelDefinition.ItemModelDefinitionSpecial special = (ItemModelDefinition.ItemModelDefinitionSpecial) itemModelDefinition;
            ItemModelDefinition.SpecialModelType<?> specialModelType = special.getModel().getModelType();
            Map<String, TextureResource> providedTextures = new HashMap<>();
            String modelKey;
            if (specialModelType.equals(ItemModelDefinition.SpecialModelType.BED)) {
                String base = special.getBase();
                if (base.contains(":")) {
                    base = base.substring(base.lastIndexOf(":") + 1);
                }
                if (base.contains("/")) {
                    base = base.substring(base.lastIndexOf("/") + 1);
                }
                modelKey = ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + base;
            } else if (specialModelType.equals(ItemModelDefinition.SpecialModelType.BANNER)) {
                BannerAssetResult bannerAsset = BannerGraphics.generateBannerAssets(itemStack);
                providedTextures.put(ResourceRegistry.BANNER_BASE_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(manager, bannerAsset.getBase()));
                providedTextures.put(ResourceRegistry.BANNER_PATTERNS_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(manager, bannerAsset.getPatterns()));
                modelKey = ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + itemStack.getType().getKey().getKey();
            } else if (specialModelType.equals(ItemModelDefinition.SpecialModelType.CONDUIT)) {
                modelKey = ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + itemStack.getType().getKey().getKey();
            } else if (specialModelType.equals(ItemModelDefinition.SpecialModelType.CHEST)) {
                ItemModelDefinition.ChestSpecialModel chestSpecialModel = (ItemModelDefinition.ChestSpecialModel) special.getModel();
                Key key = KeyUtils.toKey(chestSpecialModel.getTexture());
                TextureResource chestTexture = manager.getTextureManager().getTexture(key.namespace() + ":entity/chest/" + key.value());
                providedTextures.put(ResourceRegistry.CHEST_TEXTURE_PLACEHOLDER, chestTexture);
                modelKey = ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + "texture_chest";
            } else if (specialModelType.equals(ItemModelDefinition.SpecialModelType.DECORATED_POT)) {
                BlockStateMeta meta = (BlockStateMeta) itemStack.getItemMeta();
                BlockState state = meta.getBlockState();
                if (state instanceof DecoratedPot) {
                    DecoratedPot pot = (DecoratedPot) state;
                    List<Material> materials = pot.getShards();
                    for (int i = 0; i < materials.size(); i++) {
                        TextureResource textureResource = null;
                        ItemStack sherd = new ItemStack(materials.get(i));
                        Key type = NMSAddon.getInstance().getDecoratedPotSherdPatternName(sherd);
                        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_20_3)) {
                            String namespace = type.namespace();
                            String key = type.value();
                            String pattern = key.equals("blank") ? "decorated_pot_side" : key + "_pottery_pattern";
                            textureResource = manager.getTextureManager().getTexture(ResourceRegistry.DECORATED_POT_SHERD_LOCATION.replaceFirst("%s", namespace).replaceFirst("%s", pattern));
                            providedTextures.put(ResourceRegistry.DECORATED_POT_FACE_PLACEHOLDER.replace("%s", String.valueOf(i)), textureResource);
                        } else {
                            String key = type.value();
                            textureResource = manager.getTextureManager().getTexture(ResourceRegistry.DECORATED_POT_SHERD_MINECRAFT_LOCATION.replace("%s", key));
                            providedTextures.put(ResourceRegistry.DECORATED_POT_FACE_PLACEHOLDER.replace("%s", String.valueOf(i)), textureResource);
                        }
                    }
                }
                modelKey = ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + itemStack.getType().getKey().getKey();
            } else if (specialModelType.equals(ItemModelDefinition.SpecialModelType.HEAD)) {
                if (ICMaterial.from(itemStack).isMaterial(XMaterial.PLAYER_HEAD)) {
                    BufferedImage skinImage = manager.getTextureManager().getTexture(ResourceRegistry.DEFAULT_WIDE_SKIN_LOCATION).getTexture();
                    if (itemStack.hasItemMeta()) {
                        GameProfile gameProfile = NMSAddon.getInstance().getPlayerHeadProfile(itemStack);
                        String skinURL = GameProfileUtils.getSkinUrl(gameProfile);
                        if (skinURL != null) {
                            try {
                                skinImage = ImageUtils.downloadImage(skinURL);
                            } catch (Exception ignored) {
                            }
                        }
                        if (skinImage == null && GameProfileUtils.hasValidUUID(gameProfile)) {
                            skinImage = manager.getTextureManager().getTexture(DefaultSkinUtils.getTexture(gameProfile.getId())).getTexture();
                        }
                    }
                    providedTextures.put(ResourceRegistry.SKIN_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(manager, ModelUtils.convertToModernSkinTexture(skinImage)));
                }
                modelKey = ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + itemStack.getType().getKey().getKey();
            } else if (specialModelType.equals(ItemModelDefinition.SpecialModelType.SHULKER_BOX)) {
                modelKey = ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + itemStack.getType().getKey().getKey();
            } else if (specialModelType.equals(ItemModelDefinition.SpecialModelType.SHIELD)) {
                BannerAssetResult shieldAsset = BannerGraphics.generateShieldAssets(itemStack);
                providedTextures.put(ResourceRegistry.SHIELD_BASE_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(manager, shieldAsset.getBase()));
                providedTextures.put(ResourceRegistry.SHIELD_PATTERNS_TEXTURE_PLACEHOLDER, new GeneratedTextureResource(manager, shieldAsset.getPatterns()));
                modelKey = ResourceRegistry.BUILTIN_ENTITY_MODEL_LOCATION + itemStack.getType().getKey().getKey();
            } else if (specialModelType.equals(ItemModelDefinition.SpecialModelType.STANDING_SIGN)) {
                ItemModelDefinition.StandingSignSpecialModel standingSignSpecialModel = (ItemModelDefinition.StandingSignSpecialModel) special.getModel();
                String woodType = standingSignSpecialModel.getWoodType();
                if (woodType.contains(":")) {
                    woodType = woodType.substring(woodType.lastIndexOf(":") + 1);
                }
                if (woodType.contains("/")) {
                    woodType = woodType.substring(woodType.lastIndexOf("/") + 1);
                }
                modelKey = ResourceRegistry.ITEM_MODEL_LOCATION + woodType + "_sign";
            } else if (specialModelType.equals(ItemModelDefinition.SpecialModelType.HANGING_SIGN)) {
                ItemModelDefinition.HangingSignSpecialModel hangingSignSpecialModel = (ItemModelDefinition.HangingSignSpecialModel) special.getModel();
                String woodType = hangingSignSpecialModel.getWoodType();
                if (woodType.contains(":")) {
                    woodType = woodType.substring(woodType.lastIndexOf(":") + 1);
                }
                if (woodType.contains("/")) {
                    woodType = woodType.substring(woodType.lastIndexOf("/") + 1);
                }
                modelKey = ResourceRegistry.ITEM_MODEL_LOCATION + woodType + "_hanging_sign";
            } else if (specialModelType.equals(ItemModelDefinition.SpecialModelType.TRIDENT)) {
                modelKey = ResourceRegistry.ITEM_MODEL_LOCATION + itemStack.getType().getKey().getKey();
            } else {
                throw new IllegalArgumentException("Unsupported special model type: " + specialModelType);
            }
            return Collections.singletonList(new ModelLayer(modelKey, Collections.emptyMap(), providedTextures, TintColorProvider.EMPTY_INSTANCE, postResolveFunctionGenerator.generate(modelKey, Collections.emptyMap())));
        } else {
            throw new IllegalArgumentException("Unsupported item model definition type: " + itemModelDefinitionType);
        }
    }

    private static String ensureNamespace(String key) {
        if (key == null) {
            return null;
        }
        if (!key.contains(":")) {
            return ResourceRegistry.DEFAULT_NAMESPACE + ":" + key;
        }
        return key;
    }

    private static String tagToString(Tag<?> tag) {
        if (tag instanceof StringTag) {
            return ((StringTag) tag).getValue();
        } else {
            return tag.valueToString();
        }
    }

    @FunctionalInterface
    public interface PostResolveFunctionGenerator {

        Function<BlockModel, ValuePairs<BlockModel, Map<String, TextureResource>>> generate(String modelKey, Map<ModelOverride.ModelOverrideType, Float> predicates);

    }

    public static class ItemStackProcessResult {

        private final boolean requiresEnchantmentGlint;
        private final List<ModelLayer> modelParts;
        private final Function<ModelRenderer.RawEnchantmentGlintParameters, BufferedImage> enchantmentGlintFunction;
        private final Function<ModelRenderer.RawEnchantmentGlintParameters, RawEnchantmentGlintData> rawEnchantmentGlintFunction;

        public ItemStackProcessResult(boolean requiresEnchantmentGlint, List<ModelLayer> modelParts, Function<ModelRenderer.RawEnchantmentGlintParameters, BufferedImage> enchantmentGlintFunction, Function<ModelRenderer.RawEnchantmentGlintParameters, RawEnchantmentGlintData> rawEnchantmentGlintFunction) {
            this.requiresEnchantmentGlint = requiresEnchantmentGlint;
            this.modelParts = modelParts;
            this.enchantmentGlintFunction = enchantmentGlintFunction;
            this.rawEnchantmentGlintFunction = rawEnchantmentGlintFunction;
        }

        public boolean requiresEnchantmentGlint() {
            return requiresEnchantmentGlint;
        }

        public List<ModelLayer> getModelLayers() {
            return modelParts;
        }

        public Function<ModelRenderer.RawEnchantmentGlintParameters, BufferedImage> getEnchantmentGlintFunction() {
            return enchantmentGlintFunction;
        }

        public Function<ModelRenderer.RawEnchantmentGlintParameters, RawEnchantmentGlintData> getRawEnchantmentGlintFunction() {
            return rawEnchantmentGlintFunction;
        }

    }

}
