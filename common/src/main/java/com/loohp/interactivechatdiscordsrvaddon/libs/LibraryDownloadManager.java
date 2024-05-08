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

package com.loohp.interactivechatdiscordsrvaddon.libs;

import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.utils.HTTPRequestUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.TriConsumer;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class LibraryDownloadManager {

    public static final String LIBS_DATA_URL = "https://api.loohpjames.com/spigot/plugins/interactivechatdiscordsrvaddon/libs";

    private File libsFolder;
    private JSONObject data;

    public LibraryDownloadManager(File libsFolder) {
        this.libsFolder = libsFolder;
        this.data = null;
    }

    private void ensureData() {
        if (data == null) {
            data = HTTPRequestUtils.getJSONResponse(LIBS_DATA_URL);
        }
    }

    public String getHash() {
        ensureData();
        return data.get("hash").toString();
    }

    public synchronized void downloadLibraries(TriConsumer<Boolean, String, Double> progressListener) {
        ensureData();
        try {
            JSONObject libs = (JSONObject) data.get("libs");
            Set<String> jarNames = new HashSet<>();
            double total = libs.keySet().size();
            double current = 0.0;
            for (Object key : libs.keySet()) {
                String jarName = (String) key;
                jarNames.add(jarName);
                JSONObject details = (JSONObject) libs.get(jarName);
                String url = (String) details.get("url");
                File jarFile = new File(libsFolder, jarName);
                current += 1.0;
                if (HTTPRequestUtils.download(jarFile, url)) {
                    progressListener.accept(true, jarName, (current / total) * 100);
                } else {
                    progressListener.accept(false, jarName, (current / total) * 100);
                }
            }
            for (File jarFile : libsFolder.listFiles()) {
                if (!jarNames.contains(jarFile.getName())) {
                    jarFile.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
