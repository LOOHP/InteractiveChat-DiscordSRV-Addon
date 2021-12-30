package com.loohp.interactivechatdiscordsrvaddon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.ClickEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechatdiscordsrvaddon.api.events.InteractiveChatDiscordSRVConfigReloadEvent;
import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents;
import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents.DiscordAttachmentData;
import com.loohp.interactivechatdiscordsrvaddon.updater.Updater;
import com.loohp.interactivechatdiscordsrvaddon.updater.Updater.UpdaterResponse;

import net.md_5.bungee.api.ChatColor;

public class Commands implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!label.equalsIgnoreCase("interactivechatdiscordsrv") && !label.equalsIgnoreCase("icd")) {
			return true;
		}
		
		if (args.length == 0) {
			sender.sendMessage(ChatColor.AQUA + "InteractiveChat DiscordSRV Addon written by LOOHP!");
			sender.sendMessage(ChatColor.GOLD + "You are running ICDiscordSRVAddon version: " + InteractiveChatDiscordSrvAddon.plugin.getDescription().getVersion());
			Component mcheads = LegacyComponentSerializer.legacySection().deserialize(ChatColor.GRAY + "Thanks to " + ChatColor.YELLOW + "MCHeads " + ChatColor.GRAY + "for providing Minecraft avatars.");
			mcheads = mcheads.clickEvent(ClickEvent.openUrl("https://mc-heads.net"));
			InteractiveChat.sendMessage(sender, mcheads);
			return true;
		}
		
		if (args[0].equalsIgnoreCase("status")) {
			if (sender.hasPermission("interactivechatdiscordsrv.status")) {
				sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.defaultResourceHashLang.replaceFirst("%s", InteractiveChatDiscordSrvAddon.plugin.defaultResourceHash));
				sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.loadedResourcesLang);
				List<String> list = new ArrayList<>(InteractiveChatDiscordSrvAddon.plugin.resourceStatus.keySet());
				ListIterator<String> itr = list.listIterator(list.size());
				while (itr.hasPrevious()) {
					String key = itr.previous();
					if (InteractiveChatDiscordSrvAddon.plugin.resourceStatus.getOrDefault(key, false)) {
						sender.sendMessage(ChatColor.GREEN + " - " + key);
					} else {
						sender.sendMessage(ChatColor.RED + " - " + key);
					}
				}
			} else {
				sender.sendMessage(InteractiveChat.noPermissionMessage);
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("reloadconfig")) {
			if (sender.hasPermission("interactivechatdiscordsrv.reloadconfig")) {
				InteractiveChatDiscordSrvAddon.plugin.reloadConfig();
				Bukkit.getPluginManager().callEvent(new InteractiveChatDiscordSRVConfigReloadEvent());
				sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.reloadConfigMessage);
			} else {
				sender.sendMessage(InteractiveChat.noPermissionMessage);
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("reloadtexture")) {
			boolean redownload = false;
			if (Arrays.asList(args).contains("--redownload")) {
				redownload = true;
			}
			if (sender.hasPermission("interactivechatdiscordsrv.reloadtexture")) {
				InteractiveChatDiscordSrvAddon.plugin.reloadTextures(redownload, sender);
				sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.reloadTextureMessage);
			} else {
				sender.sendMessage(InteractiveChat.noPermissionMessage);
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("update")) {
			if (sender.hasPermission("interactivechatdiscordsrv.update")) {
				sender.sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] InteractiveChat DiscordSRV Addon written by LOOHP!");
				sender.sendMessage(ChatColor.GOLD + "[ICDiscordSrvAddon] You are running ICDiscordSRVAddon version: " + InteractiveChatDiscordSrvAddon.plugin.getDescription().getVersion());
				Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
					UpdaterResponse version = Updater.checkUpdate();
					if (version.getResult().equals("latest")) {
						if (version.isDevBuildLatest()) {
							sender.sendMessage(ChatColor.GREEN + "[ICDiscordSrvAddon] You are running the latest version!");
						} else {
							Updater.sendUpdateMessage(sender, version.getResult(), version.getSpigotPluginId(), true);
						}
					} else {
						Updater.sendUpdateMessage(sender, version.getResult(), version.getSpigotPluginId());
					}
				});
			} else {
				sender.sendMessage(InteractiveChat.noPermissionMessage);
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("imagemap")) {
			if (args.length > 1 && sender instanceof Player) {
				try {
					DiscordAttachmentData data = InboundToGameEvents.DATA.get(UUID.fromString(args[1]));
					if (data != null && data.isImage()) {
						data.getImageMap().show((Player) sender);
					}
				} catch (Exception e) {
					sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.linkExpired);
				}
			}
			return true;
		}
		
		sender.sendMessage(ChatColorUtils.translateAlternateColorCodes('&', Bukkit.spigot().getConfig().getString("messages.unknown-command")));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> tab = new LinkedList<>();
		if (!label.equalsIgnoreCase("interactivechatdiscordsrv") && !label.equalsIgnoreCase("icd")) {
			return tab;
		}
		
		switch (args.length) {
		case 0:
			if (sender.hasPermission("interactivechatdiscordsrv.reloadconfig")) {
				tab.add("reloadconfig");
			}
			if (sender.hasPermission("interactivechatdiscordsrv.reloadtexture")) {
				tab.add("reloadtexture");
			}
			if (sender.hasPermission("interactivechatdiscordsrv.update")) {
				tab.add("update");
			}
			if (sender.hasPermission("interactivechatdiscordsrv.status")) {
				tab.add("status");
			}
			return tab;
		case 1:
			if (sender.hasPermission("interactivechatdiscordsrv.reloadconfig")) {
				if ("reloadconfig".startsWith(args[0].toLowerCase())) {
					tab.add("reloadconfig");
				}
			}
			if (sender.hasPermission("interactivechatdiscordsrv.reloadtexture")) {
				if ("reloadtexture".startsWith(args[0].toLowerCase())) {
					tab.add("reloadtexture");
				}
			}
			if (sender.hasPermission("interactivechatdiscordsrv.update")) {
				if ("update".startsWith(args[0].toLowerCase())) {
					tab.add("update");
				}
			}
			if (sender.hasPermission("interactivechatdiscordsrv.status")) {
				if ("status".startsWith(args[0].toLowerCase())) {
					tab.add("status");
				}
			}
			return tab;
		case 2:
			if (sender.hasPermission("interactivechatdiscordsrv.reloadtexture")) {
				if ("reloadtexture".equals(args[0].toLowerCase())) {
					if ("--redownload".startsWith(args[1].toLowerCase())) {
						tab.add("--redownload");
					}
				}
			}
			return tab;
		default:
			return tab;
		}
	}

}
