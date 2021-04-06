package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import club.minnced.discord.webhook.send.WebhookEmbed.EmbedAuthor;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.MessageAction;

public class DiscordMessageContent {
	
	private String authorName;
	private String authorIconUrl;
	private String description;
	private String imageUrl;
	private Color color;
	private Map<String, byte[]> attachments;
	
	public DiscordMessageContent(String authorName, String authorIconUrl, String description, String imageUrl, Color color, Map<String, byte[]> attachments) {
		this.authorName = authorName;
		this.authorIconUrl = authorIconUrl;
		this.description = description;
		this.imageUrl = imageUrl;
		this.color = color;
		this.attachments = attachments;
	}
	
	public DiscordMessageContent(String authorName, String authorIconUrl, String description, String imageUrl, Color color) {
		this(authorName, authorIconUrl, description, imageUrl, color, new HashMap<>());
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorIconUrl() {
		return authorIconUrl;
	}

	public void setAuthorIconUrl(String authorIconUrl) {
		this.authorIconUrl = authorIconUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Map<String, byte[]> getAttachments() {
		return attachments;
	}

	public void setAttachments(Map<String, byte[]> attachments) {
		this.attachments = attachments;
	}
	
	public void addAttachment(String name, byte[] data) {
		attachments.put(name, data);
	}
	
	public MessageAction toJDAMessageAction(TextChannel channel) {
		EmbedBuilder embed = new EmbedBuilder().setAuthor(authorName, null, authorIconUrl).setColor(color).setDescription(description).setImage(imageUrl);
		MessageAction action = channel.sendMessage(embed.build());
		for (Entry<String, byte[]> entry : attachments.entrySet()) {
			action.addFile(entry.getValue(), entry.getKey());
		}
		return action;
	}
	
	public WebhookMessageBuilder toWebhookMessageBuilder() {
		WebhookEmbedBuilder embed = new WebhookEmbedBuilder().setAuthor(new EmbedAuthor(authorName, authorIconUrl, null)).setColor(color.getRGB()).setDescription(description).setImageUrl(imageUrl);
		WebhookMessageBuilder webhookmessage = new WebhookMessageBuilder().addEmbeds(embed.build());
		for (Entry<String, byte[]> entry : attachments.entrySet()) {
			webhookmessage.addFile(entry.getKey(), entry.getValue());
		}
		return webhookmessage;
	}

}
