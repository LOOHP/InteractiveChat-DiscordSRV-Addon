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

import com.loohp.interactivechatdiscordsrvaddon.Cache;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;

import java.awt.image.BufferedImage;
import java.lang.ref.Reference;

public class FontTextureResource {

    private static long CACHE_TIME = 0;

    public static long getCacheTime() {
        return CACHE_TIME;
    }

    public static void setCacheTime(long cacheTime) {
        CACHE_TIME = cacheTime;
    }

    private TextureResource resource;
    private char resourceWidth;
    private char resourceHeight;
    private char x;
    private char y;
    private char w;
    private char h;

    public FontTextureResource(TextureResource resource, char resourceWidth, char resourceHeight, char x, char y, char w, char h) {
        this.resource = resource;
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

    @SuppressWarnings("deprecation")
    public BufferedImage getFontImage() {
        BufferedImage image;
        if (resourceWidth < 1 || resourceHeight < 1) {
            image = resource.getTexture();
        } else {
            image = resource.getTexture(resourceWidth, resourceHeight);
        }
        if (CACHE_TIME > 0) {
            Reference<BufferedImage> internalReference = resource.getUnsafe().getTextureReference();
            BufferedImage internal;
            if ((internal = internalReference.get()) != null) {
                String hash = ImageUtils.hash(internal);
                if (Cache.getCache(hash) == null) {
                    Cache.putCache(hash, internal, CACHE_TIME);
                }
            }
        }
        if (w < 1 || h < 1) {
            return image;
        }
        return image.getSubimage(x, y, w, h);
    }

    public TextureResource getResource() {
        return resource;
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
