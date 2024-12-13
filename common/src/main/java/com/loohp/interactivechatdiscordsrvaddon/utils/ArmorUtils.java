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

import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechatdiscordsrvaddon.nms.NMSAddon;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.definitions.equipment.EquipmentModelDefinition;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;

public class ArmorUtils {

    public static ArmorTextureResult getArmorTexture(ResourceManager manager, ItemStack armorItem, EquipmentSlot slot) {
        if (armorItem == null || slot.equals(EquipmentSlot.HAND) || slot.equals(EquipmentSlot.OFF_HAND)) {
            return ArmorTextureResult.NONE;
        }
        Key key = NMSAddon.getInstance().getArmorMaterialKey(armorItem);
        if (key == null) {
            return ArmorTextureResult.NONE;
        }
        String armorMaterialName = key.value();
        String namespace = key.namespace();
        EquipmentModelDefinition equipmentModelDefinition = manager.getEquipmentModelDefinitionManager().getEquipmentModelDefinition(key.asString());
        if (equipmentModelDefinition != null && equipmentModelDefinition.getLayers().containsKey(EquipmentModelDefinition.EquipmentLayerType.IC_LEGACY)) {
            int layer = slot.equals(EquipmentSlot.LEGS) ? 2 : 1;
            String base = armorMaterialName + "_layer_" + layer;
            if (armorItem.getItemMeta() instanceof LeatherArmorMeta) {
                int color = NMSAddon.getInstance().getLeatherArmorColor(armorItem).orElse(ResourceRegistry.DEFAULT_DYE_COLOR);
                return ArmorTextureResult.result(namespace, base, base + "_overlay", color);
            } else {
                return ArmorTextureResult.result(namespace, base);
            }
        } else {
            EquipmentModelDefinition.EquipmentLayerType equipmentLayerType = slot.equals(EquipmentSlot.LEGS) ? EquipmentModelDefinition.EquipmentLayerType.HUMANOID_LEGGINGS : EquipmentModelDefinition.EquipmentLayerType.HUMANOID;
            if (equipmentModelDefinition == null || equipmentModelDefinition.getLayers(equipmentLayerType).isEmpty()) {
                return ArmorTextureResult.NONE;
            } else {
                List<EquipmentModelDefinition.EquipmentLayer> layers = equipmentModelDefinition.getLayers(equipmentLayerType);
                if (armorItem.getItemMeta() instanceof LeatherArmorMeta) {
                    int color = NMSAddon.getInstance().getLeatherArmorColor(armorItem).orElse(ResourceRegistry.DEFAULT_DYE_COLOR);
                    return ArmorTextureResult.result(layers, color);
                } else {
                    return ArmorTextureResult.result(layers);
                }
            }
        }
    }

    public static class ArmorTextureResult {

        public static ArmorTextureResult NONE = new ArmorTextureResult(Collections.emptyList(), OptionalInt.empty());

        public static ArmorTextureResult result(String namespace, String base) {
            return new ArmorTextureResult(Collections.singletonList(new EquipmentModelDefinition.EquipmentLayer(namespace + ":" + base)), OptionalInt.empty());
        }

        public static ArmorTextureResult result(String namespace, String base, String overlay, int overlayColor) {
            List<EquipmentModelDefinition.EquipmentLayer> layers = Arrays.asList(new EquipmentModelDefinition.EquipmentLayer(namespace + ":" + base), new EquipmentModelDefinition.EquipmentLayer(namespace + ":" + overlay, new EquipmentModelDefinition.EquipmentLayerDyeable(overlayColor)));
            return new ArmorTextureResult(layers, OptionalInt.of(overlayColor));
        }

        public static ArmorTextureResult result(List<EquipmentModelDefinition.EquipmentLayer> layers) {
            return new ArmorTextureResult(layers, OptionalInt.empty());
        }

        public static ArmorTextureResult result(List<EquipmentModelDefinition.EquipmentLayer> layers, int dyeableColor) {
            return new ArmorTextureResult(layers, OptionalInt.of(dyeableColor));
        }

        private final List<EquipmentModelDefinition.EquipmentLayer> layers;
        private final OptionalInt dyeableColor;

        private ArmorTextureResult(List<EquipmentModelDefinition.EquipmentLayer> layers, OptionalInt dyeableColor) {
            this.layers = layers;
            this.dyeableColor = dyeableColor;
        }

        public boolean hasArmorTexture() {
            return !layers.isEmpty();
        }

        public List<EquipmentModelDefinition.EquipmentLayer> getLayers() {
            return layers;
        }

        public boolean hasDyeableColor() {
            return dyeableColor.isPresent();
        }

        public int getDyeableColor() {
            return dyeableColor.getAsInt();
        }
    }

}
