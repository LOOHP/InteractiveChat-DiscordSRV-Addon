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
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NMSUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackType;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.PatternTypeWrapper;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TranslationKeyUtils {

    private static Method bukkitEnchantmentGetIdMethod;
    private static Class<?> nmsEnchantmentClass;
    private static Method getEnchantmentByIdMethod;
    private static Method getEnchantmentKeyMethod;
    private static Class<?> nmsMobEffectListClass;
    private static Field nmsMobEffectByIdField;
    private static Method getEffectFromIdMethod;
    private static Method getEffectKeyMethod;
    private static Class<?> craftItemStackClass;
    private static Class<?> nmsItemStackClass;
    private static Method asNMSCopyMethod;
    private static Method nmsGetItemMethod;
    private static Class<?> nmsItemRecordClass;
    private static Field nmsItemRecordTranslationKeyField;

    static {
        if (InteractiveChat.version.isLegacy()) {
            try {
                //noinspection JavaReflectionMemberAccess
                bukkitEnchantmentGetIdMethod = Enchantment.class.getMethod("getId");
                nmsEnchantmentClass = NMSUtils.getNMSClass("net.minecraft.server.%s.Enchantment", "net.minecraft.world.item.enchantment.Enchantment");
                if (InteractiveChat.version.isOld()) {
                    getEnchantmentByIdMethod = nmsEnchantmentClass.getMethod("getById", int.class);
                } else {
                    getEnchantmentByIdMethod = nmsEnchantmentClass.getMethod("c", int.class);
                }
                getEnchantmentKeyMethod = nmsEnchantmentClass.getMethod("a");
                nmsMobEffectListClass = NMSUtils.getNMSClass("net.minecraft.server.%s.MobEffectList", "net.minecraft.world.effect.MobEffectList");
                if (InteractiveChat.version.isOld()) {
                    nmsMobEffectByIdField = nmsMobEffectListClass.getField("byId");
                } else {
                    getEffectFromIdMethod = nmsMobEffectListClass.getMethod("fromId", int.class);
                }
                getEffectKeyMethod = nmsMobEffectListClass.getMethod("a");

                nmsItemRecordClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ItemRecord", "net.minecraft.world.item.ItemRecord");
                nmsItemRecordTranslationKeyField = NMSUtils.reflectiveLookup(Field.class, () -> {
                    return nmsItemRecordClass.getDeclaredField("c");
                }, () -> {
                    return nmsItemRecordClass.getDeclaredField("a");
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (InteractiveChat.version.isOlderOrEqualTo(MCVersion.V1_17)) {
                    nmsMobEffectListClass = NMSUtils.getNMSClass("net.minecraft.server.%s.MobEffectList", "net.minecraft.world.effect.MobEffectList");
                    getEffectFromIdMethod = NMSUtils.reflectiveLookup(Method.class, () -> {
                        return nmsMobEffectListClass.getMethod("fromId", int.class);
                    }, () -> {
                        return nmsMobEffectListClass.getMethod("byId", int.class);
                    });
                    getEffectKeyMethod = nmsMobEffectListClass.getMethod("c");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            craftItemStackClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.inventory.CraftItemStack");
            nmsItemStackClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ItemStack", "net.minecraft.world.item.ItemStack");
            asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
            nmsGetItemMethod = NMSUtils.reflectiveLookup(Method.class, () -> {
                return nmsItemStackClass.getMethod("getItem");
            }, () -> {
                return nmsItemStackClass.getMethod("c");
            });
        } catch (SecurityException | ReflectiveOperationException e) {
            e.printStackTrace();
        }
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

    @SuppressWarnings("deprecation")
    public static String getEffect(PotionEffectType type) {
        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_18)) {
            NamespacedKey namespacedKey = type.getKey();
            return "effect." + namespacedKey.getNamespace() + "." + namespacedKey.getKey();
        } else if (!InteractiveChat.version.isLegacy()) {
            try {
                int id = type.getId();
                Object nmsMobEffectListObject = getEffectFromIdMethod.invoke(null, id);
                if (nmsMobEffectListObject != null) {
                    return getEffectKeyMethod.invoke(nmsMobEffectListObject).toString();
                } else {
                    return "";
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            try {
                int id = type.getId();
                Object nmsMobEffectListObject;
                if (InteractiveChat.version.isOlderOrEqualTo(MCVersion.V1_8_4)) {
                    Object nmsMobEffectListArray = nmsMobEffectByIdField.get(null);
                    if (Array.getLength(nmsMobEffectListArray) > id) {
                        nmsMobEffectListObject = Array.get(nmsMobEffectListArray, id);
                    } else {
                        return "";
                    }
                } else {
                    nmsMobEffectListObject = getEffectFromIdMethod.invoke(null, id);
                }
                if (nmsMobEffectListObject != null) {
                    String str = getEffectKeyMethod.invoke(nmsMobEffectListObject).toString();
                    return "effect." + str.substring(str.indexOf(".") + 1);
                } else {
                    return "";
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return "";
            }
        }
    }

    public static String getEffectLevel(int level) {
        return "potion.potency." + level;
    }

    public static String getEnchantment(Enchantment enchantment) {
        if (!InteractiveChat.version.isLegacy()) {
            NamespacedKey namespacedKey = enchantment.getKey();
            return "enchantment." + namespacedKey.getNamespace() + "." + namespacedKey.getKey();
        } else {
            try {
                Object nmsEnchantmentObject = getEnchantmentByIdMethod.invoke(null, bukkitEnchantmentGetIdMethod.invoke(enchantment));
                if (nmsEnchantmentObject != null) {
                    return getEnchantmentKeyMethod.invoke(nmsEnchantmentObject).toString();
                } else {
                    return "";
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                return "";
            }
        }
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
        if (InteractiveChat.version.isLegacy()) {
            try {
                Object nmsItemStackObject = asNMSCopyMethod.invoke(null, disc);
                Object nmsItemObject = nmsGetItemMethod.invoke(nmsItemStackObject);
                Object nmsItemRecordObject = nmsItemRecordClass.cast(nmsItemObject);
                nmsItemRecordTranslationKeyField.setAccessible(true);
                if (InteractiveChat.version.isOld()) {
                    return "item.record." + nmsItemRecordTranslationKeyField.get(nmsItemRecordObject).toString() + ".desc";
                } else {
                    return nmsItemRecordTranslationKeyField.get(nmsItemRecordObject).toString();
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            NamespacedKey namespacedKey = disc.getType().getKey();
            return "item." + namespacedKey.getNamespace() + "." + namespacedKey.getKey() + ".desc";
        }
    }

    public static String getBannerPatternItemName(XMaterial material) {
        return "item.minecraft." + material.name().toLowerCase() + ".desc";
    }

    public static List<String> getTropicalFishBucketName(ItemStack bucket) {
        List<String> list = new ArrayList<>();
        if (!InteractiveChat.version.isLegacy() && NBTEditor.contains(bucket, "BucketVariantTag")) {
            int variance = NBTEditor.getInt(bucket, "BucketVariantTag");
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

    public static String getBannerPatternName(PatternTypeWrapper type, DyeColor color) {
        if (InteractiveChat.version.isLegacy()) {
            String colorName = WordUtils.capitalizeFully(color.name().toLowerCase().replace("_", " ")).replace(" ", "");
            colorName = colorName.substring(0, 1).toLowerCase() + colorName.substring(1);
            return "item.banner." + type.getAssetName() + "." + colorName;
        } else {
            return "block.minecraft.banner." + type.getAssetName() + "." + color.name().toLowerCase();
        }
    }

    public static String getAttributeKey(String attributeName) {
        return "attribute.name." + attributeName;
    }

    public static String getAttributeModifierKey(double amount, int operation) {
        if (amount > 0) {
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

    public static String getBundleFullness() {
        return "item.minecraft.bundle.fullness";
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

}
