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

package com.loohp.interactivechatdiscordsrvaddon.resources.languages;

public class LanguageMeta {

    private final String language;
    private final String region;
    private final String name;
    private final boolean bidirectional;

    public LanguageMeta(String language, String region, String name, boolean bidirectional) {
        this.language = language;
        this.region = region;
        this.name = name;
        this.bidirectional = bidirectional;
    }

    public String getLanguage() {
        return language;
    }

    public String getRegion() {
        return region;
    }

    public String getName() {
        return name;
    }

    public boolean isBidirectional() {
        return bidirectional;
    }

}