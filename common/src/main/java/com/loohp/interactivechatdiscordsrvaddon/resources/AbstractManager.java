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

package com.loohp.interactivechatdiscordsrvaddon.resources;

import com.loohp.interactivechat.libs.com.google.gson.Gson;
import com.loohp.interactivechat.libs.com.google.gson.GsonBuilder;
import com.loohp.interactivechat.libs.com.google.gson.JsonObject;
import com.loohp.interactivechat.libs.com.google.gson.stream.JsonReader;
import com.loohp.interactivechat.libs.org.apache.commons.io.input.BOMInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.libs.org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public abstract class AbstractManager implements IAbstractManager {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static JSONObject readJSONObject(ResourcePackFile file) throws IOException, ParseException {
        try (InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8)) {
            return (JSONObject) new JSONParser().parse(reader);
        } catch (ParseException e) {
            try (InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8)) {
                JsonReader jsonReader = new JsonReader(reader);
                jsonReader.setLenient(false);
                JsonObject jsonObject = GSON.getAdapter(JsonObject.class).read(jsonReader);
                String json = GSON.toJson(jsonObject);
                return (JSONObject) new JSONParser().parse(json);
            }
        }
    }

    protected final ResourceManager manager;

    public AbstractManager(ResourceManager manager) {
        this.manager = manager;
    }

    @Override
    public ResourceManager getManager() {
        return manager;
    }

    @Override
    public boolean isValid() {
        return manager.isValid();
    }

    protected abstract void loadDirectory(String namespace, ResourcePackFile root, Object... meta);

    protected abstract void filterResources(Pattern namespace, Pattern path);

    protected abstract void reload();

}
