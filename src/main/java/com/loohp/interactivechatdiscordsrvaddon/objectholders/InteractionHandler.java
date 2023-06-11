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

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.components.ActionRow;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;

public class InteractionHandler {

    private final Collection<? extends ActionRow> interactionToRegister;
    private final List<String> interactions;
    private final long expire;
    private final BiConsumer<GenericComponentInteractionCreateEvent, List<DiscordMessageContent>> reactionConsumer;

    public InteractionHandler(Collection<? extends ActionRow> interactionToRegister, List<String> interactions, long expire, BiConsumer<GenericComponentInteractionCreateEvent, List<DiscordMessageContent>> reactionConsumer) {
        this.interactionToRegister = interactionToRegister;
        this.interactions = interactions;
        this.expire = expire;
        this.reactionConsumer = reactionConsumer;
    }

    public Collection<? extends ActionRow> getInteractionToRegister() {
        return interactionToRegister;
    }

    public List<String> getInteractions() {
        return interactions;
    }

    public long getExpire() {
        return expire;
    }

    public BiConsumer<GenericComponentInteractionCreateEvent, List<DiscordMessageContent>> getReactionConsumer() {
        return reactionConsumer;
    }

}
