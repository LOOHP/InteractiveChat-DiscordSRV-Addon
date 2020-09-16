package com.loohp.interactivechatdiscordsrvaddon.Updater;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Updater implements Listener {
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
			if (InteractiveChatDiscordSrvAddon.plugin.UpdaterEnabled) {
				Player player = event.getPlayer();
				if (player.hasPermission("interactivechatdiscordsrv.update")) {
					String version = Updater.checkUpdate();
					if (!version.equals("latest")) {
						Updater.sendUpdateMessage(player, version);
					}
				}
			}
		}, 100);
	}
	
	@SuppressWarnings("deprecation")
	public static void sendUpdateMessage(CommandSender sender, String version) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage(ChatColor.YELLOW + "[ICDiscordSRVAddon] A new version is available on SpigotMC: " + version);
			TextComponent url = new TextComponent(ChatColor.GOLD + "https://www.spigotmc.org/resources/83917");
			url.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.AQUA + "Click me!").create()));
			url.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/83917"));
			player.spigot().sendMessage(url);
		} else {
			sender.sendMessage(ChatColor.YELLOW + "[ICDiscordSRVAddon] A new version is available on SpigotMC: " + version);
			sender.sendMessage(ChatColor.GOLD + "Download: https://www.spigotmc.org/resources/83917");
		}
	}

    public static String checkUpdate() {
        try {
            String localPluginVersion = InteractiveChatDiscordSrvAddon.plugin.getDescription().getVersion();
            String spigotPluginVersion = readStringFromURL("https://api.spigotmc.org/legacy/update.php?resource=83917");
            Version current = new Version(localPluginVersion);
            Version spigotmc = new Version(spigotPluginVersion);
            if (!spigotPluginVersion.isEmpty() && current.compareTo(spigotmc) < 0) {
                return spigotPluginVersion;
            } else {
            	return "latest";
            }
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSRVAddon] Failed to check for an update on SpigotMC.. It could be an internet issue or SpigotMC is down. If you want disable the update checker, you can disable in config.yml, but we still highly-recommend you to keep your plugin up to date!");
        }
        return "error";
    }
    
    public static String readStringFromURL(String requestURL) throws IOException {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(), StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
    
}