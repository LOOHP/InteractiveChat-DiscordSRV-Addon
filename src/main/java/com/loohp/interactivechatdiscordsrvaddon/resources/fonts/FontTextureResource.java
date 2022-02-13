package com.loohp.interactivechatdiscordsrvaddon.resources.fonts;

import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;

import java.awt.image.BufferedImage;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class FontTextureResource {

    private Reference<TextureResource> resource;
    private char resourceWidth;
    private char resourceHeight;
    private char x;
    private char y;
    private char w;
    private char h;

    public FontTextureResource(TextureResource resource, char resourceWidth, char resourceHeight, char x, char y, char w, char h) {
        this.resource = new WeakReference<>(resource);
        this.resourceWidth = resourceWidth;
        this.resourceHeight = resourceHeight;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public FontTextureResource(TextureResource resource, int resourceWidth, int resourceHeight, int x, int y, int w, int h) {
        this(resource, (char) resourceWidth, (char) resourceHeight, (char) x, (char) y, (char) w, (char) h);
    }

    public FontTextureResource(TextureResource resource, int x, int y, int w, int h) {
        this(resource, 0, 0, x, y, w, h);
    }

    public FontTextureResource(TextureResource resource) {
        this(resource, 0, 0, 0, 0, 0, 0);
    }

    public BufferedImage getFontImage() {
        if (!isValid()) {
            throw new IllegalStateException("Resource is no longer valid");
        }
        TextureResource textureResource = resource.get();
        BufferedImage image;
        if (resourceWidth < 1 || resourceHeight < 1) {
            image = textureResource.getTexture();
        } else {
            image = textureResource.getTexture(resourceWidth, resourceHeight);
        }
        if (w < 1 || h < 1) {
            return image;
        }
        return image.getSubimage(x, y, w, h);
    }

    public boolean isValid() {
        return resource.get() != null;
    }

    public TextureResource getResource() {
        return resource.get();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

}
