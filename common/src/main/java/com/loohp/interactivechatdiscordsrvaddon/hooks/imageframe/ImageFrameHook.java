/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
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

package com.loohp.interactivechatdiscordsrvaddon.hooks.imageframe;

import com.loohp.imageframe.ImageFrame;
import com.loohp.imageframe.nms.NMS;
import com.loohp.imageframe.objectholders.CombinedMapItemHandler;
import com.loohp.imageframe.objectholders.CombinedMapItemInfo;
import com.loohp.imageframe.objectholders.ImageMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ImageFrameHook {

    public static void notifyHDMapCleared(Player player, int mapId) {
        ImageFrame.customClientNetworkManager.notifyHdMapUpdated(Collections.singleton(player.getUniqueId()), Collections.emptySet(), Collections.singleton(mapId));
    }

    public static boolean isImageFrameCombinedImageItem(ItemStack itemStack) {
        return CombinedMapItemHandler.isCombinedMaps(itemStack);
    }

    public static BufferedImage getImageFrameCombinedImage(ItemStack itemStack) {
        if (!isImageFrameCombinedImageItem(itemStack)) {
            return null;
        }
        CombinedMapItemInfo info = NMS.getInstance().getCombinedMapItemInfo(itemStack);
        if (info == null) {
            return null;
        }
        ImageMap imageMap = ImageFrame.imageMapManager.getFromImageId(info.getImageMapIndex());
        if (imageMap == null) {
            return null;
        }
        List<BufferedImage> images = imageMap.getMapIds().stream().map(i -> imageMap.getOriginalImage(i)).collect(Collectors.toList());
        int sizePerImage = images.get(0).getWidth();
        BufferedImage wholeImage = new BufferedImage(sizePerImage * imageMap.getWidth(), sizePerImage * imageMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = wholeImage.createGraphics();
        for (int i = 0; i < images.size(); i++) {
            int x = i % imageMap.getWidth();
            int y = i / imageMap.getWidth();
            g.drawImage(images.get(i), x * sizePerImage, y * sizePerImage, null);
        }
        g.dispose();
        return wholeImage;
    }

}
