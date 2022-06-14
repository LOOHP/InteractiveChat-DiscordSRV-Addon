/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
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

package com.loohp.interactivechatdiscordsrvaddon.listeners;

import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ReactionsHandler;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordReactionEvents extends ListenerAdapter {

    private static final Map<String, ValuePairs<ReactionsHandler, List<DiscordMessageContent>>> REGISTER = new ConcurrentHashMap<>();

    public static void register(Message message, ReactionsHandler reactionsHandler, List<DiscordMessageContent> discordMessageContent) {
        reactionsHandler.getEmojis().stream().map(each -> message.addReaction(each)).reduce((a, b) -> a.and(b)).ifPresent(each -> each.queue());
        String id = message.getChannel().getId() + "/" + message.getId();
        REGISTER.put(id, new ValuePairs<>(reactionsHandler, discordMessageContent));
        Bukkit.getScheduler().runTaskLaterAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
            if (REGISTER.remove(id) != null) {
                reactionsHandler.getEmojis().stream().map(each -> message.clearReactions(each)).reduce((a, b) -> a.and(b)).ifPresent(each -> each.queue());
            }
        }, reactionsHandler.getExpire() / 50);
    }

    public static void unregisterAll() {
        Iterator<Entry<String, ValuePairs<ReactionsHandler, List<DiscordMessageContent>>>> itr = REGISTER.entrySet().iterator();
        while (itr.hasNext()) {
            Entry<String, ValuePairs<ReactionsHandler, List<DiscordMessageContent>>> entry = itr.next();
            String id = entry.getKey();
            ReactionsHandler reactionsHandler = entry.getValue().getFirst();
            itr.remove();
            Message message = DiscordSRV.getPlugin().getJda().getTextChannelById(id.substring(0, id.indexOf("/"))).retrieveMessageById(id.substring(id.indexOf("/") + 1)).complete();
            reactionsHandler.getEmojis().stream().map(each -> message.clearReactions(each)).reduce((a, b) -> a.and(b)).ifPresent(each -> each.complete());
        }
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        String id = event.getChannel().getId() + "/" + event.getMessageId();
        ValuePairs<ReactionsHandler, List<DiscordMessageContent>> pair = REGISTER.get(id);
        if (pair != null) {
            pair.getFirst().getReactionConsumer().accept(event, pair.getSecond());
        }
    }

}
