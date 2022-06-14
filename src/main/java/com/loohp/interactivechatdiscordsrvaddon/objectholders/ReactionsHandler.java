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

import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.util.List;
import java.util.function.BiConsumer;

public class ReactionsHandler {

    private List<String> emojis;
    private long expire;
    private BiConsumer<GuildMessageReactionAddEvent, List<DiscordMessageContent>> reactionConsumer;

    public ReactionsHandler(List<String> emojis, long expire, BiConsumer<GuildMessageReactionAddEvent, List<DiscordMessageContent>> reactionConsumer) {
        this.emojis = emojis;
        this.expire = expire;
        this.reactionConsumer = reactionConsumer;
    }

    public List<String> getEmojis() {
        return emojis;
    }

    public long getExpire() {
        return expire;
    }

    public BiConsumer<GuildMessageReactionAddEvent, List<DiscordMessageContent>> getReactionConsumer() {
        return reactionConsumer;
    }

}
