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

package com.loohp.interactivechatdiscordsrvaddon.api.events;

import com.loohp.interactivechat.libs.com.loohp.platformscheduler.Scheduler;
import com.loohp.interactivechatdiscordsrvaddon.listeners.InboundToGameEvents.DiscordAttachmentData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called when a discord attachment had been processed.
 *
 * @author LOOHP
 */
public class DiscordAttachmentConversionEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private final String url;
    private final DiscordAttachmentData data;

    public DiscordAttachmentConversionEvent(String url, DiscordAttachmentData data) {
        super(!Scheduler.isPrimaryThread());
        this.url = url;
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public DiscordAttachmentData getDiscordAttachmentData() {
        return data;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
