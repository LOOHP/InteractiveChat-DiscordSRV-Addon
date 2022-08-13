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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.Command;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.SubcommandData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.SubcommandGroupData;

public class DiscordCommandDataUtils {

    public static CommandData toCommandData(Command command) {
        CommandData commandData = new CommandData(command.getName(), command.getDescription()).setDefaultEnabled(command.isDefaultEnabled());
        for (Command.Option option : command.getOptions()) {
            commandData.addOptions(toCommandData(option));
        }
        for (Command.Subcommand subcommand : command.getSubcommands()) {
            commandData.addSubcommands(toCommandData(subcommand));
        }
        for (Command.SubcommandGroup subcommandGroup : command.getSubcommandGroups()) {
            commandData.addSubcommandGroups(toCommandData(subcommandGroup));
        }
        return commandData;
    }

    public static SubcommandData toCommandData(Command.Subcommand command) {
        SubcommandData subcommandData = new SubcommandData(command.getName(), command.getDescription());
        for (Command.Option option : command.getOptions()) {
            subcommandData.addOptions(toCommandData(option));
        }
        return subcommandData;
    }

    public static SubcommandGroupData toCommandData(Command.SubcommandGroup command) {
        SubcommandGroupData subcommandGroupData = new SubcommandGroupData(command.getName(), command.getDescription());
        for (Command.Subcommand subcommand : command.getSubcommands()) {
            subcommandGroupData.addSubcommands(toCommandData(subcommand));
        }
        return subcommandGroupData;
    }

    public static OptionData toCommandData(Command.Option option) {
        OptionData optionData = new OptionData(option.getType(), option.getName(), option.getDescription(), option.isRequired());
        if (option.getType() == OptionType.NUMBER) {
            if (option.getMaxValue() instanceof Double) {
                optionData.setMaxValue(option.getMaxValue().doubleValue());
            } else {
                optionData.setMaxValue(option.getMaxValue().longValue());
            }
            if (option.getMinValue() instanceof Double) {
                optionData.setMinValue(option.getMinValue().doubleValue());
            } else {
                optionData.setMinValue(option.getMinValue().longValue());
            }
        }
        for (Command.Choice choice : option.getChoices()) {
            if (option.getType() == OptionType.INTEGER) {
                optionData.addChoice(choice.getName(), choice.getAsLong());
            } else if (option.getType() == OptionType.STRING) {
                optionData.addChoice(choice.getName(), choice.getAsString());
            } else {
                if (option.getType() != OptionType.NUMBER) {
                    throw new IllegalArgumentException("Cannot add choice for type " + option.getType());
                }
                optionData.addChoice(choice.getName(), choice.getAsDouble());
            }
        }
        if (optionData.getType() == OptionType.CHANNEL) {
            optionData.setChannelTypes(option.getChannelTypes());
        }
        return optionData;
    }

}
