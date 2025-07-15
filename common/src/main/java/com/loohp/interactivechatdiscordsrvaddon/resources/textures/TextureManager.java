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

import com.google.common.collect.Collections2;
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
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextureManager extends AbstractManager implements ITextureManager {

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

    public static Int2IntMap createTexturePaletteReplacementMap(BufferedImage key, BufferedImage permutation) {
        if (key.getWidth() != permutation.getWidth() || key.getHeight() != permutation.getHeight()) {
            throw new ResourceLoadingException("Palette key size does not match permutation size");
        }
        Int2IntMap map = new Int2IntArrayMap();
        int[] keyColors = key.getRGB(0, 0, key.getWidth(), key.getHeight(), null, 0, key.getWidth());
        int[] permutationColors = permutation.getRGB(0, 0, permutation.getWidth(), permutation.getHeight(), null, 0, permutation.getWidth());
        for (int i = 0; i < keyColors.length; i++) {
            map.put(keyColors[i] & 16777215, permutationColors[i]);
        }
        return map;
    }

    public static BufferedImage applyPaletteReplacementMap(Int2IntMap palette, BufferedImage image) {
        int[] colors = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        for (int i = 0; i < colors.length; i++) {
            int color = colors[i];
            int alphaless = color & 16777215;
            if (palette.containsKey(alphaless)) {
                int replacement = palette.get(alphaless);
                int alpha = (int) Math.round(Math.min(1.0, Math.max(0.0, ((double) (color >> 24 & 255) / 255.0) * ((double) (replacement >> 24 & 255) / 255.0))) * 255.0);
                colors[i] = (replacement & 16777215) | (alpha << 24);
            }
        }
        image.setRGB(0, 0, image.getWidth(), image.getHeight(), colors, 0, image.getWidth());
        return image;
    }

    private Map<String, TextureResource> textures;
    private Map<String, TextureAtlases> textureAtlases;

    public TextureManager(ResourceManager manager) {
        super(manager);
        this.textures = new HashMap<>();
        this.textureAtlases = new HashMap<>();
    }

    @Override
    protected void loadDirectory(String namespace, ResourcePackFile root, Object... meta) {
        if (!root.exists() || !root.isDirectory()) {
            throw new IllegalArgumentException(root.getAbsolutePath() + " is not a directory.");
        }
        Map<String, TextureAtlases> textureAtlases = null;
        if (meta.length > 0 && meta[0] instanceof Map) {
            textureAtlases = (Map<String, TextureAtlases>) meta[0];
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
                List<TextureAtlases.TextureAtlasSource> atlasSources = textureAtlases == null ? Collections.emptyList() : checkAtlasInclusion(textureAtlases, namespace, relativePath);
                Map<String, UnaryOperator<BufferedImage>> imageTransformFunctions = null;
                if (!atlasSources.isEmpty()) {
                    for (TextureAtlases.TextureAtlasSource atlasSource : atlasSources) {
                        TextureAtlases.TextureAtlasSourceType<?> sourceType = atlasSource.getType();
                        if (sourceType.equals(TextureAtlases.TextureAtlasSourceType.DIRECTORY)) {
                            TextureAtlases.TextureAtlasDirectorySource directorySource = (TextureAtlases.TextureAtlasDirectorySource) atlasSource;
                            String fileName = file.getRelativePathFrom(root.getChild(directorySource.getSource()));
                            fileName = fileName.substring(0, fileName.lastIndexOf("."));
                            key = namespace + ":" + directorySource.getPrefix() + fileName;
                            break;
                        } else if (sourceType.equals(TextureAtlases.TextureAtlasSourceType.UNSTITCH)) {
                            imageTransformFunctions = ((TextureAtlases.TextureAtlasUnstitchSource) atlasSource).getRegions().stream().collect(Collectors.toMap(each -> each.getSpriteName(), each -> each.getImageTransformFunction(), (a, b) -> b));
                            break;
                        }
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
                    try (InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8)) {
                        JSONObject rootJson = (JSONObject) parser.parse(reader);
                        TextureMeta textureMeta = TextureMeta.fromJson(this, key + "." + extension, file, rootJson);
                        textures.put(key + "." + extension, textureMeta);
                    }
                } else {
                    textures.put(key + "." + extension, new TextureResource(this, key, file));
                }
            } catch (Exception e) {
                new ResourceLoadingException("Unable to load block model " + file.getAbsolutePath(), e).printStackTrace();
            }
        }
        this.textures.putAll(textures);
        if (textureAtlases != null) {
            this.textureAtlases.putAll(textureAtlases);
        }
    }

    private Iterable<Collection<TextureAtlases.TextureAtlasSource>> getFullTextureAtlasSource(Map<String, TextureAtlases> textureAtlases, String namespace) {
        Stream.Builder<Collection<TextureAtlases.TextureAtlasSource>> builder = Stream.builder();
        for (Map.Entry<String, TextureAtlases> entry : textureAtlases.entrySet()) {
            for (Map.Entry<TextureAtlases.TextureAtlasType, List<TextureAtlases.TextureAtlasSource>> textureEntry : entry.getValue().getTextureAtlases().entrySet()) {
                if (entry.getKey().equals(namespace)) {
                    builder.add(textureEntry.getValue());
                } else {
                    builder.add(Collections2.filter(textureEntry.getValue(), a -> a.getType().appliesAcrossNamespaces()));
                }
            }
        }
        builder.add(TextureAtlases.DEFAULT_BLOCK_ATLASES);
        builder.add(TextureAtlases.DEFAULT_BLOCK_ATLASES);
        Iterator<Collection<TextureAtlases.TextureAtlasSource>> itr = builder.build().iterator();
        return () -> itr;
    }

    protected List<TextureAtlases.TextureAtlasSource> checkAtlasInclusion(Map<String, TextureAtlases> textureAtlases, String namespace, String relativePath) {
        List<TextureAtlases.TextureAtlasSource> results = new ArrayList<>();
        for (Collection<TextureAtlases.TextureAtlasSource> textureAtlasesLists : getFullTextureAtlasSource(textureAtlases, namespace)) {
            for (TextureAtlases.TextureAtlasSource source : textureAtlasesLists) {
                if (!source.getType().equals(TextureAtlases.TextureAtlasSourceType.FILTER) && source.isIncluded(namespace, relativePath)) {
                    boolean filtered = false;
                    for (TextureAtlases.TextureAtlasSource source0 : textureAtlasesLists) {
                        if (source0.getType().equals(TextureAtlases.TextureAtlasSourceType.FILTER) && !source0.isIncluded(namespace, relativePath)) {
                            filtered = true;
                            break;
                        }
                    }
                    if (!filtered) {
                        results.add(source);
                    }
                }
            }
        }
        return results;
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
        for (Map.Entry<String, TextureAtlases> textureAtlasesEntry : this.textureAtlases.entrySet()) {
            Map<String, TextureResource> textures = new HashMap<>();
            String namespace = textureAtlasesEntry.getKey();
            TextureAtlases textureAtlases = textureAtlasesEntry.getValue();
            if (textureAtlases != null) {
                for (TextureAtlases.TextureAtlasSource textureAtlasSource : textureAtlases.getAllTextureAtlases()) {
                    if (textureAtlasSource.getType().equals(TextureAtlases.TextureAtlasSourceType.PALETTED_PERMUTATIONS)) {
                        TextureAtlases.TextureAtlasPalettedPermutationsSource source = (TextureAtlases.TextureAtlasPalettedPermutationsSource) textureAtlasSource;
                        String paletteKey = source.getPaletteKey();
                        try {
                            BufferedImage paletteImage = getTexture(paletteKey).getTexture();
                            Map<String, Int2IntMap> permutations = new HashMap<>(source.getPermutations().size());
                            for (Map.Entry<String, String> entry : source.getPermutations().entrySet()) {
                                try {
                                    permutations.put(entry.getKey(), createTexturePaletteReplacementMap(paletteImage, getTexture(entry.getValue()).getTexture()));
                                } catch (Exception e) {
                                    new ResourceLoadingException("Unable to load paletted permutation of " + entry.getKey() + " in " + namespace + ":" + paletteKey, e).printStackTrace();
                                }
                            }
                            for (String texture : source.getTextures()) {
                                if (!texture.contains(":")) {
                                    texture = ResourceRegistry.DEFAULT_NAMESPACE + ":" + texture;
                                }
                                BufferedImage textureImage = getTexture(texture).getTexture();
                                for (Map.Entry<String, Int2IntMap> entry : permutations.entrySet()) {
                                    BufferedImage newImage = new BufferedImage(textureImage.getWidth(), textureImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
                                    Graphics2D g = newImage.createGraphics();
                                    g.drawImage(textureImage, 0, 0, null);
                                    g.dispose();
                                    applyPaletteReplacementMap(entry.getValue(), newImage);
                                    String permutationName = entry.getKey();
                                    if (permutationName.contains(":")) {
                                        permutationName = permutationName.substring(permutationName.lastIndexOf(":") + 1);
                                    }
                                    String key = texture + source.getSeparator() + permutationName;
                                    textures.put(key, new GeneratedTextureResource(manager, key, newImage));
                                }
                            }
                        } catch (Exception e) {
                            new ResourceLoadingException("Unable to load paletted permutation in " + namespace + ":" + paletteKey, e).printStackTrace();
                        }
                    }
                }
            }
            this.textures.putAll(textures);
        }

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
    public TextureResource getMissingTexture() {
        return getMissingTexture(manager);
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
            return textures.getOrDefault(resourceLocation, getMissingTexture());
        } else {
            return textures.get(resourceLocation);
        }
    }

}
