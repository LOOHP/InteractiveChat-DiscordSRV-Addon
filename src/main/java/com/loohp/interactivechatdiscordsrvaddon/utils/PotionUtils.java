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
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NMSUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PotionUtils {

    public static final int WATER_COLOR = 3694022;
    public static final int UNCRAFTABLE_COLOR = 16253176;

    private static Class<?> craftItemStackClass;
    private static Class<?> nmsItemStackClass;
    private static Method asNMSCopyMethod;
    private static Method nmsItemHasTagMethod;
    private static Method nmsItemHasGetMethod;
    private static Class<?> nmsNbtTagCompoundClass;
    private static Method nmsNbtTagGetStringMethod;
    private static Class<?> craftPotionBrewerClass;
    private static Object craftPotionBrewerInstance;
    private static Method craftPotionBrewerGetEffectsFromDamageMethod;
    private static Class<?> nmsPotionRegistryClass;
    private static Method nmsPotionRegistryGetPotionRegistryFromStringMethod;
    private static Method nmsPotionRegistryGetMobEffectListMethod;
    private static Class<?> craftPotionUtilClass;
    private static Class<?> nmsMobEffectClass;
    private static Method craftPotionUtilToBukkitMethod;
    private static Class<?> nmsMobEffectListClass;
    private static Field nmsMobEffectListByIdArrayField;
    private static Method nmsMobEffectListByIdMethod;
    private static Field nmsMobEffectListTypeField;
    private static Field nmsMobEffectInfoChatFormatField;

    static {
        try {
            craftItemStackClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.inventory.CraftItemStack");
            nmsItemStackClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ItemStack", "net.minecraft.world.item.ItemStack");
            asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
            nmsItemHasTagMethod = NMSUtils.reflectiveLookup(Method.class, () -> {
                return nmsItemStackClass.getMethod("hasTag");
            }, () -> {
                return nmsItemStackClass.getMethod("r");
            });
            nmsItemHasGetMethod = NMSUtils.reflectiveLookup(Method.class, () -> {
                return nmsItemStackClass.getMethod("getTag");
            }, () -> {
                return nmsItemStackClass.getMethod("s");
            });
            nmsNbtTagCompoundClass = NMSUtils.getNMSClass("net.minecraft.server.%s.NBTTagCompound", "net.minecraft.nbt.NBTTagCompound");
            nmsNbtTagGetStringMethod = NMSUtils.reflectiveLookup(Method.class, () -> {
                return nmsNbtTagCompoundClass.getMethod("getString", String.class);
            }, () -> {
                return nmsNbtTagCompoundClass.getMethod("l", String.class);
            });
            nmsMobEffectListClass = NMSUtils.getNMSClass("net.minecraft.server.%s.MobEffectList", "net.minecraft.world.effect.MobEffectList");
            if (InteractiveChat.version.isOld()) {
                craftPotionBrewerClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.potion.CraftPotionBrewer");
                craftPotionBrewerInstance = craftPotionBrewerClass.getConstructor().newInstance();
                craftPotionBrewerGetEffectsFromDamageMethod = craftPotionBrewerClass.getMethod("getEffectsFromDamage", int.class);
            } else {
                nmsPotionRegistryClass = NMSUtils.getNMSClass("net.minecraft.server.%s.PotionRegistry", "net.minecraft.world.item.alchemy.PotionRegistry");
                nmsPotionRegistryGetPotionRegistryFromStringMethod = nmsPotionRegistryClass.getMethod("a", String.class);
                nmsPotionRegistryGetMobEffectListMethod = nmsPotionRegistryClass.getMethod("a");
                craftPotionUtilClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.potion.CraftPotionUtil");
                nmsMobEffectClass = NMSUtils.getNMSClass("net.minecraft.server.%s.MobEffect", "net.minecraft.world.effect.MobEffect");
                craftPotionUtilToBukkitMethod = craftPotionUtilClass.getMethod("toBukkit", nmsMobEffectClass);
            }
            if (InteractiveChat.version.isOld()) {
                nmsMobEffectListByIdArrayField = nmsMobEffectListClass.getField("byId");
                nmsMobEffectListTypeField = nmsMobEffectListClass.getDeclaredField("K");
            } else if (InteractiveChat.version.isOlderOrEqualTo(MCVersion.V1_13_1)) {
                nmsMobEffectListByIdMethod = nmsMobEffectListClass.getMethod("fromId", int.class);
                nmsMobEffectListTypeField = nmsMobEffectListClass.getDeclaredField("c");
            } else {
                if (InteractiveChat.version.isOlderOrEqualTo(MCVersion.V1_17)) {
                    nmsMobEffectListByIdMethod = nmsMobEffectListClass.getMethod("fromId", int.class);
                } else {
                    nmsMobEffectListByIdMethod = nmsMobEffectListClass.getMethod("a", int.class);
                }
                nmsMobEffectListTypeField = nmsMobEffectListClass.getDeclaredField("b");
                nmsMobEffectInfoChatFormatField = nmsMobEffectListTypeField.getType().getDeclaredField("d");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getPotionBaseColor(PotionType type) {
        PotionEffectType effect = type.getEffectType();
        if (effect == null) {
            if (type.name().equalsIgnoreCase("UNCRAFTABLE")) {
                return UNCRAFTABLE_COLOR;
            } else {
                return WATER_COLOR;
            }
        } else {
            return effect.getColor().asRGB();
        }
    }

    @SuppressWarnings("deprecation")
    public static List<PotionEffect> getBasePotionEffect(ItemStack potion) throws Exception {
        if (InteractiveChat.version.isOld()) {
            return new ArrayList<>((Collection<PotionEffect>) craftPotionBrewerGetEffectsFromDamageMethod.invoke(craftPotionBrewerInstance, potion.getDurability()));
        } else {
            Object nmsStack = asNMSCopyMethod.invoke(null, potion);
            if (!(boolean) nmsItemHasTagMethod.invoke(nmsStack)) {
                return null;
            }
            String pName = (String) nmsNbtTagGetStringMethod.invoke(nmsItemHasGetMethod.invoke(nmsStack), "Potion");
            if (pName == null) {
                return null;
            }
            String[] split = pName.split(":");
            if (split.length == 2) {
                pName = split[1];
            }
            Object reg = nmsPotionRegistryGetPotionRegistryFromStringMethod.invoke(null, pName);
            if (reg == null) {
                return null;
            }
            List<PotionEffect> effects = new ArrayList<>();
            for (Object me : (List<?>) nmsPotionRegistryGetMobEffectListMethod.invoke(reg)) {
                effects.add((PotionEffect) craftPotionUtilToBukkitMethod.invoke(null, me));
            }
            return effects;
        }
    }

    @SuppressWarnings("deprecation")
    public static ChatColor getPotionEffectChatColor(PotionEffectType type) throws Exception {
        int id = type.getId();
        if (InteractiveChat.version.isOld()) {
            Object array = nmsMobEffectListByIdArrayField.get(null);
            Object mobEffectListObject = Array.get(array, id);
            nmsMobEffectListTypeField.setAccessible(true);
            return nmsMobEffectListTypeField.getBoolean(mobEffectListObject) ? ChatColor.RED : ChatColor.BLUE;
        } else if (InteractiveChat.version.isOlderOrEqualTo(MCVersion.V1_13_1)) {
            Object mobEffectListObject = nmsMobEffectListByIdMethod.invoke(null, id);
            nmsMobEffectListTypeField.setAccessible(true);
            return nmsMobEffectListTypeField.getBoolean(mobEffectListObject) ? ChatColor.RED : ChatColor.BLUE;
        } else {
            Object mobEffectListObject = nmsMobEffectListByIdMethod.invoke(null, id);
            nmsMobEffectListTypeField.setAccessible(true);
            Enum<?> mobEffectType = (Enum<?>) nmsMobEffectListTypeField.get(mobEffectListObject);
            nmsMobEffectInfoChatFormatField.setAccessible(true);
            Enum<?> chatFormat = (Enum<?>) nmsMobEffectInfoChatFormatField.get(mobEffectType);
            return ChatColor.getByChar(chatFormat.toString().charAt(1));
        }
    }

}
