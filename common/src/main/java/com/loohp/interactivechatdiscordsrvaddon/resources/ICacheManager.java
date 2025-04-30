/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
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

import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager.ResourceRegistrySupplier;

import java.io.File;
import java.time.Duration;

public interface ICacheManager extends IResourceRegistry {

    String IDENTIFIER = "CacheManager";

    ICacheManager DUMMY_CACHE_MANAGER = new ICacheManager() {
        @Override
        public CacheObject<?> getCache(String key) {
            return null;
        }

        @Override
        public CacheObject<?> removeCache(String key) {
            return null;
        }

        @Override
        public <T> void putCache(String key, T value) {
            //do nothing
        }

        @Override
        public void clearAllCache() {
            //do nothing
        }

        @Override
        public String getRegistryIdentifier() {
            return IDENTIFIER;
        }
    };

    ResourceRegistrySupplier<ICacheManager> DUMMY_SUPPLIER = manager -> DUMMY_CACHE_MANAGER;

    static ResourceRegistrySupplier<ICacheManager> getDefaultSupplier(File folder) {
        return manager -> new CacheManager(folder, Duration.ofMinutes(10));
    }

    static ResourceRegistrySupplier<ICacheManager> getDummySupplier() {
        return DUMMY_SUPPLIER;
    }

    CacheObject<?> getCache(String key);

    CacheObject<?> removeCache(String key);

    <T> void putCache(String key, T value);

    void clearAllCache();

}
