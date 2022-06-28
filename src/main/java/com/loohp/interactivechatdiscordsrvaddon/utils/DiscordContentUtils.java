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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechat.utils.BookUtils;
import com.loohp.interactivechat.utils.FilledMapUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.HoverClickDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayType;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ReactionsHandler;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ToolTipComponent;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.CustomItemTextureRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordItemStackUtils.DiscordToolTip;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.TitledInventoryWrapper;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.MessageAction;
import github.scarsz.discordsrv.util.WebhookUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.map.MapView;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class DiscordContentUtils {

    public static final Color OFFSET_WHITE = new Color(0xFFFFFE);

    public static final String LEFT_EMOJI = "\u2B05\uFE0F";
    public static final String RIGHT_EMOJI = "\u27A1\uFE0F";

    public static ValuePairs<List<DiscordMessageContent>, ReactionsHandler> createContents(List<DiscordDisplayData> dataList, OfflineICPlayer player) {
        List<DiscordMessageContent> contents = new ArrayList<>();
        List<String> reactions = new ArrayList<>();
        BiConsumer<GuildMessageReactionAddEvent, List<DiscordMessageContent>> reactionConsumer = (event, discordMessageContents) -> {};
        int i = -1;
        for (DiscordDisplayData data : dataList) {
            i++;
            if (data instanceof ImageDisplayData) {
                ImageDisplayData iData = (ImageDisplayData) data;
                ImageDisplayType type = iData.getType();
                String title = iData.getTitle();
                if (iData.getItemStack().isPresent()) {
                    Debug.debug("createContents creating item discord content");
                    ItemStack item = iData.getItemStack().get();
                    Color color = DiscordItemStackUtils.getDiscordColor(item);
                    if (color == null || color.equals(Color.WHITE)) {
                        color = OFFSET_WHITE;
                    }
                    try {
                        BufferedImage image = ImageUtils.resizeImage(ImageGeneration.getItemStackImage(item, data.getPlayer(), InteractiveChatDiscordSrvAddon.plugin.itemAltAir), 1.5);
                        byte[] imageData = ImageUtils.toArray(image);

                        DiscordMessageContent content = new DiscordMessageContent(title, null, color);
                        content.setTitle(DiscordItemStackUtils.getItemNameForDiscord(item, player));
                        content.setThumbnail("attachment://Item_" + i + ".png");

                        content.addAttachment("Item_" + i + ".png", imageData);
                        contents.add(content);

                        DiscordToolTip discordToolTip = DiscordItemStackUtils.getToolTip(item, player);
                        List<ToolTipComponent<?>> toolTipComponents = discordToolTip.getComponents();

                        boolean forceShow = false;
                        if (type.equals(ImageDisplayType.ITEM_CONTAINER)) {
                            TitledInventoryWrapper inv = iData.getInventory().get();
                            BufferedImage container = ImageGeneration.getInventoryImage(inv.getInventory(), inv.getTitle(), data.getPlayer());
                            toolTipComponents.add(ToolTipComponent.image(container));
                            forceShow = true;
                        } else if (iData.isFilledMap()) {
                            forceShow = true;
                        }

                        if (forceShow || !discordToolTip.isBaseItem() || InteractiveChatDiscordSrvAddon.plugin.itemUseTooltipImageOnBaseItem) {
                            BufferedImage tooltip = ImageGeneration.getToolTipImage(toolTipComponents);

                            if (iData.isFilledMap()) {
                                MapView mapView = FilledMapUtils.getMapView(item);
                                boolean isContextual = mapView == null || FilledMapUtils.isContextual(mapView);
                                ICPlayer icPlayer = iData.getPlayer().getPlayer();
                                boolean isPlayerLocal = icPlayer != null && icPlayer.isLocal();
                                if (!isContextual || isPlayerLocal) {
                                    BufferedImage map = ImageGeneration.getMapImage(item, isPlayerLocal ? icPlayer.getLocalPlayer() : null);
                                    tooltip = ImageUtils.resizeImage(tooltip, 5);
                                    tooltip = ImageUtils.appendImageBottom(tooltip, map, 10, 0);
                                }
                            }

                            byte[] tooltipData = ImageUtils.toArray(tooltip);
                            content.addAttachment("ToolTip_" + i + ".png", tooltipData);
                            content.addImageUrl("attachment://ToolTip_" + i + ".png");
                        }

                        if (iData.isBook()) {
                            List<Component> pages = BookUtils.getPages((BookMeta) item.getItemMeta());
                            List<Supplier<BufferedImage>> images = ImageGeneration.getBookInterfaceSuppliers(pages);
                            byte[][] cachedImages = new byte[images.size()][];
                            cachedImages[0] = ImageUtils.toArray(images.get(0).get());
                            if (!images.isEmpty()) {
                                reactions.add(LEFT_EMOJI);
                                reactions.add(RIGHT_EMOJI);
                                AtomicInteger currentPage = new AtomicInteger(0);
                                reactionConsumer = reactionConsumer.andThen(getBookHandler(images, cachedImages));
                                DiscordMessageContent bookContent = new DiscordMessageContent(null, null, null, "attachment://Page.png", color);
                                bookContent.addAttachment("Page.png", cachedImages[0]);
                                contents.add(bookContent);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (iData.getInventory().isPresent()) {
                    Debug.debug("createContents creating inventory discord content");
                    TitledInventoryWrapper inv = iData.getInventory().get();
                    try {
                        BufferedImage image;
                        if (iData.isPlayerInventory()) {
                            if (InteractiveChatDiscordSrvAddon.plugin.usePlayerInvView) {
                                image = ImageGeneration.getPlayerInventoryImage(inv.getInventory(), iData.getPlayer());
                            } else {
                                image = ImageGeneration.getInventoryImage(inv.getInventory(), inv.getTitle(), data.getPlayer());
                            }
                        } else {
                            image = ImageGeneration.getInventoryImage(inv.getInventory(), inv.getTitle(), data.getPlayer());
                        }
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
                        byte[] imageData = ImageUtils.toArray(image);
                        DiscordMessageContent content = new DiscordMessageContent(title, null, null, "attachment://Inventory_" + i + ".png", color);
                        content.addAttachment("Inventory_" + i + ".png", imageData);
                        if (type.equals(ImageDisplayType.INVENTORY) && InteractiveChatDiscordSrvAddon.plugin.invShowLevel) {
                            int level = iData.getPlayer().getExperienceLevel();
                            byte[] bottleData = ImageUtils.toArray(InteractiveChatDiscordSrvAddon.plugin.modelRenderer.render(32, 32, InteractiveChatDiscordSrvAddon.plugin.resourceManager, InteractiveChatDiscordSrvAddon.plugin.resourceManager.getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getItemPostResolveFunction("minecraft:item/experience_bottle", null, XMaterial.EXPERIENCE_BOTTLE.parseItem(), InteractiveChat.version.isOld(), null, null, null, null, InteractiveChatDiscordSrvAddon.plugin.resourceManager.getLanguageManager().getTranslateFunction().ofLanguage(InteractiveChatDiscordSrvAddon.plugin.language)).orElse(null), InteractiveChat.version.isOld(), "minecraft:item/experience_bottle", ModelDisplayPosition.GUI, false, null, null).getImage());
                            content.addAttachment("Level_" + i + ".png", bottleData);
                            content.setFooter(ComponentStringUtils.convertFormattedString(LanguageUtils.getTranslation(TranslationKeyUtils.getLevelTranslation(level), InteractiveChatDiscordSrvAddon.plugin.language), level));
                            content.setFooterImageUrl("attachment://Level_" + i + ".png");
                        }
                        contents.add(content);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (data instanceof HoverClickDisplayData) {
                Debug.debug("createContents creating hover event discord content");
                try {
                    HoverClickDisplayData hData = (HoverClickDisplayData) data;
                    String title = hData.getDisplayText();
                    Color color = hData.getColor();
                    DiscordMessageContent content = new DiscordMessageContent(title, null, color);
                    String body = "";
                    String preview = null;
                    if (hData.hasHover()) {
                        if (InteractiveChatDiscordSrvAddon.plugin.hoverUseTooltipImage) {
                            Component print = hData.getHoverText();
                            BufferedImage tooltip = ImageGeneration.getToolTipImage(print, true);
                            byte[] tooltipData = ImageUtils.toArray(tooltip);
                            content.addAttachment("ToolTip_" + i + ".png", tooltipData);
                            content.addImageUrl("attachment://ToolTip_" + i + ".png");
                            content.addDescription(null);
                        } else {
                            body += ComponentStringUtils.stripColorAndConvertMagic(InteractiveChatComponentSerializer.bungeecordApiLegacy().serialize(hData.getHoverText()));
                        }
                    }
                    if (hData.hasClick()) {
                        switch (hData.getClickAction()) {
                            case COPY_TO_CLIPBOARD:
                                if (body.length() > 0) {
                                    body += "\n\n";
                                }
                                body += LanguageUtils.getTranslation(TranslationKeyUtils.getCopyToClipboard(), InteractiveChatDiscordSrvAddon.plugin.language) + ": __" + hData.getClickValue() + "__";
                                break;
                            case OPEN_URL:
                                if (body.length() > 0) {
                                    body += "\n\n";
                                }
                                String url = hData.getClickValue();
                                body += LanguageUtils.getTranslation(TranslationKeyUtils.getOpenUrl(), InteractiveChatDiscordSrvAddon.plugin.language) + ": __" + url + "__";
                                if (URLRequestUtils.IMAGE_URL_PATTERN.matcher(url).matches() && URLRequestUtils.isAllowed(url)) {
                                    preview = url;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    if (!body.isEmpty()) {
                        content.addDescription(body);
                    }
                    if (InteractiveChatDiscordSrvAddon.plugin.hoverImage) {
                        BufferedImage image = InteractiveChatDiscordSrvAddon.plugin.resourceManager.getTextureManager().getTexture(ResourceRegistry.IC_MISC_TEXTURE_LOCATION + "hover_cursor").getTexture();
                        byte[] imageData = ImageUtils.toArray(image);
                        content.setAuthorIconUrl("attachment://Hover_" + i + ".png");
                        content.addAttachment("Hover_" + i + ".png", imageData);
                    }
                    if (preview != null) {
                        content.addImageUrl(preview);
                    }
                    contents.add(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return new ValuePairs<>(contents, new ReactionsHandler(reactions, InteractiveChat.itemDisplayTimeout, reactionConsumer));
    }

    private static BiConsumer<GuildMessageReactionAddEvent, List<DiscordMessageContent>> getBookHandler(List<Supplier<BufferedImage>> imageSuppliers, byte[][] cachedImages) {
        AtomicInteger currentPage = new AtomicInteger(0);
        return (event, discordMessageContents) -> {
            if (!event.getReactionEmote().isEmoji()) {
                return;
            }
            User self = DiscordSRV.getPlugin().getJda().getSelfUser();
            User user = event.getUser();
            if (self.equals(user)) {
                return;
            }
            String reaction = event.getReactionEmote().getEmoji();
            event.retrieveMessage().queue(message -> {
                synchronized (currentPage) {
                    if (reaction.equals(LEFT_EMOJI)) {
                        if (currentPage.get() > 0) {
                            int pageNumber = currentPage.decrementAndGet();
                            byte[] pageFile = cachedImages[pageNumber];
                            if (pageFile == null) {
                                try {
                                    cachedImages[pageNumber] = pageFile = ImageUtils.toArray(imageSuppliers.get(pageNumber).get());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (message.getAuthor().equals(self)) {
                                MessageAction action = message.editMessage(message.getContentRaw()).retainFilesById(Collections.emptyList());
                                List<MessageEmbed> embeds = new ArrayList<>();
                                int u = 0;
                                for (DiscordMessageContent discordMessageContent : discordMessageContents) {
                                    u += discordMessageContent.getAttachments().size();
                                    if (u <= 10) {
                                        embeds.addAll(discordMessageContent.toJDAMessageEmbeds());
                                        for (Entry<String, byte[]> attachment : discordMessageContent.getAttachments().entrySet()) {
                                            if (attachment.getKey().equals("Page.png")) {
                                                action = action.addFile(pageFile, "Page.png");
                                            } else {
                                                action = action.addFile(attachment.getValue(), attachment.getKey());
                                            }
                                        }
                                    }
                                }
                                action.setEmbeds(embeds).queue();
                            } else if (message.isWebhookMessage()) {
                                String webHookUrl = WebhookUtil.getWebhookUrlToUseForChannel(message.getTextChannel());
                                WebhookClient client = WebhookClient.withUrl(webHookUrl);
                                WebhookMessageBuilder builder = new WebhookMessageBuilder().setContent(message.getContentRaw());
                                int u = 0;
                                for (DiscordMessageContent discordMessageContent : discordMessageContents) {
                                    u += discordMessageContent.getAttachments().size();
                                    if (u <= 10) {
                                        builder.addEmbeds(discordMessageContent.toWebhookEmbeds());
                                        for (Entry<String, byte[]> attachment : discordMessageContent.getAttachments().entrySet()) {
                                            if (attachment.getKey().equals("Page.png")) {
                                                builder.addFile("Page.png", pageFile);
                                            } else {
                                                builder.addFile(attachment.getKey(), attachment.getValue());
                                            }
                                        }
                                    }
                                }
                                WebhookMessageUtils.retainAttachments(client, message.getId(), Collections.emptyList());
                                client.edit(message.getId(), builder.build());
                                client.close();
                            }
                        }
                        message.removeReaction(reaction, user).queue();
                    } else if (reaction.equals(RIGHT_EMOJI)) {
                        if (currentPage.get() < cachedImages.length - 1) {
                            int pageNumber = currentPage.incrementAndGet();
                            byte[] pageFile = cachedImages[pageNumber];
                            if (pageFile == null) {
                                try {
                                    cachedImages[pageNumber] = pageFile = ImageUtils.toArray(imageSuppliers.get(pageNumber).get());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (message.getAuthor().equals(self)) {
                                MessageAction action = message.editMessage(message.getContentRaw()).retainFilesById(Collections.emptyList());
                                List<MessageEmbed> embeds = new ArrayList<>();
                                int u = 0;
                                for (DiscordMessageContent discordMessageContent : discordMessageContents) {
                                    u += discordMessageContent.getAttachments().size();
                                    if (u <= 10) {
                                        embeds.addAll(discordMessageContent.toJDAMessageEmbeds());
                                        for (Entry<String, byte[]> attachment : discordMessageContent.getAttachments().entrySet()) {
                                            if (attachment.getKey().equals("Page.png")) {
                                                action = action.addFile(pageFile, "Page.png");
                                            } else {
                                                action = action.addFile(attachment.getValue(), attachment.getKey());
                                            }
                                        }
                                    }
                                }
                                action.setEmbeds(embeds).queue();
                            } else {
                                String webHookUrl = WebhookUtil.getWebhookUrlToUseForChannel(message.getTextChannel());
                                WebhookClient client = WebhookClient.withUrl(webHookUrl);
                                WebhookMessageBuilder builder = new WebhookMessageBuilder().setContent(message.getContentRaw());
                                int u = 0;
                                for (DiscordMessageContent discordMessageContent : discordMessageContents) {
                                    u += discordMessageContent.getAttachments().size();
                                    if (u <= 10) {
                                        builder.addEmbeds(discordMessageContent.toWebhookEmbeds());
                                        for (Entry<String, byte[]> attachment : discordMessageContent.getAttachments().entrySet()) {
                                            if (attachment.getKey().equals("Page.png")) {
                                                builder.addFile("Page.png", pageFile);
                                            } else {
                                                builder.addFile(attachment.getKey(), attachment.getValue());
                                            }
                                        }
                                    }
                                }
                                WebhookMessageUtils.retainAttachments(client, message.getId(), Collections.emptyList());
                                client.edit(message.getId(), builder.build());
                                client.close();
                            }
                        }
                        message.removeReaction(reaction, user).queue();
                    }
                }
            });
        };
    }

}
