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

import java.util.Collections;
import java.util.List;

public class CustomModelData {

    private final List<Float> floats;
    private final List<Boolean> flags;
    private final List<String> strings;
    private final List<Integer> colors;

    public CustomModelData(List<Float> floats, List<Boolean> flags, List<String> strings, List<Integer> colors) {
        this.floats = floats;
        this.flags = flags;
        this.strings = strings;
        this.colors = colors;
    }

    public CustomModelData(float legacyIndex) {
        this(Collections.singletonList(legacyIndex), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    private static <T> T getValue(List<T> var0, int var1) {
        return var1 >= 0 && var1 < var0.size() ? var0.get(var1) : null;
    }

    public Float getFloat(int index) {
        return getValue(floats, index);
    }

    public Boolean getFlag(int index) {
        return getValue(flags, index);
    }

    public String getString(int index) {
        return getValue(strings, index);
    }

    public Integer getColor(int index) {
        return getValue(colors, index);
    }

    public List<Float> getFloats() {
        return floats;
    }

    public List<Boolean> getFlags() {
        return flags;
    }

    public List<String> getStrings() {
        return strings;
    }

    public List<Integer> getColors() {
        return colors;
    }

    public boolean hasLegacyIndex() {
        return !floats.isEmpty() && floats.get(0) != null;
    }

    public float getLegacyIndex() {
        return floats.get(0);
    }
}
