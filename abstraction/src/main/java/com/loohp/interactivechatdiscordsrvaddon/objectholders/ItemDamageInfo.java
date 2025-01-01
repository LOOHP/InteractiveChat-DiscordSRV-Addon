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

public class ItemDamageInfo {

    private final int damage;
    private final int maxDamage;

    public ItemDamageInfo(int damage, int maxDamage) {
        this.damage = damage;
        this.maxDamage = maxDamage;
    }

    public int getDamage() {
        return damage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }
}
