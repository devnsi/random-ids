package io.github.devnsi.random.dictionary;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceWordsTest {

    private final Dictionary dictionary = new ResourceWords("random-ids/words-test.txt");

    @Test
    void testSize() {
        assertEquals(26, this.dictionary.size());
    }

    @Test
    void testNormalizingMin() {
        String a = this.dictionary.readWord(-1L);
        String b = this.dictionary.readWord(0);
        assertEquals(b, a);
    }

    @Test
    void testMin() {
        String firstWord = this.dictionary.readWord(0L);
        System.out.println(firstWord);
        assertEquals("a", firstWord);
    }

    @Test
    void testMultiple() {
        List<String> words = this.dictionary.readWords(this.dictionary.size() - 1, 0L, 10L);
        System.out.println(words);
        assertEquals("z", words.get(0));
        assertEquals("a", words.get(1));
        assertEquals("k", words.get(2));
    }

    @Test
    void testMultipleEmpty() {
        List<String> words = this.dictionary.readWords();
        System.out.println(words);
        assertTrue(words.isEmpty());
    }

    @Test
    void testMultipleDuplicates() {
        List<String> words = this.dictionary.readWords(10L, 10L);
        System.out.println(words);
        assertEquals("k", words.get(0));
        assertEquals("k", words.get(1));
    }

    @Test
    void testMax() {
        String lastWord = this.dictionary.readWord(this.dictionary.size() - 1);
        System.out.println(lastWord);
        assertEquals("z", lastWord);
    }

    @Test
    void testNormalizingMax() {
        long size = this.dictionary.size();
        String a = this.dictionary.readWord(size - 1);
        String b = this.dictionary.readWord(size);
        String c = this.dictionary.readWord(size + 1);
        assertEquals(a, b);
        assertEquals(a, c);
    }

    @Test
    void testInitializationNoFile() {
        assertThrowsExactly(IllegalStateException.class, () -> new ResourceWords("test"));
    }
}
