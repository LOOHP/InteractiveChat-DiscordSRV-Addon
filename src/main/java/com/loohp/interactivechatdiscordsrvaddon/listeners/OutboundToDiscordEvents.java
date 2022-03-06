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

package com.loohp.interactivechatdiscordsrvaddon.listeners;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.api.InteractiveChatAPI;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.objectholders.CustomPlaceholder;
import com.loohp.interactivechat.objectholders.ICPlaceholder;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.ICPlayerFactory;
import com.loohp.interactivechat.objectholders.MentionPair;
import com.loohp.interactivechat.objectholders.PlaceholderCooldownManager;
import com.loohp.interactivechat.objectholders.WebData;
import com.loohp.interactivechat.registry.Registry;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ColorUtils;
import com.loohp.interactivechat.utils.ComponentFlattening;
import com.loohp.interactivechat.utils.ComponentModernizing;
import com.loohp.interactivechat.utils.ComponentReplacing;
import com.loohp.interactivechat.utils.CustomStringUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.InventoryUtils;
import com.loohp.interactivechat.utils.ItemStackUtils;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechat.utils.PlaceholderParser;
import com.loohp.interactivechat.utils.PlayerUtils;
import com.loohp.interactivechat.utils.XMaterialUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.api.events.DiscordImageEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessagePostProcessEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessagePreProcessEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessageProcessInventoryEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessageProcessItemEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessageProcessPlayerInventoryEvent;
import com.loohp.interactivechatdiscordsrvaddon.debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AdvancementType;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.AttachmentData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.HoverClickDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.IDProvider;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayType;
import com.loohp.interactivechatdiscordsrvaddon.registry.DiscordDataRegistry;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.utils.AchievementUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.AdvancementUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.DeathMessageUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordItemStackUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordItemStackUtils.DiscordDescription;
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordItemStackUtils.DiscordToolTip;
import com.loohp.interactivechatdiscordsrvaddon.utils.TranslationKeyUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.URLRequestUtils;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.TitledInventoryWrapper;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AchievementMessagePostProcessEvent;
import github.scarsz.discordsrv.api.events.AchievementMessagePreProcessEvent;
import github.scarsz.discordsrv.api.events.DeathMessagePostProcessEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessagePostProcessEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageSentEvent;
import github.scarsz.discordsrv.api.events.GameChatMessagePreProcessEvent;
import github.scarsz.discordsrv.api.events.VentureChatMessagePreProcessEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.ChannelType;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.MessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.objects.MessageFormat;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.MessageUtil;
import github.scarsz.discordsrv.util.PlaceholderUtil;
import github.scarsz.discordsrv.util.WebhookUtil;
import mineverse.Aust1n46.chat.api.MineverseChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;

public class OutboundToDiscordEvents implements Listener {

    public static final Comparator<DiscordDisplayData> DISPLAY_DATA_COMPARATOR = Comparator.comparing(each -> each.getPosition());
    public static final Map<Integer, DiscordDisplayData> DATA = Collections.synchronizedMap(new LinkedHashMap<>());
    public static final Map<Integer, AttachmentData> RESEND_WITH_ATTACHMENT = Collections.synchronizedMap(new LinkedHashMap<>());
    private static final IDProvider DATA_ID_PROVIDER = new IDProvider();
    private static final Map<UUID, ItemStack> DEATH_BY = new ConcurrentHashMap<>();

    private static List<DiscordMessageContent> createContents(List<DiscordDisplayData> dataList, Player player) {
        List<DiscordMessageContent> contents = new ArrayList<>();
        for (DiscordDisplayData data : dataList) {
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

                        DiscordMessageContent content = new DiscordMessageContent(description.getName(), "attachment://Item.png", color);
                        content.addAttachment("Item.png", imageData);
                        contents.add(content);

                        if (InteractiveChatDiscordSrvAddon.plugin.itemUseTooltipImage) {
                            DiscordToolTip discordToolTip = DiscordItemStackUtils.getToolTip(item, player);
                            if (!discordToolTip.isBaseItem() || InteractiveChatDiscordSrvAddon.plugin.itemUseTooltipImageOnBaseItem) {
                                BufferedImage tooltip = ImageGeneration.getToolTipImage(discordToolTip.getComponents());
                                byte[] tooltipData = ImageUtils.toArray(tooltip);
                                content.addAttachment("ToolTip.png", tooltipData);
                                content.addImageUrl("attachment://ToolTip.png");
                            } else {
                                content.addDescription(description.getDescription().orElse(null));
                            }
                        } else {
                            content.addDescription(description.getDescription().orElse(null));
                        }

                        if (type.equals(ImageDisplayType.ITEM_CONTAINER)) {
                            if (!description.getDescription().isPresent()) {
                                content.getImageUrls().remove("attachment://ToolTip.png");
                                content.getAttachments().remove("ToolTip.png");
                            }
                            TitledInventoryWrapper inv = iData.getInventory().get();
                            BufferedImage container = ImageGeneration.getInventoryImage(inv.getInventory(), inv.getTitle(), data.getPlayer());
                            byte[] containerData = ImageUtils.toArray(container);
                            content.addAttachment("Container.png", containerData);
                            content.addImageUrl("attachment://Container.png");
                        } else {
                            if (iData.isFilledMap() && iData.getPlayer().isLocal()) {
                                if (!description.getDescription().isPresent()) {
                                    content.getImageUrls().remove("attachment://ToolTip.png");
                                    content.getAttachments().remove("ToolTip.png");
                                }
                                BufferedImage map = ImageGeneration.getMapImage(item, iData.getPlayer().getLocalPlayer());
                                byte[] mapData = ImageUtils.toArray(map);
                                content.addAttachment("Map.png", mapData);
                                content.addImageUrl("attachment://Map.png");
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
                        DiscordMessageContent content = new DiscordMessageContent(title, null, null, "attachment://Inventory.png", color);
                        content.addAttachment("Inventory.png", imageData);
                        if (type.equals(ImageDisplayType.INVENTORY) && InteractiveChatDiscordSrvAddon.plugin.invShowLevel) {
                            int level = iData.getPlayer().getExperienceLevel();
                            byte[] bottleData = ImageUtils.toArray(InteractiveChatDiscordSrvAddon.plugin.modelRenderer.render(32, 32, InteractiveChatDiscordSrvAddon.plugin.resourceManager, InteractiveChat.version.isOld(), "minecraft:item/experience_bottle", ModelDisplayPosition.GUI, false).getImage());
                            content.addAttachment("Level.png", bottleData);
                            content.setFooter(LanguageUtils.getTranslation(TranslationKeyUtils.getLevelTranslation(level), InteractiveChatDiscordSrvAddon.plugin.language).replaceFirst("%s", level + ""));
                            content.setFooterImageUrl("attachment://Level.png");
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
                            content.addAttachment("ToolTip.png", tooltipData);
                            content.addImageUrl("attachment://ToolTip.png");
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
                        content.setAuthorIconUrl("attachment://Hover.png");
                        content.addAttachment("Hover.png", imageData);
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

    @Subscribe(priority = ListenerPriority.LOW)
    public void onDiscordToGame(DiscordGuildMessagePostProcessEvent event) {
        Debug.debug("Triggering onDiscordToGame");
        InteractiveChatDiscordSrvAddon.plugin.messagesCounter.incrementAndGet();
        github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component component = event.getMinecraftMessage();
        if (InteractiveChatDiscordSrvAddon.plugin.escapePlaceholdersFromDiscord) {
            Debug.debug("onDiscordToGame escaping placeholders");
            for (ICPlaceholder placeholder : InteractiveChat.placeholderList.values()) {
                component = component.replaceText(github.scarsz.discordsrv.dependencies.kyori.adventure.text.TextReplacementConfig.builder().match(placeholder.getKeyword()).replacement("\\" + placeholder.getKeyword()).build());
            }
            event.setMinecraftMessage(component);
        }
    }

    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onGameToDiscord(GameChatMessagePreProcessEvent event) {
        Debug.debug("Triggering onGameToDiscord");
        InteractiveChatDiscordSrvAddon.plugin.messagesCounter.incrementAndGet();

        Player sender = event.getPlayer();
        ICPlayer icSender = ICPlayerFactory.getICPlayer(sender);
        Component message = ComponentStringUtils.toRegularComponent(event.getMessageComponent());

        message = processGameMessage(icSender, message);

        event.setMessageComponent(ComponentStringUtils.toDiscordSRVComponent(message));
    }

    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onVentureChatHookToDiscord(VentureChatMessagePreProcessEvent event) {
        ICPlayer icSender = null;
        MineverseChatPlayer mcPlayer = event.getVentureChatEvent().getMineverseChatPlayer();
        if (mcPlayer != null) {
            icSender = ICPlayerFactory.getICPlayer(mcPlayer.getUUID());
        } else {
            icSender = ICPlayerFactory.getICPlayerExact(event.getVentureChatEvent().getUsername());
        }
        if (icSender == null) {
            return;
        }
        Component message = ComponentStringUtils.toRegularComponent(event.getMessageComponent());

        message = processGameMessage(icSender, message);

        event.setMessageComponent(ComponentStringUtils.toDiscordSRVComponent(message));
    }

    @SuppressWarnings("deprecation")
    public Component processGameMessage(ICPlayer icSender, Component component) {
        boolean reserializer = DiscordSRV.config().getBoolean("Experiment_MCDiscordReserializer_ToDiscord");
        PlaceholderCooldownManager cooldownManager = InteractiveChatDiscordSrvAddon.plugin.placeholderCooldownManager;
        long now = cooldownManager.checkMessage(icSender.getUniqueId(), PlainTextComponentSerializer.plainText().serialize(component)).getTimeNow();

        GameMessagePreProcessEvent gameMessagePreProcessEvent = new GameMessagePreProcessEvent(icSender, component, false);
        Bukkit.getPluginManager().callEvent(gameMessagePreProcessEvent);
        if (gameMessagePreProcessEvent.isCancelled()) {
            return null;
        }
        component = ComponentFlattening.flatten(gameMessagePreProcessEvent.getComponent());

        String plain = InteractiveChatComponentSerializer.plainText().serialize(component);

        if (InteractiveChat.useItem && PlayerUtils.hasPermission(icSender.getUniqueId(), "interactivechat.module.item", true, 200)) {
            Debug.debug("onGameToDiscord processing item display");
            if (!cooldownManager.isPlaceholderOnCooldownAt(icSender.getUniqueId(), InteractiveChat.placeholderList.values().stream().filter(each -> each.getKeyword().equals(InteractiveChat.itemPlaceholder)).findFirst().get(), now)) {
                Matcher matcher = InteractiveChat.itemPlaceholder.matcher(plain);
                if (matcher.find()) {
                    ItemStack item = PlayerUtils.getHeldItem(icSender);
                    boolean isAir = item.getType().equals(Material.AIR);
                    XMaterial xMaterial = XMaterialUtils.matchXMaterial(item);
                    String itemStr = PlainTextComponentSerializer.plainText().serialize(ComponentStringUtils.convertTranslatables(ComponentModernizing.modernize(ItemStackUtils.getDisplayName(item)), InteractiveChatDiscordSrvAddon.plugin.resourceManager.getLanguageManager().getTranslateFunction().ofLanguage(InteractiveChatDiscordSrvAddon.plugin.language)));
                    itemStr = ComponentStringUtils.stripColorAndConvertMagic(itemStr);

                    int amount = item.getAmount();
                    if (isAir) {
                        amount = 1;
                    }

                    String replaceText = PlaceholderParser.parse(icSender, (amount == 1 ? ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.itemSingularReplaceText) : ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.itemReplaceText).replace("{Amount}", String.valueOf(amount))).replace("{Item}", itemStr));
                    if (reserializer) {
                        replaceText = MessageUtil.reserializeToDiscord(github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component.text(replaceText));
                    }

                    AtomicBoolean replaced = new AtomicBoolean(false);
                    Component replaceComponent = LegacyComponentSerializer.legacySection().deserialize(replaceText);
                    component = ComponentReplacing.replace(component, InteractiveChat.itemPlaceholder.pattern(), true, (groups) -> {
                        replaced.set(true);
                        return replaceComponent;
                    });
                    if (replaced.get() && InteractiveChatDiscordSrvAddon.plugin.itemImage) {
                        int inventoryId = DATA_ID_PROVIDER.getNext();
                        int position = matcher.start();

                        String title = PlaceholderParser.parse(icSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.itemTitle));

                        Inventory inv = null;
                        if (item.hasItemMeta() && item.getItemMeta() instanceof BlockStateMeta) {
                            BlockState bsm = ((BlockStateMeta) item.getItemMeta()).getBlockState();
                            if (bsm instanceof InventoryHolder) {
                                Inventory container = ((InventoryHolder) bsm).getInventory();
                                if (!container.isEmpty()) {
                                    inv = Bukkit.createInventory(null, InventoryUtils.toMultipleOf9(container.getSize()));
                                    for (int j = 0; j < container.getSize(); j++) {
                                        if (container.getItem(j) != null) {
                                            if (!container.getItem(j).getType().equals(Material.AIR)) {
                                                inv.setItem(j, container.getItem(j).clone());
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        GameMessageProcessItemEvent gameMessageProcessItemEvent = new GameMessageProcessItemEvent(icSender, title, component, false, inventoryId, item.clone(), inv);
                        Bukkit.getPluginManager().callEvent(gameMessageProcessItemEvent);
                        if (!gameMessageProcessItemEvent.isCancelled()) {
                            component = gameMessageProcessItemEvent.getComponent();
                            title = gameMessageProcessItemEvent.getTitle();
                            if (gameMessageProcessItemEvent.hasInventory()) {
                                DATA.put(inventoryId, new ImageDisplayData(icSender, position, title, ImageDisplayType.ITEM_CONTAINER, gameMessageProcessItemEvent.getItemStack().clone(), new TitledInventoryWrapper(ItemStackUtils.getDisplayName(item, null), gameMessageProcessItemEvent.getInventory())));
                            } else {
                                DATA.put(inventoryId, new ImageDisplayData(icSender, position, title, ImageDisplayType.ITEM, gameMessageProcessItemEvent.getItemStack().clone()));
                            }
                        }
                        component = component.append(Component.text("<ICD=" + inventoryId + ">"));
                    }
                }
            }
        }

        if (InteractiveChat.useInventory && PlayerUtils.hasPermission(icSender.getUniqueId(), "interactivechat.module.inventory", true, 200)) {
            Debug.debug("onGameToDiscord processing inventory display");
            if (!cooldownManager.isPlaceholderOnCooldownAt(icSender.getUniqueId(), InteractiveChat.placeholderList.values().stream().filter(each -> each.getKeyword().equals(InteractiveChat.invPlaceholder)).findFirst().get(), now)) {
                Matcher matcher = InteractiveChat.invPlaceholder.matcher(plain);
                if (matcher.find()) {
                    String replaceText = PlaceholderParser.parse(icSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.invReplaceText));
                    if (reserializer) {
                        replaceText = MessageUtil.reserializeToDiscord(github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component.text(replaceText));
                    }

                    AtomicBoolean replaced = new AtomicBoolean(false);
                    Component replaceComponent = LegacyComponentSerializer.legacySection().deserialize(replaceText);
                    component = ComponentReplacing.replace(component, InteractiveChat.invPlaceholder.pattern(), true, (groups) -> {
                        replaced.set(true);
                        return replaceComponent;
                    });

                    if (replaced.get() && InteractiveChatDiscordSrvAddon.plugin.invImage) {
                        int inventoryId = DATA_ID_PROVIDER.getNext();
                        int position = matcher.start();

                        Inventory inv = Bukkit.createInventory(null, 45);
                        for (int j = 0; j < icSender.getInventory().getSize(); j++) {
                            if (icSender.getInventory().getItem(j) != null) {
                                if (!icSender.getInventory().getItem(j).getType().equals(Material.AIR)) {
                                    inv.setItem(j, icSender.getInventory().getItem(j).clone());
                                }
                            }
                        }
                        String title = PlaceholderParser.parse(icSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.invTitle));

                        GameMessageProcessPlayerInventoryEvent gameMessageProcessPlayerInventoryEvent = new GameMessageProcessPlayerInventoryEvent(icSender, title, component, false, inventoryId, inv);
                        Bukkit.getPluginManager().callEvent(gameMessageProcessPlayerInventoryEvent);
                        if (!gameMessageProcessPlayerInventoryEvent.isCancelled()) {
                            component = gameMessageProcessPlayerInventoryEvent.getComponent();
                            title = gameMessageProcessPlayerInventoryEvent.getTitle();
                            DATA.put(inventoryId, new ImageDisplayData(icSender, position, title, ImageDisplayType.INVENTORY, true, new TitledInventoryWrapper(Component.translatable(TranslationKeyUtils.getDefaultContainerTitle()), gameMessageProcessPlayerInventoryEvent.getInventory())));
                        }

                        component = component.append(Component.text("<ICD=" + inventoryId + ">"));
                    }
                }
            }
        }

        if (InteractiveChat.useEnder && PlayerUtils.hasPermission(icSender.getUniqueId(), "interactivechat.module.enderchest", true, 200)) {
            Debug.debug("onGameToDiscord processing enderchest display");
            if (!cooldownManager.isPlaceholderOnCooldownAt(icSender.getUniqueId(), InteractiveChat.placeholderList.values().stream().filter(each -> each.getKeyword().equals(InteractiveChat.enderPlaceholder)).findFirst().get(), now)) {
                Matcher matcher = InteractiveChat.enderPlaceholder.matcher(plain);
                if (matcher.find()) {
                    String replaceText = PlaceholderParser.parse(icSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.enderReplaceText));
                    if (reserializer) {
                        replaceText = MessageUtil.reserializeToDiscord(github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component.text(replaceText));
                    }

                    AtomicBoolean replaced = new AtomicBoolean(false);
                    Component replaceComponent = LegacyComponentSerializer.legacySection().deserialize(replaceText);
                    component = ComponentReplacing.replace(component, InteractiveChat.enderPlaceholder.pattern(), true, (groups) -> {
                        replaced.set(true);
                        return replaceComponent;
                    });

                    if (replaced.get() && InteractiveChatDiscordSrvAddon.plugin.enderImage) {
                        int inventoryId = DATA_ID_PROVIDER.getNext();
                        int position = matcher.start();

                        Inventory inv = Bukkit.createInventory(null, InventoryUtils.toMultipleOf9(icSender.getEnderChest().getSize()));
                        for (int j = 0; j < icSender.getEnderChest().getSize(); j++) {
                            if (icSender.getEnderChest().getItem(j) != null) {
                                if (!icSender.getEnderChest().getItem(j).getType().equals(Material.AIR)) {
                                    inv.setItem(j, icSender.getEnderChest().getItem(j).clone());
                                }
                            }
                        }
                        String title = PlaceholderParser.parse(icSender, ComponentStringUtils.stripColorAndConvertMagic(InteractiveChat.enderTitle));

                        GameMessageProcessInventoryEvent gameMessageProcessInventoryEvent = new GameMessageProcessInventoryEvent(icSender, title, component, false, inventoryId, inv);
                        Bukkit.getPluginManager().callEvent(gameMessageProcessInventoryEvent);
                        if (!gameMessageProcessInventoryEvent.isCancelled()) {
                            component = gameMessageProcessInventoryEvent.getComponent();
                            title = gameMessageProcessInventoryEvent.getTitle();
                            DATA.put(inventoryId, new ImageDisplayData(icSender, position, title, ImageDisplayType.ENDERCHEST, new TitledInventoryWrapper(Component.translatable(TranslationKeyUtils.getEnderChestContainerTitle()), gameMessageProcessInventoryEvent.getInventory())));
                        }

                        component = component.append(Component.text("<ICD=" + inventoryId + ">"));
                    }
                }
            }
        }

        Debug.debug("onGameToDiscord processing custom placeholders");
        for (ICPlaceholder placeholder : InteractiveChatAPI.getICPlaceholderList()) {
            if (!placeholder.isBuildIn()) {
                CustomPlaceholder customP = (CustomPlaceholder) placeholder;
                if (!InteractiveChat.useCustomPlaceholderPermissions || (InteractiveChat.useCustomPlaceholderPermissions && PlayerUtils.hasPermission(icSender.getUniqueId(), customP.getPermission(), true, 200))) {
                    boolean onCooldown = cooldownManager.isPlaceholderOnCooldownAt(icSender.getUniqueId(), customP, now);
                    Matcher matcher = customP.getKeyword().matcher(plain);
                    if (matcher.find() && !onCooldown) {
                        String replaceText;
                        if (customP.getReplace().isEnabled()) {
                            replaceText = ChatColorUtils.translateAlternateColorCodes('&', PlaceholderParser.parse(icSender, customP.getReplace().getReplaceText()));
                        } else {
                            replaceText = null;
                        }
                        List<Component> toAppend = new LinkedList<>();
                        Set<String> shown = new HashSet<>();
                        component = ComponentReplacing.replace(component, customP.getKeyword().pattern(), true, (result, matchedComponents) -> {
                            String replaceString = replaceText == null ? result.group() : CustomStringUtils.applyReplacementRegex(replaceText, result, 1);
                            if (!shown.contains(replaceString)) {
                                shown.add(replaceString);
                                int position = result.start();
                                if (InteractiveChatDiscordSrvAddon.plugin.hoverEnabled && !InteractiveChatDiscordSrvAddon.plugin.hoverIgnore.contains(customP.getPosition())) {
                                    HoverClickDisplayData.Builder hoverClick = new HoverClickDisplayData.Builder().player(icSender).postion(position).color(DiscordDataRegistry.DISCORD_HOVER_COLOR).displayText(ChatColorUtils.stripColor(replaceString));
                                    boolean usingHoverClick = false;

                                    if (customP.getHover().isEnabled()) {
                                        usingHoverClick = true;
                                        String hoverText = ChatColorUtils.translateAlternateColorCodes('&', PlaceholderParser.parse(icSender, CustomStringUtils.applyReplacementRegex(customP.getHover().getText(), result, 1)));
                                        Color color = ColorUtils.getFirstColor(hoverText);
                                        hoverClick.hoverText(LegacyComponentSerializer.legacySection().deserialize(hoverText));
                                        if (color != null) {
                                            hoverClick.color(color);
                                        }
                                    }

                                    if (customP.getClick().isEnabled()) {
                                        usingHoverClick = true;
                                        hoverClick.clickAction(customP.getClick().getAction()).clickValue(CustomStringUtils.applyReplacementRegex(customP.getClick().getValue(), result, 1));
                                    }

                                    if (usingHoverClick) {
                                        int hoverId = DATA_ID_PROVIDER.getNext();
                                        DATA.put(hoverId, hoverClick.build());
                                        toAppend.add(Component.text("<ICD=" + hoverId + ">"));
                                    }
                                }
                            }
                            return replaceText == null ? Component.empty().children(matchedComponents) : LegacyComponentSerializer.legacySection().deserialize(replaceString);
                        });
                        for (Component componentToAppend : toAppend) {
                            component = component.append(componentToAppend);
                        }
                    }
                }
            }
        }

        if (InteractiveChat.t && WebData.getInstance() != null) {
            for (CustomPlaceholder customP : WebData.getInstance().getSpecialPlaceholders()) {
                boolean onCooldown = cooldownManager.isPlaceholderOnCooldownAt(icSender.getUniqueId(), customP, now);
                Matcher matcher = customP.getKeyword().matcher(plain);
                if (matcher.find() && !onCooldown) {
                    String replaceText;
                    if (customP.getReplace().isEnabled()) {
                        replaceText = ChatColorUtils.translateAlternateColorCodes('&', PlaceholderParser.parse(icSender, customP.getReplace().getReplaceText()));
                    } else {
                        replaceText = null;
                    }
                    List<Component> toAppend = new LinkedList<>();
                    Set<String> shown = new HashSet<>();
                    component = ComponentReplacing.replace(component, customP.getKeyword().pattern(), true, (result, matchedComponents) -> {
                        String replaceString = replaceText == null ? result.group() : CustomStringUtils.applyReplacementRegex(replaceText, result, 1);
                        if (!shown.contains(replaceString)) {
                            shown.add(replaceString);
                            int position = result.start();
                            if (InteractiveChatDiscordSrvAddon.plugin.hoverEnabled && !InteractiveChatDiscordSrvAddon.plugin.hoverIgnore.contains(customP.getPosition())) {
                                HoverClickDisplayData.Builder hoverClick = new HoverClickDisplayData.Builder().player(icSender).postion(position).color(DiscordDataRegistry.DISCORD_HOVER_COLOR).displayText(ChatColorUtils.stripColor(replaceString));
                                boolean usingHoverClick = false;

                                if (customP.getHover().isEnabled()) {
                                    usingHoverClick = true;
                                    String hoverText = ChatColorUtils.translateAlternateColorCodes('&', PlaceholderParser.parse(icSender, CustomStringUtils.applyReplacementRegex(customP.getHover().getText(), result, 1)));
                                    Color color = ColorUtils.getFirstColor(hoverText);
                                    hoverClick.hoverText(LegacyComponentSerializer.legacySection().deserialize(hoverText));
                                    if (color != null) {
                                        hoverClick.color(color);
                                    }
                                }

                                if (customP.getClick().isEnabled()) {
                                    usingHoverClick = true;
                                    hoverClick.clickAction(customP.getClick().getAction()).clickValue(CustomStringUtils.applyReplacementRegex(customP.getClick().getValue(), result, 1));
                                }

                                if (usingHoverClick) {
                                    int hoverId = DATA_ID_PROVIDER.getNext();
                                    DATA.put(hoverId, hoverClick.build());
                                    toAppend.add(Component.text("<ICD=" + hoverId + ">"));
                                }
                            }
                        }
                        return replaceText == null ? Component.empty().children(matchedComponents) : LegacyComponentSerializer.legacySection().deserialize(replaceString);
                    });
                    for (Component componentToAppend : toAppend) {
                        component = component.append(componentToAppend);
                    }
                }
            }
        }

        DiscordSRV srv = InteractiveChatDiscordSrvAddon.discordsrv;
        if (InteractiveChatDiscordSrvAddon.plugin.translateMentions) {
            Debug.debug("onGameToDiscord processing mentions");
            List<MentionPair> distinctMentionPairs = new ArrayList<>();
            Set<UUID> processedReceivers = new HashSet<>();
            synchronized (InteractiveChat.mentionPair) {
                for (MentionPair pair : InteractiveChat.mentionPair) {
                    if (!processedReceivers.contains(pair.getReciever())) {
                        distinctMentionPairs.add(pair);
                        processedReceivers.add(pair.getReciever());
                    }
                }
            }
            for (MentionPair pair : distinctMentionPairs) {
                if (pair.getSender().equals(icSender.getUniqueId())) {
                    UUID receiverUUID = pair.getReciever();
                    Set<String> names = new HashSet<>();
                    ICPlayer receiver = ICPlayerFactory.getICPlayer(receiverUUID);
                    if (receiver != null) {
                        names.add(ChatColorUtils.stripColor(receiver.getName()));
                        List<String> list = InteractiveChatAPI.getNicknames(receiver.getUniqueId());
                        for (String name : list) {
                            names.add(ChatColorUtils.stripColor(name));
                        }
                    }
                    String userId = srv.getAccountLinkManager().getDiscordId(receiverUUID);
                    if (userId != null) {
                        User user = srv.getJda().getUserById(userId);
                        if (user != null) {
                            String discordMention = user.getAsMention();
                            for (String name : names) {
                                component = ComponentReplacing.replace(component, CustomStringUtils.escapeMetaCharacters(Registry.MENTION_TAG_CONVERTER.getTagStyle(InteractiveChat.mentionPrefix + name)), true, LegacyComponentSerializer.legacySection().deserialize(discordMention));
                            }
                        }
                    }
                }
            }
        }

        GameMessagePostProcessEvent gameMessagePostProcessEvent = new GameMessagePostProcessEvent(icSender, component, false);
        Bukkit.getPluginManager().callEvent(gameMessagePostProcessEvent);
        if (gameMessagePostProcessEvent.isCancelled()) {
            return null;
        }
        component = gameMessagePostProcessEvent.getComponent();
        return component;
    }

    //=====Death Message

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent event) {
        if (!InteractiveChatDiscordSrvAddon.plugin.deathMessageItem) {
            return;
        }
        Debug.debug("Triggered onDeath");
        Player player = event.getEntity();
        Component deathMessage = DeathMessageUtils.getDeathMessage(player);
        ItemStack item = ComponentStringUtils.extractItemStack(deathMessage);
        DEATH_BY.put(player.getUniqueId(), item == null ? new ItemStack(Material.AIR) : item);
    }

    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onDeathMessageSend(DeathMessagePostProcessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (!InteractiveChatDiscordSrvAddon.plugin.deathMessageItem) {
            return;
        }
        Debug.debug("Triggered onDeathMessageSend");
        ItemStack item = DEATH_BY.remove(event.getPlayer().getUniqueId());
        if (item == null || item.getType().equals(Material.AIR)) {
            return;
        }
        if (!item.hasItemMeta()) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName() || meta.getDisplayName().length() <= 0) {
            return;
        }
        Color color = null;
        if (!event.getDiscordMessage().getEmbeds().isEmpty()) {
            color = event.getDiscordMessage().getEmbeds().get(0).getColor();
        }
        if (color == null) {
            color = Color.black;
        }
        Player player = event.getPlayer();
        DiscordMessageContent content = new DiscordMessageContent(ChatColorUtils.stripColor(meta.getDisplayName()), "attachment://Item.png", color);
        try {
            BufferedImage image = ImageGeneration.getItemStackImage(item, ICPlayerFactory.getICPlayer(player), InteractiveChatDiscordSrvAddon.plugin.itemAltAir);
            byte[] itemData = ImageUtils.toArray(image);

            DiscordDescription description = DiscordItemStackUtils.getDiscordDescription(item, player);

            content.addAttachment("Item.png", itemData);

            if (InteractiveChatDiscordSrvAddon.plugin.itemUseTooltipImage) {
                DiscordToolTip discordToolTip = DiscordItemStackUtils.getToolTip(item, player);
                if (!discordToolTip.isBaseItem() || InteractiveChatDiscordSrvAddon.plugin.itemUseTooltipImageOnBaseItem) {
                    BufferedImage tooltip = ImageGeneration.getToolTipImage(discordToolTip.getComponents());
                    byte[] tooltipData = ImageUtils.toArray(tooltip);
                    content.addAttachment("ToolTip.png", tooltipData);
                    content.addImageUrl("attachment://ToolTip.png");
                } else {
                    content.addDescription(description.getDescription().orElse(null));
                }
            } else {
                content.addDescription(description.getDescription().orElse(null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(InteractiveChat.plugin, () -> {
            Debug.debug("onDeathMessageSend sending item to discord");
            TextChannel destinationChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(event.getChannel());
            if (event.isUsingWebhooks()) {
                String webHookUrl = WebhookUtil.getWebhookUrlToUseForChannel(destinationChannel);
                WebhookClient client = WebhookClient.withUrl(webHookUrl);

                if (client == null) {
                    throw new NullPointerException("Unable to get the Webhook client URL for the TextChannel " + destinationChannel.getName());
                }

                client.send(content.toWebhookMessageBuilder().setUsername(event.getWebhookName()).setAvatarUrl(event.getWebhookAvatarUrl()).build());
                client.close();
            } else {
                content.toJDAMessageRestAction(destinationChannel).queue();
            }
        }, 5);
    }

    //===== Advancement

    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onAdvancement(AchievementMessagePreProcessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Debug.debug("Triggered onAdvancement");
        MessageFormat messageFormat = event.getMessageFormat();
        if (messageFormat == null) {
            return;
        }

        String title = null;
        String description = null;
        ItemStack item = null;
        AdvancementType advancementType = null;
        boolean isMinecraft = true;

        Event bukkitEvent = event.getTriggeringBukkitEvent();
        if (bukkitEvent.getClass().getSimpleName().equals("PlayerAdvancementDoneEvent")) {
            Debug.debug("onAdvancement getting advancement");
            Object bukkitAdvancement = AdvancementUtils.getAdvancementFromEvent(bukkitEvent);
            AdvancementData data = AdvancementUtils.getAdvancementData(bukkitAdvancement);
            if (data == null) {
                return;
            }
            title = InteractiveChatComponentSerializer.bungeecordApiLegacy().serialize(data.getTitle(), InteractiveChatDiscordSrvAddon.plugin.language);
            description = InteractiveChatComponentSerializer.bungeecordApiLegacy().serialize(data.getDescription(), InteractiveChatDiscordSrvAddon.plugin.language);
            item = data.getItem();
            advancementType = data.getAdvancementType();
            isMinecraft = data.isMinecraft();
        } else if (bukkitEvent.getClass().getSimpleName().equals("PlayerAchievementAwardedEvent")) {
            Debug.debug("onAdvancement getting achievement");
            Object bukkitAchievement = AchievementUtils.getAdvancementFromEvent(bukkitEvent);
            if (bukkitAchievement == null) {
                return;
            }
            AdvancementData data = AchievementUtils.getAdvancementData(bukkitAchievement);
            if (data == null) {
                return;
            }
            title = InteractiveChatComponentSerializer.bungeecordApiLegacy().serialize(data.getTitle(), InteractiveChatDiscordSrvAddon.plugin.language);
            description = InteractiveChatComponentSerializer.bungeecordApiLegacy().serialize(data.getDescription(), InteractiveChatDiscordSrvAddon.plugin.language);
            item = data.getItem();
            advancementType = data.getAdvancementType();
            isMinecraft = data.isMinecraft();
        } else {
            return;
        }

        Debug.debug("onAdvancement processing advancement");
        if (InteractiveChatDiscordSrvAddon.plugin.advancementItem && item != null && advancementType != null) {
            String content = messageFormat.getContent();
            if (content == null) {
                content = "";
            }
            try {
                int id = DATA_ID_PROVIDER.getNext();
                BufferedImage thumbnail = ImageGeneration.getAdvancementIcon(item, advancementType, true, ICPlayerFactory.getICPlayer(event.getPlayer()));
                byte[] thumbnailData = ImageUtils.toArray(thumbnail);
                content += "<ICA=" + id + ">";
                messageFormat.setContent(content);
                RESEND_WITH_ATTACHMENT.put(id, new AttachmentData("Thumbnail.png", thumbnailData));
                messageFormat.setThumbnailUrl("attachment://Thumbnail.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (InteractiveChatDiscordSrvAddon.plugin.advancementName && title != null) {
            event.setAchievementName(ChatColorUtils.stripColor(title));
            messageFormat.setAuthorName(LanguageUtils.getTranslation(advancementType.getTranslationKey(), InteractiveChatDiscordSrvAddon.plugin.language).replaceFirst("%s", event.getPlayer().getName()).replaceFirst("%s", ChatColorUtils.stripColor(title)));
            Color color;
            if (isMinecraft) {
                color = ColorUtils.getColor(advancementType.getColor());
            } else {
                String colorStr = ChatColorUtils.getFirstColors(title);
                color = ColorUtils.getColor(ColorUtils.toChatColor(colorStr));
            }
            if (color.equals(Color.white)) {
                color = new Color(0xFFFFFE);
            }
            messageFormat.setColorRaw(color.getRGB());
        }
        if (InteractiveChatDiscordSrvAddon.plugin.advancementDescription && description != null) {
            messageFormat.setDescription(ChatColorUtils.stripColor(description));
        }
        event.setMessageFormat(messageFormat);
    }

    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onAdvancementSend(AchievementMessagePostProcessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Debug.debug("Triggered onAdvancementSend");
        Message message = event.getDiscordMessage();
        if (!message.getContentRaw().contains("<ICA=")) {
            return;
        }
        String text = message.getContentRaw();
        Set<Integer> matches = new LinkedHashSet<>();
        synchronized (RESEND_WITH_ATTACHMENT) {
            for (int key : RESEND_WITH_ATTACHMENT.keySet()) {
                if (text.contains("<ICA=" + key + ">")) {
                    matches.add(key);
                }
            }
        }
        event.setCancelled(true);
        DiscordMessageContent content = new DiscordMessageContent(message);
        for (int key : matches) {
            AttachmentData data = RESEND_WITH_ATTACHMENT.remove(key);
            if (data != null) {
                content.addAttachment(data.getName(), data.getData());
            }
        }
        TextChannel destinationChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName(event.getChannel());
        Debug.debug("onAdvancementSend sending message to discord");
        if (event.isUsingWebhooks()) {
            String webHookUrl = WebhookUtil.getWebhookUrlToUseForChannel(destinationChannel);
            WebhookClient client = WebhookClient.withUrl(webHookUrl);

            if (client == null) {
                throw new NullPointerException("Unable to get the Webhook client URL for the TextChannel " + destinationChannel.getName());
            }

            client.send(content.toWebhookMessageBuilder().setUsername(event.getWebhookName()).setAvatarUrl(event.getWebhookAvatarUrl()).build());
            client.close();
        } else {
            content.toJDAMessageRestAction(destinationChannel).queue();
        }
    }

    //=====

    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void discordMessageSent(DiscordGuildMessageSentEvent event) {
        Debug.debug("Triggered discordMessageSent");
        Message message = event.getMessage();
        String textOriginal = message.getContentRaw();
        TextChannel channel = event.getChannel();

        if (!InteractiveChatDiscordSrvAddon.plugin.isEnabled()) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
            String text = textOriginal;

            if (!text.contains("<ICD=")) {
                return;
            }

            Set<Integer> matches = new LinkedHashSet<>();

            synchronized (DATA) {
                for (int key : DATA.keySet()) {
                    if (text.contains("<ICD=" + key + ">")) {
                        text = text.replace("<ICD=" + key + ">", "");
                        matches.add(key);
                    }
                }
            }

            if (matches.isEmpty()) {
                Debug.debug("discordMessageSent keys empty");
                return;
            }

            message.delete().queue();
            ICPlayer player = DATA.get(matches.iterator().next()).getPlayer();

            List<DiscordDisplayData> dataList = new ArrayList<>();

            for (int key : matches) {
                DiscordDisplayData data = DATA.remove(key);
                if (data != null) {
                    dataList.add(data);
                }
            }

            dataList.sort(DISPLAY_DATA_COMPARATOR);

            Debug.debug("discordMessageSent creating contents");
            List<DiscordMessageContent> contents = createContents(dataList, player.isLocal() ? player.getLocalPlayer() : (Bukkit.getOnlinePlayers().isEmpty() ? null : Bukkit.getOnlinePlayers().iterator().next()));

            DiscordImageEvent discordImageEvent = new DiscordImageEvent(channel, textOriginal, text, contents, false, true);
            TextChannel textChannel = discordImageEvent.getChannel();
            if (discordImageEvent.isCancelled()) {
                String restore = discordImageEvent.getOriginalMessage();
                textChannel.sendMessage(restore).queue();
            } else {
                Debug.debug("discordMessageSent sending to discord");
                text = discordImageEvent.getNewMessage();
                textChannel.sendMessage(text).queue();
                for (DiscordMessageContent content : discordImageEvent.getDiscordMessageContents()) {
                    content.toJDAMessageRestAction(textChannel).queue();
                }
            }
        });
    }

    public static class JDAEvents extends ListenerAdapter {

        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
            Debug.debug("Triggered onMessageReceived");
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
            String textOriginal = message.getContentRaw();
            String text = textOriginal;
            if (!text.contains("<ICD=")) {
                return;
            }

            Set<Integer> matches = new LinkedHashSet<>();

            synchronized (DATA) {
                for (int key : DATA.keySet()) {
                    if (text.contains("<ICD=" + key + ">")) {
                        text = text.replace("<ICD=" + key + ">", "");
                        matches.add(key);
                    }
                }
            }

            if (matches.isEmpty()) {
                Debug.debug("onMessageReceived keys empty");
                return;
            }

            message.delete().queue();
            ICPlayer player = DATA.get(matches.iterator().next()).getPlayer();

            List<DiscordDisplayData> dataList = new ArrayList<>();

            for (int key : matches) {
                DiscordDisplayData data = DATA.remove(key);
                if (data != null) {
                    dataList.add(data);
                }
            }

            dataList.sort(DISPLAY_DATA_COMPARATOR);

            Debug.debug("onMessageReceived creating contents");
            List<DiscordMessageContent> contents = createContents(dataList, player.isLocal() ? player.getLocalPlayer() : (Bukkit.getOnlinePlayers().isEmpty() ? null : Bukkit.getOnlinePlayers().iterator().next()));

            List<WebhookMessageBuilder> messagesToSend = new ArrayList<>();

            DiscordImageEvent discordImageEvent = new DiscordImageEvent(channel, textOriginal, text, contents, false, true);
            TextChannel textChannel = discordImageEvent.getChannel();
            if (discordImageEvent.isCancelled()) {
                String restore = discordImageEvent.getOriginalMessage();
                messagesToSend.add(new WebhookMessageBuilder().setContent(restore));
            } else {
                text = discordImageEvent.getNewMessage();
                messagesToSend.add(new WebhookMessageBuilder().setContent(text));
                for (DiscordMessageContent content : discordImageEvent.getDiscordMessageContents()) {
                    messagesToSend.add(content.toWebhookMessageBuilder());
                }
            }

            String avatarUrl = player.isLocal() ? DiscordSRV.getAvatarUrl(player.getLocalPlayer()) : null;
            String username = DiscordSRV.config().getString("Experiment_WebhookChatMessageUsernameFormat")
                .replace("%displayname%", MessageUtil.strip(player.getDisplayName()))
                .replace("%username%", player.getName());
            username = PlaceholderUtil.replacePlaceholders(username, player.isLocal() ? player.getLocalPlayer() : Bukkit.getOfflinePlayer(player.getUniqueId()));
            username = MessageUtil.strip(username);

            String userId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(player.getUniqueId());
            if (userId != null) {
                Member member = DiscordUtil.getMemberById(userId);
                if (member != null) {
                    if (DiscordSRV.config().getBoolean("Experiment_WebhookChatMessageAvatarFromDiscord")) {
                        avatarUrl = member.getUser().getEffectiveAvatarUrl();
                    }
                    if (DiscordSRV.config().getBoolean("Experiment_WebhookChatMessageUsernameFromDiscord")) {
                        username = member.getEffectiveName();
                    }
                }
            }

            String webHookUrl = WebhookUtil.getWebhookUrlToUseForChannel(textChannel);
            WebhookClient client = WebhookClient.withUrl(webHookUrl);

            if (client == null) {
                throw new NullPointerException("Unable to get the Webhook client URL for the TextChannel " + textChannel.getName());
            }

            Debug.debug("onMessageReceived sending to discord");
            for (WebhookMessageBuilder builder : messagesToSend) {
                client.send(builder.setUsername(username).setAvatarUrl(avatarUrl).build());
            }
            client.close();
        }

    }

}
