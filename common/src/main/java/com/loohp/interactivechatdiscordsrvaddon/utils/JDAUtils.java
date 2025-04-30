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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.privileges.CommandPrivilege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JDAUtils {

    private static final Pattern VALID_ID = Pattern.compile("[0-9]+");

    public static List<Role> toRoles(Guild guild, Collection<String> roles) {
        List<Role> result = new ArrayList<>();
        for (String str : roles) {
            if (str.equals("everyone")) {
                result.add(guild.getPublicRole());
            } else {
                Role role = null;
                if (VALID_ID.matcher(str).matches()) {
                    role = guild.getRoleById(str);
                }
                if (role == null) {
                    result.addAll(guild.getRolesByName(str, false));
                } else {
                    result.add(role);
                }
            }
        }
        return result;
    }

    public static List<CommandPrivilege> toWhitelistedCommandPrivileges(Guild guild, Collection<Role> roles) {
        return roles.stream().map(each -> CommandPrivilege.enableRole(each.getIdLong())).collect(Collectors.toList());
    }

}
