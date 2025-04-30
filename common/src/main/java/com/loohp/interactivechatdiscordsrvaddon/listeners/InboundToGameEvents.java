/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
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

package com.loohp.interactivechatdiscordsrvaddon.listeners;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.api.events.PrePacketComponentProcessEvent;
import com.loohp.interactivechat.libs.com.loohp.platformscheduler.Scheduler;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.ClickEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.objectholders.ICPlaceholder;
import com.loohp.interactivechat.utils.ComponentReplacing;
import com.loohp.interactivechat.utils.CustomStringUtils;
import com.loohp.interactivechat.utils.HTTPRequestUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.api.events.DiscordAttachmentConversionEvent;
import com.loohp.interactivechatdiscordsrvaddon.debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.graphics.GifReader;
import com.loohp.interactivechatdiscordsrvaddon.modules.DiscordToGameMention;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.PreviewableImageContainer;
import com.loohp.interactivechatdiscordsrvaddon.utils.ThrowingSupplier;
import com.loohp.interactivechatdiscordsrvaddon.utils.URLRequestUtils;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.GraphicsToPacketMapWrapper;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePostProcessEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message.Attachment;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageSticker;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class InboundToGameEvents implements Listener {

    public static final Pattern TENOR_HTML_PATTERN = Pattern.compile("<link class=\\\"dynamic\\\" rel=\\\"image_src\\\" href=\\\"https://media1\\.tenor\\.com/m/(.*?)/.*?\\\">");

    public static final Map<UUID, DiscordAttachmentData> DATA = new ConcurrentHashMap<>();
    public static final Map<Player, GraphicsToPacketMapWrapper> MAP_VIEWERS = new ConcurrentHashMap<>();

    @Subscribe(priority = ListenerPriority.LOWEST)
    public void onReceiveMessageFromDiscordPre(DiscordGuildMessagePreProcessEvent event) {
        Debug.debug("Triggering onReceiveMessageFromDiscordPre");
        DiscordSRV srv = InteractiveChatDiscordSrvAddon.discordsrv;
        Map<Pattern, String> discordRegexes = srv.getDiscordRegexes();
        if (discordRegexes != null) {
            discordRegexes.keySet().removeIf(pattern -> pattern.pattern().equals("@+(everyone|here)"));
        }
    }

    @Subscribe(priority = ListenerPriority.LOW)
    public void onDiscordToGame(DiscordGuildMessagePostProcessEvent event) {
        Debug.debug("Triggering onDiscordToGame");
        InteractiveChatDiscordSrvAddon.plugin.messagesCounter.incrementAndGet();
        github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component component = event.getMinecraftMessage();
        if (InteractiveChatDiscordSrvAddon.plugin.escapePlaceholdersFromDiscord) {
            Debug.debug("onDiscordToGame escaping placeholders");
            for (ICPlaceholder placeholder : InteractiveChat.placeholderList.values()) {
                component = component.replaceText(github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig.builder().match(placeholder.getKeyword()).replacement((result, builder) -> builder.content("\\" + result.group())).build());
            }
            event.setMinecraftMessage(component);
        }
    }

    @Subscribe(priority = ListenerPriority.LOWEST)
    public void onReceiveMessageFromDiscordPostLowest(DiscordGuildMessagePostProcessEvent event) {
        if (InteractiveChatDiscordSrvAddon.plugin.discordToGamePriority.equals(ListenerPriority.LOWEST)) {
            handleReceiveMessageFromDiscordPost(event);
        }
    }

    @Subscribe(priority = ListenerPriority.LOW)
    public void onReceiveMessageFromDiscordPostLow(DiscordGuildMessagePostProcessEvent event) {
        if (InteractiveChatDiscordSrvAddon.plugin.discordToGamePriority.equals(ListenerPriority.LOW)) {
            handleReceiveMessageFromDiscordPost(event);
        }
    }

    @Subscribe(priority = ListenerPriority.NORMAL)
    public void onReceiveMessageFromDiscordPostNormal(DiscordGuildMessagePostProcessEvent event) {
        if (InteractiveChatDiscordSrvAddon.plugin.discordToGamePriority.equals(ListenerPriority.NORMAL)) {
            handleReceiveMessageFromDiscordPost(event);
        }
    }

    @Subscribe(priority = ListenerPriority.HIGH)
    public void onReceiveMessageFromDiscordPostHigh(DiscordGuildMessagePostProcessEvent event) {
        if (InteractiveChatDiscordSrvAddon.plugin.discordToGamePriority.equals(ListenerPriority.HIGH)) {
            handleReceiveMessageFromDiscordPost(event);
        }
    }

    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onReceiveMessageFromDiscordPostHighest(DiscordGuildMessagePostProcessEvent event) {
        if (InteractiveChatDiscordSrvAddon.plugin.discordToGamePriority.equals(ListenerPriority.HIGHEST)) {
            handleReceiveMessageFromDiscordPost(event);
        }
    }

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void onReceiveMessageFromDiscordPostMonitor(DiscordGuildMessagePostProcessEvent event) {
        if (InteractiveChatDiscordSrvAddon.plugin.discordToGamePriority.equals(ListenerPriority.MONITOR)) {
            handleReceiveMessageFromDiscordPost(event);
        }
    }

    public void handleReceiveMessageFromDiscordPost(DiscordGuildMessagePostProcessEvent event) {
        try {
            ExecutorService service = Executors.newSingleThreadExecutor();
            Future<?> future = service.submit(() -> {
                Debug.debug("Triggering onReceiveMessageFromDiscordPost");
                Message message = event.getMessage();

                github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component component = event.getMinecraftMessage();

                DiscordSRV srv = InteractiveChatDiscordSrvAddon.discordsrv;
                User author = message.getAuthor();

                if (InteractiveChatDiscordSrvAddon.plugin.translateMentions) {
                    Debug.debug("onReceiveMessageFromDiscordPost translating mentions");

                    Set<UUID> mentionTitleSent = new HashSet<>();
                    Map<Member, UUID> channelMembers = new HashMap<>();

                    TextChannel channel = event.getChannel();
                    Guild guild = channel.getGuild();
                    Member authorAsMember = guild.getMember(author);
                    String senderDiscordName = authorAsMember == null ? author.getName() : authorAsMember.getEffectiveName();
                    UUID senderUUID = srv.getAccountLinkManager().getUuid(author.getId());

                    for (Entry<UUID, String> entry : srv.getAccountLinkManager().getManyDiscordIds(Bukkit.getOnlinePlayers().stream().map(each -> each.getUniqueId()).collect(Collectors.toSet())).entrySet()) {
                        Member member = guild.getMemberById(entry.getValue());
                        if (member != null && member.hasAccess(channel)) {
                            channelMembers.put(member, entry.getKey());
                        }
                    }

                    if (message.mentionsEveryone()) {
                        //github.scarsz.discordsrv.dependencies.kyori.adventure.text.event.HoverEvent<Component> hover = Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHover.replace("{DiscordUser}", senderDiscordName).replace("{TextChannel}", "#" + channel.getName()).replace("{Guild}", guild.getName())).asHoverEvent();
                        component = component.replaceText(github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig.builder().matchLiteral("@here").replacement(github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHighlight.replace("{DiscordMention}", "@here"))).build()).replaceText(github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig.builder().matchLiteral("@everyone").replacement(github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHighlight.replace("{DiscordMention}", "@everyone"))).build());
                        for (UUID uuid : channelMembers.values()) {
                            mentionTitleSent.add(uuid);
                            Player player = Bukkit.getPlayer(uuid);
                            if (player != null) {
                                DiscordToGameMention.playTitleScreen(senderDiscordName, channel.getName(), guild.getName(), player);
                            }
                        }
                    }

                    List<Role> mentionedRoles = message.getMentionedRoles();
                    for (Role role : mentionedRoles) {
                        //github.scarsz.discordsrv.dependencies.kyori.adventure.text.event.HoverEvent<Component> hover = Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHover.replace("{DiscordUser}", senderDiscordName).replace("{TextChannel}", "#" + channel.getName()).replace("{Guild}", guild.getName())).asHoverEvent();
                        component = component.replaceText(github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig.builder().matchLiteral("@" + role.getName()).replacement(github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHighlight.replace("{DiscordMention}", "@" + role.getName()))).build());
                        for (Entry<Member, UUID> entry : channelMembers.entrySet()) {
                            UUID uuid = entry.getValue();
                            if (!mentionTitleSent.contains(uuid) && entry.getKey().getRoles().contains(role)) {
                                mentionTitleSent.add(uuid);
                                Player player = Bukkit.getPlayer(uuid);
                                if (player != null) {
                                    DiscordToGameMention.playTitleScreen(senderDiscordName, channel.getName(), guild.getName(), player);
                                }
                            }
                        }
                    }

                    List<User> mentionedUsers = message.getMentionedUsers();
                    if (!mentionedUsers.isEmpty()) {
                        for (User user : mentionedUsers) {
                            //github.scarsz.discordsrv.dependencies.kyori.adventure.text.event.HoverEvent<Component> hover = Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHover.replace("{DiscordUser}", senderDiscordName).replace("{TextChannel}", "#" + channel.getName()).replace("{Guild}", guild.getName())).asHoverEvent();
                            component = component.replaceText(github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig.builder().matchLiteral("@" + user.getName()).replacement(github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component.text(InteractiveChatDiscordSrvAddon.plugin.mentionHighlight.replace("{DiscordMention}", "@" + user.getName()))).build());
                            Member member = guild.getMember(user);
                            if (member != null) {
                                UUID uuid = channelMembers.get(member);
                                if (uuid != null && !mentionTitleSent.contains(uuid) && (senderUUID == null || !senderUUID.equals(uuid))) {
                                    mentionTitleSent.add(uuid);
                                    Player player = Bukkit.getPlayer(uuid);
                                    if (player != null) {
                                        DiscordToGameMention.playTitleScreen(senderDiscordName, channel.getName(), guild.getName(), player);
                                    }
                                }
                            }
                        }
                    }

                    event.setMinecraftMessage(component);
                }

                String processedMessage = MessageUtil.toLegacy(component);

                if (InteractiveChatDiscordSrvAddon.plugin.convertDiscordAttachments) {
                    Debug.debug("onReceiveMessageFromDiscordPost converting discord attachments");
                    Set<String> processedUrl = new HashSet<>();
                    List<PreviewableImageContainer> previewableImageContainers = new ArrayList<>(message.getAttachments().size() + message.getStickers().size());
                    for (Attachment attachment : message.getAttachments()) {
                        InteractiveChatDiscordSrvAddon.plugin.attachmentCounter.incrementAndGet();
                        String url = attachment.getUrl();
                        if (processedMessage.contains(url)) {
                            processedUrl.add(url);
                            if ((attachment.isImage() || attachment.isVideo()) && attachment.getSize() <= InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsPreviewLimit) {
                                previewableImageContainers.add(PreviewableImageContainer.fromAttachment(attachment));
                            } else {
                                DiscordAttachmentData data = new DiscordAttachmentData(attachment.getFileName(), url);
                                DiscordAttachmentConversionEvent dace = new DiscordAttachmentConversionEvent(url, data);
                                Bukkit.getPluginManager().callEvent(dace);
                                DATA.put(data.getUniqueId(), data);
                                Scheduler.runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> DATA.remove(data.getUniqueId()), InteractiveChatDiscordSrvAddon.plugin.discordAttachmentTimeout);
                            }
                        }
                    }
                    for (MessageSticker sticker : message.getStickers()) {
                        previewableImageContainers.add(PreviewableImageContainer.fromSticker(sticker));
                    }
                    for (PreviewableImageContainer imageContainer : previewableImageContainers) {
                        InteractiveChatDiscordSrvAddon.plugin.attachmentImageCounter.incrementAndGet();
                        String url = imageContainer.getUrl();
                        List<ThrowingSupplier<InputStream>> methods = new ArrayList<>();
                        for (String url0 : imageContainer.getAllUrls()) {
                            if (URLRequestUtils.isAllowed(url0)) {
                                methods.add(() -> URLRequestUtils.getInputStream0(url0));
                            }
                        }

                        try (InputStream stream = URLRequestUtils.retrieveUntilSuccessful(methods)) {
                            String type = imageContainer.getContentType();
                            GraphicsToPacketMapWrapper map;
                            boolean isVideo = false;
                            if (type.endsWith("gif.png") || type.endsWith("apng")) {
                                throw new UnsupportedOperationException("Animated PNG not yet supported, this error can be ignored");
                            } else if (type.endsWith("gif")) {
                                map = new GraphicsToPacketMapWrapper(InteractiveChatDiscordSrvAddon.plugin.playbackBarEnabled, InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsMapBackgroundColor);
                                GifReader.readGif(stream, InteractiveChatDiscordSrvAddon.plugin.mediaReadingService, (frames, e) -> {
                                    if (e != null) {
                                        e.printStackTrace();
                                        map.completeFuture(null);
                                    } else {
                                        map.completeFuture(frames);
                                    }
                                });
                            } else {
                                BufferedImage image = ImageIO.read(stream);
                                map = new GraphicsToPacketMapWrapper(image, InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsMapBackgroundColor);
                            }
                            DiscordAttachmentData data = new DiscordAttachmentData(imageContainer.getName(), url, map, isVideo);
                            DiscordAttachmentConversionEvent dace = new DiscordAttachmentConversionEvent(url, data);
                            Bukkit.getPluginManager().callEvent(dace);
                            DATA.put(data.getUniqueId(), data);
                            Scheduler.runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> DATA.remove(data.getUniqueId()), InteractiveChatDiscordSrvAddon.plugin.discordAttachmentTimeout);
                        } catch (Exception e) {
                            e.printStackTrace();
                            DiscordAttachmentData data = new DiscordAttachmentData(imageContainer.getName(), url);
                            DiscordAttachmentConversionEvent dace = new DiscordAttachmentConversionEvent(url, data);
                            Bukkit.getPluginManager().callEvent(dace);
                            DATA.put(data.getUniqueId(), data);
                            Scheduler.runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> DATA.remove(data.getUniqueId()), InteractiveChatDiscordSrvAddon.plugin.discordAttachmentTimeout);
                        }
                    }

                    Matcher matcher = URLRequestUtils.URL_PATTERN.matcher(message.getContentRaw());
                    while (matcher.find()) {
                        String url = matcher.group();
                        String imageUrl = url;
                        if (!processedUrl.contains(url) && URLRequestUtils.isAllowed(url)) {
                            if (url.startsWith("https://tenor.com/")) {
                                try {
                                    String html = HTTPRequestUtils.getTextResponse(url);
                                    Matcher matcher2 = TENOR_HTML_PATTERN.matcher(html);
                                    if (matcher2.find()) {
                                        imageUrl = "https://c.tenor.com/" + matcher2.group(1) + "/tenor.gif";
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            long size = HTTPRequestUtils.getContentSize(imageUrl);
                            if (size >= 0 && size <= InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsPreviewLimit) {
                                InteractiveChatDiscordSrvAddon.plugin.attachmentImageCounter.incrementAndGet();
                                try (InputStream stream = URLRequestUtils.getInputStream(imageUrl)) {
                                    String type = HTTPRequestUtils.getContentType(imageUrl);

                                    if (type == null || !type.startsWith("image/")) {
                                        continue;
                                    }
                                    GraphicsToPacketMapWrapper map;
                                    boolean isVideo = false;
                                    if (type.endsWith("gif.png") || type.endsWith("apng")) {
                                        throw new UnsupportedOperationException("Animated PNG not yet supported, this error can be ignored");
                                    } else if (type.endsWith("gif")) {
                                        map = new GraphicsToPacketMapWrapper(InteractiveChatDiscordSrvAddon.plugin.playbackBarEnabled, InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsMapBackgroundColor);
                                        GifReader.readGif(stream, InteractiveChatDiscordSrvAddon.plugin.mediaReadingService, (frames, e) -> {
                                            if (e != null) {
                                                e.printStackTrace();
                                                map.completeFuture(null);
                                            } else {
                                                map.completeFuture(frames);
                                            }
                                        });
                                    } else {
                                        BufferedImage image = ImageIO.read(stream);
                                        map = new GraphicsToPacketMapWrapper(image, InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsMapBackgroundColor);
                                    }
                                    String name = matcher.group(1);
                                    DiscordAttachmentData data = new DiscordAttachmentData(name, url, map, isVideo);
                                    DiscordAttachmentConversionEvent dace = new DiscordAttachmentConversionEvent(url, data);
                                    Bukkit.getPluginManager().callEvent(dace);
                                    DATA.put(data.getUniqueId(), data);
                                    Scheduler.runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> DATA.remove(data.getUniqueId()), InteractiveChatDiscordSrvAddon.plugin.discordAttachmentTimeout);
                                } catch (FileNotFoundException ignore) {
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            });
            future.get(5000, TimeUnit.MILLISECONDS);
            service.shutdownNow();
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onChatPacket(PrePacketComponentProcessEvent event) {
        Debug.debug("Triggering onChatPacket");
        if (InteractiveChatDiscordSrvAddon.plugin.convertDiscordAttachments) {
            Debug.debug("onChatPacket converting discord attachments");
            for (Entry<UUID, DiscordAttachmentData> entry : DATA.entrySet()) {
                DiscordAttachmentData data = entry.getValue();
                String url = data.getUrl();
                Component component = event.getComponent();

                String replacement = InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingText.replace("{FileName}", data.getFileName());
                Component textComponent = LegacyComponentSerializer.legacySection().deserialize(replacement);
                if (InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingHoverEnabled) {
                    String hover = InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingHoverText.replace("{FileName}", data.getFileName());
                    textComponent = textComponent.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(hover)));
                }
                if (InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsImagesUseMaps && data.isImage()) {
                    textComponent = textComponent.clickEvent(ClickEvent.runCommand("/interactivechatdiscordsrv imagemap " + data.getUniqueId().toString()));
                    Component imageAppend = LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingImageAppend.replace("{FileName}", data.getFileName()));
                    imageAppend = imageAppend.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.discordAttachmentsFormattingImageAppendHover.replace("{FileName}", data.getFileName()))));
                    imageAppend = imageAppend.clickEvent(ClickEvent.openUrl(url));
                    textComponent = textComponent.append(imageAppend);
                } else {
                    textComponent = textComponent.clickEvent(ClickEvent.openUrl(url));
                }

                component = ComponentReplacing.replace(component, "\\\\?" + CustomStringUtils.escapeMetaCharacters(url), textComponent);

                event.setComponent(component);
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventory(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        boolean removed = MAP_VIEWERS.remove(player) != null;

        if (removed) {
            player.getInventory().setItemInHand(player.getInventory().getItemInHand());
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            Scheduler.runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> {
                boolean removed = MAP_VIEWERS.remove(player) != null;

                if (removed) {
                    player.getInventory().setItemInHand(player.getInventory().getItemInHand());
                }
            }, 1, player);
        } else {
            boolean removed = MAP_VIEWERS.remove(player) != null;

            if (removed) {
                player.getInventory().setItemInHand(player.getInventory().getItemInHand());
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInventory(InventoryCreativeEvent event) {
        Player player = (Player) event.getWhoClicked();
        boolean removed = MAP_VIEWERS.remove(player) != null;

        int slot = event.getSlot();

        if (removed) {
            if (player.getInventory().equals(event.getClickedInventory()) && slot >= 9) {
                ItemStack item = player.getInventory().getItem(slot);
                Scheduler.runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> player.getInventory().setItem(slot, item), 1, player);
            } else {
                event.setCursor(null);
            }
        }

        if (removed) {
            player.getInventory().setItemInHand(player.getInventory().getItemInHand());
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event) {
        if (event.getNewSlot() == event.getPreviousSlot()) {
            return;
        }

        Player player = event.getPlayer();
        boolean removed = MAP_VIEWERS.remove(player) != null;

        if (removed) {
            player.getInventory().setItemInHand(player.getInventory().getItemInHand());
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL)) {
            return;
        }
        Player player = event.getPlayer();

        if (player.getGameMode().equals(GameMode.CREATIVE)) {
            Scheduler.runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> {
                boolean removed = MAP_VIEWERS.remove(player) != null;

                if (removed) {
                    player.getInventory().setItemInHand(player.getInventory().getItemInHand());
                }
            }, 1, player);
        } else {
            boolean removed = MAP_VIEWERS.remove(player) != null;

            if (removed) {
                player.getInventory().setItemInHand(player.getInventory().getItemInHand());
            }
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        Entity entity = event.getDamager();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            boolean removed = MAP_VIEWERS.remove(player) != null;

            if (removed) {
                player.getInventory().setItemInHand(player.getInventory().getItemInHand());
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        MAP_VIEWERS.remove(event.getPlayer());
    }

    public static class DiscordAttachmentData {

        private final String fileName;
        private final String url;
        private final GraphicsToPacketMapWrapper imageMap;
        private final UUID uuid;
        private final boolean isVideo;

        public DiscordAttachmentData(String fileName, String url, GraphicsToPacketMapWrapper imageMap, boolean isVideo) {
            this.fileName = fileName;
            this.url = url;
            this.imageMap = imageMap;
            this.uuid = UUID.randomUUID();
            this.isVideo = isVideo;
        }

        public DiscordAttachmentData(String fileName, String url) {
            this(fileName, url, null, false);
        }

        public String getFileName() {
            return fileName;
        }

        public String getUrl() {
            return url;
        }

        public boolean isImage() {
            return imageMap != null && !isVideo;
        }

        public boolean isVideo() {
            return imageMap != null && isVideo;
        }

        public GraphicsToPacketMapWrapper getImageMap() {
            return imageMap;
        }

        public UUID getUniqueId() {
            return uuid;
        }

        public int hashCode() {
            return 17 * uuid.hashCode();
        }

        public boolean equals(Object object) {
            if (object instanceof DiscordAttachmentData) {
                return ((DiscordAttachmentData) object).uuid.equals(this.uuid);
            }
            return false;
        }

    }

}
