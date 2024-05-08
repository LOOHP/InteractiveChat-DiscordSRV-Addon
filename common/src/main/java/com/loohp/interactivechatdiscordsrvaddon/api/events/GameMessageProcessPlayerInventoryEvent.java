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

package com.loohp.interactivechatdiscordsrvaddon.api.events;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.objectholders.ICPlayer;
import org.bukkit.inventory.Inventory;

/**
 * This event is called after the plugin translate a player inventory
 * placeholder. You can change the contents of the inventory/item in this event.
 *
 * @author LOOHP
 */
public class GameMessageProcessPlayerInventoryEvent extends GameMessageProcessInventoryEvent {

    public GameMessageProcessPlayerInventoryEvent(ICPlayer sender, String title, Component component, boolean cancel,
                                                  int processId, Inventory inventory) {
        super(sender, title, component, cancel, processId, inventory);
    }

}
