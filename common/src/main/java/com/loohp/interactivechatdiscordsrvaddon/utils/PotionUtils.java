/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
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

import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;
import com.loohp.interactivechatdiscordsrvaddon.nms.NMSAddon;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AttributeBase;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;
import java.util.Map;

public class PotionUtils {

    public static final int WATER_COLOR = 3694022;
    public static final int UNCRAFTABLE_COLOR = 16253176;

    @SuppressWarnings("deprecation")
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

    /**
     * Same as vanilla PotionContents#getColor: the custom color if set, otherwise the
     * amplifier-weighted mix of all visible effects (base potion and custom effects),
     * otherwise the given default.
     */
    @SuppressWarnings("deprecation")
    public static int getPotionColor(ItemStack potion, int defaultColor) {
        ItemMeta itemMeta = potion.getItemMeta();
        PotionMeta meta = itemMeta instanceof PotionMeta ? (PotionMeta) itemMeta : null;
        try {
            if (meta != null && meta.hasColor()) {
                return meta.getColor().asRGB();
            }
            int r = 0;
            int g = 0;
            int b = 0;
            int total = 0;
            for (PotionEffect effect : getAllPotionEffects(potion)) {
                if (!effect.hasParticles()) {
                    continue;
                }
                int color = effect.getType().getColor().asRGB();
                int weight = effect.getAmplifier() + 1;
                r += weight * ((color >> 16) & 0xFF);
                g += weight * ((color >> 8) & 0xFF);
                b += weight * (color & 0xFF);
                total += weight;
            }
            if (total > 0) {
                return (r / total) << 16 | (g / total) << 8 | (b / total);
            }
            PotionType type = meta == null ? null : NMSAddon.getInstance().getBasePotionType(potion);
            if (type != null && type.name().equalsIgnoreCase("UNCRAFTABLE")) {
                return UNCRAFTABLE_COLOR;
            }
            return defaultColor;
        } catch (Throwable e) {
            try {
                PotionType type = NMSAddon.getInstance().getBasePotionType(potion);
                return type == null ? defaultColor : getPotionBaseColor(type);
            } catch (Throwable e2) {
                return defaultColor;
            }
        }
    }

    public static List<PotionEffect> getAllPotionEffects(ItemStack potion) {
        return NMSAddon.getInstance().getAllPotionEffects(potion);
    }

    public static TextColor getPotionEffectChatColor(PotionEffectType type) {
        return NMSAddon.getInstance().getPotionEffectChatColor(type);
    }

    public static Map<AttributeBase, AttributeModifier> getPotionAttributes(PotionEffect effect) {
        return (Map<AttributeBase, AttributeModifier>) NMSAddon.getInstance().getPotionAttributeModifiers(effect);
    }

}
