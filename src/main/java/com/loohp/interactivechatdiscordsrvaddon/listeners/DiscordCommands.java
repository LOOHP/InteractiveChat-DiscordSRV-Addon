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

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.api.InteractiveChatAPI;
import com.loohp.interactivechat.api.InteractiveChatAPI.SharedType;
import com.loohp.interactivechat.api.events.PostPacketComponentProcessEvent;
import com.loohp.interactivechat.bungeemessaging.BungeeMessageSender;
import com.loohp.interactivechat.libs.com.cryptomorin.xseries.XMaterial;
import com.loohp.interactivechat.libs.io.github.bananapuncher714.nbteditor.NBTEditor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TextReplacementConfig;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TranslatableComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.ClickEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.minimessage.MiniMessage;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.modules.InventoryDisplay;
import com.loohp.interactivechat.modules.ItemDisplay;
import com.loohp.interactivechat.objectholders.ICPlaceholder;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.ICPlayerFactory;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechat.objectholders.ValueTrios;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ColorUtils;
import com.loohp.interactivechat.utils.ComponentModernizing;
import com.loohp.interactivechat.utils.ComponentReplacing;
import com.loohp.interactivechat.utils.ComponentStyling;
import com.loohp.interactivechat.utils.CustomStringUtils;
import com.loohp.interactivechat.utils.HashUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.InventoryUtils;
import com.loohp.interactivechat.utils.ItemStackUtils;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.PlaceholderParser;
import com.loohp.interactivechat.utils.PlayerUtils;
import com.loohp.interactivechat.utils.SkinUtils;
import com.loohp.interactivechat.utils.VanishUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.api.events.InteractiveChatDiscordSRVConfigReloadEvent;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayType;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.InteractionHandler;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.CustomItemTextureRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackInfo;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordContentUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.ResourcePackInfoUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.TranslationKeyUtils;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.TitledInventoryWrapper;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.MessageEmbed;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionMapping;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.SubcommandData;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.WebhookMessageUpdateAction;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DiscordCommands implements Listener, SlashCommandProvider {

    public static final String CUSTOM_CHANNEL = "icdsrva:discord_commands";
    public static final String RESOURCEPACK_LABEL = "resourcepack";
    public static final String PLAYERLIST_LABEL = "playerlist";
    public static final String ITEM_LABEL = "item";
    public static final String ITEM_OTHER_LABEL = "itemasuser";
    public static final String INVENTORY_LABEL = "inv";
    public static final String INVENTORY_OTHER_LABEL = "invasuser";
    public static final String ENDERCHEST_LABEL = "ender";
    public static final String ENDERCHEST_OTHER_LABEL = "enderasuser";

    public static final Set<String> DISCORD_COMMANDS;

    static {
        Set<String> discordCommands = new HashSet<>();
        for (Field field : DiscordCommands.class.getFields()) {
            if (field.getType().equals(String.class) && field.getName().endsWith("_LABEL")) {
                try {
                    discordCommands.add((String) field.get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        DISCORD_COMMANDS = Collections.unmodifiableSet(discordCommands);
    }

    private static void layout0(OfflineICPlayer player, String sha1, String title) throws Exception {
        Inventory inv = Bukkit.createInventory(null, 54, title);
        int f1 = 0;
        int f2 = 0;
        int u = 45;
        for (int j = 0; j < Math.min(player.getInventory().getSize(), 45); j++) {
            ItemStack item = player.getInventory().getItem(j);
            if (item != null && !item.getType().equals(Material.AIR)) {
                if ((j >= 9 && j < 18) || j >= 36) {
                    if (item.getType().equals(InteractiveChat.invFrame1.getType())) {
                        f1++;
                    } else if (item.getType().equals(InteractiveChat.invFrame2.getType())) {
                        f2++;
                    }
                }
                if (j < 36) {
                    inv.setItem(u, item.clone());
                }
            }
            if (u >= 53) {
                u = 18;
            } else {
                u++;
            }
        }
        ItemStack frame = f1 > f2 ? InteractiveChat.invFrame2.clone() : InteractiveChat.invFrame1.clone();
        ItemMeta frameMeta = frame.getItemMeta();
        frameMeta.setDisplayName(ChatColor.YELLOW + "");
        frame.setItemMeta(frameMeta);
        for (int j = 0; j < 18; j++) {
            inv.setItem(j, frame);
        }

        int level = player.getExperienceLevel();
        ItemStack exp = XMaterial.EXPERIENCE_BOTTLE.parseItem();
        if (InteractiveChat.version.isNewerThan(MCVersion.V1_15)) {
            TranslatableComponent expText = Component.translatable(InventoryDisplay.getLevelTranslation(level)).color(NamedTextColor.YELLOW).decorate(TextDecoration.ITALIC);
            if (level != 1) {
                expText = expText.args(Component.text(level + ""));
            }
            exp = NBTEditor.set(exp, InteractiveChatComponentSerializer.gson().serialize(expText), "display", "Name");
        } else {
            ItemMeta expMeta = exp.getItemMeta();
            expMeta.setDisplayName(ChatColor.YELLOW + LanguageUtils.getTranslation(InventoryDisplay.getLevelTranslation(level), InteractiveChat.language).replaceFirst("%s|%d", level + ""));
            exp.setItemMeta(expMeta);
        }
        inv.setItem(1, exp);

        inv.setItem(3, player.getInventory().getItem(39));
        inv.setItem(4, player.getInventory().getItem(38));
        inv.setItem(5, player.getInventory().getItem(37));
        inv.setItem(6, player.getInventory().getItem(36));

        ItemStack offhand = player.getInventory().getSize() > 40 ? player.getInventory().getItem(40) : null;
        if (!InteractiveChat.version.isOld() || (offhand != null && offhand.getType().equals(Material.AIR))) {
            inv.setItem(8, offhand);
        }

        Bukkit.getScheduler().runTaskAsynchronously(InteractiveChat.plugin, () -> {
            ItemStack skull = SkinUtils.getSkull(player.getUniqueId());
            ItemMeta meta = skull.getItemMeta();
            String name = ChatColorUtils.translateAlternateColorCodes('&', InteractiveChatDiscordSrvAddon.plugin.shareInvCommandSkullName.replace("{Player}", player.getName()));
            meta.setDisplayName(name);
            skull.setItemMeta(meta);
            inv.setItem(0, skull);
        });

        InteractiveChatAPI.addInventoryToItemShareList(SharedType.INVENTORY, sha1, inv);

        if (InteractiveChat.bungeecordMode) {
            try {
                long time = System.currentTimeMillis();
                BungeeMessageSender.addInventory(time, SharedType.INVENTORY, sha1, title, inv);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void layout1(OfflineICPlayer player, String sha1, String title) throws Exception {
        int selectedSlot = player.getSelectedSlot();
        int level = player.getExperienceLevel();

        Inventory inv = Bukkit.createInventory(null, 54, title);
        int f1 = 0;
        int f2 = 0;
        for (int j = 0; j < Math.min(player.getInventory().getSize(), 45); j++) {
            if (j == selectedSlot || j >= 36) {
                ItemStack item = player.getInventory().getItem(j);
                if (item != null && !item.getType().equals(Material.AIR)) {
                    if (item.getType().equals(InteractiveChat.invFrame1.getType())) {
                        f1++;
                    } else if (item.getType().equals(InteractiveChat.invFrame2.getType())) {
                        f2++;
                    }
                }
            }
        }
        ItemStack frame = f1 > f2 ? InteractiveChat.invFrame2.clone() : InteractiveChat.invFrame1.clone();
        ItemMeta frameMeta = frame.getItemMeta();
        frameMeta.setDisplayName(ChatColor.YELLOW + "");
        frame.setItemMeta(frameMeta);
        for (int j = 0; j < 54; j++) {
            inv.setItem(j, frame);
        }
        inv.setItem(12, player.getInventory().getItem(39));
        inv.setItem(21, player.getInventory().getItem(38));
        inv.setItem(30, player.getInventory().getItem(37));
        inv.setItem(39, player.getInventory().getItem(36));

        ItemStack offhand = player.getInventory().getSize() > 40 ? player.getInventory().getItem(40) : null;
        if (InteractiveChat.version.isOld() && (offhand == null || offhand.getType().equals(Material.AIR))) {
            inv.setItem(24, player.getInventory().getItem(selectedSlot));
        } else {
            inv.setItem(23, offhand);
            inv.setItem(25, player.getInventory().getItem(selectedSlot));
        }

        ItemStack exp = XMaterial.EXPERIENCE_BOTTLE.parseItem();
        if (InteractiveChat.version.isNewerThan(MCVersion.V1_15)) {
            TranslatableComponent expText = Component.translatable(InventoryDisplay.getLevelTranslation(level)).color(NamedTextColor.YELLOW).decorate(TextDecoration.ITALIC);
            if (level != 1) {
                expText = expText.args(Component.text(level + ""));
            }
            exp = NBTEditor.set(exp, InteractiveChatComponentSerializer.gson().serialize(expText), "display", "Name");
        } else {
            ItemMeta expMeta = exp.getItemMeta();
            expMeta.setDisplayName(ChatColor.YELLOW + LanguageUtils.getTranslation(InventoryDisplay.getLevelTranslation(level), InteractiveChat.language).replaceFirst("%s|%d", level + ""));
            exp.setItemMeta(expMeta);
        }
        inv.setItem(37, exp);

        Inventory inv2 = Bukkit.createInventory(null, 45, title);
        for (int j = 0; j < Math.min(player.getInventory().getSize(), 45); j++) {
            ItemStack item = player.getInventory().getItem(j);
            if (item != null && !item.getType().equals(Material.AIR)) {
                inv2.setItem(j, item.clone());
            }
        }

        Bukkit.getScheduler().runTaskAsynchronously(InteractiveChat.plugin, () -> {
            ItemStack skull = SkinUtils.getSkull(player.getUniqueId());
            ItemMeta meta = skull.getItemMeta();
            String name = ChatColorUtils.translateAlternateColorCodes('&', InteractiveChatDiscordSrvAddon.plugin.shareInvCommandSkullName.replace("{Player}", player.getName()));
            meta.setDisplayName(name);
            skull.setItemMeta(meta);
            inv.setItem(10, skull);
        });

        InteractiveChatAPI.addInventoryToItemShareList(SharedType.INVENTORY1_UPPER, sha1, inv);
        InteractiveChatAPI.addInventoryToItemShareList(SharedType.INVENTORY1_LOWER, sha1, inv2);

        if (InteractiveChat.bungeecordMode) {
            try {
                long time = System.currentTimeMillis();
                BungeeMessageSender.addInventory(time, SharedType.INVENTORY1_UPPER, sha1, title, inv);
                BungeeMessageSender.addInventory(time, SharedType.INVENTORY1_LOWER, sha1, title, inv2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void ender(OfflineICPlayer player, String sha1, String title) throws Exception {
        int size = player.getEnderChest().getSize();
        Inventory inv = Bukkit.createInventory(null, InventoryUtils.toMultipleOf9(size), title);
        for (int j = 0; j < size; j++) {
            if (player.getEnderChest().getItem(j) != null) {
                if (!player.getEnderChest().getItem(j).getType().equals(Material.AIR)) {
                    inv.setItem(j, player.getEnderChest().getItem(j).clone());
                }
            }
        }

        InteractiveChatAPI.addInventoryToItemShareList(SharedType.ENDERCHEST, sha1, inv);

        if (InteractiveChat.bungeecordMode) {
            try {
                long time = System.currentTimeMillis();
                BungeeMessageSender.addInventory(time, SharedType.ENDERCHEST, sha1, title, inv);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static ItemStack resolveItemStack(SlashCommandEvent event, OfflineICPlayer player) {
        String subCommand = event.getSubcommandName();
        switch (subCommand) {
            case "mainhand":
                return player.getMainHandItem();
            case "offhand":
                return player.getOffHandItem();
            case "hotbar":
            case "inventory":
                return player.getInventory().getItem((int) event.getOptions().get(0).getAsLong() - 1);
            case "armor":
                return player.getEquipment().getItem(EquipmentSlot.valueOf(event.getOptions().get(0).getAsString().toUpperCase()));
            case "ender":
                return player.getEnderChest().getItem((int) event.getOptions().get(0).getAsLong() - 1);
        }
        return null;
    }

    private static List<String> getPlayerGroups(OfflinePlayer player) {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServicesManager().getRegistration(Permission.class);
        return Arrays.asList(rsp.getProvider().getPlayerGroups(Bukkit.getWorlds().get(0).getName(), player));
    }

    @SuppressWarnings("deprecation")
    private static List<ValueTrios<UUID, Component, Integer>> sortPlayers(List<String> orderTypes, List<ValueTrios<UUID, Component, Integer>> players, Map<UUID, ValuePairs<List<String>, String>> playerInfo) {
        if (players.size() <= 1) {
            return players;
        }
        Comparator<ValueTrios<UUID, Component, Integer>> comparator = Comparator.comparing(each -> 0);
        for (String str : orderTypes) {
            String[] sections = str.split(":", 2);
            switch (sections[0].toUpperCase()) {
                case "GROUP":
                    if (sections.length > 1) {
                        List<String> groupOrder = Arrays.stream(sections[1].split(",")).map(each -> each.trim()).collect(Collectors.toList());
                        comparator = comparator.thenComparing(each -> {
                            ValuePairs<List<String>, String> info = playerInfo.get(each.getFirst());
                            if (info == null) {
                                return Integer.MAX_VALUE;
                            }
                            return info.getFirst().stream().mapToInt(e -> groupOrder.indexOf(e)).max().orElse(Integer.MAX_VALUE - 1);
                        });
                    }
                    break;
                case "PLAYERNAME":
                    comparator = comparator.thenComparing(each -> {
                        ValuePairs<List<String>, String> info = playerInfo.get(each.getFirst());
                        if (info == null) {
                            return "";
                        }
                        return info.getSecond();
                    });
                    break;
                case "PLAYERNAME_REVERSE":
                    comparator = comparator.thenComparing(Comparator.comparing(each -> {
                        ValuePairs<List<String>, String> info = playerInfo.get(((ValueTrios<UUID, Component, Integer>) each).getFirst());
                        if (info == null) {
                            return "";
                        }
                        return info.getSecond();
                    }).reversed());
                    break;
                case "PLACEHOLDER":
                    if (sections.length > 1) {
                        String placeholder = sections[1];
                        comparator = comparator.thenComparing(Comparator.comparing(each -> {
                            String parsedString = PlaceholderParser.parse(ICPlayerFactory.getUnsafe().getOfflineICPPlayerWithoutInitialization(((ValueTrios<UUID, Component, Integer>) each).getFirst()), placeholder);
                            double value = Double.MAX_VALUE;
                            try {
                                value = Double.parseDouble(parsedString);
                            } catch (NumberFormatException ignore) {
                            }
                            return value;
                        }).thenComparing(each -> {
                            return PlaceholderParser.parse(ICPlayerFactory.getUnsafe().getOfflineICPPlayerWithoutInitialization(((ValueTrios<UUID, Component, Integer>) each).getFirst()), placeholder);
                        }));
                    }
                    break;
                case "PLACEHOLDER_REVERSE":
                    if (sections.length > 1) {
                        String placeholder = sections[1];
                        comparator = comparator.thenComparing(Comparator.comparing(each -> {
                            String parsedString = PlaceholderParser.parse(ICPlayerFactory.getUnsafe().getOfflineICPPlayerWithoutInitialization(((ValueTrios<UUID, Component, Integer>) each).getFirst()), placeholder);
                            double value = Double.MAX_VALUE;
                            try {
                                value = Double.parseDouble(parsedString);
                            } catch (NumberFormatException ignore) {
                            }
                            return value;
                        }).thenComparing(each -> {
                            return PlaceholderParser.parse(ICPlayerFactory.getUnsafe().getOfflineICPPlayerWithoutInitialization(((ValueTrios<UUID, Component, Integer>) each).getFirst()), placeholder);
                        }).reversed());
                    }
                    break;
                default:
                    break;
            }
        }
        comparator = comparator.thenComparing(each -> PlainTextComponentSerializer.plainText().serialize(each.getSecond())).thenComparing(each -> each.getFirst());
        players.sort(comparator);
        return players;
    }

    private DiscordSRV discordsrv;
    private Map<String, Component> components;

    public DiscordCommands(DiscordSRV discordsrv) {
        this.discordsrv = discordsrv;
        this.components = new ConcurrentHashMap<>();
    }

    public void init() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
            if (InteractiveChat.bungeecordMode) {
                if (InteractiveChatDiscordSrvAddon.plugin.playerlistCommandEnabled && InteractiveChatDiscordSrvAddon.plugin.playerlistCommandIsMainServer) {
                    for (ICPlayer player : ICPlayerFactory.getOnlineICPlayers()) {
                        if (!player.isLocal()) {
                            StringBuilder text = new StringBuilder(InteractiveChatDiscordSrvAddon.plugin.playerlistCommandPlayerFormat +
                                    " " + InteractiveChatDiscordSrvAddon.plugin.playerlistCommandHeader +
                                    " " + InteractiveChatDiscordSrvAddon.plugin.playerlistCommandFooter);
                            for (String type : InteractiveChatDiscordSrvAddon.plugin.playerlistOrderingTypes) {
                                if (type.startsWith("PLACEHOLDER") && type.contains(":")) {
                                    String placeholder = type.split(":")[1];
                                    text.append(" ").append(placeholder);
                                }
                            }
                            try {
                                BungeeMessageSender.requestParsedPlaceholders(System.currentTimeMillis(), player.getUniqueId(), text.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }, 100, 100);
    }

    @EventHandler
    public void onConfigReload(InteractiveChatDiscordSRVConfigReloadEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> reload());
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        Guild guild = discordsrv.getMainGuild();

        String memberLabel = InteractiveChatDiscordSrvAddon.plugin.discordMemberLabel;
        String memberDescription = InteractiveChatDiscordSrvAddon.plugin.discordMemberDescription;
        String slotLabel = InteractiveChatDiscordSrvAddon.plugin.discordSlotLabel;
        String slotDescription = InteractiveChatDiscordSrvAddon.plugin.discordSlotDescription;

        List<CommandData> commandDataList = new ArrayList<>();

        if (InteractiveChatDiscordSrvAddon.plugin.resourcepackCommandIsMainServer) {
            if (InteractiveChatDiscordSrvAddon.plugin.resourcepackCommandEnabled) {
                commandDataList.add(new CommandData(RESOURCEPACK_LABEL, ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.resourcepackCommandDescription)));
            }
        }
        if (InteractiveChatDiscordSrvAddon.plugin.playerlistCommandIsMainServer) {
            if (InteractiveChatDiscordSrvAddon.plugin.playerlistCommandEnabled) {
                commandDataList.add(new CommandData(PLAYERLIST_LABEL, ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.playerlistCommandDescription)));
            }
        }
        if (InteractiveChatDiscordSrvAddon.plugin.shareItemCommandIsMainServer) {
            Optional<ICPlaceholder> optItemPlaceholder = InteractiveChat.placeholderList.values().stream().filter(each -> each.getKeyword().equals(InteractiveChat.itemPlaceholder)).findFirst();
            if (InteractiveChatDiscordSrvAddon.plugin.shareItemCommandEnabled && optItemPlaceholder.isPresent()) {
                String itemDescription = ChatColorUtils.stripColor(optItemPlaceholder.get().getDescription());

                SubcommandData mainhandSubcommand = new SubcommandData("mainhand", itemDescription);
                SubcommandData offhandSubcommand = new SubcommandData("offhand", itemDescription);
                SubcommandData hotbarSubcommand = new SubcommandData("hotbar", itemDescription).addOptions(new OptionData(OptionType.INTEGER, slotLabel, slotDescription, true).setRequiredRange(1, 9));
                SubcommandData inventorySubcommand = new SubcommandData("inventory", itemDescription).addOptions(new OptionData(OptionType.INTEGER, slotLabel, slotDescription, true).setRequiredRange(1, 41));
                SubcommandData armorSubcommand = new SubcommandData("armor", itemDescription).addOptions(new OptionData(OptionType.STRING, slotLabel, slotDescription, true).addChoice("head", "head").addChoice("chest", "chest").addChoice("legs", "legs").addChoice("feet", "feet"));
                SubcommandData enderSubcommand = new SubcommandData("ender", itemDescription).addOptions(new OptionData(OptionType.INTEGER, slotLabel, slotDescription, true).setRequiredRange(1, 27));

                commandDataList.add(new CommandData(ITEM_LABEL, ChatColorUtils.stripColor(optItemPlaceholder.get().getDescription())).addSubcommands(mainhandSubcommand).addSubcommands(offhandSubcommand).addSubcommands(hotbarSubcommand).addSubcommands(inventorySubcommand).addSubcommands(armorSubcommand).addSubcommands(enderSubcommand));

                if (InteractiveChatDiscordSrvAddon.plugin.shareItemCommandAsOthers) {
                    SubcommandData mainhandOtherSubcommand = new SubcommandData("mainhand", itemDescription).addOption(OptionType.USER, memberLabel, memberDescription, true);
                    SubcommandData offhandOtherSubcommand = new SubcommandData("offhand", itemDescription).addOption(OptionType.USER, memberLabel, memberDescription, true);
                    SubcommandData hotbarOtherSubcommand = new SubcommandData("hotbar", itemDescription).addOptions(new OptionData(OptionType.INTEGER, slotLabel, slotDescription, true).setRequiredRange(1, 9)).addOption(OptionType.USER, memberLabel, memberDescription, true);
                    SubcommandData inventoryOtherSubcommand = new SubcommandData("inventory", itemDescription).addOptions(new OptionData(OptionType.INTEGER, slotLabel, slotDescription, true).setRequiredRange(1, 41)).addOption(OptionType.USER, memberLabel, memberDescription, true);
                    SubcommandData armorOtherSubcommand = new SubcommandData("armor", itemDescription).addOptions(new OptionData(OptionType.STRING, slotLabel, slotDescription, true).addChoice("head", "head").addChoice("chest", "chest").addChoice("legs", "legs").addChoice("feet", "feet")).addOption(OptionType.USER, memberLabel, memberDescription, true);
                    SubcommandData enderOtherSubcommand = new SubcommandData("ender", itemDescription).addOptions(new OptionData(OptionType.INTEGER, slotLabel, slotDescription, true).setRequiredRange(1, 27)).addOption(OptionType.USER, memberLabel, memberDescription, true);

                    commandDataList.add(new CommandData(ITEM_OTHER_LABEL, ChatColorUtils.stripColor(optItemPlaceholder.get().getDescription())).addSubcommands(mainhandOtherSubcommand).addSubcommands(offhandOtherSubcommand).addSubcommands(hotbarOtherSubcommand).addSubcommands(inventoryOtherSubcommand).addSubcommands(armorOtherSubcommand).addSubcommands(enderOtherSubcommand));
                }
            }
        }
        if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandIsMainServer) {
            Optional<ICPlaceholder> optInvPlaceholder = InteractiveChat.placeholderList.values().stream().filter(each -> each.getKeyword().equals(InteractiveChat.invPlaceholder)).findFirst();
            if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandEnabled && optInvPlaceholder.isPresent()) {
                commandDataList.add(new CommandData(INVENTORY_LABEL, ChatColorUtils.stripColor(optInvPlaceholder.get().getDescription())));

                if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandAsOthers) {
                    commandDataList.add(new CommandData(INVENTORY_OTHER_LABEL, ChatColorUtils.stripColor(optInvPlaceholder.get().getDescription())).addOption(OptionType.USER, memberLabel, memberDescription, true));
                }
            }
        }
        if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandIsMainServer) {
            Optional<ICPlaceholder> optEnderPlaceholder = InteractiveChat.placeholderList.values().stream().filter(each -> each.getKeyword().equals(InteractiveChat.enderPlaceholder)).findFirst();
            if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandEnabled && optEnderPlaceholder.isPresent()) {
                commandDataList.add(new CommandData(ENDERCHEST_LABEL, ChatColorUtils.stripColor(optEnderPlaceholder.get().getDescription())));

                if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandAsOthers) {
                    commandDataList.add(new CommandData(ENDERCHEST_OTHER_LABEL, ChatColorUtils.stripColor(optEnderPlaceholder.get().getDescription())).addOption(OptionType.USER, memberLabel, memberDescription, true));
                }
            }
        }

        return commandDataList.stream().map(each -> new PluginSlashCommand(InteractiveChatDiscordSrvAddon.plugin, each, guild.getId())).collect(Collectors.toSet());
    }

    public void reload() {
        DiscordSRV.api.updateSlashCommands();
    }

    @SlashCommand(path = "*")
    public void onSlashCommand(SlashCommandEvent event) {
        Guild guild = discordsrv.getMainGuild();
        if (event.getGuild().getIdLong() != guild.getIdLong()) {
            return;
        }
        if (!(event.getChannel() instanceof TextChannel)) {
            return;
        }
        TextChannel channel = (TextChannel) event.getChannel();
        String label = event.getName();
        if (InteractiveChatDiscordSrvAddon.plugin.resourcepackCommandEnabled && label.equalsIgnoreCase(RESOURCEPACK_LABEL)) {
            if (InteractiveChatDiscordSrvAddon.plugin.resourcepackCommandIsMainServer) {
                event.deferReply().setEphemeral(true).queue();
                List<MessageEmbed> messageEmbeds = new ArrayList<>();
                Map<String, byte[]> attachments = new HashMap<>();
                int i = 0;
                for (ResourcePackInfo packInfo : InteractiveChatDiscordSrvAddon.plugin.resourceManager.getResourcePackInfo()) {
                    i++;
                    String packName = ResourcePackInfoUtils.resolveName(packInfo);
                    Component description = ComponentStringUtils.resolve(ComponentModernizing.modernize(ResourcePackInfoUtils.resolveDescription(packInfo)), InteractiveChatDiscordSrvAddon.plugin.resourceManager.getLanguageManager().getTranslateFunction().ofLanguage(InteractiveChatDiscordSrvAddon.plugin.language));
                    EmbedBuilder builder = new EmbedBuilder().setAuthor(packName).setThumbnail("attachment://" + i + ".png");
                    if (packInfo.getStatus()) {
                        builder.setDescription(PlainTextComponentSerializer.plainText().serialize(description));
                        ChatColor firstColor = ChatColorUtils.getColor(LegacyComponentSerializer.builder().useUnusualXRepeatedCharacterHexFormat().character(ChatColorUtils.COLOR_CHAR).build().serialize(description));
                        if (firstColor == null) {
                            firstColor = ChatColor.WHITE;
                        }
                        Color color = ColorUtils.getColor(firstColor);
                        if (color == null) {
                            color = new Color(0xAAAAAA);
                        } else if (color.equals(Color.WHITE)) {
                            color = DiscordContentUtils.OFFSET_WHITE;
                        }
                        builder.setColor(color);
                        if (packInfo.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) > 0) {
                            builder.setFooter(LanguageUtils.getTranslation(TranslationKeyUtils.getNewIncompatiblePack(), InteractiveChatDiscordSrvAddon.plugin.language));
                        } else if (packInfo.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) < 0) {
                            builder.setFooter(LanguageUtils.getTranslation(TranslationKeyUtils.getOldIncompatiblePack(), InteractiveChatDiscordSrvAddon.plugin.language));
                        }
                    } else {
                        builder.setColor(0xFF0000).setDescription(packInfo.getRejectedReason());
                    }
                    messageEmbeds.add(builder.build());
                    try {
                        attachments.put(i + ".png", ImageUtils.toArray(ImageUtils.resizeImageAbs(packInfo.getIcon(), 128, 128)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                WebhookMessageUpdateAction<Message> action = event.getHook().setEphemeral(true).editOriginal("**" + LanguageUtils.getTranslation(TranslationKeyUtils.getServerResourcePack(), InteractiveChatDiscordSrvAddon.plugin.language) + "**").setEmbeds(messageEmbeds);
                for (Entry<String, byte[]> entry : attachments.entrySet()) {
                    action = action.addFile(entry.getValue(), entry.getKey());
                }
                action.queue();
            }
        } else if (InteractiveChatDiscordSrvAddon.plugin.playerlistCommandEnabled && label.equalsIgnoreCase(PLAYERLIST_LABEL)) {
            if (InteractiveChatDiscordSrvAddon.plugin.playerlistCommandIsMainServer) {
                String minecraftChannel = discordsrv.getChannels().entrySet().stream().filter(entry -> channel.getId().equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
                if (minecraftChannel == null) {
                    if (InteractiveChatDiscordSrvAddon.plugin.respondToCommandsInInvalidChannels) {
                        event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.invalidDiscordChannel)).setEphemeral(true).queue();
                    }
                    return;
                }
                AtomicBoolean deleted = new AtomicBoolean(false);
                event.deferReply().queue(hook -> {
                    if (InteractiveChatDiscordSrvAddon.plugin.playerlistCommandDeleteAfter > 0) {
                        Bukkit.getScheduler().runTaskLaterAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
                            if (!deleted.get()) {
                                hook.deleteOriginal().queue();
                            }
                        }, InteractiveChatDiscordSrvAddon.plugin.playerlistCommandDeleteAfter * 20L);
                    }
                });
                Map<OfflinePlayer, Integer> players;
                if (InteractiveChat.bungeecordMode && InteractiveChatDiscordSrvAddon.plugin.playerlistCommandBungeecord && !Bukkit.getOnlinePlayers().isEmpty()) {
                    try {
                        List<ValueTrios<UUID, String, Integer>> bungeePlayers = InteractiveChatAPI.getBungeecordPlayerList().get();
                        players = new LinkedHashMap<>(bungeePlayers.size());
                        for (ValueTrios<UUID, String, Integer> playerinfo : bungeePlayers) {
                            UUID uuid = playerinfo.getFirst();
                            if (!VanishUtils.isVanished(uuid)) {
                                if (!InteractiveChatDiscordSrvAddon.plugin.playerlistCommandOnlyInteractiveChatServers || ICPlayerFactory.getICPlayer(uuid) != null) {
                                    players.put(Bukkit.getOfflinePlayer(uuid), playerinfo.getThird());
                                }
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        event.getHook().editOriginal(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.unableToRetrieveData) + " (-1)").queue();
                        return;
                    }
                } else {
                    players = Bukkit.getOnlinePlayers().stream().filter(each -> !VanishUtils.isVanished(each.getUniqueId())).collect(Collectors.toMap(each -> each, each -> PlayerUtils.getPing(each)));
                }
                if (players.isEmpty()) {
                    event.getHook().editOriginal(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.playerlistCommandEmptyServer)).queue();
                } else {
                    int errorCode = -2;
                    try {
                        List<ValueTrios<UUID, Component, Integer>> player = new ArrayList<>();
                        Map<UUID, ValuePairs<List<String>, String>> playerInfo = new HashMap<>();
                        for (Entry<OfflinePlayer, Integer> entry : players.entrySet()) {
                            OfflinePlayer bukkitOfflinePlayer = entry.getKey();
                            @SuppressWarnings("deprecation")
                            OfflineICPlayer offlinePlayer = ICPlayerFactory.getUnsafe().getOfflineICPPlayerWithoutInitialization(bukkitOfflinePlayer.getUniqueId());
                            playerInfo.put(offlinePlayer.getUniqueId(), new ValuePairs<>(getPlayerGroups(bukkitOfflinePlayer), offlinePlayer.getName()));
                            String name = ChatColorUtils.translateAlternateColorCodes('&', PlaceholderParser.parse(offlinePlayer, InteractiveChatDiscordSrvAddon.plugin.playerlistCommandPlayerFormat));
                            Component nameComponent;
                            if (InteractiveChatDiscordSrvAddon.plugin.playerlistCommandParsePlayerNamesWithMiniMessage) {
                                nameComponent = MiniMessage.miniMessage().deserialize(name);
                            } else {
                                nameComponent = LegacyComponentSerializer.legacySection().deserialize(name);
                            }
                            player.add(new ValueTrios<>(offlinePlayer.getUniqueId(), nameComponent, entry.getValue()));
                        }
                        errorCode--;
                        sortPlayers(InteractiveChatDiscordSrvAddon.plugin.playerlistOrderingTypes, player, playerInfo);
                        errorCode--;
                        @SuppressWarnings("deprecation")
                        OfflineICPlayer firstPlayer = ICPlayerFactory.getUnsafe().getOfflineICPPlayerWithoutInitialization(players.keySet().iterator().next().getUniqueId());
                        List<Component> header = new ArrayList<>();
                        if (!InteractiveChatDiscordSrvAddon.plugin.playerlistCommandHeader.isEmpty()) {
                            header = ComponentStyling.splitAtLineBreaks(LegacyComponentSerializer.legacySection().deserialize(ChatColorUtils.translateAlternateColorCodes('&', PlaceholderParser.parse(firstPlayer, InteractiveChatDiscordSrvAddon.plugin.playerlistCommandHeader.replace("{OnlinePlayers}", players.size() + "")))));
                        }
                        errorCode--;
                        List<Component> footer = new ArrayList<>();
                        if (!InteractiveChatDiscordSrvAddon.plugin.playerlistCommandFooter.isEmpty()) {
                            footer = ComponentStyling.splitAtLineBreaks(LegacyComponentSerializer.legacySection().deserialize(ChatColorUtils.translateAlternateColorCodes('&', PlaceholderParser.parse(firstPlayer, InteractiveChatDiscordSrvAddon.plugin.playerlistCommandFooter.replace("{OnlinePlayers}", players.size() + "")))));
                        }
                        errorCode--;
                        int playerListMaxPlayers = InteractiveChatDiscordSrvAddon.plugin.playerlistMaxPlayers;
                        if (playerListMaxPlayers < 1) {
                            playerListMaxPlayers = Integer.MAX_VALUE;
                        }
                        BufferedImage image = ImageGeneration.getTabListImage(header, footer, player, InteractiveChatDiscordSrvAddon.plugin.playerlistCommandAvatar, InteractiveChatDiscordSrvAddon.plugin.playerlistCommandPing, playerListMaxPlayers);
                        errorCode--;
                        byte[] data = ImageUtils.toArray(image);
                        errorCode--;
                        event.getHook().editOriginalEmbeds(new EmbedBuilder().setImage("attachment://Tablist.png").setColor(InteractiveChatDiscordSrvAddon.plugin.playerlistCommandColor).build()).addFile(data, "Tablist.png").queue(success -> {
                            if (InteractiveChatDiscordSrvAddon.plugin.playerlistCommandDeleteAfter > 0) {
                                deleted.set(true);
                                success.delete().queueAfter(InteractiveChatDiscordSrvAddon.plugin.playerlistCommandDeleteAfter, TimeUnit.SECONDS);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                        event.getHook().editOriginal(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.unableToRetrieveData) + " (" + errorCode + ")").queue();
                        return;
                    }
                }
            }
        } else if (InteractiveChatDiscordSrvAddon.plugin.shareItemCommandEnabled && (label.equalsIgnoreCase(ITEM_LABEL) || label.equalsIgnoreCase(ITEM_OTHER_LABEL))) {
            String minecraftChannel = discordsrv.getChannels().entrySet().stream().filter(entry -> channel.getId().equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
            if (minecraftChannel == null) {
                if (InteractiveChatDiscordSrvAddon.plugin.respondToCommandsInInvalidChannels && InteractiveChatDiscordSrvAddon.plugin.shareInvCommandIsMainServer) {
                    event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.invalidDiscordChannel)).setEphemeral(true).queue();
                }
                return;
            }
            String discordUserId = event.getUser().getId();
            List<OptionMapping> options = event.getOptionsByType(OptionType.USER);
            if (options.size() > 0) {
                discordUserId = options.get(0).getAsUser().getId();
            }
            UUID uuid = discordsrv.getAccountLinkManager().getUuid(discordUserId);
            if (uuid == null) {
                if (InteractiveChatDiscordSrvAddon.plugin.shareItemCommandIsMainServer) {
                    event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.accountNotLinked)).setEphemeral(true).queue();
                }
                return;
            }
            int errorCode = -1;
            try {
                OfflineICPlayer offlineICPlayer = ICPlayerFactory.getOfflineICPlayer(uuid);
                if (offlineICPlayer == null) {
                    if (InteractiveChatDiscordSrvAddon.plugin.shareItemCommandIsMainServer) {
                        event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.unableToRetrieveData) + " (" + errorCode + ")").setEphemeral(true).queue();
                    }
                    return;
                }
                errorCode--;
                if (InteractiveChatDiscordSrvAddon.plugin.shareItemCommandIsMainServer) {
                    event.deferReply().queue();
                }
                errorCode--;
                ICPlayer icplayer = offlineICPlayer.getPlayer();
                if (InteractiveChat.bungeecordMode && icplayer != null) {
                    if (icplayer.isLocal()) {
                        ItemStack[] equipment;
                        if (InteractiveChat.version.isOld()) {
                            //noinspection deprecation
                            equipment = new ItemStack[] {icplayer.getEquipment().getHelmet(), icplayer.getEquipment().getChestplate(), icplayer.getEquipment().getLeggings(), icplayer.getEquipment().getBoots(), icplayer.getEquipment().getItemInHand()};
                        } else {
                            equipment = new ItemStack[] {icplayer.getEquipment().getHelmet(), icplayer.getEquipment().getChestplate(), icplayer.getEquipment().getLeggings(), icplayer.getEquipment().getBoots(), icplayer.getEquipment().getItemInMainHand(), icplayer.getEquipment().getItemInOffHand()};
                        }
                        try {
                            BungeeMessageSender.forwardEquipment(System.currentTimeMillis(), icplayer.getUniqueId(), icplayer.isRightHanded(), icplayer.getSelectedSlot(), icplayer.getExperienceLevel(), equipment);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        TimeUnit.MILLISECONDS.sleep(InteractiveChat.remoteDelay);
                    }
                }
                errorCode--;
                ItemStack itemStack = resolveItemStack(event, offlineICPlayer);
                if (itemStack == null) {
                    itemStack = new ItemStack(Material.AIR);
                }
                errorCode--;
                BufferedImage itemImage = ImageGeneration.getItemStackImage(itemStack, offlineICPlayer, true);
                errorCode--;
                String title = ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.shareItemCommandTitle.replace("{Player}", offlineICPlayer.getName()));
                errorCode--;
                Component itemTag = ItemDisplay.createItemDisplay(offlineICPlayer, itemStack, title, true, null, false);
                Component resolvedItemTag = ComponentStringUtils.resolve(ComponentModernizing.modernize(itemTag), InteractiveChatDiscordSrvAddon.plugin.resourceManager.getLanguageManager().getTranslateFunction().ofLanguage(InteractiveChatDiscordSrvAddon.plugin.language));
                Component component = LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.shareItemCommandInGameMessageText.replace("{Player}", offlineICPlayer.getName())).replaceText(TextReplacementConfig.builder().matchLiteral("{ItemTag}").replacement(itemTag).build());
                Component resolvedComponent = LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.shareItemCommandInGameMessageText.replace("{Player}", offlineICPlayer.getName())).replaceText(TextReplacementConfig.builder().matchLiteral("{ItemTag}").replacement(resolvedItemTag).build());
                errorCode--;
                String key = "<DiscordShare=" + UUID.randomUUID() + ">";
                components.put(key, component);
                Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> components.remove(key), 100);
                errorCode--;
                discordsrv.broadcastMessageToMinecraftServer(minecraftChannel, ComponentStringUtils.toDiscordSRVComponent(Component.text(key)), event.getUser());
                if (InteractiveChatDiscordSrvAddon.plugin.shareItemCommandIsMainServer) {
                    errorCode--;

                    Inventory inv = null;
                    if (itemStack.hasItemMeta() && itemStack.getItemMeta() instanceof BlockStateMeta) {
                        BlockState bsm = ((BlockStateMeta) itemStack.getItemMeta()).getBlockState();
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

                    ImageDisplayData data;
                    if (inv != null) {
                        data = new ImageDisplayData(offlineICPlayer, 0, title, ImageDisplayType.ITEM_CONTAINER, itemStack.clone(), new TitledInventoryWrapper(ItemStackUtils.getDisplayName(itemStack, null), inv));
                    } else {
                        data = new ImageDisplayData(offlineICPlayer, 0, title, ImageDisplayType.ITEM, itemStack.clone());
                    }
                    ValuePairs<List<DiscordMessageContent>, InteractionHandler> pair = DiscordContentUtils.createContents(Collections.singletonList(data), offlineICPlayer);
                    List<DiscordMessageContent> contents = pair.getFirst();
                    InteractionHandler interactionHandler = pair.getSecond();
                    errorCode--;

                    WebhookMessageUpdateAction<Message> action = event.getHook().editOriginal(ComponentStringUtils.stripColorAndConvertMagic(LegacyComponentSerializer.legacySection().serialize(resolvedComponent)));
                    List<MessageEmbed> embeds = new ArrayList<>();
                    int i = 0;
                    for (DiscordMessageContent content : contents) {
                        i += content.getAttachments().size();
                        if (i <= 10) {
                            ValuePairs<List<MessageEmbed>, Set<String>> valuePair = content.toJDAMessageEmbeds();
                            embeds.addAll(valuePair.getFirst());
                            for (Entry<String, byte[]> attachment : content.getAttachments().entrySet()) {
                                if (valuePair.getSecond().contains(attachment.getKey())) {
                                    action = action.addFile(attachment.getValue(), attachment.getKey());
                                }
                            }
                        }
                    }
                    action.setEmbeds(embeds).setActionRows(interactionHandler.getInteractionToRegister()).queue(message -> {
                        if (!interactionHandler.getInteractions().isEmpty()) {
                            DiscordInteractionEvents.register(message, interactionHandler, contents);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                event.getHook().editOriginal(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.unableToRetrieveData) + " (" + errorCode + ")").queue();
                return;
            }
        } else if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandEnabled && (label.equalsIgnoreCase(INVENTORY_LABEL) || label.equalsIgnoreCase(INVENTORY_OTHER_LABEL))) {
            String minecraftChannel = discordsrv.getChannels().entrySet().stream().filter(entry -> channel.getId().equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
            if (minecraftChannel == null) {
                if (InteractiveChatDiscordSrvAddon.plugin.respondToCommandsInInvalidChannels && InteractiveChatDiscordSrvAddon.plugin.shareInvCommandIsMainServer) {
                    event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.invalidDiscordChannel)).setEphemeral(true).queue();
                }
                return;
            }
            String discordUserId = event.getUser().getId();
            List<OptionMapping> options = event.getOptionsByType(OptionType.USER);
            if (options.size() > 0) {
                discordUserId = options.get(0).getAsUser().getId();
            }
            UUID uuid = discordsrv.getAccountLinkManager().getUuid(discordUserId);
            if (uuid == null) {
                if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandIsMainServer) {
                    event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.accountNotLinked)).setEphemeral(true).queue();
                }
                return;
            }
            int errorCode = -1;
            try {
                OfflineICPlayer offlineICPlayer = ICPlayerFactory.getOfflineICPlayer(uuid);
                if (offlineICPlayer == null) {
                    if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandIsMainServer) {
                        event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.unableToRetrieveData) + " (" + errorCode + ")").setEphemeral(true).queue();
                    }
                    return;
                }
                errorCode--;
                if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandIsMainServer) {
                    event.deferReply().queue();
                }
                errorCode--;
                ICPlayer icplayer = offlineICPlayer.getPlayer();
                if (InteractiveChat.bungeecordMode && icplayer != null) {
                    if (icplayer.isLocal()) {
                        BungeeMessageSender.forwardInventory(System.currentTimeMillis(), uuid, icplayer.isRightHanded(), icplayer.getSelectedSlot(), icplayer.getExperienceLevel(), null, icplayer.getInventory());
                    } else {
                        TimeUnit.MILLISECONDS.sleep(InteractiveChat.remoteDelay);
                    }
                }
                errorCode--;
                BufferedImage image = InteractiveChatDiscordSrvAddon.plugin.usePlayerInvView ? ImageGeneration.getPlayerInventoryImage(offlineICPlayer.getInventory(), offlineICPlayer) : ImageGeneration.getInventoryImage(offlineICPlayer.getInventory(), Component.translatable(TranslationKeyUtils.getDefaultContainerTitle()), offlineICPlayer);
                errorCode--;
                Component component = LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.shareInvCommandInGameMessageText.replace("{Player}", offlineICPlayer.getName()));
                errorCode--;
                String title = ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.shareInvCommandTitle.replace("{Player}", offlineICPlayer.getName()));
                errorCode--;
                String sha1 = HashUtils.createSha1(true, offlineICPlayer.getSelectedSlot(), offlineICPlayer.getExperienceLevel(), title, offlineICPlayer.getInventory());
                errorCode--;
                layout0(offlineICPlayer, sha1, title);
                errorCode--;
                layout1(offlineICPlayer, sha1, title);
                errorCode--;
                component = component.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.shareInvCommandInGameMessageHover)));
                component = component.clickEvent(ClickEvent.runCommand("/interactivechat viewinv " + sha1));
                errorCode--;
                String key = "<DiscordShare=" + UUID.randomUUID() + ">";
                components.put(key, component);
                Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> components.remove(key), 100);
                errorCode--;
                discordsrv.broadcastMessageToMinecraftServer(minecraftChannel, ComponentStringUtils.toDiscordSRVComponent(Component.text(key)), event.getUser());
                if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandIsMainServer) {
                    errorCode--;
                    EmbedBuilder embedBuilder = new EmbedBuilder().setAuthor(title).setImage("attachment://Inventory.png").setColor(InteractiveChatDiscordSrvAddon.plugin.invColor);
                    WebhookMessageUpdateAction<Message> action = event.getHook().editOriginal(ComponentStringUtils.stripColorAndConvertMagic(LegacyComponentSerializer.legacySection().serialize(component)));
                    errorCode--;
                    byte[] data = ImageUtils.toArray(image);
                    action.addFile(data, "Inventory.png");
                    errorCode--;
                    if (InteractiveChatDiscordSrvAddon.plugin.invShowLevel) {
                        int level = offlineICPlayer.getExperienceLevel();
                        byte[] bottleData = ImageUtils.toArray(InteractiveChatDiscordSrvAddon.plugin.modelRenderer.render(32, 32, InteractiveChatDiscordSrvAddon.plugin.resourceManager, InteractiveChatDiscordSrvAddon.plugin.resourceManager.getResourceRegistry(CustomItemTextureRegistry.IDENTIFIER, CustomItemTextureRegistry.class).getItemPostResolveFunction("minecraft:item/experience_bottle", null, XMaterial.EXPERIENCE_BOTTLE.parseItem(), InteractiveChat.version.isOld(), null, null, null, null, InteractiveChatDiscordSrvAddon.plugin.resourceManager.getLanguageManager().getTranslateFunction().ofLanguage(InteractiveChatDiscordSrvAddon.plugin.language)).orElse(null), InteractiveChat.version.isOld(), "minecraft:item/experience_bottle", ModelDisplayPosition.GUI, false, null, null).getImage());
                        embedBuilder.setFooter(ComponentStringUtils.convertFormattedString(LanguageUtils.getTranslation(TranslationKeyUtils.getLevelTranslation(level), InteractiveChatDiscordSrvAddon.plugin.language), level), "attachment://Level.png");
                        action.addFile(bottleData, "Level.png");
                    }
                    errorCode--;
                    action.setEmbeds(embedBuilder.build()).queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
                event.getHook().editOriginal(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.unableToRetrieveData) + " (" + errorCode + ")").queue();
                return;
            }
        } else if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandEnabled && (label.equals(ENDERCHEST_LABEL) || label.equals(ENDERCHEST_OTHER_LABEL))) {
            String minecraftChannel = discordsrv.getChannels().entrySet().stream().filter(entry -> channel.getId().equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
            if (minecraftChannel == null) {
                if (InteractiveChatDiscordSrvAddon.plugin.respondToCommandsInInvalidChannels && InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandIsMainServer) {
                    event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.invalidDiscordChannel)).setEphemeral(true).queue();
                }
                return;
            }
            String discordUserId = event.getUser().getId();
            List<OptionMapping> options = event.getOptionsByType(OptionType.USER);
            if (options.size() > 0) {
                discordUserId = options.get(0).getAsUser().getId();
            }
            UUID uuid = discordsrv.getAccountLinkManager().getUuid(discordUserId);
            if (uuid == null) {
                if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandIsMainServer) {
                    event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.accountNotLinked)).setEphemeral(true).queue();
                }
                return;
            }
            int errorCode = -1;
            try {
                OfflineICPlayer offlineICPlayer = ICPlayerFactory.getOfflineICPlayer(uuid);
                if (offlineICPlayer == null) {
                    if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandIsMainServer) {
                        event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.unableToRetrieveData) + " (" + errorCode + ")").setEphemeral(true).queue();
                    }
                    return;
                }
                errorCode--;
                if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandIsMainServer) {
                    event.deferReply().queue();
                }
                errorCode--;
                ICPlayer icplayer = offlineICPlayer.getPlayer();
                if (InteractiveChat.bungeecordMode && icplayer != null) {
                    if (icplayer.isLocal()) {
                        BungeeMessageSender.forwardEnderchest(System.currentTimeMillis(), uuid, icplayer.isRightHanded(), icplayer.getSelectedSlot(), icplayer.getExperienceLevel(), null, icplayer.getEnderChest());
                    } else {
                        TimeUnit.MILLISECONDS.sleep(InteractiveChat.remoteDelay);
                    }
                }
                errorCode--;
                BufferedImage image = ImageGeneration.getInventoryImage(offlineICPlayer.getEnderChest(), Component.translatable(TranslationKeyUtils.getEnderChestContainerTitle()), offlineICPlayer);
                errorCode--;
                Component component = LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandInGameMessageText.replace("{Player}", offlineICPlayer.getName()));
                errorCode--;
                String title = ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandTitle.replace("{Player}", offlineICPlayer.getName()));
                errorCode--;
                String sha1 = HashUtils.createSha1(true, offlineICPlayer.getSelectedSlot(), offlineICPlayer.getExperienceLevel(), title, offlineICPlayer.getEnderChest());
                errorCode--;
                ender(offlineICPlayer, sha1, title);
                errorCode--;
                component = component.hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacySection().deserialize(InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandInGameMessageHover)));
                component = component.clickEvent(ClickEvent.runCommand("/interactivechat viewender " + sha1));
                errorCode--;
                String key = "<DiscordShare=" + UUID.randomUUID() + ">";
                components.put(key, component);
                Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> components.remove(key), 100);
                errorCode--;
                discordsrv.broadcastMessageToMinecraftServer(minecraftChannel, ComponentStringUtils.toDiscordSRVComponent(Component.text(key)), event.getUser());
                if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandIsMainServer) {
                    errorCode--;
                    byte[] data = ImageUtils.toArray(image);
                    errorCode--;
                    event.getHook().editOriginal(ComponentStringUtils.stripColorAndConvertMagic(LegacyComponentSerializer.legacySection().serialize(component))).setEmbeds(new EmbedBuilder().setAuthor(title).setImage("attachment://Inventory.png").setColor(InteractiveChatDiscordSrvAddon.plugin.enderColor).build()).addFile(data, "Inventory.png").queue();
                }
            } catch (Exception e) {
                e.printStackTrace();
                event.getHook().editOriginal(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.unableToRetrieveData) + " (" + errorCode + ")").queue();
                return;
            }
        }
    }

    @EventHandler
    public void onProcessChat(PostPacketComponentProcessEvent event) {
        Component component = event.getComponent();
        for (Entry<String, Component> entry : components.entrySet()) {
            if (PlainTextComponentSerializer.plainText().serialize(component).contains(entry.getKey())) {
                event.setComponent(ComponentReplacing.replace(component, CustomStringUtils.escapeMetaCharacters(entry.getKey()), false, entry.getValue()));
                break;
            }
        }
    }

    public static class DiscordCommandRegistrationException extends RuntimeException {

        public DiscordCommandRegistrationException(String message) {
            super(message);
        }

        public DiscordCommandRegistrationException(Throwable cause) {
            super(cause);
        }

        public DiscordCommandRegistrationException(String message, Throwable throwable) {
            super(message, throwable);
        }

    }

}
