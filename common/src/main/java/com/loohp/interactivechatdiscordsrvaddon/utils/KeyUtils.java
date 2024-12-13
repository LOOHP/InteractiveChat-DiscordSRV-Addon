/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2023. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2023. Contributors
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

import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;

public class KeyUtils {

    @SuppressWarnings("PatternValidation")
    public static Key toKey(String namespacedKey) {
        return Key.key(namespacedKey);
    }

    @SuppressWarnings("PatternValidation")
    public static Key toKey(NamespacedKey namespacedKey) {
        return Key.key(namespacedKey.getNamespace(), namespacedKey.getKey());
    }

    @SuppressWarnings("UnstableApiUsage")
    public static NamespacedKey fromKey(Key key) {
        return new NamespacedKey(key.namespace(), key.value());
    }

}
