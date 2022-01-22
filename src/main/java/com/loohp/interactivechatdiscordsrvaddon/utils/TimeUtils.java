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
