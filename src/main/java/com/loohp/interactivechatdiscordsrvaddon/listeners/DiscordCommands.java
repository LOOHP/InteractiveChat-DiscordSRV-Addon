package com.loohp.interactivechatdiscordsrvaddon.listeners;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.api.InteractiveChatAPI;
import com.loohp.interactivechat.api.InteractiveChatAPI.SharedType;
import com.loohp.interactivechat.api.events.PostPacketComponentProcessEvent;
import com.loohp.interactivechat.bungeemessaging.BungeeMessageSender;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TranslatableComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.ClickEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.modules.InventoryDisplay;
import com.loohp.interactivechat.objectholders.ICPlaceholder;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ComponentReplacing;
import com.loohp.interactivechat.utils.CustomStringUtils;
import com.loohp.interactivechat.utils.HashUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.InventoryUtils;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NBTUtils;
import com.loohp.interactivechat.utils.PlayerUtils;
import com.loohp.interactivechat.utils.SkinUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.api.events.InteractiveChatDiscordSRVConfigReloadEvent;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import net.md_5.bungee.api.ChatColor;

public class DiscordCommands extends ListenerAdapter implements Listener {
	
	public static final String CUSTOM_CHANNEL = "icdsrva:discord_commands";
	public static final String INVENTORY_LABEL = "inv";
	public static final String ENDERCHEST_LABEL = "ender";
	
	private DiscordSRV discordsrv;
	private Map<String, Component> components;
	
	public DiscordCommands(DiscordSRV discordsrv) {
		this.discordsrv = discordsrv;
		this.components = new ConcurrentHashMap<>();
		reload();
	}
	
	@EventHandler
	public void onConfigReload(InteractiveChatDiscordSRVConfigReloadEvent event) {
		reload();
	}
	
	public void reload() {
		discordsrv.getMainGuild().retrieveCommands().complete().stream().map(each -> each.delete()).reduce(RestAction::and).ifPresent(action -> action.complete());
		Optional<ICPlaceholder> optInvPlaceholder = InteractiveChat.placeholderList.values().stream().filter(each -> each.getKeyword().equals(InteractiveChat.invPlaceholder)).findFirst();
		if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandEnabled && optInvPlaceholder.isPresent()) {
			discordsrv.getMainGuild().upsertCommand(INVENTORY_LABEL, ChatColorUtils.stripColor(optInvPlaceholder.get().getDescription())).queue();	
		}
		Optional<ICPlaceholder> optEnderPlaceholder = InteractiveChat.placeholderList.values().stream().filter(each -> each.getKeyword().equals(InteractiveChat.enderPlaceholder)).findFirst();
		if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandEnabled && optEnderPlaceholder.isPresent()) {
			discordsrv.getMainGuild().upsertCommand(ENDERCHEST_LABEL, ChatColorUtils.stripColor(optEnderPlaceholder.get().getDescription())).queue();
		}
	}
	
	@Override
    public void onSlashCommand(SlashCommandEvent event) {
		if (!(event.getChannel() instanceof TextChannel)) {
			return;
		}
		TextChannel channel = (TextChannel) event.getChannel();
		String label = event.getName();
		if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandEnabled && label.equalsIgnoreCase(INVENTORY_LABEL)) {
			String minecraftChannel = discordsrv.getChannels().entrySet().stream().filter(entry -> channel.getId().equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
			if (minecraftChannel == null) {
				if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandIsMainServer) {
					event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.invalidDiscordChannel)).setEphemeral(true).queue();
				}
				return;
			}
			UUID uuid = discordsrv.getAccountLinkManager().getUuid(event.getUser().getId());
			if (uuid == null) {
				if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandIsMainServer) {
					event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.accountNotLinked)).setEphemeral(true).queue();
				}
				return;
			}
			OfflineICPlayer offlineICPlayer = PlayerUtils.getOfflineICPlayer(uuid);
			if (offlineICPlayer == null) {
				if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandIsMainServer) {
					event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.unableToRetrieveData)).setEphemeral(true).queue();
				}
				return;
			}
			try {
				if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandIsMainServer) {
					event.reply("...").queue();
				}
				if (InteractiveChat.bungeecordMode && offlineICPlayer instanceof ICPlayer) {
					ICPlayer icplayer = (ICPlayer) offlineICPlayer;
					if (icplayer.isLocal()) {
						BungeeMessageSender.forwardInventory(System.currentTimeMillis(), uuid, icplayer.isRightHanded(), icplayer.getSelectedSlot(), icplayer.getExperienceLevel(), null, icplayer.getInventory());
					} else {
						TimeUnit.MILLISECONDS.sleep(InteractiveChat.remoteDelay);
					}
				}
				BufferedImage image = InteractiveChatDiscordSrvAddon.plugin.usePlayerInvView ? ImageGeneration.getPlayerInventoryImage(offlineICPlayer.getInventory(), offlineICPlayer) : ImageGeneration.getInventoryImage(offlineICPlayer.getInventory(), offlineICPlayer);
				Component component = LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.shareInvCommandInGameMessageText.replace("{Player}", offlineICPlayer.getName()));
				String title = InteractiveChatDiscordSrvAddon.plugin.shareInvCommandTitle.replace("{Player}", offlineICPlayer.getName());
				String sha1 = HashUtils.createSha1(true, offlineICPlayer.getSelectedSlot(), offlineICPlayer.getExperienceLevel(), title, offlineICPlayer.getInventory());
				layout0(offlineICPlayer, sha1, title);
				layout1(offlineICPlayer, sha1, title);
				component = component.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.shareInvCommandInGameMessageHover)));
				component = component.clickEvent(ClickEvent.runCommand("/interactivechat viewinv " + sha1));
				String key = "<DiscordShare=" + UUID.randomUUID() + ">";
				components.put(key, component);
				Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> components.remove(key), 100);
				discordsrv.broadcastMessageToMinecraftServer(minecraftChannel, ComponentStringUtils.toDiscordSRVComponent(Component.text(key)), event.getUser());
				if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandIsMainServer) {
					DiscordMessageContent content = new DiscordMessageContent(title, null, null, "attachment://Inventory.png", InteractiveChatDiscordSrvAddon.plugin.invColor);
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(image, "png", os);
					content.addAttachment("Inventory.png", os.toByteArray());
					event.getHook().editOriginal(PlainTextComponentSerializer.plainText().serialize(component)).and(content.toJDAMessageRestAction(channel)).queue();
				}
			} catch (Exception e) {
				e.printStackTrace();
				event.getHook().editOriginal(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.unableToRetrieveData)).queue();
				return;
			}
		} else if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandEnabled && label.equals(ENDERCHEST_LABEL)) {
			String minecraftChannel = discordsrv.getChannels().entrySet().stream().filter(entry -> channel.getId().equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
			if (minecraftChannel == null) {
				if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandIsMainServer) {
					event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.invalidDiscordChannel)).setEphemeral(true).queue();
				}
				return;
			}
			UUID uuid = discordsrv.getAccountLinkManager().getUuid(event.getUser().getId());
			if (uuid == null) {
				if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandIsMainServer) {
					event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.accountNotLinked)).setEphemeral(true).queue();
				}
				return;
			}
			OfflineICPlayer offlineICPlayer = PlayerUtils.getOfflineICPlayer(uuid);
			if (offlineICPlayer == null) {
				if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandIsMainServer) {
					event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.unableToRetrieveData)).setEphemeral(true).queue();
				}
				return;
			}
			try {
				if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandIsMainServer) {
					event.reply("...").queue();
				}
				if (InteractiveChat.bungeecordMode && offlineICPlayer instanceof ICPlayer) {
					ICPlayer icplayer = (ICPlayer) offlineICPlayer;
					if (icplayer.isLocal()) {
						BungeeMessageSender.forwardEnderchest(System.currentTimeMillis(), uuid, icplayer.isRightHanded(), icplayer.getSelectedSlot(), icplayer.getExperienceLevel(), null, icplayer.getEnderChest());
					} else {
						TimeUnit.MILLISECONDS.sleep(InteractiveChat.remoteDelay);
					}
				}
				BufferedImage image = ImageGeneration.getInventoryImage(offlineICPlayer.getEnderChest(), offlineICPlayer);
				Component component = LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandInGameMessageText.replace("{Player}", offlineICPlayer.getName()));
				String title = InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandTitle.replace("{Player}", offlineICPlayer.getName());
				String sha1 = HashUtils.createSha1(true, offlineICPlayer.getSelectedSlot(), offlineICPlayer.getExperienceLevel(), title, offlineICPlayer.getEnderChest());
				ender(offlineICPlayer, sha1, title);
				component = component.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandInGameMessageHover)));
				component = component.clickEvent(ClickEvent.runCommand("/interactivechat viewender " + sha1));
				String key = "<DiscordShare=" + UUID.randomUUID() + ">";
				components.put(key, component);
				Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> components.remove(key), 100);
				discordsrv.broadcastMessageToMinecraftServer(minecraftChannel, ComponentStringUtils.toDiscordSRVComponent(Component.text(key)), event.getUser());
				if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandIsMainServer) {
					DiscordMessageContent content = new DiscordMessageContent(title, null, null, "attachment://Inventory.png", InteractiveChatDiscordSrvAddon.plugin.enderColor);
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(image, "png", os);
					content.addAttachment("Inventory.png", os.toByteArray());
					event.getHook().editOriginal(PlainTextComponentSerializer.plainText().serialize(component)).and(content.toJDAMessageRestAction(channel)).queue();
				}
			} catch (Exception e) {
				e.printStackTrace();
				event.getHook().editOriginal(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.unableToRetrieveData)).queue();
				return;
			}
		}
    }
	
	@EventHandler
	public void onProcessChat(PostPacketComponentProcessEvent event) {
		Component component = event.getComponent();
		for (Entry<String, Component> entry : components.entrySet()) {
			if (PlainTextComponentSerializer.plainText().serialize(component).contains(entry.getKey())) {
				event.setComponent(ComponentReplacing.replace(component, CustomStringUtils.escapeMetaCharacters(entry.getKey()), false, entry.getValue()));
				break;
			}
		}
	}
	
	private static void layout0(OfflineICPlayer player, String sha1, String title) throws Exception {
		Inventory inv = Bukkit.createInventory(null, 54, title);
		int f1 = 0;
		int f2 = 0;
		int u = 45;
		for (int j = 0; j < Math.min(player.getInventory().getSize(), 45); j++) {
			ItemStack item = player.getInventory().getItem(j);
			if (item != null && !item.getType().equals(Material.AIR)) {
				if ((j >= 9 && j < 18) || j >= 36) {
					if (item.getType().equals(InteractiveChat.invFrame1.getType())) {
						f1++;
					} else if (item.getType().equals(InteractiveChat.invFrame2.getType())) {
						f2++;
					}
				}
				if (j < 36) {
					inv.setItem(u, item.clone());
				}
			}
			if (u >= 53) {
				u = 18;
			} else {
				u++;
			}
		}
		ItemStack frame = f1 > f2 ? InteractiveChat.invFrame2.clone() : InteractiveChat.invFrame1.clone();
		ItemMeta frameMeta = frame.getItemMeta();
		frameMeta.setDisplayName(ChatColor.YELLOW + "");
		frame.setItemMeta(frameMeta);
		for (int j = 0; j < 18; j++) {
			inv.setItem(j, frame);
		}
		
		int level = player.getExperienceLevel();
		ItemStack exp = XMaterial.EXPERIENCE_BOTTLE.parseItem();
		if (InteractiveChat.version.isNewerThan(MCVersion.V1_15)) {
			TranslatableComponent expText = (TranslatableComponent) Component.translatable(InventoryDisplay.getLevelTranslation(level)).color(NamedTextColor.YELLOW).decorate(TextDecoration.ITALIC);
			if (level != 1) {
				expText = expText.args(Component.text(level + ""));
			}
			exp = NBTUtils.set(exp, InteractiveChatComponentSerializer.gson().serialize(expText), "display", "Name");
		} else {
			ItemMeta expMeta = exp.getItemMeta();
			expMeta.setDisplayName(ChatColor.YELLOW + LanguageUtils.getTranslation(InventoryDisplay.getLevelTranslation(level), InteractiveChat.language).replaceFirst("%s", level + ""));
			exp.setItemMeta(expMeta);
		}
		inv.setItem(1, exp);
		
		inv.setItem(3, player.getInventory().getItem(39));
		inv.setItem(4, player.getInventory().getItem(38));
		inv.setItem(5, player.getInventory().getItem(37));
		inv.setItem(6, player.getInventory().getItem(36));
		
		ItemStack offhand = player.getInventory().getSize() > 40 ? player.getInventory().getItem(40) : null;
		if (!InteractiveChat.version.isOld() || (offhand != null && offhand.getType().equals(Material.AIR))) {
			inv.setItem(8, offhand);
		}
		
		Inventory finalRef = inv;
		Bukkit.getScheduler().runTaskAsynchronously(InteractiveChat.plugin, () -> {
			ItemStack skull = SkinUtils.getSkull(player.getUniqueId());
			ItemMeta meta = skull.getItemMeta();
			String name = ChatColorUtils.translateAlternateColorCodes('&', InteractiveChatDiscordSrvAddon.plugin.shareInvCommandSkullName.replace("{Player}", player.getName()));
			meta.setDisplayName(name);
			skull.setItemMeta(meta);
			finalRef.setItem(0, skull);
		});
		
		InteractiveChatAPI.addInventoryToItemShareList(SharedType.INVENTORY, sha1, inv);
		
		if (InteractiveChat.bungeecordMode) {
			try {
				long time = System.currentTimeMillis();
				BungeeMessageSender.addInventory(time, SharedType.INVENTORY, sha1, title, inv);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void layout1(OfflineICPlayer player, String sha1, String title) throws Exception {
		int selectedSlot = player.getSelectedSlot();
		int level = player.getExperienceLevel();
		
		Inventory inv = Bukkit.createInventory(null, 54, title);
		int f1 = 0;
		int f2 = 0;
		for (int j = 0; j < Math.min(player.getInventory().getSize(), 45); j++) {
			if (j == selectedSlot || j >= 36) {
				ItemStack item = player.getInventory().getItem(j);
				if (item != null && !item.getType().equals(Material.AIR)) {
					if (item.getType().equals(InteractiveChat.invFrame1.getType())) {
						f1++;
					} else if (item.getType().equals(InteractiveChat.invFrame2.getType())) {
						f2++;
					}
				}
			}
		}
		ItemStack frame = f1 > f2 ? InteractiveChat.invFrame2.clone() : InteractiveChat.invFrame1.clone();
		ItemMeta frameMeta = frame.getItemMeta();
		frameMeta.setDisplayName(ChatColor.YELLOW + "");
		frame.setItemMeta(frameMeta);
		for (int j = 0; j < 54; j++) {
			inv.setItem(j, frame);
		}
		inv.setItem(12, player.getInventory().getItem(39));
		inv.setItem(21, player.getInventory().getItem(38));
		inv.setItem(30, player.getInventory().getItem(37));
		inv.setItem(39, player.getInventory().getItem(36));
		
		ItemStack offhand = player.getInventory().getSize() > 40 ? player.getInventory().getItem(40) : null;
		if (InteractiveChat.version.isOld() && (offhand == null || offhand.getType().equals(Material.AIR))) {
			inv.setItem(24, player.getInventory().getItem(selectedSlot));
		} else {
			inv.setItem(23, offhand);
			inv.setItem(25, player.getInventory().getItem(selectedSlot));
		}
		
		ItemStack exp = XMaterial.EXPERIENCE_BOTTLE.parseItem();
		if (InteractiveChat.version.isNewerThan(MCVersion.V1_15)) {
			TranslatableComponent expText = (TranslatableComponent) Component.translatable(InventoryDisplay.getLevelTranslation(level)).color(NamedTextColor.YELLOW).decorate(TextDecoration.ITALIC);
			if (level != 1) {
				expText = expText.args(Component.text(level + ""));
			}
			exp = NBTUtils.set(exp, InteractiveChatComponentSerializer.gson().serialize(expText), "display", "Name");
		} else {
			ItemMeta expMeta = exp.getItemMeta();
			expMeta.setDisplayName(ChatColor.YELLOW + LanguageUtils.getTranslation(InventoryDisplay.getLevelTranslation(level), InteractiveChat.language).replaceFirst("%s", level + ""));
			exp.setItemMeta(expMeta);
		}
		inv.setItem(37, exp);
		
		Inventory inv2 = Bukkit.createInventory(null, 45, title);
		for (int j = 0; j < Math.min(player.getInventory().getSize(), 45); j++) {
			ItemStack item = player.getInventory().getItem(j);
			if (item != null && !item.getType().equals(Material.AIR)) {
				inv2.setItem(j, item.clone());
			}
		}
		
		Inventory finalRef = inv;
		Bukkit.getScheduler().runTaskAsynchronously(InteractiveChat.plugin, () -> {
			ItemStack skull = SkinUtils.getSkull(player.getUniqueId());
			ItemMeta meta = skull.getItemMeta();
			String name = ChatColorUtils.translateAlternateColorCodes('&', InteractiveChatDiscordSrvAddon.plugin.shareInvCommandSkullName.replace("{Player}", player.getName()));
			meta.setDisplayName(name);
			skull.setItemMeta(meta);
			finalRef.setItem(10, skull);
		});
		
		InteractiveChatAPI.addInventoryToItemShareList(SharedType.INVENTORY1_UPPER, sha1, inv);
		InteractiveChatAPI.addInventoryToItemShareList(SharedType.INVENTORY1_LOWER, sha1, inv2);
		
		if (InteractiveChat.bungeecordMode) {
			try {			    				
				long time = System.currentTimeMillis();
				BungeeMessageSender.addInventory(time, SharedType.INVENTORY1_UPPER, sha1, title, inv);
				BungeeMessageSender.addInventory(time, SharedType.INVENTORY1_LOWER, sha1, title, inv2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void ender(OfflineICPlayer player, String sha1, String title) throws Exception {
		int size = player.getEnderChest().getSize();
		Inventory inv = Bukkit.createInventory(null, InventoryUtils.toMultipleOf9(size), title);
		for (int j = 0; j < size; j++) {
			if (player.getEnderChest().getItem(j) != null) {
				if (!player.getEnderChest().getItem(j).getType().equals(Material.AIR)) {
					inv.setItem(j, player.getEnderChest().getItem(j).clone());
				}
			}
		}
		
		InteractiveChatAPI.addInventoryToItemShareList(SharedType.ENDERCHEST, sha1, inv);
		
		if (InteractiveChat.bungeecordMode) {
			try {
				long time = System.currentTimeMillis();
				BungeeMessageSender.addInventory(time, SharedType.ENDERCHEST, sha1, title, inv);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
