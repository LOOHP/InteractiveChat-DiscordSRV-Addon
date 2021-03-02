package com.loohp.interactivechatdiscordsrvaddon.API.Events;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * This event is called after the plugin translate a normal inventory
 * placeholder. (Currently only used by the enderchest) You can change the
 * contents of the inventory/item in this event.
 * 
 * For player inventories, please use GameMessageProcessPlayerInventoryEvent
 * @author LOOHP
 *
 */
public class GameMessageProcessInventoryEvent extends GameMessageProcessEvent {

	private Inventory inventory;

	public GameMessageProcessInventoryEvent(Player sender, String title, String message, boolean cancel, int processId,
			Inventory inventory) {
		super(sender, title, message, cancel, processId);
		this.inventory = inventory;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

}
