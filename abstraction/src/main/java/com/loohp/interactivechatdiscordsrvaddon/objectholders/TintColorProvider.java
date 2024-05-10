/*
 * This file is part of InteractiveChatDiscordSrvAddon-Abstraction.
 *
 * Copyright (C) 2024. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2024. Contributors
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

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import com.loohp.interactivechatdiscordsrvaddon.grahpics.BaseImageUtils;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;

public interface TintColorProvider {

    TintColorProvider EMPTY_INSTANCE = new TintColorProvider() {
        @Override
        public BufferedImage applyTint(BufferedImage image, int tintIndex) {
            return image;
        }

        @Override
        public int getTintColor(int tintIndex) {
            return -1;
        }

        @Override
        public boolean hasTintColor(int tintIndex) {
            return false;
        }
    };

    default BufferedImage applyTint(BufferedImage image, int tintIndex) {
        if (hasTintColor(tintIndex)) {
            int color = getTintColor(tintIndex);
            BufferedImage tintImage = BaseImageUtils.changeColorTo(BaseImageUtils.copyImage(image), color);
            return BaseImageUtils.multiply(image, tintImage);
        }
        return image;
    }

    int getTintColor(int tintIndex);

    boolean hasTintColor(int tintIndex);

    class TintIndexData implements TintColorProvider {

        private final List<IntSupplier> data;

        public TintIndexData(List<IntSupplier> data) {
            this.data = Collections.unmodifiableList(data);
        }

        public TintIndexData(IntSupplier... data) {
            this(Arrays.asList(data));
        }

        @Override
        public int getTintColor(int tintIndex) {
            if (tintIndex >= 0 && tintIndex < data.size()) {
                IntSupplier colorSupplier = data.get(tintIndex);
                if (colorSupplier != null) {
                    return colorSupplier.getAsInt();
                }
            }
            return -1;
        }

        @Override
        public boolean hasTintColor(int tintIndex) {
            return tintIndex >= 0 && tintIndex < data.size() && data.get(tintIndex) != null;
        }
    }

    class DyeTintProvider implements TintColorProvider {

        private final IntUnaryOperator colorSupplier;

        public DyeTintProvider(IntUnaryOperator colorSupplier) {
            this.colorSupplier = colorSupplier;
        }

        @Override
        public int getTintColor(int tintIndex) {
            if (tintIndex >= 0) {
                return colorSupplier.applyAsInt(tintIndex);
            }
            return -1;
        }

        @Override
        public boolean hasTintColor(int tintIndex) {
            return tintIndex >= 0;
        }
    }

    class SpawnEggTintData implements TintColorProvider {

        private final int base;
        private final int overlay;

        public SpawnEggTintData(int base, int overlay) {
            this.base = base;
            this.overlay = overlay;
        }

        public int getBase() {
            return base;
        }

        public int getOverlay() {
            return overlay;
        }

        @Override
        public int getTintColor(int tintIndex) {
            return tintIndex == 0 ? this.base : this.overlay;
        }

        @Override
        public boolean hasTintColor(int tintIndex) {
            return true;
        }
    }

}