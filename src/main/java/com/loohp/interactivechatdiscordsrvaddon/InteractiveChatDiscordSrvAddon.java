package com.loohp.interactivechatdiscordsrvaddon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.config.Config;
import com.loohp.interactivechat.registry.Registry;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ColorUtils;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechatdiscordsrvaddon.debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageGeneration;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.graphics.MCFont;
import com.loohp.interactivechatdiscordsrvaddon.listeners.DiscordReadyEvents;
import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents;
import com.loohp.interactivechatdiscordsrvaddon.listeners.OutboundToDiscordEvents;
import com.loohp.interactivechatdiscordsrvaddon.metrics.Charts;
import com.loohp.interactivechatdiscordsrvaddon.metrics.Metrics;
import com.loohp.interactivechatdiscordsrvaddon.registies.InteractiveChatRegistry;
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
	
	private List<String> resourceOrder = new ArrayList<>();
	
	private Map<String, BufferedImage> blocks = new HashMap<>();
	private Map<String, BufferedImage> items = new HashMap<>();
	private Map<String, BufferedImage> misc = new HashMap<>();
	private Map<String, BufferedImage> gui = new HashMap<>();
	private Map<String, BufferedImage> banner = new HashMap<>();	
	private Map<String, BufferedImage> font = new HashMap<>();
	private Map<String, BufferedImage> puppet = new HashMap<>();
	private Map<String, BufferedImage> armor = new HashMap<>();
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

		Config.loadConfig(CONFIG_ID, new File(getDataFolder(), "config.yml"), getClass().getClassLoader().getResourceAsStream("config.yml"), getClass().getClassLoader().getResourceAsStream("config.yml"), true);
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
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] VERSION NOT COMPATIBLE WITH INTERACTIVECHAT, PLEASE UPDATE!!!!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] VERSION NOT COMPATIBLE WITH INTERACTIVECHAT, PLEASE UPDATE!!!!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] VERSION NOT COMPATIBLE WITH INTERACTIVECHAT, PLEASE UPDATE!!!!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] VERSION NOT COMPATIBLE WITH INTERACTIVECHAT, PLEASE UPDATE!!!!");
			getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] VERSION NOT COMPATIBLE WITH INTERACTIVECHAT, PLEASE UPDATE!!!!");
		} else {
			getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ICDiscordSrvAddon] InteractiveChat DiscordSRV Addon has been Enabled!");
		}
		
		reloadTextures(false);
		/*
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.HIGHEST, PacketType.Play.Server.ENTITY_EQUIPMENT) {
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet2 = event.getPacket();
				System.out.println(packet2.getIntegers().read(0));
				System.out.println(packet2.getIntegers().read(1));
				System.out.println(packet2.getItemModifier().read(0));
			}
		});
		*/
	}
	
	@Override
	public void onDisable() {
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
		
		debug = config.getConfiguration().getBoolean("Debug.PrintInfoToConsole");
		
		resourceOrder.clear();
		List<String> order = config.getConfiguration().getStringList("Resources.Order");
		ListIterator<String> itr = order.listIterator(order.size());
		resourceOrder.add("assets");
		while (itr.hasPrevious()) {
			String pack = itr.previous();
			resourceOrder.add("resources/" + pack);
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
	
	public boolean hasBlockTexture(String str) {
		return blocks.get(str) != null;
	}
	
	public BufferedImage getBlockTexture(String str) {
		BufferedImage image = blocks.get(str);
		if (image == null) {
			return ImageGeneration.getMissingImage(32, 32);
		}
		return ImageUtils.copyImage(image);
	}
	
	public boolean hasItemTexture(String str) {
		return items.get(str) != null;
	}
	
	public BufferedImage getItemTexture(String str) {
		BufferedImage image = items.get(str);
		if (image == null) {
			return ImageGeneration.getMissingImage(32, 32);
		}
		return ImageUtils.copyImage(image);
	}
	
	public boolean hasFontTexture(String str) {
		return font.get(str) != null;
	}
	
	public BufferedImage getFontTexture(String str) {
		BufferedImage image = font.get(str);
		if (image == null) {
			return ImageGeneration.getMissingImage(14, 14);
		}
		return ImageUtils.copyImage(image);
	}
	
	public boolean hasMiscTexture(String str) {
		return misc.get(str) != null;
	}
	
	public BufferedImage getMiscTexture(String str) {
		BufferedImage image = misc.get(str);
		if (image == null) {
			return ImageGeneration.getMissingImage(512, 512);
		}
		return ImageUtils.copyImage(image);
	}
	
	public boolean hasGUITexture(String str) {
		return gui.get(str) != null;
	}
	
	public BufferedImage getGUITexture(String str) {
		BufferedImage image = gui.get(str);
		if (image == null) {
			return ImageGeneration.getMissingImage(512, 512);
		}
		return ImageUtils.copyImage(image);
	}
	
	public boolean hasBannerTexture(String str) {
		return banner.get(str) != null;
	}
	
	public BufferedImage getBannerTexture(String str) {
		BufferedImage image = banner.get(str);
		if (image == null) {
			return ImageGeneration.getMissingImage(512, 512);
		}
		return ImageUtils.copyImage(image);
	}
	
	public boolean hasPuppetTexture(String str) {
		return puppet.get(str) != null;
	}
	
	public BufferedImage getPuppetTexture(String str) {
		BufferedImage image = puppet.get(str);
		if (image == null) {
			return ImageGeneration.getMissingImage(512, 512);
		}
		return ImageUtils.copyImage(image);
	}
	
	public boolean hasArmorTexture(String str) {
		return armor.get(str) != null;
	}
	
	public BufferedImage getArmorTexture(String str) {
		BufferedImage image = armor.get(str);
		if (image == null) {
			return ImageGeneration.getMissingImage(512, 512);
		}
		return ImageUtils.copyImage(image);
	}
	
	public byte[] getExtras(String str) {
		return extras.get(str);
	}
	
	public void reloadTextures(boolean redownload) {
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			try {
				AssetsDownloader.loadAssets(getDataFolder(), redownload);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[ICDiscordSrvAddon] Loading textures...");
			Map<String, BufferedImage> blocks = new HashMap<>();
			Map<String, BufferedImage> items = new HashMap<>();
			Map<String, BufferedImage> font = new HashMap<>();
			Map<String, BufferedImage> misc = new HashMap<>();
			Map<String, BufferedImage> gui = new HashMap<>();
			Map<String, BufferedImage> banner = new HashMap<>();
			Map<String, BufferedImage> puppet = new HashMap<>();
			Map<String, BufferedImage> armor = new HashMap<>();
			
			for (String folder : resourceOrder) {
				for (File file : new File(getDataFolder() + "/" + folder + "/blocks/").listFiles()) {
					if (!file.exists() || file.isDirectory()) {
						continue;
					}
					try {
						BufferedImage item_ori = ImageIO.read(file);
						
						if (item_ori == null) {
							continue;
						}
						
						item_ori = ImageUtils.squarify(item_ori);
						
						BufferedImage itemImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
						Graphics2D g = itemImage.createGraphics();
						g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
						g.drawImage(item_ori, 0, 0, 32, 32, null);
						g.dispose();
						
						String name = file.getName();
						int lastDot = name.lastIndexOf(".");
						if (lastDot >= 0) {
							name = name.substring(0, lastDot);
						}
						
						blocks.put(name, itemImage);
					} catch (IOException e) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while loading " + file.getPath());
						e.printStackTrace();
					}
				}
				
				for (File file : new File(getDataFolder() + "/" + folder + "/items/").listFiles()) {
					if (!file.exists() || file.isDirectory()) {
						continue;
					}
					try {
						BufferedImage item_ori = ImageIO.read(file);
						
						if (item_ori == null) {
							continue;
						}
						
						BufferedImage itemImage = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
						Graphics2D g = itemImage.createGraphics();
						g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
						g.drawImage(item_ori, 0, 0, 32, 32, null);
						g.dispose();
						
						String name = file.getName();
						int lastDot = name.lastIndexOf(".");
						if (lastDot >= 0) {
							name = name.substring(0, lastDot);
						}
						
						items.put(name, itemImage);
					} catch (IOException e) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while loading " + file.getPath());
						e.printStackTrace();
					}
				}
				
				for (File file : new File(getDataFolder() + "/" + folder + "/font/").listFiles()) {
					if (!file.exists() || file.isDirectory()) {
						continue;
					}
					try {
						BufferedImage font_ori = ImageIO.read(file);
						
						if (font_ori == null) {
							continue;
						}
						
						BufferedImage fontImage = new BufferedImage(14, 14, BufferedImage.TYPE_INT_ARGB);
						Graphics2D g = fontImage.createGraphics();
						g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
						g.drawImage(font_ori, 0, 0, 14, 14, null);
						g.dispose();
						
						String name = file.getName();
						int lastDot = name.lastIndexOf(".");
						if (lastDot >= 0) {
							name = name.substring(0, lastDot);
						}
						
						font.put(name, fontImage);
					} catch (IOException e) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while loading " + file.getPath());
						e.printStackTrace();
					}
				}
				
				for (File file : new File(getDataFolder() + "/" + folder + "/misc/").listFiles()) {
					if (!file.exists() || file.isDirectory()) {
						continue;
					}
					try {
						BufferedImage miscImage = ImageIO.read(file);
						
						if (miscImage == null) {
							continue;
						}
						
						String name = file.getName();
						int lastDot = name.lastIndexOf(".");
						if (lastDot >= 0) {
							name = name.substring(0, lastDot);
						}
						
						misc.put(name, miscImage);
					} catch (IOException e) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while loading " + file.getPath());
						e.printStackTrace();
					}
				}
				
				for (File file : new File(getDataFolder() + "/" + folder + "/gui/").listFiles()) {
					if (!file.exists() || file.isDirectory()) {
						continue;
					}
					try {
						BufferedImage guiImage = ImageIO.read(file);
						
						if (guiImage == null) {
							continue;
						}
						
						String name = file.getName();
						int lastDot = name.lastIndexOf(".");
						if (lastDot >= 0) {
							name = name.substring(0, lastDot);
						}
						
						gui.put(name, guiImage);
					} catch (IOException e) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while loading " + file.getPath());
						e.printStackTrace();
					}
				}
				
				for (File file : new File(getDataFolder() + "/" + folder + "/banner/").listFiles()) {
					if (!file.exists() || file.isDirectory()) {
						continue;
					}
					try {
						BufferedImage guiImage = ImageIO.read(file);
						
						if (guiImage == null) {
							continue;
						}
						
						String name = file.getName();
						int lastDot = name.lastIndexOf(".");
						if (lastDot >= 0) {
							name = name.substring(0, lastDot);
						}
						
						banner.put(name, guiImage);
					} catch (IOException e) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while loading " + file.getPath());
						e.printStackTrace();
					}
				}
				
				for (File file : new File(getDataFolder() + "/" + folder + "/puppet/").listFiles()) {
					if (!file.exists() || file.isDirectory()) {
						continue;
					}
					try {
						BufferedImage guiImage = ImageIO.read(file);
						
						if (guiImage == null) {
							continue;
						}
						
						String name = file.getName();
						int lastDot = name.lastIndexOf(".");
						if (lastDot >= 0) {
							name = name.substring(0, lastDot);
						}
						
						puppet.put(name, guiImage);
					} catch (IOException e) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while loading " + file.getPath());
						e.printStackTrace();
					}
				}
				
				for (File file : new File(getDataFolder() + "/" + folder + "/armor/").listFiles()) {
					if (!file.exists() || file.isDirectory()) {
						continue;
					}
					try {
						BufferedImage guiImage = ImageIO.read(file);
						
						if (guiImage == null) {
							continue;
						}
						
						String name = file.getName();
						int lastDot = name.lastIndexOf(".");
						if (lastDot >= 0) {
							name = name.substring(0, lastDot);
						}
						
						armor.put(name, guiImage);
					} catch (IOException e) {
						Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error while loading " + file.getPath());
						e.printStackTrace();
					}
				}
			}
			
			Bukkit.getScheduler().runTask(plugin, () -> {
				InteractiveChatDiscordSrvAddon.plugin.blocks = blocks;
				InteractiveChatDiscordSrvAddon.plugin.items = items;
				InteractiveChatDiscordSrvAddon.plugin.font = font;
				InteractiveChatDiscordSrvAddon.plugin.misc = misc;
				InteractiveChatDiscordSrvAddon.plugin.gui = gui;
				InteractiveChatDiscordSrvAddon.plugin.banner = banner;
				InteractiveChatDiscordSrvAddon.plugin.puppet = puppet;
				InteractiveChatDiscordSrvAddon.plugin.armor = armor;
				
				if (!MCFont.reloadFonts()) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[ICDiscordSrvAddon] As Fonts failed to load, features that requires it will be disabled.");
					itemUseTooltipImage = false;
					hoverUseTooltipImage = false;
				}
				
				Cache.clearAllCache();
				
				int total = blocks.size() + items.size() + font.size() + misc.size() + gui.size() + banner.size() + puppet.size() + armor.size();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[ICDiscordSrvAddon] Loaded " + total + " textures!");
			});
		});
	}

}
