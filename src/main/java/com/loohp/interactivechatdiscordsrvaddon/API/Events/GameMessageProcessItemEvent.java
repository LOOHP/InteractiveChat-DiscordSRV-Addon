package com.loohp.interactivechatdiscordsrvaddon.API.Events;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * This event is called after the plugin translate an item placeholder.<br>
 * hasInventory() is true if this item is a container (like a shulker box)<br>
 * getInventory() is null if hasInventory() is false<br>
 * setInventory() accepts null as value<br>
 * You can change the contents of the inventory/item in this event.
 * @author LOOHP
 *
 */
public class GameMessageProcessItemEvent extends GameMessageProcessEvent {

	private ItemStack itemstack;
	private Optional<Inventory> inventory;
	
	public GameMessageProcessItemEvent(Player sender, String title, String message, boolean cancel, int processId, ItemStack itemstack, Inventory inventory) {
		super(sender, title, message, cancel, processId);
		this.itemstack = itemstack;
		this.inventory = Optional.ofNullable(inventory);
	}
	
	public GameMessageProcessItemEvent(Player sender, String title, String message, boolean cancel, int processId, ItemStack itemstack) {
		this(sender, title, message, cancel, processId, itemstack, null);
	}
	
	public ItemStack getItemStack() {
		return itemstack;
	}
	
	public void setItemStack(ItemStack itemstack) {
		this.itemstack = itemstack;
	}
	
	public boolean hasInventory() {
		return inventory.isPresent();
	}
	
	public Inventory getInventory() {
		return inventory.orElse(null);
	}
	
	public void setInventory(Inventory inventory) {
		this.inventory = Optional.ofNullable(inventory);
	}

}
