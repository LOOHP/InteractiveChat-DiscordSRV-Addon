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

package com.loohp.interactivechatdiscordsrvaddon.resources.textures;

import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureAnimation.TextureAnimationFrames;

import java.util.ArrayList;
import java.util.List;

public class TextureMeta extends TextureResource {

    @SuppressWarnings("DuplicateBranchesInSwitch")
    public static TextureMeta fromJson(ITextureManager manager, String resourceKey, ResourcePackFile file, JSONObject rootJson) {
        TextureAnimation animation = null;
        if (rootJson.containsKey("animation")) {
            JSONObject animationJson = (JSONObject) rootJson.get("animation");
            boolean interpolate = Boolean.parseBoolean(animationJson.getOrDefault("interpolate", false).toString());
            int width = ((Number) animationJson.getOrDefault("width", -1)).intValue();
            int height = ((Number) animationJson.getOrDefault("height", -1)).intValue();
            int frametime = ((Number) animationJson.getOrDefault("frametime", 1)).intValue();
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
            boolean blur = Boolean.parseBoolean(propertiesJson.getOrDefault("blur", false).toString());
            boolean clamp = Boolean.parseBoolean(propertiesJson.getOrDefault("clamp", false).toString());
            int[] mipmaps = ((JSONArray) propertiesJson.getOrDefault("mipmaps", new JSONArray())).stream().mapToInt(each -> ((Number) each).intValue()).toArray();
            properties = new TextureProperties(blur, clamp, mipmaps);
        }
        TextureGui gui = null;
        if (rootJson.containsKey("gui")) {
            TextureGui.Scaling<?> scaling = null;
            JSONObject guiJson = (JSONObject) rootJson.get("gui");
            if (guiJson.containsKey("scaling")) {
                JSONObject scalingJson = (JSONObject) guiJson.get("scaling");
                String type = (String) scalingJson.get("type");
                switch (type) {
                    case "stretch": {
                        scaling = new TextureGui.Scaling<>(TextureGui.ScalingType.STRETCH, new TextureGui.StretchScalingProperty());
                        break;
                    }
                    case "tile": {
                        int width = ((Number) scalingJson.get("width")).intValue();
                        int height = ((Number) scalingJson.get("height")).intValue();
                        scaling = new TextureGui.Scaling<>(TextureGui.ScalingType.TILE, new TextureGui.TileScalingProperty(width, height));
                        break;
                    }
                    case "nine_slice": {
                        int width = ((Number) scalingJson.get("width")).intValue();
                        int height = ((Number) scalingJson.get("height")).intValue();
                        boolean stretchInner = (boolean) scalingJson.getOrDefault("stretch_inner", false);
                        int borderLeft;
                        int borderTop;
                        int borderRight;
                        int borderBottom;
                        Object border = scalingJson.get("border");
                        if (border instanceof Number) {
                            int value = ((Number) border).intValue();
                            borderLeft = value;
                            borderTop = value;
                            borderRight = value;
                            borderBottom = value;
                        } else if (border instanceof JSONObject) {
                            JSONObject boarderJson = (JSONObject) border;
                            borderLeft = ((Number) boarderJson.get("left")).intValue();
                            borderTop = ((Number) boarderJson.get("top")).intValue();
                            borderRight = ((Number) boarderJson.get("right")).intValue();
                            borderBottom = ((Number) boarderJson.get("bottom")).intValue();
                        } else {
                            throw new IllegalArgumentException("Invalid type for boarder properties \"" + border.getClass() + "\"");
                        }
                        scaling = new TextureGui.Scaling<>(TextureGui.ScalingType.NINE_SLICE, new TextureGui.NineSliceScalingProperty(width, height, borderLeft, borderTop, borderRight, borderBottom, stretchInner));
                        break;
                    }
                    default: {
                        scaling = new TextureGui.Scaling<>(TextureGui.ScalingType.STRETCH, new TextureGui.StretchScalingProperty());
                        break;
                    }
                }
            }
            gui = new TextureGui(scaling);
        }
        return new TextureMeta(manager, resourceKey, file, animation, properties, gui);
    }

    private final TextureAnimation animation;
    private final TextureProperties properties;
    private final TextureGui gui;

    public TextureMeta(ITextureManager manager, String resourceKey, ResourcePackFile file, TextureAnimation animation, TextureProperties properties, TextureGui gui) {
        super(manager, resourceKey, file, false, null);
        this.animation = animation;
        this.properties = properties;
        this.gui = gui;
    }

    public TextureAnimation getAnimation() {
        return animation;
    }

    public TextureProperties getProperties() {
        return properties;
    }

    public TextureGui getGui() {
        return gui;
    }

    public boolean hasAnimation() {
        return animation != null;
    }

    public boolean hasProperties() {
        return properties != null;
    }

    public boolean hasGui() {
        return gui != null;
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
