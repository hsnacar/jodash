package io.github.jodash;

import io.github.jodash.core.ObjectPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EdgeCasesTest {

    @Test
    @DisplayName("ObjectPath handles 2D matrices and bracket notation with quotes")
    void testMatrixAndBracketQuotes() {
        List<List<Integer>> matrix = List.of(
                List.of(10, 20),
                List.of(30, 40)
        );

        assertThat(Jo.<Integer>get(matrix, "[1][0]")).isEqualTo(30);
        assertThat(Jo.<Integer>get(matrix, "1.1")).isEqualTo(40);

        Map<String, Object> specialMap = Map.of("special key", "val");
        assertThat(Jo.<String>get(specialMap, "['special key']")).isEqualTo("val");
    }

    @Test
    @DisplayName("Predicate pick and omit")
    void testPredicatePickAndOmit() {
        Map<String, Object> map = Jo.dict("a", 10, "b", "str", "c", 20);

        Map<String, Object> onlyNumbers = Jo.pick(map, (k, v) -> v instanceof Integer);
        assertThat(onlyNumbers).containsOnlyKeys("a", "c");

        Map<String, Object> withoutNumbers = Jo.omit(map, (k, v) -> v instanceof Integer);
        assertThat(withoutNumbers).containsOnlyKeys("b");
    }

    @Test
    @DisplayName("Map invert handles keys and values")
    void testInvert() {
        Map<String, Integer> map = Map.of("one", 1, "two", 2);
        Map<Integer, String> inverted = Jo.invert(map);

        assertThat(inverted).containsEntry(1, "one").containsEntry(2, "two");
        assertThat(Jo.invert(null)).isEmpty();
    }

    @Test
    @DisplayName("Collections handle nulls and empty boundary conditions")
    void testCollectionsBoundaries() {
        assertThat(Jo.chunk(null, 5)).isEmpty();
        assertThat(Jo.chunk(List.of(1, 2), 0)).isEmpty();
        assertThat(Jo.chunk(List.of(1, 2), -1)).isEmpty();
        assertThat(Jo.chunk(List.of(1, 2), 10)).hasSize(1);

        assertThat(Jo.groupBy(null, Object::toString)).isEmpty();
        assertThat(Jo.keyBy(null, Object::toString)).isEmpty();
        assertThat(Jo.countBy(null, Object::toString)).isEmpty();
        assertThat(Jo.compact(null)).isEmpty();
        assertThat(Jo.uniq(null)).isEmpty();
        assertThat(Jo.uniqBy(null, Object::toString)).isEmpty();
        assertThat(Jo.flatten(null)).isEmpty();
        assertThat(Jo.flattenDeep(null)).isEmpty();

        assertThat(Jo.difference(null, List.of(1))).isEmpty();
        assertThat(Jo.difference(List.of(1), null)).containsExactly(1);
        assertThat(Jo.intersection(null, List.of(1))).isEmpty();
        assertThat(Jo.intersection(List.of(1), null)).isEmpty();
        assertThat(Jo.union((Collection<Object>[]) null)).isEmpty();

        assertThat((Object) Jo.sample(null)).isNull();
        assertThat((Object) Jo.sample(List.of())).isNull();
        assertThat(Jo.sampleSize(null, 2)).isEmpty();
        assertThat(Jo.sampleSize(List.of(1, 2), 0)).isEmpty();
        assertThat(Jo.shuffle(null)).isEmpty();

        assertThat(Jo.maxBy(null, Object::hashCode)).isEmpty();
        assertThat(Jo.minBy(null, Object::hashCode)).isEmpty();
        assertThat(Jo.sumBy(null, x -> 1.0)).isEqualTo(0.0);
        assertThat(Jo.meanBy(null, x -> 1.0)).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Strings boundary checks")
    void testStringsBoundaries() {
        assertThat(Jo.words(null)).isEmpty();
        assertThat(Jo.words("")).isEmpty();
        assertThat(Jo.camelCase(null)).isEmpty();
        assertThat(Jo.kebabCase(null)).isEmpty();
        assertThat(Jo.snakeCase(null)).isEmpty();
        assertThat(Jo.pascalCase(null)).isEmpty();
        assertThat(Jo.capitalize(null)).isEmpty();
        assertThat(Jo.uncapitalize(null)).isEmpty();
        assertThat(Jo.truncate(null, 5)).isEmpty();
        assertThat(Jo.pad(null, 5, "-")).isEqualTo("-----");
        assertThat(Jo.padStart(null, 5, "-")).isEqualTo("-----");
        assertThat(Jo.padEnd(null, 5, "-")).isEqualTo("-----");
        assertThat(Jo.repeat(null, 3)).isEmpty();
        assertThat(Jo.repeat("a", 0)).isEmpty();
        assertThat(Jo.escape(null)).isEmpty();
        assertThat(Jo.unescape(null)).isEmpty();
    }

    @Test
    @DisplayName("ObjectPath set on null root throws IllegalArgumentException")
    void testSetNullRoot() {
        assertThatThrownBy(() -> ObjectPath.set(null, "a.b", 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("ObjectPath get with non-existing index or path returns default")
    void testGetNonExisting() {
        List<String> list = List.of("a", "b");
        assertThat(Jo.<String>get(list, "[5]", "fallback")).isEqualTo("fallback");
        assertThat(Jo.<String>get(list, "invalid_property", "fallback")).isEqualTo("fallback");
    }

    @Test
    @DisplayName("BUG-01: ThreadLocalRandom safely handles concurrent shuffle and sample across threads")
    void testBug01ConcurrentShuffleAndSample() throws Exception {
        int threads = 50;
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(threads);
        List<java.util.concurrent.Future<?>> futures = new ArrayList<>();
        List<Integer> list = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        for (int i = 0; i < threads; i++) {
            futures.add(executor.submit(() -> {
                for (int j = 0; j < 50; j++) {
                    List<Integer> shuffled = Jo.shuffle(list);
                    assertThat(shuffled).hasSize(10).containsAll(list);
                    Integer sample = Jo.sample(list);
                    assertThat(sample).isNotNull().isIn(list);
                }
            }));
        }

        for (java.util.concurrent.Future<?> f : futures) {
            f.get(5, java.util.concurrent.TimeUnit.SECONDS);
        }
        executor.shutdown();
    }

    @Test
    @DisplayName("BUG-02: padStart and padEnd with multi-character padding strings")
    void testBug02PadStartAndPadEndMultiChar() {
        assertThat(Jo.padStart("hi", 7, "abc")).isEqualTo("abcabhi");
        assertThat(Jo.padEnd("hi", 7, "abc")).isEqualTo("hiabcab");
        assertThat(Jo.padStart("test", 6, "ab")).isEqualTo("abtest");
        assertThat(Jo.padEnd("test", 6, "ab")).isEqualTo("testab");
    }

    @Test
    @DisplayName("BUG-03: unescape supports forward slash entity codes")
    void testBug03UnescapeForwardSlash() {
        assertThat(Jo.unescape("a&#47;b")).isEqualTo("a/b");
        assertThat(Jo.unescape("a&sol;b")).isEqualTo("a/b");
        assertThat(Jo.unescape("path&#47;to&#47;resource")).isEqualTo("path/to/resource");
    }

    @Test
    @DisplayName("BUG-04: Coerce.asBoolean handles float and double numbers without truncation")
    void testBug04AsBooleanFloatAndDouble() {
        assertThat(io.github.jodash.core.Coerce.asBoolean(0.1, false)).isTrue();
        assertThat(io.github.jodash.core.Coerce.asBoolean(0.5, false)).isTrue();
        assertThat(io.github.jodash.core.Coerce.asBoolean(0.0001f, false)).isTrue();
        assertThat(io.github.jodash.core.Coerce.asBoolean(-0.9, false)).isTrue();
        assertThat(io.github.jodash.core.Coerce.asBoolean(0.0, true)).isFalse();
        assertThat(io.github.jodash.core.Coerce.asBoolean(0f, true)).isFalse();
        assertThat(io.github.jodash.core.Coerce.asBoolean(Double.NaN, true)).isFalse();
    }

    @Test
    @DisplayName("BUG-05: Functions.memoize supplier executes once across concurrent threads")
    void testBug05MemoizeSupplierConcurrently() throws Exception {
        java.util.concurrent.atomic.AtomicInteger counter = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.function.Supplier<String> memoized = Jo.memoize(() -> {
            counter.incrementAndGet();
            try {
                Thread.sleep(20);
            } catch (InterruptedException ignored) {}
            return "computed";
        });

        int threads = 20;
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(threads);
        List<java.util.concurrent.Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            futures.add(executor.submit(memoized::get));
        }

        for (java.util.concurrent.Future<String> f : futures) {
            assertThat(f.get(5, java.util.concurrent.TimeUnit.SECONDS)).isEqualTo("computed");
        }
        executor.shutdown();
        assertThat(counter.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("BUG-06: intersection preserves ordered unique common elements")
    void testBug06IntersectionLinkedHashSet() {
        List<Integer> a = List.of(1, 2, 3, 2, 1);
        List<Integer> b = List.of(2, 3, 4, 3);
        List<Integer> result = Jo.intersection(a, b);
        assertThat(result).containsExactly(2, 3);
    }
}
