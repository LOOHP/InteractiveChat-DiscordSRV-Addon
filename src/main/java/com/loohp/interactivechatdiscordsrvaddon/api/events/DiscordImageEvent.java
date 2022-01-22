package com.loohp.interactivechatdiscordsrvaddon.api.events;

import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * This event is called after the plugin deletes the original message on discord
 * and generates the required images, but before the new discord messages are
 * sent.
 * <p>
 * Cancelling this even causes the plugin to resend the oringal message back to
 * discord.
 *
 * @author LOOHP
 */
public class DiscordImageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private TextChannel channel;
    private String originalMessage;
    private String newMessage;
    private List<DiscordMessageContent> discordMessageContents;
    private boolean cancel;

    public DiscordImageEvent(TextChannel channel, String originalMessage, String newMessage,
                             List<DiscordMessageContent> discordMessageContents, boolean cancel, boolean async) {
        super(async);
        this.channel = channel;
        this.originalMessage = originalMessage;
        this.newMessage = newMessage;
        this.discordMessageContents = discordMessageContents;
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

    public TextChannel getChannel() {
        return channel;
    }

    public void setChannel(TextChannel channel) {
        this.channel = channel;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public List<DiscordMessageContent> getDiscordMessageContents() {
        return discordMessageContents;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
