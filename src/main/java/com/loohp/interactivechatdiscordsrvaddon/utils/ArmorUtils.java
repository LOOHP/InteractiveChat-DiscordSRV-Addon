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

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.io.github.bananapuncher714.nbteditor.NBTEditor;
import com.loohp.interactivechat.objectholders.ICMaterial;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NMSUtils;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class ArmorUtils {

    private static Class<?> craftItemStackClass;
    private static Class<?> nmsItemStackClass;
    private static Class<?> nmsItemClass;
    private static Method asNMSCopyMethod;
    private static Method nmsGetItemMethod;
    private static Class<?> nmsItemArmorClass;
    private static Class<?> nmsItemArmorColorableClass;
    private static Class<?> nmsArmorMaterialClass;
    private static Method nmsItemArmorGetArmorMaterialMethod;
    private static Method nmsArmorMaterialGetNameMethod;
    private static Class<?> nmsEnumArmorMaterialClass;
    private static Field nmsEnumArmorMaterialNameField;
    private static Class<?> nmsEnumItemSlotClass;
    private static Method nmsItemArmorGetItemSlotMethod;

    static {
        try {
            if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_13)) {
                craftItemStackClass = NMSUtils.getNMSClass("org.bukkit.craftbukkit.%s.inventory.CraftItemStack");
                nmsItemStackClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ItemStack", "net.minecraft.world.item.ItemStack");
                asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
                nmsItemClass = NMSUtils.getNMSClass("net.minecraft.server.%s.Item", "net.minecraft.world.item.Item");
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
                nmsItemArmorClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ItemArmor", "net.minecraft.world.item.ItemArmor");
                nmsItemArmorColorableClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ItemArmorColorable", "net.minecraft.world.item.ItemArmorColorable");
                nmsArmorMaterialClass = NMSUtils.getNMSClass("net.minecraft.server.%s.ArmorMaterial", "net.minecraft.world.item.ArmorMaterial");
                nmsItemArmorGetArmorMaterialMethod = Arrays.stream(nmsItemArmorClass.getMethods()).filter(m -> m.getReturnType().equals(nmsArmorMaterialClass)).findFirst().get();
                nmsArmorMaterialGetNameMethod = Arrays.stream(nmsArmorMaterialClass.getMethods()).filter(m -> m.getReturnType().equals(String.class)).findFirst().orElse(null);
                if (nmsArmorMaterialGetNameMethod == null) {
                    nmsEnumArmorMaterialClass = NMSUtils.getNMSClass("net.minecraft.server.%s.EnumArmorMaterial", "net.minecraft.world.item.EnumArmorMaterial");
                    nmsEnumArmorMaterialNameField = Arrays.stream(nmsEnumArmorMaterialClass.getDeclaredFields()).filter(m -> m.getType().equals(String.class)).findFirst().get();
                }
                nmsEnumItemSlotClass = NMSUtils.getNMSClass("net.minecraft.server.%s.EnumItemSlot", "net.minecraft.world.entity.EnumItemSlot");
                nmsItemArmorGetItemSlotMethod = Arrays.stream(nmsItemArmorClass.getMethods()).filter(m -> m.getReturnType().equals(nmsEnumItemSlotClass)).findFirst().get();
            }
        } catch (SecurityException | ReflectiveOperationException | NoSuchElementException e) {
            e.printStackTrace();
        }
    }

    public static ArmorTextureResult getArmorTexture(ItemStack armorItem, EquipmentSlot slot) {
        if (armorItem == null || slot.equals(EquipmentSlot.HAND) || slot.equals(EquipmentSlot.OFF_HAND)) {
            return ArmorTextureResult.NONE;
        }
        try {
            String armorMaterialName;
            boolean isArmorColorable;
            if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_13)) {
                Object nmsItemStackObject = asNMSCopyMethod.invoke(null, armorItem);
                Object nmsItemObject = nmsGetItemMethod.invoke(nmsItemStackObject);
                if (!nmsItemArmorClass.isInstance(nmsItemObject)) {
                    return ArmorTextureResult.NONE;
                }
                Enum<?> nmsEnumItemSlot = (Enum<?>) nmsItemArmorGetItemSlotMethod.invoke(nmsItemObject);
                if (slot.ordinal() != nmsEnumItemSlot.ordinal()) {
                    return ArmorTextureResult.NONE;
                }
                Object nmsArmorMaterial = nmsItemArmorGetArmorMaterialMethod.invoke(nmsItemObject);
                if (nmsArmorMaterialGetNameMethod == null) {
                    nmsEnumArmorMaterialNameField.setAccessible(true);
                    armorMaterialName = (String) nmsEnumArmorMaterialNameField.get(nmsArmorMaterial);
                } else {
                    armorMaterialName = (String) nmsArmorMaterialGetNameMethod.invoke(nmsArmorMaterial);
                }
                isArmorColorable = nmsItemArmorColorableClass.isInstance(nmsItemObject);
            } else {
                String armorItemMaterial = armorItem.getType().name();
                if (armorItemMaterial.contains("DIAMOND")) {
                    armorMaterialName = "diamond";
                    isArmorColorable = false;
                } else if (armorItemMaterial.contains("GOLD")) {
                    armorMaterialName = "gold";
                    isArmorColorable = false;
                } else if (armorItemMaterial.contains("IRON")) {
                    armorMaterialName = "iron";
                    isArmorColorable = false;
                } else if (armorItemMaterial.contains("CHAIN")) {
                    armorMaterialName = "chainmail";
                    isArmorColorable = false;
                } else {
                    armorMaterialName = "leather";
                    isArmorColorable = true;
                }
            }
            String namespace = ModelUtils.getNamespace(ICMaterial.from(armorItem));
            int layer = slot.equals(EquipmentSlot.LEGS) ? 2 : 1;
            String base = armorMaterialName + "_layer_" + layer;
            if (isArmorColorable) {
                int color;
                if (NBTEditor.contains(armorItem, "display", "color")) {
                    color = NBTEditor.getInt(armorItem, "display", "color");
                } else {
                    color = ResourceRegistry.DEFAULT_DYE_COLOR;
                }
                return ArmorTextureResult.result(namespace, base, base + "_overlay", color);
            } else {
                return ArmorTextureResult.result(namespace, base);
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return ArmorTextureResult.NONE;
    }

    public static class ArmorTextureResult {

        public static ArmorTextureResult NONE = new ArmorTextureResult(null, null, null, Integer.MIN_VALUE);

        public static ArmorTextureResult result(String namespace, String base) {
            return new ArmorTextureResult(namespace, base, null, Integer.MIN_VALUE);
        }

        public static ArmorTextureResult result(String namespace, String base, String overlay, int overlayColor) {
            return new ArmorTextureResult(namespace, base, overlay, overlayColor);
        }

        private final String namespace;
        private final String base;
        private final String overlay;
        private final int overlayColor;

        private ArmorTextureResult(String namespace, String base, String overlay, int overlayColor) {
            this.namespace = namespace;
            this.base = base;
            this.overlay = overlay;
            this.overlayColor = overlayColor;
        }

        public boolean hasArmorTexture() {
            return namespace != null && base != null;
        }

        public String getNamespace() {
            return namespace;
        }

        public String getBase() {
            return base;
        }

        public boolean hasOverlay() {
            return hasArmorTexture() && overlay != null;
        }

        public String getOverlay() {
            return overlay;
        }

        public int getOverlayColor() {
            return overlayColor;
        }
    }

}
