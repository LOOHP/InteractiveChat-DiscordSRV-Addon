package com.loohp.interactivechatdiscordsrvaddon.API.Events;

import org.bukkit.entity.Player;

/**
 * This is the base class of all GameMessageProcessEvents
 * @author LOOHP
 *
 */
public class GameMessageProcessEvent extends GameMessageEvent {

	private int processId;
	private String title;

	public GameMessageProcessEvent(Player sender, String title, String message, boolean cancel, int processId) {
		super(sender, message, cancel);
		this.processId = processId;
		this.title = title;
	}

	public int getProcessId() {
		return processId;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
