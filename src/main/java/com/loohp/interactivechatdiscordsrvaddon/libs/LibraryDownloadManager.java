package com.loohp.interactivechatdiscordsrvaddon.libs;

import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.utils.HTTPRequestUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

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

    public synchronized void downloadLibraries(BiConsumer<Boolean, String> progressListener) {
        ensureData();
        try {
            JSONObject libs = (JSONObject) data.get("libs");
            Set<String> jarNames = new HashSet<>();
            for (Object key : libs.keySet()) {
                String jarName = (String) key;
                jarNames.add(jarName);
                JSONObject details = (JSONObject) libs.get(jarName);
                String url = (String) details.get("url");
                File jarFile = new File(libsFolder, jarName);
                if (HTTPRequestUtils.download(jarFile, url)) {
                    progressListener.accept(true, jarName);
                } else {
                    progressListener.accept(false, jarName);
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
