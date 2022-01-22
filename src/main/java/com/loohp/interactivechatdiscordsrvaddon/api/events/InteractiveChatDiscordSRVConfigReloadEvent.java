package com.loohp.interactivechatdiscordsrvaddon.api.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Fired when /icd reloadconfig is used
 *
 * @author LOOHP
 */
public class InteractiveChatDiscordSRVConfigReloadEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public InteractiveChatDiscordSRVConfigReloadEvent() {
        super(!Bukkit.isPrimaryThread());
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
