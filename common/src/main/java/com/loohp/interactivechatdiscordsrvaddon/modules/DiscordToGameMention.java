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
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.nms.NMS;
import com.loohp.interactivechat.objectholders.Either;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.SoundUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class DiscordToGameMention {

    public static void playTitleScreen(String sender, String channelName, String guild, Player receiver) {
        Config config = Config.getConfig(InteractiveChatDiscordSrvAddon.CONFIG_ID);

        String title = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordMention.MentionedTitle").replace("{DiscordUser}", sender).replace("{TextChannel}", "#" + channelName).replace("{Guild}", guild));
        String subtitle = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordMention.DiscordMentionSubtitle").replace("{DiscordUser}", sender).replace("{TextChannel}", "#" + channelName).replace("{Guild}", guild));
        String actionbar = ChatColorUtils.translateAlternateColorCodes('&', config.getConfiguration().getString("DiscordMention.DiscordMentionActionbar").replace("{DiscordUser}", sender).replace("{TextChannel}", "#" + channelName).replace("{Guild}", guild));

        String settings = config.getConfiguration().getString("DiscordMention.MentionedSound");
        Either<Sound, String> sound;
        float volume = 3.0F;
        float pitch = 1.0F;

        String[] settingsArgs = settings.split(":");
        if (settingsArgs.length >= 3) {
            settings = String.join("", Arrays.copyOfRange(settingsArgs, 0, settingsArgs.length - 2)).toUpperCase();
            try {
                volume = Float.parseFloat(settingsArgs[settingsArgs.length - 2]);
            } catch (Exception ignore) {
            }
            try {
                pitch = Float.parseFloat(settingsArgs[settingsArgs.length - 1]);
            } catch (Exception ignore) {
            }
        } else {
            settings = settings.toUpperCase();
        }

        Sound bukkitSound = SoundUtils.parseSound(settings);
        if (bukkitSound == null) {
            settings = settings.toLowerCase();
            if (!settings.contains(":")) {
                settings = "minecraft:" + settings;
            }
            sound = Either.right(settings);
        } else {
            sound = Either.left(bukkitSound);
        }

        int time = (int) Math.round(config.getConfiguration().getDouble("DiscordMention.MentionedTitleDuration") * 20);

        Component titleComponent = LegacyComponentSerializer.legacySection().deserialize(title);
        Component subtitleComponent = LegacyComponentSerializer.legacySection().deserialize(subtitle);
        Component actionbarComponent = LegacyComponentSerializer.legacySection().deserialize(actionbar);

        NMS.getInstance().sendTitle(receiver, titleComponent, subtitleComponent, actionbarComponent, 10, time, 20);
        if (sound != null) {
            if (sound.isLeft()) {
                receiver.playSound(receiver.getLocation(), sound.getLeft(), volume, pitch);
            } else {
                receiver.playSound(receiver.getLocation(), sound.getRight(), volume, pitch);
            }
        }
    }

}
