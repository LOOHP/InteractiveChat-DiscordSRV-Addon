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

import java.util.Comparator;

public class PackFormatVersion implements Comparable<PackFormatVersion> {

    private static final Comparator<PackFormatVersion> COMPARATOR = Comparator.comparing(PackFormatVersion::getMajor).thenComparing(PackFormatVersion::getMinor);

    public static PackFormatVersion of(int major, int minor) {
        return new PackFormatVersion(major, minor);
    }

    public static PackFormatVersion of(int major) {
        return new PackFormatVersion(major, 0);
    }

    private final int major;
    private final int minor;

    public PackFormatVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    @Override
    public int compareTo(PackFormatVersion o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return "[" + major + ", " + minor + "]";
    }
}
