/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2024. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2024. Contributors
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
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.libs.org.json.simple.parser.ParseException;
import com.loohp.interactivechat.utils.SkinUtils;
import com.loohp.interactivechatdiscordsrvaddon.nms.NMSAddon;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;

import java.util.Base64;
import java.util.Collection;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameProfileUtils {

    private static final Pattern WEIRD_SKULL_TEXTURE_PATTERN = Pattern.compile("\\{(\\\"?textures\\\"?):\\{(?:(\\\"?SKIN\\\"?):\\{(?:(\\\"?url\\\"?):\\\".*:\\/\\/.*\\\")?})?}}");
    private static final UnaryOperator<String> FIX_WEIRD_SKULL_TEXTURE = str -> {
        str = str.trim();
        StringBuilder sb = new StringBuilder(str);
        Matcher matcher = WEIRD_SKULL_TEXTURE_PATTERN.matcher(str);
        if (matcher.find()) {
            int offset = 0;
            for (int i = 1; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);
                if (group != null) {
                    if (!group.startsWith("\"")) {
                        sb.insert(matcher.start(i) + offset++, "\"");
                    }
                    if (!group.endsWith("\"")) {
                        sb.insert(matcher.end(i) + offset++, "\"");
                    }
                }
            }
        }
        return sb.toString();
    };
    private static final UUID UUID_ZERO = new UUID(0, 0);

    public static boolean hasValidUUID(GameProfile gameProfile) {
        return gameProfile.getId() != UUID_ZERO;
    }

    public static boolean hasValidName(GameProfile gameProfile) {
        return !gameProfile.getName().isEmpty();
    }

    @SuppressWarnings({"deprecation", "CallToPrintStackTrace"})
    public static String getSkinUrl(GameProfile gameProfile) {
        try {
            if (gameProfile == null) {
                return null;
            }
            Collection<Property> textures = gameProfile.getProperties().get("textures");
            if (textures != null && !textures.isEmpty()) {
                String value = NMSAddon.getInstance().toProfileProperty(textures.iterator().next()).getValue();
                String json = FIX_WEIRD_SKULL_TEXTURE.apply(new String(Base64.getDecoder().decode(value)));
                try {
                    JSONObject texturesJson = (JSONObject) ((JSONObject) new JSONParser().parse(json)).get("textures");
                    if (texturesJson != null) {
                        JSONObject skinJson = (JSONObject) texturesJson.get("SKIN");
                        if (skinJson != null) {
                            return (String) skinJson.get("url");
                        }
                    }
                } catch (ParseException e) {
                    new IllegalArgumentException("Skull contains illegal texture data: \n" + json, e).printStackTrace();
                }
            }
            boolean validUUID = hasValidUUID(gameProfile);
            boolean validName = hasValidName(gameProfile);
            if (!validUUID || !validName) {
                if (validUUID) {
                    return SkinUtils.getSkinURLFromUUID(gameProfile.getId());
                }
                if (validName) {
                    return SkinUtils.getSkinURLFromUUID(Bukkit.getOfflinePlayer(gameProfile.getName()).getUniqueId());
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
