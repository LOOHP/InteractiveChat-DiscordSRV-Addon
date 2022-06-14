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
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.Style;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NBTParsingUtils;
import com.loohp.interactivechat.utils.RarityUtils;
import com.loohp.interactivechat.utils.XMaterialUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ToolTipComponent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ToolTipComponent.ToolTipType;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.PatternTypeWrapper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static com.loohp.interactivechat.utils.LanguageUtils.getTranslation;
import static com.loohp.interactivechat.utils.LanguageUtils.getTranslationKey;

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
            chatColorHasGetColor = Arrays.stream(ChatColor.class.getMethods()).anyMatch(each -> each.getName().equalsIgnoreCase("getColor") && each.getReturnType().equals(Color.class));
            itemMetaHasUnbreakable = Arrays.stream(ItemMeta.class.getMethods()).anyMatch(each -> each.getName().equalsIgnoreCase("isUnbreakable") && each.getReturnType().equals(boolean.class));
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

    public static String getItemNameForDiscord(ItemStack item, OfflineICPlayer player) {
        String language = InteractiveChatDiscordSrvAddon.plugin.language;
        UnaryOperator<String> translationFunction = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getLanguageManager().getTranslateFunction().ofLanguage(language);

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
        String name = InteractiveChatComponentSerializer.bungeecordApiLegacy().serialize(ItemStackUtils.getDisplayName(item), language);
        if (item.getAmount() == 1 || item == null || item.getType().equals(Material.AIR)) {
            name = InteractiveChatDiscordSrvAddon.plugin.itemDisplaySingle.replace("{Item}", ComponentStringUtils.stripColorAndConvertMagic(name)).replace("{Amount}", String.valueOf(item.getAmount()));
        } else {
            name = InteractiveChatDiscordSrvAddon.plugin.itemDisplayMultiple.replace("{Item}", ComponentStringUtils.stripColorAndConvertMagic(name)).replace("{Amount}", String.valueOf(item.getAmount()));
        }

        return name;
    }

    public static DiscordToolTip getToolTip(ItemStack item, OfflineICPlayer player) throws Exception {
        String language = InteractiveChatDiscordSrvAddon.plugin.language;
        UnaryOperator<String> translationFunction = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getLanguageManager().getTranslateFunction().ofLanguage(language);

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

        if (xMaterial.equals(XMaterial.GOAT_HORN)) {
            String instrument = NBTEditor.getString(item, "instrument");
            if (instrument != null) {
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getGoatHornInstrument(NamespacedKey.fromString(instrument))).color(NamedTextColor.GRAY)));
            }
        }

        if (xMaterial.equals(XMaterial.BUNDLE) && hasMeta && item.getItemMeta() instanceof BundleMeta) {
            BundleMeta meta = (BundleMeta) item.getItemMeta();
            List<ItemStack> items = meta.getItems();
            BufferedImage contentsImage = ImageGeneration.getBundleContainerInterface(player, items);
            prints.add(ToolTipComponent.image(contentsImage));
            int fullness = BundleUtils.getFullness(items);
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getBundleFullness()).args(Component.text(fullness), Component.text(64)).color(NamedTextColor.GRAY)));
        }

        if (xMaterial.equals(XMaterial.WRITTEN_BOOK) && hasMeta && item.getItemMeta() instanceof BookMeta) {
            BookMeta meta = (BookMeta) item.getItemMeta();
            String author = meta.getAuthor();
            if (author != null) {
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getBookAuthor()).args(Component.text(author)).color(NamedTextColor.GRAY)));
            }
            Generation generation = meta.getGeneration();
            if (generation == null) {
                generation = Generation.ORIGINAL;
            }
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getBookGeneration(generation)).color(NamedTextColor.GRAY)));
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
                    prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getBannerPatternName(type, pattern.getColor())).color(NamedTextColor.GRAY)));
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
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getBannerPatternName(type, pattern.getColor())).color(NamedTextColor.GRAY)));
            }
        }

        if (xMaterial.equals(XMaterial.TROPICAL_FISH_BUCKET)) {
            List<String> translations = TranslationKeyUtils.getTropicalFishBucketName(item);
            if (translations.size() > 0) {
                prints.add(ToolTipComponent.text(Component.translatable(translations.get(0)).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)));
                if (translations.size() > 1) {
                    prints.add(ToolTipComponent.text(ComponentStringUtils.join(Component.empty().color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC), Component.text(", "), translations.stream().skip(1).map(each -> Component.translatable(each)).collect(Collectors.toList()))));
                }
            }
        }

        if (xMaterial.isOneOf(Collections.singletonList("CONTAINS:music_disc"))) {
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getMusicDiscName(item)).color(NamedTextColor.GRAY)));
        }

        if (xMaterial.isOneOf(Collections.singletonList("CONTAINS:disc_fragment"))) {
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getDiscFragmentName(item)).color(NamedTextColor.GRAY)));
        }

        if (xMaterial.isOneOf(Collections.singletonList("CONTAINS:banner_pattern"))) {
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getBannerPatternItemName(xMaterial)).color(NamedTextColor.GRAY)));
        }

        if (xMaterial.equals(XMaterial.FIREWORK_ROCKET)) {
            if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && NBTEditor.contains(item, "Fireworks", "Flight")) {
                int flight = NBTEditor.getByte(item, "Fireworks", "Flight");
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getRocketFlightDuration()).append(Component.text(" " + flight)).color(NamedTextColor.GRAY)));
            }
            if (hasMeta && item.getItemMeta() instanceof FireworkMeta) {
                FireworkMeta fireworkMeta = (FireworkMeta) item.getItemMeta();
                for (FireworkEffect fireworkEffect : fireworkMeta.getEffects()) {
                    prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getFireworkType(fireworkEffect.getType())).color(NamedTextColor.GRAY)));
                    if (!fireworkEffect.getColors().isEmpty()) {
                        prints.add(ToolTipComponent.text(ComponentStringUtils.join(Component.text("  ").color(NamedTextColor.GRAY), Component.text(", "), fireworkEffect.getColors().stream().map(each -> Component.translatable(TranslationKeyUtils.getFireworkColor(each))).collect(Collectors.toList()))));
                    }
                    if (!fireworkEffect.getFadeColors().isEmpty()) {
                        prints.add(ToolTipComponent.text(ComponentStringUtils.join(Component.text("  ").append(Component.translatable(TranslationKeyUtils.getFireworkFade())).append(Component.text(" ")).color(NamedTextColor.GRAY), Component.text(", "), fireworkEffect.getFadeColors().stream().map(each -> Component.translatable(TranslationKeyUtils.getFireworkColor(each))).collect(Collectors.toList()))));
                    }
                    if (fireworkEffect.hasTrail()) {
                        prints.add(ToolTipComponent.text(Component.text("  ").append(Component.translatable(TranslationKeyUtils.getFireworkTrail())).color(NamedTextColor.GRAY)));
                    }
                    if (fireworkEffect.hasFlicker()) {
                        prints.add(ToolTipComponent.text(Component.text("  ").append(Component.translatable(TranslationKeyUtils.getFireworkFlicker())).color(NamedTextColor.GRAY)));
                    }
                }
            }
        }

        if (xMaterial.equals(XMaterial.FIREWORK_STAR) && hasMeta && item.getItemMeta() instanceof FireworkEffectMeta) {
            FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) item.getItemMeta();
            FireworkEffect fireworkEffect = fireworkEffectMeta.getEffect();
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getFireworkType(fireworkEffect.getType())).color(NamedTextColor.GRAY)));
            if (!fireworkEffect.getColors().isEmpty()) {
                prints.add(ToolTipComponent.text(ComponentStringUtils.join(Component.text("  ").color(NamedTextColor.GRAY), Component.text(", "), fireworkEffect.getColors().stream().map(each -> Component.translatable(TranslationKeyUtils.getFireworkColor(each))).collect(Collectors.toList()))));
            }
            if (!fireworkEffect.getFadeColors().isEmpty()) {
                prints.add(ToolTipComponent.text(ComponentStringUtils.join(Component.text("  ").append(Component.translatable(TranslationKeyUtils.getFireworkFade())).append(Component.text(" ")).color(NamedTextColor.GRAY), Component.text(", "), fireworkEffect.getFadeColors().stream().map(each -> Component.translatable(TranslationKeyUtils.getFireworkColor(each))).collect(Collectors.toList()))));
            }
            if (fireworkEffect.hasTrail()) {
                prints.add(ToolTipComponent.text(Component.text("  ").append(Component.translatable(TranslationKeyUtils.getFireworkTrail())).color(NamedTextColor.GRAY)));
            }
            if (fireworkEffect.hasFlicker()) {
                prints.add(ToolTipComponent.text(Component.text("  ").append(Component.translatable(TranslationKeyUtils.getFireworkFlicker())).color(NamedTextColor.GRAY)));
            }
        }

        if (xMaterial.equals(XMaterial.CROSSBOW)) {
            CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
            List<ItemStack> charged = meta.getChargedProjectiles();
            if (charged != null && !charged.isEmpty()) {
                ItemStack charge = charged.get(0);
                List<ToolTipComponent<?>> chargedItemInfo = getToolTip(charge, player).getComponents();
                Component chargeItemName = (Component) chargedItemInfo.get(0).getToolTipComponent();
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getCrossbowProjectile()).color(NamedTextColor.WHITE).append(Component.text(" [").color(NamedTextColor.WHITE)).append(chargeItemName).append(Component.text("]").color(NamedTextColor.WHITE))));
                if (XMaterialUtils.matchXMaterial(charge).equals(XMaterial.FIREWORK_ROCKET)) {
                    chargedItemInfo.stream().skip(1).forEachOrdered(each -> {
                        if (each.getType().equals(ToolTipType.TEXT)) {
                            prints.add(ToolTipComponent.text(Component.text("  ").append((Component) each.getToolTipComponent())));
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
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getFilledMapId()).args(Component.text(id)).color(NamedTextColor.GRAY)));
            } else {
                prints.set(0, ToolTipComponent.text(((Component) prints.get(0).getToolTipComponent()).append(Component.text(" (#" + id + ")").color(NamedTextColor.GRAY))));
            }
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getFilledMapScale()).args(Component.text((int) Math.pow(2, scale))).color(NamedTextColor.GRAY)));
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getFilledMapLevel()).args(Component.text(scale), Component.text(4)).color(NamedTextColor.GRAY)));
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
                    prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getNoEffect()).color(NamedTextColor.GRAY)));
                } else {
                    Map<String, AttributeModifier> attributes = new HashMap<>();
                    for (PotionEffect effect : effects) {
                        if (InteractiveChat.version.isLegacy()) {
                            String key = TranslationKeyUtils.getEffect(effect.getType());
                            String translation = getTranslation(key, language);
                            String description = "";
                            if (key.equals(translation)) {
                                description += WordUtils.capitalize(effect.getType().getName().toLowerCase().replace("_", " "));
                            } else {
                                description += translation;
                            }
                            int amplifier = effect.getAmplifier();
                            String effectLevelTranslation = getTranslation(TranslationKeyUtils.getEffectLevel(amplifier), language);
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
                        } else {
                            String key = TranslationKeyUtils.getEffect(effect.getType());
                            String potionName;
                            if (key.equals(getTranslation(key, language))) {
                                potionName = WordUtils.capitalize(effect.getType().getName().toLowerCase().replace("_", " "));
                            } else {
                                potionName = key;
                            }
                            int amplifier = effect.getAmplifier();
                            int duration;
                            if (effect.getType().isInstant()) {
                                duration = 0;
                            } else {
                                duration = effect.getDuration() * 50;
                                if (xMaterial.equals(XMaterial.LINGERING_POTION)) {
                                    duration /= 4;
                                }
                            }
                            TextColor color;
                            try {
                                color = ColorUtils.toTextColor(PotionUtils.getPotionEffectChatColor(effect.getType()));
                            } catch (Throwable e) {
                                color = NamedTextColor.BLUE;
                            }
                            Component component = Component.translatable(potionName);
                            if (amplifier > 0) {
                                component = Component.translatable(TranslationKeyUtils.getPotionWithAmplifier()).args(component, Component.translatable(TranslationKeyUtils.getEffectLevel(amplifier)));
                            }
                            if (duration > 20) {
                                String time = TimeUtils.getReadableTimeBetween(0, duration, ":", ChronoUnit.MINUTES, ChronoUnit.SECONDS, true);
                                component = Component.translatable(TranslationKeyUtils.getPotionWithDuration()).args(component, Component.text(time));
                            }
                            prints.add(ToolTipComponent.text(component.color(color)));
                        }
                        attributes.putAll(PotionUtils.getPotionAttributes(effect));
                    }
                    if (!attributes.isEmpty()) {
                        prints.add(ToolTipComponent.text(Component.empty()));
                        prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getPotionWhenDrunk()).color(NamedTextColor.DARK_PURPLE)));
                        for (Entry<String, AttributeModifier> entry : attributes.entrySet()) {
                            String attributeName = entry.getKey();
                            if (attributeName.startsWith("attribute.name.")) {
                                attributeName = attributeName.substring(15);
                            }
                            AttributeModifier attributeModifier = entry.getValue();
                            double amount = attributeModifier.getAmount();
                            int operation = attributeModifier.getOperation().ordinal();
                            if (!(operation != 1 && operation != 2)) {
                                amount *= 100;
                            }
                            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getAttributeModifierKey(amount, operation)).args(Component.text(ATTRIBUTE_FORMAT.format(Math.abs(amount))), Component.translatable(TranslationKeyUtils.getAttributeKey(attributeName))).color(amount < 0 ? NamedTextColor.RED : NamedTextColor.BLUE)));
                        }
                    }
                }
            }
        }

        if (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS))) {
            Map<Enchantment, Integer> enchantments;
            if (hasMeta && item.getItemMeta() instanceof EnchantmentStorageMeta) {
                enchantments = ((EnchantmentStorageMeta) item.getItemMeta()).getStoredEnchants();
            } else {
                enchantments = item.getEnchantments();
            }
            for (Entry<Enchantment, Integer> entry : CustomMapUtils.sortMapByValue(enchantments).entrySet()) {
                Enchantment ench = entry.getKey();
                int level = entry.getValue();
                String key = TranslationKeyUtils.getEnchantment(ench);
                String enchName;
                if (key.equals(getTranslation(key, language))) {
                    enchName = WordUtils.capitalize(ench.getName().toLowerCase().replace("_", " "));
                } else {
                    enchName = key;
                }
                if (enchName != null) {
                    if (ench.getMaxLevel() == 1 && level == 1) {
                        prints.add(ToolTipComponent.text(Component.translatable(enchName).color(NamedTextColor.GRAY)));
                    } else {
                        prints.add(ToolTipComponent.text(Component.translatable(enchName).append(Component.text(" ")).append(Component.translatable(TranslationKeyUtils.getEnchantmentLevel(level))).color(NamedTextColor.GRAY)));
                    }
                }
            }
        }

        if (hasMeta && item.getItemMeta() instanceof LeatherArmorMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DYE)) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            if (NBTEditor.contains(item, "display", "color")) {
                Color color = new Color(meta.getColor().asRGB());
                String hex = ColorUtils.rgb2Hex(color).toUpperCase();
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getDyeColor()).args(Component.text(hex)).color(NamedTextColor.GRAY)));
            }
        }

        if (hasMeta) {
            List<Component> loreLines = ItemStackUtils.getLore(item);
            if (loreLines != null) {
                for (Component lore : loreLines) {
                    Component component = lore.applyFallbackStyle(Style.style(NamedTextColor.DARK_PURPLE, TextDecoration.ITALIC));
                    prints.add(ToolTipComponent.text(component));
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
            ListTag<CompoundTag> attributeList = (ListTag<CompoundTag>) NBTParsingUtils.fromSNBT(NBTEditor.getNBTCompound(item, "tag", "AttributeModifiers").toJson());
            for (CompoundTag attributeTag : attributeList) {
                String attributeName = attributeTag.getString("AttributeName").replace("minecraft:", "");
                double amount = attributeTag.getNumber("Amount").doubleValue();
                int operation = attributeTag.containsKey("Operation") ? attributeTag.getNumber("Operation").intValue() : 0;
                if (!(operation != 1 && operation != 2)) {
                    amount *= 100;
                }
                ToolTipComponent<?> attributeComponent = ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getAttributeModifierKey(amount, operation)).args(Component.text(ATTRIBUTE_FORMAT.format(Math.abs(amount))), Component.translatable(TranslationKeyUtils.getAttributeKey(attributeName))).color(amount < 0 ? NamedTextColor.RED : NamedTextColor.BLUE));
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
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.HAND)).color(NamedTextColor.GRAY)));
                prints.addAll(mainHand);
            }
            if (useOffhand) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.OFF_HAND)).color(NamedTextColor.GRAY)));
                prints.addAll(offHand);
            }
            if (useFeet) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.FEET)).color(NamedTextColor.GRAY)));
                prints.addAll(feet);
            }
            if (useLegs) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.LEGS)).color(NamedTextColor.GRAY)));
                prints.addAll(legs);
            }
            if (useChest) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.CHEST)).color(NamedTextColor.GRAY)));
                prints.addAll(chest);
            }
            if (useHead) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getModifierSlotKey(EquipmentSlot.HEAD)).color(NamedTextColor.GRAY)));
                prints.addAll(head);
            }
        }

        if (hasMeta && isUnbreakable(item) && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)) {
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getUnbreakable()).color(NamedTextColor.BLUE)));
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_DESTROYS)) {
            if (NBTEditor.contains(item, "CanDestroy") && NBTEditor.getSize(item, "CanDestroy") > 0) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getCanDestroy()).color(NamedTextColor.GRAY)));
                ListTag<StringTag> materialList = (ListTag<StringTag>) NBTParsingUtils.fromSNBT(NBTEditor.getNBTCompound(item, "tag", "CanDestroy").toJson());
                for (StringTag materialTag : materialList) {
                    XMaterial parsedXMaterial = XMaterialUtils.matchXMaterial(materialTag.getValue().replace("minecraft:", "").toUpperCase());
                    if (parsedXMaterial == null) {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(WordUtils.capitalizeFully(materialTag.getValue().replace("_", " ").toLowerCase())).color(NamedTextColor.DARK_GRAY)));
                    } else {
                        prints.add(ToolTipComponent.text(Component.translatable(getTranslationKey(parsedXMaterial.parseItem())).color(NamedTextColor.DARK_GRAY)));
                    }
                }
            }
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_PLACED_ON)) {
            if (NBTEditor.contains(item, "CanPlaceOn") && NBTEditor.getSize(item, "CanPlaceOn") > 0) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getCanPlace()).color(NamedTextColor.GRAY)));
                ListTag<StringTag> materialList = (ListTag<StringTag>) NBTParsingUtils.fromSNBT(NBTEditor.getNBTCompound(item, "tag", "CanPlaceOn").toJson());
                for (StringTag materialTag : materialList) {
                    XMaterial parsedXMaterial = XMaterialUtils.matchXMaterial(materialTag.getValue().replace("minecraft:", "").toUpperCase());
                    if (parsedXMaterial == null) {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.DARK_GRAY + WordUtils.capitalizeFully(materialTag.getValue().replace("_", " ").toLowerCase()))));
                    } else {
                        prints.add(ToolTipComponent.text(Component.translatable(getTranslationKey(parsedXMaterial.parseItem())).color(NamedTextColor.DARK_GRAY)));
                    }
                }
            }
        }

        if (item.getType().getMaxDurability() > 0) {
            int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
            int maxDur = item.getType().getMaxDurability();
            if (durability < maxDur) {
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getDurability()).args(Component.text(durability), Component.text(maxDur)).color(NamedTextColor.WHITE)));
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
