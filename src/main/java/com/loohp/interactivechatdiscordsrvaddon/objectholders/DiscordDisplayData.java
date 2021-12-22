package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import com.loohp.interactivechat.objectholders.ICPlayer;

public abstract class DiscordDisplayData {
	
	protected final ICPlayer player;
	protected final int postion;
	
	public DiscordDisplayData(ICPlayer player, int postion) {
		this.player = player;
		this.postion = postion;
	}
	
	public ICPlayer getPlayer() {
		return player;
	}
	
	public int getPosition() {
		return postion;
	}

}
