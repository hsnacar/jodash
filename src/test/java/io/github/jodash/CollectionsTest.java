package io.github.jodash;

import io.github.jodash.core.JoCollections.Partition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionsTest {

    record Product(String name, String category, double price) {}

    @Test
    @DisplayName("chunk splits lists into chunks")
    void testChunk() {
        List<Integer> numbers = Jo.list(1, 2, 3, 4, 5);
        List<List<Integer>> chunks = Jo.chunk(numbers, 2);

        assertThat(chunks).hasSize(3);
        assertThat(chunks.get(0)).containsExactly(1, 2);
        assertThat(chunks.get(1)).containsExactly(3, 4);
        assertThat(chunks.get(2)).containsExactly(5);
    }

    @Test
    @DisplayName("groupBy and keyBy group and index items correctly")
    void testGroupByAndKeyBy() {
        List<Product> products = Jo.list(
                new Product("iPhone", "tech", 999.0),
                new Product("MacBook", "tech", 1999.0),
                new Product("T-Shirt", "clothing", 25.0)
        );

        Map<String, List<Product>> byCategory = Jo.groupBy(products, Product::category);
        assertThat(byCategory.get("tech")).hasSize(2);
        assertThat(byCategory.get("clothing")).hasSize(1);

        Map<String, Product> byName = Jo.keyBy(products, Product::name);
        assertThat(byName.get("iPhone").price()).isEqualTo(999.0);

        Map<String, Long> counts = Jo.countBy(products, Product::category);
        assertThat(counts.get("tech")).isEqualTo(2L);
        assertThat(counts.get("clothing")).isEqualTo(1L);
    }

    @Test
    @DisplayName("compact eliminates null, blank strings, empty optionals")
    void testCompact() {
        List<Object> dirty = Jo.list("hello", null, "", "  ", Optional.empty(), "world");
        List<Object> clean = Jo.compact(dirty);

        assertThat(clean).containsExactly("hello", "world");
    }

    @Test
    @DisplayName("uniq and uniqBy remove duplicates preserving order")
    void testUniq() {
        List<Integer> list = Jo.list(1, 2, 2, 3, 1, 4);
        assertThat(Jo.uniq(list)).containsExactly(1, 2, 3, 4);

        List<Product> products = Jo.list(
                new Product("Phone A", "tech", 100),
                new Product("Phone B", "tech", 200),
                new Product("Shirt", "clothing", 50)
        );
        List<Product> uniqueCategories = Jo.uniqBy(products, Product::category);
        assertThat(uniqueCategories).hasSize(2);
        assertThat(uniqueCategories.get(0).category()).isEqualTo("tech");
        assertThat(uniqueCategories.get(1).category()).isEqualTo("clothing");
    }

    @Test
    @DisplayName("flatten and flattenDeep unfold nested structures")
    void testFlatten() {
        List<List<Integer>> nested = Jo.list(Jo.list(1, 2), Jo.list(3, 4));
        assertThat(Jo.flatten(nested)).containsExactly(1, 2, 3, 4);

        List<Object> deep = Jo.list(1, Jo.list(2, Jo.list(3, new Integer[]{4, 5})));
        assertThat(Jo.flattenDeep(deep)).containsExactly(1, 2, 3, 4, 5);
    }

    @Test
    @DisplayName("partition splits list based on predicate")
    void testPartition() {
        List<Integer> numbers = Jo.list(1, 2, 3, 4, 5, 6);
        Partition<Integer> part = Jo.partition(numbers, n -> n % 2 == 0);

        assertThat(part.matches()).containsExactly(2, 4, 6);
        assertThat(part.rejects()).containsExactly(1, 3, 5);
    }

    @Test
    @DisplayName("difference, intersection, union handle set math")
    void testSetOperations() {
        List<Integer> a = Jo.list(1, 2, 3, 4);
        List<Integer> b = Jo.list(3, 4, 5, 6);

        assertThat(Jo.difference(a, b)).containsExactly(1, 2);
        assertThat(Jo.intersection(a, b)).containsExactly(3, 4);
        assertThat(Jo.union(a, b)).containsExactly(1, 2, 3, 4, 5, 6);
    }

    @Test
    @DisplayName("sortBy, minBy, maxBy, sumBy, meanBy perform analytics correctly")
    void testAnalytics() {
        List<Product> products = Jo.list(
                new Product("A", "tech", 100.0),
                new Product("B", "tech", 300.0),
                new Product("C", "tech", 200.0)
        );

        List<Product> sorted = Jo.sortBy(products, Product::price);
        assertThat(sorted.get(0).price()).isEqualTo(100.0);
        assertThat(sorted.get(1).price()).isEqualTo(200.0);
        assertThat(sorted.get(2).price()).isEqualTo(300.0);

        assertThat(Jo.maxBy(products, Product::price).orElseThrow().price()).isEqualTo(300.0);
        assertThat(Jo.minBy(products, Product::price).orElseThrow().price()).isEqualTo(100.0);
        assertThat(Jo.sumBy(products, Product::price)).isEqualTo(600.0);
        assertThat(Jo.meanBy(products, Product::price)).isEqualTo(200.0);
    }

    @Test
    @DisplayName("sample and shuffle randomize elements")
    void testSampling() {
        List<Integer> list = Jo.list(10, 20, 30, 40, 50);

        assertThat(list).contains(Jo.sample(list));
        assertThat(Jo.sampleSize(list, 3)).hasSize(3);
        assertThat(Jo.shuffle(list)).containsExactlyInAnyOrder(10, 20, 30, 40, 50);
    }

    @Test
    @DisplayName("keyBy overwrites duplicates with later values and handles nulls")
    void testKeyByDuplicatesAndNulls() {
        List<Product> products = Jo.list(
                new Product("Phone", "tech", 100),
                new Product("Phone", "tech", 200) // duplicate name
        );

        Map<String, Product> keyed = Jo.keyBy(products, Product::name);
        assertThat(keyed.get("Phone").price()).isEqualTo(200); // last wins

        assertThat(Jo.keyBy(products, null)).isEmpty();
        assertThat(Jo.groupBy(products, null)).isEmpty();

        // flatten with null inner list
        List<List<String>> withNulls = new ArrayList<>();
        withNulls.add(List.of("a"));
        withNulls.add(null);
        withNulls.add(List.of("b"));
        assertThat(Jo.flatten(withNulls)).containsExactly("a", "b");

        // sampleSize does not mutate input list
        List<Integer> original = Jo.list(1, 2, 3, 4, 5);
        List<Integer> sample = Jo.sampleSize(original, 2);
        assertThat(original).containsExactly(1, 2, 3, 4, 5);
        assertThat(sample).hasSize(2);
    }
}
