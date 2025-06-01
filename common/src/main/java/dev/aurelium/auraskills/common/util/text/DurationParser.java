package dev.aurelium.auraskills.common.util.text;

import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationParser {

    private DurationParser() {
    }

    private static final Pattern DURATION_PATTERN = Pattern.compile(
            "(?:(\\d+)y)?(?:(\\d+)mo)?(?:(\\d+)d)?(?:(\\d+)h)?(?:(\\d+)m)?(?:(\\d+)s)?"
    );

    public static Duration parse(String input) {
        Matcher matcher = DURATION_PATTERN.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid duration format: " + input);
        }

        long years = parseNumber(matcher.group(1));
        long months = parseNumber(matcher.group(2));
        long days = parseNumber(matcher.group(3));
        long hours = parseNumber(matcher.group(4));
        long minutes = parseNumber(matcher.group(5));
        long seconds = parseNumber(matcher.group(6));

        // Approximate 1 year = 365 days, 1 month = 30 days
        long totalDays = days + months * 30 + years * 365;

        return Duration.ofDays(totalDays)
                .plusHours(hours)
                .plusMinutes(minutes)
                .plusSeconds(seconds);
    }

    public static String toString(Duration duration) {
        long totalSeconds = duration.getSeconds();

        long totalDays = totalSeconds / (24 * 3600);
        long hours = (totalSeconds % (24 * 3600)) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        long years = totalDays / 365;
        totalDays %= 365;

        long months = totalDays / 30;
        long days = totalDays % 30;

        StringBuilder sb = new StringBuilder();
        if (years > 0) sb.append(years).append("y");
        if (months > 0) sb.append(months).append("mo");
        if (days > 0) sb.append(days).append("d");
        if (hours > 0) sb.append(hours).append("h");
        if (minutes > 0) sb.append(minutes).append("m");
        if (seconds > 0) sb.append(seconds).append("s");

        return sb.isEmpty() ? "0s" : sb.toString();
    }

    private static long parseNumber(String group) {
        return group == null ? 0 : Long.parseLong(group);
    }

}
