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

package com.loohp.interactivechatdiscordsrvaddon.resources.mods.optifine;

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.ComponentIteratorType;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TextComponent;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;

import java.util.function.UnaryOperator;

public class OptifineUtils {

    public static String componentToString(Component component, UnaryOperator<String> translateFunction) {
        StringBuilder sb = new StringBuilder();
        for (Component each : ComponentStringUtils.resolve(component, translateFunction).iterable(ComponentIteratorType.DEPTH_FIRST)) {
            if (each instanceof TextComponent) {
                sb.append(((TextComponent) each).content());
            }
        }
        return sb.toString();
    }

}
