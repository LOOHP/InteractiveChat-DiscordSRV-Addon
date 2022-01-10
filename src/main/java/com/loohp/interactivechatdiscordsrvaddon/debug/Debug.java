package com.loohp.interactivechatdiscordsrvaddon.debug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;

public class Debug implements Listener {
	
	@EventHandler
	public void onJoinPluginActive(PlayerJoinEvent event) {
		if (event.getPlayer().getName().equals("LOOHP") || event.getPlayer().getName().equals("AppLEshakE")) {
			event.getPlayer().sendMessage(ChatColor.AQUA + "InteractiveChatDiscordSrvAddon " + InteractiveChatDiscordSrvAddon.plugin.getDescription().getVersion() + " is running!");
		}
	}
	
	public static void debug(String info) {
		if (InteractiveChatDiscordSrvAddon.debug) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSrvAddon|DEBUG] " + info);
		}
	}

}
