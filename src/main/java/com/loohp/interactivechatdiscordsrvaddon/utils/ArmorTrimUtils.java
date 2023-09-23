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
import com.loohp.interactivechat.utils.NMSUtils;
import it.unimi.dsi.fastutil.floats.FloatObjectPair;
import org.bukkit.inventory.meta.trim.TrimMaterial;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class ArmorTrimUtils {

    private static Class<?> craftTrimMaterialClass;
    private static Method craftTrimMaterialGetHandleMethod;
    private static Class<?> nmsIChatBaseComponentClass;
    private static Class<?> nmsTrimMaterialClass;
    private static Method nmsArmorTrimMaterialGetMaterialIndexMethod;
    private static Method nmsArmorTrimMaterialGetDescriptionMethod;

    static {
        try {
            craftTrimMaterialClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.inventory.trim.CraftTrimMaterial");
            craftTrimMaterialGetHandleMethod = craftTrimMaterialClass.getMethod("getHandle");
            nmsIChatBaseComponentClass = NMSUtils.getNMSClass("net.minecraft.network.chat.IChatBaseComponent");
            nmsTrimMaterialClass = NMSUtils.getNMSClass("net.minecraft.world.item.armortrim.TrimMaterial");
            nmsArmorTrimMaterialGetMaterialIndexMethod = Arrays.stream(nmsTrimMaterialClass.getMethods()).filter(m -> m.getReturnType().equals(float.class)).findFirst().get();
            nmsArmorTrimMaterialGetDescriptionMethod = Arrays.stream(nmsTrimMaterialClass.getMethods()).filter(m -> m.getReturnType().equals(nmsIChatBaseComponentClass)).findFirst().get();
        } catch (ClassNotFoundException | NoSuchMethodException | NoSuchElementException e) {
            e.printStackTrace();
        }
    }

    public static FloatObjectPair<TextColor> getTrimMaterialItemModelData(TrimMaterial trimMaterial) {
        if (trimMaterial == null) {
            return FloatObjectPair.of(0.0F, NamedTextColor.GRAY);
        }
        try {
            Object nmsTrimMaterial = craftTrimMaterialGetHandleMethod.invoke(craftTrimMaterialClass.cast(trimMaterial));
            float index = (float) nmsArmorTrimMaterialGetMaterialIndexMethod.invoke(nmsTrimMaterial);
            Object nmsDescription = nmsArmorTrimMaterialGetDescriptionMethod.invoke(nmsTrimMaterial);
            Component component = ChatComponentType.IChatBaseComponent.convertFrom(nmsDescription);
            TextColor color = component.color();
            return FloatObjectPair.of(index, color == null ? NamedTextColor.GRAY : color);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return FloatObjectPair.of(Float.NEGATIVE_INFINITY, NamedTextColor.GRAY);
        }
    }

}
