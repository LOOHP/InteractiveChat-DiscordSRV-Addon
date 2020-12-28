package com.loohp.interactivechatdiscordsrvaddon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.Utils.ChatColorUtils;
import com.loohp.interactivechatdiscordsrvaddon.Listeners.DiscordAttachmentEvents;
import com.loohp.interactivechatdiscordsrvaddon.Listeners.DiscordAttachmentEvents.DiscordAttachmentData;
import com.loohp.interactivechatdiscordsrvaddon.Updater.Updater;
import com.loohp.interactivechatdiscordsrvaddon.Updater.Updater.UpdaterResponse;

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
		
		if (args[0].equalsIgnoreCase("reloadconfig")) {
			if (sender.hasPermission("interactivechatdiscordsrv.reloadconfig")) {
				InteractiveChatDiscordSrvAddon.plugin.reloadConfig();
				sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.reloadConfigMessage);
			} else {
				sender.sendMessage(InteractiveChat.NoPermission);
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("reloadtexture")) {
			if (sender.hasPermission("interactivechatdiscordsrv.reloadtexture")) {
				InteractiveChatDiscordSrvAddon.plugin.reloadTextures();
				sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.reloadTextureMessage);
			} else {
				sender.sendMessage(InteractiveChat.NoPermission);
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("update")) {
			if (sender.hasPermission("interactivechatdiscordsrv.update")) {
				sender.sendMessage(ChatColor.AQUA + "[ICDiscordSRVAddon] InteractiveChat DiscordSRV Addon written by LOOHP!");
				sender.sendMessage(ChatColor.GOLD + "[ICDiscordSRVAddon] You are running ICDiscordSRVAddon version: " + InteractiveChatDiscordSrvAddon.plugin.getDescription().getVersion());
				Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
					UpdaterResponse version = Updater.checkUpdate();
					if (version.getResult().equals("latest")) {
						if (version.isDevBuildLatest()) {
							sender.sendMessage(ChatColor.GREEN + "[ICDiscordSRVAddon] You are running the latest version!");
						} else {
							Updater.sendUpdateMessage(sender, version.getResult(), version.getSpigotPluginId(), true);
						}
					} else {
						Updater.sendUpdateMessage(sender, version.getResult(), version.getSpigotPluginId());
					}
				});
			} else {
				sender.sendMessage(InteractiveChat.NoPermission);
			}
			return true;
		}
		
		if (args[0].equalsIgnoreCase("imagemap")) {
			if (args.length > 1 && sender instanceof Player) {
				Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
					Optional<DiscordAttachmentData> opt = DiscordAttachmentEvents.DATA.values().stream().filter(each -> each.getUniqueId().toString().equalsIgnoreCase(args[1])).findFirst();
					if (opt.isPresent() && opt.get().isImage()) {
						Bukkit.getScheduler().runTask(InteractiveChatDiscordSrvAddon.plugin, () -> opt.get().getImageMap().show((Player) sender));
					} else {
						sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.linkExpired);
					}
				});
			}
			return true;
		}
		
		sender.sendMessage(ChatColorUtils.translateAlternateColorCodes('&', Bukkit.spigot().getConfig().getString("messages.unknown-command")));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> tab = new ArrayList<String>();
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
			return tab;
		default:
			return tab;
		}
	}

}
