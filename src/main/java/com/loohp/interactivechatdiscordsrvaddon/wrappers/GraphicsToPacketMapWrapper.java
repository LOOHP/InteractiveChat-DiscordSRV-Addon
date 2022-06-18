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

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NMSUtils;
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
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("deprecation")
public class GraphicsToPacketMapWrapper {

    public static final short MAP_ID = Short.MAX_VALUE;

    private static Class<?> nmsMapIconClass;
    private static Class<?> nmsWorldMapClass;
    private static Class<?> nmsWorldMapBClass;
    private static Constructor<?> nmsWorldMapBClassConstructor;

    static {
        try {
            nmsMapIconClass = NMSUtils.getNMSClass("net.minecraft.server.%s.MapIcon", "net.minecraft.world.level.saveddata.maps.MapIcon");
            nmsWorldMapClass = NMSUtils.getNMSClass("net.minecraft.server.%s.WorldMap", "net.minecraft.world.level.saveddata.maps.WorldMap");

            if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_17)) {
                //noinspection OptionalGetWithoutIsPresent
                nmsWorldMapBClass = Arrays.stream(nmsWorldMapClass.getClasses()).filter(each -> each.getName().endsWith("$b")).findFirst().get();
                nmsWorldMapBClassConstructor = nmsWorldMapBClass.getConstructor(int.class, int.class, int.class, int.class, byte[].class);
            }
        } catch (Exception e) {
        }
    }

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

    public void show(Player player) {
        if (!done) {
            throw new IllegalStateException("Future has not complete!");
        }
        if (colors == null || colors.isEmpty()) {
            return;
        }
        InteractiveChatDiscordSrvAddon.plugin.imagesViewedCounter.incrementAndGet();
        InboundToGameEvents.MAP_VIEWERS.put(player, this);

        ProtocolManager protocollib = ProtocolLibrary.getProtocolManager();
        PacketContainer packet1;
        if (InteractiveChat.version.isOld()) {
            packet1 = protocollib.createPacket(PacketType.Play.Server.SET_SLOT);
            packet1.getIntegers().write(0, 0);
            packet1.getIntegers().write(1, player.getInventory().getHeldItemSlot() + 36);
            packet1.getItemModifier().write(0, mapItem);
        } else {
            packet1 = protocollib.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            packet1.getIntegers().write(0, player.getEntityId());
            if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16)) {
                List<Pair<ItemSlot, ItemStack>> list = new ArrayList<>();
                list.add(new Pair<>(ItemSlot.MAINHAND, mapItem));
                packet1.getSlotStackPairLists().write(0, list);
            } else {
                packet1.getItemSlots().write(0, ItemSlot.MAINHAND);
                packet1.getItemModifier().write(0, mapItem);
            }
        }

        PacketContainer packet2 = protocollib.createPacket(PacketType.Play.Server.MAP);
        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_17)) {
            packet2.getIntegers().write(0, (int) MAP_ID);
            packet2.getBytes().write(0, (byte) 0);
            packet2.getBooleans().write(0, false);
        } else {
            int mapIconFieldPos = 2;
            packet2.getIntegers().write(0, (int) MAP_ID);
            packet2.getBytes().write(0, (byte) 0);
            if (!InteractiveChat.version.isOld()) {
                packet2.getBooleans().write(0, false);
                mapIconFieldPos++;
            }
            if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_14)) {
                packet2.getBooleans().write(1, false);
                mapIconFieldPos++;
            }
            packet2.getModifier().write(mapIconFieldPos, Array.newInstance(nmsMapIconClass, 0));
            packet2.getIntegers().write(1, 0);
            packet2.getIntegers().write(2, 0);
            packet2.getIntegers().write(3, 128);
            packet2.getIntegers().write(4, 128);
        }

        protocollib.sendServerPacket(player, packet1);

        GraphicsToPacketMapWrapper ref = this;
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                GraphicsToPacketMapWrapper wrapper = InboundToGameEvents.MAP_VIEWERS.get(player);
                if (wrapper != null && wrapper.equals(ref)) {
                    try {
                        byte[] colorArray = colors.get(index);
                        if (colorArray == null) {
                            throw new IllegalStateException("Color Array at index " + index + " is null!");
                        }
                        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_17)) {
                            packet2.getModifier().write(4, nmsWorldMapBClassConstructor.newInstance(0, 0, 128, 128, colorArray));
                        } else {
                            packet2.getByteArrays().write(0, colorArray);
                        }
                        protocollib.sendServerPacket(player, packet2);
                    } catch (InvocationTargetException | FieldAccessException | InstantiationException |
                             IllegalAccessException | IllegalArgumentException e) {
                        e.printStackTrace();
                    }
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
