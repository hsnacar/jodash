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
}
