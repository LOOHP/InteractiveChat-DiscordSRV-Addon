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

package com.loohp.interactivechatdiscordsrvaddon.registry;

import com.loohp.interactivechat.utils.ColorUtils;
import com.loohp.interactivechat.utils.CustomStringUtils;

import java.awt.Color;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DiscordDataRegistry {

    public static final Color DISCORD_HOVER_COLOR = ColorUtils.hex2Rgb("#1F083A");
    private static Set<String> markdownChars;
    private static String markdownPattern;

    static {
        markdownChars = new HashSet<>();
        markdownChars.add("\\");
        markdownChars.add("`");
        markdownChars.add("*");
        markdownChars.add("_");
        markdownChars.add("{");
        markdownChars.add("}");
        markdownChars.add("[");
        markdownChars.add("]");
        markdownChars.add("(");
        markdownChars.add(")");
        markdownChars.add("#");
        markdownChars.add("+");
        markdownChars.add("-");
        markdownChars.add(".");
        markdownChars.add("!");
        markdownChars.add(">");
        markdownChars.add("~");
        markdownChars.add(":");

        markdownPattern = "([" + CustomStringUtils.escapeMetaCharacters(markdownChars.stream().collect(Collectors.joining())) + "])";
    }

    public static Set<String> getMarkdownSpecialChars() {
        return Collections.unmodifiableSet(markdownChars);
    }

    public static String getMarkdownSpecialPattern() {
        return markdownPattern;
    }

}
