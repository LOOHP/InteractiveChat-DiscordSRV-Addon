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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Pattern;

public class URLRequestUtils {

    public static final Pattern IMAGE_URL_PATTERN = Pattern.compile("https?:\\/(?:\\/[^\\/]+)+\\.(jpg|jpeg|gif|png)(?:\\?.*)*");

    public static InputStream getInputStream(String link) {
        InputStream stream = getInputStream0(link);
        return stream == null ? new ByteArrayInputStream(new byte[0]) : stream;
    }

    public static InputStream getInputStream0(String link) {
        URLConnection connection;
        try {
            connection = new URL(link).openConnection();
            connection.setUseCaches(false);
            connection.setDefaultUseCaches(false);
            connection.addRequestProperty("User-Agent", "Mozilla/5.0");
            connection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
            connection.addRequestProperty("Pragma", "no-cache");
            return connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream retrieveInputStreamUntilSuccessful(List<ThrowingSupplier<InputStream>> sources) {
        InputStream stream = null;
        boolean success = false;
        int i = 0;
        while (!success && i < sources.size()) {
            try {
                stream = sources.get(i).get();
                if (stream != null) {
                    success = true;
                }
            } catch (Throwable e) {
            }
            i++;
        }
        return stream;
    }

    public static boolean isAllowed(String url) {
        if (!InteractiveChatDiscordSrvAddon.plugin.imageWhitelistEnabled) {
            return true;
        }
        for (String possiblyAllowedUrl : InteractiveChatDiscordSrvAddon.plugin.whitelistedImageUrls) {
            if (url.startsWith(possiblyAllowedUrl)) {
                return true;
            }
        }
        return false;
    }

}
