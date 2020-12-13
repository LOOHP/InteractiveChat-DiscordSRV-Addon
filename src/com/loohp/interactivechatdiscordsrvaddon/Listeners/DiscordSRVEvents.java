package com.loohp.interactivechatdiscordsrvaddon.Listeners;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.ConfigManager;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.API.InteractiveChatAPI;
import com.loohp.interactivechat.ObjectHolders.CustomPlaceholder;
import com.loohp.interactivechat.ObjectHolders.ICPlaceholder;
import com.loohp.interactivechat.ObjectHolders.PlayerWrapper;
import com.loohp.interactivechat.Utils.CustomStringUtils;
import com.loohp.interactivechat.Utils.MaterialUtils;
import com.loohp.interactivechat.Utils.NBTUtils;
import com.loohp.interactivechat.Utils.PlaceholderParser;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ComponentStringUtils;
import com.loohp.interactivechatdiscordsrvaddon.Utils.IDProvider;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ItemMapWrapper;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ItemStackUtils;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ItemStackUtils.DiscordDescription;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePostProcessEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageSentEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.api.events.GameChatMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.GuildChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.MessageAction;
import net.md_5.bungee.api.ChatColor;

public class DiscordSRVEvents {
	
	private static IDProvider inventoryIdProvider = new IDProvider();
	public static Map<Integer, ImageDisplayData> data = Collections.synchronizedMap(new LinkedHashMap<>());
	
	@Subscribe(priority = ListenerPriority.HIGHEST)
	public void onDiscordReady(DiscordReadyEvent event) {
		DiscordSRV discordsrv = InteractiveChatDiscordSrvAddon.discordsrv;
		
		JDA jda = discordsrv.getJda();
		jda.addEventListener(new JDAEvents());
		
		for (String channelId : discordsrv.getChannels().values()) {
			GuildChannel channel = jda.getGuildChannelById(channelId);
			Guild guild = channel.getGuild();
			Member self = guild.getMember(jda.getSelfUser());
			for (Permission permission : InteractiveChatDiscordSrvAddon.requiredPermissions) {
				if (!self.hasPermission(channel, permission)) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSRVAddon] DiscordSRV Bot is missing the \"" + permission.getName() + "\" permission in the channel \"" + channel.getName() + "\" (Id: " + channel.getId() + ")");
				}
			}
		}
	}
	
	@Subscribe(priority = ListenerPriority.LOW)
	public void onDiscordToGame(DiscordGuildMessagePostProcessEvent event) {
		InteractiveChatDiscordSrvAddon.plugin.messagesCounter.incrementAndGet();
		String message = event.getProcessedMessage();
		if (InteractiveChatDiscordSrvAddon.plugin.escapePlaceholdersFromDiscord) {
			for (String placeholder : InteractiveChat.aliasesMapping.keySet()) {
				message = message.replaceAll(placeholder, "\\" + placeholder);
			}
			for (ICPlaceholder placeholder : InteractiveChat.placeholderList) {
				message = message.replace(placeholder.getKeyword(), "\\" + placeholder.getKeyword());
			}
			event.setProcessedMessage(message);
		}
	}
	
	@Subscribe(priority = ListenerPriority.HIGHEST)
	public void onGameToDiscord(GameChatMessagePreProcessEvent event) {
		InteractiveChatDiscordSrvAddon.plugin.messagesCounter.incrementAndGet();
		Player sender = event.getPlayer();
		PlayerWrapper wrappedSender = new PlayerWrapper(sender);
		String message = event.getMessage();
		
		long now = System.currentTimeMillis();
		long uniCooldown = InteractiveChatAPI.getPlayerUniversalCooldown(sender) - now;
		
		if (!(uniCooldown < 0 || uniCooldown + 100 > InteractiveChat.universalCooldown)) {
			return;
		}
		
		if (InteractiveChat.useItem && sender.hasPermission("interactivechat.module.item")) {
			long cooldown = InteractiveChatAPI.getPlayerPlaceholderCooldown(sender, InteractiveChat.itemPlaceholder) - now;
			if (cooldown < 0 || cooldown + 100 > ConfigManager.getConfig().getLong("ItemDisplay.Item.Cooldown") * 1000) {
				if (message.toLowerCase().contains(InteractiveChat.itemPlaceholder.toLowerCase())) {
					@SuppressWarnings("deprecation")
					ItemStack item = sender.getEquipment().getItemInHand();
					if (item == null) {
						item = new ItemStack(Material.AIR);
					}
					XMaterial xMaterial = XMaterial.matchXMaterial(item);
					String itemStr;
					if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && !item.getItemMeta().getDisplayName().equals("")) {
						itemStr = item.getItemMeta().getDisplayName();
					} else {
						String itemKey = MaterialUtils.getMinecraftLangName(item);
						if (InteractiveChat.version.isLegacy()) {
							itemStr = itemKey;
						} else {
							itemStr = InteractiveChatDiscordSrvAddon.plugin.getModernItemTrans(itemKey);
							if (xMaterial.equals(XMaterial.PLAYER_HEAD)) {
								String owner = NBTUtils.getString(item, "SkullOwner", "Name");
								if (owner != null) {
									itemStr = itemStr.replaceFirst("%s", owner);
								}
							}
						}
					}
					itemStr = ComponentStringUtils.stripColorAndConvertMagic(itemStr);
					
					int amount = item.getAmount();
				
					String replaceText = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.itemReplaceText).replace("{Amount}", String.valueOf(amount)).replace("{Item}", itemStr));
					message = message.replaceAll((InteractiveChat.itemCaseSensitive ? "" : "(?i)") + CustomStringUtils.escapeMetaCharacters(InteractiveChat.itemPlaceholder), replaceText);
					if (InteractiveChatDiscordSrvAddon.plugin.itemImage && !item.getType().equals(Material.AIR)) {
						int inventoryId = inventoryIdProvider.getNext();
						String title = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.itemTitle));
						
						Inventory inv = null;
						if (item.hasItemMeta() && item.getItemMeta() instanceof BlockStateMeta) {
							BlockState bsm = ((BlockStateMeta) item.getItemMeta()).getBlockState();
							if (bsm instanceof InventoryHolder) {
								Inventory container = ((InventoryHolder) bsm).getInventory();
								if (!container.isEmpty()) {
									inv = Bukkit.createInventory(null, container.getSize());
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
						
						if (inv == null) {
							data.put(inventoryId, new ImageDisplayData(sender, title, ImageDisplayType.ITEM, item.clone()));
						} else {
							data.put(inventoryId, new ImageDisplayData(sender, title, ImageDisplayType.ITEM_CONTAINER, item.clone(), inv));
						}
						message += "<ICD=" + inventoryId + ">";
					}
				}
			}
		}
		
		if (InteractiveChat.useInventory && sender.hasPermission("interactivechat.module.inventory")) {
			long cooldown = InteractiveChatAPI.getPlayerPlaceholderCooldown(sender, InteractiveChat.invPlaceholder) - now;
			if (cooldown < 0 || cooldown + 100 > ConfigManager.getConfig().getLong("ItemDisplay.Inventory.Cooldown") * 1000) {
				if (message.toLowerCase().contains(InteractiveChat.invPlaceholder.toLowerCase())) {
					String replaceText = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.invReplaceText));
					message = message.replaceAll((InteractiveChat.invCaseSensitive ? "" : "(?i)") + CustomStringUtils.escapeMetaCharacters(InteractiveChat.invPlaceholder), replaceText);
					if (InteractiveChatDiscordSrvAddon.plugin.invImage) {
						int inventoryId = inventoryIdProvider.getNext();
						Inventory inv = Bukkit.createInventory(null, 45);
						for (int j = 0; j < sender.getInventory().getSize(); j++) {
							if (sender.getInventory().getItem(j) != null) {
								if (!sender.getInventory().getItem(j).getType().equals(Material.AIR)) {
									inv.setItem(j, sender.getInventory().getItem(j).clone());
								}
							}
						}
						String title = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.invTitle));
						data.put(inventoryId, new ImageDisplayData(sender, title, ImageDisplayType.INVENTORY, true, inv));
						message += "<ICD=" + inventoryId + ">";
					}
				}
			}
		}
		
		if (InteractiveChat.useEnder && sender.hasPermission("interactivechat.module.enderchest")) {
			long cooldown = InteractiveChatAPI.getPlayerPlaceholderCooldown(sender, InteractiveChat.enderPlaceholder) - now;
			if (cooldown < 0 || cooldown + 100 > ConfigManager.getConfig().getLong("ItemDisplay.EnderChest.Cooldown") * 1000) {
				if (message.toLowerCase().contains(InteractiveChat.enderPlaceholder.toLowerCase())) {
					String replaceText = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.enderReplaceText));
					message = message.replaceAll((InteractiveChat.enderCaseSensitive ? "" : "(?i)") + CustomStringUtils.escapeMetaCharacters(InteractiveChat.enderPlaceholder), replaceText);
					if (InteractiveChatDiscordSrvAddon.plugin.enderImage) {
						int inventoryId = inventoryIdProvider.getNext();
						Inventory inv = Bukkit.createInventory(null, 27);
						for (int j = 0; j < sender.getEnderChest().getSize(); j++) {
							if (sender.getEnderChest().getItem(j) != null) {
								if (!sender.getEnderChest().getItem(j).getType().equals(Material.AIR)) {
									inv.setItem(j, sender.getEnderChest().getItem(j).clone());
								}
							}
						}
						String title = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.enderTitle));
						data.put(inventoryId, new ImageDisplayData(sender, title, ImageDisplayType.ENDERCHEST, inv));
						message += "<ICD=" + inventoryId + ">";
					}
				}
			}
		}
		
		for (ICPlaceholder placeholder : InteractiveChatAPI.getICPlaceholderList()) {
			if (!placeholder.isBuildIn()) {
				CustomPlaceholder customP = placeholder.getCustomPlaceholder().get();
				if ((!InteractiveChat.useCustomPlaceholderPermissions || (InteractiveChat.useCustomPlaceholderPermissions && sender.hasPermission("interactivechat.module.custom." + customP.getPosition()))) && customP.getReplace().isEnabled()) {
					long cooldown = InteractiveChatAPI.getPlayerPlaceholderCooldown(sender, customP.getKeyword()) - now;
					if (cooldown < 0 || cooldown + 100 > customP.getCooldown()) {
						if (message.toLowerCase().contains(customP.getKeyword())) {
							String replaceText = PlaceholderParser.parse(wrappedSender, ComponentStringUtils.stripColorAndConvertMagic(customP.getReplace().getReplaceText()));
							message = message.replaceAll((customP.isCaseSensitive() ? "" : "(?i)") + CustomStringUtils.escapeMetaCharacters(customP.getKeyword()), replaceText);
						}
					}
				}
			}
		}
		
		event.setMessage(message);
	}
	
	@Subscribe(priority = ListenerPriority.HIGHEST)
	public void discordMessageSent(DiscordGuildMessageSentEvent event) {
		Message message = event.getMessage();
		String text0 = message.getContentRaw();
		TextChannel channel = event.getChannel();
		
		if (!InteractiveChatDiscordSrvAddon.plugin.isEnabled()) {
			return;
		}
		Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
			String text = text0;
			
			if (!text.contains("<ICD=")) {
				return;
			}
			
			Set<Integer> matches = new LinkedHashSet<>();
			
			for (int key : data.keySet()) {
				if (text.contains("<ICD=" + key + ">")) {
					text = text.replace("<ICD=" + key + ">", "");
					matches.add(key);
				}
			}
			
			if (matches.isEmpty()) {
				return;
			}
			
			message.delete().queue();
			
			for (int key : matches) {
				ImageDisplayData iData = data.remove(key);
				ImageDisplayType type = iData.getType();
				String title = iData.getTitle();
				if (iData.getItemStack().isPresent()) {
					ItemStack item = iData.getItemStack().get();
					Color color = ItemStackUtils.getDiscordColor(item);
					if (color.equals(Color.white)) {
						color = new Color(0xFFFFFE);
					}
					try {
						if (type.equals(ImageDisplayType.ITEM_CONTAINER)) {
							DiscordDescription description = ItemStackUtils.getDiscordDescription(item);
							BufferedImage image = ImageGeneration.getItemStackImage(item);
							ByteArrayOutputStream os = new ByteArrayOutputStream();
							ImageIO.write(image, "png", os);
							EmbedBuilder embed = new EmbedBuilder().setDescription(description.getDescription().orElse(null)).setColor(color).setAuthor(description.getName(), null, "attachment://Item.png");					
							embed.setImage("attachment://Container.png");
							MessageAction messageToSend = channel.sendMessage(embed.build()).addFile(os.toByteArray(), "Item.png");
							BufferedImage map = ImageGeneration.getInventoryImage(iData.getInventory().get());
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							ImageIO.write(map, "png", out);
							messageToSend.addFile(out.toByteArray(), "Container.png");
							channel.sendMessage(text).queue();
							messageToSend.queue();
						} else {
							DiscordDescription description = ItemStackUtils.getDiscordDescription(item);
							BufferedImage image = ImageGeneration.getItemStackImage(item);
							ByteArrayOutputStream os = new ByteArrayOutputStream();
							ImageIO.write(image, "png", os);
							EmbedBuilder embed = new EmbedBuilder().setDescription(description.getDescription().orElse(null)).setColor(color).setAuthor(description.getName(), null, "attachment://Item.png");					
							if (iData.isFilledMap()) {
								embed.setImage("attachment://Map.png");
							}
							MessageAction messageToSend = channel.sendMessage(embed.build()).addFile(os.toByteArray(), "Item.png");
							if (iData.isFilledMap()) {
								BufferedImage map = ImageGeneration.getMapImage(item);
								ByteArrayOutputStream out = new ByteArrayOutputStream();
								ImageIO.write(map, "png", out);
								messageToSend.addFile(out.toByteArray(), "Map.png");
							}
							channel.sendMessage(text).queue();
							messageToSend.queue();
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
								image = ImageGeneration.getInventoryImage(inv);
							}
						} else {
							image = ImageGeneration.getInventoryImage(inv);
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
						channel.sendMessage(text).queue();
						channel.sendMessage(new EmbedBuilder().setAuthor(title, null, null).setImage("attachment://Inventory.png").setColor(color).build()).addFile(os.toByteArray(), "Inventory.png").queue();
					} catch (Exception e) {
						e.printStackTrace();
					}			
				}
			}
		});
	}
	
	public enum ImageDisplayType {
		ITEM,
		ITEM_CONTAINER,
		INVENTORY,
		ENDERCHEST;
	}
	
	public static class ImageDisplayData {
		
		private final Player player;
		private final String title;
		private final ImageDisplayType type;
		private final Optional<Inventory> inventory;
		private final boolean isPlayerInventory;
		private final Optional<ItemStack> item;
		private final boolean isFilledMap;
		
		private ImageDisplayData(Player player, String title, ImageDisplayType type, Inventory inventory, boolean isPlayerInventory, ItemStack item, boolean isFilledMap) {
			this.type = type;
			this.player = player;
			this.title = title;
			this.inventory = Optional.ofNullable(inventory);
			this.isPlayerInventory = isPlayerInventory;
			this.item = Optional.ofNullable(item);
			this.isFilledMap = isFilledMap;
		}
		
		public ImageDisplayData(Player player, String title, ImageDisplayType type, Inventory inventory) {
			this(player, title, type, inventory, false, null, false);
		}
		
		public ImageDisplayData(Player player, String title, ImageDisplayType type, boolean isPlayerInventory, Inventory inventory) {
			this(player, title, type, inventory, isPlayerInventory, null, false);
		}
		
		public ImageDisplayData(Player player, String title, ImageDisplayType type, ItemStack itemstack) {
			this(player, title, type, null, false, itemstack, ItemMapWrapper.isFilledMap(itemstack));
		}
		
		public ImageDisplayData(Player player, String title, ImageDisplayType type, ItemStack itemstack, Inventory inventory) {
			this(player, title, type, inventory, false, itemstack, ItemMapWrapper.isFilledMap(itemstack));
		}
		
		public Player getPlayer() {
			return player;
		}
		
		public String getTitle() {
			return title;
		}

		public Optional<Inventory> getInventory() {
			return inventory;
		}
		
		public boolean isPlayerInventory() {
			return isPlayerInventory;
		}

		public Optional<ItemStack> getItemStack() {
			return item;
		}
		
		public boolean isFilledMap() {
			return isFilledMap;
		}

		public ImageDisplayType getType() {
			return type;
		}
	}

}
