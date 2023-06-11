/*
 * This file is part of InteractiveChatDiscordSrvAddon.
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

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import net.md_5.bungee.api.ChatColor;

public enum AdvancementType {

    TASK(ChatColor.GREEN, "chat.type.advancement.task"),
    CHALLENGE(ChatColor.DARK_PURPLE, "chat.type.advancement.challenge"),
    GOAL(ChatColor.GREEN, "chat.type.advancement.goal"),
    LEGACY(ChatColor.GREEN, "chat.type.achievement", true);

    private static final AdvancementType[] VALUES = values();

    public static AdvancementType fromHandle(Object obj) {
        for (AdvancementType type : VALUES) {
            if (type.toString().equalsIgnoreCase(obj.toString())) {
                return type;
            }
        }
        return null;
    }

    private final ChatColor color;
    private final String translationKey;
    private final boolean isLegacy;

    AdvancementType(ChatColor color, String translationKey, boolean isLegacy) {
        this.color = color;
        this.translationKey = translationKey;
        this.isLegacy = isLegacy;
    }

    AdvancementType(ChatColor color, String translationKey) {
        this(color, translationKey, false);
    }

    public ChatColor getColor() {
        return this.color;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public boolean isLegacy() {
        return isLegacy;
    }

}
