/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2023. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2023. Contributors
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

import com.loohp.interactivechat.utils.NMSUtils;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class EnchantmentManagerUtils {

    private static Class<?> craftItemStackClass;
    private static Class<?> nmsItemStackClass;
    private static Method asNMSCopyMethod;
    private static Class<?> nmsEnumMonsterTypeClass;
    private static Object[] nmsEnumMonsterTypeEnums;
    private static Class<?> nmsEnchantmentManagerClass;
    private static Method nmsEnchantmentManagerGetDamageBonusMethod;

    static {
        try {
            craftItemStackClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.inventory.CraftItemStack");
            nmsItemStackClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ItemStack", "net.minecraft.world.item.ItemStack");
            asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
            nmsEnumMonsterTypeClass = NMSUtils.getNMSClass("net.minecraft.server.%s.EnumMonsterType", "net.minecraft.world.entity.EnumMonsterType");
            if (nmsEnumMonsterTypeClass.isEnum()) {
                nmsEnumMonsterTypeEnums = nmsEnumMonsterTypeClass.getEnumConstants();
            } else {
                nmsEnumMonsterTypeEnums = Arrays.stream(nmsEnumMonsterTypeClass.getFields())
                        .filter(f -> f.getType().equals(nmsEnumMonsterTypeClass) && Modifier.isPublic(f.getModifiers()) && Modifier.isStatic(f.getModifiers()) && Modifier.isFinal(f.getModifiers()))
                        .map(f -> {
                            try {
                                return f.get(null);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                        .toArray();
            }
            nmsEnchantmentManagerClass = NMSUtils.getNMSClass("net.minecraft.server.%s.EnchantmentManager", "net.minecraft.world.item.enchantment.EnchantmentManager");
            nmsEnchantmentManagerGetDamageBonusMethod = nmsEnchantmentManagerClass.getMethod("a", nmsItemStackClass, nmsEnumMonsterTypeClass);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static float getDamageBonus(ItemStack itemStack, MonsterType monsterType) {
        try {
            Object nmsItemStack = asNMSCopyMethod.invoke(null, itemStack);
            Object nmsEnumMonsterType = nmsEnumMonsterTypeEnums[monsterType.ordinal()];
            return (float) nmsEnchantmentManagerGetDamageBonusMethod.invoke(null, nmsItemStack, nmsEnumMonsterType);
        } catch (IllegalAccessException | InvocationTargetException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return 0F;
    }

    public enum MonsterType {

        UNDEFINED, UNDEAD, ARTHROPOD, ILLAGER, WATER;

    }

}
