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

package com.loohp.interactivechatdiscordsrvaddon.resources.models;

import com.loohp.blockmodelrenderer.render.Vector;

public enum ModelGUILight {

    FRONT(new Vector(0, 0, 1), 0.2, 0.85),
    SIDE(new Vector(-0.5, 0.65, 0.9), 0.1, 1.0);

    public static ModelGUILight fromKey(String key) {
        for (ModelGUILight type : values()) {
            if (key.equalsIgnoreCase(type.toString())) {
                return type;
            }
        }
        return null;
    }

    private final Vector lightVector;
    private final double ambientLevel;
    private final double maxLevel;

    ModelGUILight(Vector lightVector, double ambientLevel, double maxLevel) {
        this.lightVector = lightVector;
        this.ambientLevel = ambientLevel;
        this.maxLevel = maxLevel;
    }

    public Vector getLightVector() {
        return lightVector.clone();
    }

    public double getAmbientLevel() {
        return ambientLevel;
    }

    public double getMaxLevel() {
        return maxLevel;
    }

}
