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

package com.loohp.interactivechatdiscordsrvaddon.objectholders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum MoonPhase {

    FULL_MOON(0, "full_moon"),
    WANING_GIBBOUS(1, "waning_gibbous"),
    THIRD_QUARTER(2, "third_quarter"),
    WANING_CRESCENT(3, "waning_crescent"),
    NEW_MOON(4, "new_moon"),
    WAXING_CRESCENT(5, "waxing_crescent"),
    FIRST_QUARTER(6, "first_quarter"),
    WAXING_GIBBOUS(7, "waxing_gibbous");

    public static final List<MoonPhase> MOON_PHASES = Collections.unmodifiableList(Arrays.asList(values()));
    public static final int TOTAL_TIME_IN_DAY = 24000;

    public static MoonPhase fromIndex(int index) {
        return MOON_PHASES.get(index % MOON_PHASES.size());
    }

    private final int index;
    private final String name;

    MoonPhase(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public int startTick() {
        return index * TOTAL_TIME_IN_DAY;
    }
}
