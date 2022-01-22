package com.loohp.interactivechatdiscordsrvaddon.resources.textures;

public class TextureProperties {

    private boolean blur;
    private boolean clamp;
    private int[] mipmaps;

    public TextureProperties(boolean blur, boolean clamp, int[] mipmaps) {
        this.blur = blur;
        this.clamp = clamp;
        this.mipmaps = mipmaps;
    }

    public boolean isBlur() {
        return blur;
    }

    public boolean isClamp() {
        return clamp;
    }

    public int[] getMipmaps() {
        return mipmaps;
    }

}
