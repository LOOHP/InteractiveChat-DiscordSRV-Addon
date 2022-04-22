/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
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

package com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.cit;

import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.IntegerRange;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PercentageOrIntegerRange;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Map;
import java.util.Set;

public class EnchantmentProperties extends CITProperties {

    private int layer;
    private double speed;
    private double rotation;
    private double duration;
    private String blend;

    private String texture;

    public EnchantmentProperties(int weight, Set<XMaterial> items, IntegerRange stackSize, PercentageOrIntegerRange damage, int damageMask, EquipmentSlot hand, Map<Enchantment, IntegerRange> enchantments, Map<String, CITStringMatcher> nbtMatch, int layer, double speed, double rotation, double duration, String blend, String texture) {
        super(weight, items, stackSize, damage, damageMask, hand, enchantments, nbtMatch);
        this.layer = layer;
        this.speed = speed;
        this.rotation = rotation;
        this.duration = duration;
        this.blend = blend;
        this.texture = texture;
    }

    public int getLayer() {
        return layer;
    }

    public double getSpeed() {
        return speed;
    }

    public double getRotation() {
        return rotation;
    }

    public double getDuration() {
        return duration;
    }

    public String getBlend() {
        return blend;
    }

    public String getTexture() {
        return texture;
    }

    @Override
    public String getOverrideAsset(String path, String extension) {
        if (extension.equalsIgnoreCase("png")) {
            return texture;
        } else {
            return null;
        }
    }

}
