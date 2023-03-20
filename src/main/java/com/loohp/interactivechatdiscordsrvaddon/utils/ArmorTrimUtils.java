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

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechat.utils.ChatComponentType;
import com.loohp.interactivechat.utils.ItemStackUtils;
import com.loohp.interactivechat.utils.NMSUtils;
import it.unimi.dsi.fastutil.floats.FloatObjectPair;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class ArmorTrimUtils {

    private static Class<?> craftWorldClass;
    private static Class<?> nmsWorldServerClass;
    private static Class<?> nmsArmorTrimClass;
    private static Class<?> nmsItemStackClass;
    private static Class<?> nmsTrimMaterialClass;
    private static Method craftWorldGetHandleMethod;
    private static Method nmsWorldServerGetIRegistryCustom;
    private static Method nmsArmorTrimGetTrimMethod;
    private static Method nmsArmorTrimGetMaterialHolderMethod;
    private static Method nmsHolderGetValueMethod;
    private static Method nmsArmorTrimMaterialGetMaterialIndexMethod;
    private static Method nmsArmorTrimMaterialGetDescriptionMethod;

    static {
        try {
            craftWorldClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.CraftWorld");
            craftWorldGetHandleMethod = craftWorldClass.getMethod("getHandle");
            nmsWorldServerClass = craftWorldGetHandleMethod.getReturnType();
            nmsArmorTrimClass = NMSUtils.getNMSClass("net.minecraft.world.item.armortrim.ArmorTrim");
            nmsItemStackClass = NMSUtils.getNMSClass("net.minecraft.world.item.ItemStack");
            nmsTrimMaterialClass = NMSUtils.getNMSClass("net.minecraft.world.item.armortrim.TrimMaterial");
            nmsWorldServerGetIRegistryCustom = nmsWorldServerClass.getMethod("u_");
            nmsArmorTrimGetTrimMethod = nmsArmorTrimClass.getMethod("a", nmsWorldServerGetIRegistryCustom.getReturnType(), nmsItemStackClass);
            nmsArmorTrimGetMaterialHolderMethod = nmsArmorTrimClass.getMethod("b");
            nmsHolderGetValueMethod = nmsArmorTrimGetMaterialHolderMethod.getReturnType().getMethod("a");
            nmsArmorTrimMaterialGetMaterialIndexMethod = nmsTrimMaterialClass.getMethod("c");
            nmsArmorTrimMaterialGetDescriptionMethod = nmsTrimMaterialClass.getMethod("e");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static FloatObjectPair<TextColor> getArmorTrimIndex(World world, ItemStack itemStack) {
        if (world == null) {
            return FloatObjectPair.of(0.0F, NamedTextColor.GRAY);
        }
        try {
            Object nmsWorldServer = craftWorldGetHandleMethod.invoke(craftWorldClass.cast(world));
            Object nmsIRegistryCustom = nmsWorldServerGetIRegistryCustom.invoke(nmsWorldServer);
            Optional<?> optNmsArmorTrim = (Optional<?>) nmsArmorTrimGetTrimMethod.invoke(null, nmsIRegistryCustom, ItemStackUtils.toNMSCopy(itemStack));
            if (optNmsArmorTrim.isPresent()) {
                Object nmsHolderArmorTrimMaterial = nmsArmorTrimGetMaterialHolderMethod.invoke(optNmsArmorTrim.get());
                Object nmsArmorTrimMaterial = nmsHolderGetValueMethod.invoke(nmsHolderArmorTrimMaterial);
                float index = (float) nmsArmorTrimMaterialGetMaterialIndexMethod.invoke(nmsArmorTrimMaterial);
                Object nmsDescription = nmsArmorTrimMaterialGetDescriptionMethod.invoke(nmsArmorTrimMaterial);
                Component component = ChatComponentType.IChatBaseComponent.convertFrom(nmsDescription);
                TextColor color = component.color();
                return FloatObjectPair.of(index, color == null ? NamedTextColor.GRAY : color);
            } else {
                return FloatObjectPair.of(0.0F, NamedTextColor.GRAY);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return FloatObjectPair.of(Float.NEGATIVE_INFINITY, NamedTextColor.GRAY);
        }
    }

}
