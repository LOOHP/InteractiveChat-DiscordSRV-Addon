package com.loohp.interactivechatdiscordsrvaddon.api.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents.DiscordAttachmentData;

/**
 * This event is called when a discord attachment had been processed.
 * @author LOOHP
 *
 */
public class DiscordAttachmentConversionEvent extends Event {
	
	private final String url;
	private final DiscordAttachmentData data;
	
	public DiscordAttachmentConversionEvent(String url, DiscordAttachmentData data) {
		super(!Bukkit.isPrimaryThread());
		this.url = url;
		this.data = data;
	}
	
	public String getUrl() {
		return url;
	}
	
	public DiscordAttachmentData getDiscordAttachmentData() {
		return data;
	}

	private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
