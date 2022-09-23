package me.matzhilven.modernkitpvp.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    public static String getDayOfWeek() {
        LocalDate currentDate = LocalDate.now();
        DayOfWeek day = currentDate.getDayOfWeek();
        return day.name().toLowerCase();
    }

    public static long calculate(String time) {
        if (time.contains("s")) {
            return Integer.parseInt(time.replace("s", ""));
        } else if (time.contains("m")) {
            int minutesToAdd = Integer.parseInt(time.replace("m", ""));
            return minutesToAdd * 60L;
        } else if (time.contains("h")) {
            int hoursToAdd = Integer.parseInt(time.replace("h", ""));
            return hoursToAdd * 3600L;
        } else if (time.contains("d")) {
            int daysToAdd = Integer.parseInt(time.replace("d", ""));
            return daysToAdd * 3600L * 24L;
        } else if (time.contains("w")) {
            int weeksToAdd = Integer.parseInt(time.replace("w", ""));
            return weeksToAdd * 3600L * 24L * 7L;
        } else if (time.contains("y")) {
            int yearsToAdd = Integer.parseInt(time.replace("y", ""));
            return yearsToAdd * 3600L * 24L * 365L;
        }

        return 0;
    }

    public static String formatDuration(long totalSeconds) {
        StringBuilder stringBuilder = new StringBuilder();

        long milliseconds = totalSeconds * 1000;

        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        final long years = days / 365;
        if (years > 0) stringBuilder.append(years).append("y ");

        days %= 365;
        final long months = days / 30;
        if (months > 0) stringBuilder.append(months).append("mo ");

        days %= 30;
        final long weeks = days / 7;
        if (weeks > 0) stringBuilder.append(weeks).append("w ");

        days %= 7;
        if (days > 0) stringBuilder.append(days).append("d ");

        final long hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
                - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(milliseconds));
        if (hours > 0) stringBuilder.append(hours).append("h ");

        final long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
                - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds));
        if (minutes > 0) stringBuilder.append(minutes).append("m ");

        final long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
                - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        if (seconds > 0) stringBuilder.append(seconds).append("s ");

        return stringBuilder.toString().replaceAll("\\s+$", "");
    }
}
