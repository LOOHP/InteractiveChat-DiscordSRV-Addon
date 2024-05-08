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

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.nms.NMS;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageFrame;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapPalette;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class GraphicsToPacketMapWrapper {

    public static final short MAP_ID = Short.MAX_VALUE;

    private volatile boolean done;
    private List<ImageFrame> frames;
    private List<byte[]> colors;
    private ItemStack mapItem;
    private int totalTime;
    private boolean playbackBar;
    private Color backgroundColor;

    public GraphicsToPacketMapWrapper(List<ImageFrame> frames, boolean playbackBar, Color backgroundColor) {
        this.done = true;
        this.frames = frames;
        this.playbackBar = playbackBar;
        this.backgroundColor = backgroundColor;
        update();
    }

    public GraphicsToPacketMapWrapper(BufferedImage image, Color backgroundColor) {
        this(Collections.singletonList(new ImageFrame(image)), false, backgroundColor);
    }

    public GraphicsToPacketMapWrapper(boolean playbackBar, Color backgroundColor) {
        this.done = false;
        this.frames = null;
        this.playbackBar = playbackBar;
        this.backgroundColor = backgroundColor;
    }

    public boolean futureCompleted() {
        return done;
    }

    public boolean futureCancelled() {
        return done && frames == null;
    }

    public synchronized void completeFuture(List<ImageFrame> frames) {
        if (done) {
            return;
        }
        this.frames = frames;
        this.done = true;
        update();
    }

    public void update() {
        if (!done) {
            throw new IllegalStateException("Future has not complete!");
        }
        this.colors = new ArrayList<>();
        this.mapItem = XMaterial.FILLED_MAP.parseItem();
        if (InteractiveChat.version.isLegacy()) {
            mapItem.setDurability(MAP_ID);
        } else {
            MapMeta meta = (MapMeta) mapItem.getItemMeta();
            meta.setMapId(MAP_ID);
            mapItem.setItemMeta(meta);
        }
        int totalTime = 0;
        for (ImageFrame frame : frames) {
            totalTime += frame.getDelay();
        }
        this.totalTime = totalTime;
        for (int currentTime = 0; currentTime <= totalTime; currentTime += 50) {
            int currentFrame = getFrameAt(currentTime);
            if (currentFrame < 0) {
                break;
            }
            BufferedImage processedFrame = ImageUtils.resizeImageQuality(ImageUtils.squarify(frames.get(currentFrame).getImage()), 128, 128);
            if (playbackBar) {
                Graphics2D g = processedFrame.createGraphics();
                g.setColor(InteractiveChatDiscordSrvAddon.plugin.playbackBarEmptyColor);
                g.fillRect(0, 126, 128, 2);
                g.setColor(InteractiveChatDiscordSrvAddon.plugin.playbackBarFilledColor);
                g.fillRect(0, 126, (int) (((double) currentTime / (double) totalTime) * 128), 2);
                g.dispose();
            }
            if (backgroundColor != null) {
                BufferedImage background = new BufferedImage(processedFrame.getWidth(), processedFrame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = background.createGraphics();
                g.setColor(backgroundColor);
                g.fillRect(0, 0, background.getWidth(), background.getHeight());
                g.drawImage(processedFrame, 0, 0, null);
                g.dispose();
                processedFrame = background;
            }
            this.colors.add(MapPalette.imageToBytes(processedFrame));
        }
    }

    public List<ImageFrame> getImageFrame() {
        return frames;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getFrameAt(int ms) {
        int current = 0;
        int i = 0;
        for (ImageFrame frame : frames) {
            current += frame.getDelay();
            if (current >= ms) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public List<byte[]> getColors() {
        return colors;
    }

    public void show(Player player) {
        if (!done) {
            throw new IllegalStateException("Future has not complete!");
        }
        InteractiveChatDiscordSrvAddon.plugin.imagesViewedCounter.incrementAndGet();
        InboundToGameEvents.MAP_VIEWERS.put(player, this);

        NMS.getInstance().sendFakeMainHandSlot(player, mapItem);

        GraphicsToPacketMapWrapper ref = this;
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                GraphicsToPacketMapWrapper wrapper = InboundToGameEvents.MAP_VIEWERS.get(player);
                if (wrapper != null && wrapper.equals(ref)) {
                    byte[] colorArray = colors.get(index);
                    NMS.getInstance().sendFakeMapUpdate(player, MAP_ID, Collections.emptyList(), colorArray);
                } else {
                    this.cancel();
                }
                if (++index >= colors.size()) {
                    index = 0;
                }
            }
        }.runTaskTimer(InteractiveChatDiscordSrvAddon.plugin, 0, 1);
    }

}
