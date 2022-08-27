package io.github.devnsi.random.dictionary;

import java.util.List;

/**
 * Dictionary allows access to a list of words.
 */
public interface Dictionary {

    /**
     * Amount of distinct words in this listing.
     * @return amount of words.
     */
    long size();

    /**
     * Read word at the position.
     * @param position between 0 and {@link #size}-1.
     * @return word at the position.
     */
    String readWord(long position);

    /**
     * Read words at the positions.
     * @param positions between 0 and {@link #size}-1.
     * @return word at the position.
     */
    List<String> readWords(Long... positions);
}
