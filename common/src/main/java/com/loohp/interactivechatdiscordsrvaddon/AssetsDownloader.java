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

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.com.google.gson.Gson;
import com.loohp.interactivechat.libs.com.google.gson.GsonBuilder;
import com.loohp.interactivechat.libs.com.google.gson.JsonParser;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.utils.FileUtils;
import com.loohp.interactivechat.utils.HTTPRequestUtils;
import com.loohp.interactivechat.utils.HashUtils;
import com.loohp.interactivechatdiscordsrvaddon.hooks.ItemsAdderHook;
import com.loohp.interactivechatdiscordsrvaddon.libs.LibraryDownloadManager;
import com.loohp.interactivechatdiscordsrvaddon.libs.LibraryLoader;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceDownloadManager;
import com.loohp.interactivechatdiscordsrvaddon.utils.ResourcePackUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class AssetsDownloader {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.0");
    private static final ReentrantLock LOCK = new ReentrantLock(true);

    @SuppressWarnings("deprecation")
    public static void loadAssets(File rootFolder, boolean force, boolean clean, CommandSender... senders) throws Exception {
        if (!Arrays.asList(senders).contains(Bukkit.getConsoleSender())) {
            List<CommandSender> senderList = new ArrayList<>(Arrays.asList(senders));
            senderList.add(Bukkit.getConsoleSender());
            senders = senderList.toArray(new CommandSender[senderList.size()]);
        }
        try {
            if (!LOCK.tryLock(0, TimeUnit.MILLISECONDS)) {
                return;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        try {
            File hashes = new File(rootFolder, "hashes.json");
            if (!hashes.exists()) {
                try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(hashes.toPath()), StandardCharsets.UTF_8))) {
                    pw.println("{}");
                    pw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            JSONObject json;
            try (InputStreamReader hashReader = new InputStreamReader(Files.newInputStream(hashes.toPath()), StandardCharsets.UTF_8)) {
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
                            if (!InteractiveChatDiscordSrvAddon.plugin.reducedAssetsDownloadInfo && percentage == 0.0) {
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSrvAddon] Downloading client jar");
                            }
                            break;
                        case EXTRACT:
                            if (!InteractiveChatDiscordSrvAddon.plugin.reducedAssetsDownloadInfo) {
                                Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[ICDiscordSrvAddon] Extracting " + fileName + " (" + FORMAT.format(percentage) + "%)");
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

            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(hashes.toPath()), StandardCharsets.UTF_8))) {
                Gson g = new GsonBuilder().setPrettyPrinting().create();
                pw.println(g.toJson(new JsonParser().parse(json.toString())));
                pw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            LOCK.unlock();
        }
    }

    public static void loadExtras() {
        ResourceDownloadManager downloadManager = new ResourceDownloadManager(InteractiveChat.exactMinecraftVersion, null);
        downloadManager.downloadExtras(() -> {
            InteractiveChatDiscordSrvAddon.plugin.extras.clear();
        }, (key, dataBytes) -> {
            InteractiveChatDiscordSrvAddon.plugin.extras.put(key, dataBytes);
        });
    }

    public static ServerResourcePackDownloadResult downloadServerResourcePack(File packFolder) {
        String url = InteractiveChatDiscordSrvAddon.plugin.alternateResourcePackURL;
        String hash = InteractiveChatDiscordSrvAddon.plugin.alternateResourcePackHash;
        if (InteractiveChatDiscordSrvAddon.itemsAdderHook && InteractiveChatDiscordSrvAddon.plugin.itemsAdderPackAsServerResourcePack) {
            String iaUrl = ItemsAdderHook.getItemsAdderResourcePackURL();
            if (iaUrl != null) {
                url = iaUrl;
                hash = null;
            }
        }
        if (url == null || url.isEmpty()) {
            url = ResourcePackUtils.getServerResourcePack();
            hash = ResourcePackUtils.getServerResourcePackHash();
            if (url == null || url.isEmpty()) {
                return new ServerResourcePackDownloadResult(ServerResourcePackDownloadResultType.NO_PACK);
            }
        }
        File desFile = hash != null && !hash.isEmpty() ? new File(packFolder, hash) : null;
        if (desFile != null && desFile.exists()) {
            try {
                if (hash != null && !hash.isEmpty()) {
                    String packHash = HashUtils.createSha1String(desFile);
                    if (packHash.equalsIgnoreCase(hash)) {
                        return new ServerResourcePackDownloadResult(ServerResourcePackDownloadResultType.SUCCESS_NO_CHANGES, desFile, packHash, hash);
                    }
                }
            } catch (Exception ignore) {
            }
        }
        Arrays.stream(packFolder.listFiles()).forEach(each -> {
            if (each.isFile()) {
                each.delete();
            }
        });
        byte[] packData = HTTPRequestUtils.download(url);
        if (packData != null) {
            try {
                String packHash = HashUtils.createSha1String(new ByteArrayInputStream(packData));
                desFile = new File(packFolder, packHash);
                if (hash == null || hash.isEmpty()) {
                    FileUtils.copy(new ByteArrayInputStream(packData), desFile);
                    return new ServerResourcePackDownloadResult(ServerResourcePackDownloadResultType.SUCCESS_NO_HASH, desFile);
                } else {
                    if (packHash.equalsIgnoreCase(hash)) {
                        FileUtils.copy(new ByteArrayInputStream(packData), desFile);
                        return new ServerResourcePackDownloadResult(ServerResourcePackDownloadResultType.SUCCESS_WITH_HASH, desFile, packHash, hash);
                    }
                    return new ServerResourcePackDownloadResult(ServerResourcePackDownloadResultType.FAILURE_WRONG_HASH, packHash, hash);
                }
            } catch (Exception e) {
                return new ServerResourcePackDownloadResult(ServerResourcePackDownloadResultType.FAILURE_WRONG_HASH, null, "ERROR", hash, e);
            }
        } else {
            return new ServerResourcePackDownloadResult(ServerResourcePackDownloadResultType.FAILURE_DOWNLOAD);
        }
    }

    public static void loadLibraries(File rootFolder) {
        try {
            File hashes = new File(rootFolder, "hashes.json");
            if (!hashes.exists()) {
                try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(hashes.toPath()), StandardCharsets.UTF_8))) {
                    pw.println("{}");
                    pw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            JSONObject json;
            try (InputStreamReader hashReader = new InputStreamReader(Files.newInputStream(hashes.toPath()), StandardCharsets.UTF_8)) {
                json = (JSONObject) new JSONParser().parse(hashReader);
            } catch (Throwable e) {
                new RuntimeException("Invalid hashes.json! It will be reset.", e).printStackTrace();
                json = new JSONObject();
            }
            String oldHash = InteractiveChatDiscordSrvAddon.plugin.defaultResourceHash = json.containsKey("libs") ? json.get("libs").toString() : "EMPTY";
            String oldVersion = json.containsKey("version") ? json.get("version").toString() : "EMPTY";

            File libsFolder = new File(rootFolder, "libs");
            libsFolder.mkdirs();

            LibraryDownloadManager downloadManager = new LibraryDownloadManager(libsFolder);

            String hash = "N/A";
            try {
                hash = downloadManager.getHash();

                if (!hash.equals(oldHash) || !InteractiveChatDiscordSrvAddon.plugin.getDescription().getVersion().equals(oldVersion)) {
                    downloadManager.downloadLibraries((result, jarName, percentage) -> {
                        if (result) {
                            Bukkit.getConsoleSender().sendMessage("[ICDiscordSrvAddon] Downloaded library \"" + jarName + "\"");
                        } else {
                            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to download library \"" + jarName + "\"");
                        }
                    });
                }
            } catch (Throwable e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Error while downloading libraries");
                e.printStackTrace();
            }

            LibraryLoader.loadLibraries(libsFolder, (file, e) -> {
                String jarName = file.getName();
                if (e == null) {
                    Bukkit.getConsoleSender().sendMessage("[ICDiscordSrvAddon] Remapped library \"" + jarName + "\"");
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to remap library \"" + jarName + "\"");
                    e.printStackTrace();
                }
            }, (file, e) -> {
                String jarName = file.getName();
                if (e == null) {
                    Bukkit.getConsoleSender().sendMessage("[ICDiscordSrvAddon] Loaded library \"" + jarName + "\"");
                } else {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[ICDiscordSrvAddon] Unable to load library \"" + jarName + "\"");
                    e.printStackTrace();
                }
            });

            json.put("libs", hash);
            json.put("version", InteractiveChatDiscordSrvAddon.plugin.getDescription().getVersion());

            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(hashes.toPath()), StandardCharsets.UTF_8))) {
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

    public static class ServerResourcePackDownloadResult {

        private final ServerResourcePackDownloadResultType type;
        private final  File resourcePackFile;
        private final String packHash;
        private final String expectedHash;
        private final Throwable error;

        public ServerResourcePackDownloadResult(ServerResourcePackDownloadResultType type, File resourcePackFile, String packHash, String expectedHash, Throwable error) {
            this.type = type;
            this.resourcePackFile = resourcePackFile;
            this.packHash = packHash;
            this.expectedHash = expectedHash;
            this.error = error;
        }

        public ServerResourcePackDownloadResult(ServerResourcePackDownloadResultType type, File resourcePackFile, String packHash, String expectedHash) {
            this(type, resourcePackFile, packHash, expectedHash, null);
        }

        public ServerResourcePackDownloadResult(ServerResourcePackDownloadResultType type, String packHash, String expectedHash) {
            this(type, null, packHash, expectedHash, null);
        }

        public ServerResourcePackDownloadResult(ServerResourcePackDownloadResultType type, Throwable error) {
            this(type, null, null, null, error);
        }

        public ServerResourcePackDownloadResult(ServerResourcePackDownloadResultType type, File resourcePackFile) {
            this(type, resourcePackFile, null, null, null);
        }

        public ServerResourcePackDownloadResult(ServerResourcePackDownloadResultType type) {
            this(type, null, null, null, null);
        }

        public ServerResourcePackDownloadResultType getType() {
            return type;
        }

        public File getResourcePackFile() {
            return resourcePackFile;
        }

        public String getPackHash() {
            return packHash;
        }

        public String getExpectedHash() {
            return expectedHash;
        }

        public Throwable getError() {
            return error;
        }

    }

    public enum ServerResourcePackDownloadResultType {
        NO_PACK, SUCCESS_NO_CHANGES, SUCCESS_NO_HASH, SUCCESS_WITH_HASH, FAILURE_DOWNLOAD, FAILURE_WRONG_HASH;
    }

}
