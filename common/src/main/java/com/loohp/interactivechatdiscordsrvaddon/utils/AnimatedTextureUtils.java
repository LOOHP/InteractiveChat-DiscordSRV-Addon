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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import com.loohp.blockmodelrenderer.utils.ColorUtils;
import com.loohp.interactivechatdiscordsrvaddon.graphics.ImageUtils;
import com.loohp.interactivechatdiscordsrvaddon.resources.textures.TextureAnimation;

import java.awt.image.BufferedImage;
import java.util.List;

public class AnimatedTextureUtils {

    public static BufferedImage getEnchantedImageFrame(BufferedImage texture, int tick, double glintSpeed, double glintStrength) {
        int offset = (int) (tick * glintSpeed * 16.0) % texture.getWidth();
        if (offset != 0) {
            BufferedImage image = new BufferedImage(texture.getWidth(), texture.getHeight(), BufferedImage.TYPE_INT_ARGB);
            int x = offset;
            for (; x < texture.getWidth(); x++) {
                for (int y = 0; y < texture.getHeight(); y++) {
                    image.setRGB(x - offset, y, texture.getRGB(x, y));
                }
            }
            x -= offset;
            for (; x < texture.getWidth(); x++) {
                for (int y = 0; y < texture.getHeight(); y++) {
                    image.setRGB(x, y, texture.getRGB(x - (texture.getWidth() - offset), y));
                }
            }
            texture = image;
        }
        if (glintStrength < 1.0) {
            return ImageUtils.multiply(texture, glintStrength);
        }
        return texture;
    }

    public static BufferedImage getCurrentAnimationFrame(BufferedImage texture, TextureAnimation animation, int tick) {
        if (animation == null) {
            return ImageUtils.copyImage(texture);
        }
        int width;
        int height;
        if (animation.hasWidth() && animation.hasHeight()) {
            width = animation.getWidth();
            height = animation.getHeight();
        } else {
            int size = Math.min(texture.getWidth(), texture.getHeight());
            width = size;
            height = size;
        }
        int totalRuntime = findTotalRuntime(animation, texture, width, height);
        int currentTick = tick % totalRuntime;
        int frameIndex;
        int nextFrameIndex;
        float interpolateFraction;
        if (animation.hasFrames()) {
            FrameIndexResult result = findFrameIndexAt(currentTick, animation.getMasterFrametime(), animation.getFrames());
            frameIndex = result.getCurrentIndex();
            nextFrameIndex = result.getNextIndex();
            interpolateFraction = result.getInterpolateFraction();
        } else {
            frameIndex = currentTick / animation.getMasterFrametime();
            nextFrameIndex = (currentTick + animation.getMasterFrametime()) % totalRuntime / animation.getMasterFrametime();
            interpolateFraction = (currentTick % animation.getMasterFrametime()) / (float) animation.getMasterFrametime();
        }
        BufferedImage currentImage;
        if (texture.getHeight() > height) {
            currentImage = ImageUtils.copyAndGetSubImage(texture, 0, height * frameIndex, width, height);
        } else {
            currentImage = ImageUtils.copyAndGetSubImage(texture, width * frameIndex, 0, width, height);
        }
        if (!animation.isInterpolate()) {
            return currentImage;
        }
        BufferedImage nextImage;
        if (texture.getHeight() > height) {
            nextImage = texture.getSubimage(0, height * nextFrameIndex, width, height);
        } else {
            nextImage = texture.getSubimage(width * nextFrameIndex, 0, width, height);
        }
        return ImageUtils.transformRGB(currentImage, (x, y, start) -> interpolateColor(start, nextImage.getRGB(x, y), interpolateFraction));
    }

    private static int findTotalRuntime(TextureAnimation animation, BufferedImage texture, int width, int height) {
        if (animation.hasFrames()) {
            return animation.getFrames().stream().mapToInt(f -> f.hasTimes() ? f.getTimes() : animation.getMasterFrametime()).sum();
        } else {
            int totalFrames;
            if (texture.getHeight() > height) {
                totalFrames = texture.getHeight() / height;
            } else {
                totalFrames = texture.getWidth() / width;
            }
            return animation.getMasterFrametime() * totalFrames;
        }
    }

    private static FrameIndexResult findFrameIndexAt(int tick, int masterFrametime, List<TextureAnimation.TextureAnimationFrames> frames) {
        int t = 0;
        for (int i = 0; i < frames.size(); i++) {
            TextureAnimation.TextureAnimationFrames frame = frames.get(i);
            int time = frame.hasTimes() ? frame.getTimes() : masterFrametime;
            t += time;
            if (t > tick) {
                return new FrameIndexResult(frame.getIndex(), frames.get(i + 1 >= frames.size() ? 0 : (i + 1)).getIndex(), 1F - ((t - tick) / (float) time));
            }
        }
        throw new IllegalStateException();
    }

    private static class FrameIndexResult {

        private final int currentIndex;
        private final int nextIndex;
        private final float interpolateFraction;

        private FrameIndexResult(int currentIndex, int nextIndex, float interpolateFraction) {
            this.currentIndex = currentIndex;
            this.nextIndex = nextIndex;
            this.interpolateFraction = interpolateFraction;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public int getNextIndex() {
            return nextIndex;
        }

        public float getInterpolateFraction() {
            return interpolateFraction;
        }
    }

    private static int interpolateColor(int startColor, int endColor, float fraction) {
        float startA = ColorUtils.getAlpha(startColor) / 255.0F;
        float startR = ColorUtils.getRed(startColor) / 255.0F;
        float startG = ColorUtils.getGreen(startColor) / 255.0F;
        float startB = ColorUtils.getBlue(startColor) / 255.0F;

        float endA = ColorUtils.getAlpha(endColor) / 255.0F;
        float endR = ColorUtils.getRed(endColor) / 255.0F;
        float endG = ColorUtils.getGreen(endColor) / 255.0F;
        float endB = ColorUtils.getBlue(endColor) / 255.0F;

        float a = Math.max(0F, Math.min(1F, startA + (endA - startA) * fraction));
        float r = Math.max(0F, Math.min(1F, startR + (endR - startR) * fraction));
        float g = Math.max(0F, Math.min(1F, startG + (endG - startG) * fraction));
        float b = Math.max(0F, Math.min(1F, startB + (endB - startB) * fraction));

        int colorA = Math.round(a * 255.0F);
        int colorR = Math.round(r * 255.0F);
        int colorG = Math.round(g * 255.0F);
        int colorB = Math.round(b * 255.0F);

        return ColorUtils.getIntFromColor(colorR, colorG, colorB, colorA);
    }

}
