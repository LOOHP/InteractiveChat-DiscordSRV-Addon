package com.loohp.interactivechatdiscordsrvaddon.api.events;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.objectholders.ICPlayer;

/**
 * This event is called before the plugin process the placeholders within a
 * message from discordsrv that is about to be sent to discord.
 * @author LOOHP
 *
 */
public class GameMessagePreProcessEvent extends GameMessageEvent {

	public GameMessagePreProcessEvent(ICPlayer sender, Component component, boolean cancel) {
		super(sender, component, cancel);
	}

}
