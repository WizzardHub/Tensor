package dev.wizzardr.tensor.util;

import java.util.ArrayDeque;
import java.util.stream.Collectors;

public class DequeUtil {
    /**
     * Creates a new ArrayDeque instance containing the most recent elements from the original deque.
     *
     * @param originalDeque the original ArrayDeque
     * @param newSize the desired size of the new deque
     * @return a new ArrayDeque instance with the most recent elements
     */
    public static <T> ArrayDeque<T> resize(ArrayDeque<T> originalDeque, int newSize) {
        return originalDeque.size() <= newSize
                ? new ArrayDeque<>(originalDeque)
                : originalDeque.stream()
                .skip(originalDeque.size() - newSize)
                .collect(Collectors.toCollection(ArrayDeque::new));
    }

    /**
     * Removes the specified number of entries from the front of the given ArrayDeque.
     *
     * @param deque  the ArrayDeque to truncate
     * @param numToRemove the number of entries to remove
     */
    public static void removeEntries(ArrayDeque<?> deque, int numToRemove) {
        if (numToRemove <= 0) {
            return;
        }

        for (int i = 0; i < numToRemove && !deque.isEmpty(); i++) {
            deque.pollFirst();
        }
    }
}
