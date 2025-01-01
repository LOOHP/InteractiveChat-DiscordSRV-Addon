/*
 * This file is part of InteractiveChatDiscordSrvAddon.
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

package com.loohp.interactivechatdiscordsrvaddon.listeners;

import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.HashUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.metrics.Metrics;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.InteractionHandler;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.ButtonClickEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SelectionMenuEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordInteractionEvents extends ListenerAdapter {

    public static final String INTERACTION_ID_PREFIX;

    static {
        try {
            String uuid = Metrics.getServerUUID();
            if (uuid == null) {
                uuid = UUID.randomUUID().toString();
            }
            INTERACTION_ID_PREFIX = "ICD_" + HashUtils.createSha1String(new ByteArrayInputStream(uuid.getBytes(StandardCharsets.UTF_8))) + "_";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final Map<String, InteractionData> REGISTER = new ConcurrentHashMap<>();

    public static void register(Message message, InteractionHandler interactionHandler, List<DiscordMessageContent> discordMessageContent) {
        String messageId = message.getChannel().getId() + "/" + message.getId();
        List<String> interactionIds = interactionHandler.getInteractions();
        InteractionData interactionData = new InteractionData(interactionHandler, discordMessageContent, interactionIds, messageId);
        for (String id : interactionIds) {
            if (!id.startsWith(INTERACTION_ID_PREFIX)) {
                throw new IllegalArgumentException("InteractionIds must start with the INTERACTION_ID_PREFIX, however \"" + id + "\" does not");
            }
            REGISTER.put(id, interactionData);
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
            for (String id : interactionIds) {
                REGISTER.remove(id);
            }
        }, interactionHandler.getExpire() / 50);
    }

    public static InteractionData getInteractionData(String interactionId) {
        return REGISTER.get(interactionId);
    }

    public static void unregisterAll() {
        REGISTER.clear();
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        handleInteraction(event);
    }

    @Override
    public void onSelectionMenu(SelectionMenuEvent event) {
        handleInteraction(event);
    }

    private void handleInteraction(GenericComponentInteractionCreateEvent event) {
        String id = event.getComponent().getId();
        if (!id.startsWith(INTERACTION_ID_PREFIX)) {
            return;
        }
        InteractionData data = REGISTER.get(id);
        if (data != null) {
            data.getInteractionHandler().getReactionConsumer().accept(event, data.getContents());
            return;
        }
        event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.interactionExpire)).setEphemeral(true).queue();
    }

    public static class InteractionData {

        private InteractionHandler interactionHandler;
        private List<DiscordMessageContent> contents;
        private List<String> interactionIds;
        private List<String> messageIds;

        public InteractionData(InteractionHandler interactionHandler, List<DiscordMessageContent> contents, List<String> interactionIds, List<String> messageIds) {
            this.interactionHandler = interactionHandler;
            this.contents = contents;
            this.interactionIds = interactionIds;
            this.messageIds = messageIds;
        }

        public InteractionData(InteractionHandler interactionHandler, List<DiscordMessageContent> contents, List<String> interactionIds, String messageId) {
            this.interactionHandler = interactionHandler;
            this.contents = contents;
            this.interactionIds = interactionIds;
            List<String> messageIds = new ArrayList<>();
            messageIds.add(messageId);
            this.messageIds = messageIds;
        }

        public InteractionHandler getInteractionHandler() {
            return interactionHandler;
        }

        public List<DiscordMessageContent> getContents() {
            return contents;
        }

        public List<String> getInteractionIds() {
            return interactionIds;
        }

        public List<String> getMessageIds() {
            return messageIds;
        }

    }

}
