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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.loohp.interactivechat.libs.org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.utils.FileUtils;
import com.loohp.interactivechat.utils.HTTPRequestUtils;

import net.md_5.bungee.api.ChatColor;

public class AssetsDownloader {
	
	public static final String ASSETS_DATA_URL = "https://api.loohpjames.com/spigot/plugins/interactivechatdiscordsrvaddon";
	
	private static final DecimalFormat FORMAT = new DecimalFormat("0.0");
	private static final AtomicBoolean LOCK = new AtomicBoolean(false);
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static void loadAssets(File rootFolder, boolean force, CommandSender... senders) throws Exception {
		if (!Arrays.asList(senders).contains(Bukkit.getConsoleSender())) {
			List<CommandSender> senderList = new ArrayList<>(Arrays.asList(senders));
			senderList.add(Bukkit.getConsoleSender());
			senders = senderList.toArray(new CommandSender[senderList.size()]);
		}
		if (LOCK.get()) {
			return;
		}
		LOCK.set(true);
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
		String oldHash = InteractiveChatDiscordSrvAddon.plugin.defaultResourceHash = json.containsKey("assets") ? json.get("assets").toString() : "EMPTY";
		
		File assetsFolder = new File(rootFolder, "assets");
		assetsFolder.mkdirs();
		
		JSONObject data = HTTPRequestUtils.getJSONResponse(ASSETS_DATA_URL);
		
		String hash = data.get("hash").toString();
		
		if (force || !hash.equals(oldHash)) {
			if (force && hash.equals(oldHash)) {
				InteractiveChatDiscordSrvAddon.plugin.sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Forcibly re-downloading assets! Please wait... (" + oldHash + " -> " + hash + ")", senders);
			} else {
				InteractiveChatDiscordSrvAddon.plugin.sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Hash changed! Re-downloading assets! Please wait... (" + oldHash + " -> " + hash + ")", senders);
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
					if (name.startsWith("assets") && !entry.isDirectory()) {
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
						
						File folder = new File(rootFolder, name).getParentFile();
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
					Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSrvAddon] Downloading " + value + "/" + fileName + " (" + FORMAT.format(percentage) + "%)");
				}
				File folder = new File(rootFolder, value);
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
		
		json.put("assets", hash);
		
		try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(hashes), StandardCharsets.UTF_8))) {
			Gson g = new GsonBuilder().setPrettyPrinting().create();
			pw.println(g.toJson(new JsonParser().parse(json.toString())));
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LOCK.set(false);
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
