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

package com.loohp.interactivechatdiscordsrvaddon.utils;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

public class TimeUtils {

    private static DecimalFormat formatter = new DecimalFormat("00");

    public static String getReadableTimeBetween(long beginning, long ending, String delimiter, ChronoUnit largestUnit, ChronoUnit smallestUnit, boolean showEvenIfLargestIs0) {
        LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(beginning), ZoneId.systemDefault());
        LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(ending), ZoneId.systemDefault());

        List<String> timeStrings = new LinkedList<>();

        for (ChronoUnit unit : EnumUtils.valuesBetween(ChronoUnit.class, smallestUnit, largestUnit)) {
            long time = unit.between(start, end);
            if (unit.equals(largestUnit)) {
                if (showEvenIfLargestIs0 || time != 0) {
                    timeStrings.add(0, String.valueOf(time));
                }
            } else {
                timeStrings.add(0, formatter.format(time % 60));
            }
        }

        return String.join(delimiter, timeStrings);
    }

}
