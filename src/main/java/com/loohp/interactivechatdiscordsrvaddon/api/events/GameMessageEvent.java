package com.loohp.interactivechatdiscordsrvaddon.api.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This is the base class of all GameMessageEvents
 * @author LOOHP
 *
 */
public class GameMessageEvent extends Event implements Cancellable {

	private Player sender;
	private String message;
	private boolean cancel;

    public GameMessageEvent(Player sender, String message, boolean cancel) {
    	super(!Bukkit.isPrimaryThread());
        this.sender = sender;
        this.message = message;
        this.cancel = cancel;
    }
    
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
    
    public Player getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
