package com.loohp.interactivechatdiscordsrvaddon.listeners;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.api.events.ICPlayerJoinEvent;
import com.loohp.interactivechat.api.events.OfflineICPlayerCreationEvent;
import com.loohp.interactivechat.api.events.OfflineICPlayerUpdateEvent;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.objectholders.ConcurrentCacheHashMap;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.utils.HTTPRequestUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class ICPlayerEvents implements Listener {

    public static final String PROFILE_URL = "https://api.loohpjames.com/spigot/plugins/interactivechatdiscordsrvaddon/profile/%s";

    private static final ConcurrentCacheHashMap<UUID, Map<String, Object>> CACHED_PROPERTIES = new ConcurrentCacheHashMap<>(300000);

    static {
        Bukkit.getScheduler().runTaskTimerAsynchronously(InteractiveChat.plugin, () -> CACHED_PROPERTIES.cleanUp(), 12000, 12000);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(ICPlayerJoinEvent event) {
        populate(event.getPlayer(), true);
    }

    @EventHandler
    public void onCreation(OfflineICPlayerCreationEvent event) {
        populate(event.getPlayer(), false);
    }

    @EventHandler
    public void onUpdate(OfflineICPlayerUpdateEvent event) {
        populate(event.getPlayer(), false);
    }

    private void populate(OfflineICPlayer player, boolean scheduleAsync) {
        if (scheduleAsync) {
            Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> populate(player, false));
            return;
        }
        Map<String, Object> cachedProperties = CACHED_PROPERTIES.get(player.getUniqueId());
        if (cachedProperties == null) {
            cachedProperties = new HashMap<>();
            JSONObject json = HTTPRequestUtils.getJSONResponse(PROFILE_URL.replace("%s", player.getName()));
            if (json != null && json.containsKey("properties")) {
                JSONObject properties = (JSONObject) json.get("properties");
                for (Object obj : properties.keySet()) {
                    try {
                        String key = (String) obj;
                        String value = (String) properties.get(key);
                        if (value.endsWith(".png")) {
                            BufferedImage image = ImageUtils.downloadImage(value);
                            player.addProperties(key, image);
                            cachedProperties.put(key, image);
                        } else {
                            player.addProperties(key, value);
                            cachedProperties.put(key, value);
                        }
                    } catch (Exception ignore) {
                    }
                }
            }
            CACHED_PROPERTIES.put(player.getUniqueId(), cachedProperties);
        } else {
            for (Entry<String, Object> entry : cachedProperties.entrySet()) {
                player.addProperties(entry.getKey(), entry.getValue());
            }
        }
    }

}
