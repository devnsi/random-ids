package io.github.devnsi.random;

import io.github.devnsi.random.dictionary.Dictionary;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomIdGenTest {

    @Test
    void testNext() {
        String delimiter = "~";
        int amountParts = 5;
        RandomIdGen random = new RandomIdGen(delimiter, amountParts, new Random(0));

        String value = random.next();
        System.out.println(value);

        assertTrue(value.contains(delimiter));
        String[] parts = value.split(delimiter);
        assertEquals(amountParts, parts.length);
        assertTrue(Arrays.stream(parts).allMatch(s -> s != null && !s.isEmpty()));
    }

    @Test
    void testNextBatch() {
        String delimiter = "~";
        int amountParts = 5;
        RandomIdGen random = new RandomIdGen(delimiter, amountParts, new Random(0));

        List<String> value = random.next(5);
        System.out.println(value);

        assertEquals(5, value.size());
        assertTrue(value.stream().allMatch(s -> s != null && s.split(delimiter).length == amountParts));
    }

    @Test
    void testCustomDictionary() {
        String delimiter = "-";
        int amountParts = 10;
        DictionaryTest dictionary = new DictionaryTest();
        RandomIdGen random = new RandomIdGen(delimiter, amountParts, new Random(0), dictionary);

        random.next();
        random.next(10);

        for (Long position : dictionary.getLatestPositions()) {
            assertTrue(position >= 0, position + " must be above or equal to 0");
            assertTrue(position < dictionary.size(), position + " must be below dictionary size " + dictionary.size());
        }
    }

    @Test
    void testNextPostProcessing() {
        String delimiter = "-";
        int amountParts = 3;
        RandomIdGen random = new RandomIdGen(delimiter, amountParts, new Random(0)) {
            @Override
            protected String postProcess(String word) {
                return word.substring(0, 1).toUpperCase() + word.substring(1);
            }
        };

        String value = random.next();
        System.out.println(value);
        String[] parts = value.split(delimiter);
        assertEquals(amountParts, parts.length);
        for (String part : parts) {
            assertTrue(Character.isUpperCase(part.charAt(0)));
        }
    }

    @Test
    void testSupplier() {
        RandomIdGen random = new RandomIdGen("-", 2, new Random(0));

        List<String> collector = new LinkedList<>();
        Stream.generate(random).limit(10).forEach(collector::add);
        assertEquals(10, collector.size());
    }

    @Test
    void testStream() {
        RandomIdGen random = new RandomIdGen("-", 2, new Random(0));

        List<String> collector = random.stream().limit(10).collect(Collectors.toList());
        assertEquals(10, collector.size());
    }

    private static class DictionaryTest implements Dictionary {

        private Long[] latestPositions = new Long[0];

        @Override
        public long size() {
            return 3;
        }

        @Override
        public String readWord(long position) {
            this.latestPositions = new Long[]{position};
            return "word";
        }

        @Override
        public List<String> readWords(Long... positions) {
            this.latestPositions = positions;
            return IntStream.rangeClosed(1, positions.length).mapToObj(value -> "word").collect(Collectors.toList());
        }

        public Long[] getLatestPositions() {
            return this.latestPositions;
        }
    }
}
