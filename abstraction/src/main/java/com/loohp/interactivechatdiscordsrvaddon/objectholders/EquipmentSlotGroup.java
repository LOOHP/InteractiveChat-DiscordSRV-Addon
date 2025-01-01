/*
 * This file is part of InteractiveChatDiscordSrvAddon-Abstraction.
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

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import org.bukkit.inventory.EquipmentSlot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum EquipmentSlotGroup {

    ANY("any", EquipmentSlot.values()),
    MAINHAND("mainhand", EquipmentSlot.HAND),
    OFFHAND("offhand", EquipmentSlot.OFF_HAND),
    ANY_HAND("hand", EquipmentSlot.HAND, EquipmentSlot.OFF_HAND),
    FEET("feet", EquipmentSlot.FEET),
    LEGS("legs", EquipmentSlot.LEGS),
    CHEST("chest", EquipmentSlot.CHEST),
    HEAD("head", EquipmentSlot.HEAD),
    ARMOR("armor", EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD),
    BODY("body");

    private final String name;
    private final List<EquipmentSlot> slots;

    EquipmentSlotGroup(String name, List<EquipmentSlot> slots) {
        this.name = name;
        this.slots = Collections.unmodifiableList(slots);
    }

    EquipmentSlotGroup(String name, EquipmentSlot slot) {
        this(name, Collections.singletonList(slot));
    }

    EquipmentSlotGroup(String name, EquipmentSlot... slots) {
        this(name, Arrays.asList(slots));
    }

    public static EquipmentSlotGroup forEquipmentSlot(EquipmentSlot slot) {
        switch (slot) {
            case HAND: return MAINHAND;
            case OFF_HAND: return OFFHAND;
            case FEET: return FEET;
            case LEGS: return LEGS;
            case CHEST: return CHEST;
            case HEAD: return HEAD;
        }
        return null;
    }

    public static EquipmentSlotGroup fromName(String name) {
        for (EquipmentSlotGroup slotGroup : EquipmentSlotGroup.values()) {
            if (slotGroup.name.equals(name)) {
                return slotGroup;
            }
        }
        return null;
    }

    public String asString() {
        return this.name;
    }

    public boolean matches(EquipmentSlot slot) {
        return slots.contains(slot);
    }

    public List<EquipmentSlot> getEquipmentSlots() {
        return slots;
    }

}