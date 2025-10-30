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

import com.loohp.interactivechat.libs.org.json.simple.JSONObject;

import java.util.function.Function;

public class JsonLenientUtils {

    public static <T> T getLenientOrDefault(JSONObject jsonObject, String key, T defaultValue, Class<T> typeClass, Function<String, T> parser) {
        Object value = jsonObject.getOrDefault(key, defaultValue);
        if (value == null) {
            return defaultValue;
        }
        if (typeClass.isInstance(value)) {
            return (T) value;
        }
        return parser.apply(value.toString());
    }

    public static boolean getBooleanLenientOrDefault(JSONObject jsonObject, String key, boolean defaultValue) {
        return getLenientOrDefault(jsonObject, key, defaultValue, Boolean.class, s -> Boolean.parseBoolean(s));
    }

}
