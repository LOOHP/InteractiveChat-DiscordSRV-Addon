package com.loohp.interactivechatdiscordsrvaddon.api.events;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.objectholders.ICPlayer;
import org.bukkit.Bukkit;
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
    private ICPlayer sender;
    private Component component;
    private boolean cancel;

    public GameMessageEvent(ICPlayer sender, Component component, boolean cancel) {
        super(!Bukkit.isPrimaryThread());
        this.sender = sender;
        this.component = component;
        this.cancel = cancel;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
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

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
