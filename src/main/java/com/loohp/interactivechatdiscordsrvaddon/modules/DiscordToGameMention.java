package com.loohp.interactivechatdiscordsrvaddon.modules;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.SoundUtils;
import com.loohp.interactivechat.utils.TitleUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;

import net.md_5.bungee.api.ChatColor;

public class DiscordToGameMention {
	
	public static void playTitleScreen(String sender, String channelName, String guild, Player reciever) {
		String title = ChatColorUtils.translateAlternateColorCodes('&', InteractiveChatDiscordSrvAddon.plugin.getConfig().getString("DiscordMention.MentionedTitle").replace("{DiscordUser}", sender).replace("{TextChannel}", "#" + channelName).replace("{Guild}", guild));
		String subtitle = ChatColorUtils.translateAlternateColorCodes('&', InteractiveChatDiscordSrvAddon.plugin.getConfig().getString("DiscordMention.DiscordMentionSubtitle").replace("{DiscordUser}", sender).replace("{TextChannel}", "#" + channelName).replace("{Guild}", guild));
		String actionbar = ChatColorUtils.translateAlternateColorCodes('&', InteractiveChatDiscordSrvAddon.plugin.getConfig().getString("DiscordMention.DiscordMentionActionbar").replace("{DiscordUser}", sender).replace("{TextChannel}", "#" + channelName).replace("{Guild}", guild));
		
		String settings = InteractiveChatDiscordSrvAddon.plugin.getConfig().getString("DiscordMention.MentionedSound");
		Sound sound = null;
		float volume = 3.0F;
		float pitch = 1.0F;
		
		String[] settingsArgs = settings.split(":");
		if (settingsArgs.length == 3) {
			settings = settingsArgs[0];
			try {
				volume = Float.parseFloat(settingsArgs[1]);
			} catch (Exception ignore) {}
			try {
				pitch = Float.parseFloat(settingsArgs[2]);
			} catch (Exception ignore) {}
		} else if (settingsArgs.length > 0) {
			settings = settingsArgs[0];
		}
		
		sound = SoundUtils.parseSound(settings);
		if (sound == null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Invalid Sound: " + settings);
		}
			
		int time = (int) Math.round(InteractiveChatDiscordSrvAddon.plugin.getConfig().getDouble("DiscordMention.MentionedTitleDuration") * 20);
		TitleUtils.sendTitle(reciever, title, subtitle, actionbar, 10, time, 20);
		if (sound != null) {
			reciever.playSound(reciever.getLocation(), sound, volume, pitch);
		}
	}

}
