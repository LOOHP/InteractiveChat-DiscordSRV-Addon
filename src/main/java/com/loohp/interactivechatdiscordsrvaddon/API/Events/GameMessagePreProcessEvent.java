package com.loohp.interactivechatdiscordsrvaddon.API.Events;

import org.bukkit.entity.Player;

/**
 * This event is called before the plugin process the placeholders within a
 * message from discordsrv that is about to be sent to discord.
 * @author LOOHP
 *
 */
public class GameMessagePreProcessEvent extends GameMessageEvent {

	public GameMessagePreProcessEvent(Player sender, String message, boolean cancel) {
		super(sender, message, cancel);
	}

}
