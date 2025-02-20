/*
 * This file is part of InteractiveChatDiscordSrvAddon.
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

import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackFile;
import com.loohp.interactivechatdiscordsrvaddon.utils.AnimatedTextureUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.function.UnaryOperator;

public class TextureResource {

    public static final String MCMETA_SUFFIX = ".mcmeta";
    public static final String PNG_MCMETA_SUFFIX = ".png" + MCMETA_SUFFIX;

    private final ITextureManager manager;
    private final String resourceKey;
    private final ResourcePackFile file;
    private final boolean isTexture;
    private Reference<BufferedImage> texture;
    private final UnaryOperator<BufferedImage> imageTransformFunction;

    private Unsafe unsafe;

    public TextureResource(ITextureManager manager, String resourceKey, ResourcePackFile file, boolean isTexture, UnaryOperator<BufferedImage> imageTransformFunction) {
        this.manager = manager;
        this.resourceKey = resourceKey;
        this.file = file;
        this.isTexture = isTexture;
        this.texture = null;
        this.imageTransformFunction = imageTransformFunction;
        this.unsafe = null;
    }

    protected TextureResource(ITextureManager manager, String resourceKey, ResourcePackFile file, BufferedImage image, UnaryOperator<BufferedImage> imageTransformFunction) {
        this.manager = manager;
        this.resourceKey = resourceKey;
        this.file = file;
        this.isTexture = true;
        this.texture = new WeakReference<>(image);
        this.imageTransformFunction = imageTransformFunction;
        this.unsafe = null;
    }

    public TextureResource(TextureManager manager, String resourceKey, ResourcePackFile file) {
        this(manager, resourceKey, file, false, null);
    }

    public ITextureManager getManager() {
        return manager;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    private synchronized BufferedImage loadImage() {
        if (!isTexture) {
            throw new IllegalStateException(resourceKey + " is not a texture!");
        }
        BufferedImage image;
        if (texture != null && (image = texture.get()) != null) {
            return image;
        }
        try (InputStream inputStream = file.getInputStream()) {
            image = ImageIO.read(inputStream);
            if (image == null) {
                throw new IOException("Image is null!");
            }
            this.texture = new WeakReference<>(image);
            return image;
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to load image " + resourceKey + " from " + file.getAbsolutePath(), e);
        }
    }

    public boolean isTexture() {
        return isTexture;
    }

    public BufferedImage getTexture(int width, int height) {
        return getTexture(width, height, false);
    }

    public BufferedImage getTexture(int width, int height, boolean clearAnimation) {
        BufferedImage image = loadImage();
        if (imageTransformFunction != null) {
            image = imageTransformFunction.apply(image);
        }
        TextureMeta meta = getTextureMeta();
        if (clearAnimation && meta != null) {
            if (meta.hasAnimation()) {
                image = AnimatedTextureUtils.getCurrentAnimationFrame(image, meta.getAnimation(), 0);
            }
        }
        if (image.getWidth() != width || image.getHeight() != height) {
            image = ImageUtils.resizeImageAbs(image, width, height);
        } else {
            image = ImageUtils.copyImage(image);
        }
        return image;
    }

    public BufferedImage getScaledTexture(int srcWidth, int srcHeight, int dstWidth, int dstHeight) {
        TextureGui.Scaling<?> scaling = TextureGui.Scaling.DEFAULT_SCALING;
        TextureMeta meta = getTextureMeta();
        if (meta != null) {
            TextureGui textureGui = meta.getGui();
            if (textureGui != null) {
                scaling = textureGui.getScaling();
            }
        }
        BufferedImage source = getTexture(srcWidth, srcHeight, true);
        return scaling.getScalingProperty().apply(source, dstWidth, dstHeight);
    }

    public BufferedImage getTexture() {
        return getTexture(false);
    }

    public BufferedImage getTexture(boolean clearAnimation) {
        BufferedImage image = loadImage();
        if (imageTransformFunction != null) {
            image = imageTransformFunction.apply(image);
        }
        if (clearAnimation && hasTextureMeta()) {
            TextureMeta meta = getTextureMeta();
            if (meta.hasAnimation()) {
                image = AnimatedTextureUtils.getCurrentAnimationFrame(image, meta.getAnimation(), 0);
            }
        }
        return ImageUtils.copyImage(image);
    }

    public boolean hasFile() {
        return file != null;
    }

    public ResourcePackFile getFile() {
        return file;
    }

    public boolean isTextureMeta() {
        return false;
    }

    public boolean hasTextureMeta() {
        return getTextureMeta() != null;
    }

    public boolean hasImageTransformFunction() {
        return imageTransformFunction != null;
    }

    public UnaryOperator<BufferedImage> getImageTransformFunction() {
        return imageTransformFunction;
    }

    public TextureMeta getTextureMeta() {
        if (resourceKey == null || manager == null) {
            return null;
        }
        TextureResource meta = manager.getTexture(resourceKey.contains(".") ? resourceKey + MCMETA_SUFFIX : resourceKey + PNG_MCMETA_SUFFIX, false);
        if (meta != null && meta.isTextureMeta()) {
            return (TextureMeta) meta;
        }
        return null;
    }

    @SuppressWarnings({"DeprecatedIsStillUsed", "Convert2Lambda", "deprecation"})
    @Deprecated
    public Unsafe getUnsafe() {
        if (unsafe != null) {
            return unsafe;
        }
        return unsafe = new Unsafe() {
            @Override
            public Reference<BufferedImage> getTextureReference() {
                return texture;
            }
        };
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Deprecated
    public interface Unsafe {

        @Deprecated
        Reference<BufferedImage> getTextureReference();

    }

}
