package com.loohp.interactivechatdiscordsrvaddon;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.config.Config;
import com.loohp.interactivechat.libs.org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import com.loohp.interactivechat.libs.org.simpleyaml.exceptions.InvalidConfigurationException;
import com.loohp.interactivechat.objectholders.PlaceholderCooldownManager;
import com.loohp.interactivechat.registry.Registry;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ColorUtils;
import com.loohp.interactivechat.utils.FileUtils;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechatdiscordsrvaddon.debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.listeners.DiscordReadyEvents;
import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents;
import com.loohp.interactivechatdiscordsrvaddon.listeners.OutboundToDiscordEvents;
import com.loohp.interactivechatdiscordsrvaddon.metrics.Charts;
import com.loohp.interactivechatdiscordsrvaddon.metrics.Metrics;
import com.loohp.interactivechatdiscordsrvaddon.registies.InteractiveChatRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resource.ModelRenderer;
import com.loohp.interactivechatdiscordsrvaddon.resource.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.updater.Updater;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import net.md_5.bungee.api.ChatColor;

public class InteractiveChatDiscordSrvAddon extends JavaPlugin {
	
	public static final int BSTATS_PLUGIN_ID = 8863;
	public static final String CONFIG_ID = "interactivechatdiscordsrvaddon_config";
	
	public static InteractiveChatDiscordSrvAddon plugin;
	public static InteractiveChat interactivechat;
	public static DiscordSRV discordsrv;
	
	public static boolean isReady = false;
	
	public static boolean debug = false;
	
	public static List<Permission> requiredPermissions;
	
	static {
		List<Permission> requiredPerms = new ArrayList<>();
		requiredPerms.add(Permission.MESSAGE_READ);
		requiredPerms.add(Permission.MESSAGE_WRITE);
		requiredPerms.add(Permission.MESSAGE_MANAGE);
		requiredPerms.add(Permission.MESSAGE_EMBED_LINKS);
		requiredPerms.add(Permission.MESSAGE_ATTACH_FILES);
		requiredPerms.add(Permission.MANAGE_WEBHOOKS);
		requiredPermissions = Collections.unmodifiableList(requiredPerms);
	}
	
	public Metrics metrics;
	public AtomicLong messagesCounter = new AtomicLong(0);
	public AtomicLong imageCounter = new AtomicLong(0);
	public AtomicLong inventoryImageCounter = new AtomicLong(0);
	public AtomicLong attachmentCounter = new AtomicLong(0);
	public AtomicLong attachmentImageCounter = new AtomicLong(0);
	public AtomicLong imagesViewedCounter = new AtomicLong(0);
	
	public boolean itemImage = true;
	public boolean invImage = true;
	public boolean enderImage = true;
	
	public boolean usePlayerInvView = true;
	
	public String itemDisplaySingle = "";
	public String itemDisplayMultiple = "";
	public Color invColor = Color.black;
	public Color enderColor = Color.black;
	
	public boolean itemUseTooltipImage = true;
	public boolean itemUseTooltipImageOnBaseItem = false;
	public boolean itemAltAir = true;
	
	public boolean invShowLevel = true;
	
	public boolean hoverEnabled = true;
	public boolean hoverImage = true;
	public Set<Integer> hoverIngore = new HashSet<>();
	public boolean hoverUseTooltipImage = true;
	
	public String reloadConfigMessage;
	public String reloadTextureMessage;
	public String linkExpired;
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
	public boolean discordAttachmentsUseMaps = true;
	public int discordAttachmentTimeout = 0;
	public String discordAttachmentsFormattingImageAppend;
	public String discordAttachmentsFormattingImageAppendHover;
	
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
	
	public boolean playerlistCommandEnabled = true;
	public String playerlistCommandDescription = "";
	public boolean playerlistCommandIsMainServer = true;
	public boolean playerlistCommandBungeecord = true;
	public int playerlistCommandDeleteAfter = 10;
	public String playerlistCommandPlayerFormat = "";
	public boolean playerlistCommandAvatar = true;
	public boolean playerlistCommandPing = true;
	public String playerlistCommandHeader = "";
	public String playerlistCommandFooter = "";
	public String playerlistCommandEmptyServer = "";
	public Color playerlistCommandColor = new Color(153, 153, 153);
	
	public boolean shareInvCommandEnabled = true;
	public boolean shareInvCommandIsMainServer = true;
	public String shareInvCommandInGameMessageText = "";
	public String shareInvCommandInGameMessageHover = "";
	public String shareInvCommandTitle = "";
	public String shareInvCommandSkullName = "";
	
	public boolean shareEnderCommandEnabled = true;
	public boolean shareEnderCommandIsMainServer = true;
	public String shareEnderCommandInGameMessageText = "";
	public String shareEnderCommandInGameMessageHover = "";
	public String shareEnderCommandTitle = "";
	
	public PlaceholderCooldownManager placeholderCooldownManager = new PlaceholderCooldownManager();
	
	public String defaultResourceHash = "N/A";
	public List<String> resourceOrder = new ArrayList<>();
	public Map<String, Boolean> resourceStatus = new LinkedHashMap<>();
	public ResourceManager resourceManager;
	public ModelRenderer modelRenderer;
	
	protected Map<String, byte[]> extras = new HashMap<>();
	
	@Override
	public void onEnable() {
		plugin = this;
		interactivechat = InteractiveChat.plugin;
		discordsrv = DiscordSRV.getPlugin();
		
		//Rename old folder
		File pluginFolder = new File(Bukkit.getWorldContainer(), "plugins");
		if (pluginFolder.exists() && pluginFolder.isDirectory()) {
			for (File file : pluginFolder.listFiles()) {
				if (file.isDirectory() && file.getName().equals("InteractiveChatDiscordSRVAddon")) {
					file.renameTo(new File(pluginFolder, getName()));
				}
			}
		}
		
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}

		try {
			Config.loadConfig(CONFIG_ID, new File(getDataFolder(), "config.yml"), getClass().getClassLoader().getResourceAsStream("config.yml"), getClass().getClassLoader().getResourceAsStream("config.yml"), true);
		} catch (IOException | InvalidConfigurationException e) {
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
		
		getServer().getPluginManager().registerEvents(new InboundToGameEvents(), this);
		getServer().getPluginManager().registerEvents(new OutboundToDiscordEvents(), this);
		getServer().getPluginManager().registerEvents(new Debug(), this);
		getServer().getPluginManager().registerEvents(new Updater(), this);
		getCommand("interactivechatdiscordsrv").setExecutor(new Commands());
		
		File resources = new File(getDataFolder(), "resources");
		if (!resources.exists()) {
			resources.mkdirs();
		}
		
		if (!compatible()) {
			for (int i = 0; i < 10; i++) {
				getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] VERSION NOT COMPATIBLE WITH INSTALLED INTERACTIVECHAT VERSION, PLEASE UPDATE BOTH TO LATEST!!!!");
			}
			getServer().getPluginManager().disablePlugin(this);
		} else {
			getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ICDiscordSrvAddon] InteractiveChat DiscordSRV Addon has been Enabled!");
		}
		
		reloadTextures(false);
		modelRenderer = new ModelRenderer(8);
	}
	
	@Override
	public void onDisable() {
		modelRenderer.close();
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] InteractiveChat DiscordSRV Addon has been Disabled!");
	}
	
	@SuppressWarnings("all")
	public boolean compatible() {
		return Registry.INTERACTIVE_CHAT_DISCORD_SRV_ADDON_COMPATIBLE_VERSION == InteractiveChatRegistry.INTERACTIVE_CHAT_DISCORD_SRV_ADDON_COMPATIBLE_VERSION;
	}
	
	@Override
	public void reloadConfig() {
		Config config = Config.getConfig(CONFIG_ID);
		config.reload();
		
		reloadConfigMessage = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.ReloadConfig"));
		reloadTextureMessage = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.ReloadTexture"));
		linkExpired = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.LinkExpired"));
		accountNotLinked = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.AccountNotLinked"));
		unableToRetrieveData = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.UnableToRetrieveData"));
		invalidDiscordChannel = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("Messages.InvalidDiscordChannel"));
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
		
		itemImage = config.getConfiguration().getBoolean("InventoryImage.Item.Enabled");
		invImage = config.getConfiguration().getBoolean("InventoryImage.Inventory.Enabled");
		enderImage = config.getConfiguration().getBoolean("InventoryImage.EnderChest.Enabled");
		
		usePlayerInvView = config.getConfiguration().getBoolean("InventoryImage.Inventory.UsePlayerInventoryView");
		
		itemUseTooltipImage = config.getConfiguration().getBoolean("InventoryImage.Item.UseTooltipImage");
		itemUseTooltipImageOnBaseItem = config.getConfiguration().getBoolean("InventoryImage.Item.UseTooltipImageOnBaseItem");
		itemAltAir = config.getConfiguration().getBoolean("InventoryImage.Item.AlternateAirTexture");
		
		invShowLevel = config.getConfiguration().getBoolean("InventoryImage.Inventory.ShowExperienceLevel");
		
		hoverEnabled = config.getConfiguration().getBoolean("HoverEventDisplay.Enabled");
		hoverImage = config.getConfiguration().getBoolean("HoverEventDisplay.ShowCursorImage");
		hoverIngore.clear();
		hoverIngore = config.getConfiguration().getIntegerList("HoverEventDisplay.IgnoredPlaceholderIndexes").stream().collect(Collectors.toSet());
		
		hoverUseTooltipImage = config.getConfiguration().getBoolean("HoverEventDisplay.UseTooltipImage");
		
		convertDiscordAttachments = config.getConfiguration().getBoolean("DiscordAttachments.Convert");
		discordAttachmentsFormattingText = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordAttachments.Formatting.Text"));
		discordAttachmentsFormattingHoverEnabled = config.getConfiguration().getBoolean("DiscordAttachments.Formatting.Hover.Enabled");
		discordAttachmentsFormattingHoverText = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getStringList("DiscordAttachments.Formatting.Hover.HoverText").stream().collect(Collectors.joining("\n")));
		discordAttachmentsUseMaps = config.getConfiguration().getBoolean("DiscordAttachments.ShowImageUsingMaps");
		discordAttachmentTimeout = config.getConfiguration().getInt("DiscordAttachments.Timeout") * 20;
		discordAttachmentsFormattingImageAppend = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordAttachments.Formatting.ImageOriginal"));
		discordAttachmentsFormattingImageAppendHover = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getStringList("DiscordAttachments.Formatting.Hover.ImageOriginalHover").stream().collect(Collectors.joining("\n")));
		
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
		
		playerlistCommandEnabled = config.getConfiguration().getBoolean("DiscordCommands.PlayerList.Enabled");
		playerlistCommandDescription = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.PlayerList.Description"));
		playerlistCommandIsMainServer = config.getConfiguration().getBoolean("DiscordCommands.PlayerList.IsMainServer");
		playerlistCommandBungeecord = config.getConfiguration().getBoolean("DiscordCommands.PlayerList.ListBungeecordPlayers");
		playerlistCommandDeleteAfter = config.getConfiguration().getInt("DiscordCommands.PlayerList.DeleteAfter");
		playerlistCommandPlayerFormat = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.PlayerList.TablistOptions.PlayerFormat"));
		playerlistCommandAvatar = config.getConfiguration().getBoolean("DiscordCommands.PlayerList.TablistOptions.ShowPlayerAvatar");
		playerlistCommandPing = config.getConfiguration().getBoolean("DiscordCommands.PlayerList.TablistOptions.ShowPlayerPing");
		playerlistCommandHeader = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getStringList("DiscordCommands.PlayerList.TablistOptions.HeaderText").stream().collect(Collectors.joining("\n")));
		playerlistCommandFooter = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getStringList("DiscordCommands.PlayerList.TablistOptions.FooterText").stream().collect(Collectors.joining("\n")));
		playerlistCommandEmptyServer = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.PlayerList.EmptyServer"));
		playerlistCommandColor = ColorUtils.hex2Rgb(config.getConfiguration().getString("DiscordCommands.PlayerList.TablistOptions.SidebarColor"));
		
		shareInvCommandEnabled = config.getConfiguration().getBoolean("DiscordCommands.ShareInventory.Enabled");
		shareInvCommandIsMainServer = config.getConfiguration().getBoolean("DiscordCommands.ShareInventory.IsMainServer");
		shareInvCommandInGameMessageText = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.ShareInventory.InGameMessage.Text"));
		shareInvCommandInGameMessageHover = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getStringList("DiscordCommands.ShareInventory.InGameMessage.Hover").stream().collect(Collectors.joining("\n")));
		shareInvCommandTitle = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.ShareInventory.InventoryTitle"));
		shareInvCommandSkullName = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.ShareInventory.SkullDisplayName"));
		
		shareEnderCommandEnabled = config.getConfiguration().getBoolean("DiscordCommands.ShareEnderChest.Enabled");
		shareEnderCommandIsMainServer = config.getConfiguration().getBoolean("DiscordCommands.ShareEnderChest.IsMainServer");
		shareEnderCommandInGameMessageText = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.ShareEnderChest.InGameMessage.Text"));
		shareEnderCommandInGameMessageHover = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getStringList("DiscordCommands.ShareEnderChest.InGameMessage.Hover").stream().collect(Collectors.joining("\n")));
		shareEnderCommandTitle = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordCommands.ShareEnderChest.InventoryTitle"));
		
		language = config.getConfiguration().getString("Resources.Language");
		
		LanguageUtils.loadTranslations(language);
		
		discordsrv.reloadRegexes();
	}
	
	public byte[] getExtras(String str) {
		return extras.get(str);
	}
	
	public void reloadTextures(boolean redownload, CommandSender... receivers) {
		isReady = false;
		CommandSender[] senders;
		if (Stream.of(receivers).noneMatch(each -> each.equals(Bukkit.getConsoleSender()))) {
			senders = new CommandSender[receivers.length + 1];
			for (int i = 0; i < receivers.length; i++) {
				senders[i] = receivers[i];
			}
			senders[senders.length - 1] = Bukkit.getConsoleSender();
		} else {
			senders = receivers;
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			try {
				AssetsDownloader.loadAssets(getDataFolder(), redownload, receivers);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Map<String, Boolean> resourceStatus = new LinkedHashMap<>();
			List<String> resourceList = new ArrayList<>();
			resourceList.add("Default");
			resourceList.addAll(resourceOrder);
			sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Reloading ResourceManager: " + ChatColor.YELLOW + String.join(", ", resourceList), senders);
			
			ResourceManager resourceManager = new ResourceManager();
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Loading \"Default\" resources...");
			resourceManager.loadResources(new File(getDataFolder(), "assets"));
			resourceStatus.put("Default", true);
			for (String resourceName : resourceOrder) {
				try {
					Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Loading \"" + resourceName + "\" resources...");
					File resourceFile = new File(getDataFolder(), "resources/" + resourceName);
					File assetsFolder;
					if (extractIfNotFound(resourceFile) && (assetsFolder = new File(resourceFile, "assets")).exists() && assetsFolder.isDirectory()) {
						resourceManager.loadResources(assetsFolder);
						resourceStatus.put(resourceName, true);
					}
				} catch (Exception e) {
					sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to load \"" + resourceName + "\"", senders);
					resourceStatus.put(resourceName, false);
					e.printStackTrace();
				}
			}
			
			Bukkit.getScheduler().runTask(plugin, () -> {
				InteractiveChatDiscordSrvAddon.plugin.resourceManager = resourceManager;
				InteractiveChatDiscordSrvAddon.plugin.resourceStatus = resourceStatus;
				
				Cache.clearAllCache();
				
				if (resourceStatus.values().stream().allMatch(each -> each)) {
					sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Loaded all resources!", senders);
					isReady = true;
				} else {
					sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] There is a problem while loading resources.", senders);
				}
			});
		});
	}
	
	public void sendMessage(String message, CommandSender... senders) {
		for (CommandSender sender : senders) {
			sender.sendMessage(message);
		}
	}
	
	public boolean extractIfNotFound(File resourceFile) throws Exception {
		if (resourceFile.exists()) {
			return true;
		} else {
			resourceFile.mkdirs();
			File zipFile = new File(resourceFile.getParent(), resourceFile.getName() + ".zip");
			if (zipFile.exists()) {
				try (ZipArchiveInputStream zip = new ZipArchiveInputStream(new FileInputStream(zipFile), StandardCharsets.UTF_8.toString(), false, true, true)) {
					while (true) {
						ZipEntry entry = zip.getNextZipEntry();
						if (entry == null) {
							break;
						}
						String name = entry.getName();
						if (entry.isDirectory()) {
							File folder = new File(resourceFile, name).getParentFile();
							folder.mkdirs();
						} else {
							String fileName = getEntryName(name);
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							byte[] byteChunk = new byte[4096];
							int n;
							while ((n = zip.read(byteChunk)) > 0) {
								baos.write(byteChunk, 0, n);
							}
							byte[] currentEntry = baos.toByteArray();
							
							File folder = new File(resourceFile, name).getParentFile();
							folder.mkdirs();
							File file = new File(folder, fileName);
							if (file.exists()) {
								file.delete();
							}
							FileUtils.copy(new ByteArrayInputStream(currentEntry), file);
						}
					}
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			} else {
				return false;
			}
		}
	}
	
	private static String getEntryName(String name) {
		int pos = name.lastIndexOf("/");
		if (pos >= 0) {
			return name.substring(pos + 1);
		}
		pos = name.lastIndexOf("\\");
		if (pos >= 0) {
			return name.substring(pos + 1);
		}
		return name;
	}

}
