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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.Bukkit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.loohp.interactivechat.Utils.FileUtils;
import com.loohp.interactivechat.Utils.HTTPRequestUtils;

import net.md_5.bungee.api.ChatColor;

public class AssetsDownloader {
	
	public static final String ASSETS_DATA_URL = "https://api.loohpjames.com/spigot/plugins/interactivechatdiscordsrvaddon";
	
	private static final DecimalFormat FORMAT = new DecimalFormat("0.0");
	
	@SuppressWarnings("unchecked")
	public static void loadAssets(File rootFolder) throws Exception {
		File hashes = new File(rootFolder, "hashes.json");
		if (!hashes.exists()) {
			try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(hashes), StandardCharsets.UTF_8))) {
				pw.println("{}");
				pw.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		InputStreamReader hashReader = new InputStreamReader(new FileInputStream(hashes), StandardCharsets.UTF_8);
		JSONObject json = (JSONObject) new JSONParser().parse(hashReader);
		hashReader.close();
		String oldHash = json.containsKey("assets") ? json.get("assets").toString() : "EMPTY";
		
		File assetsFolder = new File(rootFolder, "assets");
		assetsFolder.mkdirs();
		
		JSONObject data = HTTPRequestUtils.getJSONResponse(ASSETS_DATA_URL);
		
		String hash = data.get("hash").toString();
		
		if (!hash.equals(oldHash)) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ICDiscordSRVAddon] Hash changed! Re-downloading assets! (" + oldHash + " -> " + hash + ")");
			
			JSONObject client = (JSONObject) data.get("client-entries");
			
			String clientUrl = client.get("url").toString();
			JSONObject clientEntries = (JSONObject) client.get("entries");
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSRVAddon] Downloading client jar");
			try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(HTTPRequestUtils.download(clientUrl)))) {
				while (true) {
					ZipEntry entry = zip.getNextEntry();
					if (entry == null) {
						break;
					}
					String name = entry.getName();
					Object outputObj = clientEntries.get(name);
					if (outputObj != null) {
						String output = outputObj.toString();
						String fileName = getEntryName(name);
						Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSRVAddon] Extracting " + output + "/" + fileName);
						
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						byte[] byteChunk = new byte[4096];
						int n;
						while ((n = zip.read(byteChunk)) > 0) {
							baos.write(byteChunk, 0, n);
						}
						byte[] currentEntry = baos.toByteArray();
						
						File folder = new File(rootFolder, output);
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
				double percentage = ((double) ++i / (double) size ) * 100;
				Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSRVAddon] Downloading " + value + "/" + fileName + " (" + FORMAT.format(percentage) + "%)");
				File folder = new File(rootFolder, value);
				folder.mkdirs();
				File file = new File(folder, fileName);
				if (file.exists()) {
					file.delete();
				}
				HTTPRequestUtils.download(file, key);
			}
			
			Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ICDiscordSRVAddon] Done!");
		}
		
		json.put("assets", hash);
		
		try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(hashes), StandardCharsets.UTF_8))) {
			Gson g = new GsonBuilder().setPrettyPrinting().create();
			pw.println(g.toJson(new JsonParser().parse(json.toString())));
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
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
