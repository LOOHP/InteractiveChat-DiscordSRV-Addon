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

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.utils.BookUtils;
import com.loohp.interactivechat.utils.FilledMapUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.graphics.GifSequenceWriter;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.HoverClickDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayType;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordItemStackUtils.DiscordDescription;
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordItemStackUtils.DiscordToolTip;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.TitledInventoryWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DiscordContentUtils {

    public static List<DiscordMessageContent> createContents(List<DiscordDisplayData> dataList, OfflineICPlayer player) {
        List<DiscordMessageContent> contents = new ArrayList<>();
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
                        color = new Color(0xFFFFFE);
                    }
                    try {
                        BufferedImage image = ImageGeneration.getItemStackImage(item, data.getPlayer(), InteractiveChatDiscordSrvAddon.plugin.itemAltAir);
                        byte[] imageData = ImageUtils.toArray(image);

                        DiscordDescription description = DiscordItemStackUtils.getDiscordDescription(item, player);

                        DiscordMessageContent content = new DiscordMessageContent(description.getName(), "attachment://Item_" + i + ".png", color);
                        content.addAttachment("Item_" + i + ".png", imageData);
                        contents.add(content);

                        if (InteractiveChatDiscordSrvAddon.plugin.itemUseTooltipImage) {
                            DiscordToolTip discordToolTip = DiscordItemStackUtils.getToolTip(item, player);
                            if (!discordToolTip.isBaseItem() || InteractiveChatDiscordSrvAddon.plugin.itemUseTooltipImageOnBaseItem) {
                                BufferedImage tooltip = ImageGeneration.getToolTipImage(discordToolTip.getComponents());
                                byte[] tooltipData = ImageUtils.toArray(tooltip);
                                content.addAttachment("ToolTip_" + i + ".png", tooltipData);
                                content.addImageUrl("attachment://ToolTip_" + i + ".png");
                            } else {
                                content.addDescription(description.getDescription().orElse(null));
                            }
                        } else {
                            content.addDescription(description.getDescription().orElse(null));
                        }

                        if (type.equals(ImageDisplayType.ITEM_CONTAINER)) {
                            if (!description.getDescription().isPresent()) {
                                content.getImageUrls().remove("attachment://ToolTip_" + i + ".png");
                                content.getAttachments().remove("ToolTip_" + i + ".png");
                            }
                            TitledInventoryWrapper inv = iData.getInventory().get();
                            BufferedImage container = ImageGeneration.getInventoryImage(inv.getInventory(), inv.getTitle(), data.getPlayer());
                            byte[] containerData = ImageUtils.toArray(container);
                            content.addAttachment("Container_" + i + ".png", containerData);
                            content.addImageUrl("attachment://Container_" + i + ".png");
                        } else {
                            if (iData.isFilledMap()) {
                                MapView mapView = FilledMapUtils.getMapView(item);
                                boolean isContextual = mapView == null || FilledMapUtils.isContextual(mapView);
                                ICPlayer icPlayer = iData.getPlayer().getPlayer();
                                boolean isPlayerLocal = icPlayer != null && icPlayer.isLocal();
                                if (!isContextual || isPlayerLocal) {
                                    if (!description.getDescription().isPresent()) {
                                        content.getImageUrls().remove("attachment://ToolTip_" + i + ".png");
                                        content.getAttachments().remove("ToolTip_" + i + ".png");
                                    }
                                    BufferedImage map = ImageGeneration.getMapImage(item, isPlayerLocal ? icPlayer.getLocalPlayer() : null);
                                    byte[] mapData = ImageUtils.toArray(map);
                                    content.addAttachment("Map_" + i + ".png", mapData);
                                    content.addImageUrl("attachment://Map_" + i + ".png");
                                }
                            } else if (iData.isBook()) {
                                List<Component> pages = BookUtils.getPages((BookMeta) item.getItemMeta());
                                List<BufferedImage> images = ImageGeneration.getBookInterface(pages);
                                if (!images.isEmpty()) {
                                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                                    ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(output);
                                    GifSequenceWriter writer = new GifSequenceWriter(imageOutputStream, images.get(0).getType(), 5000, true);
                                    for (BufferedImage page : images) {
                                        writer.writeToSequence(page);
                                    }
                                    writer.close();
                                    imageOutputStream.close();

                                    DiscordMessageContent bookContent = new DiscordMessageContent(null, null, null, "attachment://Pages.gif", color);
                                    bookContent.addAttachment("Pages.gif", output.toByteArray());
                                    contents.add(bookContent);
                                }
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
                            byte[] bottleData = ImageUtils.toArray(InteractiveChatDiscordSrvAddon.plugin.modelRenderer.render(32, 32, InteractiveChatDiscordSrvAddon.plugin.resourceManager, CustomItemTextureUtils.getItemPostResolveFunction(InteractiveChatDiscordSrvAddon.plugin.resourceManager, null, XMaterial.EXPERIENCE_BOTTLE.parseItem(), InteractiveChat.version.isOld(), null).orElse(null), InteractiveChat.version.isOld(), "minecraft:item/experience_bottle", ModelDisplayPosition.GUI, false, null, null).getImage());
                            content.addAttachment("Level_" + i + ".png", bottleData);
                            content.setFooter(LanguageUtils.getTranslation(TranslationKeyUtils.getLevelTranslation(level), InteractiveChatDiscordSrvAddon.plugin.language).replaceFirst("%s", level + ""));
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
        return contents;
    }

}
