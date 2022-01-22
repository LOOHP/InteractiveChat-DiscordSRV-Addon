package com.loohp.interactivechatdiscordsrvaddon.resources.models;

import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ModelFace {

    private ModelFaceSide side;
    private TextureUV uv;
    private String texture;
    private ModelFaceSide cullface;
    private int rotation;
    private int tintindex;

    public ModelFace(ModelFaceSide side, TextureUV uv, String texture, ModelFaceSide cullface, int rotation, int tintindex) {
        this.side = side;
        this.uv = uv;
        this.texture = texture;
        this.cullface = cullface;
        this.rotation = rotation;
        this.tintindex = tintindex;
    }

    public ModelFaceSide getSide() {
        return side;
    }

    public TextureUV getUV() {
        return uv;
    }

    public String getRawTexture() {
        return texture;
    }

    public String getTexture() {
        if (texture.startsWith("#")) {
            return texture;
        }
        return texture == null ? null : (texture.contains(":") ? texture : ResourceRegistry.DEFAULT_NAMESPACE + ":" + texture);
    }

    public ModelFace cloneWithNewTexture(String texture) {
        return new ModelFace(side, uv, texture, cullface, rotation, tintindex);
    }

    public ModelFaceSide getCullface() {
        return cullface;
    }

    public int getRotation() {
        return rotation;
    }

    public int getTintindex() {
        return tintindex;
    }

    public enum ModelFaceSide {

        UP,
        DOWN("BOTTOM"),
        NORTH,
        EAST,
        SOUTH,
        WEST;

        private Set<String> aliases;

        ModelFaceSide(String... aliases) {
            this.aliases = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(aliases)));
        }

        public static ModelFaceSide fromKey(String key) {
            for (ModelFaceSide face : values()) {
                if (key.toUpperCase().equals(face.toString()) || face.aliases.contains(key.toUpperCase())) {
                    return face;
                }
            }
            return null;
        }

        public Set<String> getAliases() {
            return aliases;
        }

    }

}
