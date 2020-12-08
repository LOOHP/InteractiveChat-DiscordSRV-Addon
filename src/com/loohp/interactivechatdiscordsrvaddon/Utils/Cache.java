package com.loohp.interactivechatdiscordsrvaddon.Utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;

import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;

public class Cache<T> {
	
	private static Map<String, Cache<?>> data = new ConcurrentHashMap<>();
	
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
		return data.get(key);
	}
	
	public static <T> void putCache(String key, T object, long ticks) {
		Cache<T> cache = new Cache<>(object);
		data.put(key, cache);
		Bukkit.getScheduler().runTaskLater(InteractiveChatDiscordSrvAddon.plugin, () -> {
			data.remove(key, cache);
		}, ticks);
	}

}
