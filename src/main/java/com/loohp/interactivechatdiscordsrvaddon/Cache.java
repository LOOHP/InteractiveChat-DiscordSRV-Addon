package com.loohp.interactivechatdiscordsrvaddon;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;

public class Cache<T> {
	
	private static final Map<String, Cache<?>> DATA = new ConcurrentHashMap<>();
	private static final List<Integer> TASKS = new LinkedList<>();
	private static final Object LOCK = new Object();
	
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
	
	public static Cache<?> getCache(String key) {
		return DATA.get(key);
	}
	
	public static <T> void putCache(String key, T object, long ticks) {
		synchronized (LOCK) {
			Cache<T> cache = new Cache<>(object);
			DATA.put(key, cache);
			TASKS.add(Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> {
				DATA.remove(key, cache);
			}, ticks).getTaskId());
		}
	}
	
	public static void clearAllCache() {
		synchronized (LOCK) {
			TASKS.clear();
			DATA.clear();
		}
	}

}
