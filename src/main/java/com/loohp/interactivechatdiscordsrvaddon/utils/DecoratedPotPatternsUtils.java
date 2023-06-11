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

public class DecoratedPotPatternsUtils {

    private static Class<?> nmsDecoratedPotPatternsClass;
    private static Class<?> nmsItemClass;
    private static Method getResourceKeyMethod;
    private static Method nmsResourceKeyGetLocationMethod;
    private static Class<?> craftItemStackClass;
    private static Class<?> nmsItemStackClass;
    private static Method asNMSCopyMethod;
    private static Method nmsGetItemMethod;

    static {
        try {
            nmsDecoratedPotPatternsClass = NMSUtils.getNMSClass("net.minecraft.world.level.block.entity.DecoratedPotPatterns");
            nmsItemClass = NMSUtils.getNMSClass("net.minecraft.world.item.Item");
            getResourceKeyMethod = nmsDecoratedPotPatternsClass.getMethod("a", nmsItemClass);
            nmsResourceKeyGetLocationMethod = getResourceKeyMethod.getReturnType().getMethod("a");
            craftItemStackClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.inventory.CraftItemStack");
            nmsItemStackClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ItemStack", "net.minecraft.world.item.ItemStack");
            asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
            nmsGetItemMethod = NMSUtils.reflectiveLookup(Method.class, () -> {
                return nmsItemStackClass.getMethod("getItem");
            }, () -> {
                Method method = nmsItemStackClass.getMethod("c");
                if (!method.getReturnType().equals(nmsItemClass)) {
                    throw new ReflectiveOperationException("Wrong return type");
                }
                return method;
            }, () -> {
                return nmsItemStackClass.getMethod("d");
            });
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    public static String getPatternName(ItemStack itemStack) {
        try {
            Object nmsItemStackObject = asNMSCopyMethod.invoke(null, itemStack);
            Object nmsItemObject = nmsGetItemMethod.invoke(nmsItemStackObject);
            Object nmsResourceKey = getResourceKeyMethod.invoke(null, nmsItemObject);
            if (nmsResourceKey == null) {
                return null;
            }
            return nmsResourceKeyGetLocationMethod.invoke(nmsResourceKey).toString();
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
