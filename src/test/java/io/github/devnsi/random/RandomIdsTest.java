package io.github.devnsi.random;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomIdsTest {

    @Test
    void testNext() {
        int amount = 1000;
        List<String> random = IntStream.range(0, amount)
                .parallel()
                .mapToObj(value -> RandomIds.next())
                .collect(Collectors.toList());
        random.stream().limit(10L).forEach(System.out::println);
        checkDuplicates(amount, random);
    }

    @Test
    void testNextBatch() {
        int amount = 1000;
        List<String> random = RandomIds.next(amount);
        random.stream().limit(10L).forEach(System.out::println);
        checkDuplicates(amount, random);
    }

    @Test
    void testDuration() {
        int amount = 1000;
        List<Duration> durations = IntStream.range(0, amount)
                .mapToObj(i -> measureNext())
                .collect(Collectors.toList());
        double averageMs = getAverageMs(durations);
        assertTrue(averageMs <= 10);
    }

    @Test
    void testDurationBatch() {
        int amount = 1000;
        Instant begin = Instant.now();
        RandomIds.next(amount);
        Instant end = Instant.now();
        Duration diff = Duration.between(begin, end).abs();

        double averageMs = nanoToMilli(diff.getNano()) / amount;
        System.out.println("Average: " + averageMs + " ms.");
        assertTrue(averageMs <= 0.5);
    }

    private static void checkDuplicates(int amount, List<String> random) {
        long count = Integer.valueOf(random.size()).longValue();
        long countDistinct = random.stream().distinct().count();
        assertEquals(count, countDistinct);
        assertEquals(count, amount);
    }

    private static Duration measureNext() {
        Instant begin = Instant.now();
        RandomIds.next();
        Instant end = Instant.now();
        return Duration.between(begin, end).abs();
    }

    private double getAverageMs(List<Duration> durations) {
        double sum = durations.stream()
                .map(Duration::getNano)
                .map(this::nanoToMilli)
                .reduce(0d, Double::sum);
        double averageMs = sum / durations.size();
        System.out.println("Average: " + averageMs + " ms.");
        return averageMs;
    }

    private double nanoToMilli(double nano) {
        return Math.ceil(nano / 1000.0 / 1000.0); // nanoseconds, microseconds, milliseconds.
    }
}
