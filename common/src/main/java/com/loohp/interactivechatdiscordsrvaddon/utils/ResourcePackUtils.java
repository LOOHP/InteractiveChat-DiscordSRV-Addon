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

import com.loohp.interactivechatdiscordsrvaddon.nms.NMSAddon;

public class ResourcePackUtils {

    public static String getServerResourcePack() {
        return NMSAddon.getInstance().getServerResourcePack();
    }

    public static String getServerResourcePackHash() {
        return NMSAddon.getInstance().getServerResourcePackHash();
    }

    public static int getServerResourcePackVersion() {
        return NMSAddon.getInstance().getServerResourcePackVersion();
    }

}
