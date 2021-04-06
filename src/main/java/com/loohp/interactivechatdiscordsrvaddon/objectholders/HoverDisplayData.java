package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import java.awt.Color;

import org.bukkit.entity.Player;

public class HoverDisplayData extends DiscordDisplayData {
	
	private String displayText;
	private String hoverText;
	private Color color;

	public HoverDisplayData(Player player, int position, String displayText, String hoverText, Color color) {
		super(player, position);
		this.displayText = displayText;
		this.hoverText = hoverText;
		this.color = color;
	}

	public String getDisplayText() {
		return displayText;
	}

	public String getHoverText() {
		return hoverText;
	}
	
	public Color getColor() {
		return color;
	}

}
