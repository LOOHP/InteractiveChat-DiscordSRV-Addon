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

package com.loohp.interactivechatdiscordsrvaddon.resources.fonts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.loohp.interactivechat.libs.net.kyori.adventure.key.Key;
import com.loohp.interactivechat.libs.org.apache.commons.io.input.BOMInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechat.libs.org.json.simple.parser.ParseException;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.AbstractManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.GeneratedTextureResource;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class FontManager extends AbstractManager implements IFontManager {

    public static final Key DEFAULT_FONT = Key.key("minecraft:default");
    public static final Key UNIFORM_FONT = Key.key("minecraft:uniform");

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    public static JSONObject specialReadProvider(ResourcePackFile file) throws IOException, ParseException {
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

    private Key defaultKey;
    private Map<String, FontProvider> fonts;
    private Map<String, Map<String, ResourcePackFile>> files;

    public FontManager(ResourceManager manager) {
        super(manager);
        this.defaultKey = DEFAULT_FONT;
        this.fonts = new HashMap<>();
        this.files = new HashMap<>();
    }

    @Override
    protected void loadDirectory(String namespace, ResourcePackFile root, Object... meta) {
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException(root.getAbsolutePath() + " is not a directory.");
        }
        Map<String, ResourcePackFile> fileList = files.computeIfAbsent(namespace, k -> new HashMap<>());
        JSONParser parser = new JSONParser();
        Map<String, FontProvider> fonts = new HashMap<>(this.fonts);
        Collection<ResourcePackFile> files = root.listFilesRecursively();
        for (ResourcePackFile file : files) {
            if (!file.isDirectory()) {
                fileList.put(file.getRelativePathFrom(root), file);
            }
        }
        for (ResourcePackFile file : files) {
            if (file.getName().endsWith(".json")) {
                try {
                    String key = namespace + ":" + file.getRelativePathFrom(root);
                    key = key.substring(0, key.lastIndexOf("."));
                    JSONObject rootJson = specialReadProvider(file);
                    List<MinecraftFont> providedFonts = new ArrayList<>();
                    int index = -1;
                    for (Object obj : (JSONArray) rootJson.get("providers")) {
                        index++;
                        JSONObject fontJson = (JSONObject) obj;
                        try {
                            MinecraftFont minecraftFont = MinecraftFont.fromJson(manager, this, null, fontJson);
                            providedFonts.add(minecraftFont);
                        } catch (Exception e) {
                            new ResourceLoadingException("Unable to load font provider " + index + " in " + file.getAbsolutePath(), e).printStackTrace();
                        }
                    }
                    FontProvider existingProvider = fonts.get(key);
                    if (existingProvider == null) {
                        FontProvider provider = new FontProvider(manager, key, providedFonts);
                        for (MinecraftFont mcFont : provider.getProviders()) {
                            mcFont.setProvider(provider);
                        }
                        fonts.put(key, provider);
                    } else {
                        for (MinecraftFont mcFont : providedFonts) {
                            mcFont.setProvider(existingProvider);
                        }
                        existingProvider.prependProviders(providedFonts);
                    }
                } catch (Exception e) {
                    new ResourceLoadingException("Unable to load font " + file.getAbsolutePath(), e).printStackTrace();
                }
            }
        }
        this.fonts.clear();
        this.fonts.putAll(fonts);
    }

    @Override
    protected void filterResources(Pattern namespace, Pattern path) {
        Iterator<String> itr = fonts.keySet().iterator();
        while (itr.hasNext()) {
            String namespacedKey = itr.next();
            String assetNamespace = namespacedKey.substring(0, namespacedKey.indexOf(":"));
            String assetKey = namespacedKey.substring(namespacedKey.indexOf(":") + 1);
            if (!assetKey.contains(".")) {
                assetKey = assetKey + ".json";
            }
            if (namespace.matcher(assetNamespace).matches() && path.matcher(assetKey).matches()) {
                itr.remove();
            }
        }

        for (Entry<String, Map<String, ResourcePackFile>> entry : files.entrySet()) {
            String assetNamespace = entry.getKey();
            Iterator<String> itr2 = entry.getValue().keySet().iterator();
            while (itr2.hasNext()) {
                String assetKey = itr2.next();
                if (!assetKey.contains(".")) {
                    assetKey = assetKey + ".json";
                }
                if (namespace.matcher(assetNamespace).matches() && path.matcher(assetKey).matches()) {
                    itr2.remove();
                }
            }
        }
    }

    @Override
    public Key getDefaultFontKey() {
        return defaultKey;
    }

    public void setDefaultKey(Key defaultKey) {
        this.defaultKey = defaultKey;
    }

    @Override
    protected void reload() {
        for (FontProvider provider : fonts.values()) {
            provider.reloadFonts();
        }
    }

    @Override
    public TextureResource getFontResource(String resourceLocation) {
        String namespace;
        String key;
        if (resourceLocation.contains(":")) {
            namespace = resourceLocation.substring(0, resourceLocation.indexOf(":"));
            key = resourceLocation.substring(resourceLocation.indexOf(":") + 1);
        } else {
            namespace = ResourceRegistry.DEFAULT_NAMESPACE;
            key = resourceLocation;
        }

        if (resourceLocation.endsWith(".png")) {
            return manager.getTextureManager().getTexture(resourceLocation, false);
        } else {
            Map<String, ResourcePackFile> fileList = files.get(namespace);
            if (fileList == null) {
                return null;
            }
            ResourcePackFile current0 = fileList.get(key);
            if (current0 != null && current0.exists()) {
                return new GeneratedTextureResource(manager, current0);
            }
            ResourcePackFile current1 = fileList.get(key.replace("font/", ""));
            if (current1 != null && current1.exists()) {
                return new GeneratedTextureResource(manager, current1);
            }
        }
        return null;
    }

    @Override
    public FontProvider getFontProviders(Key resourceKey) {
        return getFontProviders(resourceKey == null ? getDefaultFontKey().asString() : resourceKey.asString());
    }

    @Override
    public FontProvider getFontProviders(String resourceLocation) {
        if (!resourceLocation.contains(":")) {
            resourceLocation = ResourceRegistry.DEFAULT_NAMESPACE + ":" + resourceLocation;
        }
        String defaultFont = defaultKey.asString();
        return fonts.getOrDefault(resourceLocation, resourceLocation.equals(defaultFont) ? null : getFontProviders(defaultFont));
    }

}
