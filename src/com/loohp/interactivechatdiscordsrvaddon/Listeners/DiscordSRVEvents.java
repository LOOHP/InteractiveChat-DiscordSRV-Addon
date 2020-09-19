package com.loohp.interactivechatdiscordsrvaddon.Listeners;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.ConfigManager;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.API.InteractiveChatAPI;
import com.loohp.interactivechat.ObjectHolders.CustomPlaceholder;
import com.loohp.interactivechat.ObjectHolders.ICPlaceholder;
import com.loohp.interactivechat.ObjectHolders.PlayerWrapper;
import com.loohp.interactivechat.Utils.ChatColorUtils;
import com.loohp.interactivechat.Utils.CustomStringUtils;
import com.loohp.interactivechat.Utils.MaterialUtils;
import com.loohp.interactivechat.Utils.NBTUtils;
import com.loohp.interactivechat.Utils.PlaceholderParser;
import com.loohp.interactivechat.Utils.RarityUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ColorUtils;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ItemStackUtils;

import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageSentEvent;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.api.events.GameChatMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class DiscordSRVEvents {
	
	private static Random random = new Random();
	public static Map<Integer, ImageDisplayData> data = Collections.synchronizedMap(new LinkedHashMap<>());
	
	@Subscribe
	public void onDiscordReady(DiscordReadyEvent event) {
		 InteractiveChatDiscordSrvAddon.discordsrv.getJda().addEventListener(new JDAEvents());
	}
	
	@Subscribe
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
						TranslatableComponent component = new TranslatableComponent(MaterialUtils.getMinecraftLangName(item));
						if (xMaterial.equals(XMaterial.PLAYER_HEAD)) {
							String owner = NBTUtils.getString(item, "SkullOwner", "Name");
							if (owner != null) {
								component.addWith(owner);
							}
						}
						itemStr = component.toLegacyText();
					}
					itemStr = ChatColorUtils.stripColor(itemStr);
					
					int amount = item.getAmount();
				
					String replaceText = PlaceholderParser.parse(wrappedSender, ChatColorUtils.stripColor(InteractiveChat.itemReplaceText).replace("{Amount}", String.valueOf(amount)).replace("{Item}", itemStr));
					message = message.replaceAll((InteractiveChat.itemCaseSensitive ? "" : "(?i)") + CustomStringUtils.escapeMetaCharacters(InteractiveChat.itemPlaceholder), replaceText);
					if (InteractiveChatDiscordSrvAddon.plugin.itemImage && !item.getType().equals(Material.AIR)) {
						int inventoryId = random.nextInt();
						String title = PlaceholderParser.parse(wrappedSender, ChatColorUtils.stripColor(InteractiveChat.itemTitle));
						data.put(inventoryId, new ImageDisplayData(sender, title, ImageDisplayType.ITEM, item.clone()));
						message += "<ICD=" + inventoryId + ">";
					}
				}
			}
		}
		
		if (InteractiveChat.useInventory && sender.hasPermission("interactivechat.module.inventory")) {
			long cooldown = InteractiveChatAPI.getPlayerPlaceholderCooldown(sender, InteractiveChat.invPlaceholder) - now;
			if (cooldown < 0 || cooldown + 100 > ConfigManager.getConfig().getLong("ItemDisplay.Inventory.Cooldown") * 1000) {
				if (message.toLowerCase().contains(InteractiveChat.invPlaceholder.toLowerCase())) {
					String replaceText = PlaceholderParser.parse(wrappedSender, ChatColorUtils.stripColor(InteractiveChat.invReplaceText));
					message = message.replaceAll((InteractiveChat.invCaseSensitive ? "" : "(?i)") + CustomStringUtils.escapeMetaCharacters(InteractiveChat.invPlaceholder), replaceText);
					if (InteractiveChatDiscordSrvAddon.plugin.invImage) {
						int inventoryId = random.nextInt();
						Inventory inv = Bukkit.createInventory(null, 45);
						for (int j = 0; j < sender.getInventory().getSize(); j = j + 1) {
							if (sender.getInventory().getItem(j) != null) {
								if (!sender.getInventory().getItem(j).getType().equals(Material.AIR)) {
									inv.setItem(j, sender.getInventory().getItem(j).clone());
								}
							}
						}
						String title = PlaceholderParser.parse(wrappedSender, ChatColorUtils.stripColor(InteractiveChat.invTitle));
						data.put(inventoryId, new ImageDisplayData(sender, title, ImageDisplayType.INVENTORY, inv));
						message += "<ICD=" + inventoryId + ">";
					}
				}
			}
		}
		
		if (InteractiveChat.useEnder && sender.hasPermission("interactivechat.module.enderchest")) {
			long cooldown = InteractiveChatAPI.getPlayerPlaceholderCooldown(sender, InteractiveChat.enderPlaceholder) - now;
			if (cooldown < 0 || cooldown + 100 > ConfigManager.getConfig().getLong("ItemDisplay.EnderChest.Cooldown") * 1000) {
				if (message.toLowerCase().contains(InteractiveChat.enderPlaceholder.toLowerCase())) {
					String replaceText = PlaceholderParser.parse(wrappedSender, ChatColorUtils.stripColor(InteractiveChat.enderReplaceText));
					message = message.replaceAll((InteractiveChat.enderCaseSensitive ? "" : "(?i)") + CustomStringUtils.escapeMetaCharacters(InteractiveChat.enderPlaceholder), replaceText);
					if (InteractiveChatDiscordSrvAddon.plugin.enderImage) {
						int inventoryId = random.nextInt();
						Inventory inv = Bukkit.createInventory(null, 27);
						for (int j = 0; j < sender.getEnderChest().getSize(); j = j + 1) {
							if (sender.getEnderChest().getItem(j) != null) {
								if (!sender.getEnderChest().getItem(j).getType().equals(Material.AIR)) {
									inv.setItem(j, sender.getEnderChest().getItem(j).clone());
								}
							}
						}
						String title = PlaceholderParser.parse(wrappedSender, ChatColorUtils.stripColor(InteractiveChat.enderTitle));
						data.put(inventoryId, new ImageDisplayData(sender, title, ImageDisplayType.ENDERCHEST, inv));
						message += "<ICD=" + inventoryId + ">";
					}
				}
			}
		}
		
		for (ICPlaceholder placeholder : InteractiveChatAPI.getICPlaceholderList()) {
			if (!placeholder.isBuildIn()) {
				CustomPlaceholder customP = placeholder.getCustomPlaceholder().get();
				if ((InteractiveChat.useCustomPlaceholderPermissions && sender.hasPermission("interactivechat.module.custom." + customP.getPosition())) && customP.getReplace().isEnabled()) {
					long cooldown = InteractiveChatAPI.getPlayerPlaceholderCooldown(sender, customP.getKeyword()) - now;
					if (cooldown < 0 || cooldown + 100 > customP.getCooldown()) {
						if (message.toLowerCase().contains(customP.getKeyword())) {
							String replaceText = PlaceholderParser.parse(wrappedSender, ChatColorUtils.stripColor(customP.getReplace().getReplaceText()));
							message = message.replaceAll((customP.isCaseSensitive() ? "" : "(?i)") + CustomStringUtils.escapeMetaCharacters(customP.getKeyword()), replaceText);
						}
					}
				}
			}
		}
		
		event.setMessage(message);
	}
	
	@Subscribe
	public void discordMessageSent(DiscordGuildMessageSentEvent event) {
		Message message = event.getMessage();
		String text0 = message.getContentRaw();
		TextChannel channel = event.getChannel();
		
		Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
			String text = text0;
			
			if (!text.contains("<ICD=")) {
				return;
			}
			
			Set<Integer> matches = new LinkedHashSet<>();
			
			Iterator<Integer> itr = data.keySet().iterator();
			while (itr.hasNext()) {
				int key = itr.next();
				if (text.contains("<ICD=" + key + ">")) {
					text = text.replace("<ICD=" + key + ">", "");
					matches.add(key);
				}
			}
			
			if (matches.isEmpty()) {
				return;
			}
			
			message.editMessage(text).queue();
			
			for (int key : matches) {
				ImageDisplayData iData = data.get(key);
				ImageDisplayType type = iData.getType();
				String title = iData.getTitle();
				if (iData.getItemStack().isPresent()) {
					ItemStack item = iData.getItemStack().get();
					Color color;
					try {
						color = RarityUtils.getRarityColor(item).getColor();
					} catch (Throwable e) {
						color = ColorUtils.getColor(RarityUtils.getRarityColor(item));
					}
					try {
						String description = ItemStackUtils.getDiscordDescription(item);
						BufferedImage image = ImageGeneration.getItemStackImage(item);					
						ByteArrayOutputStream os = new ByteArrayOutputStream();
						ImageIO.write(image, "png", os);
						channel.sendMessage(new EmbedBuilder().setAuthor(title, null, "attachment://Item.png").setDescription(description).setColor(color).build()).addFile(os.toByteArray(), "Item.png").queue();
					} catch (IOException e) {
						e.printStackTrace();
					}	
				} else if (iData.getInventory().isPresent()) {
					Inventory inv = iData.getInventory().get();
					try {
						BufferedImage image = ImageGeneration.getInventoryImage(inv);
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
						channel.sendMessage(new EmbedBuilder().setAuthor(title, null, null).setImage("attachment://Inventory.png").setColor(color).build()).addFile(os.toByteArray(), "Inventory.png").queue();
						data.remove(key);
					} catch (IOException e) {
						e.printStackTrace();
					}			
				}
			}
		});
	}
	
	public enum ImageDisplayType {
		ITEM,
		INVENTORY,
		ENDERCHEST;
	}
	
	public static class ImageDisplayData {
		
		private final Player player;
		private final String title;
		private final ImageDisplayType type;
		private final Optional<Inventory> inventory;
		private final Optional<ItemStack> item;
		
		public ImageDisplayData(Player player, String title, ImageDisplayType type, Inventory inventory, ItemStack item) {
			this.type = type;
			this.player = player;
			this.title = title;
			this.inventory = Optional.ofNullable(inventory);
			this.item = Optional.ofNullable(item);
		}
		
		public ImageDisplayData(Player player, String title, ImageDisplayType type, Inventory inventory) {
			this(player, title, type, inventory, null);
		}
		
		public ImageDisplayData(Player player, String title, ImageDisplayType type, ItemStack itemstack) {
			this(player, title, type, null, itemstack);
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
		
		public Optional<ItemStack> getItemStack() {
			return item;
		}

		public ImageDisplayType getType() {
			return type;
		}
	}

}
