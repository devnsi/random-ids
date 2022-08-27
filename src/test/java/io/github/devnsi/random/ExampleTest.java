package io.github.devnsi.random;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests for the examples in README.
 */
class ExampleTest {

    @Test
    void testExamples() {
        List<String> ids = RandomIds.next(5);

        assertNotNull(ids);
        ids.forEach(System.out::println);
    }

    @Test
    void testStaticExamples() {
        String id = RandomIds.next();
        List<String> ids = RandomIds.next(5);

        System.out.println(id);
        System.out.println(ids);
        assertNotNull(id);
        assertNotNull(ids);
    }

    @Test
    void testCustomExamples() {
        RandomIdGen random = new RandomIdGen("~", 5, new Random(0));
        String id = random.next();

        System.out.println(id);
        assertNotNull(id);
    }

    @Test
    void testStreamExamples() {
        RandomIdGen random = new RandomIdGen("", 3, new Random(0));
        random.setPostProcessor(word -> word.substring(0, 1).toUpperCase() + word.substring(1));
        random.stream().limit(3).forEach(System.out::println);

        assertNotNull(random);
    }
}
