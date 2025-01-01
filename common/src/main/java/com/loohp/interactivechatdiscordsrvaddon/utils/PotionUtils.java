/*
 * This file is part of InteractiveChatDiscordSrvAddon.
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

import com.loohp.interactivechatdiscordsrvaddon.nms.NMSAddon;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
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

    public static List<PotionEffect> getAllPotionEffects(ItemStack potion) {
        return NMSAddon.getInstance().getAllPotionEffects(potion);
    }

    public static ChatColor getPotionEffectChatColor(PotionEffectType type) {
        return NMSAddon.getInstance().getPotionEffectChatColor(type);
    }

    public static Map<String, AttributeModifier> getPotionAttributes(PotionEffect effect) {
        return (Map<String, AttributeModifier>) NMSAddon.getInstance().getPotionAttributeModifiers(effect);
    }

}
