package com.loohp.interactivechatdiscordsrvaddon.listeners;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import com.loohp.interactivechat.ConfigManager;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.Utils.XMaterial;
import com.loohp.interactivechat.api.InteractiveChatAPI;
import com.loohp.interactivechat.objectholders.CustomPlaceholder;
import com.loohp.interactivechat.objectholders.ICPlaceholder;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.MentionPair;
import com.loohp.interactivechat.objectholders.WebData;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.CustomStringUtils;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechat.utils.NBTUtils;
import com.loohp.interactivechat.utils.PlaceholderParser;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.api.events.DiscordImageEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessagePostProcessEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessagePreProcessEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessageProcessInventoryEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessageProcessItemEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessageProcessPlayerInventoryEvent;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.HoverClickDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.IDProvider;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayType;
import com.loohp.interactivechatdiscordsrvaddon.registies.DiscordDataRegistry;
import com.loohp.interactivechatdiscordsrvaddon.utils.ColorUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordItemStackUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordItemStackUtils.DiscordDescription;
import com.loohp.interactivechatdiscordsrvaddon.utils.TranslationUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.URLRequestUtils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePostProcessEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageSentEvent;
import github.scarsz.discordsrv.api.events.GameChatMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.ChannelType;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.MessageUtil;
import github.scarsz.discordsrv.util.PlaceholderUtil;
import github.scarsz.discordsrv.util.WebhookUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class OutboundToDiscordEvents {
	
	public static final Comparator<DiscordDisplayData> DISPLAY_DATA_COMPARATOR = Comparator.comparing(each -> each.getPosition());
	private static final IDProvider DATA_ID_PROVIDER = new IDProvider();
	public static final Map<Integer, DiscordDisplayData> DATA = Collections.synchronizedMap(new LinkedHashMap<>());
	
	@Subscribe(priority = ListenerPriority.LOW)
	public void onDiscordToGame(DiscordGuildMessagePostProcessEvent event) {
		InteractiveChatDiscordSrvAddon.plugin.messagesCounter.incrementAndGet();
		Component component = event.getMinecraftMessage();
		if (InteractiveChatDiscordSrvAddon.plugin.escapePlaceholdersFromDiscord) {
			for (ICPlaceholder placeholder : InteractiveChat.placeholderList) {
				component = component.replaceText(TextReplacementConfig.builder().matchLiteral(placeholder.getKeyword()).replacement("\\" + placeholder.getKeyword()).build());
			}
			event.setMinecraftMessage(component);
		}
	}
	
	@SuppressWarnings("deprecation")
	@Subscribe(priority = ListenerPriority.HIGHEST)
	public void onGameToDiscord(GameChatMessagePreProcessEvent event) {
		InteractiveChatDiscordSrvAddon.plugin.messagesCounter.incrementAndGet();
		Player sender = event.getPlayer();
		ICPlayer wrappedSender = new ICPlayer(sender);
		String message = event.getMessage();
		String originalMessage = message;
		long now = System.currentTimeMillis();
		long uniCooldown = InteractiveChatAPI.getPlayerUniversalCooldown(sender) - now;
		
		if (!(uniCooldown < 0 || uniCooldown + 100 > InteractiveChat.universalCooldown)) {
			return;
		}

		GameMessagePreProcessEvent gameMessagePreProcessEvent = new GameMessagePreProcessEvent(sender, message, false);
		Bukkit.getPluginManager().callEvent(gameMessagePreProcessEvent);
		if (gameMessagePreProcessEvent.isCancelled()) {
			return;
		}
		message = gameMessagePreProcessEvent.getMessage();

		if (InteractiveChat.useItem && sender.hasPermission("interactivechat.module.item")) {
			long cooldown = InteractiveChatAPI.getPlayerPlaceholderCooldown(sender, InteractiveChat.itemPlaceholder) - now;
			if (cooldown < 0 || cooldown + 100 > ConfigManager.getConfig().getLong("ItemDisplay.Item.Cooldown") * 1000) {
				String placeholder = InteractiveChat.itemPlaceholder;
				int index = InteractiveChat.itemCaseSensitive ? message.indexOf(placeholder) : message.toLowerCase().indexOf(placeholder.toLowerCase());
				if (index >= 0 && !((index > 0 && message.charAt(index - 1) == '\\') && (index < 2 || message.charAt(index - 2) != '\\'))) {
					ItemStack item = sender.getEquipment().getItemInHand();
					if (item == null) {
						item = new ItemStack(Material.AIR);
					}
					XMaterial xMaterial = XMaterial.matchXMaterial(item);
					String itemStr;
					if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && !item.getItemMeta().getDisplayName().equals("")) {
						itemStr = item.getItemMeta().getDisplayName();
					} else {
						String itemKey = LanguageUtils.getTranslationKey(item);
						itemStr = LanguageUtils.getTranslation(itemKey, InteractiveChatDiscordSrvAddon.plugin.language);
						if (xMaterial.equals(XMaterial.PLAYER_HEAD)) {
							String owner = NBTUtils.getString(item, "SkullOwner", "Name");
							if (owner != null) {
								itemStr = itemStr.replaceFirst("%s", owner);
							}
						}
					}
					itemStr = ComponentStringUtils.stripColorAndConvertMagic(itemStr);
					
					int amount = item.getAmount();
					if (item == null || item.getType().equals(Material.AIR)) {
						amount = 1;
					}
				
					String replaceText = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.itemReplaceText).replace("{Amount}", String.valueOf(amount)).replace("{Item}", itemStr));
					message = message.replaceAll((InteractiveChat.itemCaseSensitive ? "" : "(?i)") + CustomStringUtils.escapeMetaCharacters(InteractiveChat.itemPlaceholder), replaceText);
					if (InteractiveChatDiscordSrvAddon.plugin.itemImage) {
						int inventoryId = DATA_ID_PROVIDER.getNext();
						int position = InteractiveChat.itemCaseSensitive ? originalMessage.indexOf(InteractiveChat.itemPlaceholder) : originalMessage.toLowerCase().indexOf(InteractiveChat.itemPlaceholder.toLowerCase());
						
						String title = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.itemTitle));
						
						Inventory inv = null;
						if (item.hasItemMeta() && item.getItemMeta() instanceof BlockStateMeta) {
							BlockState bsm = ((BlockStateMeta) item.getItemMeta()).getBlockState();
							if (bsm instanceof InventoryHolder) {
								Inventory container = ((InventoryHolder) bsm).getInventory();
								if (!container.isEmpty()) {
									inv = Bukkit.createInventory(null, container.getSize() + (container.getSize() % 9));
									for (int j = 0; j < container.getSize(); j++) {
										if (container.getItem(j) != null) {
											if (!container.getItem(j).getType().equals(Material.AIR)) {
												inv.setItem(j, container.getItem(j).clone());
											}
										}
									}
								}
							}
						}
						
						GameMessageProcessItemEvent gameMessageProcessItemEvent = new GameMessageProcessItemEvent(sender, title, message, false, inventoryId, item.clone(), inv);
						Bukkit.getPluginManager().callEvent(gameMessageProcessItemEvent);
						if (!gameMessageProcessItemEvent.isCancelled()) {
							message = gameMessageProcessItemEvent.getMessage();
							title = gameMessageProcessItemEvent.getTitle();
							if (gameMessageProcessItemEvent.hasInventory()) {
								DATA.put(inventoryId, new ImageDisplayData(sender, position, title, ImageDisplayType.ITEM_CONTAINER, gameMessageProcessItemEvent.getItemStack().clone(), gameMessageProcessItemEvent.getInventory()));
							} else {
								DATA.put(inventoryId, new ImageDisplayData(sender, position, title, ImageDisplayType.ITEM, gameMessageProcessItemEvent.getItemStack().clone()));
							}
						}
						message += "<ICD=" + inventoryId + ">";
					}
				}
			}
		}
		
		if (InteractiveChat.useInventory && sender.hasPermission("interactivechat.module.inventory")) {
			long cooldown = InteractiveChatAPI.getPlayerPlaceholderCooldown(sender, InteractiveChat.invPlaceholder) - now;
			if (cooldown < 0 || cooldown + 100 > ConfigManager.getConfig().getLong("ItemDisplay.Inventory.Cooldown") * 1000) {
				String placeholder = InteractiveChat.invPlaceholder;
				int index = InteractiveChat.invCaseSensitive ? message.indexOf(placeholder) : message.toLowerCase().indexOf(placeholder.toLowerCase());
				if (index >= 0 && !((index > 0 && message.charAt(index - 1) == '\\') && (index < 2 || message.charAt(index - 2) != '\\'))) {
					String replaceText = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.invReplaceText));
					message = message.replaceAll((InteractiveChat.invCaseSensitive ? "" : "(?i)") + CustomStringUtils.escapeMetaCharacters(InteractiveChat.invPlaceholder), replaceText);
					if (InteractiveChatDiscordSrvAddon.plugin.invImage) {
						int inventoryId = DATA_ID_PROVIDER.getNext();
						int position = InteractiveChat.invCaseSensitive ? originalMessage.indexOf(InteractiveChat.invPlaceholder) : originalMessage.toLowerCase().indexOf(InteractiveChat.invPlaceholder.toLowerCase());
						
						Inventory inv = Bukkit.createInventory(null, 45);
						for (int j = 0; j < sender.getInventory().getSize(); j++) {
							if (sender.getInventory().getItem(j) != null) {
								if (!sender.getInventory().getItem(j).getType().equals(Material.AIR)) {
									inv.setItem(j, sender.getInventory().getItem(j).clone());
								}
							}
						}
						String title = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.invTitle));
						
						GameMessageProcessPlayerInventoryEvent gameMessageProcessPlayerInventoryEvent = new GameMessageProcessPlayerInventoryEvent(sender, title, message, false, inventoryId, inv);
						Bukkit.getPluginManager().callEvent(gameMessageProcessPlayerInventoryEvent);
						if (!gameMessageProcessPlayerInventoryEvent.isCancelled()) {
							message = gameMessageProcessPlayerInventoryEvent.getMessage();
							title = gameMessageProcessPlayerInventoryEvent.getTitle();
							DATA.put(inventoryId, new ImageDisplayData(sender, position, title, ImageDisplayType.INVENTORY, true, gameMessageProcessPlayerInventoryEvent.getInventory()));
						}
						
						message += "<ICD=" + inventoryId + ">";
					}
				}
			}
		}
		
		if (InteractiveChat.useEnder && sender.hasPermission("interactivechat.module.enderchest")) {
			long cooldown = InteractiveChatAPI.getPlayerPlaceholderCooldown(sender, InteractiveChat.enderPlaceholder) - now;
			if (cooldown < 0 || cooldown + 100 > ConfigManager.getConfig().getLong("ItemDisplay.EnderChest.Cooldown") * 1000) {
				String placeholder = InteractiveChat.enderPlaceholder;
				int index = InteractiveChat.enderCaseSensitive ? message.indexOf(placeholder) : message.toLowerCase().indexOf(placeholder.toLowerCase());
				if (index >= 0 && !((index > 0 && message.charAt(index - 1) == '\\') && (index < 2 || message.charAt(index - 2) != '\\'))) {
					String replaceText = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.enderReplaceText));
					message = message.replaceAll((InteractiveChat.enderCaseSensitive ? "" : "(?i)") + CustomStringUtils.escapeMetaCharacters(InteractiveChat.enderPlaceholder), replaceText);
					if (InteractiveChatDiscordSrvAddon.plugin.enderImage) {
						int inventoryId = DATA_ID_PROVIDER.getNext();
						int position = InteractiveChat.enderCaseSensitive ? originalMessage.indexOf(InteractiveChat.enderPlaceholder) : originalMessage.toLowerCase().indexOf(InteractiveChat.enderPlaceholder.toLowerCase());
						
						Inventory inv = Bukkit.createInventory(null, 27);
						for (int j = 0; j < sender.getEnderChest().getSize(); j++) {
							if (sender.getEnderChest().getItem(j) != null) {
								if (!sender.getEnderChest().getItem(j).getType().equals(Material.AIR)) {
									inv.setItem(j, sender.getEnderChest().getItem(j).clone());
								}
							}
						}
						String title = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.enderTitle));
						
						GameMessageProcessInventoryEvent gameMessageProcessInventoryEvent = new GameMessageProcessInventoryEvent(sender, title, message, false, inventoryId, inv);
						Bukkit.getPluginManager().callEvent(gameMessageProcessInventoryEvent);
						if (!gameMessageProcessInventoryEvent.isCancelled()) {
							message = gameMessageProcessInventoryEvent.getMessage();
							title = gameMessageProcessInventoryEvent.getTitle();
							DATA.put(inventoryId, new ImageDisplayData(sender, position, title, ImageDisplayType.ENDERCHEST, gameMessageProcessInventoryEvent.getInventory()));
						}
						
						message += "<ICD=" + inventoryId + ">";
					}
				}
			}
		}
		
		for (ICPlaceholder placeholder : InteractiveChatAPI.getICPlaceholderList()) {
			if (!placeholder.isBuildIn()) {
				CustomPlaceholder customP = placeholder.getCustomPlaceholder().get();
				if (!InteractiveChat.useCustomPlaceholderPermissions || (InteractiveChat.useCustomPlaceholderPermissions && sender.hasPermission(customP.getPermission()))) {
					long cooldown = InteractiveChatAPI.getPlayerPlaceholderCooldown(sender, customP.getKeyword()) - now;
					int index = placeholder.isCaseSensitive() ? message.indexOf(placeholder.getKeyword()) : message.toLowerCase().indexOf(placeholder.getKeyword().toLowerCase());
					if (index >= 0 && !((index > 0 && message.charAt(index - 1) == '\\') && (index < 2 || message.charAt(index - 2) != '\\')) && (cooldown < 0 || cooldown + 100 > customP.getCooldown())) {
						String replaceText = customP.getKeyword();
						if (customP.getReplace().isEnabled()) {
							replaceText = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(customP.getReplace().getReplaceText()));
							message = message.replaceAll((customP.isCaseSensitive() ? "" : "(?i)") + CustomStringUtils.escapeMetaCharacters(customP.getKeyword()), replaceText);
						}
						if (InteractiveChatDiscordSrvAddon.plugin.hoverEnabled && !InteractiveChatDiscordSrvAddon.plugin.hoverIngore.contains(customP.getPosition())) {
							int position = customP.isCaseSensitive() ? originalMessage.indexOf(customP.getKeyword()) : originalMessage.toLowerCase().indexOf(customP.getKeyword().toLowerCase());
							HoverClickDisplayData.Builder hoverClick = new HoverClickDisplayData.Builder().player(sender).postion(position).color(DiscordDataRegistry.DISCORD_HOVER_COLOR).displayText(replaceText);
							boolean usingHoverClick = false;
							
							if (customP.getHover().isEnabled()) {
								usingHoverClick = true;
								String hoverText = PlaceholderParser.parse(wrappedSender, customP.getHover().getText());
								Color color = ColorUtils.getFirstColor(customP.getHover().getText());
								hoverClick.hoverText(new TextComponent(hoverText));
								if (color != null) {
									hoverClick.color(color);
								}
							}
							
							if (customP.getClick().isEnabled()) {
								usingHoverClick = true;
								hoverClick.clickAction(customP.getClick().getAction()).clickValue(customP.getClick().getValue());
							}
							
							if (usingHoverClick) {
								int hoverId = DATA_ID_PROVIDER.getNext();
								DATA.put(hoverId, hoverClick.build());
								message += "<ICD=" + hoverId + ">";
							}
						}
					}
				}
			}
		}
		
		if (InteractiveChat.t && WebData.getInstance() != null) {
			for (CustomPlaceholder customP : WebData.getInstance().getSpecialPlaceholders()) {
				long cooldown = InteractiveChatAPI.getPlayerPlaceholderCooldown(sender, customP.getKeyword()) - now;
				int index = customP.isCaseSensitive() ? message.indexOf(customP.getKeyword()) : message.toLowerCase().indexOf(customP.getKeyword().toLowerCase());
				if (index >= 0 && !((index > 0 && message.charAt(index - 1) == '\\') && (index < 2 || message.charAt(index - 2) != '\\')) && (cooldown < 0 || cooldown + 100 > customP.getCooldown())) {
					String replaceText = customP.getKeyword();
					if (customP.getReplace().isEnabled()) {
						replaceText = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(customP.getReplace().getReplaceText()));
						message = message.replaceAll((customP.isCaseSensitive() ? "" : "(?i)") + CustomStringUtils.escapeMetaCharacters(customP.getKeyword()), replaceText);
					}
					if (InteractiveChatDiscordSrvAddon.plugin.hoverEnabled && !InteractiveChatDiscordSrvAddon.plugin.hoverIngore.contains(customP.getPosition())) {
						int position = customP.isCaseSensitive() ? originalMessage.indexOf(customP.getKeyword()) : originalMessage.toLowerCase().indexOf(customP.getKeyword().toLowerCase());
						HoverClickDisplayData.Builder hoverClick = new HoverClickDisplayData.Builder().player(sender).postion(position).color(DiscordDataRegistry.DISCORD_HOVER_COLOR).displayText(replaceText);
						boolean usingHoverClick = false;
						
						if (customP.getHover().isEnabled()) {
							usingHoverClick = true;
							String hoverText = PlaceholderParser.parse(wrappedSender, customP.getHover().getText());
							Color color = ColorUtils.getFirstColor(customP.getHover().getText());
							hoverClick.hoverText(new TextComponent(hoverText));
							if (color != null) {
								hoverClick.color(color);
							}
						}
						
						if (customP.getClick().isEnabled()) {
							usingHoverClick = true;
							hoverClick.clickAction(customP.getClick().getAction()).clickValue(customP.getClick().getValue());
						}
						
						if (usingHoverClick) {
							int hoverId = DATA_ID_PROVIDER.getNext();
							DATA.put(hoverId, hoverClick.build());
							message += "<ICD=" + hoverId + ">";
						}
					}
				}
			}
		}
		
		DiscordSRV srv = InteractiveChatDiscordSrvAddon.discordsrv;
		if (InteractiveChatDiscordSrvAddon.plugin.translateMentions) {
			for (MentionPair pair : InteractiveChat.mentionPair.values()) {
				if (pair.getSender().equals(sender.getUniqueId())) {
					UUID recieverUUID = pair.getReciever();
					Set<String> names = new HashSet<>();
					Player reciever = Bukkit.getPlayer(recieverUUID);
					if (reciever != null) {
						names.add(ChatColorUtils.stripColor(reciever.getName()));
						if (!names.contains(ChatColorUtils.stripColor(reciever.getDisplayName()))) {
							names.add(ChatColorUtils.stripColor(reciever.getDisplayName()));
						}
						List<String> list = InteractiveChatAPI.getNicknames(reciever.getUniqueId());
						for (String name : list) {
							names.add(ChatColorUtils.stripColor(name));
						}
					} else {
						ICPlayer icplayer = InteractiveChat.remotePlayers.get(recieverUUID);
						if (icplayer != null) {
							names.add(icplayer.getDisplayName());
						}
					}
					String userId = srv.getAccountLinkManager().getDiscordId(recieverUUID);
					if (userId != null) {
						User user = srv.getJda().getUserById(userId);
						if (user != null) {
							String discordMention = user.getAsMention();
							for (String name : names) {
								if (message.contains(InteractiveChat.mentionPrefix + name)) {
									message = message.replace(InteractiveChat.mentionPrefix + name, discordMention);
								}
							}
						}
					}
				}
			}
		}
		
		GameMessagePostProcessEvent gameMessagePostProcessEvent = new GameMessagePostProcessEvent(sender, message, false);
		Bukkit.getPluginManager().callEvent(gameMessagePostProcessEvent);
		if (gameMessagePostProcessEvent.isCancelled()) {
			return;
		}
		message = gameMessagePostProcessEvent.getMessage();
		
		event.setMessage(message);
	}
	
	@Subscribe(priority = ListenerPriority.HIGHEST)
	public void discordMessageSent(DiscordGuildMessageSentEvent event) {
		Message message = event.getMessage();
		String textOriginal = message.getContentRaw();
		TextChannel channel = event.getChannel();
		
		if (!InteractiveChatDiscordSrvAddon.plugin.isEnabled()) {
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
			String text = textOriginal;
			
			if (!text.contains("<ICD=")) {
				return;
			}
			
			Set<Integer> matches = new LinkedHashSet<>();
			
			for (int key : DATA.keySet()) {
				if (text.contains("<ICD=" + key + ">")) {
					text = text.replace("<ICD=" + key + ">", "");
					matches.add(key);
				}
			}
			
			if (matches.isEmpty()) {
				return;
			}
			
			message.delete().queue();
			
			List<DiscordDisplayData> dataList = new ArrayList<>();
			
			for (int key : matches) {
				DiscordDisplayData data = DATA.remove(key); 
				if (data != null) {
					dataList.add(data);
				}
			}
			
			Collections.sort(dataList, DISPLAY_DATA_COMPARATOR);
			
			List<DiscordMessageContent> contents = createContents(dataList);
			
			DiscordImageEvent discordImageEvent = new DiscordImageEvent(channel, textOriginal, text, contents, false, true);
			TextChannel textChannel = discordImageEvent.getChannel();
			if (discordImageEvent.isCancelled()) {
				String restore = discordImageEvent.getOriginalMessage();
				textChannel.sendMessage(restore).queue();
			} else {
				text = discordImageEvent.getNewMessage();
				textChannel.sendMessage(text).queue();
				for (DiscordMessageContent content : discordImageEvent.getDiscordMessageContents()) {
					content.toJDAMessageRestAction(textChannel).queue();
				}
			}
		});
	}
	
	public static class JDAEvents extends ListenerAdapter {
		
		@Override
		public void onMessageReceived(MessageReceivedEvent event) {
			if (event.getAuthor().equals(event.getJDA().getSelfUser())) {
				return;
			}
			if (!event.getChannelType().equals(ChannelType.TEXT)) {
				return;
			}
			if (!event.isWebhookMessage()) {
				return;
			}
			Message message = event.getMessage();
			TextChannel channel = event.getTextChannel();
			String textOriginal = message.getContentRaw();
			String text = textOriginal;
			if (!text.contains("<ICD=")) {
				return;
			}
			
			Set<Integer> matches = new LinkedHashSet<>();
			
			for (int key : OutboundToDiscordEvents.DATA.keySet()) {
				if (text.contains("<ICD=" + key + ">")) {
					text = text.replace("<ICD=" + key + ">", "");
					matches.add(key);
				}
			}
			if (matches.isEmpty()) {
				return;
			}
			
			message.delete().queue();
			Player player = OutboundToDiscordEvents.DATA.get(matches.iterator().next()).getPlayer();

			List<DiscordDisplayData> dataList = new ArrayList<>();
			
			for (int key : matches) {
				DiscordDisplayData data = DATA.remove(key); 
				if (data != null) {
					dataList.add(data);
				}
			}
			
			Collections.sort(dataList, DISPLAY_DATA_COMPARATOR);
			
			List<DiscordMessageContent> contents = createContents(dataList);
			
			List<WebhookMessageBuilder> messagesToSend = new ArrayList<>();
			
			DiscordImageEvent discordImageEvent = new DiscordImageEvent(channel, textOriginal, text, contents, false, true);
			TextChannel textChannel = discordImageEvent.getChannel();
			if (discordImageEvent.isCancelled()) {
				String restore = discordImageEvent.getOriginalMessage();
				messagesToSend.add(new WebhookMessageBuilder().setContent(restore));
			} else {
				text = discordImageEvent.getNewMessage();
				messagesToSend.add(new WebhookMessageBuilder().setContent(text));
				for (DiscordMessageContent content : discordImageEvent.getDiscordMessageContents()) {
					messagesToSend.add(content.toWebhookMessageBuilder());
				}
			}
			
			String avatarUrl = DiscordSRV.getAvatarUrl(player);
            String username = DiscordSRV.config().getString("Experiment_WebhookChatMessageUsernameFormat")
                    .replace("%displayname%", MessageUtil.strip(player.getDisplayName()))
                    .replace("%username%", player.getName());
            username = PlaceholderUtil.replacePlaceholders(username, player);
            username = MessageUtil.strip(username);

            String userId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId());
            if (userId != null) {
                Member member = DiscordUtil.getMemberById(userId);
                if (member != null) {
                    if (DiscordSRV.config().getBoolean("Experiment_WebhookChatMessageAvatarFromDiscord"))
                        avatarUrl = member.getUser().getEffectiveAvatarUrl();
                    if (DiscordSRV.config().getBoolean("Experiment_WebhookChatMessageUsernameFromDiscord"))
                        username = member.getEffectiveName();
                }
            }
			
			String webHookUrl = WebhookUtil.getWebhookUrlToUseForChannel(textChannel, username);
			WebhookClient client = WebhookClient.withUrl(webHookUrl);
			
			if (client == null) {
				throw new NullPointerException("Unable to get the Webhook client URL for the TextChannel " + textChannel.getName());
			}
			
			for (WebhookMessageBuilder builder : messagesToSend) {
				client.send(builder.setUsername(username).setAvatarUrl(avatarUrl).build());
			}
			client.close();
		}
	}
	
	private static List<DiscordMessageContent> createContents(List<DiscordDisplayData> dataList) {
		List<DiscordMessageContent> contents = new ArrayList<>();
		for (DiscordDisplayData data : dataList) {
			if (data instanceof ImageDisplayData) {
				ImageDisplayData iData = (ImageDisplayData) data;
				ImageDisplayType type = iData.getType();
				String title = iData.getTitle();
				if (iData.getItemStack().isPresent()) {
					ItemStack item = iData.getItemStack().get();
					Color color = DiscordItemStackUtils.getDiscordColor(item);
					if (color == null || color.equals(Color.white)) {
						color = new Color(0xFFFFFE);
					}
					try {
						BufferedImage image = ImageGeneration.getItemStackImage(item, data.getPlayer());
						ByteArrayOutputStream itemOs = new ByteArrayOutputStream();
						ImageIO.write(image, "png", itemOs);
						
						DiscordDescription description = DiscordItemStackUtils.getDiscordDescription(item);
						
						DiscordMessageContent content = new DiscordMessageContent(description.getName(), "attachment://Item.png", color);
						content.addAttachment("Item.png", itemOs.toByteArray());
						contents.add(content);
						
						if (InteractiveChatDiscordSrvAddon.plugin.itemUseTooltipImage) {
							List<BaseComponent> prints = DiscordItemStackUtils.getToolTip(item);
							BufferedImage tooltip = ImageGeneration.getToolTipImage(prints);
							ByteArrayOutputStream tooltipOs = new ByteArrayOutputStream();
							ImageIO.write(tooltip, "png", tooltipOs);
							content.addAttachment("ToolTip.png", tooltipOs.toByteArray());
							content.addImageUrl("attachment://ToolTip.png");
						} else {
							content.addDescription(description.getDescription().orElse(null));
						}
						
						if (type.equals(ImageDisplayType.ITEM_CONTAINER)) {
							if (!description.getDescription().isPresent()) {
								content.getImageUrls().remove("attachment://ToolTip.png");
								content.getAttachments().remove("ToolTip.png");
							}
							BufferedImage container = ImageGeneration.getInventoryImage(iData.getInventory().get(), data.getPlayer());
							ByteArrayOutputStream contentOs = new ByteArrayOutputStream();
							ImageIO.write(container, "png", contentOs);
							content.addAttachment("Container.png", contentOs.toByteArray());
							content.addImageUrl("attachment://Container.png");
						} else {
							if (iData.isFilledMap()) {
								if (!description.getDescription().isPresent()) {
									content.getImageUrls().remove("attachment://ToolTip.png");
									content.getAttachments().remove("ToolTip.png");
								}
								BufferedImage map = ImageGeneration.getMapImage(item);
								ByteArrayOutputStream mapOs = new ByteArrayOutputStream();
								ImageIO.write(map, "png", mapOs);
								content.addAttachment("Map.png", mapOs.toByteArray());
								content.addImageUrl("attachment://Map.png");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (iData.getInventory().isPresent()) {
					Inventory inv = iData.getInventory().get();
					try {
						BufferedImage image;
						if (iData.isPlayerInventory()) {
							if (InteractiveChatDiscordSrvAddon.plugin.usePlayerInvView) {
								image = ImageGeneration.getPlayerInventoryImage(inv, iData.getPlayer());
							} else {
								image = ImageGeneration.getInventoryImage(inv, data.getPlayer());
							}
						} else {
							image = ImageGeneration.getInventoryImage(inv, data.getPlayer());
						}
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						Color color;
						switch (type) {
						case ENDERCHEST:
							color = InteractiveChatDiscordSrvAddon.plugin.enderColor;
							break;
						case INVENTORY:
							color = InteractiveChatDiscordSrvAddon.plugin.invColor;
							break;
						default:
							color = Color.black;
							break;
						}
						ImageIO.write(image, "png", os);
						DiscordMessageContent content = new DiscordMessageContent(title, null, null, "attachment://Inventory.png", color);
						content.addAttachment("Inventory.png", os.toByteArray());
						contents.add(content);
					} catch (Exception e) {
						e.printStackTrace();
					}			
				}
			} else if (data instanceof HoverClickDisplayData) {
				try {
					HoverClickDisplayData hData = (HoverClickDisplayData) data;
					String title = hData.getDisplayText();
					Color color = hData.getColor();
					DiscordMessageContent content = new DiscordMessageContent(title, null, color);
					String body = "";
					String preview = null;
					if (hData.hasHover()) {
						if (InteractiveChatDiscordSrvAddon.plugin.hoverUseTooltipImage) {
							BaseComponent print = hData.getHoverText();
							BufferedImage tooltip = ImageGeneration.getToolTipImage(print, true);
							ByteArrayOutputStream tooltipOs = new ByteArrayOutputStream();
							ImageIO.write(tooltip, "png", tooltipOs);
							content.addAttachment("ToolTip.png", tooltipOs.toByteArray());
							content.addImageUrl("attachment://ToolTip.png");
							content.addDescription(null);
						} else {
							body += ComponentStringUtils.stripColorAndConvertMagic(hData.getHoverText().toLegacyText());
						}
					}
					if (hData.hasClick()) {
						switch (hData.getClickAction()) {
						case COPY_TO_CLIPBOARD:
							if (body.length() > 0) {
								body += "\n\n";
							}
							body += LanguageUtils.getTranslation(TranslationUtils.getCopyToClipboard(), InteractiveChatDiscordSrvAddon.plugin.language) + ": __" + hData.getClickValue() + "__";
							break;
						case OPEN_URL:
							if (body.length() > 0) {
								body += "\n\n";
							}
							String url = hData.getClickValue();
							body += LanguageUtils.getTranslation(TranslationUtils.getOpenUrl(), InteractiveChatDiscordSrvAddon.plugin.language) + ": __" + url + "__";
							if (URLRequestUtils.IMAGE_URL_PATTERN.matcher(url).matches() && URLRequestUtils.isAllowed(url)) {
								preview = url;
							}
							break;
						default:
							break;							
						}
					}
					content.addDescription(body);
					if (InteractiveChatDiscordSrvAddon.plugin.hoverImage) {
						BufferedImage image = InteractiveChatDiscordSrvAddon.plugin.getMiscTexture("hover_cursor");
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						ImageIO.write(image, "png", os);
						content.setAuthorIconUrl("attachment://Hover.png");
						content.addAttachment("Hover.png", os.toByteArray());
					}
					if (preview != null) {
						content.addImageUrl(preview);
					}
					contents.add(content);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return contents;
	}

}
