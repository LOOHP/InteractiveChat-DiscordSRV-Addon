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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.config.Config;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.ICPlayerFactory;
import com.loohp.interactivechat.objectholders.PlaceholderCooldownManager;
import com.loohp.interactivechat.registry.Registry;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ColorUtils;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.SkinUtils;
import com.loohp.interactivechatdiscordsrvaddon.AssetsDownloader.ServerResourcePackDownloadResult;
import com.loohp.interactivechatdiscordsrvaddon.api.events.ResourceManagerInitializeEvent;
import com.loohp.interactivechatdiscordsrvaddon.debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.listeners.DiscordInteractionEvents;
import com.loohp.interactivechatdiscordsrvaddon.listeners.DiscordReadyEvents;
import com.loohp.interactivechatdiscordsrvaddon.listeners.ICPlayerEvents;
import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents;
import com.loohp.interactivechatdiscordsrvaddon.listeners.OutboundToDiscordEvents;
import com.loohp.interactivechatdiscordsrvaddon.metrics.Charts;
import com.loohp.interactivechatdiscordsrvaddon.metrics.Metrics;
import com.loohp.interactivechatdiscordsrvaddon.registry.InteractiveChatRegistry;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.CustomItemTextureRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ICacheManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ModelRenderer;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager.ModManagerSupplier;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackInfo;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackType;
import com.loohp.interactivechatdiscordsrvaddon.resources.fonts.FontManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.fonts.FontTextureResource;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.ModManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.chime.ChimeManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine.OptifineManager;
import com.loohp.interactivechatdiscordsrvaddon.updater.Updater;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.requests.GatewayIntent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class InteractiveChatDiscordSrvAddon extends JavaPlugin implements Listener {

    public static final int BSTATS_PLUGIN_ID = 8863;
    public static final String CONFIG_ID = "interactivechatdiscordsrvaddon_config";

    public static final List<Permission> requiredPermissions = Collections.unmodifiableList(Arrays.asList(
        Permission.MESSAGE_READ,
        Permission.MESSAGE_WRITE,
        Permission.MESSAGE_MANAGE,
        Permission.MESSAGE_EMBED_LINKS,
        Permission.MESSAGE_ATTACH_FILES,
        Permission.MANAGE_WEBHOOKS
    ));

    public static InteractiveChatDiscordSrvAddon plugin;
    public static InteractiveChat interactivechat;
    public static DiscordSRV discordsrv;

    public static boolean isReady = false;

    public static boolean debug = false;

    protected final ReentrantLock resourceReloadLock = new ReentrantLock(true);
    public Metrics metrics;
    public AtomicLong messagesCounter = new AtomicLong(0);
    public AtomicLong imageCounter = new AtomicLong(0);
    public AtomicLong inventoryImageCounter = new AtomicLong(0);
    public AtomicLong attachmentCounter = new AtomicLong(0);
    public AtomicLong attachmentImageCounter = new AtomicLong(0);
    public AtomicLong imagesViewedCounter = new AtomicLong(0);
    public Queue<Integer> playerModelRenderingTimes = new ConcurrentLinkedQueue<>();
    public boolean itemImage = true;
    public boolean invImage = true;
    public boolean enderImage = true;
    public boolean usePlayerInvView = true;
    public boolean renderHandHeldItems = true;
    public String itemDisplaySingle = "";
    public String itemDisplayMultiple = "";
    public Color invColor = Color.black;
    public Color enderColor = Color.black;
    public boolean itemUseTooltipImageOnBaseItem = false;
    public boolean itemAltAir = true;
    public boolean invShowLevel = true;
    public boolean hoverEnabled = true;
    public boolean hoverImage = true;
    public Set<Integer> hoverIgnore = new HashSet<>();
    public boolean hoverUseTooltipImage = true;
    public String reloadConfigMessage;
    public String reloadTextureMessage;
    public String linkExpired;
    public String interactionExpire;
    public String previewLoading;
    public String accountNotLinked;
    public String unableToRetrieveData;
    public String invalidDiscordChannel;
    public String trueLabel;
    public String falseLabel;
    public String defaultResourceHashLang;
    public String fontsActiveLang;
    public String loadedResourcesLang;
    public boolean convertDiscordAttachments = true;
    public String discordAttachmentsFormattingText;
    public boolean discordAttachmentsFormattingHoverEnabled = true;
    public String discordAttachmentsFormattingHoverText;
    public boolean discordAttachmentsImagesUseMaps = true;
    public long discordAttachmentsPreviewLimit = 0;
    public int discordAttachmentTimeout = 0;
    public String discordAttachmentsFormattingImageAppend;
    public String discordAttachmentsFormattingImageAppendHover;
    public Color discordAttachmentsMapBackgroundColor = null;
    public boolean imageWhitelistEnabled = false;
    public List<String> whitelistedImageUrls = new ArrayList<>();
    public boolean translateMentions = true;
    public String mentionHighlight = "";
    public boolean deathMessageItem = true;
    public boolean advancementName = true;
    public boolean advancementItem = true;
    public boolean advancementDescription = true;
    public boolean updaterEnabled = true;
    public int cacheTimeout = 1200;
    public boolean escapePlaceholdersFromDiscord = true;
    public boolean escapeDiscordMarkdownInItems = true;
    public boolean reducedAssetsDownloadInfo = false;
    public boolean playbackBarEnabled = true;
    public Color playbackBarFilledColor;
    public Color playbackBarEmptyColor;
    public String language = "en_us";
    public boolean respondToCommandsInInvalidChannels = true;
    public boolean respondToInvalidInteractions = true;
    public String discordMemberLabel = "";
    public String discordMemberDescription = "";
    public String discordSlotLabel = "";
    public String discordSlotDescription = "";
    public boolean resourcepackCommandEnabled = true;
    public String resourcepackCommandDescription = "";
    public boolean resourcepackCommandIsMainServer = true;
    public boolean playerlistCommandEnabled = true;
    public String playerlistCommandDescription = "";
    public boolean playerlistCommandIsMainServer = true;
    public boolean playerlistCommandBungeecord = true;
    public boolean playerlistCommandOnlyInteractiveChatServers = true;
    public int playerlistCommandDeleteAfter = 10;
    public String playerlistCommandPlayerFormat = "";
    public boolean playerlistCommandAvatar = true;
    public boolean playerlistCommandPing = true;
    public String playerlistCommandHeader = "";
    public String playerlistCommandFooter = "";
    public boolean playerlistCommandParsePlayerNamesWithMiniMessage = false;
    public String playerlistCommandEmptyServer = "";
    public Color playerlistCommandColor = new Color(153, 153, 153);
    public int playerlistCommandMinWidth = 0;
    public int playerlistMaxPlayers = 80;
    public List<String> playerlistOrderingTypes = new ArrayList<>();
    public boolean shareItemCommandEnabled = true;
    public boolean shareItemCommandAsOthers = true;
    public boolean shareItemCommandIsMainServer = true;
    public String shareItemCommandInGameMessageText = "";
    public String shareItemCommandTitle = "";
    public boolean shareInvCommandEnabled = true;
    public boolean shareInvCommandAsOthers = true;
    public boolean shareInvCommandIsMainServer = true;
    public String shareInvCommandInGameMessageText = "";
    public String shareInvCommandInGameMessageHover = "";
    public String shareInvCommandTitle = "";
    public String shareInvCommandSkullName = "";
    public boolean shareEnderCommandEnabled = true;
    public boolean shareEnderCommandAsOthers = true;
    public boolean shareEnderCommandIsMainServer = true;
    public String shareEnderCommandInGameMessageText = "";
    public String shareEnderCommandInGameMessageHover = "";
    public String shareEnderCommandTitle = "";
    public PlaceholderCooldownManager placeholderCooldownManager = new PlaceholderCooldownManager();
    public String defaultResourceHash = "N/A";
    public List<String> resourceOrder = new ArrayList<>();
    public boolean forceUnicode = false;
    public boolean includeServerResourcePack = true;
    public String alternateResourcePackURL = "";
    public String alternateResourcePackHash = "";
    public boolean optifineCustomTextures = true;
    public boolean chimeOverrideModels = true;
    public ResourceManager resourceManager;
    public ModelRenderer modelRenderer;
    public ExecutorService mediaReadingService;

    protected Map<String, byte[]> extras = new ConcurrentHashMap<>();

    @Override
    public void onLoad() {
        DiscordSRV.api.requireIntent(GatewayIntent.GUILD_MESSAGE_REACTIONS);
    }

    @Override
    public void onEnable() {
        plugin = this;
        interactivechat = InteractiveChat.plugin;
        discordsrv = DiscordSRV.getPlugin();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        AssetsDownloader.loadLibraries(getDataFolder());

        try {
            Config.loadConfig(CONFIG_ID, new File(getDataFolder(), "config.yml"), getClass().getClassLoader().getResourceAsStream("config.yml"), getClass().getClassLoader().getResourceAsStream("config.yml"), true);
        } catch (IOException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        reloadConfig();

        metrics = new Metrics(this, BSTATS_PLUGIN_ID);
        Charts.setup(metrics);

        DiscordSRV.api.subscribe(new DiscordReadyEvents());
        DiscordSRV.api.subscribe(new OutboundToDiscordEvents());
        DiscordSRV.api.subscribe(new InboundToGameEvents());

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new InboundToGameEvents(), this);
        getServer().getPluginManager().registerEvents(new OutboundToDiscordEvents(), this);
        getServer().getPluginManager().registerEvents(new ICPlayerEvents(), this);
        getServer().getPluginManager().registerEvents(new Debug(), this);
        getServer().getPluginManager().registerEvents(new Updater(), this);
        getCommand("interactivechatdiscordsrv").setExecutor(new Commands());

        File resourcepacks = new File(getDataFolder(), "resourcepacks");
        if (!resourcepacks.exists()) {
            File resources = new File(getDataFolder(), "resources");
            if (resources.exists() && resources.isDirectory()) {
                try {
                    Files.move(resources.toPath(), resourcepacks.toPath(), StandardCopyOption.ATOMIC_MOVE);
                } catch (IOException e) {
                    getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to move folder, are any files opened?");
                    e.printStackTrace();
                    getServer().getPluginManager().disablePlugin(this);
                    return;
                }
            } else {
                resourcepacks.mkdirs();
            }
        }
        File serverResourcePack = new File(getDataFolder(), "server-resource-packs");
        if (!serverResourcePack.exists()) {
            serverResourcePack.mkdirs();
        }

        if (!compatible()) {
            for (int i = 0; i < 10; i++) {
                getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] VERSION NOT COMPATIBLE WITH INSTALLED INTERACTIVECHAT VERSION, PLEASE UPDATE BOTH TO LATEST!!!!");
            }
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ICDiscordSrvAddon] InteractiveChat DiscordSRV Addon has been Enabled!");
        }

        reloadTextures(false, false);
        modelRenderer = new ModelRenderer(str -> new ThreadFactoryBuilder().setNameFormat(str).build(), () -> InteractiveChatDiscordSrvAddon.plugin.cacheTimeout, () -> 8, () -> Runtime.getRuntime().availableProcessors());

        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("InteractiveChatDiscordSRVAddon Async Media Reading Thread #%d").build();
        mediaReadingService = Executors.newFixedThreadPool(4, factory);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (ICPlayer player : ICPlayerFactory.getOnlineICPlayers()) {
                cachePlayerSkin(player);
            }
            AssetsDownloader.loadExtras();
        }, 600, 6000);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> cachePlayerSkin(ICPlayerFactory.getICPlayer(event.getPlayer())), 40);
    }

    private void cachePlayerSkin(ICPlayer player) {
        Debug.debug("Caching skin for player " + player.getName() + " (" + player.getUniqueId() + ")");
        if (player.isLocal()) {
            try {
                UUID uuid = player.getUniqueId();
                JSONObject json = (JSONObject) new JSONParser().parse(SkinUtils.getSkinJsonFromProfile(player.getLocalPlayer()));
                String value = (String) ((JSONObject) ((JSONObject) json.get("textures")).get("SKIN")).get("url");
                BufferedImage skin = ImageUtils.downloadImage(value);
                resourceManager.getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).putCache(uuid + value + ImageGeneration.PLAYER_SKIN_CACHE_KEY, skin);
            } catch (Exception e) {
            }
        } else {
            try {
                UUID uuid = player.getUniqueId();
                String value = SkinUtils.getSkinURLFromUUID(uuid);
                BufferedImage skin = ImageUtils.downloadImage(value);
                resourceManager.getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).putCache(uuid + "null" + ImageGeneration.PLAYER_SKIN_CACHE_KEY, skin);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onDisable() {
        DiscordInteractionEvents.unregisterAll();
        modelRenderer.close();
        mediaReadingService.shutdown();
        if (resourceManager != null) {
            resourceManager.close();
        }
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] InteractiveChat DiscordSRV Addon has been Disabled!");
    }

    public boolean compatible() {
        try {
            return Registry.class.getField("INTERACTIVE_CHAT_DISCORD_SRV_ADDON_COMPATIBLE_VERSION").getInt(null) == InteractiveChatRegistry.class.getField("INTERACTIVE_CHAT_DISCORD_SRV_ADDON_COMPATIBLE_VERSION").getInt(null);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void reloadConfig() {
        Config config = Config.getConfig(CONFIG_ID);
        config.reload();

        reloadConfigMessage = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.ReloadConfig"));
        reloadTextureMessage = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.ReloadTexture"));
        linkExpired = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.LinkExpired"));
        previewLoading = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.PreviewLoading"));
        accountNotLinked = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.AccountNotLinked"));
        unableToRetrieveData = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.UnableToRetrieveData"));
        invalidDiscordChannel = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.InvalidDiscordChannel"));
        interactionExpire = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.InteractionExpired"));
        trueLabel = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.TrueLabel"));
        falseLabel = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.FalseLabel"));

        defaultResourceHashLang = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.StatusCommand.DefaultResourceHash"));
        fontsActiveLang = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.StatusCommand.FontsActive"));
        loadedResourcesLang = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.StatusCommand.LoadedResources"));

        debug = config.getConfiguration().getBoolean("Debug.PrintInfoToConsole");

        resourceOrder.clear();
        List<String> order = config.getConfiguration().getStringList("Resources.Order");
        ListIterator<String> itr = order.listIterator(order.size());
        while (itr.hasPrevious()) {
            String pack = itr.previous();
            resourceOrder.add(pack);
        }

        includeServerResourcePack = config.getConfiguration().getBoolean("Resources.IncludeServerResourcePack");
        alternateResourcePackURL = config.getConfiguration().getString("Resources.AlternateServerResourcePack.URL");
        alternateResourcePackHash = config.getConfiguration().getString("Resources.AlternateServerResourcePack.Hash");
        optifineCustomTextures = config.getConfiguration().getBoolean("Resources.OptifineCustomTextures");
        chimeOverrideModels = config.getConfiguration().getBoolean("Resources.ChimeOverrideModels") && InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16);

        itemImage = config.getConfiguration().getBoolean("InventoryImage.Item.Enabled");
        invImage = config.getConfiguration().getBoolean("InventoryImage.Inventory.Enabled");
        enderImage = config.getConfiguration().getBoolean("InventoryImage.EnderChest.Enabled");

        usePlayerInvView = config.getConfiguration().getBoolean("InventoryImage.Inventory.UsePlayerInventoryView");
        renderHandHeldItems = config.getConfiguration().getBoolean("InventoryImage.Inventory.RenderHandHeldItems");

        itemUseTooltipImageOnBaseItem = config.getConfiguration().getBoolean("InventoryImage.Item.UseTooltipImageOnBaseItem");
        itemAltAir = config.getConfiguration().getBoolean("InventoryImage.Item.AlternateAirTexture");

        invShowLevel = config.getConfiguration().getBoolean("InventoryImage.Inventory.ShowExperienceLevel");

        hoverEnabled = config.getConfiguration().getBoolean("HoverEventDisplay.Enabled");
        hoverImage = config.getConfiguration().getBoolean("HoverEventDisplay.ShowCursorImage");
        hoverIgnore.clear();
        hoverIgnore = new HashSet<>(config.getConfiguration().getIntegerList("HoverEventDisplay.IgnoredPlaceholderIndexes"));

        hoverUseTooltipImage = config.getConfiguration().getBoolean("HoverEventDisplay.UseTooltipImage");

        convertDiscordAttachments = config.getConfiguration().getBoolean("DiscordAttachments.Convert");
        discordAttachmentsFormattingText = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordAttachments.Formatting.Text"));
        discordAttachmentsFormattingHoverEnabled = config.getConfiguration().getBoolean("DiscordAttachments.Formatting.Hover.Enabled");
        discordAttachmentsFormattingHoverText = ChatColorUtils.translateAlternateColorCodes('&', String.join("\n", config.getConfiguration().getStringList("DiscordAttachments.Formatting.Hover.HoverText")));
        discordAttachmentsImagesUseMaps = config.getConfiguration().getBoolean("DiscordAttachments.ShowImageUsingMaps");
        discordAttachmentsPreviewLimit = config.getConfiguration().getLong("DiscordAttachments.FileSizeLimit");
        discordAttachmentTimeout = config.getConfiguration().getInt("DiscordAttachments.Timeout") * 20;
        discordAttachmentsFormattingImageAppend = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordAttachments.Formatting.ImageOriginal"));
        discordAttachmentsFormattingImageAppendHover = ChatColorUtils.translateAlternateColorCodes('&', String.join("\n", config.getConfiguration().getStringList("DiscordAttachments.Formatting.Hover.ImageOriginalHover")));

        boolean transparent = config.getConfiguration().getBoolean("DiscordAttachments.ImageMapBackground.Transparent");
        if (transparent) {
            discordAttachmentsMapBackgroundColor = null;
        } else {
            discordAttachmentsMapBackgroundColor = ColorUtils.hex2Rgb(config.getConfiguration().getString("DiscordAttachments.ImageMapBackground.Color"));
        }

        imageWhitelistEnabled = config.getConfiguration().getBoolean("DiscordAttachments.RestrictImageUrl.Enabled");
        whitelistedImageUrls = config.getConfiguration().getStringList("DiscordAttachments.RestrictImageUrl.Whitelist");

        updaterEnabled = config.getConfiguration().getBoolean("Options.UpdaterEnabled");

        cacheTimeout = config.getConfiguration().getInt("Settings.CacheTimeout") * 20;

        escapePlaceholdersFromDiscord = config.getConfiguration().getBoolean("Settings.EscapePlaceholdersSentFromDiscord");
        escapeDiscordMarkdownInItems = config.getConfiguration().getBoolean("Settings.EscapeDiscordMarkdownFormattingInItems");
        reducedAssetsDownloadInfo = config.getConfiguration().getBoolean("Settings.ReducedAssetsDownloadInfo");

        itemDisplaySingle = config.getConfiguration().getString("InventoryImage.Item.EmbedDisplay.Single");
        itemDisplayMultiple = config.getConfiguration().getString("InventoryImage.Item.EmbedDisplay.Multiple");
        invColor = ColorUtils.hex2Rgb(config.getConfiguration().getString("InventoryImage.Inventory.EmbedColor"));
        enderColor = ColorUtils.hex2Rgb(config.getConfiguration().getString("InventoryImage.EnderChest.EmbedColor"));

        deathMessageItem = config.getConfiguration().getBoolean("DeathMessage.ShowItems");

        advancementName = config.getConfiguration().getBoolean("Advancements.CorrectAdvancementName");
        advancementItem = config.getConfiguration().getBoolean("Advancements.ChangeToItemIcon");
        advancementDescription = config.getConfiguration().getBoolean("Advancements.ShowDescription");

        translateMentions = config.getConfiguration().getBoolean("DiscordMention.TranslateMentions");
        mentionHighlight = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordMention.MentionHighlight"));

        playbackBarEnabled = config.getConfiguration().getBoolean("DiscordAttachments.PlaybackBar.Enabled");
        playbackBarFilledColor = ColorUtils.hex2Rgb(config.getConfiguration().getString("DiscordAttachments.PlaybackBar.FilledColor"));
        playbackBarEmptyColor = ColorUtils.hex2Rgb(config.getConfiguration().getString("DiscordAttachments.PlaybackBar.EmptyColor"));

        respondToCommandsInInvalidChannels = config.getConfiguration().getBoolean("DiscordCommands.GlobalSettings.RespondToCommandsInInvalidChannels");
        respondToInvalidInteractions = config.getConfiguration().getBoolean("DiscordCommands.GlobalSettings.RespondToInvalidInteractions");

        discordMemberLabel = config.getConfiguration().getString("DiscordCommands.GlobalSettings.Messages.MemberLabel").toLowerCase();
        discordMemberDescription = config.getConfiguration().getString("DiscordCommands.GlobalSettings.Messages.MemberDescription");
        discordSlotLabel = config.getConfiguration().getString("DiscordCommands.GlobalSettings.Messages.SlotLabel").toLowerCase();
        discordSlotDescription = config.getConfiguration().getString("DiscordCommands.GlobalSettings.Messages.SlotDescription");

        resourcepackCommandEnabled = config.getConfiguration().getBoolean("DiscordCommands.ResourcePack.Enabled");
        resourcepackCommandDescription = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.ResourcePack.Description"));
        resourcepackCommandIsMainServer = config.getConfiguration().getBoolean("DiscordCommands.ResourcePack.IsMainServer");

        playerlistCommandEnabled = config.getConfiguration().getBoolean("DiscordCommands.PlayerList.Enabled");
        playerlistCommandDescription = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.PlayerList.Description"));
        playerlistCommandIsMainServer = config.getConfiguration().getBoolean("DiscordCommands.PlayerList.IsMainServer");
        playerlistCommandBungeecord = config.getConfiguration().getBoolean("DiscordCommands.PlayerList.ListBungeecordPlayers");
        playerlistCommandOnlyInteractiveChatServers = config.getConfiguration().getBoolean("DiscordCommands.PlayerList.OnlyInteractiveChatServers");
        playerlistCommandDeleteAfter = config.getConfiguration().getInt("DiscordCommands.PlayerList.DeleteAfter");
        playerlistCommandPlayerFormat = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.PlayerList.TablistOptions.PlayerFormat"));
        playerlistCommandAvatar = config.getConfiguration().getBoolean("DiscordCommands.PlayerList.TablistOptions.ShowPlayerAvatar");
        playerlistCommandPing = config.getConfiguration().getBoolean("DiscordCommands.PlayerList.TablistOptions.ShowPlayerPing");
        playerlistCommandHeader = ChatColorUtils.translateAlternateColorCodes('&', String.join("\n", config.getConfiguration().getStringList("DiscordCommands.PlayerList.TablistOptions.HeaderText")));
        playerlistCommandFooter = ChatColorUtils.translateAlternateColorCodes('&', String.join("\n", config.getConfiguration().getStringList("DiscordCommands.PlayerList.TablistOptions.FooterText")));
        playerlistCommandParsePlayerNamesWithMiniMessage = config.getConfiguration().getBoolean("DiscordCommands.PlayerList.TablistOptions.ParsePlayerNamesWithMiniMessage");
        playerlistCommandEmptyServer = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.PlayerList.EmptyServer"));
        playerlistCommandColor = ColorUtils.hex2Rgb(config.getConfiguration().getString("DiscordCommands.PlayerList.TablistOptions.SidebarColor"));
        playerlistCommandMinWidth = config.getConfiguration().getInt("DiscordCommands.PlayerList.TablistOptions.PlayerMinWidth");
        playerlistMaxPlayers = config.getConfiguration().getInt("DiscordCommands.PlayerList.TablistOptions.MaxPlayersDisplayable");
        playerlistOrderingTypes = config.getConfiguration().getStringList("DiscordCommands.PlayerList.TablistOptions.PlayerOrder.OrderBy");

        shareItemCommandEnabled = config.getConfiguration().getBoolean("DiscordCommands.ShareItem.Enabled");
        shareItemCommandAsOthers = config.getConfiguration().getBoolean("DiscordCommands.ShareItem.AllowAsOthers");
        shareItemCommandIsMainServer = config.getConfiguration().getBoolean("DiscordCommands.ShareItem.IsMainServer");
        shareItemCommandInGameMessageText = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.ShareItem.InGameMessage.Text"));
        shareItemCommandTitle = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.ShareItem.InventoryTitle"));

        shareInvCommandEnabled = config.getConfiguration().getBoolean("DiscordCommands.ShareInventory.Enabled");
        shareInvCommandAsOthers = config.getConfiguration().getBoolean("DiscordCommands.ShareInventory.AllowAsOthers");
        shareInvCommandIsMainServer = config.getConfiguration().getBoolean("DiscordCommands.ShareInventory.IsMainServer");
        shareInvCommandInGameMessageText = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.ShareInventory.InGameMessage.Text"));
        shareInvCommandInGameMessageHover = ChatColorUtils.translateAlternateColorCodes('&', String.join("\n", config.getConfiguration().getStringList("DiscordCommands.ShareInventory.InGameMessage.Hover")));
        shareInvCommandTitle = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.ShareInventory.InventoryTitle"));
        shareInvCommandSkullName = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.ShareInventory.SkullDisplayName"));

        shareEnderCommandEnabled = config.getConfiguration().getBoolean("DiscordCommands.ShareEnderChest.Enabled");
        shareEnderCommandAsOthers = config.getConfiguration().getBoolean("DiscordCommands.ShareEnderChest.AllowAsOthers");
        shareEnderCommandIsMainServer = config.getConfiguration().getBoolean("DiscordCommands.ShareEnderChest.IsMainServer");
        shareEnderCommandInGameMessageText = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.ShareEnderChest.InGameMessage.Text"));
        shareEnderCommandInGameMessageHover = ChatColorUtils.translateAlternateColorCodes('&', String.join("\n", config.getConfiguration().getStringList("DiscordCommands.ShareEnderChest.InGameMessage.Hover")));
        shareEnderCommandTitle = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.ShareEnderChest.InventoryTitle"));

        language = config.getConfiguration().getString("Resources.Language");
        LanguageUtils.loadTranslations(language);
        forceUnicode = config.getConfiguration().getBoolean("Resources.ForceUnicodeFont");

        FontTextureResource.setCacheTime(cacheTimeout);

        discordsrv.reloadRegexes();
    }

    public byte[] getExtras(String str) {
        return extras.get(str);
    }

    public void reloadTextures(boolean redownload, boolean clean, CommandSender... receivers) {
        CommandSender[] senders;
        if (Arrays.stream(receivers).noneMatch(each -> each.equals(Bukkit.getConsoleSender()))) {
            senders = new CommandSender[receivers.length + 1];
            System.arraycopy(receivers, 0, senders, 0, receivers.length);
            senders[senders.length - 1] = Bukkit.getConsoleSender();
        } else {
            senders = receivers;
        }

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                if (!resourceReloadLock.tryLock(0, TimeUnit.MILLISECONDS)) {
                    sendMessage(ChatColor.YELLOW + "Resource reloading already in progress!", senders);
                    return;
                }
                isReady = false;
                if (InteractiveChatDiscordSrvAddon.plugin.resourceManager != null) {
                    Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                        InteractiveChatDiscordSrvAddon.plugin.resourceManager.close();
                        return null;
                    }).get();
                }
                try {
                    AssetsDownloader.loadAssets(getDataFolder(), redownload, clean, receivers);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                List<String> resourceList = new ArrayList<>();
                resourceList.add("Default");
                resourceList.addAll(resourceOrder);

                File serverResourcePackFolder = new File(getDataFolder(), "server-resource-packs");
                File serverResourcePack = null;
                if (includeServerResourcePack) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSrvAddon] Checking for server resource pack...");
                    ServerResourcePackDownloadResult result = AssetsDownloader.downloadServerResourcePack(serverResourcePackFolder);
                    serverResourcePack = result.getResourcePackFile();
                    if (result.getError() != null) {
                        result.getError().printStackTrace();
                    }
                    switch (result.getType()) {
                        case SUCCESS_NO_CHANGES:
                            sendMessage(ChatColor.GREEN + "[ICDiscordSrvAddon] Server resource pack found with verification hash: No changes", senders);
                            resourceList.add(serverResourcePack.getName());
                            break;
                        case SUCCESS_WITH_HASH:
                            sendMessage(ChatColor.GREEN + "[ICDiscordSrvAddon] Server resource pack found with verification hash: Hash changed, downloaded", senders);
                            resourceList.add(serverResourcePack.getName());
                            break;
                        case SUCCESS_NO_HASH:
                            sendMessage(ChatColor.GREEN + "[ICDiscordSrvAddon] Server resource pack found without verification hash: Downloaded", senders);
                            resourceList.add(serverResourcePack.getName());
                            break;
                        case FAILURE_WRONG_HASH:
                            sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Server resource pack had wrong hash (expected " + result.getExpectedHash() + ", found " + result.getPackHash() + ")", senders);
                            sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Server resource pack will not be applied: Hash check failure", senders);
                            break;
                        case FAILURE_DOWNLOAD:
                            sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Failed to download server resource pack", senders);
                            break;
                        case NO_PACK:
                            Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSrvAddon] No server resource pack found");
                            break;
                    }
                }

                sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Reloading ResourceManager: " + ChatColor.YELLOW + String.join(", ", resourceList), senders);

                List<ModManagerSupplier<?>> mods = new ArrayList<>();
                if (optifineCustomTextures) {
                    mods.add(manager -> new OptifineManager(manager));
                }
                if (chimeOverrideModels) {
                    mods.add(manager -> new ChimeManager(manager));
                }

                Bukkit.getPluginManager().callEvent(new ResourceManagerInitializeEvent(mods));

                @SuppressWarnings("resource")
                ResourceManager resourceManager = new ResourceManager(InteractiveChat.version.isLegacy(), InteractiveChat.version.isOlderOrEqualTo(MCVersion.V1_18_2), mods, Arrays.asList(CustomItemTextureRegistry.getDefaultSupplier(), ICacheManager.getDefaultSupplier(new File(getDataFolder(), "cache"))));

                for (Entry<String, ModManager> entry : resourceManager.getModManagers().entrySet()) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSrvAddon] Registered ModManager \"" + entry.getKey() + "\" of class \"" + entry.getValue().getClass().getName() + "\"");
                }

                resourceManager.getFontManager().setDefaultKey(forceUnicode ? FontManager.UNIFORM_FONT : FontManager.DEFAULT_FONT);
                resourceManager.getLanguageManager().setTranslateFunction((translateKey, language) -> LanguageUtils.getTranslation(translateKey, language));
                resourceManager.getLanguageManager().setAvailableLanguagesSupplier(() -> LanguageUtils.getLoadedLanguages());
                resourceManager.getLanguageManager().registerReloadListener(e -> {
                    LanguageUtils.clearPluginTranslations(InteractiveChatDiscordSrvAddon.plugin);
                    for (Entry<String, Map<String, String>> entry : e.getTranslations().entrySet()) {
                        LanguageUtils.loadPluginTranslations(InteractiveChatDiscordSrvAddon.plugin, entry.getKey(), entry.getValue());
                    }
                });

                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Loading \"Default\" resources...");
                resourceManager.loadResources(new File(getDataFolder() + "/built-in", "Default"), ResourcePackType.BUILT_IN);
                for (String resourceName : resourceOrder) {
                    try {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Loading \"" + resourceName + "\" resources...");
                        File resourcePackFile = new File(getDataFolder(), "resourcepacks/" + resourceName);
                        ResourcePackInfo info = resourceManager.loadResources(resourcePackFile, ResourcePackType.LOCAL);
                        if (info.getStatus()) {
                            if (info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) > 0) {
                                sendMessage(ChatColor.YELLOW + "[ICDiscordSrvAddon] Warning: \"" + resourceName + "\" was made for a newer version of Minecraft!", senders);
                            } else if (info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) < 0) {
                                sendMessage(ChatColor.YELLOW + "[ICDiscordSrvAddon] Warning: \"" + resourceName + "\" was made for an older version of Minecraft!", senders);
                            }
                        } else {
                            if (info.getRejectedReason() == null) {
                                sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to load \"" + resourceName + "\"", senders);
                            } else {
                                sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to load \"" + resourceName + "\", Reason: " + info.getRejectedReason(), senders);
                            }
                        }
                    } catch (Exception e) {
                        sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to load \"" + resourceName + "\"", senders);
                        e.printStackTrace();
                    }
                }
                if (includeServerResourcePack && serverResourcePack != null && serverResourcePack.exists()) {
                    String resourceName = serverResourcePack.getName();
                    try {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Loading \"" + resourceName + "\" resources...");
                        ResourcePackInfo info = resourceManager.loadResources(serverResourcePack, ResourcePackType.SERVER);
                        if (info.getStatus()) {
                            if (info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) > 0) {
                                sendMessage(ChatColor.YELLOW + "[ICDiscordSrvAddon] Warning: \"" + resourceName + "\" was made for a newer version of Minecraft!", senders);
                            } else if (info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) < 0) {
                                sendMessage(ChatColor.YELLOW + "[ICDiscordSrvAddon] Warning: \"" + resourceName + "\" was made for an older version of Minecraft!", senders);
                            }
                        } else {
                            if (info.getRejectedReason() == null) {
                                sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to load \"" + resourceName + "\"", senders);
                            } else {
                                sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to load \"" + resourceName + "\", Reason: " + info.getRejectedReason(), senders);
                            }
                        }
                    } catch (Exception e) {
                        sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to load \"" + resourceName + "\"", senders);
                        e.printStackTrace();
                    }
                }

                Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                    InteractiveChatDiscordSrvAddon.plugin.resourceManager = resourceManager;

                    if (resourceManager.getResourcePackInfo().stream().allMatch(each -> each.getStatus())) {
                        sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Loaded all resources!", senders);
                        isReady = true;
                    } else {
                        sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] There is a problem while loading resources.", senders);
                    }
                    return null;
                }).get();

                resourceReloadLock.unlock();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }

    public void sendMessage(String message, CommandSender... senders) {
        for (CommandSender sender : senders) {
            sender.sendMessage(message);
        }
    }

}
