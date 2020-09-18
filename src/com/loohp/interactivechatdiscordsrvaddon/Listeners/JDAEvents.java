package com.loohp.interactivechatdiscordsrvaddon.Listeners;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.loohp.interactivechatdiscordsrvaddon.Image.InventoryGeneration;
import com.loohp.interactivechatdiscordsrvaddon.Listeners.DiscordSRVEvents.InventoryImageData;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.ChannelType;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.PlaceholderUtil;
import github.scarsz.discordsrv.util.WebhookUtil;

public class JDAEvents extends ListenerAdapter {
	
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getAuthor().equals(event.getJDA().getSelfUser())) {
			return;
		}
		
		if (!event.getChannelType().equals(ChannelType.TEXT)) {
			return;
		}
		
		if (!event.isWebhookMessage()) {
			return;
		}
		
		Message message = event.getMessage();
		TextChannel channel = event.getTextChannel();
		String text = message.getContentRaw();
		
		if (!text.contains("<ICD=")) {
			return;
		}
		
		Set<Integer> matches = new LinkedHashSet<>();
		
		Iterator<Integer> itr = DiscordSRVEvents.data.keySet().iterator();
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
		
		message.delete().queue();
		Player player = null;
		List<WebhookMessageBuilder> messagesToSend = new ArrayList<>();
		
		for (int key : matches) {
			InventoryImageData iData = DiscordSRVEvents.data.get(key);
			String title = iData.getTitle();
			Inventory inv = iData.getInventory();
			player = iData.getPlayer();
			if (inv == null) {
				continue;
			}
			try {
				BufferedImage image = InventoryGeneration.getImage(inv);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(image, "png", os);
				InputStream is = new ByteArrayInputStream(os.toByteArray());
				
				messagesToSend.add(new WebhookMessageBuilder().setContent("**" + title + "**").addFile("Inventory.png", is));
				
				DiscordSRVEvents.data.remove(key);
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
		
		String avatarUrl = DiscordSRV.config().getString("Experiment_EmbedAvatarUrl");
        avatarUrl = PlaceholderUtil.replacePlaceholders(avatarUrl, player);

        String username = DiscordSRV.config().getString("Experiment_WebhookChatMessageUsernameFormat")
                .replace("%displayname%", DiscordUtil.strip(player.getDisplayName()))
                .replace("%username%", player.getName());
        username = PlaceholderUtil.replacePlaceholders(username, player);
        username = DiscordUtil.strip(username);

        String userId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId());
        if (userId != null) {
            Member member = DiscordUtil.getMemberById(userId);
            if (member != null) {
                if (DiscordSRV.config().getBoolean("Experiment_WebhookChatMessageAvatarFromDiscord"))
                    avatarUrl = member.getUser().getEffectiveAvatarUrl();
                if (DiscordSRV.config().getBoolean("Experiment_WebhookChatMessageUsernameFromDiscord"))
                    username = member.getEffectiveName();
            }
        }

        if (StringUtils.isBlank(avatarUrl)) avatarUrl = "https://minotar.net/helm/{uuid-nodashes}/{size}";
        avatarUrl = avatarUrl
                .replace("{timestamp}", String.valueOf(System.currentTimeMillis() / 1000))
                .replace("{username}", player.getName())
                .replace("{uuid}", player.getUniqueId().toString())
                .replace("{uuid-nodashes}", player.getUniqueId().toString().replace("-", ""))
                .replace("{size}", "128");
		
		String webHookUrl = WebhookUtil.getWebhookUrlToUseForChannel(channel, username);
		WebhookClient client = WebhookClient.withUrl(webHookUrl);
		
		if (client == null || player == null) {
			return;
		}
		
		client.send(new WebhookMessageBuilder().setUsername(username).setAvatarUrl(avatarUrl).setContent(text).build());
		for (WebhookMessageBuilder builder : messagesToSend) {
			client.send(builder.setUsername(username).setAvatarUrl(avatarUrl).build());
		}
		
		client.close();
		
	}

}
