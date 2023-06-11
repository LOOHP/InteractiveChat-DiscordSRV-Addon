/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.objectholders.CustomPlaceholder.ClickEventAction;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;

import java.awt.Color;

public class HoverClickDisplayData extends DiscordDisplayData {

    private final String displayText;
    private final Component hoverText;
    private final Color color;
    private final ClickEventAction clickAction;
    private final String clickValue;

    public HoverClickDisplayData(OfflineICPlayer player, int position, Color color, String displayText, Component hoverText, ClickEventAction clickAction, String clickValue) {
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
