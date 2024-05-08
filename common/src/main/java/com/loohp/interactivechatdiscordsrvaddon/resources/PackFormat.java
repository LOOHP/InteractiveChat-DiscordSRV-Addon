/*
 * This file is part of InteractiveChatDiscordSrvAddon.
 *
 * Copyright (C) 2023. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2023. Contributors
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
        return new PackFormat(version, version, version);
    }

    public static PackFormat version(int major, int min, int max) {
        if (major < min || major > max) {
            throw new IllegalArgumentException("Major version must be within range");
        }
        return new PackFormat(major, min, max);
    }

    public static PackFormat version(int min, int max) {
        return new PackFormat(max, min, max);
    }

    private final int major;
    private final int min;
    private final int max;

    private PackFormat(int major, int min, int max) {
        this.major = major;
        this.min = min;
        this.max = max;
    }

    public int getMajor() {
        return major;
    }

    public boolean isCompatible(int version) {
        return min <= version && version <= max;
    }
}