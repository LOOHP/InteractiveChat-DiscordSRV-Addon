package com.loohp.interactivechatdiscordsrvaddon.listeners;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

import com.loohp.interactivechat.api.events.PrePacketComponentProcessEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TextReplacementConfig;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.ClickEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.api.events.DiscordAttachmentConversionEvent;
import com.loohp.interactivechatdiscordsrvaddon.debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.graphics.GifReader;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageFrame;
import com.loohp.interactivechatdiscordsrvaddon.modules.DiscordToGameMention;
import com.loohp.interactivechatdiscordsrvaddon.utils.ThrowingSupplier;
import com.loohp.interactivechatdiscordsrvaddon.utils.URLRequestUtils;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.GraphicsToPacketMapWrapper;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePostProcessEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message.Attachment;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.MessageUtil;

public class InboundToGameEvents implements Listener {
	
	public static final Map<UUID, DiscordAttachmentData> DATA = new ConcurrentHashMap<>();	
	public static final Map<Player, GraphicsToPacketMapWrapper> MAP_VIEWERS = new ConcurrentHashMap<>();
	
	private static Field discordRegexesField;
	
	protected static void ready(DiscordSRV srv) {
		try {
			discordRegexesField = srv.getClass().getDeclaredField("discordRegexes");
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}
	
	@Subscribe(priority = ListenerPriority.LOWEST)
	public void onRecieveMessageFromDiscordPre(DiscordGuildMessagePreProcessEvent event) {
		Debug.debug("Triggering onRecieveMessageFromDiscordPre");
		DiscordSRV srv = InteractiveChatDiscordSrvAddon.discordsrv;
		try {
			discordRegexesField.setAccessible(true);
			@SuppressWarnings("unchecked")
			Map<Pattern, String> discordRegexes = (Map<Pattern, String>) discordRegexesField.get(srv);
			Iterator<Pattern> itr = discordRegexes.keySet().iterator();
			while (itr.hasNext()) {
				Pattern pattern = itr.next();
				if (pattern.pattern().equals("@+(everyone|here)")) {
					itr.remove();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Subscribe(priority = ListenerPriority.HIGH)
	public void onRecieveMessageFromDiscordPost(DiscordGuildMessagePostProcessEvent event) {
		Debug.debug("Triggering onRecieveMessageFromDiscordPost");
		Message message = event.getMessage();
		
		github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component component = event.getMinecraftMessage();
		
		DiscordSRV srv = InteractiveChatDiscordSrvAddon.discordsrv;
		User author = message.getAuthor();
		
		if (InteractiveChatDiscordSrvAddon.plugin.translateMentions) {
			Debug.debug("onRecieveMessageFromDiscordPost translating mentions");
			
			Set<UUID> mentionTitleSent = new HashSet<>();
			Map<Member, UUID> channelMembers = new HashMap<>();
			
			TextChannel channel = event.getChannel();
			Guild guild = channel.getGuild();
			Member authorAsMember = guild.getMember(author);
			String senderDiscordName = authorAsMember == null ? author.getName() : authorAsMember.getEffectiveName();
			UUID senderUUID = srv.getAccountLinkManager().getUuid(author.getId());
			
			for (Entry<UUID, String> entry : srv.getAccountLinkManager().getManyDiscordIds(Bukkit.getOnlinePlayers().stream().map(each -> each.getUniqueId()).collect(Collectors.toSet())).entrySet()) {
				Member member = guild.getMemberById(entry.getValue());
				if (member != null && member.hasAccess(channel)) {
					channelMembers.put(member, entry.getKey());
				}
			}
			
			if (message.mentionsEveryone()) {
				//github.scarsz.discordsrv.dependencies.kyori.adventure.text.event.HoverEvent<Component> hover = Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHover.replace("{DiscordUser}", senderDiscordName).replace("{TextChannel}", "#" + channel.getName()).replace("{Guild}", guild.getName())).asHoverEvent();
				component = component.replaceText(github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig.builder().matchLiteral("@here").replacement(github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHighlight.replace("{DiscordMention}", "@here"))).build()).replaceText(github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig.builder().matchLiteral("@everyone").replacement(github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHighlight.replace("{DiscordMention}", "@everyone"))).build());
				for (UUID uuid : channelMembers.values()) {
					mentionTitleSent.add(uuid);
					Player player = Bukkit.getPlayer(uuid);
					if (player != null) {
						DiscordToGameMention.playTitleScreen(senderDiscordName, channel.getName(), guild.getName(), player);
					}
				}
			}
			
			List<Role> mentionedRoles = message.getMentionedRoles();
			for (Role role : mentionedRoles) {
				//github.scarsz.discordsrv.dependencies.kyori.adventure.text.event.HoverEvent<Component> hover = Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHover.replace("{DiscordUser}", senderDiscordName).replace("{TextChannel}", "#" + channel.getName()).replace("{Guild}", guild.getName())).asHoverEvent();
				component = component.replaceText(github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig.builder().matchLiteral("@" + role.getName()).replacement(github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHighlight.replace("{DiscordMention}", "@" + role.getName()))).build());
				for (Entry<Member, UUID> entry : channelMembers.entrySet()) {
					UUID uuid = entry.getValue();
					if (!mentionTitleSent.contains(uuid) && entry.getKey().getRoles().contains(role)) {
						mentionTitleSent.add(uuid);
						Player player = Bukkit.getPlayer(uuid);
						if (player != null) {
							DiscordToGameMention.playTitleScreen(senderDiscordName, channel.getName(), guild.getName(), player);
						}
					}
				}
			}
			
			List<User> mentionedUsers = message.getMentionedUsers();
			if (!mentionedUsers.isEmpty()) {
				for (User user : mentionedUsers) {
					//github.scarsz.discordsrv.dependencies.kyori.adventure.text.event.HoverEvent<Component> hover = Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHover.replace("{DiscordUser}", senderDiscordName).replace("{TextChannel}", "#" + channel.getName()).replace("{Guild}", guild.getName())).asHoverEvent();
					component = component.replaceText(github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig.builder().matchLiteral("@" + user.getName()).replacement(github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHighlight.replace("{DiscordMention}", "@" + user.getName()))).build());
					Member member = guild.getMember(user);
					if (member != null) {
						UUID uuid = channelMembers.get(member);
						if (uuid != null && !mentionTitleSent.contains(uuid) && (senderUUID == null || !senderUUID.equals(uuid))) {
							mentionTitleSent.add(uuid);
							Player player = Bukkit.getPlayer(uuid);
							if (player != null) {
								DiscordToGameMention.playTitleScreen(senderDiscordName, channel.getName(), guild.getName(), player);
							}
						}
					}
				}
			}
			
			event.setMinecraftMessage(component);
		}
		
		String processedMessage = MessageUtil.toLegacy(component);
		
		if (InteractiveChatDiscordSrvAddon.plugin.convertDiscordAttachments) {
			Debug.debug("onRecieveMessageFromDiscordPost converting discord attachments");
			Set<String> processedUrl = new HashSet<>();
			for (Attachment attachment : message.getAttachments()) {
				InteractiveChatDiscordSrvAddon.plugin.attachmentCounter.incrementAndGet();
				String url = attachment.getUrl();
				if (processedMessage.contains(url)) {
					processedUrl.add(url);
					if (attachment.isImage()) {
						InteractiveChatDiscordSrvAddon.plugin.attachmentImageCounter.incrementAndGet();
						List<ThrowingSupplier<InputStream>> methods = new ArrayList<>();
						methods.add(() -> attachment.retrieveInputStream().get());
						if (URLRequestUtils.isAllowed(attachment.getUrl())) {
							methods.add(() -> URLRequestUtils.getInputStream0(attachment.getUrl()));
						}
						if (URLRequestUtils.isAllowed(attachment.getProxyUrl())) {
							methods.add(() -> URLRequestUtils.getInputStream0(attachment.getProxyUrl()));
						}
						
						try (InputStream stream = URLRequestUtils.retrieveInputStreamUntilSuccessful(methods)) {
							GraphicsToPacketMapWrapper map;
							if (url.toLowerCase().endsWith(".gif")) {
								ImageFrame[] frames = GifReader.readGif(stream);
								map = new GraphicsToPacketMapWrapper(frames, InteractiveChatDiscordSrvAddon.plugin.playbackBarEnabled);
							} else {
								BufferedImage image = ImageIO.read(stream);
								map = new GraphicsToPacketMapWrapper(image);
							}
							DiscordAttachmentData data = new DiscordAttachmentData(attachment.getFileName(), url, map);
							DiscordAttachmentConversionEvent dace = new DiscordAttachmentConversionEvent(url, data);
							Bukkit.getPluginManager().callEvent(dace);
							DATA.put(data.getUniqueId(), data);
							Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> DATA.remove(data.getUniqueId()), InteractiveChatDiscordSrvAddon.plugin.discordAttachmentTimeout);
						} catch (IOException e) {
							e.printStackTrace();
							DiscordAttachmentData data = new DiscordAttachmentData(attachment.getFileName(), url);
							DiscordAttachmentConversionEvent dace = new DiscordAttachmentConversionEvent(url, data);
							Bukkit.getPluginManager().callEvent(dace);
							DATA.put(data.getUniqueId(), data);
							Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> DATA.remove(data.getUniqueId()), InteractiveChatDiscordSrvAddon.plugin.discordAttachmentTimeout);
						}
					} else {
						DiscordAttachmentData data = new DiscordAttachmentData(attachment.getFileName(), url);
						DiscordAttachmentConversionEvent dace = new DiscordAttachmentConversionEvent(url, data);
						Bukkit.getPluginManager().callEvent(dace);
						DATA.put(data.getUniqueId(), data);
						Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> DATA.remove(data.getUniqueId()), InteractiveChatDiscordSrvAddon.plugin.discordAttachmentTimeout);
					}
				}
			}
			
			Matcher matcher = URLRequestUtils.IMAGE_URL_PATTERN.matcher(message.getContentRaw());
			while (matcher.find()) {
				String url = matcher.group();
				if (!processedUrl.contains(url) && URLRequestUtils.isAllowed(url)) {
					InteractiveChatDiscordSrvAddon.plugin.attachmentImageCounter.incrementAndGet();
					try (InputStream stream = URLRequestUtils.getInputStream(url)) {
						GraphicsToPacketMapWrapper map;
						if (url.toLowerCase().endsWith(".gif")) {
							ImageFrame[] frames = GifReader.readGif(stream);
							map = new GraphicsToPacketMapWrapper(frames, InteractiveChatDiscordSrvAddon.plugin.playbackBarEnabled);
						} else {
							BufferedImage image = ImageIO.read(stream);
							map = new GraphicsToPacketMapWrapper(image);
						}
						String name = url.lastIndexOf("/") < 0 ? url : url.substring(url.lastIndexOf("/") + 1);
						DiscordAttachmentData data = new DiscordAttachmentData(name, url, map);
						DiscordAttachmentConversionEvent dace = new DiscordAttachmentConversionEvent(url, data);
						Bukkit.getPluginManager().callEvent(dace);
						DATA.put(data.getUniqueId(), data);
						Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> DATA.remove(data.getUniqueId()), InteractiveChatDiscordSrvAddon.plugin.discordAttachmentTimeout);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onChatPacket(PrePacketComponentProcessEvent event) {
		Debug.debug("Trggering onChatPacket");
		if (InteractiveChatDiscordSrvAddon.plugin.convertDiscordAttachments) {
			Debug.debug("onChatPacket converting discord attachments");
			for (Entry<UUID, DiscordAttachmentData> entry : DATA.entrySet()) {
				DiscordAttachmentData data = entry.getValue();
				String url = data.getUrl();
				Component component = event.getComponent();
				
				String replacement = InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingText.replace("{FileName}", data.getFileName());
				Component textComponent = LegacyComponentSerializer.legacySection().deserialize(replacement);
				if (InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingHoverEnabled) {
					String hover = InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingHoverText.replace("{FileName}", data.getFileName());
					textComponent = textComponent.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(hover)));
				}
				if (InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsUseMaps && data.isImage()) {
					textComponent = textComponent.clickEvent(ClickEvent.runCommand("/interactivechatdiscordsrv imagemap " + data.getUniqueId().toString()));
					Component imageAppend = LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingImageAppend.replace("{FileName}", data.getFileName()));
					imageAppend = imageAppend.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingImageAppendHover.replace("{FileName}", data.getFileName()))));
					imageAppend = imageAppend.clickEvent(ClickEvent.openUrl(url));
					textComponent = textComponent.append(imageAppend);
				} else {
					textComponent = textComponent.clickEvent(ClickEvent.openUrl(url));
				}
				
				component = component.replaceText(TextReplacementConfig.builder().matchLiteral(url).replacement(textComponent).build());
				
				event.setComponent(component);
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
