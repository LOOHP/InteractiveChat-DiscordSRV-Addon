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

import com.loohp.interactivechat.libs.org.apache.commons.io.input.BOMInputStream;
import com.loohp.interactivechat.libs.org.json.simple.JSONArray;
import com.loohp.interactivechat.libs.org.json.simple.JSONObject;
import com.loohp.interactivechat.libs.org.json.simple.parser.JSONParser;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextureAtlases {

    public static final List<TextureAtlasSource> DEFAULT_BLOCK_ATLASES = Collections.unmodifiableList(Arrays.asList(
            new TextureAtlasDirectorySource("block", "block/"),
            new TextureAtlasDirectorySource("item", "item/")
    ));

    public static final TextureAtlases EMPTY_ATLAS = new TextureAtlases(Collections.emptyMap());

    public static TextureAtlases fromAtlasesFolder(ResourcePackFile folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            return EMPTY_ATLAS;
        }
        Map<TextureAtlasType, List<TextureAtlasSource>> sources = new HashMap<>();
        JSONParser parser = new JSONParser();
        for (ResourcePackFile file : folder.listFilesAndFolders()) {
            try {
                String name = file.getName();
                if (name.endsWith(".json")) {
                    String typeName = name.substring(0, name.lastIndexOf("."));
                    TextureAtlasType type = TextureAtlasType.fromName(typeName);
                    InputStreamReader reader = new InputStreamReader(new BOMInputStream(file.getInputStream()), StandardCharsets.UTF_8);
                    JSONObject rootJson = (JSONObject) parser.parse(reader);
                    reader.close();
                    JSONArray sourcesJson = (JSONArray) rootJson.get("sources");
                    List<TextureAtlasSource> textureAtlasSources = new ArrayList<>(sourcesJson.size());
                    for (Object obj : sourcesJson) {
                        JSONObject sourceJson = (JSONObject) obj;
                        String sourceTypeName = (String) sourceJson.get("type");
                        TextureAtlasSourceType<?> sourceType = TextureAtlasSourceType.fromName(sourceTypeName);
                        if (sourceType == null) {
                            continue;
                        }
                        TextureAtlasSource textureAtlasSource;
                        if (sourceType.equals(TextureAtlasSourceType.DIRECTORY)) {
                            String source = (String) sourceJson.get("source");
                            String prefix = (String) sourceJson.get("prefix");
                            textureAtlasSource = new TextureAtlasDirectorySource(source, prefix);
                        } else if (sourceType.equals(TextureAtlasSourceType.SINGLE)) {
                            String resource = (String) sourceJson.get("resource");
                            String sprite = (String) sourceJson.getOrDefault("sprite", resource);
                            textureAtlasSource = new TextureAtlasSingleSource(resource, sprite);
                        } else if (sourceType.equals(TextureAtlasSourceType.FILTER)) {
                            Pattern namespace = Pattern.compile((String) sourceJson.get("namespace"));
                            Pattern path = Pattern.compile((String) sourceJson.get("path"));
                            textureAtlasSource = new TextureAtlasFilterSource(namespace, path);
                        } else if (sourceType.equals(TextureAtlasSourceType.UNSTITCH)) {
                            String resource = (String) sourceJson.get("resource");
                            double divisorX = ((Number) sourceJson.get("divisor_x")).doubleValue();
                            double divisorY = ((Number) sourceJson.get("divisor_y")).doubleValue();
                            JSONArray regionsJson = (JSONArray) sourceJson.get("regions");
                            List<TextureAtlasUnstitchSource.Region> regions = new ArrayList<>(regionsJson.size());
                            for (Object obj1 : regionsJson) {
                                JSONObject regionJson = (JSONObject) obj1;
                                String sprite = (String) regionJson.get("resource");
                                double x = ((Number) regionJson.get("x")).doubleValue();
                                double y = ((Number) regionJson.get("y")).doubleValue();
                                double width = ((Number) regionJson.get("width")).doubleValue();
                                double height = ((Number) regionJson.get("height")).doubleValue();
                            }
                            textureAtlasSource = new TextureAtlasUnstitchSource(resource, divisorX, divisorY, regions);
                        } else if (sourceType.equals(TextureAtlasSourceType.PALETTED_PERMUTATIONS)) {
                            JSONArray texturesArray = (JSONArray) sourceJson.get("textures");
                            List<String> textures = new ArrayList<>(texturesArray.size());
                            for (Object o : texturesArray) {
                                textures.add((String) o);
                            }
                            String paletteKey = (String) sourceJson.get("palette_key");
                            JSONObject permutationsJson = (JSONObject) sourceJson.get("permutations");
                            Map<String, String> permutations = new LinkedHashMap<>();
                            for (Object o : permutationsJson.keySet()) {
                                String key = (String) o;
                                permutations.put(key, (String) permutationsJson.get(key));
                            }
                            textureAtlasSource = new TextureAtlasPalettedPermutationsSource(textures, paletteKey, permutations);
                        } else {
                            continue;
                        }
                        textureAtlasSources.add(textureAtlasSource);
                    }
                    sources.put(type, Collections.unmodifiableList(textureAtlasSources));
                }
            } catch (Exception e) {
                new ResourceLoadingException("Unable to load texture atlas " + file.getAbsolutePath(), e).printStackTrace();
            }
        }
        return new TextureAtlases(sources);
    }

    private final Map<TextureAtlasType, List<TextureAtlasSource>> textureAtlases;

    public TextureAtlases(Map<TextureAtlasType, List<TextureAtlasSource>> textureAtlases) {
        this.textureAtlases = Collections.unmodifiableMap(textureAtlases);
    }

    public Map<TextureAtlasType, List<TextureAtlasSource>> getTextureAtlases() {
        return textureAtlases;
    }

    public List<TextureAtlasSource> getTextureAtlases(TextureAtlasType type) {
        return textureAtlases.getOrDefault(type, Collections.emptyList());
    }

    public List<TextureAtlasSource> getAllTextureAtlases() {
        return textureAtlases.values().stream().flatMap(each -> each.stream()).collect(Collectors.toList());
    }

    public TextureAtlases merge(TextureAtlases other) {
        Map<TextureAtlasType, List<TextureAtlasSource>> textureAtlases = new HashMap<>(this.textureAtlases);
        textureAtlases.putAll(other.getTextureAtlases());
        return new TextureAtlases(textureAtlases);
    }

    public static final class TextureAtlasType {

        private static final Map<String, TextureAtlasType> TYPES = new ConcurrentHashMap<>();

        public static final TextureAtlasType ARMOR_TRIMS = registerBuiltIn("armor_trims");
        public static final TextureAtlasType BANNER_PATTERNS = registerBuiltIn("banner_patterns");
        public static final TextureAtlasType BEDS = registerBuiltIn("beds");
        public static final TextureAtlasType BLOCKS = registerBuiltIn("blocks");
        public static final TextureAtlasType CHESTS = registerBuiltIn("chests");
        public static final TextureAtlasType DECORATED_POT = registerBuiltIn("decorated_pot");
        public static final TextureAtlasType GUI = registerBuiltIn("gui");
        public static final TextureAtlasType MAP_DECORATIONS = registerBuiltIn("map_decorations");
        public static final TextureAtlasType MOB_EFFECTS = registerBuiltIn("mob_effects");
        public static final TextureAtlasType PAINTINGS = registerBuiltIn("paintings");
        public static final TextureAtlasType PARTICLES = registerBuiltIn("particles");
        public static final TextureAtlasType SHIELD_PATTERNS = registerBuiltIn("shield_patterns");
        public static final TextureAtlasType SHULKER_BOXES = registerBuiltIn("shulker_boxes");
        public static final TextureAtlasType SIGNS = registerBuiltIn("signs");

        private static TextureAtlasType registerBuiltIn(String name) {
            TextureAtlasType type = new TextureAtlasType(name);
            TYPES.put(type.name(), type);
            return type;
        }

        public static Map<String, TextureAtlasType> types() {
            return Collections.unmodifiableMap(TYPES);
        }

        private final String name;

        TextureAtlasType(String name) {
            this.name = name;
        }

        public String name() {
            return name;
        }

        @Override
        public String toString() {
            return name();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TextureAtlasType that = (TextureAtlasType) o;
            return name.equals(that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        public static TextureAtlasType fromName(String name) {
            String normalized = name.toLowerCase();
            return TYPES.computeIfAbsent(normalized, k -> new TextureAtlasType(normalized));
        }
    }

    public static final class TextureAtlasSourceType<T extends TextureAtlasSource> {

        public static final TextureAtlasSourceType<TextureAtlasDirectorySource> DIRECTORY = new TextureAtlasSourceType<>("directory", TextureAtlasDirectorySource.class);
        public static final TextureAtlasSourceType<TextureAtlasSingleSource> SINGLE = new TextureAtlasSourceType<>("single", TextureAtlasSingleSource.class);
        public static final TextureAtlasSourceType<TextureAtlasFilterSource> FILTER = new TextureAtlasSourceType<>("filter", TextureAtlasFilterSource.class);
        public static final TextureAtlasSourceType<TextureAtlasUnstitchSource> UNSTITCH = new TextureAtlasSourceType<>("unstitch", TextureAtlasUnstitchSource.class);
        public static final TextureAtlasSourceType<TextureAtlasPalettedPermutationsSource> PALETTED_PERMUTATIONS = new TextureAtlasSourceType<>("paletted_permutations", TextureAtlasPalettedPermutationsSource.class);

        private static final Map<String, TextureAtlasSourceType<?>> TYPES;

        static {
            Map<String, TextureAtlasSourceType<?>> types = new HashMap<>();
            types.put(DIRECTORY.name(), DIRECTORY);
            types.put(SINGLE.name(), SINGLE);
            types.put(FILTER.name(), FILTER);
            types.put(UNSTITCH.name(), UNSTITCH);
            types.put(PALETTED_PERMUTATIONS.name(), PALETTED_PERMUTATIONS);
            TYPES = Collections.unmodifiableMap(types);
        }

        public static Map<String, TextureAtlasSourceType<?>> types() {
            return TYPES;
        }

        private final String name;
        private final Class<T> typeClass;

        private TextureAtlasSourceType(String name, Class<T> typeClass) {
            this.name = name;
            this.typeClass = typeClass;
        }

        public String name() {
            return name;
        }

        @Override
        public String toString() {
            return name();
        }

        public static TextureAtlasSourceType<?> fromName(String name) {
            return TYPES.get(name.toLowerCase());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TextureAtlasSourceType<?> that = (TextureAtlasSourceType<?>) o;
            return name.equals(that.name) && typeClass.equals(that.typeClass);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, typeClass);
        }
    }

    public static abstract class TextureAtlasSource {

        public abstract TextureAtlasSourceType<?> getType();

        public abstract boolean isIncluded(String namespace, String relativePath);

    }

    public static class TextureAtlasDirectorySource extends TextureAtlasSource {

        private final String source;
        private final String prefix;

        private final String[] sourceTree;

        public TextureAtlasDirectorySource(String source, String prefix) {
            this.source = source;
            this.prefix = prefix;

            this.sourceTree = source.split("/");
        }

        public String getSource() {
            return source;
        }

        public String getPrefix() {
            return prefix;
        }

        @Override
        public TextureAtlasSourceType<?> getType() {
            return TextureAtlasSourceType.DIRECTORY;
        }

        @Override
        public boolean isIncluded(String namespace, String relativePath) {
            String[] path = relativePath.split("/");
            if (path.length < sourceTree.length) {
                return false;
            }
            for (int i = 0; i < sourceTree.length; i++) {
                String part = sourceTree[i];
                String match = path[i];
                if (!part.equals(match)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class TextureAtlasSingleSource extends TextureAtlasSource {

        private final String resource;
        private final String sprite;

        public TextureAtlasSingleSource(String resource, String sprite) {
            this.resource = resource;
            this.sprite = sprite;
        }

        public String getResource() {
            return resource;
        }

        public String getSprite() {
            return sprite;
        }

        @Override
        public TextureAtlasSourceType<?> getType() {
            return TextureAtlasSourceType.SINGLE;
        }

        @Override
        public boolean isIncluded(String namespace, String relativePath) {
            String file = resource;
            if (!resource.contains(".")) {
                file += ".png";
            }
            return relativePath.equals(file);
        }
    }

    public static class TextureAtlasFilterSource extends TextureAtlasSource {

        private final Pattern namespace;
        private final Pattern path;

        public TextureAtlasFilterSource(Pattern namespace, Pattern path) {
            this.namespace = namespace;
            this.path = path;
        }

        public Pattern getNamespace() {
            return namespace;
        }

        public Pattern getPath() {
            return path;
        }

        @Override
        public TextureAtlasSourceType<?> getType() {
            return TextureAtlasSourceType.FILTER;
        }

        @Override
        public boolean isIncluded(String namespace, String relativePath) {
            return !this.namespace.matcher(namespace).matches() && !path.matcher(relativePath).matches();
        }
    }

    public static class TextureAtlasUnstitchSource extends TextureAtlasSource {

        private final String resource;
        private final double divisorX;
        private final double divisorY;
        private final List<Region> regions;

        public TextureAtlasUnstitchSource(String resource, double divisorX, double divisorY, List<Region> regions) {
            this.resource = resource;
            this.divisorX = divisorX;
            this.divisorY = divisorY;
            this.regions = Collections.unmodifiableList(regions);
        }

        public String getResource() {
            return resource;
        }

        public double getDivisorX() {
            return divisorX;
        }

        public double getDivisorY() {
            return divisorY;
        }

        public List<Region> getRegions() {
            return regions;
        }

        @Override
        public TextureAtlasSourceType<?> getType() {
            return TextureAtlasSourceType.UNSTITCH;
        }

        @Override
        public boolean isIncluded(String namespace, String relativePath) {
            String file = resource;
            if (!resource.contains(".")) {
                file += ".png";
            }
            return relativePath.equals(file);
        }

        public class Region {

            private final String spriteName;
            private final double x;
            private final double y;
            private final double width;
            private final double height;
            private final UnaryOperator<BufferedImage> imageTransformFunction;

            public Region(String spriteName, double x, double y, double width, double height) {
                this.spriteName = spriteName;
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
                this.imageTransformFunction = image -> {
                    double d = (double) image.getWidth() / divisorX;
                    double e = (double) image.getHeight() / divisorY;
                    int i = (int) Math.floor(this.x * d);
                    int j = (int) Math.floor(this.y * e);
                    int k = (int) Math.floor(this.width * d);
                    int l = (int) Math.floor(this.height * e);
                    BufferedImage newImage = new BufferedImage(k, l, BufferedImage.TYPE_INT_ARGB);
                    ImageUtils.copyRect(image, newImage, i, j, 0, 0, k, l, false, false);
                    return newImage;
                };
            }

            public UnaryOperator<BufferedImage> getImageTransformFunction() {
                return imageTransformFunction;
            }

            public String getSpriteName() {
                return spriteName;
            }

            public double getX() {
                return x;
            }

            public double getY() {
                return y;
            }

            public double getWidth() {
                return width;
            }

            public double getHeight() {
                return height;
            }
        }
    }

    public static class TextureAtlasPalettedPermutationsSource extends TextureAtlasSource {

        private final List<String> textures;
        private final String paletteKey;
        private final Map<String, String> permutations;

        public TextureAtlasPalettedPermutationsSource(List<String> textures, String paletteKey, Map<String, String> permutations) {
            this.textures = Collections.unmodifiableList(textures);
            this.paletteKey = paletteKey;
            this.permutations = Collections.unmodifiableMap(permutations);
        }

        public List<String> getTextures() {
            return textures;
        }

        public String getPaletteKey() {
            return paletteKey;
        }

        public Map<String, String> getPermutations() {
            return permutations;
        }

        @Override
        public TextureAtlasSourceType<?> getType() {
            return TextureAtlasSourceType.PALETTED_PERMUTATIONS;
        }

        @Override
        public boolean isIncluded(String namespace, String relativePath) {
            for (String resource : textures) {
                String file = resource;
                if (!resource.contains(".")) {
                    file += ".png";
                }
                if (relativePath.equals(file)) {
                    return true;
                }
            }
            return false;
        }
    }

}
