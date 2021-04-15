package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
	private List<String> imageUrl;
	private Color color;
	private Map<String, byte[]> attachments;
	
	public DiscordMessageContent(String authorName, String authorIconUrl, String description, List<String> imageUrl, Color color, Map<String, byte[]> attachments) {
		this.authorName = authorName;
		this.authorIconUrl = authorIconUrl;
		this.description = description;
		this.imageUrl = imageUrl;
		this.color = color;
		this.attachments = attachments;
	}
	
	public DiscordMessageContent(String authorName, String authorIconUrl, String description, String imageUrl, Color color) {
		this(authorName, authorIconUrl, description, new ArrayList<>(Arrays.asList(imageUrl)), color, new HashMap<>());
	}
	
	public DiscordMessageContent(String authorName, String authorIconUrl, String description, Color color) {
		this(authorName, authorIconUrl, description, new ArrayList<>(), color, new HashMap<>());
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

	public List<String> getImageUrls() {
		return imageUrl;
	}

	public void addImageUrl(String imageUrl) {
		this.imageUrl.add(imageUrl);
	}
	
	public void setImageUrl(int index, String imageUrl) {
		this.imageUrl.set(index, imageUrl);
	}
	
	public void setImageUrl(List<String> imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public void clearImageUrls() {
		imageUrl.clear();
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

	public void setAttachment(Map<String, byte[]> attachments) {
		this.attachments = attachments;
	}
	
	public void addAttachment(String name, byte[] data) {
		attachments.put(name, data);
	}
	
	public void clearAttachments() {
		attachments.clear();
	}
	
	public MessageAction toJDAMessageAction(TextChannel channel) {
		EmbedBuilder embed = new EmbedBuilder().setAuthor(authorName, null, authorIconUrl).setColor(color).setDescription(description);
		if (imageUrl.size() > 0) {
			embed.setImage(imageUrl.get(0));
		}
		MessageAction action = channel.sendMessage(embed.build());
		for (int i = 1; i < imageUrl.size(); i++) {
			action.embed(new EmbedBuilder().setColor(color).setImage(imageUrl.get(i)).build());
		}
		for (Entry<String, byte[]> entry : attachments.entrySet()) {
			action.addFile(entry.getValue(), entry.getKey());
		}
		return action;
	}
	
	public WebhookMessageBuilder toWebhookMessageBuilder() {
		WebhookEmbedBuilder embed = new WebhookEmbedBuilder().setAuthor(new EmbedAuthor(authorName, authorIconUrl, null)).setColor(color.getRGB()).setDescription(description);
		if (imageUrl.size() > 0) {
			embed.setImageUrl(imageUrl.get(0));
		}
		WebhookMessageBuilder webhookmessage = new WebhookMessageBuilder().addEmbeds(embed.build());
		for (int i = 1; i < imageUrl.size(); i++) {
			webhookmessage.addEmbeds(new WebhookEmbedBuilder().setColor(color.getRGB()).setImageUrl(imageUrl.get(i)).build());
		}
		for (Entry<String, byte[]> entry : attachments.entrySet()) {
			webhookmessage.addFile(entry.getKey(), entry.getValue());
		}
		return webhookmessage;
	}

}
