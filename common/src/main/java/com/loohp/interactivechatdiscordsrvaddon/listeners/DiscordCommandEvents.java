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

import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordCommandDataUtils;
import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.GuildSlashCommandUpdateEvent;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.Command;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;

import java.util.List;
import java.util.Set;

public class DiscordCommandEvents {

    @Subscribe(priority = ListenerPriority.HIGHEST)
    public void onSlashCommandUpdate(GuildSlashCommandUpdateEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Guild guild = event.getGuild();
        if (DiscordSRV.getPlugin().getMainGuild().equals(guild)) {
            return;
        }
        Set<CommandData> commands = event.getCommands();
        List<Command> originalCommands = guild.retrieveCommands().complete();
        for (Command command : originalCommands) {
            if (DiscordCommands.DISCORD_COMMANDS.contains(command.getName())) {
                commands.add(DiscordCommandDataUtils.toCommandData(command));
            }
        }
    }

}
