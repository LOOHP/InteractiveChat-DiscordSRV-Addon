package com.loohp.interactivechatdiscordsrvaddon.Wrappers;

import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapPalette;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import com.comphenix.protocol.wrappers.Pair;
import com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.Utils.MCVersion;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.Graphics.ImageFrame;
import com.loohp.interactivechatdiscordsrvaddon.Graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.Listeners.DiscordAttachmentEvents;

@SuppressWarnings("deprecation")
public class GraphicsToPacketMapWrapper {
	
	private static Class<?> nmsMapIconClass;
	
	static {
		try {
			nmsMapIconClass = getNMSClass("net.minecraft.server.", "MapIcon");
		} catch (Exception e) {}
	}
	
	private static Class<?> getNMSClass(String prefix, String nmsClassString) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        String name = prefix + version + nmsClassString;
        return Class.forName(name);
    }
	
	private ImageFrame[] frames;
	private byte[][] colors;
	private ItemStack mapItem;
	private short mapId = Short.MAX_VALUE;
	private int totalTime;
	
	public GraphicsToPacketMapWrapper(ImageFrame[] frames) {
		this.frames = frames;
		update();
	}
	
	public GraphicsToPacketMapWrapper(BufferedImage image) {
		this.frames = new ImageFrame[] {new ImageFrame(image)};
		update();
	}
	
	public void update() {
		this.colors = new byte[frames.length][];
		this.mapItem = XMaterial.FILLED_MAP.parseItem();
		if (InteractiveChat.version.isLegacy()) {
			mapItem.setDurability(mapId);
		} else {
			MapMeta meta = (MapMeta) mapItem.getItemMeta();
			meta.setMapId(mapId);
			mapItem.setItemMeta(meta);
		}
		int totalTime = 0;
		for (int i = 0; i < frames.length; i++) {
			ImageFrame frame = frames[i];
			this.colors[i] = MapPalette.imageToBytes(ImageUtils.resizeImageQuality(ImageUtils.squarify(frame.getImage()), 128, 128));
			totalTime += frame.getDelay() * 10;
		}
		this.totalTime = totalTime;
	}
	
	public ImageFrame[] getImageFrame() {
		return frames;
	}
	
	public int getTotalTime() {
		return totalTime;
	}
	
	public int getFrameAt(int ms) {
		int current = 0;
		for (int i = 0; i < frames.length; i++) {
			ImageFrame frame = frames[i];
			current += frame.getDelay() * 10;
			if (current >= ms) {
				return i;
			}
		}
		return -1;
	}
	
	public void show(Player player) {
		InteractiveChatDiscordSrvAddon.plugin.imagesViewedCounter.incrementAndGet();
		DiscordAttachmentEvents.MAP_VIEWERS.put(player, this);
		
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
				list.add(new Pair<ItemSlot, ItemStack>(ItemSlot.MAINHAND, mapItem));
				packet1.getSlotStackPairLists().write(0, list);
			} else {
				packet1.getItemSlots().write(0, ItemSlot.MAINHAND);
				packet1.getItemModifier().write(0, mapItem);
			}
		}
		
		PacketContainer packet2 = protocollib.createPacket(PacketType.Play.Server.MAP);
		int mapIconFieldPos = 2;
		packet2.getIntegers().write(0, (int) mapId);
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
		
		try {
			protocollib.sendServerPacket(player, packet1);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		GraphicsToPacketMapWrapper ref = this;
		new BukkitRunnable() {
			int frameTime = 0;
			@Override
			public void run() {
				GraphicsToPacketMapWrapper wrapper = DiscordAttachmentEvents.MAP_VIEWERS.get(player);
				if (wrapper != null && wrapper.equals(ref)) {
					int current = getFrameAt(frameTime);
					if (current < 0) {
						current = 0;
						frameTime = 0;
					}
					try {
						packet2.getByteArrays().write(0, colors[current]);
						protocollib.sendServerPacket(player, packet2);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				} else {
					this.cancel();
				}
				frameTime += 50;
			}
		}.runTaskTimer(InteractiveChatDiscordSrvAddon.plugin, 0, 1);
	}

}
