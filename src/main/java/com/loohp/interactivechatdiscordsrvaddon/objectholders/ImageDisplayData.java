package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import java.util.Optional;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.loohp.interactivechat.utils.FilledMapUtils;

public class ImageDisplayData extends DiscordDisplayData {
	
	private final String title;
	private final ImageDisplayType type;
	private final Optional<Inventory> inventory;
	private final boolean isPlayerInventory;
	private final Optional<ItemStack> item;
	private final boolean isFilledMap;
	
	private ImageDisplayData(Player player, int position, String title, ImageDisplayType type, Inventory inventory, boolean isPlayerInventory, ItemStack item, boolean isFilledMap) {
		super(player, position);
		this.type = type;
		this.title = title;
		this.inventory = Optional.ofNullable(inventory);
		this.isPlayerInventory = isPlayerInventory;
		this.item = Optional.ofNullable(item);
		this.isFilledMap = isFilledMap;
	}
	
	public ImageDisplayData(Player player, int position, String title, ImageDisplayType type, Inventory inventory) {
		this(player, position, title, type, inventory, false, null, false);
	}
	
	public ImageDisplayData(Player player, int position, String title, ImageDisplayType type, boolean isPlayerInventory, Inventory inventory) {
		this(player, position, title, type, inventory, isPlayerInventory, null, false);
	}
	
	public ImageDisplayData(Player player, int position, String title, ImageDisplayType type, ItemStack itemstack) {
		this(player, position, title, type, null, false, itemstack, FilledMapUtils.isFilledMap(itemstack));
	}
	
	public ImageDisplayData(Player player, int position, String title, ImageDisplayType type, ItemStack itemstack, Inventory inventory) {
		this(player, position, title, type, inventory, false, itemstack, FilledMapUtils.isFilledMap(itemstack));
	}
	
	public String getTitle() {
		return title;
	}

	public Optional<Inventory> getInventory() {
		return inventory;
	}
	
	public boolean isPlayerInventory() {
		return isPlayerInventory;
	}

	public Optional<ItemStack> getItemStack() {
		return item;
	}
	
	public boolean isFilledMap() {
		return isFilledMap;
	}

	public ImageDisplayType getType() {
		return type;
	}
}