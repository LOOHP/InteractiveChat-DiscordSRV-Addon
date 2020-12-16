package com.loohp.interactivechatdiscordsrvaddon.API.Events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class GameMessageProcessPlayerInventoryEvent extends GameMessageProcessInventoryEvent {
	
	/*
	 * This event is called after the plugin translate a player inventory placeholder.
	 * You can change the contents of the inventory/item in this event.
	 */

	public GameMessageProcessPlayerInventoryEvent(Player sender, String title, String message, boolean cancel, int processId, Inventory inventory) {
		super(sender, title, message, cancel, processId, inventory);
	}

}
