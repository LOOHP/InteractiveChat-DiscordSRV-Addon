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
import com.loohp.interactivechat.hooks.ecoenchants.EcoHook;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.io.github.bananapuncher714.nbteditor.NBTEditor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.libs.net.querz.nbt.io.SNBTDeserializer;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.CompoundTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.ListTag;
import com.loohp.interactivechat.libs.net.querz.nbt.tag.StringTag;
import com.loohp.interactivechat.libs.org.apache.commons.text.WordUtils;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ColorUtils;
import com.loohp.interactivechat.utils.FilledMapUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.ItemStackUtils;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.RarityUtils;
import com.loohp.interactivechat.utils.XMaterialUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ToolTipComponent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ToolTipComponent.ToolTipType;
import com.loohp.interactivechatdiscordsrvaddon.registry.DiscordDataRegistry;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.PatternTypeWrapper;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.mcdiscordreserializer.discord.DiscordSerializer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class DiscordItemStackUtils {

    public static final String DISCORD_EMPTY = "\u200e";

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().extractUrls().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    private static final DecimalFormat ATTRIBUTE_FORMAT = new DecimalFormat("0");

    private static Method bukkitBukkitClassGetMapShortMethod = null;
    private static Method bukkitMapViewClassGetIdMethod = null;
    private static boolean chatColorHasGetColor = false;
    private static boolean itemMetaHasUnbreakable = false;

    static {
        try {
            try {
                //noinspection JavaReflectionMemberAccess
                bukkitBukkitClassGetMapShortMethod = Bukkit.class.getMethod("getMap", short.class);
            } catch (NoSuchMethodException e1) {
            }
            try {
                bukkitMapViewClassGetIdMethod = MapView.class.getMethod("getId");
            } catch (NoSuchMethodException e1) {
            }
            chatColorHasGetColor = Stream.of(ChatColor.class.getMethods()).anyMatch(each -> each.getName().equalsIgnoreCase("getColor") && each.getReturnType().equals(Color.class));
            itemMetaHasUnbreakable = Stream.of(ItemMeta.class.getMethods()).anyMatch(each -> each.getName().equalsIgnoreCase("isUnbreakable") && each.getReturnType().equals(boolean.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Color getDiscordColor(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName() && !meta.getDisplayName().equals("")) {
                String colorStr = ChatColorUtils.getFirstColors(meta.getDisplayName());
                if (colorStr.length() > 1) {
                    ChatColor chatColor = ColorUtils.toChatColor(colorStr);
                    if (chatColor != null && ChatColorUtils.isColor(chatColor)) {
                        return chatColorHasGetColor ? chatColor.getColor() : ColorUtils.getColor(chatColor);
                    }
                }
            }
        }
        return chatColorHasGetColor ? RarityUtils.getRarityColor(item).getColor() : ColorUtils.getColor(RarityUtils.getRarityColor(item));
    }

    public static DiscordDescription getDiscordDescription(ItemStack item, OfflineICPlayer player) throws Exception {
        String language = InteractiveChatDiscordSrvAddon.plugin.language;

        if (!item.getType().equals(Material.AIR) && InteractiveChat.ecoHook) {
            Player bukkitPlayer = player.getPlayer() == null || !player.getPlayer().isLocal() ? null : player.getPlayer().getLocalPlayer();
            if (bukkitPlayer == null && !Bukkit.getOnlinePlayers().isEmpty()) {
                bukkitPlayer = Bukkit.getOnlinePlayers().iterator().next();
            }
            item = EcoHook.setEcoLores(item, bukkitPlayer);
        }

        if (item == null) {
            item = new ItemStack(Material.AIR);
        }
        XMaterial xMaterial = XMaterialUtils.matchXMaterial(item);
        String name = PlainTextComponentSerializer.plainText().serialize(LegacyComponentSerializer.legacySection().deserialize(InteractiveChatComponentSerializer.bungeecordApiLegacy().serialize(ItemStackUtils.getDisplayName(item), language)));
        if (item.getAmount() == 1 || item == null || item.getType().equals(Material.AIR)) {
            name = InteractiveChatDiscordSrvAddon.plugin.itemDisplaySingle.replace("{Item}", ComponentStringUtils.stripColorAndConvertMagic(name)).replace("{Amount}", String.valueOf(item.getAmount()));
        } else {
            name = InteractiveChatDiscordSrvAddon.plugin.itemDisplayMultiple.replace("{Item}", ComponentStringUtils.stripColorAndConvertMagic(name)).replace("{Amount}", String.valueOf(item.getAmount()));
        }

        boolean hasMeta = item.hasItemMeta();
        StringBuilder description = new StringBuilder();

        if (xMaterial.equals(XMaterial.BUNDLE) && hasMeta && item.getItemMeta() instanceof BundleMeta) {
            BundleMeta meta = (BundleMeta) item.getItemMeta();
            List<ItemStack> items = meta.getItems();
            int fullness = BundleUtils.getFullness(items);
            description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getBundleFullness(), language).replaceFirst("%s", fullness + "").replaceFirst("%s", "64")).append("\n");
            description.append("\n");
        }

        if (xMaterial.equals(XMaterial.WRITTEN_BOOK) && hasMeta && item.getItemMeta() instanceof BookMeta) {
            BookMeta meta = (BookMeta) item.getItemMeta();
            String author = meta.getAuthor();
            if (author != null) {
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getBookAuthor(), language).replaceFirst("%1\\$s", ChatColorUtils.stripColor(author))).append("\n");
            }
            Generation generation = meta.getGeneration();
            if (generation == null) {
                generation = Generation.ORIGINAL;
            }
            description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getBookGeneration(generation), language)).append("\n");
            description.append("\n");
        }

        if (xMaterial.equals(XMaterial.SHIELD) && (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)))) {
            if (NBTEditor.contains(item, "BlockEntityTag")) {
                List<Pattern> patterns = Collections.emptyList();
                if (!(item.getItemMeta() instanceof BannerMeta)) {
                    if (item.getItemMeta() instanceof BlockStateMeta) {
                        BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
                        if (bmeta.hasBlockState()) {
                            Banner bannerBlockMeta = (Banner) bmeta.getBlockState();
                            patterns = bannerBlockMeta.getPatterns();
                        }
                    }
                } else {
                    BannerMeta meta = (BannerMeta) item.getItemMeta();
                    patterns = meta.getPatterns();
                }

                for (Pattern pattern : patterns) {
                    PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
                    description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getBannerPatternName(type, pattern.getColor()), language)).append("\n");
                }
            }
        }

        if (xMaterial.isOneOf(Collections.singletonList("CONTAINS:Banner")) && (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)))) {
            List<Pattern> patterns = Collections.emptyList();
            if (!(item.getItemMeta() instanceof BannerMeta)) {
                if (item.getItemMeta() instanceof BlockStateMeta) {
                    BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
                    Banner bannerBlockMeta = (Banner) bmeta.getBlockState();
                    patterns = bannerBlockMeta.getPatterns();
                }
            } else {
                BannerMeta meta = (BannerMeta) item.getItemMeta();
                patterns = meta.getPatterns();
            }

            for (Pattern pattern : patterns) {
                PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getBannerPatternName(type, pattern.getColor()), language)).append("\n");
            }
        }

        if (xMaterial.equals(XMaterial.TROPICAL_FISH_BUCKET)) {
            List<String> translations = TranslationKeyUtils.getTropicalFishBucketName(item);
            if (translations.size() > 0) {
                description.append(LanguageUtils.getTranslation(translations.get(0), language)).append("\n");
                if (translations.size() > 1) {
                    description.append(translations.stream().skip(1).map(each -> LanguageUtils.getTranslation(each, language)).collect(Collectors.joining(", "))).append("\n");
                }
                description.append("\n");
            }
        }

        if (xMaterial.isOneOf(Collections.singletonList("CONTAINS:Music_Disc"))) {
            description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getMusicDiscName(item), language)).append("\n");
        }

        if (xMaterial.equals(XMaterial.FIREWORK_ROCKET)) {
            if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && NBTEditor.contains(item, "Fireworks", "Flight")) {
                int flight = NBTEditor.getByte(item, "Fireworks", "Flight");
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getRocketFlightDuration(), language)).append(" ").append(flight).append("\n");
            }if (hasMeta && item.getItemMeta() instanceof FireworkMeta) {
                FireworkMeta fireworkMeta = (FireworkMeta) item.getItemMeta();
                for (FireworkEffect fireworkEffect : fireworkMeta.getEffects()) {
                    description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkType(fireworkEffect.getType()), language)).append("\n");
                    if (!fireworkEffect.getColors().isEmpty()) {
                        description.append("  ").append(fireworkEffect.getColors().stream().map(each -> {
                            return LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkColor(each), language);
                        }).collect(Collectors.joining(", "))).append("\n");
                    }
                    if (!fireworkEffect.getFadeColors().isEmpty()) {
                        description.append("  ").append(LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkFade(), language)).append(" ").append(fireworkEffect.getFadeColors().stream().map(each -> {
                            return LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkColor(each), language);
                        }).collect(Collectors.joining(", "))).append("\n");
                    }
                    if (fireworkEffect.hasTrail()) {
                        description.append("  ").append(LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkTrail(), language)).append("\n");
                    }
                    if (fireworkEffect.hasFlicker()) {
                        description.append("  ").append(LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkFlicker(), language)).append("\n");
                    }
                }
            }
        }

        if (xMaterial.equals(XMaterial.FIREWORK_STAR) && hasMeta && item.getItemMeta() instanceof FireworkEffectMeta) {
            FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) item.getItemMeta();
            FireworkEffect fireworkEffect = fireworkEffectMeta.getEffect();
            description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkType(fireworkEffect.getType()), language)).append("\n");
            if (!fireworkEffect.getColors().isEmpty()) {
                description.append("  ").append(fireworkEffect.getColors().stream().map(each -> {
                    return LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkColor(each), language);
                }).collect(Collectors.joining(", "))).append("\n");
            }
            if (!fireworkEffect.getFadeColors().isEmpty()) {
                description.append("  ").append(LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkFade(), language)).append(" ").append(fireworkEffect.getFadeColors().stream().map(each -> {
                    return LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkColor(each), language);
                }).collect(Collectors.joining(", "))).append("\n");
            }
            if (fireworkEffect.hasTrail()) {
                description.append("  ").append(LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkTrail(), language)).append("\n");
            }
            if (fireworkEffect.hasFlicker()) {
                description.append("  ").append(LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkFlicker(), language)).append("\n");
            }
        }

        if (xMaterial.equals(XMaterial.CROSSBOW)) {
            CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
            List<ItemStack> charged = meta.getChargedProjectiles();
            if (charged != null && !charged.isEmpty()) {
                ItemStack charge = charged.get(0);
                DiscordDescription chargeItemInfo = getDiscordDescription(charge, player);
                String chargeItemName = chargeItemInfo.getName();
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getCrossbowProjectile(), language)).append(" [**").append(chargeItemName).append("**]\n\n");
                if (XMaterialUtils.matchXMaterial(charge).equals(XMaterial.FIREWORK_ROCKET) && chargeItemInfo.getDescription().isPresent()) {
                    description.append("  ").append(chargeItemInfo.getDescription().get().replace("\n", "\n  "));
                }
            }
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && FilledMapUtils.isFilledMap(item)) {
            MapMeta map = (MapMeta) item.getItemMeta();
            MapView mapView = FilledMapUtils.getMapView(item);
            int id = FilledMapUtils.getMapId(item);
            int scale = mapView == null ? 0 : mapView.getScale().getValue();
            if (!InteractiveChat.version.isLegacy()) {
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getFilledMapId(), language).replaceFirst("%s", id + "")).append("\n");
            } else {
                name += " (#" + id + ")";
            }
            description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getFilledMapScale(), language).replaceFirst("%s", (int) Math.pow(2, scale) + "")).append("\n");
            description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getFilledMapLevel(), language).replaceFirst("%s", scale + "").replaceFirst("%s", "4")).append("\n");
            description.append("\n");
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && !hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS))) {
            if (item.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                List<PotionEffect> effects = new ArrayList<>();
                List<PotionEffect> base = PotionUtils.getBasePotionEffect(item);
                if (base != null) {
                    effects.addAll(base);
                }
                effects.addAll(meta.getCustomEffects());

                if (effects.isEmpty()) {
                    description.append("**").append(LanguageUtils.getTranslation(TranslationKeyUtils.getNoEffect(), language)).append("**\n");
                } else {
                    for (PotionEffect effect : effects) {
                        String key = TranslationKeyUtils.getEffect(effect.getType());
                        String translation = LanguageUtils.getTranslation(key, language);
                        if (key.equals(translation)) {
                            description.append("**").append(WordUtils.capitalize(effect.getType().getName().toLowerCase().replace("_", " ")));
                        } else {
                            description.append("**").append(translation);
                        }
                        int amplifier = effect.getAmplifier();
                        String effectLevelTranslation = LanguageUtils.getTranslation(TranslationKeyUtils.getEffectLevel(amplifier), language);
                        if (effectLevelTranslation.length() > 0) {
                            description.append(" ").append(effectLevelTranslation);
                        }
                        if (!effect.getType().isInstant()) {
                            if (xMaterial.equals(XMaterial.LINGERING_POTION)) {
                                description.append(" (").append(TimeUtils.getReadableTimeBetween(0, effect.getDuration() / 4 * 50L, ":", ChronoUnit.MINUTES, ChronoUnit.SECONDS, true)).append(")");
                            } else {
                                description.append(" (").append(TimeUtils.getReadableTimeBetween(0, effect.getDuration() * 50L, ":", ChronoUnit.MINUTES, ChronoUnit.SECONDS, true)).append(")");
                            }
                        }
                        description.append("**\n");
                    }
                }

                if (!description.toString().equals("")) {
                    description.append("\n");
                }
            }
        }

        if (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))) {
            if (hasMeta && item.getItemMeta() instanceof EnchantmentStorageMeta) {
                for (Entry<Enchantment, Integer> entry : CustomMapUtils.sortMapByValue(((EnchantmentStorageMeta) item.getItemMeta()).getStoredEnchants()).entrySet()) {
                    Enchantment ench = entry.getKey();
                    int level = entry.getValue();
                    String key = TranslationKeyUtils.getEnchantment(ench);
                    String translation = LanguageUtils.getTranslation(key, language);
                    String enchName;
                    if (key.equals(translation)) {
                        enchName = WordUtils.capitalize(ench.getName().toLowerCase().replace("_", " "));
                    } else {
                        enchName = translation;
                    }
                    if (enchName != null) {
                        description.append("**").append(enchName).append(ench.getMaxLevel() == 1 && level == 1 ? "" : " " + LanguageUtils.getTranslation(TranslationKeyUtils.getEnchantmentLevel(level), language)).append("**\n");
                    }
                }
            } else {
                for (Entry<Enchantment, Integer> entry : CustomMapUtils.sortMapByValue(item.getEnchantments()).entrySet()) {
                    Enchantment ench = entry.getKey();
                    int level = entry.getValue();
                    String key = TranslationKeyUtils.getEnchantment(ench);
                    String translation = LanguageUtils.getTranslation(key, language);
                    String enchName;
                    if (key.equals(translation)) {
                        enchName = WordUtils.capitalize(ench.getName().toLowerCase().replace("_", " "));
                    } else {
                        enchName = translation;
                    }
                    if (enchName != null) {
                        description.append("**").append(enchName).append(ench.getMaxLevel() == 1 && level == 1 ? "" : " " + LanguageUtils.getTranslation(TranslationKeyUtils.getEnchantmentLevel(level), language)).append("**\n");
                    }
                }
            }
        }

        if (hasMeta && item.getItemMeta() instanceof LeatherArmorMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DYE)) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            if (NBTEditor.contains(item, "display", "color")) {
                if (!description.toString().equals("")) {
                    description.append("\n");
                }
                Color color = new Color(meta.getColor().asRGB());
                String hex = ColorUtils.rgb2Hex(color).toUpperCase();
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getDyeColor(), language).replaceFirst("%s", hex)).append("\n");
            }
        }

        if (hasMeta) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                if (!description.toString().equals("")) {
                    description.append("\n");
                }
                String lore = String.join("\n", meta.getLore());
                if (DiscordSRV.config().getBoolean("Experiment_MCDiscordReserializer_ToDiscord")) {
                    if (InteractiveChatDiscordSrvAddon.plugin.escapeDiscordMarkdownInItems) {
                        lore = lore.replaceAll(DiscordDataRegistry.getMarkdownSpecialPattern(), "\\\\$1");
                    }
                    lore = DiscordSerializer.INSTANCE.serialize(ComponentStringUtils.toDiscordSRVComponent(LEGACY_SERIALIZER.deserialize(lore)));
                } else {
                    lore = ComponentStringUtils.stripColorAndConvertMagic(String.join("\n", meta.getLore()));
                    if (InteractiveChatDiscordSrvAddon.plugin.escapeDiscordMarkdownInItems) {
                        lore = lore.replaceAll(DiscordDataRegistry.getMarkdownSpecialPattern(), "\\\\$1");
                    }
                }
                description.append(lore).append("\n");
            }
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && NBTEditor.contains(item, "AttributeModifiers") && NBTEditor.getSize(item, "AttributeModifiers") > 0 && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
            boolean useMainHand = false;
            List<String> mainHand = new LinkedList<>();
            boolean useOffhand = false;
            List<String> offHand = new LinkedList<>();
            boolean useFeet = false;
            List<String> feet = new LinkedList<>();
            boolean useLegs = false;
            List<String> legs = new LinkedList<>();
            boolean useChest = false;
            List<String> chest = new LinkedList<>();
            boolean useHead = false;
            List<String> head = new LinkedList<>();
            ListTag<CompoundTag> attributeList = (ListTag<CompoundTag>) new SNBTDeserializer().fromString(NBTEditor.getNBTCompound(item, "tag", "AttributeModifiers").toJson());
            for (CompoundTag attributeTag : attributeList) {
                String attributeName = attributeTag.getString("AttributeName").replace("minecraft:", "");
                double amount = attributeTag.getDouble("Amount");
                int operation = attributeTag.containsKey("Operation") ? attributeTag.getInt("Operation") : 0;
                String attributeComponent = LanguageUtils.getTranslation(TranslationKeyUtils.getAttributeModifierKey(amount, operation), language).replaceFirst("%s", ATTRIBUTE_FORMAT.format(Math.abs(amount)) + "").replaceFirst("%s", LanguageUtils.getTranslation(TranslationKeyUtils.getAttributeKey(attributeName), language)).replace("%%", "%");
                if (attributeTag.containsKey("Slot")) {
                    String slot = attributeTag.getString("Slot");
                    switch (slot) {
                        case "mainhand":
                            if (amount != 0) {
                                mainHand.add(attributeComponent);
                            }
                            useMainHand = true;
                            break;
                        case "offhand":
                            if (amount != 0) {
                                offHand.add(attributeComponent);
                            }
                            useOffhand = true;
                            break;
                        case "feet":
                            if (amount != 0) {
                                feet.add(attributeComponent);
                            }
                            useFeet = true;
                            break;
                        case "legs":
                            if (amount != 0) {
                                legs.add(attributeComponent);
                            }
                            useLegs = true;
                            break;
                        case "chest":
                            if (amount != 0) {
                                chest.add(attributeComponent);
                            }
                            useChest = true;
                            break;
                        case "head":
                            if (amount != 0) {
                                head.add(attributeComponent);
                            }
                            useHead = true;
                            break;
                    }
                } else {
                    if (amount != 0) {
                        mainHand.add(attributeComponent);
                        offHand.add(attributeComponent);
                        feet.add(attributeComponent);
                        legs.add(attributeComponent);
                        chest.add(attributeComponent);
                        head.add(attributeComponent);
                    }
                    useMainHand = true;
                    useOffhand = true;
                    useFeet = true;
                    useLegs = true;
                    useChest = true;
                    useHead = true;
                }
            }
            if (useMainHand) {
                description.append("\n");
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.HAND), language)).append("\n");
                for (String each : mainHand) {
                    description.append(each).append("\n");
                }
            }
            if (useOffhand) {
                description.append("\n");
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.OFF_HAND), language)).append("\n");
                for (String each : offHand) {
                    description.append(each).append("\n");
                }
            }
            if (useFeet) {
                description.append("\n");
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.FEET), language)).append("\n");
                for (String each : feet) {
                    description.append(each).append("\n");
                }
            }
            if (useLegs) {
                description.append("\n");
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.LEGS), language)).append("\n");
                for (String each : legs) {
                    description.append(each).append("\n");
                }
            }
            if (useChest) {
                description.append("\n");
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.CHEST), language)).append("\n");
                for (String each : chest) {
                    description.append(each).append("\n");
                }
            }
            if (useHead) {
                description.append("\n");
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.HEAD), language)).append("\n");
                for (String each : head) {
                    description.append(each).append("\n");
                }
            }
        }

        if (hasMeta && isUnbreakable(item) && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
            if (!description.toString().equals("")) {
                description.append("\n");
            }
            description.append("**").append(LanguageUtils.getTranslation(TranslationKeyUtils.getUnbreakable(), language)).append("**\n");
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DESTROYS)) {
            if (NBTEditor.contains(item, "CanDestroy") && NBTEditor.getSize(item, "CanDestroy") > 0) {
                description.append("\n");
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getCanDestroy(), language)).append("\n");
                ListTag<StringTag> materialList = (ListTag<StringTag>) new SNBTDeserializer().fromString(NBTEditor.getNBTCompound(item, "tag", "CanDestroy").toJson());
                for (StringTag materialTag : materialList) {
                    XMaterial parsedXMaterial = XMaterialUtils.matchXMaterial(materialTag.getValue().replace("minecraft:", "").toUpperCase());
                    if (parsedXMaterial == null) {
                        description.append(WordUtils.capitalizeFully(materialTag.getValue().replace("_", " ").toLowerCase())).append("\n");
                    } else {
                        description.append(LanguageUtils.getTranslation(LanguageUtils.getTranslationKey(parsedXMaterial.parseItem()), language)).append("\n");
                    }
                }
            }
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_PLACED_ON)) {
            if (NBTEditor.contains(item, "CanPlaceOn") && NBTEditor.getSize(item, "CanPlaceOn") > 0) {
                description.append("\n");
                description.append(LanguageUtils.getTranslation(TranslationKeyUtils.getCanPlace(), language)).append("\n");
                ListTag<StringTag> materialList = (ListTag<StringTag>) new SNBTDeserializer().fromString(NBTEditor.getNBTCompound(item, "tag", "CanPlaceOn").toJson());
                for (StringTag materialTag : materialList) {
                    XMaterial parsedXMaterial = XMaterialUtils.matchXMaterial(materialTag.getValue().replace("minecraft:", "").toUpperCase());
                    if (parsedXMaterial == null) {
                        description.append(WordUtils.capitalizeFully(materialTag.getValue().replace("_", " ").toLowerCase())).append("\n");
                    } else {
                        description.append(LanguageUtils.getTranslation(LanguageUtils.getTranslationKey(parsedXMaterial.parseItem()), language)).append("\n");
                    }
                }
            }
        }

        if (item.getType().getMaxDurability() > 0) {
            int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
            int maxDur = item.getType().getMaxDurability();
            if (durability < maxDur) {
                if (!description.toString().equals("")) {
                    description.append("\n");
                }
                description.append("**").append(LanguageUtils.getTranslation(TranslationKeyUtils.getDurability(), language).replaceFirst("%s", String.valueOf(durability)).replaceFirst("%s", String.valueOf(maxDur))).append("**\n");
            }
        }

        return new DiscordDescription(name, description.toString().trim().isEmpty() ? null : description.toString());
    }

    public static DiscordToolTip getToolTip(ItemStack item, OfflineICPlayer player) throws Exception {
        String language = InteractiveChatDiscordSrvAddon.plugin.language;

        if (!item.getType().equals(Material.AIR) && InteractiveChat.ecoHook) {
            Player bukkitPlayer = player.getPlayer() == null || !player.getPlayer().isLocal() ? null : player.getPlayer().getLocalPlayer();
            if (bukkitPlayer == null && !Bukkit.getOnlinePlayers().isEmpty()) {
                bukkitPlayer = Bukkit.getOnlinePlayers().iterator().next();
            }
            item = EcoHook.setEcoLores(item, bukkitPlayer);
        }

        List<ToolTipComponent<?>> prints = new ArrayList<>();
        boolean hasCustomName = true;

        if (item == null) {
            item = new ItemStack(Material.AIR);
        }
        XMaterial xMaterial = XMaterialUtils.matchXMaterial(item);

        Component itemDisplayNameComponent = ItemStackUtils.getDisplayName(item);
        prints.add(ToolTipComponent.text(itemDisplayNameComponent));

        boolean hasMeta = item.hasItemMeta();

        if (xMaterial.equals(XMaterial.BUNDLE) && hasMeta && item.getItemMeta() instanceof BundleMeta) {
            BundleMeta meta = (BundleMeta) item.getItemMeta();
            List<ItemStack> items = meta.getItems();
            BufferedImage contentsImage = ImageGeneration.getBundleContainerInterface(player, items);
            prints.add(ToolTipComponent.image(contentsImage));
            int fullness = BundleUtils.getFullness(items);
            prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getBundleFullness(), language).replaceFirst("%s", fullness + "").replaceFirst("%s", "64"))));
        }

        if (xMaterial.equals(XMaterial.WRITTEN_BOOK) && hasMeta && item.getItemMeta() instanceof BookMeta) {
            BookMeta meta = (BookMeta) item.getItemMeta();
            String author = meta.getAuthor();
            if (author != null) {
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getBookAuthor(), language).replaceFirst("%1\\$s", author))));
            }
            Generation generation = meta.getGeneration();
            if (generation == null) {
                generation = Generation.ORIGINAL;
            }
            prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getBookGeneration(generation), language))));
        }

        if (xMaterial.equals(XMaterial.SHIELD) && (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)))) {
            if (NBTEditor.contains(item, "BlockEntityTag")) {
                List<Pattern> patterns = Collections.emptyList();
                if (!(item.getItemMeta() instanceof BannerMeta)) {
                    if (item.getItemMeta() instanceof BlockStateMeta) {
                        BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
                        if (bmeta.hasBlockState()) {
                            Banner bannerBlockMeta = (Banner) bmeta.getBlockState();
                            patterns = bannerBlockMeta.getPatterns();
                        }
                    }
                } else {
                    BannerMeta meta = (BannerMeta) item.getItemMeta();
                    patterns = meta.getPatterns();
                }

                for (Pattern pattern : patterns) {
                    PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
                    prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getBannerPatternName(type, pattern.getColor()), language))));
                }
            }
        }

        if (xMaterial.isOneOf(Collections.singletonList("CONTAINS:Banner")) && (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)))) {
            List<Pattern> patterns = Collections.emptyList();
            if (!(item.getItemMeta() instanceof BannerMeta)) {
                if (item.getItemMeta() instanceof BlockStateMeta) {
                    BlockStateMeta bmeta = (BlockStateMeta) item.getItemMeta();
                    Banner bannerBlockMeta = (Banner) bmeta.getBlockState();
                    patterns = bannerBlockMeta.getPatterns();
                }
            } else {
                BannerMeta meta = (BannerMeta) item.getItemMeta();
                patterns = meta.getPatterns();
            }

            for (Pattern pattern : patterns) {
                PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getBannerPatternName(type, pattern.getColor()), language))));
            }
        }

        if (xMaterial.equals(XMaterial.TROPICAL_FISH_BUCKET)) {
            List<String> translations = TranslationKeyUtils.getTropicalFishBucketName(item);
            if (translations.size() > 0) {
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + "" + ChatColor.ITALIC + LanguageUtils.getTranslation(translations.get(0), language))));
                if (translations.size() > 1) {
                    prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + "" + ChatColor.ITALIC + translations.stream().skip(1).map(each -> LanguageUtils.getTranslation(each, language)).collect(Collectors.joining(", ")))));
                }
            }
        }

        if (xMaterial.isOneOf(Collections.singletonList("CONTAINS:Music_Disc"))) {
            prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getMusicDiscName(item), language))));
        }

        if (xMaterial.equals(XMaterial.FIREWORK_ROCKET)) {
            if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && NBTEditor.contains(item, "Fireworks", "Flight")) {
                int flight = NBTEditor.getByte(item, "Fireworks", "Flight");
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getRocketFlightDuration(), language) + " " + flight)));
            }
            if (hasMeta && item.getItemMeta() instanceof FireworkMeta) {
                FireworkMeta fireworkMeta = (FireworkMeta) item.getItemMeta();
                for (FireworkEffect fireworkEffect : fireworkMeta.getEffects()) {
                    prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkType(fireworkEffect.getType()), language))));
                    if (!fireworkEffect.getColors().isEmpty()) {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + "  " + fireworkEffect.getColors().stream().map(each -> {
                            return LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkColor(each), language);
                        }).collect(Collectors.joining(", ")))));
                    }
                    if (!fireworkEffect.getFadeColors().isEmpty()) {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + "  " + LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkFade(), language) + " " + fireworkEffect.getFadeColors().stream().map(each -> {
                            return LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkColor(each), language);
                        }).collect(Collectors.joining(", ")))));
                    }
                    if (fireworkEffect.hasTrail()) {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + "  " + LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkTrail(), language))));
                    }
                    if (fireworkEffect.hasFlicker()) {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + "  " + LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkFlicker(), language))));
                    }
                }
            }
        }

        if (xMaterial.equals(XMaterial.FIREWORK_STAR) && hasMeta && item.getItemMeta() instanceof FireworkEffectMeta) {
            FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) item.getItemMeta();
            FireworkEffect fireworkEffect = fireworkEffectMeta.getEffect();
            prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkType(fireworkEffect.getType()), language))));
            if (!fireworkEffect.getColors().isEmpty()) {
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + "  " + fireworkEffect.getColors().stream().map(each -> {
                    return LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkColor(each), language);
                }).collect(Collectors.joining(", ")))));
            }
            if (!fireworkEffect.getFadeColors().isEmpty()) {
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + "  " + LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkFade(), language) + " " + fireworkEffect.getFadeColors().stream().map(each -> {
                    return LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkColor(each), language);
                }).collect(Collectors.joining(", ")))));
            }
            if (fireworkEffect.hasTrail()) {
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + "  " + LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkTrail(), language))));
            }
            if (fireworkEffect.hasFlicker()) {
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + "  " + LanguageUtils.getTranslation(TranslationKeyUtils.getFireworkFlicker(), language))));
            }
        }

        if (xMaterial.equals(XMaterial.CROSSBOW)) {
            CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
            List<ItemStack> charged = meta.getChargedProjectiles();
            if (charged != null && !charged.isEmpty()) {
                ItemStack charge = charged.get(0);
                List<ToolTipComponent<?>> chargedItemInfo = getToolTip(charge, player).getComponents();
                Component chargeItemName = (Component) chargedItemInfo.get(0).getToolTipComponent();
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.WHITE + LanguageUtils.getTranslation(TranslationKeyUtils.getCrossbowProjectile(), language) + " [" + InteractiveChatComponentSerializer.bungeecordApiLegacy().serialize(chargeItemName) + ChatColor.WHITE + "]")));
                if (XMaterialUtils.matchXMaterial(charge).equals(XMaterial.FIREWORK_ROCKET)) {
                    chargedItemInfo.stream().skip(1).forEachOrdered(each -> {
                        if (each.getType().equals(ToolTipType.TEXT)) {
                            prints.add(ToolTipComponent.text(Component.text("  ").append((Component)each.getToolTipComponent())));
                        } else {
                            prints.add(each);
                        }
                    });
                }
            }
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && FilledMapUtils.isFilledMap(item)) {
            MapMeta map = (MapMeta) item.getItemMeta();
            MapView mapView = FilledMapUtils.getMapView(item);
            int id = FilledMapUtils.getMapId(item);
            int scale = mapView == null ? 0 : mapView.getScale().getValue();
            if (!InteractiveChat.version.isLegacy()) {
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getFilledMapId(), language).replaceFirst("%s", id + ""))));
            } else {
                prints.set(0, ToolTipComponent.text(((Component) prints.get(0).getToolTipComponent()).children(Arrays.asList(LegacyComponentSerializer.legacySection().deserialize(ChatColor.WHITE + " (#" + id + ")")))));
            }
            prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getFilledMapScale(), language).replaceFirst("%s", (int) Math.pow(2, scale) + ""))));
            prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getFilledMapLevel(), language).replaceFirst("%s", scale + "").replaceFirst("%s", "4"))));
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && !hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS))) {
            if (item.getItemMeta() instanceof PotionMeta) {
                PotionMeta meta = (PotionMeta) item.getItemMeta();
                List<PotionEffect> effects = new ArrayList<>();
                List<PotionEffect> base = PotionUtils.getBasePotionEffect(item);
                if (base != null) {
                    effects.addAll(base);
                }
                effects.addAll(meta.getCustomEffects());

                if (effects.isEmpty()) {
                    prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(LanguageUtils.getTranslation(TranslationKeyUtils.getNoEffect(), language))));
                } else {
                    for (PotionEffect effect : effects) {
                        String key = TranslationKeyUtils.getEffect(effect.getType());
                        String translation = LanguageUtils.getTranslation(key, language);
                        String description = "";
                        if (key.equals(translation)) {
                            description += WordUtils.capitalize(effect.getType().getName().toLowerCase().replace("_", " "));
                        } else {
                            description += translation;
                        }
                        int amplifier = effect.getAmplifier();
                        String effectLevelTranslation = LanguageUtils.getTranslation(TranslationKeyUtils.getEffectLevel(amplifier), language);
                        if (effectLevelTranslation.length() > 0) {
                            description += " " + effectLevelTranslation;
                        }
                        if (!effect.getType().isInstant()) {
                            if (xMaterial.equals(XMaterial.LINGERING_POTION)) {
                                description += " (" + TimeUtils.getReadableTimeBetween(0, effect.getDuration() / 4 * 50L, ":", ChronoUnit.MINUTES, ChronoUnit.SECONDS, true) + ")";
                            } else {
                                description += " (" + TimeUtils.getReadableTimeBetween(0, effect.getDuration() * 50L, ":", ChronoUnit.MINUTES, ChronoUnit.SECONDS, true) + ")";
                            }
                        }
                        ChatColor color;
                        try {
                            color = PotionUtils.getPotionEffectChatColor(effect.getType());
                        } catch (Throwable e) {
                            color = ChatColor.BLUE;
                        }
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(color + description)));
                    }
                }
            }
        }

        if (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))) {
            if (hasMeta && item.getItemMeta() instanceof EnchantmentStorageMeta) {
                for (Entry<Enchantment, Integer> entry : CustomMapUtils.sortMapByValue(((EnchantmentStorageMeta) item.getItemMeta()).getStoredEnchants()).entrySet()) {
                    Enchantment ench = entry.getKey();
                    int level = entry.getValue();
                    String key = TranslationKeyUtils.getEnchantment(ench);
                    String translation = LanguageUtils.getTranslation(key, language);
                    String enchName;
                    if (key.equals(translation)) {
                        enchName = WordUtils.capitalize(ench.getName().toLowerCase().replace("_", " "));
                    } else {
                        enchName = translation;
                    }
                    if (enchName != null) {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + enchName + (ench.getMaxLevel() == 1 && level == 1 ? "" : " " + LanguageUtils.getTranslation(TranslationKeyUtils.getEnchantmentLevel(level), language)))));
                    }
                }
            } else {
                for (Entry<Enchantment, Integer> entry : CustomMapUtils.sortMapByValue(item.getEnchantments()).entrySet()) {
                    Enchantment ench = entry.getKey();
                    int level = entry.getValue();
                    String key = TranslationKeyUtils.getEnchantment(ench);
                    String translation = LanguageUtils.getTranslation(key, language);
                    String enchName;
                    if (key.equals(translation)) {
                        enchName = WordUtils.capitalize(ench.getName().toLowerCase().replace("_", " "));
                    } else {
                        enchName = translation;
                    }
                    if (enchName != null) {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + enchName + (ench.getMaxLevel() == 1 && level == 1 ? "" : " " + LanguageUtils.getTranslation(TranslationKeyUtils.getEnchantmentLevel(level), language)))));
                    }
                }
            }
        }

        if (hasMeta && item.getItemMeta() instanceof LeatherArmorMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DYE)) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            if (NBTEditor.contains(item, "display", "color")) {
                Color color = new Color(meta.getColor().asRGB());
                String hex = ColorUtils.rgb2Hex(color).toUpperCase();
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getDyeColor(), language).replaceFirst("%s", hex))));
            }
        }

        if (hasMeta) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                for (String lore : meta.getLore()) {
                    prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.DARK_PURPLE + "" + ChatColor.ITALIC + lore)));
                }
            }
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && NBTEditor.contains(item, "AttributeModifiers") && NBTEditor.getSize(item, "AttributeModifiers") > 0 && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
            boolean useMainHand = false;
            List<ToolTipComponent<?>> mainHand = new LinkedList<>();
            boolean useOffhand = false;
            List<ToolTipComponent<?>> offHand = new LinkedList<>();
            boolean useFeet = false;
            List<ToolTipComponent<?>> feet = new LinkedList<>();
            boolean useLegs = false;
            List<ToolTipComponent<?>> legs = new LinkedList<>();
            boolean useChest = false;
            List<ToolTipComponent<?>> chest = new LinkedList<>();
            boolean useHead = false;
            List<ToolTipComponent<?>> head = new LinkedList<>();
            ListTag<CompoundTag> attributeList = (ListTag<CompoundTag>) new SNBTDeserializer().fromString(NBTEditor.getNBTCompound(item, "tag", "AttributeModifiers").toJson());
            for (CompoundTag attributeTag : attributeList) {
                String attributeName = attributeTag.getString("AttributeName").replace("minecraft:", "");
                double amount = attributeTag.getDouble("Amount");
                int operation = attributeTag.containsKey("Operation") ? attributeTag.getInt("Operation") : 0;
                ToolTipComponent<?> attributeComponent = ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize((amount < 0 ? ChatColor.RED : ChatColor.BLUE) + LanguageUtils.getTranslation(TranslationKeyUtils.getAttributeModifierKey(amount, operation), language).replaceFirst("%s", ATTRIBUTE_FORMAT.format(Math.abs(amount)) + "").replaceFirst("%s", LanguageUtils.getTranslation(TranslationKeyUtils.getAttributeKey(attributeName), language)).replace("%%", "%")));
                if (attributeTag.containsKey("Slot")) {
                    String slot = attributeTag.getString("Slot");
                    switch (slot) {
                        case "mainhand":
                            if (amount != 0) {
                                mainHand.add(attributeComponent);
                            }
                            useMainHand = true;
                            break;
                        case "offhand":
                            if (amount != 0) {
                                offHand.add(attributeComponent);
                            }
                            useOffhand = true;
                            break;
                        case "feet":
                            if (amount != 0) {
                                feet.add(attributeComponent);
                            }
                            useFeet = true;
                            break;
                        case "legs":
                            if (amount != 0) {
                                legs.add(attributeComponent);
                            }
                            useLegs = true;
                            break;
                        case "chest":
                            if (amount != 0) {
                                chest.add(attributeComponent);
                            }
                            useChest = true;
                            break;
                        case "head":
                            if (amount != 0) {
                                head.add(attributeComponent);
                            }
                            useHead = true;
                            break;
                    }
                } else {
                    if (amount != 0) {
                        mainHand.add(attributeComponent);
                        offHand.add(attributeComponent);
                        feet.add(attributeComponent);
                        legs.add(attributeComponent);
                        chest.add(attributeComponent);
                        head.add(attributeComponent);
                    }
                    useMainHand = true;
                    useOffhand = true;
                    useFeet = true;
                    useLegs = true;
                    useChest = true;
                    useHead = true;
                }
            }
            if (useMainHand) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.HAND), language))));
                prints.addAll(mainHand);
            }
            if (useOffhand) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.OFF_HAND), language))));
                prints.addAll(offHand);
            }
            if (useFeet) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.FEET), language))));
                prints.addAll(feet);
            }
            if (useLegs) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.LEGS), language))));
                prints.addAll(legs);
            }
            if (useChest) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.CHEST), language))));
                prints.addAll(chest);
            }
            if (useHead) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.HEAD), language))));
                prints.addAll(head);
            }
        }

        if (hasMeta && isUnbreakable(item) && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
            prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.BLUE + LanguageUtils.getTranslation(TranslationKeyUtils.getUnbreakable(), language))));
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DESTROYS)) {
            if (NBTEditor.contains(item, "CanDestroy") && NBTEditor.getSize(item, "CanDestroy") > 0) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getCanDestroy(), language))));
                ListTag<StringTag> materialList = (ListTag<StringTag>) new SNBTDeserializer().fromString(NBTEditor.getNBTCompound(item, "tag", "CanDestroy").toJson());
                for (StringTag materialTag : materialList) {
                    XMaterial parsedXMaterial = XMaterialUtils.matchXMaterial(materialTag.getValue().replace("minecraft:", "").toUpperCase());
                    if (parsedXMaterial == null) {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.DARK_GRAY + WordUtils.capitalizeFully(materialTag.getValue().replace("_", " ").toLowerCase()))));
                    } else {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.DARK_GRAY + LanguageUtils.getTranslation(LanguageUtils.getTranslationKey(parsedXMaterial.parseItem()), language))));
                    }
                }
            }
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_PLACED_ON)) {
            if (NBTEditor.contains(item, "CanPlaceOn") && NBTEditor.getSize(item, "CanPlaceOn") > 0) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + LanguageUtils.getTranslation(TranslationKeyUtils.getCanPlace(), language))));
                ListTag<StringTag> materialList = (ListTag<StringTag>) new SNBTDeserializer().fromString(NBTEditor.getNBTCompound(item, "tag", "CanPlaceOn").toJson());
                for (StringTag materialTag : materialList) {
                    XMaterial parsedXMaterial = XMaterialUtils.matchXMaterial(materialTag.getValue().replace("minecraft:", "").toUpperCase());
                    if (parsedXMaterial == null) {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.DARK_GRAY + WordUtils.capitalizeFully(materialTag.getValue().replace("_", " ").toLowerCase()))));
                    } else {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.DARK_GRAY + LanguageUtils.getTranslation(LanguageUtils.getTranslationKey(parsedXMaterial.parseItem()), language))));
                    }
                }
            }
        }

        if (item.getType().getMaxDurability() > 0) {
            int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
            int maxDur = item.getType().getMaxDurability();
            if (durability < maxDur) {
                prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.WHITE + LanguageUtils.getTranslation(TranslationKeyUtils.getDurability(), language).replaceFirst("%s", String.valueOf(durability)).replaceFirst("%s", String.valueOf(maxDur)))));
            }
        }

        return new DiscordToolTip(prints, !hasCustomName && prints.size() <= 1);
    }

    public static boolean isUnbreakable(ItemStack item) {
        if (itemMetaHasUnbreakable) {
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null) {
                    return meta.isUnbreakable();
                }
            }
            return false;
        } else {
            return NBTEditor.getByte(item, "Unbreakable") > 0;
        }
    }

    public static class DiscordDescription {

        private String name;
        private Optional<String> description;

        public DiscordDescription(String name, String description) {
            this.name = name.trim().isEmpty() ? DISCORD_EMPTY : name;
            this.description = Optional.ofNullable(description);
        }

        public String getName() {
            return name;
        }

        public Optional<String> getDescription() {
            return description;
        }

    }

    public static class DiscordToolTip {

        private List<ToolTipComponent<?>> components;
        private boolean isBaseItem;

        public DiscordToolTip(List<ToolTipComponent<?>> components, boolean isBaseItem) {
            this.components = components;
            this.isBaseItem = isBaseItem;
        }

        public List<ToolTipComponent<?>> getComponents() {
            return components;
        }

        public boolean isBaseItem() {
            return isBaseItem;
        }

    }

}
