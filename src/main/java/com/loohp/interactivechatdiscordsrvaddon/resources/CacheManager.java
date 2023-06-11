/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
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

import com.loohp.interactivechat.utils.FileUtils;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class CacheManager implements ICacheManager {

    private final File folder;
    private final DB db;
    private final HTreeMap<String, byte[]> cacheObjectMap;
    private final ScheduledExecutorService service;
    private final AtomicBoolean isValid;

    public CacheManager(File folder, Duration timeout) {
        this.folder = folder;
        if (folder.exists()) {
            FileUtils.removeFolderRecursively(folder);
        }
        folder.mkdirs();
        this.db = DBMaker.fileDB(new File(folder, "data.dat")).fileMmapEnableIfSupported().fileDeleteAfterClose().make();
        this.cacheObjectMap = db.hashMap("cache", Serializer.STRING, Serializer.BYTE_ARRAY).createOrOpen();
        this.service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> cacheObjectMap.expireEvict(), 5, 5, TimeUnit.MINUTES);
        this.isValid = new AtomicBoolean(true);
    }

    @Override
    public String getRegistryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public CacheObject<?> getCache(String key) {
        if (!isValid.get()) {
            return null;
        }
        byte[] data = cacheObjectMap.get(key);
        if (data == null) {
            return null;
        }
        try {
            return CacheObject.deserialize(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public <T> void putCache(String key, T value) {
        if (!isValid.get()) {
            return;
        }
        try {
            cacheObjectMap.put(key, new CacheObject<>(System.currentTimeMillis(), value).serialize());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CacheObject<?> removeCache(String key) {
        if (!isValid.get()) {
            return null;
        }
        byte[] data = cacheObjectMap.remove(key);
        if (data == null) {
            return null;
        }
        try {
            return CacheObject.deserialize(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void clearAllCache() {
        if (!isValid.get()) {
            return;
        }
        cacheObjectMap.clear();
    }

    @Override
    public synchronized void close() {
        if (isValid.getAndSet(false)) {
            service.shutdown();
            cacheObjectMap.close();
            db.close();
            if (folder.exists()) {
                FileUtils.removeFolderRecursively(folder);
            }
        }
    }

}
