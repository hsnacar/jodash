package sample_4_cleaning_deduplication;

import static io.github.jodash.Jo.*;
import java.util.List;
import java.util.Optional;

/**
 * SAMPLE 4: Cleaning Dirty Inputs & Deduplication
 * Jodash Implementation
 */
public class Jodash {

    public static void main(String[] args) {
        List<Object> dirtyData = list(
                "java", null, "", "   ", Optional.empty(), "spring", "java", "quarkus"
        );

        // 1. Compact in 1 line: removes nulls, blank strings, empty optionals
        List<String> cleanList = compact(dirtyData).stream().map(Object::toString).toList();
        System.out.println("Compacted: " + cleanList);

        // 2. Uniq in 1 line: preserves order
        List<String> uniqueList = uniq(cleanList);
        System.out.println("Unique: " + uniqueList);

        // 3. Partition in 1 line: clean record with matches() and rejects()
        var part = partition(list(1, 2, 3, 4, 5, 6, 7, 8), n -> n % 2 == 0);
        System.out.println("Evens: " + part.matches() + ", Odds: " + part.rejects());
    }
}
