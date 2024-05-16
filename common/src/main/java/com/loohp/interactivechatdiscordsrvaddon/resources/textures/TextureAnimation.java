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

import java.util.Collections;
import java.util.List;

public class TextureAnimation {

    private final boolean interpolate;
    private final int width;
    private final int height;
    private final int masterFrametime;
    private final List<TextureAnimationFrames> frames;

    public TextureAnimation(boolean interpolate, int width, int height, int masterFrametime, List<TextureAnimationFrames> frames) {
        this.interpolate = interpolate;
        this.width = width;
        this.height = height;
        this.masterFrametime = masterFrametime;
        this.frames = Collections.unmodifiableList(frames);
    }

    public boolean isInterpolate() {
        return interpolate;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean hasWidth() {
        return width >= 0;
    }

    public boolean hasHeight() {
        return height >= 0;
    }

    public int getMasterFrametime() {
        return masterFrametime;
    }

    public boolean hasMasterFrametime() {
        return masterFrametime >= 0;
    }

    public List<TextureAnimationFrames> getFrames() {
        return frames;
    }

    public static class TextureAnimationFrames {

        private final int index;
        private final int times;

        public TextureAnimationFrames(int index, int times) {
            this.index = index;
            this.times = times;
        }

        public int getIndex() {
            return index;
        }

        public int getTimes() {
            return times;
        }

        public boolean hasTimes() {
            return times >= 0;
        }

    }

}
