package sample_4_cleaning_deduplication;

import java.util.*;

/**
 * SAMPLE 4: Cleaning Dirty Inputs & Deduplication
 * Standard Java (Vanilla / Streams) Implementation
 */
public class StandardJava {

    record Tag(String label, String category) {}

    public static void main(String[] args) {
        List<Object> dirtyData = Arrays.asList(
                "java", null, "", "   ", Optional.empty(), "spring", "java", "quarkus"
        );

        // 1. Compact: remove nulls, blank strings, empty optionals
        List<String> cleanList = new ArrayList<>();
        for (Object item : dirtyData) {
            if (item != null) {
                if (item instanceof String s && s.trim().isEmpty()) {
                    continue;
                }
                if (item instanceof Optional<?> opt && opt.isEmpty()) {
                    continue;
                }
                cleanList.add(item.toString());
            }
        }
        System.out.println("Compacted: " + cleanList);

        // 2. Uniq: preserve original encounter order
        Set<String> seen = new LinkedHashSet<>(cleanList);
        List<String> uniqueList = new ArrayList<>(seen);
        System.out.println("Unique: " + uniqueList);

        // 3. Partition: split into matches and rejects
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        List<Integer> evens = new ArrayList<>();
        List<Integer> odds = new ArrayList<>();
        for (Integer num : numbers) {
            if (num % 2 == 0) {
                evens.add(num);
            } else {
                odds.add(num);
            }
        }
        System.out.println("Evens: " + evens + ", Odds: " + odds);
    }
}
