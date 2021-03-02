package com.loohp.interactivechatdiscordsrvaddon.ObjectHolders;

import org.bukkit.entity.Player;

public abstract class DiscordDisplayData {
	
	protected final Player player;
	protected final int postion;
	
	public DiscordDisplayData(Player player, int postion) {
		this.player = player;
		this.postion = postion;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public int getPosition() {
		return postion;
	}

}
