/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
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

import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.TextColor;

public class AttributeBase {

    private final String descriptionId;
    private final boolean syncable;
    private final AttributeSentiment sentiment;
    private final boolean hidden;

    public AttributeBase(String descriptionId, boolean syncable, AttributeSentiment sentiment, boolean hidden) {
        this.syncable = syncable;
        this.descriptionId = descriptionId;
        this.sentiment = sentiment;
        this.hidden = hidden;
    }

    public AttributeBase(String descriptionId, boolean syncable, AttributeSentiment sentiment) {
        this(descriptionId, syncable, AttributeSentiment.POSITIVE, false);
    }

    public AttributeBase(String descriptionId, boolean syncable) {
        this(descriptionId, syncable, AttributeSentiment.POSITIVE);
    }

    public AttributeBase(String descriptionId) {
        this(descriptionId, true);
    }

    public String getDescriptionId() {
        return descriptionId;
    }

    public boolean isClientSyncable() {
        return syncable;
    }

    public AttributeSentiment getSentiment() {
        return sentiment;
    }

    public boolean isHidden() {
        return hidden;
    }

    public TextColor getStyle(boolean isAmountPositive) {
        return sentiment.getStyle(isAmountPositive);
    }

    public enum AttributeSentiment {

        POSITIVE, NEUTRAL, NEGATIVE;

        private static final AttributeSentiment[] VALUES = values();

        public static AttributeSentiment fromNMS(Enum<?> nmsSentiment) {
            return VALUES[nmsSentiment.ordinal()];
        }

        public TextColor getStyle(boolean isAmountPositive) {
            switch (ordinal()) {
                case 0:
                    return isAmountPositive ? NamedTextColor.BLUE : NamedTextColor.RED;
                case 1:
                    return NamedTextColor.GRAY;
                case 2:
                    return isAmountPositive ? NamedTextColor.RED : NamedTextColor.BLUE;
                default:
                    throw new IllegalArgumentException();
            }
        }

    }
}

