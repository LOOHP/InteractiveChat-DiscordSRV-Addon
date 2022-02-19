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
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TranslatableComponent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.ClickEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextDecoration;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.modules.InventoryDisplay;
import com.loohp.interactivechat.objectholders.ICPlaceholder;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.ICPlayerFactory;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.objectholders.ValueTrios;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ComponentReplacing;
import com.loohp.interactivechat.utils.ComponentStyling;
import com.loohp.interactivechat.utils.CustomStringUtils;
import com.loohp.interactivechat.utils.HashUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.InventoryUtils;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.PlayerUtils;
import com.loohp.interactivechat.utils.SkinUtils;
import com.loohp.interactivechat.utils.VanishUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.api.events.InteractiveChatDiscordSRVConfigReloadEvent;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.resources.models.ModelDisplay.ModelDisplayPosition;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.JDAUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.TranslationKeyUtils;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorResponseException;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionMapping;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.WebhookMessageUpdateAction;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class DiscordCommands extends ListenerAdapter implements Listener {

    public static final String CUSTOM_CHANNEL = "icdsrva:discord_commands";
    public static final String PLAYERLIST_LABEL = "playerlist";
    public static final String INVENTORY_LABEL = "inv";
    public static final String INVENTORY_OTHER_LABEL = "invasuser";
    public static final String ENDERCHEST_LABEL = "ender";
    public static final String ENDERCHEST_OTHER_LABEL = "enderasuser";

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
            TranslatableComponent expText = (TranslatableComponent) Component.translatable(InventoryDisplay.getLevelTranslation(level)).color(NamedTextColor.YELLOW).decorate(TextDecoration.ITALIC);
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
            TranslatableComponent expText = (TranslatableComponent) Component.translatable(InventoryDisplay.getLevelTranslation(level)).color(NamedTextColor.YELLOW).decorate(TextDecoration.ITALIC);
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

    private DiscordSRV discordsrv;
    private Map<String, Component> components;

    public DiscordCommands(DiscordSRV discordsrv) {
        this.discordsrv = discordsrv;
        this.components = new ConcurrentHashMap<>();
        reload();
    }

    @EventHandler
    public void onConfigReload(InteractiveChatDiscordSRVConfigReloadEvent event) {
        reload();
    }

    public void reload() {
        try {
            Guild guild = discordsrv.getMainGuild();
            String memberLabel = InteractiveChatDiscordSrvAddon.plugin.discordMemberLabel;
            String memberDescription = InteractiveChatDiscordSrvAddon.plugin.discordMemberDescription;
            guild.retrieveCommands().complete().stream().filter(each -> {
                String label = each.getName();
                return label.equals(PLAYERLIST_LABEL) || label.equals(INVENTORY_LABEL) || label.equals(INVENTORY_OTHER_LABEL) || label.equals(ENDERCHEST_LABEL) || label.equals(ENDERCHEST_OTHER_LABEL);
            }).map(each -> each.delete()).reduce(RestAction::and).ifPresent(action -> action.complete());
            if (InteractiveChatDiscordSrvAddon.plugin.playerlistCommandEnabled) {
                guild.upsertCommand(PLAYERLIST_LABEL, ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.playerlistCommandDescription)).setDefaultEnabled(false).queue(command -> {
                    command.updatePrivileges(guild, JDAUtils.toWhitelistedCommandPrivileges(guild, JDAUtils.toRoles(guild, InteractiveChatDiscordSrvAddon.plugin.playerlistCommandRoles))).queue();
                });
            }
            Optional<ICPlaceholder> optInvPlaceholder = InteractiveChat.placeholderList.values().stream().filter(each -> each.getKeyword().equals(InteractiveChat.invPlaceholder)).findFirst();
            if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandEnabled && optInvPlaceholder.isPresent()) {
                guild.upsertCommand(INVENTORY_LABEL, ChatColorUtils.stripColor(optInvPlaceholder.get().getDescription())).setDefaultEnabled(false).queue(command -> {
                    command.updatePrivileges(guild, JDAUtils.toWhitelistedCommandPrivileges(guild, JDAUtils.toRoles(guild, InteractiveChatDiscordSrvAddon.plugin.shareInvCommandSelfRoles))).queue();
                });
                if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandAsOthers) {
                    guild.upsertCommand(INVENTORY_OTHER_LABEL, ChatColorUtils.stripColor(optInvPlaceholder.get().getDescription())).addOption(OptionType.USER, memberLabel, memberDescription, true).setDefaultEnabled(false).queue(command -> {
                        command.updatePrivileges(guild, JDAUtils.toWhitelistedCommandPrivileges(guild, JDAUtils.toRoles(guild, InteractiveChatDiscordSrvAddon.plugin.shareInvCommandOthersRoles))).queue();
                    });
                }
            }
            Optional<ICPlaceholder> optEnderPlaceholder = InteractiveChat.placeholderList.values().stream().filter(each -> each.getKeyword().equals(InteractiveChat.enderPlaceholder)).findFirst();
            if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandEnabled && optEnderPlaceholder.isPresent()) {
                guild.upsertCommand(ENDERCHEST_LABEL, ChatColorUtils.stripColor(optEnderPlaceholder.get().getDescription())).setDefaultEnabled(false).queue(command -> {
                    command.updatePrivileges(guild, JDAUtils.toWhitelistedCommandPrivileges(guild, JDAUtils.toRoles(guild, InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandSelfRoles))).queue();
                });
                if (InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandAsOthers) {
                    guild.upsertCommand(ENDERCHEST_OTHER_LABEL, ChatColorUtils.stripColor(optEnderPlaceholder.get().getDescription())).addOption(OptionType.USER, memberLabel, memberDescription, true).setDefaultEnabled(false).queue(command -> {
                        command.updatePrivileges(guild, JDAUtils.toWhitelistedCommandPrivileges(guild, JDAUtils.toRoles(guild, InteractiveChatDiscordSrvAddon.plugin.shareEnderCommandOthersRoles))).queue();
                    });
                }
            }
        } catch (ErrorResponseException e) {
            if (e.getResponse().code == 50001) {
                throw new DiscordCommandRegistrationException("Scope \"applications.commands\" missing in discord bot application.\nCheck the Q&A section in https://www.spigotmc.org/resources/83917/ for more information", e);
            }
            throw new DiscordCommandRegistrationException(e);
        }
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (!(event.getChannel() instanceof TextChannel)) {
            return;
        }
        TextChannel channel = (TextChannel) event.getChannel();
        String label = event.getName();
        if (InteractiveChatDiscordSrvAddon.plugin.playerlistCommandEnabled && label.equalsIgnoreCase(PLAYERLIST_LABEL)) {
            String minecraftChannel = discordsrv.getChannels().entrySet().stream().filter(entry -> channel.getId().equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
            if (minecraftChannel == null) {
                if (InteractiveChatDiscordSrvAddon.plugin.respondToCommandsInInvalidChannels && InteractiveChatDiscordSrvAddon.plugin.playerlistCommandIsMainServer) {
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
                        if (!VanishUtils.isVanished(playerinfo.getFirst())) {
                            players.put(Bukkit.getOfflinePlayer(playerinfo.getFirst()), playerinfo.getThird());
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
                    OfflinePlayer firstPlayer = players.keySet().iterator().next();
                    List<Component> header = new ArrayList<>();
                    if (!InteractiveChatDiscordSrvAddon.plugin.playerlistCommandHeader.isEmpty()) {
                        header = ComponentStyling.splitAtLineBreaks(LegacyComponentSerializer.legacySection().deserialize(ChatColorUtils.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(firstPlayer, InteractiveChatDiscordSrvAddon.plugin.playerlistCommandHeader.replace("{OnlinePlayers}", players.size() + "")))));
                    }
                    errorCode--;
                    List<Component> footer = new ArrayList<>();
                    if (!InteractiveChatDiscordSrvAddon.plugin.playerlistCommandFooter.isEmpty()) {
                        footer = ComponentStyling.splitAtLineBreaks(LegacyComponentSerializer.legacySection().deserialize(ChatColorUtils.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(firstPlayer, InteractiveChatDiscordSrvAddon.plugin.playerlistCommandFooter.replace("{OnlinePlayers}", players.size() + "")))));
                    }
                    errorCode--;
                    List<ValueTrios<UUID, Component, Integer>> player = new ArrayList<>();
                    for (Entry<OfflinePlayer, Integer> entry : players.entrySet()) {
                        OfflinePlayer offlineplayer = entry.getKey();
                        player.add(new ValueTrios<>(offlineplayer.getUniqueId(), LegacyComponentSerializer.legacySection().deserialize(ChatColorUtils.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(offlineplayer, InteractiveChatDiscordSrvAddon.plugin.playerlistCommandPlayerFormat))), entry.getValue()));
                    }
                    errorCode--;
                    BufferedImage image = ImageGeneration.getTabListImage(header, footer, player, InteractiveChatDiscordSrvAddon.plugin.playerlistCommandAvatar, InteractiveChatDiscordSrvAddon.plugin.playerlistCommandPing);
                    errorCode--;
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", os);
                    errorCode--;
                    event.getHook().editOriginalEmbeds(new EmbedBuilder().setImage("attachment://Tablist.png").setColor(InteractiveChatDiscordSrvAddon.plugin.playerlistCommandColor).build()).addFile(os.toByteArray(), "Tablist.png").queue(success -> {
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

        } else if (InteractiveChatDiscordSrvAddon.plugin.shareInvCommandEnabled && (label.equalsIgnoreCase(INVENTORY_LABEL) || label.equalsIgnoreCase(INVENTORY_OTHER_LABEL))) {
            String minecraftChannel = discordsrv.getChannels().entrySet().stream().filter(entry -> channel.getId().equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
            if (minecraftChannel == null) {
                if (InteractiveChatDiscordSrvAddon.plugin.respondToCommandsInInvalidChannels && InteractiveChatDiscordSrvAddon.plugin.shareInvCommandIsMainServer) {
                    event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.invalidDiscordChannel)).setEphemeral(true).queue();
                }
                return;
            }
            String discordUserId = event.getUser().getId();
            List<OptionMapping> options = event.getOptions();
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
                if (InteractiveChat.bungeecordMode && offlineICPlayer instanceof ICPlayer) {
                    ICPlayer icplayer = (ICPlayer) offlineICPlayer;
                    if (icplayer.isLocal()) {
                        BungeeMessageSender.forwardInventory(System.currentTimeMillis(), uuid, icplayer.isRightHanded(), icplayer.getSelectedSlot(), icplayer.getExperienceLevel(), null, icplayer.getInventory());
                    } else {
                        TimeUnit.MILLISECONDS.sleep(InteractiveChat.remoteDelay);
                    }
                }
                errorCode--;
                BufferedImage image = InteractiveChatDiscordSrvAddon.plugin.usePlayerInvView ? ImageGeneration.getPlayerInventoryImage(offlineICPlayer.getInventory(), offlineICPlayer) : ImageGeneration.getInventoryImage(offlineICPlayer.getInventory(), offlineICPlayer);
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
                    WebhookMessageUpdateAction<Message> action = event.getHook().editOriginal(PlainTextComponentSerializer.plainText().serialize(component));
                    errorCode--;
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", os);
                    action.addFile(os.toByteArray(), "Inventory.png");
                    errorCode--;
                    if (InteractiveChatDiscordSrvAddon.plugin.invShowLevel) {
                        int level = offlineICPlayer.getExperienceLevel();
                        ByteArrayOutputStream bottleOut = new ByteArrayOutputStream();
                        ImageIO.write(InteractiveChatDiscordSrvAddon.plugin.modelRenderer.render(32, 32, InteractiveChatDiscordSrvAddon.plugin.resourceManager, InteractiveChat.version.isOld(), "minecraft:item/experience_bottle", ModelDisplayPosition.GUI, false).getImage(), "png", bottleOut);
                        embedBuilder.setFooter(LanguageUtils.getTranslation(TranslationKeyUtils.getLevelTranslation(level), InteractiveChatDiscordSrvAddon.plugin.language).replaceFirst("%s|%d", level + ""), "attachment://Level.png");
                        action.addFile(bottleOut.toByteArray(), "Level.png");
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
            List<OptionMapping> options = event.getOptions();
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
                if (InteractiveChat.bungeecordMode && offlineICPlayer instanceof ICPlayer) {
                    ICPlayer icplayer = (ICPlayer) offlineICPlayer;
                    if (icplayer.isLocal()) {
                        BungeeMessageSender.forwardEnderchest(System.currentTimeMillis(), uuid, icplayer.isRightHanded(), icplayer.getSelectedSlot(), icplayer.getExperienceLevel(), null, icplayer.getEnderChest());
                    } else {
                        TimeUnit.MILLISECONDS.sleep(InteractiveChat.remoteDelay);
                    }
                }
                errorCode--;
                BufferedImage image = ImageGeneration.getInventoryImage(offlineICPlayer.getEnderChest(), offlineICPlayer);
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
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", os);
                    errorCode--;
                    event.getHook().editOriginal(PlainTextComponentSerializer.plainText().serialize(component)).setEmbeds(new EmbedBuilder().setAuthor(title).setImage("attachment://Inventory.png").setColor(InteractiveChatDiscordSrvAddon.plugin.enderColor).build()).addFile(os.toByteArray(), "Inventory.png").queue();
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
