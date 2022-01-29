package com.loohp.interactivechatdiscordsrvaddon.listeners;

import com.loohp.interactivechat.api.events.ICPlayerJoinEvent;
import com.loohp.interactivechat.api.events.OfflineICPlayerCreationEvent;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.utils.HTTPRequestUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ICPlayerEvents implements Listener {

    public static final String PROFILE_URL = "https://api.loohpjames.com/spigot/plugins/interactivechatdiscordsrvaddon/profile/%s";

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(ICPlayerJoinEvent event) {
        populate(event.getPlayer(), true);
    }

    @EventHandler
    public void onCreation(OfflineICPlayerCreationEvent event) {
        populate(event.getPlayer(), false);
    }

    private void populate(OfflineICPlayer player, boolean scheduleAsync) {
        if (scheduleAsync) {
            Bukkit.getScheduler().runTaskAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> populate(player, false));
            return;
        }
        JSONObject json = HTTPRequestUtils.getJSONResponse(PROFILE_URL.replace("%s", player.getName()));
        if (json != null && json.containsKey("properties")) {
            JSONObject properties = (JSONObject) json.get("properties");
            for (Object obj : properties.keySet()) {
                try {
                    String key = (String) obj;
                    String value = (String) properties.get(key);
                    if (value.endsWith(".png")) {
                        player.addProperties(key, ImageUtils.downloadImage(value));
                    } else {
                        player.addProperties(key, value);
                    }
                } catch (Exception ignore) {
                }
            }
        }
    }

}
