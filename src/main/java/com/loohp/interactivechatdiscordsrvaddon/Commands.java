package com.loohp.interactivechatdiscordsrvaddon;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.api.InteractiveChatAPI;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ComponentStyling;
import com.loohp.interactivechatdiscordsrvaddon.api.events.InteractiveChatDiscordSRVConfigReloadEvent;
import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents;
import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents.DiscordAttachmentData;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackInfo;
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
			return true;
		}
		
		if (args[0].equalsIgnoreCase("status")) {
			if (sender.hasPermission("interactivechatdiscordsrv.status")) {
				sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.defaultResourceHashLang.replaceFirst("%s", InteractiveChatDiscordSrvAddon.plugin.defaultResourceHash));
				sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.loadedResourcesLang);
				for (ResourcePackInfo info : InteractiveChatDiscordSrvAddon.plugin.resourceManager.getResourcePackInfo()) {
					String name = info.getName();
					if (info.getStatus()) {
						Component component = Component.text(" - " + name).color(NamedTextColor.GREEN);
						component = component.hoverEvent(HoverEvent.showText(info.getDescription()));
						InteractiveChatAPI.sendMessage(sender, component);
						if (!(sender instanceof Player)) {
							for (Component each : ComponentStyling.splitAtLineBreaks(info.getDescription())) {
								InteractiveChatAPI.sendMessage(sender, Component.text("   - ").color(NamedTextColor.GRAY).append(each));
							}
						}
					} else {
						Component component = Component.text(" - " + name).color(NamedTextColor.RED);
						if (info.getRejectedReason() != null) {
							component = component.hoverEvent(HoverEvent.showText(Component.text(info.getRejectedReason()).color(NamedTextColor.RED)));
						}
						InteractiveChatAPI.sendMessage(sender, component);
						if (!(sender instanceof Player)) {
							InteractiveChatAPI.sendMessage(sender, Component.text("   - ").append(Component.text(info.getRejectedReason()).color(NamedTextColor.RED)).color(NamedTextColor.RED));
						}
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
			List<String> argList = Arrays.asList(args);
			boolean clean = argList.contains("--reset");
			boolean redownload = argList.contains("--redownload") || clean;
			if (sender.hasPermission("interactivechatdiscordsrv.reloadtexture")) {
				sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.reloadTextureMessage);
				InteractiveChatDiscordSrvAddon.plugin.reloadTextures(redownload, clean, sender);
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
					if ("--reset".startsWith(args[1].toLowerCase())) {
						tab.add("--reset");
					}
				}
			}
			return tab;
		default:
			return tab;
		}
	}

}
