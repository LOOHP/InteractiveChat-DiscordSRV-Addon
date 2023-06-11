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

import com.google.common.collect.Multimap;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.api.InteractiveChatAPI;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.io.github.bananapuncher714.nbteditor.NBTEditor;
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
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
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ColorUtils;
import com.loohp.interactivechat.utils.FilledMapUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.ItemNBTUtils;
import com.loohp.interactivechat.utils.ItemStackUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NBTParsingUtils;
import com.loohp.interactivechat.utils.RarityUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ToolTipComponent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ToolTipComponent.ToolTipType;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.PatternTypeWrapper;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.KeybindComponent;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.TranslatableComponent;
import github.scarsz.discordsrv.dependencies.mcdiscordreserializer.discord.DiscordSerializer;
import github.scarsz.discordsrv.dependencies.mcdiscordreserializer.discord.DiscordSerializerOptions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.DecoratedPot;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static com.loohp.interactivechat.utils.LanguageUtils.getTranslation;
import static com.loohp.interactivechat.utils.LanguageUtils.getTranslationKey;

@SuppressWarnings("deprecation")
public class DiscordItemStackUtils {

    public static final String DISCORD_EMPTY = "\u200e";

    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder().extractUrls().hexColors().useUnusualXRepeatedCharacterHexFormat().build();
    private static final DecimalFormat ATTRIBUTE_FORMAT = new DecimalFormat("#.##");

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
        if (item != null && item.getItemMeta() != null) {
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

    public static String getItemNameForDiscord(ItemStack item, OfflineICPlayer player, String language) {
        UnaryOperator<String> translationFunction = InteractiveChatDiscordSrvAddon.plugin.getResourceManager().getLanguageManager().getTranslateFunction().ofLanguage(language);

        Player bukkitPlayer = player == null || player.getPlayer() == null || !player.getPlayer().isLocal() ? null : player.getPlayer().getLocalPlayer();
        if (bukkitPlayer == null && !Bukkit.getOnlinePlayers().isEmpty()) {
            bukkitPlayer = Bukkit.getOnlinePlayers().iterator().next();
        }
        item = InteractiveChatAPI.transformItemStack(item, bukkitPlayer == null ? null : bukkitPlayer.getUniqueId());

        if (item == null) {
            item = new ItemStack(Material.AIR);
        }
        ICMaterial icMaterial = ICMaterial.from(item);
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
        UnaryOperator<String> translationFunction = InteractiveChatDiscordSrvAddon.plugin.getResourceManager().getLanguageManager().getTranslateFunction().ofLanguage(language);

        Player bukkitPlayer = player == null || player.getPlayer() == null || !player.getPlayer().isLocal() ? null : player.getPlayer().getLocalPlayer();
        if (bukkitPlayer == null && !Bukkit.getOnlinePlayers().isEmpty()) {
            bukkitPlayer = Bukkit.getOnlinePlayers().iterator().next();
        }
        item = InteractiveChatAPI.transformItemStack(item, bukkitPlayer == null ? null : bukkitPlayer.getUniqueId());
        World world = bukkitPlayer == null ? null : bukkitPlayer.getWorld();

        List<ToolTipComponent<?>> prints = new ArrayList<>();
        boolean hasCustomName = false;

        if (item == null) {
            item = new ItemStack(Material.AIR);
        }
        ICMaterial icMaterial = ICMaterial.from(item);

        Component itemDisplayNameComponent = ItemStackUtils.getDisplayName(item);
        prints.add(ToolTipComponent.text(itemDisplayNameComponent));

        boolean hasMeta = item.getItemMeta() != null;

        if (hasMeta && item.getItemMeta().getDisplayName() != null) {
            hasCustomName = true;
        }

        if (icMaterial.isMaterial(XMaterial.DECORATED_POT) && hasMeta && item.getItemMeta() instanceof BlockStateMeta) {
            BlockState state = ((BlockStateMeta) item.getItemMeta()).getBlockState();
            if (state instanceof DecoratedPot) {
                DecoratedPot pot = (DecoratedPot) state;
                List<Material> materials = pot.getShards();
                prints.add(ToolTipComponent.text(Component.empty()));
                for (int i : new int[] {3, 1, 2, 0}) {
                    if (i < materials.size()) {
                        Key material = Key.key(materials.get(i).getKey().toString());
                        prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getPotterySherdName(material)).color(NamedTextColor.GRAY)));
                    }
                }
            }
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_19_4) && ItemStackUtils.isArmor(item) && hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ARMOR_TRIM)) {
            if (NBTEditor.contains(item, "Trim")) {
                Key material = Key.key(NBTEditor.getString(item, "Trim", "material"));
                Key pattern = Key.key(NBTEditor.getString(item, "Trim", "pattern"));
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getSmithingTemplateUpgrade()).color(NamedTextColor.GRAY)));
                TextColor color = ArmorTrimUtils.getArmorTrimIndex(world, item).right();
                prints.add(ToolTipComponent.text(Component.text(" ").append(Component.translatable(TranslationKeyUtils.getArmorTrimPatternDescription(pattern)).color(color))));
                prints.add(ToolTipComponent.text(Component.text(" ").append(Component.translatable(TranslationKeyUtils.getArmorTrimMaterialDescription(material)).color(color))));
            }
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_19_4) && icMaterial.isOneOf(Collections.singletonList("CONTAINS:Smithing_Template"))) {
            Key key = Key.key(item.getType().getKey().toString());
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getTrimPatternName(key)).color(NamedTextColor.GRAY)));
        }

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_19_4) && icMaterial.isMaterial(XMaterial.PAINTING)) {
            if (NBTEditor.contains(item, "EntityTag", "variant")) {
                String variant = NBTEditor.getString(item, "EntityTag", "variant");
                if (variant.contains(":")) {
                    variant = variant.substring(variant.indexOf(":") + 1);
                }
                Art art = Art.getByName(variant);
                if (art != null) {
                    prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getPaintingTitle(art)).color(NamedTextColor.YELLOW)));
                    prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getPaintingAuthor(art)).color(NamedTextColor.GRAY)));
                    prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getPaintingDimension()).args(Component.text(art.getBlockWidth()), Component.text(art.getBlockHeight())).color(NamedTextColor.WHITE)));
                }
            }
        }

        if (InteractiveChat.version.isNewerThan(MCVersion.V1_19) && icMaterial.isMaterial(XMaterial.SPAWNER) && hasMeta && item.getItemMeta() instanceof BlockStateMeta) {
            BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
            EntityType entityType = null;
            if (meta.hasBlockState()) {
                BlockState blockState = meta.getBlockState();
                if (blockState instanceof CreatureSpawner) {
                    CreatureSpawner spawner = (CreatureSpawner) meta.getBlockState();
                    entityType = spawner.getSpawnedType();
                }
            }
            if (entityType == null) {
                prints.add(ToolTipComponent.text(Component.empty()));
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getSpawnerDescription1()).color(NamedTextColor.GRAY)));
                prints.add(ToolTipComponent.text(Component.text(" ").append(Component.translatable(TranslationKeyUtils.getSpawnerDescription2()).color(NamedTextColor.BLUE))));
            } else {
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getEntityTypeName(entityType)).color(NamedTextColor.GRAY)));
            }
        }

        if (icMaterial.isMaterial(XMaterial.GOAT_HORN)) {
            String instrument = NBTEditor.getString(item, "instrument");
            if (instrument != null) {
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getGoatHornInstrument(NamespacedKey.fromString(instrument))).color(NamedTextColor.GRAY)));
            }
        }

        if (icMaterial.isMaterial(XMaterial.BUNDLE) && hasMeta && item.getItemMeta() instanceof BundleMeta) {
            BundleMeta meta = (BundleMeta) item.getItemMeta();
            List<ItemStack> items = meta.getItems();
            BufferedImage contentsImage = ImageGeneration.getBundleContainerInterface(player, items);
            prints.add(ToolTipComponent.image(contentsImage));
            int fullness = BundleUtils.getFullness(items);
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getBundleFullness()).args(Component.text(fullness), Component.text(64)).color(NamedTextColor.GRAY)));
        }

        if (icMaterial.isMaterial(XMaterial.WRITTEN_BOOK) && hasMeta && item.getItemMeta() instanceof BookMeta) {
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

        if (icMaterial.isMaterial(XMaterial.SHIELD) && (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)))) {
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

                int count = 0;
                for (Pattern pattern : patterns) {
                    if (++count > 6) {
                        break;
                    }
                    PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
                    prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getBannerPatternName(type, pattern.getColor())).color(NamedTextColor.GRAY)));
                }
            }
        }

        if (icMaterial.isOneOf(Collections.singletonList("CONTAINS:Banner")) && (!hasMeta || (hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS)))) {
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

            int count = 0;
            for (Pattern pattern : patterns) {
                if (++count > 6) {
                    break;
                }
                PatternTypeWrapper type = PatternTypeWrapper.fromPatternType(pattern.getPattern());
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getBannerPatternName(type, pattern.getColor())).color(NamedTextColor.GRAY)));
            }
        }

        if (icMaterial.isMaterial(XMaterial.TROPICAL_FISH_BUCKET)) {
            List<String> translations = TranslationKeyUtils.getTropicalFishBucketName(item);
            if (translations.size() > 0) {
                prints.add(ToolTipComponent.text(Component.translatable(translations.get(0)).color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC)));
                if (translations.size() > 1) {
                    prints.add(ToolTipComponent.text(ComponentStringUtils.join(Component.empty().color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC), Component.text(", "), translations.stream().skip(1).map(each -> Component.translatable(each)).collect(Collectors.toList()))));
                }
            }
        }

        if (icMaterial.isOneOf(Collections.singletonList("CONTAINS:music_disc"))) {
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getMusicDiscName(item)).color(NamedTextColor.GRAY)));
        }

        if (icMaterial.isOneOf(Collections.singletonList("CONTAINS:disc_fragment"))) {
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getDiscFragmentName(item)).color(NamedTextColor.GRAY)));
        }

        if (icMaterial.isOneOf(Collections.singletonList("CONTAINS:banner_pattern"))) {
            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getBannerPatternItemName(icMaterial)).color(NamedTextColor.GRAY)));
        }

        if (icMaterial.isMaterial(XMaterial.FIREWORK_ROCKET)) {
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

        if (icMaterial.isMaterial(XMaterial.FIREWORK_STAR) && hasMeta && item.getItemMeta() instanceof FireworkEffectMeta) {
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

        if (icMaterial.isMaterial(XMaterial.CROSSBOW)) {
            CrossbowMeta meta = (CrossbowMeta) item.getItemMeta();
            List<ItemStack> charged = meta.getChargedProjectiles();
            if (charged != null && !charged.isEmpty()) {
                ItemStack charge = charged.get(0);
                List<ToolTipComponent<?>> chargedItemInfo = getToolTip(charge, player).getComponents();
                Component chargeItemName = (Component) chargedItemInfo.get(0).getToolTipComponent();
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getCrossbowProjectile()).color(NamedTextColor.WHITE).append(Component.text(" [").color(NamedTextColor.WHITE)).append(chargeItemName).append(Component.text("]").color(NamedTextColor.WHITE))));
                if (InteractiveChatDiscordSrvAddon.plugin.showFireworkRocketDetailsInCrossbow && ICMaterial.from(charge).isMaterial(XMaterial.FIREWORK_ROCKET)) {
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

        if (InteractiveChatDiscordSrvAddon.plugin.showMapScale && InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && FilledMapUtils.isFilledMap(item)) {
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
                                if (icMaterial.isMaterial(XMaterial.LINGERING_POTION)) {
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
                                if (icMaterial.isMaterial(XMaterial.LINGERING_POTION)) {
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
                                Component time;
                                if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_19_4) && effect.getDuration() == -1) {
                                    time = Component.translatable(TranslationKeyUtils.getPotionDurationInfinite());
                                } else {
                                    time = Component.text(TimeUtils.getReadableTimeBetween(0, duration, ":", ChronoUnit.MINUTES, ChronoUnit.SECONDS, true));
                                }
                                component = Component.translatable(TranslationKeyUtils.getPotionWithDuration()).args(component, time);
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
                            AttributeModifier attributeModifier = entry.getValue();
                            double amount = attributeModifier.getAmount();
                            int operation = attributeModifier.getOperation().ordinal();
                            if (!(operation != 1 && operation != 2)) {
                                amount *= 100;
                            }
                            prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getAttributeModifierKey(false, amount, operation)).args(Component.text(ATTRIBUTE_FORMAT.format(Math.abs(amount))), Component.translatable(attributeName)).color(amount < 0 ? NamedTextColor.RED : NamedTextColor.BLUE)));
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
            for (Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                int level = entry.getValue();
                String key = TranslationKeyUtils.getEnchantment(enchantment);
                String enchantmentName;
                if (key.equals(getTranslation(key, language))) {
                    continue;
                } else {
                    enchantmentName = key;
                }
                if (enchantmentName != null) {
                    if (enchantment.getMaxLevel() == 1 && level == 1) {
                        prints.add(ToolTipComponent.text(Component.translatable(enchantmentName).color(NamedTextColor.GRAY)));
                    } else {
                        prints.add(ToolTipComponent.text(Component.translatable(enchantmentName).append(Component.text(" ")).append(Component.translatable(TranslationKeyUtils.getEnchantmentLevel(level))).color(NamedTextColor.GRAY)));
                    }
                }
            }
        }

        if (InteractiveChatDiscordSrvAddon.plugin.showArmorColor && hasMeta && item.getItemMeta() instanceof LeatherArmorMeta && item.getItemMeta().getItemFlags().stream().noneMatch(each -> each.name().equals("HIDE_DYE"))) {
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

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_12) && hasMeta && !item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ATTRIBUTES)) {
            Map<EquipmentSlot, List<ToolTipComponent<?>>> tooltips = new EnumMap<>(EquipmentSlot.class);
            Map<EquipmentSlot, Multimap<String, AttributeModifier>> attributeList = AttributeModifiersUtils.getAttributeModifiers(item);
            for (Entry<EquipmentSlot, Multimap<String, AttributeModifier>> entry : attributeList.entrySet()) {
                for (Entry<String, AttributeModifier> subEntry : entry.getValue().entries()) {
                    AttributeModifier attributemodifier = subEntry.getValue();
                    String attributeName = subEntry.getKey();
                    double amount = attributemodifier.getAmount();
                    AttributeModifier.Operation operation = attributemodifier.getOperation();

                    boolean flag = false;

                    if (bukkitPlayer != null) {
                        if (attributemodifier.getUniqueId().equals(AttributeModifiersUtils.BASE_ATTACK_DAMAGE_UUID)) {
                            amount += bukkitPlayer.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue();
                            amount += EnchantmentManagerUtils.getDamageBonus(item, EnchantmentManagerUtils.MonsterType.UNDEFINED);
                            flag = true;
                        } else if (attributemodifier.getUniqueId().equals(AttributeModifiersUtils.BASE_ATTACK_SPEED_UUID)) {
                            amount += bukkitPlayer.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getBaseValue();
                            flag = true;
                        }
                    }

                    if (!attributemodifier.getOperation().equals(AttributeModifier.Operation.ADD_SCALAR) && !attributemodifier.getOperation().equals(AttributeModifier.Operation.MULTIPLY_SCALAR_1)) {
                        if (attributeName.equals("attribute.name.generic.knockback_resistance")) {
                            amount = amount * 10.0D;
                        }
                    } else {
                        amount = amount * 100.0D;
                    }

                    if (amount != 0) {
                        TextColor color = flag ? NamedTextColor.DARK_GREEN : (amount < 0 ? NamedTextColor.RED : NamedTextColor.BLUE);
                        Component component = Component.translatable(TranslationKeyUtils.getAttributeModifierKey(flag, amount, operation.ordinal())).args(Component.text(ATTRIBUTE_FORMAT.format(Math.abs(amount))), Component.translatable(attributeName)).color(color);
                        if (flag) {
                            component = Component.text(" ").append(component);
                        }
                        ToolTipComponent<?> attributeComponent = ToolTipComponent.text(component);
                        tooltips.computeIfAbsent(entry.getKey(), k -> new LinkedList<>()).add(attributeComponent);
                    }
                }
            }
            for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                List<ToolTipComponent<?>> lines = tooltips.get(equipmentSlot);
                if (lines != null) {
                    String modifierSlotKey = TranslationKeyUtils.getModifierSlotKey(equipmentSlot);
                    prints.add(ToolTipComponent.text(Component.empty()));
                    prints.add(ToolTipComponent.text(Component.translatable(modifierSlotKey).color(NamedTextColor.GRAY)));
                    prints.addAll(lines);
                }
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
                    String key = materialTag.getValue();
                    if (key.contains(":")) {
                        key = key.substring(key.lastIndexOf(":") + 1);
                    }
                    ICMaterial parsedICMaterial = ICMaterial.from(key.toUpperCase());
                    if (parsedICMaterial == null) {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(WordUtils.capitalizeFully(materialTag.getValue().replace("_", " ").toLowerCase())).color(NamedTextColor.DARK_GRAY)));
                    } else {
                        prints.add(ToolTipComponent.text(Component.translatable(getTranslationKey(parsedICMaterial.parseItem())).color(NamedTextColor.DARK_GRAY)));
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
                    String key = materialTag.getValue();
                    if (key.contains(":")) {
                        key = key.substring(key.lastIndexOf(":") + 1);
                    }
                    ICMaterial parsedICMaterial = ICMaterial.from(key.toUpperCase());
                    if (parsedICMaterial == null) {
                        prints.add(ToolTipComponent.text(LegacyComponentSerializer.legacySection().deserialize(ChatColor.DARK_GRAY + WordUtils.capitalizeFully(materialTag.getValue().replace("_", " ").toLowerCase()))));
                    } else {
                        prints.add(ToolTipComponent.text(Component.translatable(getTranslationKey(parsedICMaterial.parseItem())).color(NamedTextColor.DARK_GRAY)));
                    }
                }
            }
        }

        if (InteractiveChatDiscordSrvAddon.plugin.showDurability && item.getType().getMaxDurability() > 0) {
            int durability = item.getType().getMaxDurability() - (InteractiveChat.version.isLegacy() ? item.getDurability() : ((Damageable) item.getItemMeta()).getDamage());
            int maxDur = item.getType().getMaxDurability();
            if (durability < maxDur) {
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getDurability()).args(Component.text(durability), Component.text(maxDur)).color(NamedTextColor.WHITE)));
            }
        }
        if (InteractiveChatDiscordSrvAddon.plugin.showAdvanceDetails) {
            CompoundTag nbt = (CompoundTag) NBTParsingUtils.fromSNBT(ItemNBTUtils.getNMSItemStackJson(item));
            prints.add(ToolTipComponent.text(Component.text(nbt.getString("id")).color(NamedTextColor.DARK_GRAY)));
            CompoundTag tag = nbt.getCompoundTag("tag");
            if (tag != null) {
                prints.add(ToolTipComponent.text(Component.translatable(TranslationKeyUtils.getItemNbtTag()).args(Component.text(tag.size())).color(NamedTextColor.DARK_GRAY)));
            }
        }

        return new DiscordToolTip(prints, !hasCustomName && prints.size() <= 1);
    }

    public static boolean isUnbreakable(ItemStack item) {
        if (itemMetaHasUnbreakable) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                return meta.isUnbreakable();
            }
            return false;
        } else {
            return NBTEditor.getByte(item, "Unbreakable") > 0;
        }
    }

    public static String toDiscordText(List<ToolTipComponent<?>> toolTipComponents, Function<ToolTipComponent<BufferedImage>, Component> imageToolTipHandler, String language, boolean embedLinks) {
        UnaryOperator<String> translationFunction = InteractiveChatDiscordSrvAddon.plugin.getResourceManager().getLanguageManager().getTranslateFunction().ofLanguage(language);
        DiscordSerializer serializerSpecial = new DiscordSerializer(DiscordSerializerOptions.defaults().withEmbedLinks(embedLinks));
        Function<?, String> resolver = component -> serializerSpecial.serialize(ComponentStringUtils.toDiscordSRVComponent(ComponentStringUtils.resolve((Component) component, translationFunction)));
        DiscordSerializer serializerRegular = new DiscordSerializer(new DiscordSerializerOptions(embedLinks, true, (Function<KeybindComponent, String>) resolver, (Function<TranslatableComponent, String>) resolver));
        return toolTipComponents.stream().map(toolTipComponent -> {
            if (toolTipComponent.getType().equals(ToolTipType.TEXT)) {
                Component component = (Component) toolTipComponent.getToolTipComponent();
                if (component != null) {
                    return serializerRegular.serialize(ComponentStringUtils.toDiscordSRVComponent(component));
                }
            } else if (toolTipComponent.getType().equals(ToolTipType.IMAGE)) {
                return serializerRegular.serialize(ComponentStringUtils.toDiscordSRVComponent(imageToolTipHandler.apply((ToolTipComponent<BufferedImage>) toolTipComponent)));
            }
            return "";
        }).collect(Collectors.joining("\n"));
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
