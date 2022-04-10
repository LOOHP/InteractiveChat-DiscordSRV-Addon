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

import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.utils.BookUtils;
import com.loohp.interactivechat.utils.FilledMapUtils;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.TitledInventoryWrapper;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ImageDisplayData extends DiscordDisplayData {

    private final String title;
    private final ImageDisplayType type;
    private final Optional<TitledInventoryWrapper> inventory;
    private final boolean isPlayerInventory;
    private final Optional<ItemStack> item;
    private final boolean isFilledMap;
    private final boolean isBook;

    private ImageDisplayData(OfflineICPlayer player, int position, String title, ImageDisplayType type, TitledInventoryWrapper inventory, boolean isPlayerInventory, ItemStack item, boolean isFilledMap, boolean isBook) {
        super(player, position);
        this.type = type;
        this.title = title;
        this.inventory = Optional.ofNullable(inventory);
        this.isPlayerInventory = isPlayerInventory;
        this.item = Optional.ofNullable(item);
        this.isFilledMap = isFilledMap;
        this.isBook = isBook;
    }

    public ImageDisplayData(OfflineICPlayer player, int position, String title, ImageDisplayType type, TitledInventoryWrapper inventory) {
        this(player, position, title, type, inventory, false, null, false, false);
    }

    public ImageDisplayData(OfflineICPlayer player, int position, String title, ImageDisplayType type, boolean isPlayerInventory, TitledInventoryWrapper inventory) {
        this(player, position, title, type, inventory, isPlayerInventory, null, false, false);
    }

    public ImageDisplayData(OfflineICPlayer player, int position, String title, ImageDisplayType type, ItemStack itemstack) {
        this(player, position, title, type, null, false, itemstack, FilledMapUtils.isFilledMap(itemstack), BookUtils.isTextBook(itemstack));
    }

    public ImageDisplayData(OfflineICPlayer player, int position, String title, ImageDisplayType type, ItemStack itemstack, TitledInventoryWrapper inventory) {
        this(player, position, title, type, inventory, false, itemstack, FilledMapUtils.isFilledMap(itemstack), BookUtils.isTextBook(itemstack));
    }

    public String getTitle() {
        return title;
    }

    public Optional<TitledInventoryWrapper> getInventory() {
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

    public boolean isBook() {
        return isBook;
    }

    public ImageDisplayType getType() {
        return type;
    }

}