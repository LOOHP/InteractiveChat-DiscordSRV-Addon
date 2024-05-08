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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.EntityBlockStorage;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BundleUtils {

    public static int getContainerGridSizeX(int itemCount) {
        return Math.max(2, (int) Math.ceil(Math.sqrt((double) itemCount + 1.0)));
    }

    public static int getContainerGridSizeY(int itemCount) {
        return (int) Math.ceil(((double) itemCount + 1.0) / (double) getContainerGridSizeX(itemCount));
    }

    public static float getFullnessPercentage(List<ItemStack> items) {
        return (float) getContentWeight(items) / 64.0F;
    }

    public static int getFullness(List<ItemStack> items) {
        return getContentWeight(items);
    }

    private static int getWeight(ItemStack itemStack) {
        Material material = itemStack.getType();
        if (material.equals(Material.BUNDLE)) {
            return 4 + getContentWeight(((BundleMeta) itemStack.getItemMeta()).getItems());
        } else {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta instanceof BlockStateMeta) {
                BlockState blockState = ((BlockStateMeta) itemMeta).getBlockState();
                if (blockState instanceof EntityBlockStorage && ((EntityBlockStorage<?>) blockState).getEntityCount() > 0) {
                    return 64;
                }
            }
            return 64 / itemStack.getMaxStackSize();
        }
    }

    private static int getContentWeight(List<ItemStack> items) {
        return items.stream().mapToInt(each -> {
            return getWeight(each) * each.getAmount();
        }).sum();
    }

}
