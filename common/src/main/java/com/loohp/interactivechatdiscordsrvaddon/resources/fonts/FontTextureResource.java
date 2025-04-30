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

package com.loohp.interactivechatdiscordsrvaddon.resources.fonts;

import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.ICacheManager;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureResource;

import java.awt.image.BufferedImage;
import java.lang.ref.Reference;

public class FontTextureResource extends FontResource {

    private static long CACHE_TIME = 0;

    public static long getCacheTime() {
        return CACHE_TIME;
    }

    public static void setCacheTime(long cacheTime) {
        CACHE_TIME = cacheTime;
    }

    private final TextureResource resource;
    private final char resourceWidth;
    private final char resourceHeight;
    private final char x;
    private final char y;

    public FontTextureResource(TextureResource resource, char resourceWidth, char resourceHeight, char x, char y, char width, char height) {
        super(width, height);
        this.resource = resource;
        this.resourceWidth = resourceWidth;
        this.resourceHeight = resourceHeight;
        this.x = x;
        this.y = y;
    }

    public FontTextureResource(TextureResource resource, int resourceWidth, int resourceHeight, int x, int y, int width, int height) {
        this(resource, (char) resourceWidth, (char) resourceHeight, (char) x, (char) y, (char) width, (char) height);
    }

    public FontTextureResource(TextureResource resource, int x, int y, int width, int height) {
        this(resource, 0, 0, x, y, width, height);
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
                if (resource.getManager().getManager().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).getCache(hash) == null) {
                    resource.getManager().getManager().getResourceRegistry(ICacheManager.IDENTIFIER, ICacheManager.class).putCache(hash, internal);
                }
            }
        }
        if (width < 1 || height < 1) {
            return image;
        }
        return image.getSubimage(x, y, width, height);
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

}
