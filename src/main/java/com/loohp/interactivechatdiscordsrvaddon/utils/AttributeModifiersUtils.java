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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.loohp.interactivechat.utils.NMSUtils;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class AttributeModifiersUtils {

    public static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    private static Class<?> craftItemStackClass;
    private static Class<?> nmsItemStackClass;
    private static Method asNMSCopyMethod;
    private static Class<?> nmsEnumItemSlotClass;
    private static Object[] nmsEnumItemSlotEnums;
    private static Method nmsItemStackGetAttributeModifiersMethod;
    private static Class<?> nmsAttributeBaseClass;
    private static Method nmsAttributeBaseGetNameMethod;
    private static Class<?> craftAttributeInstanceClass;
    private static Class<?> nmsAttributeModifierClass;
    private static Method craftAttributeInstanceConvertMethod;

    static {
        try {
            craftItemStackClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.inventory.CraftItemStack");
            nmsItemStackClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ItemStack", "net.minecraft.world.item.ItemStack");
            asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
            nmsEnumItemSlotClass = NMSUtils.getNMSClass("net.minecraft.server.%s.EnumItemSlot", "net.minecraft.world.entity.EnumItemSlot");
            nmsEnumItemSlotEnums = nmsEnumItemSlotClass.getEnumConstants();
            nmsItemStackGetAttributeModifiersMethod = nmsItemStackClass.getMethod("a", nmsEnumItemSlotClass);

            try {
                nmsAttributeBaseClass = NMSUtils.getNMSClass("net.minecraft.server.%s.AttributeBase", "net.minecraft.world.entity.ai.attributes.AttributeBase");
                nmsAttributeBaseGetNameMethod = NMSUtils.reflectiveLookup(Method.class, () -> {
                    return nmsAttributeBaseClass.getMethod("getName");
                }, () -> {
                    return nmsAttributeBaseClass.getMethod("c");
                });
            } catch (ReflectiveOperationException ignore) {
            }
            craftAttributeInstanceClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.attribute.CraftAttributeInstance");
            nmsAttributeModifierClass = NMSUtils.getNMSClass("net.minecraft.server.%s.AttributeModifier", "net.minecraft.world.entity.ai.attributes.AttributeModifier");
            craftAttributeInstanceConvertMethod = craftAttributeInstanceClass.getMethod("convert", nmsAttributeModifierClass);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static Map<EquipmentSlot, Multimap<String, AttributeModifier>> getAttributeModifiers(ItemStack itemStack) {
        try {
            Object nmsItemStack = asNMSCopyMethod.invoke(null, itemStack);
            Map<EquipmentSlot, Multimap<String, AttributeModifier>> result = new EnumMap<>(EquipmentSlot.class);
            for (int i = 0; i < nmsEnumItemSlotEnums.length; i++) {
                Multimap<?, ?> multimap = (Multimap<?, ?>) nmsItemStackGetAttributeModifiersMethod.invoke(nmsItemStack, nmsEnumItemSlotEnums[i]);
                if (multimap != null) {
                    EquipmentSlot equipmentSlot = EquipmentSlot.values()[i];
                    Multimap<String, AttributeModifier> newMap = result.computeIfAbsent(equipmentSlot, k -> LinkedHashMultimap.create());
                    for (Map.Entry<?, ?> entry : multimap.entries()) {
                        String key = entry.getKey() instanceof String ? (String) entry.getKey() : (String) nmsAttributeBaseGetNameMethod.invoke(entry.getKey());
                        AttributeModifier value = (AttributeModifier) craftAttributeInstanceConvertMethod.invoke(null, entry.getValue());
                        newMap.put(key, value);
                    }
                }
            }
            return result;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

}
