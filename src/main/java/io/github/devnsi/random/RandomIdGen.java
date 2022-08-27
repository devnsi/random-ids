package io.github.devnsi.random;

import io.github.devnsi.random.dictionary.Dictionary;
import io.github.devnsi.random.dictionary.RandomWords;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * {@link RandomIds} adds the lightweight capabilities to generate memorable identifiers.
 */
public class RandomIdGen implements Supplier<String> {

    /** Delimiter by which words are concatenated to an identifier. */
    protected final String delimiter;

    /** Amount of words that the identifier consists of. */
    protected final int amountParts;

    /** Random number generator to determine words. */
    protected final Random random;

    /** Dictionary from which to choose words from. */
    protected final Dictionary dictionary;

    /** Post-processing on each individual word. */
    protected Function<String, String> postProcessor = Function.identity();

    /**
     * Generator for human-readable random identifiers.
     */
    public RandomIdGen() {
        this("-", 3, ThreadLocalRandom.current());
    }

    /**
     * Generator for human-readable random identifiers.
     * @param delimiter to be used to concatenate words.
     * @param amountParts to be concatenated (increasing entropy).
     * @param random to create reproducable results (if seeded invariably).
     */
    public RandomIdGen(String delimiter, int amountParts, Random random) {
        this(delimiter, amountParts, random, new RandomWords());
    }

    /**
     * Generator for human-readable random identifiers.
     * @param delimiter to be used to concatenate words.
     * @param amountParts to be concatenated (increasing entropy).
     * @param random to create reproducable results (if seeded invariably).
     * @param dictionary from which to build the identifier.
     */
    public RandomIdGen(String delimiter, int amountParts, Random random, Dictionary dictionary) {
        this.delimiter = delimiter;
        this.amountParts = amountParts;
        this.random = random;
        this.dictionary = dictionary;
    }

    /**
     * {@inheritDoc}
     * @see #next()
     */
    @Override
    public String get() {
        return next();
    }

    /**
     * <b>Infinite</b> stream of random identifiers.
     * @return infinite stream, use with {@link Stream#limit(long)} or another terminal operation.
     */
    public Stream<String> stream() {
        return Stream.generate(this::next);
    }

    /**
     * Generates a new identifier.
     * @return generated identifier.
     */
    public String next() {
        Long[] randoms = IntStream.range(0, this.amountParts)
                .mapToObj(value -> randomValue())
                .toArray(Long[]::new);
        String word = this.dictionary.readWords(randoms).stream()
                .map(this::postProcess)
                .collect(Collectors.joining(this.delimiter));
        return postProcess(word);
    }

    /**
     * Generates new batch of identifiers.
     * @return generated identifiers.
     */
    public List<String> next(int amount) {
        Long[] randoms = IntStream.range(0, this.amountParts * amount)
                .mapToObj(value -> randomValue())
                .toArray(Long[]::new);
        List<String> words = this.dictionary.readWords(randoms).stream()
                .map(this::postProcess)
                .collect(Collectors.toList());

        return buildResults(amount, words);
    }

    /**
     * Determines the position of the next random word within the dictionary.
     * @return random position in dictionary.
     */
    protected long randomValue() {
        long pos = this.random.nextLong();
        return Math.abs(pos) % this.dictionary.size();
    }

    protected List<String> buildResults(int amount, List<String> words) {
        List<String> results = new ArrayList<>(amount);
        for (int i = 0; i < amount; i++) {
            String result = partitionByIndex(words, i);
            results.add(result);
        }
        return results;
    }

    protected String partitionByIndex(List<String> words, int partitionIndex) {
        int partitionStart = partitionIndex * this.amountParts;
        int partitionEnd = partitionStart + this.amountParts;
        List<String> partition = words.subList(partitionStart, partitionEnd);
        return String.join(this.delimiter, partition);
    }

    /**
     * Post-processing on each individual word to handle casing, encoding, ...
     * @param word that was looked up.
     * @return modified word.
     */
    protected String postProcess(String word) {
        return this.postProcessor.apply(word);
    }

    /**
     * Set post-processor for each individual word contained in the identifier.
     * @param postProcessor to modify words.
     */
    public void setPostProcessor(UnaryOperator<String> postProcessor) {
        this.postProcessor = postProcessor;
    }
}
