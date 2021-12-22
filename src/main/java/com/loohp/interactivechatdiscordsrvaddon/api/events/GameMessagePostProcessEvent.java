package com.loohp.interactivechatdiscordsrvaddon.api.events;

import com.loohp.interactivechat.objectholders.ICPlayer;

/**
 * This event is called after all other GameMessageEvents and is ready to be sent to discord.
 * @author LOOHP
 *
 */
public class GameMessagePostProcessEvent extends GameMessageEvent {

	public GameMessagePostProcessEvent(ICPlayer sender, String message, boolean cancel) {
		super(sender, message, cancel);
	}

}
