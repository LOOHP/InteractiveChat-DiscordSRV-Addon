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

import com.loohp.interactivechat.libs.org.apache.commons.lang3.math.Fraction;
import com.loohp.interactivechatdiscordsrvaddon.nms.NMSAddon;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BundleUtils {

    public static int getLegacyContainerGridSizeX(int itemCount) {
        return Math.max(2, (int) Math.ceil(Math.sqrt((double) itemCount + 1.0)));
    }

    public static int getLegacyContainerGridSizeY(int itemCount) {
        return (int) Math.ceil(((double) itemCount + 1.0) / (double) getLegacyContainerGridSizeX(itemCount));
    }

    public static Fraction getWeight(List<ItemStack> items) {
        Fraction fraction = Fraction.ZERO;
        for (ItemStack itemStack : items) {
            fraction = fraction.add(getWeight(itemStack).multiplyBy(Fraction.getFraction(itemStack.getAmount(), 1)));
        }
        return fraction;
    }

    public static Fraction getWeight(ItemStack itemStack) {
        return NMSAddon.getInstance().getWeightForBundle(itemStack);
    }

}
