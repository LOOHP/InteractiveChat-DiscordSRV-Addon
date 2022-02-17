/*
 * This file is part of InteractiveChatDiscordSrvAddon.
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

package com.loohp.interactivechatdiscordsrvaddon.modules;

import com.loohp.interactivechat.config.Config;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.SoundUtils;
import com.loohp.interactivechat.utils.TitleUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class DiscordToGameMention {

    public static void playTitleScreen(String sender, String channelName, String guild, Player reciever) {
        Config config = Config.getConfig(InteractiveChatDiscordSrvAddon.CONFIG_ID);

        String title = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordMention.MentionedTitle").replace("{DiscordUser}", sender).replace("{TextChannel}", "#" + channelName).replace("{Guild}", guild));
        String subtitle = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordMention.DiscordMentionSubtitle").replace("{DiscordUser}", sender).replace("{TextChannel}", "#" + channelName).replace("{Guild}", guild));
        String actionbar = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordMention.DiscordMentionActionbar").replace("{DiscordUser}", sender).replace("{TextChannel}", "#" + channelName).replace("{Guild}", guild));

        String settings = config.getConfiguration().getString("DiscordMention.MentionedSound");
        Sound sound = null;
        float volume = 3.0F;
        float pitch = 1.0F;

        String[] settingsArgs = settings.split(":");
        if (settingsArgs.length == 3) {
            settings = settingsArgs[0];
            try {
                volume = Float.parseFloat(settingsArgs[1]);
            } catch (Exception ignore) {
            }
            try {
                pitch = Float.parseFloat(settingsArgs[2]);
            } catch (Exception ignore) {
            }
        } else if (settingsArgs.length > 0) {
            settings = settingsArgs[0];
        }

        sound = SoundUtils.parseSound(settings);
        if (sound == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Invalid Sound: " + settings);
        }

        int time = (int) Math.round(config.getConfiguration().getDouble("DiscordMention.MentionedTitleDuration") * 20);
        TitleUtils.sendTitle(reciever, title, subtitle, actionbar, 10, time, 20);
        if (sound != null) {
            reciever.playSound(reciever.getLocation(), sound, volume, pitch);
        }
    }

}
