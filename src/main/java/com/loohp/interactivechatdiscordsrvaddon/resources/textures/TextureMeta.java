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

package com.loohp.interactivechatdiscordsrvaddon.resources.textures;

import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureAnimation.TextureAnimationFrames;

import java.util.ArrayList;
import java.util.List;

public class TextureMeta extends TextureResource {

    public static TextureMeta fromJson(ITextureManager manager, String resourceKey, ResourcePackFile file, JSONObject rootJson) {
        TextureAnimation animation = null;
        if (rootJson.containsKey("animation")) {
            JSONObject animationJson = (JSONObject) rootJson.get("animation");
            boolean interpolate = (boolean) animationJson.getOrDefault("interpolate", false);
            int width = ((Number) animationJson.getOrDefault("width", -1)).intValue();
            int height = ((Number) animationJson.getOrDefault("height", -1)).intValue();
            int frametime = ((Number) animationJson.getOrDefault("frametime", -1)).intValue();
            JSONArray framesArray = ((JSONArray) animationJson.getOrDefault("frames", new JSONArray()));
            List<TextureAnimationFrames> frames = new ArrayList<>();
            for (Object obj : framesArray) {
                if (obj instanceof Number) {
                    frames.add(new TextureAnimationFrames(((Number) obj).intValue(), frametime));
                } else if (obj instanceof JSONObject) {
                    JSONObject frameJson = (JSONObject) obj;
                    frames.add(new TextureAnimationFrames(((Number) frameJson.get("index")).intValue(), ((Number) frameJson.get("time")).intValue()));
                }
            }
            animation = new TextureAnimation(interpolate, width, height, frametime, frames);
        }
        TextureProperties properties = null;
        if (rootJson.containsKey("texture")) {
            JSONObject propertiesJson = (JSONObject) rootJson.get("texture");
            boolean blur = (boolean) propertiesJson.getOrDefault("blur", false);
            boolean clamp = (boolean) propertiesJson.getOrDefault("clamp", false);
            int[] mipmaps = ((JSONArray) propertiesJson.getOrDefault("mipmaps", new JSONArray())).stream().mapToInt(each -> ((Number) each).intValue()).toArray();
            properties = new TextureProperties(blur, clamp, mipmaps);
        }
        return new TextureMeta(manager, resourceKey, file, animation, properties);
    }

    private TextureAnimation animation;
    private TextureProperties properties;

    public TextureMeta(ITextureManager manager, String resourceKey, ResourcePackFile file, TextureAnimation animation, TextureProperties properties) {
        super(manager, resourceKey, file, false, null);
        this.animation = animation;
        this.properties = properties;
    }

    public TextureAnimation getAnimation() {
        return animation;
    }

    public TextureProperties getProperties() {
        return properties;
    }

    public boolean hasAnimation() {
        return animation != null;
    }

    public boolean hasProperties() {
        return properties != null;
    }

    @Override
    public boolean isTextureMeta() {
        return true;
    }

    @Override
    public boolean hasTextureMeta() {
        return false;
    }

    @Override
    public TextureMeta getTextureMeta() {
        return null;
    }

}
