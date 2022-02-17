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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Cache<T> {

    private static final Map<String, Cache<?>> DATA = new ConcurrentHashMap<>();
    private static final Map<Cache<?>, ScheduledFuture<?>> TASKS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public static Cache<?> getCache(String key) {
        return DATA.get(key);
    }

    public static <T> void putCache(String key, T object, long ticks) {
        if (ticks <= 0) {
            return;
        }
        synchronized (TASKS) {
            Cache<T> cache = new Cache<>(object);
            Cache<?> old = DATA.put(key, cache);
            TASKS.put(cache, service.schedule(() -> DATA.remove(key, cache), ticks * 50, TimeUnit.MILLISECONDS));
            if (old != null) {
                ScheduledFuture<?> future = TASKS.remove(old);
                if (future != null) {
                    future.cancel(false);
                }
            }
        }
    }

    public static void clearAllCache() {
        synchronized (TASKS) {
            Iterator<ScheduledFuture<?>> itr = TASKS.values().iterator();
            while (itr.hasNext()) {
                itr.next().cancel(false);
                itr.remove();
            }
            DATA.clear();
        }
    }

    private long timeCreated;
    private T object;

    private Cache(T object) {
        this.timeCreated = System.currentTimeMillis();
        this.object = object;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public T getObject() {
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Cache<?> cache = (Cache<?>) o;
        return timeCreated == cache.timeCreated && Objects.equals(object, cache.object);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeCreated, object);
    }

}
