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

package com.loohp.interactivechatdiscordsrvaddon;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.api.InteractiveChatAPI;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ComponentStyling;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechatdiscordsrvaddon.api.events.InteractiveChatDiscordSRVConfigReloadEvent;
import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents;
import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents.DiscordAttachmentData;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackInfo;
import com.loohp.interactivechatdiscordsrvaddon.updater.Updater;
import com.loohp.interactivechatdiscordsrvaddon.updater.Updater.UpdaterResponse;
import com.loohp.interactivechatdiscordsrvaddon.utils.ResourcePackInfoUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.TranslationKeyUtils;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.GraphicsToPacketMapWrapper;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Commands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!label.equalsIgnoreCase("interactivechatdiscordsrv") && !label.equalsIgnoreCase("icd")) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + "InteractiveChat DiscordSRV Addon written by LOOHP!");
            sender.sendMessage(ChatColor.GOLD + "You are running ICDiscordSRVAddon version: " + InteractiveChatDiscordSrvAddon.plugin.getDescription().getVersion());
            return true;
        }

        if (args[0].equalsIgnoreCase("status")) {
            if (sender.hasPermission("interactivechatdiscordsrv.status")) {
                sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.defaultResourceHashLang.replaceFirst("%s", InteractiveChatDiscordSrvAddon.plugin.defaultResourceHash + " (" + InteractiveChat.exactMinecraftVersion + ")"));
                sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.loadedResourcesLang);
                for (ResourcePackInfo info : InteractiveChatDiscordSrvAddon.plugin.getResourceManager().getResourcePackInfo()) {
                    Component name = ResourcePackInfoUtils.resolveName(info);
                    if (info.getStatus()) {
                        Component component = Component.text(" - ").append(name).color(info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) == 0 ? NamedTextColor.GREEN : NamedTextColor.YELLOW);
                        Component hoverComponent = ResourcePackInfoUtils.resolveDescription(info);
                        if (info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) > 0) {
                            hoverComponent = hoverComponent.append(Component.text("\n")).append(Component.translatable(TranslationKeyUtils.getNewIncompatiblePack()).color(NamedTextColor.YELLOW));
                        } else if (info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) < 0) {
                            hoverComponent = hoverComponent.append(Component.text("\n")).append(Component.translatable(TranslationKeyUtils.getOldIncompatiblePack()).color(NamedTextColor.YELLOW));
                        }
                        component = component.hoverEvent(HoverEvent.showText(hoverComponent));
                        InteractiveChatAPI.sendMessage(sender, component);
                        if (!(sender instanceof Player)) {
                            for (Component each : ComponentStyling.splitAtLineBreaks(ResourcePackInfoUtils.resolveDescription(info))) {
                                InteractiveChatAPI.sendMessage(sender, Component.text("   - ").color(NamedTextColor.GRAY).append(each));
                                if (info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) > 0) {
                                    sender.sendMessage(ChatColor.YELLOW + "     " + LanguageUtils.getTranslation(TranslationKeyUtils.getNewIncompatiblePack(), InteractiveChatDiscordSrvAddon.plugin.language));
                                } else if (info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) < 0) {
                                    sender.sendMessage(ChatColor.YELLOW + "     " + LanguageUtils.getTranslation(TranslationKeyUtils.getOldIncompatiblePack(), InteractiveChatDiscordSrvAddon.plugin.language));
                                }
                            }
                        }
                    } else {
                        Component component = Component.text(" - ").append(name).color(NamedTextColor.RED);
                        if (info.getRejectedReason() != null) {
                            component = component.hoverEvent(HoverEvent.showText(Component.text(info.getRejectedReason()).color(NamedTextColor.RED)));
                        }
                        InteractiveChatAPI.sendMessage(sender, component);
                        if (!(sender instanceof Player)) {
                            InteractiveChatAPI.sendMessage(sender, Component.text("   - ").append(Component.text(info.getRejectedReason()).color(NamedTextColor.RED)).color(NamedTextColor.RED));
                        }
                    }
                }
            } else {
                sender.sendMessage(InteractiveChat.noPermissionMessage);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reloadconfig")) {
            if (sender.hasPermission("interactivechatdiscordsrv.reloadconfig")) {
                try {
                    if (InteractiveChatDiscordSrvAddon.plugin.resourceReloadLock.tryLock(0, TimeUnit.MILLISECONDS)) {
                        try {
                            InteractiveChatDiscordSrvAddon.plugin.reloadConfig();
                            Bukkit.getPluginManager().callEvent(new InteractiveChatDiscordSRVConfigReloadEvent());
                            sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.reloadConfigMessage);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            InteractiveChatDiscordSrvAddon.plugin.resourceReloadLock.unlock();
                        }
                    } else {
                        sender.sendMessage(ChatColor.YELLOW + "Resource reloading in progress, please wait!");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                sender.sendMessage(InteractiveChat.noPermissionMessage);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reloadtexture")) {
            List<String> argList = Arrays.asList(args);
            boolean clean = argList.contains("--reset");
            boolean redownload = argList.contains("--redownload") || clean;
            if (sender.hasPermission("interactivechatdiscordsrv.reloadtexture")) {
                sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.reloadTextureMessage);
                InteractiveChatDiscordSrvAddon.plugin.reloadTextures(redownload, clean, sender);
            } else {
                sender.sendMessage(InteractiveChat.noPermissionMessage);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("update")) {
            if (sender.hasPermission("interactivechatdiscordsrv.update")) {
                sender.sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] InteractiveChat DiscordSRV Addon written by LOOHP!");
                sender.sendMessage(ChatColor.GOLD + "[ICDiscordSrvAddon] You are running ICDiscordSRVAddon version: " + InteractiveChatDiscordSrvAddon.plugin.getDescription().getVersion());
                Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
                    UpdaterResponse version = Updater.checkUpdate();
                    if (version.getResult().equals("latest")) {
                        if (version.isDevBuildLatest()) {
                            sender.sendMessage(ChatColor.GREEN + "[ICDiscordSrvAddon] You are running the latest version!");
                        } else {
                            Updater.sendUpdateMessage(sender, version.getResult(), version.getSpigotPluginId(), true);
                        }
                    } else {
                        Updater.sendUpdateMessage(sender, version.getResult(), version.getSpigotPluginId());
                    }
                });
            } else {
                sender.sendMessage(InteractiveChat.noPermissionMessage);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("imagemap")) {
            if (args.length > 1 && sender instanceof Player) {
                try {
                    DiscordAttachmentData data = InboundToGameEvents.DATA.get(UUID.fromString(args[1]));
                    if (data != null && (data.isImage() || data.isVideo())) {
                        GraphicsToPacketMapWrapper imageMap = data.getImageMap();
                        if (imageMap.futureCancelled()) {
                            sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.linkExpired);
                        } else if (imageMap.futureCompleted()) {
                            if (imageMap.getColors() == null || imageMap.getColors().isEmpty()) {
                                sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.linkExpired);
                            } else {
                                imageMap.show((Player) sender);
                            }
                        } else {
                            sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.previewLoading);
                        }
                    } else {
                        sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.linkExpired);
                    }
                } catch (Exception e) {
                    sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.linkExpired);
                }
            }
            return true;
        }

        sender.sendMessage(ChatColorUtils.translateAlternateColorCodes('&', Bukkit.spigot().getConfig().getString("messages.unknown-command")));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> tab = new LinkedList<>();
        if (!label.equalsIgnoreCase("interactivechatdiscordsrv") && !label.equalsIgnoreCase("icd")) {
            return tab;
        }

        switch (args.length) {
            case 0:
                if (sender.hasPermission("interactivechatdiscordsrv.reloadconfig")) {
                    tab.add("reloadconfig");
                }
                if (sender.hasPermission("interactivechatdiscordsrv.reloadtexture")) {
                    tab.add("reloadtexture");
                }
                if (sender.hasPermission("interactivechatdiscordsrv.update")) {
                    tab.add("update");
                }
                if (sender.hasPermission("interactivechatdiscordsrv.status")) {
                    tab.add("status");
                }
                return tab;
            case 1:
                if (sender.hasPermission("interactivechatdiscordsrv.reloadconfig")) {
                    if ("reloadconfig".startsWith(args[0].toLowerCase())) {
                        tab.add("reloadconfig");
                    }
                }
                if (sender.hasPermission("interactivechatdiscordsrv.reloadtexture")) {
                    if ("reloadtexture".startsWith(args[0].toLowerCase())) {
                        tab.add("reloadtexture");
                    }
                }
                if (sender.hasPermission("interactivechatdiscordsrv.update")) {
                    if ("update".startsWith(args[0].toLowerCase())) {
                        tab.add("update");
                    }
                }
                if (sender.hasPermission("interactivechatdiscordsrv.status")) {
                    if ("status".startsWith(args[0].toLowerCase())) {
                        tab.add("status");
                    }
                }
                return tab;
            case 2:
                if (sender.hasPermission("interactivechatdiscordsrv.reloadtexture")) {
                    if ("reloadtexture".equals(args[0].toLowerCase())) {
                        if ("--redownload".startsWith(args[1].toLowerCase())) {
                            tab.add("--redownload");
                        }
                        if ("--reset".startsWith(args[1].toLowerCase())) {
                            tab.add("--reset");
                        }
                    }
                }
                return tab;
            default:
                return tab;
        }
    }

}
