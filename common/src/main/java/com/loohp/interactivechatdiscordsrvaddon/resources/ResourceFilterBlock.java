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

package com.loohp.interactivechatdiscordsrvaddon.resources;

import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ResourceFilterBlock {

    public static final String MATCH_ALL = ".*";

    public static ResourceFilterBlock fromJson(JSONObject json) {
        Pattern namespace = Pattern.compile((String) json.getOrDefault("namespace", MATCH_ALL));
        Pattern path = Pattern.compile((String) json.getOrDefault("path", MATCH_ALL));
        return new ResourceFilterBlock(namespace, path);
    }

    public static List<ResourceFilterBlock> fromJson(JSONArray array) {
        List<ResourceFilterBlock> list = new ArrayList<>(array.size());
        for (Object obj : array) {
            list.add(fromJson((JSONObject) obj));
        }
        return list;
    }

    private final Pattern namespace;
    private final Pattern path;

    public ResourceFilterBlock(Pattern namespace, Pattern path) {
        this.namespace = namespace;
        this.path = path;
    }

    public Pattern getNamespace() {
        return namespace;
    }

    public Pattern getPath() {
        return path;
    }

}
