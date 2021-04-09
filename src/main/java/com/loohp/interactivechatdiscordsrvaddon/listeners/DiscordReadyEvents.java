package com.loohp.interactivechatdiscordsrvaddon.listeners;

import org.bukkit.Bukkit;

import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.GuildChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import net.md_5.bungee.api.ChatColor;

public class DiscordReadyEvents {
	
	private volatile boolean init;
	
	public DiscordReadyEvents() {
		init = false;
		if (DiscordSRV.isReady) {
			init = true;
			ready();
		}
	}
	
	@Subscribe(priority = ListenerPriority.HIGHEST)
	public void onDiscordReady(DiscordReadyEvent event) {
		if (!init) {
			init = true;
			ready();
		}
	}
	
	public void ready() {
		DiscordSRV discordsrv = InteractiveChatDiscordSrvAddon.discordsrv;
		
		JDA jda = discordsrv.getJda();
		jda.addEventListener(new OutboundToDiscordEvents.JDAEvents());
		
		for (String channelId : discordsrv.getChannels().values()) {
			GuildChannel channel = jda.getGuildChannelById(channelId);
			if (channel != null) {
				Guild guild = channel.getGuild();
				Member self = guild.getMember(jda.getSelfUser());
				for (Permission permission : InteractiveChatDiscordSrvAddon.requiredPermissions) {
					if (!self.hasPermission(channel, permission)) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] DiscordSRV Bot is missing the \"" + permission.getName() + "\" permission in the channel \"" + channel.getName() + "\" (Id: " + channel.getId() + ")");
					}
				}
			}
		}
		
		InboundToGameEvents.ready(discordsrv);
	}

}
