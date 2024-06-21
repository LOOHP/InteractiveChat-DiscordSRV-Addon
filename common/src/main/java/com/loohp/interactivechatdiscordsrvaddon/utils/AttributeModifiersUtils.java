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

import com.google.common.collect.Multimap;
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechatdiscordsrvaddon.nms.NMSAddon;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.EquipmentSlotGroup;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class AttributeModifiersUtils {

    public static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    public static final Key BASE_ATTACK_DAMAGE_MODIFIER_ID = Key.key("minecraft", "base_attack_damage");
    public static final Key BASE_ATTACK_SPEED_MODIFIER_ID = Key.key("minecraft", "base_attack_speed");

    public static final String GENERIC_KNOCKBACK_RESISTANCE = "attribute.name.generic.knockback_resistance";

    public static Map<EquipmentSlotGroup, Multimap<String, AttributeModifier>> getAttributeModifiers(ItemStack itemStack) {
        return (Map<EquipmentSlotGroup, Multimap<String, AttributeModifier>>) NMSAddon.getInstance().getItemAttributeModifiers(itemStack);
    }

}
