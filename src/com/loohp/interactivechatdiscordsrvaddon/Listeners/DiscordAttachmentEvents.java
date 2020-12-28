package com.loohp.interactivechatdiscordsrvaddon.Listeners;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.API.Events.PrePacketComponentProcessEvent;
import com.loohp.interactivechat.Utils.CustomStringUtils;
import com.loohp.interactivechat.Utils.MCVersion;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.API.Events.DiscordAttachmentConversionEvent;
import com.loohp.interactivechatdiscordsrvaddon.Graphics.GifReader;
import com.loohp.interactivechatdiscordsrvaddon.Graphics.ImageFrame;
import com.loohp.interactivechatdiscordsrvaddon.Wrappers.GraphicsToPacketMapWrapper;

import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePostProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message.Attachment;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class DiscordAttachmentEvents implements Listener {
	
	public static final Pattern IMAGE_URL_PATTERN = Pattern.compile("https?:/(?:/[^/]+)+\\.(?:jpg|jpeg|gif|png)");
	public static final Map<String, DiscordAttachmentData> DATA = new ConcurrentHashMap<>();	
	public static final Map<Player, GraphicsToPacketMapWrapper> MAP_VIEWERS = new ConcurrentHashMap<>();
	
	@Subscribe(priority = ListenerPriority.MONITOR)
	public void onRecieveMessageFromDiscord(DiscordGuildMessagePostProcessEvent event) {
		if (InteractiveChatDiscordSrvAddon.plugin.convertDiscordAttachments) {
			Message message = event.getMessage();
			String processedMessage = event.getProcessedMessage();
			
			for (Attachment attachment : message.getAttachments()) {
				String url = attachment.getUrl();
				if (processedMessage.contains(url)) {
					if (attachment.isImage()) {
						try {
							InputStream stream = attachment.retrieveInputStream().get();
							GraphicsToPacketMapWrapper map;
							if (url.toLowerCase().endsWith(".gif")) {
								ImageFrame[] frames = GifReader.readGif(stream);
								stream.close();
								map = new GraphicsToPacketMapWrapper(frames);
							} else {
								BufferedImage image = ImageIO.read(stream);
								stream.close();
								map = new GraphicsToPacketMapWrapper(image);
							}
							DiscordAttachmentData data = new DiscordAttachmentData(attachment.getFileName(), url, map);
							DiscordAttachmentConversionEvent dace = new DiscordAttachmentConversionEvent(url, data);
							Bukkit.getPluginManager().callEvent(dace);
							DATA.put(url, data);
							Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> DATA.remove(url, data), InteractiveChatDiscordSrvAddon.plugin.discordAttachmentTimeout);
						} catch (IOException | InterruptedException | ExecutionException e) {
							e.printStackTrace();
							DiscordAttachmentData data = new DiscordAttachmentData(attachment.getFileName(), url);
							DiscordAttachmentConversionEvent dace = new DiscordAttachmentConversionEvent(url, data);
							Bukkit.getPluginManager().callEvent(dace);
							DATA.put(url, data);
							Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> DATA.remove(url, data), InteractiveChatDiscordSrvAddon.plugin.discordAttachmentTimeout);
						}
					} else {
						DiscordAttachmentData data = new DiscordAttachmentData(attachment.getFileName(), url);
						DiscordAttachmentConversionEvent dace = new DiscordAttachmentConversionEvent(url, data);
						Bukkit.getPluginManager().callEvent(dace);
						DATA.put(url, data);
						Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> DATA.remove(url, data), InteractiveChatDiscordSrvAddon.plugin.discordAttachmentTimeout);
					}
				}
			}
			
			Matcher matcher = IMAGE_URL_PATTERN.matcher(message.getContentRaw());
			while (matcher.find()) {
				String url = matcher.group();
				if (!DATA.containsKey(url)) {
					try {
						InputStream stream = new URL(url).openStream();
						GraphicsToPacketMapWrapper map;
						if (url.toLowerCase().endsWith(".gif")) {
							ImageFrame[] frames = GifReader.readGif(stream);
							stream.close();
							map = new GraphicsToPacketMapWrapper(frames);
						} else {
							BufferedImage image = ImageIO.read(stream);
							stream.close();
							map = new GraphicsToPacketMapWrapper(image);
						}
						String name = url.lastIndexOf("/") < 0 ? url : url.substring(url.lastIndexOf("/") + 1);
						DiscordAttachmentData data = new DiscordAttachmentData(name, url, map);
						DiscordAttachmentConversionEvent dace = new DiscordAttachmentConversionEvent(url, data);
						Bukkit.getPluginManager().callEvent(dace);
						DATA.put(url, data);
						Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> DATA.remove(url, data), InteractiveChatDiscordSrvAddon.plugin.discordAttachmentTimeout);
					} catch (IOException e) {}
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onChatPacket(PrePacketComponentProcessEvent event) {
		if (InteractiveChatDiscordSrvAddon.plugin.convertDiscordAttachments) {
			for (Entry<String, DiscordAttachmentData> entry : DATA.entrySet()) {
				String url = entry.getKey();
				BaseComponent baseComponent = event.getBaseComponent();
	
				List<BaseComponent> newlist = new ArrayList<>();
				for (BaseComponent each : CustomStringUtils.loadExtras(baseComponent)) {
					if (each instanceof TextComponent) {
						String text = ((TextComponent) each).getText();
						if (text.contains(url)) {
							((TextComponent) each).setText(text.substring(0, text.indexOf(url)));
							newlist.add(each);
							DiscordAttachmentData data = entry.getValue();
							String replacement = InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingText.replace("{FileName}", data.getFileName());
							TextComponent textComponent = new TextComponent(replacement);
							if (InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingHoverEnabled) {
								String hover = InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingHoverText.replace("{FileName}", data.getFileName());
								textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(hover)}));
							}
							TextComponent imageAppend = null;
							if (InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsUseMaps && data.isImage()) {
								textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/interactivechatdiscordsrv imagemap " + data.getUniqueId().toString()));
								imageAppend = new TextComponent(InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingImageAppend.replace("{FileName}", data.getFileName()));
								imageAppend.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[] {new TextComponent(InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingImageAppendHover.replace("{FileName}", data.getFileName()))}));
								imageAppend.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
							} else {
								textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
							}
							
							newlist.add(textComponent);
							if (imageAppend != null) {
								newlist.add(imageAppend);
							}
							
							TextComponent trailing = new TextComponent(text.substring(text.indexOf(url) + url.length(), text.length()));
							if (InteractiveChat.version.isLegacy() && !InteractiveChat.version.equals(MCVersion.V1_12)) {
								trailing = (TextComponent) CustomStringUtils.copyFormatting(trailing, each);
			 	        	} else {
			 	        		trailing.copyFormatting(each);
			 	        	}
							if (InteractiveChat.version.isPost1_16()) {
								trailing.setFont(each.getFont());
							}
							newlist.add(trailing);
						} else {
							newlist.add(each);
						}
					} else {
						newlist.add(each);
					}
				}
				
				TextComponent product = new TextComponent("");
				for (int i = 0; i < newlist.size(); i++) {
					BaseComponent each = newlist.get(i);
					product.addExtra(each);
				}
				
				event.setBaseComponent(product);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventory(InventoryOpenEvent event) {
		Player player = (Player) event.getPlayer();
		boolean removed = MAP_VIEWERS.remove(player) != null;
		
		if (removed) {
			player.getInventory().setItemInHand(player.getInventory().getItemInHand());
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (player.getGameMode().equals(GameMode.CREATIVE)) {
			Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> {
				boolean removed = MAP_VIEWERS.remove(player) != null;
				
				if (removed) {
					player.getInventory().setItemInHand(player.getInventory().getItemInHand());
				}
			}, 1);
		} else {
			boolean removed = MAP_VIEWERS.remove(player) != null;
		
			if (removed) {
				player.getInventory().setItemInHand(player.getInventory().getItemInHand());
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventory(InventoryCreativeEvent event) {
		Player player = (Player) event.getWhoClicked();
		boolean removed = MAP_VIEWERS.remove(player) != null;
		
		int slot = event.getSlot();
		
		if (removed) {
			if (player.getInventory().equals(event.getClickedInventory()) && slot >= 9) {
				ItemStack item = player.getInventory().getItem(slot);
				Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> player.getInventory().setItem(slot, item), 1);
			} else {
				event.setCursor(null);
			}
		}
		
		if (removed) {
			player.getInventory().setItemInHand(player.getInventory().getItemInHand());
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSlotChange(PlayerItemHeldEvent event) {
		if (event.getNewSlot() == event.getPreviousSlot()) {
			return;
		}
		
		Player player = event.getPlayer();
		boolean removed = MAP_VIEWERS.remove(player) != null;
		
		if (removed) {
			player.getInventory().setItemInHand(player.getInventory().getItemInHand());
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.PHYSICAL)) {
			return;
		}
		Player player = event.getPlayer();
		
		if (player.getGameMode().equals(GameMode.CREATIVE)) {
			Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> {
				boolean removed = MAP_VIEWERS.remove(player) != null;
				
				if (removed) {
					player.getInventory().setItemInHand(player.getInventory().getItemInHand());
				}
			}, 1);
		} else {
			boolean removed = MAP_VIEWERS.remove(player) != null;
			
			if (removed) {
				player.getInventory().setItemInHand(player.getInventory().getItemInHand());
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		Entity entity = event.getDamager();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			boolean removed = MAP_VIEWERS.remove(player) != null;
			
			if (removed) {
				player.getInventory().setItemInHand(player.getInventory().getItemInHand());
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		MAP_VIEWERS.remove(event.getPlayer());
	}
	
	public static class DiscordAttachmentData {
		
		private final String fileName;
		private final String url;
		private final GraphicsToPacketMapWrapper imageMap;
		private final UUID uuid;
		
		public DiscordAttachmentData(String fileName, String url, GraphicsToPacketMapWrapper imageMap) {
			this.fileName = fileName;
			this.url = url;
			this.imageMap = imageMap;
			this.uuid = UUID.randomUUID();
		}
		
		public DiscordAttachmentData(String fileName, String url) {
			this(fileName, url, null);
		}

		public String getFileName() {
			return fileName;
		}

		public String getUrl() {
			return url;
		}
		
		public boolean isImage() {
			return imageMap != null;
		}

		public GraphicsToPacketMapWrapper getImageMap() {
			return imageMap;
		}
		
		public UUID getUniqueId() {
			return uuid;
		}

		public int hashCode() {
			return 17 * uuid.hashCode();
		}
		
		public boolean equals(Object object) {
			if (object instanceof DiscordAttachmentData) {
				return ((DiscordAttachmentData) object).uuid.equals(this.uuid);
			}
			return false;
		}
		
	}

}
