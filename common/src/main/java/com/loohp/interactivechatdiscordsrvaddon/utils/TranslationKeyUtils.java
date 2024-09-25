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
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.org.apache.commons.text.WordUtils;
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechatdiscordsrvaddon.nms.NMSAddon;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.EquipmentSlotGroup;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PaintingVariant;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackType;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

public class TranslationKeyUtils {

    public static String getBundleEmptyDescription() {
        return "item.minecraft.bundle.empty.description";
    }

    public static String getBundleEmpty() {
        return "item.minecraft.bundle.empty";
    }

    public static String getBundleFull() {
        return "item.minecraft.bundle.full";
    }

    public static String getBundleLegacyFullness() {
        return "item.minecraft.bundle.fullness";
    }

    public static String getPotterySherdName(Key material) {
        return "item." + material.namespace() + "." + material.value();
    }

    public static String getArmorTrimMaterialDescription(Key material) {
        return "trim_material." + material.namespace() + "." + material.value();
    }

    public static String getArmorTrimPatternDescription(Key pattern) {
        return "trim_pattern." + pattern.namespace() + "." + pattern.value();
    }

    public static String getSmithingTemplateUpgrade() {
        return "item.minecraft.smithing_template.upgrade";
    }

    public static String getSmithingTemplateAppliesTo() {
        return "item.minecraft.smithing_template.applies_to";
    }

    public static String getSmithingTemplateArmorTrimAppliesTo() {
        return "item.minecraft.smithing_template.armor_trim.applies_to";
    }

    public static String getSmithingTemplateNetheriteUpgradeAppliesTo() {
        return "item.minecraft.smithing_template.netherite_upgrade.applies_to";
    }

    public static String getSmithingTemplateIngredients() {
        return "item.minecraft.smithing_template.ingredients";
    }

    public static String getSmithingTemplateArmorTrimIngredients() {
        return "item.minecraft.smithing_template.armor_trim.ingredients";
    }

    public static String getSmithingTemplateNetheriteUpgradeIngredients() {
        return "item.minecraft.smithing_template.netherite_upgrade.ingredients";
    }

    public static String getTrimPatternName(Key material) {
        String namespace = material.namespace();
        String key = material.value();
        key = key.substring(0, key.length() - "_smithing_template".length());
        if (key.endsWith("_upgrade")) {
            return "upgrade." + namespace + "." + key;
        } else if (key.endsWith("_armor_trim")) {
            key = key.substring(0, key.length() - "_armor_trim".length());
            return "trim_pattern." + namespace + "." + key;
        }
        return material.asString();
    }

    public static String getPaintingTitle(PaintingVariant paintingVariant) {
        Key key = paintingVariant.getKey();
        return "painting." + key.namespace() + "." + key.value() + ".title";
    }

    public static String getPaintingAuthor(PaintingVariant paintingVariant) {
        Key key = paintingVariant.getKey();
        return "painting." + key.namespace() + "." + key.value() + ".author";
    }

    public static String getPaintingDimension() {
        return "painting.dimensions";
    }

    public static String getPotionDurationInfinite() {
        return "effect.duration.infinite";
    }

    public static String getItemNbtTag() {
        return "item.nbt_tags";
    }

    public static String getItemComponents() {
        return "item.components";
    }

    public static String getSpawnerDescription1() {
        return "block.minecraft.spawner.desc1";
    }

    public static String getSpawnerDescription2() {
        return "block.minecraft.spawner.desc2";
    }

    public static String getEntityTypeName(EntityType type) {
        return NMSAddon.getInstance().getEntityTypeTranslationKey(type);
    }

    public static String getResourcePackVanillaName() {
        return "resourcePack.vanilla.name";
    }

    public static String getResourcePackVanillaDescription() {
        return "resourcePack.vanilla.description";
    }

    public static String getOldIncompatiblePack() {
        if (InteractiveChat.version.isLegacy()) {
            return "resourcePack.incompatible.old";
        } else {
            return "pack.incompatible.old";
        }
    }

    public static String getNewIncompatiblePack() {
        if (InteractiveChat.version.isLegacy()) {
            return "resourcePack.incompatible.new";
        } else {
            return "pack.incompatible.new";
        }
    }

    public static String getServerResourcePack() {
        return "addServer.resourcePack";
    }

    public static String getServerResourcePackType(ResourcePackType type) {
        if (InteractiveChat.version.isLegacy()) {
            switch (type) {
                case BUILT_IN:
                    return "built-in";
                case WORLD:
                    return "world";
                case LOCAL:
                    return "local";
                case SERVER:
                    return "server";
            }
        } else {
            switch (type) {
                case BUILT_IN:
                    return "pack.source.builtin";
                case WORLD:
                    return "pack.source.world";
                case LOCAL:
                    return "pack.source.local";
                case SERVER:
                    return "pack.source.server";
            }
        }
        return "";
    }

    public static String getWorldSpecificResources() {
        if (InteractiveChat.version.isLegacy()) {
            return "addServer.resourcePack";
        } else {
            return "resourcePack.server.name";
        }
    }

    public static String getFilledMapId() {
        return "filled_map.id";
    }

    public static String getFilledMapScale() {
        return "filled_map.scale";
    }

    public static String getFilledMapLevel() {
        return "filled_map.level";
    }

    public static String getNoEffect() {
        return "effect.none";
    }

    public static String getEffect(PotionEffectType type) {
        return NMSAddon.getInstance().getEffectTranslationKey(type);
    }

    public static String getEffectLevel(int level) {
        return "potion.potency." + level;
    }

    public static String getPotionWhenDrunk() {
        return "potion.whenDrank";
    }

    public static String getPotionWithAmplifier() {
        return "potion.withAmplifier";
    }

    public static String getPotionWithDuration() {
        return "potion.withDuration";
    }

    public static String getEnchantment(Enchantment enchantment) {
        return NMSAddon.getInstance().getEnchantmentTranslationKey(enchantment);
    }

    public static String getEnchantmentLevel(int level) {
        return "enchantment.level." + level;
    }

    public static String getDyeColor() {
        return "item.color";
    }

    public static String getUnbreakable() {
        return "item.unbreakable";
    }

    public static String getDurability() {
        return "item.durability";
    }

    public static String getCrossbowProjectile() {
        return "item.minecraft.crossbow.projectile";
    }

    public static String getCopyToClipboard() {
        return "chat.copy";
    }

    public static String getOpenUrl() {
        return "chat.link.open";
    }

    public static String getRocketFlightDuration() {
        if (InteractiveChat.version.isLegacy()) {
            return "item.fireworks.flight";
        } else {
            return "item.minecraft.firework_rocket.flight";
        }
    }

    public static String getLevelTranslation(int level) {
        if (level == 1) {
            return "container.enchant.level.one";
        } else {
            return "container.enchant.level.many";
        }
    }

    public static String getMusicDiscName(ItemStack disc) {
        return NMSAddon.getInstance().getMusicDiscNameTranslationKey(disc);
    }

    public static String getDiscFragmentName(ItemStack fragment) {
        NamespacedKey namespacedKey = fragment.getType().getKey();
        return "item." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".desc";
    }

    public static String getBannerPatternItemName(ICMaterial material) {
        return "item.minecraft." + material.name().toLowerCase() + ".desc";
    }

    public static List<String> getTropicalFishBucketName(ItemStack bucket) {
        List<String> list = new ArrayList<>();
        OptionalInt optVariance = NMSAddon.getInstance().getTropicalFishBucketVariantTag(bucket);
        if (optVariance.isPresent()) {
            int variance = optVariance.getAsInt();
            int predefinedType = FishUtils.getPredefinedType(variance);
            if (predefinedType >= 0) {
                list.add("entity.minecraft.tropical_fish.predefined." + predefinedType);
            } else {
                DyeColor baseColor = FishUtils.getTropicalFishBaseColor(variance);
                DyeColor patternColor = FishUtils.getTropicalFishPatternColor(variance);
                list.add("entity.minecraft.tropical_fish.type." + FishUtils.getTropicalFishTypeName(variance));
                list.add("color.minecraft." + baseColor.toString().toLowerCase());
                if (!baseColor.equals(patternColor)) {
                    list.add("color.minecraft." + patternColor.toString().toLowerCase());
                }
            }
        }
        return list;
    }

    public static String getBannerPatternName(PatternType type, DyeColor color) {
        Key typeKey = NMSAddon.getInstance().getPatternTypeKey(type);
        if (InteractiveChat.version.isLegacy()) {
            String colorName = WordUtils.capitalizeFully(color.name().toLowerCase().replace("_", " ")).replace(" ", "");
            colorName = colorName.substring(0, 1).toLowerCase() + colorName.substring(1);
            return "item.banner." + typeKey.value() + "." + colorName;
        } else {
            return "block.minecraft.banner." + typeKey.value() + "." + color.name().toLowerCase();
        }
    }

    public static String getAttributeModifierKey(boolean equalFlag, double amount, int operation) {
        if (equalFlag) {
            return "attribute.modifier.equals." + operation;
        } else if (amount > 0) {
            return "attribute.modifier.plus." + operation;
        } else if (amount < 0) {
            return "attribute.modifier.take." + operation;
        } else {
            return "attribute.modifier.equals." + operation;
        }
    }

    public static String getModifierSlotKey(EquipmentSlot slot) {
        switch (slot) {
            case HEAD:
                return "item.modifiers.head";
            case CHEST:
                return "item.modifiers.chest";
            case LEGS:
                return "item.modifiers.legs";
            case FEET:
                return "item.modifiers.feet";
            case HAND:
                return "item.modifiers.mainhand";
            case OFF_HAND:
                return "item.modifiers.offhand";
            default:
                return "item.modifiers." + slot.toString().toLowerCase();
        }
    }

    public static String getModifierSlotGroupKey(EquipmentSlotGroup slot) {
        return "item.modifiers." + slot.asString();
    }

    public static String getCanDestroy() {
        return "item.canBreak";
    }

    public static String getCanPlace() {
        return "item.canPlace";
    }

    public static String getBookAuthor() {
        return "book.byAuthor";
    }

    public static String getBookGeneration(Generation generation) {
        switch (generation) {
            case COPY_OF_ORIGINAL:
                return "book.generation.1";
            case COPY_OF_COPY:
                return "book.generation.2";
            case TATTERED:
                return "book.generation.3";
            case ORIGINAL:
            default:
                return "book.generation.0";
        }
    }

    public static String getBookPageIndicator() {
        return "book.pageIndicator";
    }

    public static String getDefaultContainerTitle() {
        return "container.inventory";
    }

    public static String getEnderChestContainerTitle() {
        return "container.enderchest";
    }

    public static String getFireworkType(FireworkEffect.Type type) {
        if (InteractiveChat.version.isLegacy()) {
            switch (type) {
                case BALL:
                    return "item.fireworksCharge.type.0";
                case BALL_LARGE:
                    return "item.fireworksCharge.type.1";
                case STAR:
                    return "item.fireworksCharge.type.2";
                case CREEPER:
                    return "item.fireworksCharge.type.3";
                case BURST:
                    return "item.fireworksCharge.type.4";
                default:
                    return "item.fireworksCharge.type";
            }
        } else {
            switch (type) {
                case BALL:
                    return "item.minecraft.firework_star.shape.small_ball";
                case BALL_LARGE:
                    return "item.minecraft.firework_star.shape.large_ball";
                case STAR:
                    return "item.minecraft.firework_star.shape.star";
                case CREEPER:
                    return "item.minecraft.firework_star.shape.creeper";
                case BURST:
                    return "item.minecraft.firework_star.shape.burst";
                default:
                    return "item.minecraft.firework_star.shape";
            }
        }
    }

    public static String getFireworkTrail() {
        if (InteractiveChat.version.isLegacy()) {
            return "item.fireworksCharge.trail";
        } else {
            return "item.minecraft.firework_star.trail";
        }
    }

    public static String getFireworkFlicker() {
        if (InteractiveChat.version.isLegacy()) {
            return "item.fireworksCharge.flicker";
        } else {
            return "item.minecraft.firework_star.flicker";
        }
    }

    public static String getFireworkFade() {
        if (InteractiveChat.version.isLegacy()) {
            return "item.fireworksCharge.fadeTo";
        } else {
            return "item.minecraft.firework_star.fade_to";
        }
    }

    public static String getFireworkColor(Color color) {
        DyeColor dyeColor = DyeColor.getByFireworkColor(color);
        if (InteractiveChat.version.isLegacy()) {
            if (dyeColor == null) {
                return "item.fireworksCharge.customColor";
            } else {
                String colorName = WordUtils.capitalizeFully(dyeColor.name().toLowerCase().replace("_", " ")).replace(" ", "");
                colorName = colorName.substring(0, 1).toLowerCase() + colorName.substring(1);
                return "item.fireworksCharge." + colorName;
            }
        } else {
            if (dyeColor == null) {
                return "item.minecraft.firework_star.custom_color";
            } else {
                return "item.minecraft.firework_star." + dyeColor.name().toLowerCase();
            }
        }
    }

    public static String getGoatHornInstrument(Key instrument) {
        return "instrument." + instrument.namespace() + "." + instrument.value();
    }

}
