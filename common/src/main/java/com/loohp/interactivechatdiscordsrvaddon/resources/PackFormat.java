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

package com.loohp.interactivechatdiscordsrvaddon.resources;

public class PackFormat {

    public static PackFormat version(int version) {
        return version(version, version, version);
    }

    public static PackFormat version(int major, int min, int max) {
        if (major < min || major > max) {
            throw new IllegalArgumentException("Major version must be within range");
        }
        return new PackFormat(PackFormatVersion.of(min), PackFormatVersion.of(max));
    }

    public static PackFormat version(int min, int max) {
        return version(max, min, max);
    }

    public static PackFormat version(PackFormatVersion min, PackFormatVersion max) {
        return new PackFormat(min, max);
    }

    public static PackFormat version(PackFormatVersion only) {
        return version(only, only);
    }

    private final PackFormatVersion min;
    private final PackFormatVersion max;

    private PackFormat(PackFormatVersion min, PackFormatVersion max) {
        this.min = min;
        this.max = max;
    }

    public PackFormatVersion getMax() {
        return max;
    }

    public PackFormatVersion getMin() {
        return min;
    }

    public boolean isCompatible(PackFormatVersion version) {
        return min.compareTo(version) <= 0 && max.compareTo(version) >= 0;
    }

    @Override
    public String toString() {
        return "{min=" + min + ", max=" + max + "}";
    }
}