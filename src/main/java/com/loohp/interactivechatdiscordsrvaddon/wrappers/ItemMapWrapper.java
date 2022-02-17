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

package com.loohp.interactivechatdiscordsrvaddon.wrappers;

import com.loohp.interactivechat.utils.FilledMapUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapWrapper {

    private static final Comparator<MapCursor> ICON_ORDER;

    static {
        @SuppressWarnings("deprecation")
        Comparator<MapCursor> first = Comparator.comparing(each -> each.getRawType());
        Comparator<MapCursor> second = Collections.reverseOrder(Comparator.comparing(each -> each.getY()));
        Comparator<MapCursor> third = Collections.reverseOrder(Comparator.comparing(each -> each.getX()));
        ICON_ORDER = first.thenComparing(second).thenComparing(third);
    }

    private ItemStack itemStack;
    private byte[] colors;
    private List<MapCursor> icons;

    public ItemMapWrapper(ItemStack itemStack, Player player) throws Exception {
        this.itemStack = itemStack;
        this.icons = new ArrayList<>();
        update(player);
    }

    public void update(Player player) throws Exception {
        if (!FilledMapUtils.isFilledMap(itemStack)) {
            throw new IllegalArgumentException("Provided item is not a filled map");
        }
        MapView mapView = FilledMapUtils.getMapView(itemStack);
        colors = FilledMapUtils.getColors(mapView, player);
        icons = FilledMapUtils.getCursors(mapView, player).stream().sorted(ICON_ORDER).collect(Collectors.toList());
    }

    public byte[] getColors() {
        return colors;
    }

    public List<MapCursor> getMapCursors() {
        return Collections.unmodifiableList(icons);
    }

}
