package com.loohp.interactivechatdiscordsrvaddon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.Utils.ChatColorUtils;
import com.loohp.interactivechatdiscordsrvaddon.Debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.Graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.Listeners.DiscordAttachmentEvents;
import com.loohp.interactivechatdiscordsrvaddon.Listeners.DiscordReadyEvents;
import com.loohp.interactivechatdiscordsrvaddon.Listeners.PlaceholderImageEvents;
import com.loohp.interactivechatdiscordsrvaddon.Metrics.Charts;
import com.loohp.interactivechatdiscordsrvaddon.Metrics.Metrics;
import com.loohp.interactivechatdiscordsrvaddon.Updater.Updater;
import com.loohp.interactivechatdiscordsrvaddon.Utils.ColorUtils;
import com.loohp.interactivechatdiscordsrvaddon.Utils.JarUtils;
import com.loohp.interactivechatdiscordsrvaddon.Utils.JarUtils.CopyOption;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import net.md_5.bungee.api.ChatColor;

public class InteractiveChatDiscordSrvAddon extends JavaPlugin {
	
	public static InteractiveChatDiscordSrvAddon plugin;
	public static InteractiveChat interactivechat;
	public static DiscordSRV discordsrv;
	
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
	
	public boolean itemImage = true;
	public boolean invImage = true;
	public boolean enderImage = true;
	
	public boolean usePlayerInvView = true;
	
	public String itemDisplaySingle = "";
	public String itemDisplayMultiple = "";
	public Color invColor = Color.black;
	public Color enderColor = Color.black;
	
	public String reloadConfigMessage;
	public String reloadTextureMessage;
	public String linkExpired;
	
	public boolean convertDiscordAttachments = true;
	public String discordAttachmentsFormattingText;
	public boolean discordAttachmentsFormattingHoverEnabled = true;
	public String discordAttachmentsFormattingHoverText;
	public boolean discordAttachmentsUseMaps = true;
	public int discordAttachmentTimeout = 0;
	public String discordAttachmentsFormattingImageAppend;
	public String discordAttachmentsFormattingImageAppendHover;
	
	public boolean UpdaterEnabled = true;
	
	public int cacheTimeout = 1200;
	
	public boolean escapePlaceholdersFromDiscord = true;
	public boolean escapeDiscordMarkdownInItems = true;
	
	private ConfigurationSection translations;
	private Map<String, String> modernItemTranslations = new HashMap<>();
	
	private List<String> resourceOrder = new ArrayList<>();
	
	private Map<String, BufferedImage> blocks = new HashMap<>();
	private Map<String, BufferedImage> items = new HashMap<>();
	private Map<String, BufferedImage> misc = new HashMap<>();
	private Map<String, BufferedImage> gui = new HashMap<>();
	private Map<String, BufferedImage> banner = new HashMap<>();	
	private Map<String, BufferedImage> font = new HashMap<>();
	private Map<String, BufferedImage> puppet = new HashMap<>();
	
	@Override
	public void onEnable() {
		plugin = this;
		interactivechat = InteractiveChat.plugin;
		discordsrv = DiscordSRV.getPlugin();

		getConfig().options().copyDefaults(true);
		saveConfig();
		
		File file = new File(getDataFolder(), "lang.json"); 
        if (!file.exists() && !InteractiveChat.version.isLegacy()) {
            try (InputStream in = this.getClassLoader().getResourceAsStream("lang.json")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                getLogger().severe("[ICDiscordSRVAddon] Unable to copy lang.json");
            }
        }
		
		reloadConfig();
		
		int pluginId = 8863;
		metrics = new Metrics(this, pluginId);
		Charts.setup(metrics);
		
		DiscordSRV.api.subscribe(new DiscordReadyEvents());
		DiscordSRV.api.subscribe(new PlaceholderImageEvents());
		DiscordSRV.api.subscribe(new DiscordAttachmentEvents());
		
		getServer().getPluginManager().registerEvents(new DiscordAttachmentEvents(), this);
		getServer().getPluginManager().registerEvents(new Debug(), this);
		getServer().getPluginManager().registerEvents(new Updater(), this);
		getCommand("interactivechatdiscordsrv").setExecutor(new Commands());
		
		try {
			JarUtils.copyFolderFromJar("assets", getDataFolder(), CopyOption.REPLACE_IF_EXIST);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File resources = new File(getDataFolder(), "resources");
		if (!resources.exists()) {
			resources.mkdirs();
		}
		
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ICDiscordSRVAddon] InteractiveChat DiscordSRV Addon has been Enabled!");
		
		reloadTextures();
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
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSRVAddon] InteractiveChat DiscordSRV Addon has been Disabled!");
	}
	
	@Override
	public void reloadConfig() {
		super.reloadConfig();
		
		reloadConfigMessage = ChatColorUtils.translateAlternateColorCodes('&', getConfig().getString("Messages.ReloadConfig"));
		reloadTextureMessage = ChatColorUtils.translateAlternateColorCodes('&', getConfig().getString("Messages.ReloadTexture"));
		linkExpired = ChatColorUtils.translateAlternateColorCodes('&', getConfig().getString("Messages.LinkExpired"));
		
		resourceOrder.clear();
		List<String> order = getConfig().getStringList("Resources.Order");
		ListIterator<String> itr = order.listIterator(order.size());
		resourceOrder.add("assets");
		while (itr.hasPrevious()) {
			String pack = itr.previous();
			resourceOrder.add("resources/" + pack);
		}
		
		itemImage = getConfig().getBoolean("InventoryImage.Item.Enabled");
		invImage = getConfig().getBoolean("InventoryImage.Inventory.Enabled");
		enderImage = getConfig().getBoolean("InventoryImage.EnderChest.Enabled");
		
		usePlayerInvView = getConfig().getBoolean("InventoryImage.Inventory.UsePlayerInventoryView");
		
		convertDiscordAttachments = getConfig().getBoolean("DiscordAttachments.Convert");
		discordAttachmentsFormattingText = ChatColorUtils.translateAlternateColorCodes('&', getConfig().getString("DiscordAttachments.Formatting.Text"));
		discordAttachmentsFormattingHoverEnabled = getConfig().getBoolean("DiscordAttachments.Formatting.Hover.Enabled");
		discordAttachmentsFormattingHoverText = ChatColorUtils.translateAlternateColorCodes('&', getConfig().getStringList("DiscordAttachments.Formatting.Hover.HoverText").stream().collect(Collectors.joining("\n")));
		discordAttachmentsUseMaps = getConfig().getBoolean("DiscordAttachments.ShowImageUsingMaps");
		discordAttachmentTimeout = getConfig().getInt("DiscordAttachments.Timeout") * 20;
		discordAttachmentsFormattingImageAppend = ChatColorUtils.translateAlternateColorCodes('&', getConfig().getString("DiscordAttachments.Formatting.ImageOriginal"));
		discordAttachmentsFormattingImageAppendHover = ChatColorUtils.translateAlternateColorCodes('&', getConfig().getStringList("DiscordAttachments.Formatting.Hover.ImageOriginalHover").stream().collect(Collectors.joining("\n")));
		
		UpdaterEnabled = getConfig().getBoolean("Options.UpdaterEnabled");
		
		cacheTimeout = getConfig().getInt("Settings.CacheTimeout") * 20;
		
		escapePlaceholdersFromDiscord = getConfig().getBoolean("Settings.EscapePlaceholdersSentFromDiscord");
		escapeDiscordMarkdownInItems = getConfig().getBoolean("Settings.EscapeDiscordMarkdownFormattingInItems");
		
		itemDisplaySingle = getConfig().getString("InventoryImage.Item.EmbedDisplay.Single");
		itemDisplayMultiple = getConfig().getString("InventoryImage.Item.EmbedDisplay.Multiple");		
		invColor = ColorUtils.hex2Rgb(getConfig().getString("InventoryImage.Inventory.EmbedColor"));
		enderColor = ColorUtils.hex2Rgb(getConfig().getString("InventoryImage.EnderChest.EmbedColor"));
		
		translations = getConfig().getConfigurationSection("Translations");
		
		modernItemTranslations.clear();
		
		File lang = new File(getDataFolder(), "lang.json");
		if (!InteractiveChat.version.isLegacy() && lang.exists()) {
			try {
				JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(lang));
				for (Object obj : json.keySet()) {
					try {
						String key = (String) obj;
						modernItemTranslations.put(key, (String) json.get(key));
					} catch (Exception e) {}
				}
			} catch (IOException | ParseException e) {
				try (InputStream in = this.getClassLoader().getResourceAsStream("lang.json")) {
	                Files.copy(in, lang.toPath());
	            } catch (IOException e1) {
	                getLogger().severe("[ICDiscordSRVAddon] Unable to copy lang.json");
	            }
			}
		}		
	}
	
	public String getModernItemTrans(String key) {
		return modernItemTranslations.getOrDefault(key, key);
	}
	
	public ConfigurationSection getTrans() {
		return translations;
	}
	
	public BufferedImage getBlockTexture(String str) {
		BufferedImage image = blocks.get(str);
		if (image == null) {
			return null;
		}
		return ImageUtils.copyImage(image);
	}
	
	public BufferedImage getItemTexture(String str) {
		BufferedImage image = items.get(str);
		if (image == null) {
			return null;
		}
		return ImageUtils.copyImage(image);
	}
	
	public BufferedImage getFontTexture(String str) {
		BufferedImage image = font.get(str);
		if (image == null) {
			return null;
		}
		return ImageUtils.copyImage(image);
	}
	
	public BufferedImage getMiscTexture(String str) {
		BufferedImage image = misc.get(str);
		if (image == null) {
			return null;
		}
		return ImageUtils.copyImage(image);
	}
	
	public BufferedImage getGUITexture(String str) {
		BufferedImage image = gui.get(str);
		if (image == null) {
			return null;
		}
		return ImageUtils.copyImage(image);
	}
	
	public BufferedImage getBannerTexture(String str) {
		BufferedImage image = banner.get(str);
		if (image == null) {
			return null;
		}
		return ImageUtils.copyImage(image);
	}
	
	public BufferedImage getPuppetTexture(String str) {
		BufferedImage image = puppet.get(str);
		if (image == null) {
			return null;
		}
		return ImageUtils.copyImage(image);
	}
	
	public void reloadTextures() {
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[ICDiscordSRVAddon] Loading textures...");
			Map<String, BufferedImage> blocks = new HashMap<>();
			Map<String, BufferedImage> items = new HashMap<>();
			Map<String, BufferedImage> font = new HashMap<>();
			Map<String, BufferedImage> misc = new HashMap<>();
			Map<String, BufferedImage> gui = new HashMap<>();
			Map<String, BufferedImage> banner = new HashMap<>();
			Map<String, BufferedImage> puppet = new HashMap<>();
			
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
			}
			
			Bukkit.getScheduler().runTask(plugin, () -> {
				InteractiveChatDiscordSrvAddon.plugin.blocks = blocks;
				InteractiveChatDiscordSrvAddon.plugin.items = items;
				InteractiveChatDiscordSrvAddon.plugin.font = font;
				InteractiveChatDiscordSrvAddon.plugin.misc = misc;
				InteractiveChatDiscordSrvAddon.plugin.gui = gui;
				InteractiveChatDiscordSrvAddon.plugin.banner = banner;
				InteractiveChatDiscordSrvAddon.plugin.puppet = puppet;
				
				int total = blocks.size() + items.size() + font.size() + misc.size() + gui.size() + banner.size() + puppet.size();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[ICDiscordSRVAddon] Loaded " + total + " textures!");
			});
		});
	}

}
