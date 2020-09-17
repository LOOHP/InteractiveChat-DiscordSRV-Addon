package com.loohp.interactivechatdiscordsrvaddon;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.Utils.ChatColorUtils;
import com.loohp.interactivechatdiscordsrvaddon.Listeners.DiscordSRVEvents;
import com.loohp.interactivechatdiscordsrvaddon.Metrics.Charts;
import com.loohp.interactivechatdiscordsrvaddon.Metrics.Metrics;
import com.loohp.interactivechatdiscordsrvaddon.Updater.Updater;
import com.loohp.interactivechatdiscordsrvaddon.Utils.CustomImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.Utils.JarUtils;
import com.loohp.interactivechatdiscordsrvaddon.Utils.JarUtils.CopyOption;

import github.scarsz.discordsrv.DiscordSRV;

public class InteractiveChatDiscordSrvAddon extends JavaPlugin {
	
	public static InteractiveChatDiscordSrvAddon plugin;
	public static InteractiveChat interactivechat;
	public static DiscordSRV discordsrv;
	
	public Metrics metrics;
	public AtomicLong messagesCounter = new AtomicLong(0);
	public AtomicLong imageCounter = new AtomicLong(0);
	
	public boolean itemImage = true;
	public boolean invImage = true;
	public boolean enderImage = true;
	
	public String reloadConfigMessage;
	public String reloadTextureMessage;
	
	public boolean UpdaterEnabled = true;
	
	private Map<String, BufferedImage> blocks = new HashMap<>();
	private Map<String, BufferedImage> items = new HashMap<>();
	private Map<String, BufferedImage> font = new HashMap<>();
	private Map<String, BufferedImage> misc = new HashMap<>();
	private Map<String, BufferedImage> gui = new HashMap<>();
	private Map<String, BufferedImage> banner = new HashMap<>();
	
	@Override
	public void onEnable() {
		plugin = this;
		interactivechat = InteractiveChat.plugin;
		discordsrv = DiscordSRV.getPlugin();
		
		getConfig().options().copyDefaults(true);
		saveConfig();
		reloadConfig();
		
		int pluginId = 8863;
		metrics = new Metrics(this, pluginId);
		Charts.setup(metrics);
		
		DiscordSRV.api.subscribe(new DiscordSRVEvents());
		
		getServer().getPluginManager().registerEvents(new Updater(), this);
		getCommand("interactivechatdiscordsrv").setExecutor(new Commands());
		
		try {
			JarUtils.copyFolderFromJar("assets", getDataFolder(), CopyOption.COPY_IF_NOT_EXIST);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[ICDiscordSRVAddon] InteractiveChat DiscordSRV Addon has been Enabled!");
		
		reloadTextures();
	}
	
	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSRVAddon] InteractiveChat DiscordSRV Addon has been Disabled!");
	}
	
	public void loadConfig() {
		reloadConfigMessage = ChatColorUtils.translateAlternateColorCodes('&', getConfig().getString("Messages.ReloadConfig"));
		reloadTextureMessage = ChatColorUtils.translateAlternateColorCodes('&', getConfig().getString("Messages.ReloadTexture"));
		
		itemImage = getConfig().getBoolean("InventoryImage.Item.Enabled");
		invImage = getConfig().getBoolean("InventoryImage.Inventory.Enabled");
		enderImage = getConfig().getBoolean("InventoryImage.EnderChest.Enabled");
		
		UpdaterEnabled = getConfig().getBoolean("Options.UpdaterEnabled");
	}
	
	public BufferedImage getBlockTexture(String str) {
		BufferedImage image = blocks.get(str);
		if (image == null) {
			return null;
		}
		return CustomImageUtils.copyImage(image);
	}
	
	public BufferedImage getItemTexture(String str) {
		BufferedImage image = items.get(str);
		if (image == null) {
			return null;
		}
		return CustomImageUtils.copyImage(image);
	}
	
	public BufferedImage getFontTexture(String str) {
		BufferedImage image = font.get(str);
		if (image == null) {
			return null;
		}
		return CustomImageUtils.copyImage(image);
	}
	
	public BufferedImage getMiscTexture(String str) {
		BufferedImage image = misc.get(str);
		if (image == null) {
			return null;
		}
		return CustomImageUtils.copyImage(image);
	}
	
	public BufferedImage getGUITexture(String str) {
		BufferedImage image = gui.get(str);
		if (image == null) {
			return null;
		}
		return CustomImageUtils.copyImage(image);
	}
	
	public BufferedImage getBannerTexture(String str) {
		BufferedImage image = banner.get(str);
		if (image == null) {
			return null;
		}
		return CustomImageUtils.copyImage(image);
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
			
			for (File file : new File(getDataFolder() + "/assets/blocks/").listFiles()) {
				try {
					BufferedImage item_ori = ImageIO.read(file);
					
					if (item_ori == null) {
						continue;
					}
					
					item_ori = CustomImageUtils.squarify(item_ori);
					
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
			
			for (File file : new File(getDataFolder() + "/assets/items/").listFiles()) {
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
			
			for (File file : new File(getDataFolder() + "/assets/font/").listFiles()) {
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
			
			for (File file : new File(getDataFolder() + "/assets/misc/").listFiles()) {
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
			
			for (File file : new File(getDataFolder() + "/assets/gui/").listFiles()) {
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
			
			for (File file : new File(getDataFolder() + "/assets/banner/").listFiles()) {
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
			
			Bukkit.getScheduler().runTask(plugin, () -> {
				InteractiveChatDiscordSrvAddon.plugin.blocks = blocks;
				InteractiveChatDiscordSrvAddon.plugin.items = items;
				InteractiveChatDiscordSrvAddon.plugin.font = font;
				InteractiveChatDiscordSrvAddon.plugin.misc = misc;
				InteractiveChatDiscordSrvAddon.plugin.gui = gui;
				InteractiveChatDiscordSrvAddon.plugin.banner = banner;
				
				int total = blocks.size() + items.size() + font.size() + misc.size() + gui.size() + banner.size();
				Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[ICDiscordSRVAddon] Loaded " + total + " textures!");
			});
		});
	}

}
