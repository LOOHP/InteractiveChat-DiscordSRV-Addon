/*
 * This file is part of ImageFrame.
 *
 * Copyright (C) 2024. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2024. Contributors
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
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.BiomePrecipitation;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DimensionManager;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.EquipmentSlotGroup;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PaintingVariant;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ProfileProperty;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.TintColorProvider;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.ChatColor;
import com.loohp.interactivechat.libs.org.apache.commons.lang3.math.Fraction;
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

    public abstract DimensionManager getDimensionManager(World world);

    public abstract Key getNamespacedKey(World world);

    public abstract BiomePrecipitation getPrecipitation(Location location);

    public abstract OptionalInt getTropicalFishBucketVariantTag(ItemStack bucket);

    public abstract PotionType getBasePotionType(ItemStack potion);

    public abstract List<PotionEffect> getAllPotionEffects(ItemStack potion);

    public abstract ChatColor getPotionEffectChatColor(PotionEffectType type);

    public abstract Map<String, ?> getPotionAttributeModifiers(PotionEffect effect);

    public abstract boolean isItemUnbreakable(ItemStack itemStack);

    public abstract List<ICMaterial> getItemCanPlaceOnList(ItemStack itemStack);

    public abstract List<ICMaterial> getItemCanDestroyList(ItemStack itemStack);

    public abstract OptionalInt getLeatherArmorColor(ItemStack itemStack);

    public abstract boolean hasBlockEntityTag(ItemStack itemStack);

    public abstract Key getGoatHornInstrument(ItemStack itemStack);

    public abstract PaintingVariant getPaintingVariant(ItemStack itemStack);

    public abstract String getEntityNBT(Entity entity);

    public abstract float getTrimMaterialIndex(Object trimMaterial);

    public abstract TextColor getTrimMaterialColor(Object trimMaterial);

    public abstract AdvancementData getAdvancementDataFromBukkitAdvancement(Object bukkitAdvancement);

    public abstract Object getBukkitAdvancementFromEvent(Event event);

    public abstract boolean matchArmorSlot(ItemStack armorItem, EquipmentSlot slot);

    public abstract Key getArmorMaterialKey(ItemStack armorItem);

    public abstract Map<EquipmentSlotGroup, ? extends Multimap<String, ?>> getItemAttributeModifiers(ItemStack itemStack);

    public abstract Component getDeathMessage(Player player);

    public abstract Key getDecoratedPotSherdPatternName(ItemStack itemStack);

    public abstract String getMusicDiscNameTranslationKey(ItemStack disc);

    public abstract String getEnchantmentTranslationKey(Enchantment enchantment);

    public abstract String getEffectTranslationKey(PotionEffectType type);

    public abstract String getEntityTypeTranslationKey(EntityType type);

    public abstract FishHook getFishHook(Player player);

    public abstract String getServerResourcePack();

    public abstract String getServerResourcePackHash();

    public abstract int getServerResourcePackVersion();

    public abstract float getEnchantmentDamageBonus(ItemStack itemStack, LivingEntity livingEntity);

    public abstract int getItemComponentsSize(ItemStack itemStack);

    public abstract GameProfile getPlayerHeadProfile(ItemStack playerHead);

    public abstract ItemFlag getHideAdditionalItemFlag();

    public abstract Key getAttributeModifierKey(Object attributeModifier);

    public abstract ProfileProperty toProfileProperty(Property property);

    public abstract Fraction getWeightForBundle(ItemStack itemStack);

}
