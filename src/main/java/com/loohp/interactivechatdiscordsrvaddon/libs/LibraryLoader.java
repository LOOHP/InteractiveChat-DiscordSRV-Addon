package com.loohp.interactivechatdiscordsrvaddon.libs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.utils.HTTPRequestUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class LibraryLoader {

    public static final String LIBS_DATA_URL = "https://api.loohpjames.com/spigot/plugins/interactivechatdiscordsrvaddon/libs";

    private static final URLClassLoaderAccess LOADER_ACCESS = URLClassLoaderAccess.create((URLClassLoader) InteractiveChatDiscordSrvAddon.class.getClassLoader());

    public static void load(File rootFolder) {
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
            String oldHash = InteractiveChatDiscordSrvAddon.plugin.defaultResourceHash = json.containsKey("libs") ? json.get("libs").toString() : "EMPTY";
            String oldVersion = json.containsKey("version") ? json.get("version").toString() : "EMPTY";

            File libsFolder = new File(rootFolder, "libs");
            libsFolder.mkdirs();

            JSONObject data = HTTPRequestUtils.getJSONResponse(LIBS_DATA_URL);

            String hash = data.get("hash").toString();

            if (!hash.equals(oldHash) || !InteractiveChatDiscordSrvAddon.plugin.getDescription().getVersion().equals(oldVersion)) {
                JSONObject libs = (JSONObject) data.get("libs");
                Set<String> jarNames = new HashSet<>();
                for (Object key : libs.keySet()) {
                    String jarName = (String) key;
                    jarNames.add(jarName);
                    JSONObject details = (JSONObject) libs.get(jarName);
                    String url = (String) details.get("url");
                    File jarFile = new File(libsFolder, jarName);
                    Bukkit.getConsoleSender().sendMessage("[ICDiscordSrvAddon] Downloading Library library \"" + jarName + "\"");
                    if (HTTPRequestUtils.download(jarFile, url)) {
                        Bukkit.getConsoleSender().sendMessage("[ICDiscordSrvAddon] Downloaded Library library \"" + jarName + "\"");
                    } else {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to download library \"" + jarName + "\"");
                    }
                }
                for (File jarFile : libsFolder.listFiles()) {
                    if (!jarNames.contains(jarFile.getName())) {
                        jarFile.delete();
                    }
                }
            }

            for (File jarFile : libsFolder.listFiles()) {
                String jarName = jarFile.getName();
                if (jarName.endsWith(".jar")) {
                    try {
                        LOADER_ACCESS.addURL(jarFile.toURI().toURL());
                        Bukkit.getConsoleSender().sendMessage("[ICDiscordSrvAddon] Loaded library \"" + jarName + "\"");
                    } catch (Exception e) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to load library \"" + jarName + "\"");
                        e.printStackTrace();
                    }
                }
            }

            json.put("libs", hash);
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
    }

}
