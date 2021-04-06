package com.loohp.interactivechatdiscordsrvaddon.api.events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * This event is called after the plugin translate a player inventory
 * placeholder. You can change the contents of the inventory/item in this event.
 * @author LOOHP
 *
 */
public class GameMessageProcessPlayerInventoryEvent extends GameMessageProcessInventoryEvent {

	public GameMessageProcessPlayerInventoryEvent(Player sender, String title, String message, boolean cancel,
			int processId, Inventory inventory) {
		super(sender, title, message, cancel, processId, inventory);
	}

}
