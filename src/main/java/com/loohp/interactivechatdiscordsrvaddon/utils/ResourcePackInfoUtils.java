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

import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackInfo;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackType;

public class ResourcePackInfoUtils {

    public static Component resolveName(ResourcePackInfo info) {
        return resolveName(info.getName(), info.getType());
    }

    public static Component resolveName(Component name, ResourcePackType type) {
        switch (type) {
            case BUILT_IN:
            case LOCAL:
                return name;
            case WORLD:
            case SERVER:
                return Component.translatable(TranslationKeyUtils.getWorldSpecificResources());
        }
        return name;
    }

    public static Component resolveDescription(ResourcePackInfo info) {
        return resolveDescription(info.getDescription(), info.getType());
    }

    public static Component resolveDescription(Component component, ResourcePackType type) {
        String space = PlainTextComponentSerializer.plainText().serialize(component).isEmpty() ? "" : " ";
        switch (type) {
            case BUILT_IN:
            case WORLD:
            case SERVER:
                component = component.append(Component.empty().append(Component.text(space + "(").append(Component.translatable(TranslationKeyUtils.getServerResourcePackType(type))).append(Component.text(")"))).color(NamedTextColor.GRAY));
                break;
        }
        return component;
    }

}
