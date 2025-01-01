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

public enum ChargeType {
    NONE("none"),
    ARROW("arrow"),
    ROCKET("rocket");

    private static final ChargeType[] VALUES = values();

    private final String name;

    ChargeType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ChargeType fromName(String name) {
        for (ChargeType type : VALUES) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}