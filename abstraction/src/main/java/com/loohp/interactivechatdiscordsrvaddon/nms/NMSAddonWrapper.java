/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
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

package com.loohp.interactivechatdiscordsrvaddon.nms;

import com.google.common.collect.Multimap;
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.object.PlayerHeadObjectContents;
import com.loohp.interactivechat.libs.org.apache.commons.lang3.math.Fraction;
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AttributeBase;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.BiomePrecipitation;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.CustomModelData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.LegacyDimensionManager;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.EquipmentSlotGroup;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ItemDamageInfo;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.MoonPhase;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PaintingVariant;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ProfileProperty;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.TintColorProvider;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

public abstract class NMSAddonWrapper {

    private static Plugin plugin;
    private static NMSAddonWrapper instance;

    @Deprecated
    public static Plugin getPlugin() {
        return plugin;
    }

    @Deprecated
    public static NMSAddonWrapper getInstance() {
        return instance;
    }

    @Deprecated
    public static void setup(NMSAddonWrapper instance, Plugin plugin) {
        NMSAddonWrapper.instance = instance;
        NMSAddonWrapper.plugin = plugin;
    }

    static final ItemStack ITEM_STACK_AIR = new ItemStack(Material.AIR);

    public abstract Map<ICMaterial, TintColorProvider.SpawnEggTintData> getSpawnEggColorMap();

    public abstract Key getMapCursorTypeKey(MapCursor mapCursor);

    public abstract Key getPatternTypeKey(PatternType patternType);

    public abstract LegacyDimensionManager getLegacyDimensionManager(World world);

    public abstract Key getNamespacedKey(World world);

    public abstract BiomePrecipitation getPrecipitation(Location location);

    public abstract OptionalInt getTropicalFishBucketVariantTag(ItemStack bucket);

    public abstract PotionType getBasePotionType(ItemStack potion);

    public abstract List<PotionEffect> getAllPotionEffects(ItemStack potion);

    public abstract TextColor getPotionEffectChatColor(PotionEffectType type);

    public abstract Map<AttributeBase, ?> getPotionAttributeModifiers(PotionEffect effect);

    public abstract boolean isItemUnbreakable(ItemStack itemStack);

    public abstract List<ICMaterial> getItemCanPlaceOnList(ItemStack itemStack);

    public abstract List<ICMaterial> getItemCanDestroyList(ItemStack itemStack);

    public abstract OptionalInt getLeatherArmorColor(ItemStack itemStack);

    public abstract boolean hasBlockEntityTag(ItemStack itemStack);

    public abstract Component getInstrumentDescription(ItemStack itemStack);

    public abstract PaintingVariant getPaintingVariant(ItemStack itemStack);

    public abstract String getEntityNBT(Entity entity);

    public abstract float getLegacyTrimMaterialIndex(Object trimMaterial);

    public abstract TextColor getTrimMaterialColor(Object trimMaterial);

    public abstract AdvancementData getAdvancementDataFromBukkitAdvancement(Object bukkitAdvancement);

    public abstract Object getBukkitAdvancementFromEvent(Event event);

    public abstract boolean matchArmorSlot(ItemStack armorItem, EquipmentSlot slot);

    public abstract Key getArmorMaterialKey(ItemStack armorItem);

    public abstract Map<EquipmentSlotGroup, ? extends Multimap<AttributeBase, ?>> getItemAttributeModifiers(ItemStack itemStack);

    public abstract Component getDeathMessage(Player player);

    public abstract Key getDecoratedPotSherdPatternName(ItemStack itemStack);

    public abstract boolean isJukeboxPlayable(ItemStack itemStack);

    public abstract boolean shouldSongShowInToolTip(ItemStack disc);

    public abstract Component getJukeboxSongDescription(ItemStack disc);

    public abstract Component getEnchantmentDescription(Enchantment enchantment);

    public abstract List<Enchantment> getEnchantmentOrderForTooltip(Collection<Enchantment> enchantments);

    public abstract String getEffectTranslationKey(PotionEffectType type);

    public abstract String getEntityTypeTranslationKey(EntityType type);

    public abstract FishHook getFishHook(Player player);

    public abstract String getServerResourcePack();

    public abstract String getServerResourcePackHash();

    public abstract int getServerResourcePackMajorVersion();

    public abstract int getServerResourcePackMinorVersion();

    public abstract float getEnchantmentDamageBonus(ItemStack itemStack, LivingEntity livingEntity);

    public abstract int getItemComponentsSize(ItemStack itemStack);

    public abstract GameProfile getPlayerHeadProfile(ItemStack playerHead);

    public abstract GameProfile getPlayerHeadProfile(PlayerHeadObjectContents contents);

    public abstract ItemFlag getHideAdditionalItemFlag();

    public abstract boolean shouldHideTooltip(ItemStack itemStack);

    public abstract Key getAttributeModifierKey(Object attributeModifier);

    public abstract PropertyMap getGameProfilePropertyMap(GameProfile gameProfile);

    public abstract ProfileProperty toProfileProperty(Property property);

    public abstract Fraction getWeightForBundle(ItemStack itemStack);

    public abstract CustomModelData getCustomModelData(ItemStack itemStack);

    public abstract boolean hasDataComponent(ItemStack itemStack, Key componentName, boolean ignoreDefault);

    public abstract String getBlockStateProperty(ItemStack itemStack, String property);

    public abstract ItemDamageInfo getItemDamageInfo(ItemStack itemStack);

    public abstract float getItemCooldownProgress(Player player, ItemStack itemStack);

    public abstract float getSkyAngle(Location location);

    public abstract MoonPhase getMoonPhase(Location location);

    public abstract int getCrossbowPullTime(ItemStack itemStack, LivingEntity livingEntity);

    public abstract int getItemUseTimeLeft(LivingEntity livingEntity);

    public abstract int getTicksUsedSoFar(ItemStack itemStack, LivingEntity livingEntity);

    public abstract Key getItemModelResourceLocation(ItemStack itemStack);

    public abstract Boolean getEnchantmentGlintOverride(ItemStack itemStack);

    public abstract Key getCustomTooltipResourceLocation(ItemStack itemStack);

    public abstract String getBannerPatternTranslationKey(PatternType type, DyeColor color);

    public abstract Component getTrimMaterialDescription(Object trimMaterial);

    public abstract Component getTrimPatternDescription(Object trimPattern, Object trimMaterial);

    public abstract OptionalInt getFireworkFlightDuration(ItemStack itemStack);

    public abstract boolean shouldShowOperatorBlockWarnings(ItemStack itemStack, Player player);

    public abstract Object getItemStackDataComponentValue(ItemStack itemStack, Key component);

    public abstract Object serializeDataComponent(Key component, String data);

    public abstract boolean evaluateComponentPredicateOnItemStack(ItemStack itemStack, String predicateData, String data);

}
