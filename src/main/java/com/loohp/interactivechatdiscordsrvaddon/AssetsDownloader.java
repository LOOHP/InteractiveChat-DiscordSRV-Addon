package com.loohp.interactivechatdiscordsrvaddon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.utils.FileUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceDownloadManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

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

public class AssetsDownloader {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.0");
    private static final ReentrantLock LOCK = new ReentrantLock(true);

    @SuppressWarnings({"unchecked", "deprecation"})
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

            ResourceDownloadManager downloadManager = new ResourceDownloadManager(InteractiveChat.exactMinecraftVersion, defaultAssetsFolder);

            String hash = downloadManager.getHash();

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

                downloadManager.downloadResources((type, fileName, percentage) -> {
                    switch (type) {
                        case CLIENT_DOWNLOAD:
                            if (!InteractiveChatDiscordSrvAddon.plugin.reducedAssetsDownloadInfo) {
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSrvAddon] Downloading client jar");
                            }
                            break;
                        case EXTRACT:
                            if (!InteractiveChatDiscordSrvAddon.plugin.reducedAssetsDownloadInfo) {
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSrvAddon] Extracting " + fileName);
                            }
                            break;
                        case DOWNLOAD:
                            if (!InteractiveChatDiscordSrvAddon.plugin.reducedAssetsDownloadInfo) {
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSrvAddon] Downloading " + fileName + " (" + FORMAT.format(percentage) + "%)");
                            }
                            break;
                        case DONE:
                            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[ICDiscordSrvAddon] Done!");
                            break;
                    }
                });
            }

            downloadManager.downloadExtras(() -> {
                InteractiveChatDiscordSrvAddon.plugin.extras.clear();
            }, (key, dataBytes) -> {
                InteractiveChatDiscordSrvAddon.plugin.extras.put(key, dataBytes);
            });

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

    public static void loadExtras() {
        ResourceDownloadManager downloadManager = new ResourceDownloadManager(InteractiveChat.exactMinecraftVersion, null);
        downloadManager.downloadExtras(() -> {
            InteractiveChatDiscordSrvAddon.plugin.extras.clear();
        }, (key, dataBytes) -> {
            InteractiveChatDiscordSrvAddon.plugin.extras.put(key, dataBytes);
        });
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
