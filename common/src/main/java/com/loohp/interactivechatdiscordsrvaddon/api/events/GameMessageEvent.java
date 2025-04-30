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
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.objectholders.ICPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This is the base class of all GameMessageEvents
 *
 * @author LOOHP
 */
public class GameMessageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private ICPlayer sender;
    private Component component;
    private boolean cancel;

    public GameMessageEvent(ICPlayer sender, Component component, boolean cancel) {
        super(!Scheduler.isPrimaryThread());
        this.sender = sender;
        this.component = component;
        this.cancel = cancel;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public ICPlayer getSender() {
        return sender;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    @Deprecated
    public String getMessage() {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    @Deprecated
    public void setMessage(String message) {
        this.component = PlainTextComponentSerializer.plainText().deserialize(message);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
