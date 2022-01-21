package com.loohp.interactivechatdiscordsrvaddon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.utils.FileUtils;
import com.loohp.interactivechat.utils.HTTPRequestUtils;

import net.md_5.bungee.api.ChatColor;

public class AssetsDownloader {
	
	public static final String ASSETS_DATA_URL = "https://api.loohpjames.com/spigot/plugins/interactivechatdiscordsrvaddon?minecraftVersion=" + InteractiveChat.exactMinecraftVersion;
	
	private static final DecimalFormat FORMAT = new DecimalFormat("0.0");
	private static final ReentrantLock LOCK = new ReentrantLock(true);
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void loadAssets(File rootFolder, boolean force, boolean clean, CommandSender... senders) throws Exception {
		if (!Arrays.asList(senders).contains(Bukkit.getConsoleSender())) {
			List<CommandSender> senderList = new ArrayList<>(Arrays.asList(senders));
			senderList.add(Bukkit.getConsoleSender());
			senders = senderList.toArray(new CommandSender[senderList.size()]);
		}
		if (!LOCK.tryLock(0, TimeUnit.MILLISECONDS)) {
			return;
		}
		try {
			File hashes = new File(rootFolder, "hashes.json");
			if (!hashes.exists()) {
				try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(hashes), StandardCharsets.UTF_8))) {
					pw.println("{}");
					pw.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			JSONObject json;
			try (InputStreamReader hashReader = new InputStreamReader(new FileInputStream(hashes), StandardCharsets.UTF_8)) {
				json = (JSONObject) new JSONParser().parse(hashReader);
			} catch (Throwable e) {
				new RuntimeException("Invalid hashes.json! It will be reset.", e).printStackTrace();
				json = new JSONObject();
			}
			String oldHash = InteractiveChatDiscordSrvAddon.plugin.defaultResourceHash = json.containsKey("Default") ? json.get("Default").toString() : "EMPTY";
			String oldVersion = json.containsKey("version") ? json.get("version").toString() : "EMPTY";
			
			File defaultAssetsFolder = new File(rootFolder + "/built-in", "Default");
			defaultAssetsFolder.mkdirs();
			
			JSONObject data = HTTPRequestUtils.getJSONResponse(ASSETS_DATA_URL);
			
			String hash = data.get("hash").toString();
			
			if (force || !hash.equals(oldHash) || !InteractiveChatDiscordSrvAddon.plugin.getDescription().getVersion().equals(oldVersion)) {
				if (clean) {
					InteractiveChatDiscordSrvAddon.plugin.sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Cleaning old default resources!", senders);
					FileUtils.removeFolderRecursively(defaultAssetsFolder);
					defaultAssetsFolder.mkdirs();
				}
				if (force) {
					InteractiveChatDiscordSrvAddon.plugin.sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Forcibly re-downloading default resources! Please wait... (" + oldHash + " -> " + hash + ")", senders);
				} else if (!hash.equals(oldHash)) {
					InteractiveChatDiscordSrvAddon.plugin.sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Hash changed! Re-downloading default resources! Please wait... (" + oldHash + " -> " + hash + ")", senders);
				} else {
					InteractiveChatDiscordSrvAddon.plugin.sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Plugin version changed! Re-downloading default resources! Please wait... (" + oldHash + " -> " + hash + ")", senders);
				}
				
				JSONObject client = (JSONObject) data.get("client-entries");
				
				String clientUrl = client.get("url").toString();
				
				if (!InteractiveChatDiscordSrvAddon.plugin.reducedAssetsDownloadInfo) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSrvAddon] Downloading client jar");
				}
				try (ZipArchiveInputStream zip = new ZipArchiveInputStream(new ByteArrayInputStream(HTTPRequestUtils.download(clientUrl)), StandardCharsets.UTF_8.toString(), false, true, true)) {
					while (true) {
						ZipEntry entry = zip.getNextZipEntry();
						if (entry == null) {
							break;
						}
						String name = entry.getName();
						if ((name.startsWith("assets") || name.equals("pack.png")) && !entry.isDirectory()) {
							String fileName = getEntryName(name);
							if (!InteractiveChatDiscordSrvAddon.plugin.reducedAssetsDownloadInfo) {
								Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSrvAddon] Extracting " + name);
							}
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							byte[] byteChunk = new byte[4096];
							int n;
							while ((n = zip.read(byteChunk)) > 0) {
								baos.write(byteChunk, 0, n);
							}
							byte[] currentEntry = baos.toByteArray();
							
							File folder = new File(defaultAssetsFolder, name).getParentFile();
							folder.mkdirs();
							File file = new File(folder, fileName);
							if (file.exists()) {
								file.delete();
							}
							FileUtils.copy(new ByteArrayInputStream(currentEntry), file);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				JSONObject downloadedEntries = (JSONObject) data.get("downloaded-entries");
				int size = downloadedEntries.size();
				int i = 0;
				for (Object obj : downloadedEntries.keySet()) {
					String key = obj.toString();
					String value = downloadedEntries.get(key).toString();
					String fileName = getEntryName(key);
					if (!InteractiveChatDiscordSrvAddon.plugin.reducedAssetsDownloadInfo) {
						double percentage = ((double) ++i / (double) size) * 100;
						String trimmedValue = (value.startsWith("/") ? value.substring(1) : value).trim();
						if (!trimmedValue.isEmpty()) {
							trimmedValue += "/";
						}
						Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSrvAddon] Downloading " + trimmedValue + fileName + " (" + FORMAT.format(percentage) + "%)");
					}
					File folder = value.isEmpty() || value.equals("/") ? defaultAssetsFolder : new File(defaultAssetsFolder, value);
					folder.mkdirs();
					File file = new File(folder, fileName);
					if (file.exists()) {
						file.delete();
					}
					HTTPRequestUtils.download(file, key);
				}			
				Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Done!");
			}
			
			if (data.containsKey("extras-entries")) {
				InteractiveChatDiscordSrvAddon.plugin.extras.clear();
				JSONObject extras = (JSONObject) data.get("extras-entries");
				for (Object obj : extras.keySet()) {
					String key = obj.toString();
					String value = extras.get(key).toString();
					try {
						InteractiveChatDiscordSrvAddon.plugin.extras.put(value, HTTPRequestUtils.download(key));
					} catch (Exception e) {}
				}
			}
			
			InteractiveChatDiscordSrvAddon.plugin.defaultResourceHash = hash;
			
			json.put("Default", hash);
			json.put("version", InteractiveChatDiscordSrvAddon.plugin.getDescription().getVersion());
			
			try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(hashes), StandardCharsets.UTF_8))) {
				Gson g = new GsonBuilder().setPrettyPrinting().create();
				pw.println(g.toJson(new JsonParser().parse(json.toString())));
				pw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOCK.unlock();
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
