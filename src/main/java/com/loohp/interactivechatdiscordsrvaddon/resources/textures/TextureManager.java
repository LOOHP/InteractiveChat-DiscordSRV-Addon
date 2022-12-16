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
import com.loohp.interactivechatdiscordsrvaddon.resources.TextureAtlases;
import com.loohp.interactivechatdiscordsrvaddon.utils.TintUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextureManager extends AbstractManager implements ITextureManager {

    public static final String SKIN_REQUIRED = "interactivechatdiscordsrvaddon/skin";
    private static final Color MISSING_TEXTURE_0 = new Color(0, 0, 0);
    private static final Color MISSING_TEXTURE_1 = new Color(248, 0, 248);

    public static BufferedImage getMissingImage(int width, int length) {
        BufferedImage image = new BufferedImage(width, length, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setColor(MISSING_TEXTURE_0);
        g.fillRect(0, 0, width, length);
        g.setColor(MISSING_TEXTURE_1);
        g.fillRect(width / 2, 0, width / 2, length / 2);
        g.fillRect(0, length / 2, width / 2, length / 2);
        g.dispose();
        return image;
    }

    public static TextureResource getMissingTexture(ResourceManager resourceManager) {
        return new GeneratedTextureResource(resourceManager, getMissingImage(16, 16));
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
        TextureAtlases textureAtlases = null;
        if (meta.length > 0 && meta[0] instanceof Map) {
            textureAtlases = (TextureAtlases) meta[0];
        }
        JSONParser parser = new JSONParser();
        Map<String, TextureResource> textures = new HashMap<>();
        Collection<ResourcePackFile> files = root.listFilesRecursively();
        for (ResourcePackFile file : files) {
            try {
                String relativePath = file.getRelativePathFrom(root);
                String key = namespace + ":" + relativePath;
                String extension = "";
                if (key.lastIndexOf(".") >= 0) {
                    extension = key.substring(key.lastIndexOf(".") + 1);
                    key = key.substring(0, key.lastIndexOf("."));
                }
                TextureAtlases.TextureAtlasSource atlasSource = null;
                if (textureAtlases == null || (atlasSource = checkAtlasInclusion(textureAtlases, namespace, relativePath)) != null) {
                    Map<String, UnaryOperator<BufferedImage>> imageTransformFunctions = null;
                    if (atlasSource != null) {
                        TextureAtlases.TextureAtlasSourceType<?> sourceType = atlasSource.getType();
                        if (sourceType.equals(TextureAtlases.TextureAtlasSourceType.DIRECTORY)) {
                            String fileName = file.getName();
                            fileName = fileName.substring(0, fileName.lastIndexOf("."));
                            key = ((TextureAtlases.TextureAtlasDirectorySource) atlasSource).getPrefix() + fileName;
                        } else if (sourceType.equals(TextureAtlases.TextureAtlasSourceType.UNSTITCH)) {
                            imageTransformFunctions = ((TextureAtlases.TextureAtlasUnstitchSource) atlasSource).getRegions().stream().collect(Collectors.toMap(each -> each.getSpriteName(), each -> each.getImageTransformFunction(), (a, b) -> b));
                        }
                    }
                    if (extension.equalsIgnoreCase("png")) {
                        if (imageTransformFunctions == null) {
                            textures.put(key, new TextureResource(this, key, file, true, null));
                        } else {
                            for (Map.Entry<String, UnaryOperator<BufferedImage>> entry : imageTransformFunctions.entrySet()) {
                                String spriteName = entry.getKey();
                                textures.put(spriteName, new TextureResource(this, spriteName, file, true, entry.getValue()));
                            }
                        }
                    } else if (extension.equalsIgnoreCase("mcmeta")) {
                        InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8);
                        JSONObject rootJson = (JSONObject) parser.parse(reader);
                        reader.close();
                        TextureMeta textureMeta = TextureMeta.fromJson(this, key + "." + extension, file, rootJson);
                        textures.put(key + "." + extension, textureMeta);
                    } else {
                        textures.put(key + "." + extension, new TextureResource(this, key, file));
                    }
                }
            } catch (Exception e) {
                new ResourceLoadingException("Unable to load block model " + file.getAbsolutePath(), e).printStackTrace();
            }
        }
        this.textures.putAll(textures);
    }

    protected TextureAtlases.TextureAtlasSource checkAtlasInclusion(TextureAtlases textureAtlases, String namespace, String relativePath) {
        for (List<TextureAtlases.TextureAtlasSource> textureAtlasesLists : textureAtlases.getTextureAtlases().values()) {
            TextureAtlases.TextureAtlasSource result = null;
            for (TextureAtlases.TextureAtlasSource source : textureAtlasesLists) {
                if (!source.getType().equals(TextureAtlases.TextureAtlasSourceType.FILTER) && source.isIncluded(namespace, relativePath)) {
                    result = source;
                    break;
                }
            }
            if (result == null) {
                break;
            }
            for (TextureAtlases.TextureAtlasSource source : textureAtlasesLists) {
                if (source.getType().equals(TextureAtlases.TextureAtlasSourceType.FILTER) && !source.isIncluded(namespace, relativePath)) {
                    return null;
                }
            }
            return result;
        }
        TextureAtlases.TextureAtlasSource result = null;
        for (TextureAtlases.TextureAtlasSource source : TextureAtlases.DEFAULT_BLOCK_ATLASES) {
            if (!source.getType().equals(TextureAtlases.TextureAtlasSourceType.FILTER) && source.isIncluded(namespace, relativePath)) {
                result = source;
                break;
            }
        }
        if (result == null) {
            return null;
        }
        for (TextureAtlases.TextureAtlasSource source : TextureAtlases.DEFAULT_BLOCK_ATLASES) {
            if (source.getType().equals(TextureAtlases.TextureAtlasSourceType.FILTER) && !source.isIncluded(namespace, relativePath)) {
                return null;
            }
        }
        return null;
    }

    @Override
    protected void filterResources(Pattern namespace, Pattern path) {
        Iterator<String> itr = textures.keySet().iterator();
        while (itr.hasNext()) {
            String namespacedKey = itr.next();
            String assetNamespace = namespacedKey.substring(0, namespacedKey.indexOf(":"));
            String assetKey = namespacedKey.substring(namespacedKey.indexOf(":") + 1);
            if (!assetKey.contains(".")) {
                assetKey = assetKey + ".png";
            }
            if (namespace.matcher(assetNamespace).matches() && path.matcher(assetKey).matches()) {
                itr.remove();
            }
        }
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
            return textures.getOrDefault(resourceLocation, getMissingTexture(manager));
        } else {
            return textures.get(resourceLocation);
        }
    }

}
