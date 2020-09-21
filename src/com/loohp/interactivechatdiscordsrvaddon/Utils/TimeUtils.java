package com.loohp.interactivechatdiscordsrvaddon.Utils;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
	
	private static DecimalFormat formatter = new DecimalFormat("00");
	
	public static String getReadableTimeBetween(long beginning, long ending) {
		LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochMilli(beginning), ZoneId.systemDefault());
		LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(ending), ZoneId.systemDefault());
		long hrs = ChronoUnit.HOURS.between(start, end);
		long mins = ChronoUnit.MINUTES.between(start, end);
		long secs = ChronoUnit.SECONDS.between(start, end);
		
		return (hrs == 0 ? "" : (hrs + ":")) + formatter.format(mins % 60) + ":" + formatter.format(secs % 60);
	}
	
	public static String getReadableTime(long time) {		
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(time),
	            TimeUnit.MILLISECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time)),
	            TimeUnit.MILLISECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));
	}

}
