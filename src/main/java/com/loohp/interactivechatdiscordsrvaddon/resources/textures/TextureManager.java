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

import com.loohp.interactivechat.libs.org.apache.commons.io.input.BOMInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.AbstractManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceLoadingException;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourceManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;
import com.loohp.interactivechatdiscordsrvaddon.utils.TintUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TextureManager extends AbstractManager implements ITextureManager {

    public static final String SKIN_REQUIRED = "interactivechatdiscordsrvaddon/skin";
    private static final Color MISSING_TEXTURE_0 = new Color(0, 0, 0);
    private static final Color MISSING_TEXTURE_1 = new Color(248, 0, 248);
    public static final TextureResource MISSING_TEXTURE = new GeneratedTextureResource(getMissingImage(16, 16));

    public static BufferedImage getMissingImage(int width, int length) {
        BufferedImage image = new BufferedImage(width, length, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(MISSING_TEXTURE_0);
        g.fillRect(0, 0, width, length);
        g.setColor(MISSING_TEXTURE_1);
        g.fillRect(0, 0, width / 2, length / 2);
        g.fillRect(width / 2, length / 2, width / 2, length / 2);
        g.dispose();
        return image;
    }

    private Map<String, TextureResource> textures;

    public TextureManager(ResourceManager manager) {
        super(manager);
        this.textures = new HashMap<>();
    }

    @Override
    protected void loadDirectory(String namespace, ResourcePackFile root, Object... meta) {
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException(root.getAbsolutePath() + " is not a directory.");
        }
        JSONParser parser = new JSONParser();
        Map<String, TextureResource> textures = new HashMap<>();
        Collection<ResourcePackFile> files = root.listFilesRecursively();
        for (ResourcePackFile file : files) {
            try {
                String key = namespace + ":" + file.getRelativePathFrom(root);
                String extension = "";
                if (key.lastIndexOf(".") >= 0) {
                    extension = key.substring(key.lastIndexOf(".") + 1);
                    key = key.substring(0, key.lastIndexOf("."));
                }
                if (extension.equalsIgnoreCase("png")) {
                    textures.put(key, new TextureResource(this, key, file, true));
                } else if (extension.equalsIgnoreCase("mcmeta")) {
                    InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8);
                    JSONObject rootJson = (JSONObject) parser.parse(reader);
                    reader.close();
                    TextureMeta textureMeta = TextureMeta.fromJson(this, key + "." + extension, file, rootJson);
                    textures.put(key + "." + extension, textureMeta);
                } else {
                    textures.put(key + "." + extension, new TextureResource(this, key, file));
                }
            } catch (Exception e) {
                new ResourceLoadingException("Unable to load block model " + file.getAbsolutePath(), e).printStackTrace();
            }
        }
        this.textures.putAll(textures);
    }

    @Override
    protected void reload() {
        int[] grassColorArray;
        TextureResource grassColorMap = getTexture(ResourceRegistry.GRASS_COLORMAP_LOCATION, false);
        if (grassColorMap != null && grassColorMap.isTexture()) {
            grassColorArray = grassColorMap.getTexture(256, 256).getRGB(0, 0, 256, 256, null, 0, 256);
        } else {
            grassColorArray = new int[65536];
            Arrays.fill(grassColorArray, 0xFFFFFF);
        }

        int[] foliageColorArray;
        TextureResource foliageColorMap = getTexture(ResourceRegistry.FOLIAGE_COLORMAP_LOCATION, false);
        if (foliageColorMap != null && foliageColorMap.isTexture()) {
            foliageColorArray = foliageColorMap.getTexture(256, 256).getRGB(0, 0, 256, 256, null, 0, 256);
        } else {
            foliageColorArray = new int[65536];
            Arrays.fill(foliageColorArray, 0xFFFFFF);
        }

        TintUtils.setGrassAndFoliageColorMap(grassColorArray, foliageColorArray);
    }

    @Override
    public TextureResource getTexture(String resourceLocation, boolean returnMissingTexture) {
        if (!resourceLocation.contains(":")) {
            resourceLocation = ResourceRegistry.DEFAULT_NAMESPACE + ":" + resourceLocation;
        }
        if (resourceLocation.endsWith(".png")) {
            resourceLocation = resourceLocation.substring(0, resourceLocation.length() - 4);
        }
        if (returnMissingTexture) {
            return textures.getOrDefault(resourceLocation, MISSING_TEXTURE);
        } else {
            return textures.get(resourceLocation);
        }
    }

}
