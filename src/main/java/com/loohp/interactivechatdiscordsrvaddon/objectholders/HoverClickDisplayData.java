package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import java.awt.Color;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.objectholders.CustomPlaceholder.ClickEventAction;
import com.loohp.interactivechat.objectholders.ICPlayer;

public class HoverClickDisplayData extends DiscordDisplayData {
	
	private String displayText;
	private Component hoverText;
	private Color color;
	private ClickEventAction clickAction;
	private String clickValue;

	public HoverClickDisplayData(ICPlayer player, int position, Color color, String displayText, Component hoverText, ClickEventAction clickAction, String clickValue) {
		super(player, position);
		this.displayText = displayText;
		this.hoverText = hoverText;
		this.color = color;
		this.clickAction = clickAction;
		this.clickValue = clickValue;
	}

	public String getDisplayText() {
		return displayText;
	}

	public Component getHoverText() {
		return hoverText;
	}
	
	public boolean hasHover() {
		return hoverText != null;
	}
	
	public Color getColor() {
		return color;
	}
	
	public ClickEventAction getClickAction() {
		return clickAction;
	}

	public String getClickValue() {
		return clickValue;
	}
	
	public boolean hasClick() {
		return clickAction != null && clickValue != null;
	}
	
	public static class Builder {
		
		private ICPlayer player;
		private Integer postion;
		private String displayText;
		private Component hoverText;
		private Color color;
		private ClickEventAction clickAction;
		private String clickValue;
		
		public Builder() {
			
		}

		public ICPlayer getPlayer() {
			return player;
		}

		public Builder player(ICPlayer player) {
			this.player = player;
			return this;
		}

		public Integer getPostion() {
			return postion;
		}

		public Builder postion(int postion) {
			this.postion = postion;
			return this;
		}

		public String getDisplayText() {
			return displayText;
		}

		public Builder displayText(String displayText) {
			this.displayText = displayText;
			return this;
		}

		public Component getHoverText() {
			return hoverText;
		}

		public Builder hoverText(Component hoverText) {
			this.hoverText = hoverText;
			return this;
		}

		public Color getColor() {
			return color;
		}

		public Builder color(Color color) {
			this.color = color;
			return this;
		}
		
		public ClickEventAction getClickAction() {
			return clickAction;
		}

		public Builder clickAction(ClickEventAction clickAction) {
			this.clickAction = clickAction;
			return this;
		}

		public String getClickValue() {
			return clickValue;
		}

		public Builder clickValue(String clickValue) {
			this.clickValue = clickValue;
			return this;
		}

		public HoverClickDisplayData build() {
			if (player == null) {
				throw new IllegalStateException("player must be provided");
			}
			if (postion == null) {
				throw new IllegalStateException("postion must be provided");
			}
			if (color == null) {
				throw new IllegalStateException("color must be provided");
			}
			if (displayText == null) {
				throw new IllegalStateException("displayText must be provided");
			}
			return new HoverClickDisplayData(player, postion, color, displayText, hoverText, clickAction, clickValue);
		}
		
	}

}
