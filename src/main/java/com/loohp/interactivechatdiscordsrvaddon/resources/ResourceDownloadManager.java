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

package com.loohp.interactivechatdiscordsrvaddon.resources;

import com.loohp.interactivechat.libs.org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.utils.FileUtils;
import com.loohp.interactivechat.utils.HTTPRequestUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.TriConsumer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.zip.ZipEntry;

public class ResourceDownloadManager {

    public static final String ASSETS_DATA_URL = "https://api.loohpjames.com/spigot/plugins/interactivechatdiscordsrvaddon?minecraftVersion=%s";
    public static final String VERSIONS_URL = "https://api.loohpjames.com/spigot/plugins/interactivechatdiscordsrvaddon/versions";
    public static final String MOJANG_RESOURCES_URL = "https://resources.download.minecraft.net/";

    private static Set<String> MINECRAFT_VERSIONS = null;

    private static void ensureVersions() {
        if (MINECRAFT_VERSIONS == null) {
            JSONObject data = HTTPRequestUtils.getJSONResponse(VERSIONS_URL);
            MINECRAFT_VERSIONS = new LinkedHashSet<>();
            if (data != null && data.containsKey("versions")) {
                for (Object version : (JSONArray) data.get("versions")) {
                    MINECRAFT_VERSIONS.add(version.toString());
                }
            }
            MINECRAFT_VERSIONS = Collections.unmodifiableSet(MINECRAFT_VERSIONS);
        }
    }

    public static Set<String> getMinecraftVersions() {
        ensureVersions();
        return MINECRAFT_VERSIONS;
    }

    private String minecraftVersion;
    private File packFolder;
    private JSONObject data;
    private JSONObject assetIndex;

    public ResourceDownloadManager(String minecraftVersion, File packFolder) {
        this.minecraftVersion = minecraftVersion;
        this.packFolder = packFolder;
        this.data = null;
        this.assetIndex = null;
    }

    private void ensureData() {
        if (data == null || assetIndex == null) {
            data = HTTPRequestUtils.getJSONResponse(ASSETS_DATA_URL.replace("%s", minecraftVersion));
            if (data == null) {
                throw new RuntimeException("Unable to fetch assets from \"api.loohpjames.com\". This could be an internet issue or \"api.loohpjames.com\" is down. If the plugin functions correctly after this, this error can be ignored.");
            }
            JSONObject client = (JSONObject) data.get("client-entries");
            assetIndex = HTTPRequestUtils.getJSONResponse(client.get("asset-index").toString());
        }
    }

    public String getHash() {
        ensureData();
        return data.get("hash").toString();
    }

    public synchronized void applyRename() {
        JSONObject renameEntries = (JSONObject) data.get("rename-entries");
        for (Object obj : renameEntries.keySet()) {
            try {
                String from = (String) obj;
                String target = renameEntries.get(from).toString();
                File fromFile = new File(packFolder, from);
                File targetFile = new File(packFolder, target);
                if (fromFile.exists()) {
                    Files.move(fromFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void downloadResources(TriConsumer<TaskType, String, Double> progressListener) {
        ensureData();
        JSONObject client = (JSONObject) data.get("client-entries");

        String clientUrl = client.get("url").toString();

        progressListener.accept(TaskType.CLIENT_DOWNLOAD, "", 0.0);
        double totalSize = HTTPRequestUtils.getContentSize(clientUrl);
        AtomicLong downloadedCount = new AtomicLong(0);
        AtomicBoolean hasFinished = new AtomicBoolean(false);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (hasFinished.get()) {
                    this.cancel();
                    return;
                }
                progressListener.accept(TaskType.CLIENT_DOWNLOAD, "", ((double) downloadedCount.get() / totalSize) * 100);
            }
        }, 0, 200);
        byte[] clientBytes = HTTPRequestUtils.download(clientUrl, downloadedCount);
        hasFinished.set(true);
        progressListener.accept(TaskType.CLIENT_DOWNLOAD, "", 1.0);

        try (ZipArchiveInputStream zip = new ZipArchiveInputStream(new ByteArrayInputStream(clientBytes), StandardCharsets.UTF_8.toString(), false, true, true)) {
            ZipEntry entry;
            while ((entry = zip.getNextZipEntry()) != null) {
                String name = entry.getName();
                if ((name.startsWith("assets") || name.equals("pack.png") || name.equals("version.json")) && !entry.isDirectory()) {
                    String fileName = getEntryName(name);
                    progressListener.accept(TaskType.EXTRACT, name, (zip.getBytesRead() / totalSize) * 100);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] byteChunk = new byte[4096];
                    int n;
                    while ((n = zip.read(byteChunk)) > 0) {
                        baos.write(byteChunk, 0, n);
                    }
                    byte[] currentEntry = baos.toByteArray();

                    File folder = new File(packFolder, name).getParentFile();
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
            double percentage = ((double) ++i / (double) size) * 100;
            String trimmedValue = (value.startsWith("/") ? value.substring(1) : value).trim();
            if (!trimmedValue.isEmpty()) {
                trimmedValue += "/";
            }
            progressListener.accept(TaskType.DOWNLOAD, trimmedValue + fileName, percentage);
            File folder = value.isEmpty() || value.equals("/") ? packFolder : new File(packFolder, value);
            folder.mkdirs();
            File file = new File(folder, fileName);
            if (file.exists()) {
                file.delete();
            }
            HTTPRequestUtils.download(file, key);
        }

        applyRename();
        progressListener.accept(TaskType.DONE, "", 100.0);
    }

    public synchronized void downloadLanguages(TriConsumer<TaskType, String, Double> progressListener) {
        ensureData();
        JSONObject json = (JSONObject) assetIndex.get("objects");
        Map<String, String> langEntries = new LinkedHashMap<>();
        for (Object obj : json.keySet()) {
            String key = (String) obj;
            if (key.startsWith("minecraft/lang/")) {
                langEntries.put(key, ((JSONObject) json.get(key)).get("hash").toString());
            }
        }
        File assetsFolder = new File(packFolder, "assets");
        assetsFolder.mkdirs();
        int size = langEntries.size();
        int i = 0;
        for (Entry<String, String> entry : langEntries.entrySet()) {
            String name = entry.getKey();
            String hash = entry.getValue();
            double percentage = ((double) ++i / (double) size) * 100;
            progressListener.accept(TaskType.DOWNLOAD, "assets/" + name, percentage);
            File file = name.isEmpty() || name.equals("/") ? assetsFolder : new File(assetsFolder, name);
            file.getParentFile().mkdirs();
            if (file.exists()) {
                file.delete();
            }
            HTTPRequestUtils.download(file, MOJANG_RESOURCES_URL + hash.substring(0, 2) + "/" + hash);
        }

        applyRename();
        progressListener.accept(TaskType.DONE, "", 100.0);
    }

    public synchronized void downloadExtras(Runnable preparation, BiConsumer<String, byte[]> dataHandler) {
        ensureData();
        try {
            if (data.containsKey("extras-entries")) {
                JSONObject extras = (JSONObject) data.get("extras-entries");
                preparation.run();
                for (Object obj : extras.keySet()) {
                    String key = obj.toString();
                    String value = extras.get(key).toString();
                    try {
                        dataHandler.accept(value, HTTPRequestUtils.download(key));
                    } catch (Exception e) {
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        applyRename();
    }

    private String getEntryName(String name) {
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

    public enum TaskType {

        CLIENT_DOWNLOAD,
        EXTRACT,
        DOWNLOAD(true),
        DONE;

        private boolean hasPercentage;

        TaskType(boolean hasPercentage) {
            this.hasPercentage = hasPercentage;
        }

        TaskType() {
            this(false);
        }

        public boolean isHasPercentage() {
            return hasPercentage;
        }
    }

}
