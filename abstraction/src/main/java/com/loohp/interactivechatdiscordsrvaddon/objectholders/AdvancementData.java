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

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;

public class AdvancementData {

    private final Component title;
    private final Component description;
    private final ItemStack item;
    private final AdvancementType advancementType;
    private final boolean isMinecraft;

    public AdvancementData(Component title, Component description, ItemStack item, AdvancementType advancementType, boolean isMinecraft) {
        this.title = title;
        this.description = description;
        this.item = item;
        this.advancementType = advancementType;
        this.isMinecraft = isMinecraft;
    }

    public Component getTitle() {
        return title;
    }

    public Component getDescription() {
        return description;
    }

    public ItemStack getItem() {
        return item;
    }

    public AdvancementType getAdvancementType() {
        return advancementType;
    }

    public boolean isMinecraft() {
        return isMinecraft;
    }

}
