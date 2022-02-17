/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import club.minnced.discord.webhook.send.WebhookEmbed.EmbedAuthor;
import club.minnced.discord.webhook.send.WebhookEmbed.EmbedFooter;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.MessageAction;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class DiscordMessageContent {

    private String authorName;
    private String authorIconUrl;
    private List<String> description;
    private List<String> imageUrl;
    private String thumbnail;
    private Color color;
    private String footer;
    private String footerImageUrl;
    private Map<String, byte[]> attachments;

    public DiscordMessageContent(String authorName, String authorIconUrl, List<String> description, List<String> imageUrl, Color color, Map<String, byte[]> attachments) {
        this.authorName = authorName;
        this.authorIconUrl = authorIconUrl;
        this.description = description;
        this.imageUrl = imageUrl;
        this.color = color;
        this.attachments = attachments;
        this.footer = null;
        this.footerImageUrl = null;
    }

    public DiscordMessageContent(String authorName, String authorIconUrl, String description, String imageUrl, Color color) {
        this(authorName, authorIconUrl, new ArrayList<>(Arrays.asList(description)), new ArrayList<>(Arrays.asList(imageUrl)), color, new HashMap<>());
    }

    public DiscordMessageContent(String authorName, String authorIconUrl, Color color) {
        this(authorName, authorIconUrl, new ArrayList<>(), new ArrayList<>(), color, new HashMap<>());
    }

    public DiscordMessageContent(Message message) {
        if (message.getEmbeds().isEmpty()) {
            throw new IllegalArgumentException("Not embeds found!");
        }
        MessageEmbed embed = message.getEmbeds().get(0);
        this.authorName = embed.getAuthor().getName();
        this.authorIconUrl = embed.getAuthor().getIconUrl();
        this.description = new ArrayList<>();
        if (embed.getDescription() != null) {
            description.add(embed.getDescription());
        }
        this.imageUrl = new ArrayList<>();
        if (embed.getImage() != null) {
            imageUrl.add(embed.getImage().getUrl());
        }
        this.color = embed.getColor();
        if (embed.getThumbnail() != null) {
            this.thumbnail = embed.getThumbnail().getUrl();
        }
        this.attachments = new HashMap<>();
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public List<String> getDescriptions() {
        return description;
    }

    public void setDescriptions(List<String> description) {
        this.description = description;
    }

    public void addDescription(String description) {
        this.description.add(description);
    }

    public void setDescription(int index, String description) {
        this.description.set(index, description);
    }

    public void clearDescriptions() {
        description.clear();
    }

    public List<String> getImageUrls() {
        return imageUrl;
    }

    public void setImageUrls(List<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void addImageUrl(String imageUrl) {
        this.imageUrl.add(imageUrl);
    }

    public void setImageUrl(int index, String imageUrl) {
        this.imageUrl.set(index, imageUrl);
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

    public String getFooter() {
        return footer;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public String getFooterImageUrl() {
        return footerImageUrl;
    }

    public void setFooterImageUrl(String footerImageUrl) {
        this.footerImageUrl = footerImageUrl;
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

    public void clearAttachments() {
        attachments.clear();
    }

    @SuppressWarnings("deprecation")
    public RestAction<List<Message>> toJDAMessageRestAction(TextChannel channel) {
        Map<MessageAction, Set<String>> actions = new LinkedHashMap<>();
        Set<String> rootAttachments = new HashSet<>();
        rootAttachments.add(authorIconUrl);
        EmbedBuilder embed = new EmbedBuilder().setAuthor(authorName, null, authorIconUrl).setColor(color).setThumbnail(thumbnail);
        if (description.size() > 0) {
            embed.setDescription(description.get(0));
        }
        if (imageUrl.size() > 0) {
            String url = imageUrl.get(0);
            embed.setImage(url);
            rootAttachments.add(url);
        }
        if (imageUrl.size() == 1 || description.size() == 1) {
            if (footer != null) {
                if (footerImageUrl == null) {
                    embed.setFooter(footer);
                } else {
                    embed.setFooter(footer, footerImageUrl);
                    rootAttachments.add(footerImageUrl);
                }
            }
        }
        actions.put(channel.sendMessage(embed.build()), rootAttachments);
        for (int i = 1; i < imageUrl.size() || i < description.size(); i++) {
            Set<String> usedAttachments = new HashSet<>();
            EmbedBuilder otherEmbed = new EmbedBuilder().setColor(color);
            if (i < imageUrl.size()) {
                String url = imageUrl.get(i);
                otherEmbed.setImage(url);
                usedAttachments.add(url);
            }
            if (i < description.size()) {
                otherEmbed.setDescription(description.get(i));
            }
            if (!(i + 1 < imageUrl.size() || i + 1 < description.size())) {
                if (footer != null) {
                    if (footerImageUrl == null) {
                        otherEmbed.setFooter(footer);
                    } else {
                        otherEmbed.setFooter(footer, footerImageUrl);
                    }
                }
            }
            if (!otherEmbed.isEmpty()) {
                actions.put(channel.sendMessage(otherEmbed.build()), usedAttachments);
            }
        }
        Set<String> embeddedAttachments = new HashSet<>();
        for (Entry<MessageAction, Set<String>> entry : actions.entrySet()) {
            MessageAction action = entry.getKey();
            Set<String> neededUrls = entry.getValue();
            for (Entry<String, byte[]> attachment : attachments.entrySet()) {
                String attachmentName = attachment.getKey();
                if (neededUrls.contains("attachment://" + attachmentName)) {
                    action.addFile(attachment.getValue(), attachmentName);
                    embeddedAttachments.add(attachmentName);
                }
            }
        }
        MessageAction lastAction = actions.keySet().stream().skip(actions.size() - 1).findFirst().get();
        for (Entry<String, byte[]> attachment : attachments.entrySet()) {
            String attachmentName = attachment.getKey();
            if (!embeddedAttachments.contains(attachmentName)) {
                lastAction.addFile(attachment.getValue(), attachmentName);
            }
        }
        return RestAction.allOf(actions.keySet());
    }

    public WebhookMessageBuilder toWebhookMessageBuilder() {
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder().setAuthor(new EmbedAuthor(authorName, authorIconUrl, null)).setColor(color.getRGB()).setThumbnailUrl(thumbnail);
        if (description.size() > 0) {
            embed.setDescription(description.get(0));
        }
        if (imageUrl.size() > 0) {
            embed.setImageUrl(imageUrl.get(0));
        }
        if (imageUrl.size() == 1 || description.size() == 1) {
            if (footer != null) {
                embed.setFooter(new EmbedFooter(footer, footerImageUrl));
            }
        }
        WebhookMessageBuilder webhookmessage = new WebhookMessageBuilder().addEmbeds(embed.build());
        for (int i = 1; i < imageUrl.size() || i < description.size(); i++) {
            WebhookEmbedBuilder otherEmbed = new WebhookEmbedBuilder().setColor(color.getRGB());
            if (i < imageUrl.size()) {
                otherEmbed.setImageUrl(imageUrl.get(i));
            }
            if (i < description.size()) {
                otherEmbed.setDescription(description.get(i));
            }
            if (!(i + 1 < imageUrl.size() || i + 1 < description.size())) {
                if (footer != null) {
                    otherEmbed.setFooter(new EmbedFooter(footer, footerImageUrl));
                }
            }
            if (!otherEmbed.isEmpty()) {
                webhookmessage.addEmbeds(otherEmbed.build());
            }
        }
        for (Entry<String, byte[]> entry : attachments.entrySet()) {
            webhookmessage.addFile(entry.getKey(), entry.getValue());
        }
        return webhookmessage;
    }

}
