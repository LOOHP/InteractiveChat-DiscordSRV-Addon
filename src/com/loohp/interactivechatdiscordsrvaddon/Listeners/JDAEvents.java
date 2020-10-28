package com.loohp.interactivechatdiscordsrvaddon.Listeners;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.Listeners.DiscordSRVEvents.ImageDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.Listeners.DiscordSRVEvents.ImageDisplayType;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ItemStackUtils;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ItemStackUtils.DiscordDescription;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedAuthor;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
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
		
		for (int key : DiscordSRVEvents.data.keySet()) {
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
			ImageDisplayData iData = DiscordSRVEvents.data.remove(key);
			ImageDisplayType type = iData.getType();
			String title = iData.getTitle();
			player = iData.getPlayer();
			if (iData.getItemStack().isPresent()) {
				ItemStack item = iData.getItemStack().get();
				Color color = ItemStackUtils.getDiscordColor(item);
				if (color.equals(Color.white)) {
					color = new Color(0xFFFFFE);
				}
				try {
					DiscordDescription description = ItemStackUtils.getDiscordDescription(item);
					BufferedImage image = ImageGeneration.getItemStackImage(item);					
					ByteArrayOutputStream os = new ByteArrayOutputStream();
					ImageIO.write(image, "png", os);
					messagesToSend.add(new WebhookMessageBuilder().addEmbeds(new WebhookEmbedBuilder().setAuthor(new EmbedAuthor(description.getName(), "attachment://Item.png", null)).setColor(color.getRGB()).setDescription(description.getDescription().orElse(null)).build()).addFile("Item.png", os.toByteArray()));
				} catch (Exception e) {
					e.printStackTrace();
				}	
			} else if (iData.getInventory().isPresent()) {
				Inventory inv = iData.getInventory().get();
				try {
					BufferedImage image;
					if (iData.isPlayerInventory()) {
						if (InteractiveChatDiscordSrvAddon.plugin.usePlayerInvView) {
							image = ImageGeneration.getPlayerInventoryImage(inv, iData.getPlayer());
						} else {
							image = ImageGeneration.getInventoryImage(inv);
						}
					} else {
						image = ImageGeneration.getInventoryImage(inv);
					}
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
					messagesToSend.add(new WebhookMessageBuilder().addEmbeds(new WebhookEmbedBuilder().setAuthor(new EmbedAuthor(title, null, null)).setColor(color.getRGB()).setImageUrl("attachment://Inventory.png").build()).addFile("Inventory.png", os.toByteArray()));					
				} catch (Exception e) {
					e.printStackTrace();
				}
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
