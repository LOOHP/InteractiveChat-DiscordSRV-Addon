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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArmorUtils {

    public static ArmorTextureResult getArmorTexture(ItemStack armorItem, EquipmentSlot slot) {
        if (armorItem == null || slot.equals(EquipmentSlot.HAND) || slot.equals(EquipmentSlot.OFF_HAND)) {
            return ArmorTextureResult.NONE;
        }
        Key key = NMSAddon.getInstance().getArmorMaterialKey(armorItem);
        String armorMaterialName = key.value();
        String namespace = key.namespace();
        int layer = slot.equals(EquipmentSlot.LEGS) ? 2 : 1;
        String base = armorMaterialName + "_layer_" + layer;
        if (armorItem.getItemMeta() instanceof LeatherArmorMeta) {
            int color = NMSAddon.getInstance().getLeatherArmorColor(armorItem).orElse(ResourceRegistry.DEFAULT_DYE_COLOR);
            return ArmorTextureResult.result(namespace, base, base + "_overlay", color);
        } else {
            return ArmorTextureResult.result(namespace, base);
        }
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
