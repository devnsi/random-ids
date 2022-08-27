package io.github.devnsi.random.dictionary;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Access to a word list in resources.
 * <p>
 * Resource file is expected to be newline-delimited.
 */
public class ResourceWords implements Dictionary {

    /** Amount of distinct words available. */
    protected final long amount;
    protected final String resourcePath;

    public ResourceWords(String path) {
        this.resourcePath = path;
        this.amount = countWords();
    }

    /** Determined amount of words in the dictionary. */
    private int countWords() {
        try {
            return countWordsTry();
        } catch (IOException e) {
            throw new IllegalStateException("words could not be read.", e);
        }
    }

    private int countWordsTry() throws IOException {
        InputStream input = getRessource();
        Reader reader = new InputStreamReader(input);
        try (LineNumberReader lineNumberReader = new LineNumberReader(reader)) {
            lineNumberReader.skip(Long.MAX_VALUE);
            return lineNumberReader.getLineNumber();
        }
    }

    private InputStream getRessource() {
        ClassLoader cls = Thread.currentThread().getContextClassLoader();
        InputStream resource = cls.getResourceAsStream(this.resourcePath);
        return Optional.ofNullable(resource).orElseThrow(() -> {
            String message = String.format("could not find resource %s", this.resourcePath);
            return new IllegalStateException(message);
        });
    }

    @Override
    public long size() {
        return this.amount;
    }

    /**
     * Read word at the position.
     * @param position between 0 and {@link #size()}-1.
     * @return word at the position.
     */
    @Override
    public String readWord(long position) {
        return readWords(position).get(0);
    }

    /**
     * Read words at the positions.
     * @param positions between 0 and {@link #size()}-1.
     * @return word at the position.
     */
    @Override
    public List<String> readWords(Long... positions) {
        LinkedList<Long> sorted = Arrays.stream(positions)
                .map(this::normalized)
                .collect(Collectors.toCollection(LinkedList::new));
        InputStream input = getRessource();
        Reader reader = new InputStreamReader(input);
        try (LineNumberReader lineNumberReader = new LineNumberReader(reader)) {
            return readWordTry(sorted, lineNumberReader);
        } catch (IOException exception) {
            String message = String.format("could not read resource %s", this.resourcePath);
            throw new IllegalStateException(message, exception);
        }
    }

    protected long normalized(long position) {
        return Math.min(Math.max(0, position), this.amount - 1);
    }

    /**
     * Replaces the positions with the corresponding words. Upholds the order of the requested positions.
     * @param positions of the requested words
     * @param reader to read the words.
     * @return words corresponding to the positions.
     * @throws IOException while reading the dictionary source.
     */
    protected static List<String> readWordTry(List<Long> positions, LineNumberReader reader) throws IOException {
        List<String> words = initializeWithValues(positions);
        Iterator<Long> lookup = positions.stream()
                .map(p -> p + 1) // lines are 1-indexed in stream.
                .distinct() // because any words needs to be only resolved once.
                .sorted() // lookup lines in order to reduce seeking.
                .iterator();

        while (lookup.hasNext()) {
            long lookupNext = lookup.next();
            String correspondingWord = seekNext(reader, lookupNext);
            replaceAll(words, lookupNext, correspondingWord);
        }
        return words;
    }

    protected static String seekNext(LineNumberReader reader, long lookupNext) throws IOException {
        String line;
        boolean continueSeeking;
        do {
            line = reader.readLine();
            boolean noEOF = line != null;
            boolean noMatch = reader.getLineNumber() != lookupNext;
            continueSeeking = noEOF && noMatch;
        } while (continueSeeking);
        return line;
    }

    protected static List<String> initializeWithValues(List<Long> positions) {
        return positions.stream()
                .map(String::valueOf)
                .collect(Collectors.toCollection(() -> new ArrayList<>(positions.size())));
    }

    protected static void replaceAll(List<String> words, long position, String resolved) {
        words.replaceAll(s -> s.equals(String.valueOf(position - 1)) ? resolved : s);
    }
}
