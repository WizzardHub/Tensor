package dev.wizzardr.tensor.util.math;


import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class MathUtil {

    /**
     * Reads tick delays from a file.
     *
     * @param filePath the path to the file
     * @return a list of tick delays
     * @throws IOException if an error occurs reading the file
     */
    public List<Integer> readTickDelays(Path filePath) throws IOException {
        try (var lines = Files.lines(filePath)) {
            return lines.map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .map(Integer::parseInt)
                    .toList();
        }
    }

    /**
     * Retrieves the creation date of a record file in milliseconds.
     *
     * @param filePath the path to the file
     * @return the creation time in millis, or -1 if an error occurs
     */
    public long getRecordDate(Path filePath) {
        try {
            var attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
            return attrs.creationTime().toMillis();
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Calculates the longest streak of consecutive identical tick values.
     *
     * @param tickDelays a list of tick delays
     * @return the length of the longest streak
     */
    public int calculateLongestStreak(List<Integer> tickDelays) {
        if (tickDelays.isEmpty()) return 0;

        int longestStreak = 1;
        int currentStreak = 1;
        var previousTick = tickDelays.get(0);

        for (int i = 1; i < tickDelays.size(); i++) {
            var currentTick = tickDelays.get(i);
            if (Objects.equals(currentTick, previousTick)) {
                currentStreak++;
                longestStreak = Math.max(longestStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
            previousTick = currentTick;
        }
        return longestStreak;
    }

    /**
     * Calculates a consistency score based on the frequency of the most common tick value.
     *
     * @param tickDelays a list of tick delays
     * @return the consistency score as a percentage
     */
    public double calculateConsistencyScore(List<Integer> tickDelays) {
        if (tickDelays.isEmpty()) return 0;

        var distribution = tickDelays.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        var mostCommon = distribution.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        return (mostCommon == null) ? 0 : (double) mostCommon.getValue() / tickDelays.size() * 100;
    }

    /**
     * Calculates the standard deviation of tick delays.
     *
     * @param tickDelays a list of tick delays
     * @param mean       the mean of the tick delays
     * @return the standard deviation
     */
    public double calculateStandardDeviation(List<Integer> tickDelays, double mean) {
        if (tickDelays.isEmpty()) return 0;

        var variance = tickDelays.stream()
                .mapToDouble(tick -> Math.pow(tick - mean, 2))
                .average()
                .orElse(0.0);
        return Math.sqrt(variance);
    }

    /**
     * Calculates a jitter score, representing the average absolute difference between consecutive tick values.
     *
     * @param tickDelays a list of tick delays
     * @return the jitter score
     */
    public double calculateJitterScore(List<Integer> tickDelays) {
        if (tickDelays.size() < 2) return 0;

        double totalChanges = 0;
        for (int i = 1; i < tickDelays.size(); i++) {
            totalChanges += Math.abs(tickDelays.get(i) - tickDelays.get(i - 1));
        }
        return totalChanges / (tickDelays.size() - 1);
    }

    /**
     * Detects fatigue by comparing the average tick delays of the first and second halves of the data.
     * Fatigue is considered detected if the second half is at least 15% slower.
     *
     * @param tickDelays a list of tick delays
     * @return {@code true} if fatigue is detected, {@code false} otherwise
     */
    public boolean detectFatigue(List<Integer> tickDelays) {
        if (tickDelays.size() < 10) return false;

        int halfSize = tickDelays.size() / 2;

        double firstHalfAvg = tickDelays.subList(0, halfSize).stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        double secondHalfAvg = tickDelays.subList(halfSize, tickDelays.size()).stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        return secondHalfAvg > firstHalfAvg * 1.15;
    }

    /**
     * Get color based on consistency score
     */
    public ChatColor getColorForConsistency(double score) {
        if (score >= 85) return ChatColor.GREEN;
        if (score >= 70) return ChatColor.DARK_GREEN;
        if (score >= 50) return ChatColor.YELLOW;
        if (score >= 30) return ChatColor.GOLD;
        return ChatColor.RED;
    }

    /**
     * Get color based on jitter score
     */
    public ChatColor getColorForJitter(double score) {
        if (score <= 0.5) return ChatColor.GREEN;  // Very stable
        if (score <= 1.0) return ChatColor.DARK_GREEN;  // Stable
        if (score <= 1.5) return ChatColor.YELLOW;  // Moderate
        if (score <= 2.5) return ChatColor.GOLD;  // Jittery
        return ChatColor.RED;  // Very jittery
    }

}