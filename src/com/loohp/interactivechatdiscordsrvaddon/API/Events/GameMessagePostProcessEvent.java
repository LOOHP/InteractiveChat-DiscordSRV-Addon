package com.loohp.interactivechatdiscordsrvaddon.API.Events;

import org.bukkit.entity.Player;

public class GameMessagePostProcessEvent extends GameMessageEvent {
	
	/*
	 * This event is called after all other GameMessageEvents and is ready to be sent to discord.
	 */

	public GameMessagePostProcessEvent(Player sender, String message, boolean cancel) {
		super(sender, message, cancel);
	}

}
