package com.loohp.interactivechatdiscordsrvaddon;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Cache<T> {

    private static final Map<String, Cache<?>> DATA = new ConcurrentHashMap<>();
    private static final List<ScheduledFuture<?>> TASKS = new LinkedList<>();
    private static final Object LOCK = new Object();
    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    public static Cache<?> getCache(String key) {
        return DATA.get(key);
    }

    public static <T> void putCache(String key, T object, long ticks) {
        if (ticks <= 0) {
            return;
        }
        synchronized (LOCK) {
            Cache<T> cache = new Cache<>(object);
            DATA.put(key, cache);
            service.schedule(() -> DATA.remove(key, cache), ticks * 50, TimeUnit.MILLISECONDS);
        }
    }

    public static void clearAllCache() {
        synchronized (LOCK) {
            Iterator<ScheduledFuture<?>> itr = TASKS.iterator();
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        result = prime * result + (int) (timeCreated ^ (timeCreated >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Cache)) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        Cache other = (Cache) obj;
        if (object == null) {
            if (other.object != null) {
                return false;
            }
        } else if (!object.equals(other.object)) {
            return false;
        }
        return timeCreated == other.timeCreated;
    }

}
