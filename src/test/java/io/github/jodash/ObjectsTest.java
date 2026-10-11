package io.github.jodash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObjectsTest {

    @Test
    @DisplayName("dict creates populated LinkedHashMap")
    void testDict() {
        Map<String, Object> map = Jo.dict("a", 1, "b", "hello", "c", true);
        assertThat(map).containsEntry("a", 1).containsEntry("b", "hello").containsEntry("c", true);

        assertThatThrownBy(() -> Jo.dict("oddKey"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("pick and omit filter map properties correctly")
    void testPickAndOmit() {
        Map<String, Object> source = Jo.dict("id", 101, "password", "secret", "email", "test@example.com");

        Map<String, Object> picked = Jo.pick(source, "id", "email");
        assertThat(picked).containsOnlyKeys("id", "email");

        Map<String, Object> omitted = Jo.omit(source, "password");
        assertThat(omitted).containsOnlyKeys("id", "email");
    }

    @Test
    @DisplayName("merge deeply combines nested maps")
    void testMerge() {
        Map<String, Object> target = Jo.dict(
                "user", Jo.dict("name", "Hasan", "role", "user"),
                "status", "active"
        );

        Map<String, Object> source = Jo.dict(
                "user", Jo.dict("role", "admin", "verified", true),
                "ip", "127.0.0.1"
        );

        Map<String, Object> merged = Jo.merge(target, source);

        assertThat(Jo.<String>get(merged, "user.name")).isEqualTo("Hasan");
        assertThat(Jo.<String>get(merged, "user.role")).isEqualTo("admin");
        assertThat(Jo.<Boolean>get(merged, "user.verified")).isTrue();
        assertThat(Jo.<String>get(merged, "ip")).isEqualTo("127.0.0.1");
    }

    @Test
    @DisplayName("defaults fills missing properties without overwriting existing non-null ones")
    void testDefaults() {
        Map<String, Object> config = Jo.dict("port", 8080, "host", null);
        Map<String, Object> defaults = Jo.dict("port", 3000, "host", "localhost", "timeout", 5000);

        Map<String, Object> result = Jo.defaults(config, defaults);

        assertThat(result.get("port")).isEqualTo(8080);
        assertThat(result.get("host")).isEqualTo("localhost");
        assertThat(result.get("timeout")).isEqualTo(5000);
    }

    @Test
    @DisplayName("cloneDeep produces independent deep copy")
    void testCloneDeep() {
        Map<String, Object> original = Jo.dict("items", Jo.list("apple", "banana"));
        Map<String, Object> clone = Jo.cloneDeep(original);

        assertThat(clone).isEqualTo(original);
        ((List<String>) clone.get("items")).add("orange");

        assertThat(((List<?>) original.get("items"))).hasSize(2);
        assertThat(((List<?>) clone.get("items"))).hasSize(3);
    }

    @Test
    @DisplayName("isEqual compares deep equality properly")
    void testIsEqual() {
        Map<String, Object> a = Jo.dict("nums", Jo.list(1, 2, 3));
        Map<String, Object> b = Jo.dict("nums", Jo.list(1, 2, 3));
        Map<String, Object> c = Jo.dict("nums", Jo.list(1, 2, 4));

        assertThat(Jo.isEqual(a, b)).isTrue();
        assertThat(Jo.isEqual(a, c)).isFalse();
    }

    @Test
    @DisplayName("isEmpty validates empty states correctly")
    void testIsEmpty() {
        assertThat(Jo.isEmpty(null)).isTrue();
        assertThat(Jo.isEmpty("")).isTrue();
        assertThat(Jo.isEmpty("   ")).isFalse();
        assertThat(Jo.isEmpty(List.of())).isTrue();
        assertThat(Jo.isEmpty(Map.of())).isTrue();
        assertThat(Jo.isEmpty(new int[]{})).isTrue();
        assertThat(Jo.isEmpty(Optional.empty())).isTrue();

        assertThat(Jo.isEmpty("a")).isFalse();
        assertThat(Jo.isEmpty(List.of(1))).isFalse();
    }

    @Test
    @DisplayName("cloneDeep handles Sets, Arrays, and primitives")
    void testCloneDeepAdvanced() {
        assertThat((Object) Jo.cloneDeep(null)).isNull();
        assertThat(Jo.cloneDeep("immutable")).isEqualTo("immutable");
        assertThat(Jo.cloneDeep(123)).isEqualTo(123);

        Set<String> set = new LinkedHashSet<>(List.of("x", "y"));
        Set<String> clonedSet = Jo.cloneDeep(set);
        assertThat(clonedSet).containsExactly("x", "y");
        clonedSet.add("z");
        assertThat(set).hasSize(2);
        assertThat(clonedSet).hasSize(3);

        String[] arr = new String[]{"one", "two"};
        String[] clonedArr = Jo.cloneDeep(arr);
        assertThat(clonedArr).containsExactly("one", "two");
        clonedArr[0] = "mutated";
        assertThat(arr[0]).isEqualTo("one");
    }

    @Test
    @DisplayName("merge and defaults with null target, null sources, multiple sources")
    void testMergeAndDefaultsNullsAndMultiples() {
        // merge with null target
        Map<String, Object> target = Jo.merge(null, Map.of("a", 1));
        assertThat(target).containsEntry("a", 1);

        // merge with null source
        assertThat(Jo.merge(target, (Map<String, Object>[]) null)).isSameAs(target);
        assertThat(Jo.merge(target, (Map<String, Object>) null)).isSameAs(target);

        // defaults with null target
        Map<String, Object> defResult = Jo.defaults(null, Map.of("x", 10));
        assertThat(defResult).containsEntry("x", 10);

        // defaults with null source
        assertThat(Jo.defaults(defResult, (Map<String, Object>[]) null)).isSameAs(defResult);
        assertThat(Jo.defaults(defResult, (Map<String, Object>) null)).isSameAs(defResult);

        // nested defaults
        Map<String, Object> nestedTarget = Jo.dict("user", Jo.dict("name", "Hasan", "role", null));
        Map<String, Object> nestedDefaults = Jo.dict("user", Jo.dict("name", "DefaultName", "role", "user"));
        Jo.defaults(nestedTarget, nestedDefaults);
        assertThat(Jo.<String>get(nestedTarget, "user.name")).isEqualTo("Hasan");
        assertThat(Jo.<String>get(nestedTarget, "user.role")).isEqualTo("user");
    }

    @Test
    @DisplayName("isEqual handles arrays, maps, and lists branch conditions")
    void testIsEqualBranches() {
        // Arrays: different length, different items
        assertThat(Jo.isEqual(new int[]{1, 2}, new int[]{1, 2, 3})).isFalse();
        assertThat(Jo.isEqual(new int[]{1, 2}, new int[]{1, 3})).isFalse();
        assertThat(Jo.isEqual(new int[]{1, 2}, new int[]{1, 2})).isTrue();

        // Maps: different sizes, different keys
        assertThat(Jo.isEqual(Map.of("a", 1), Map.of("a", 1, "b", 2))).isFalse();
        assertThat(Jo.isEqual(Map.of("a", 1), Map.of("b", 1))).isFalse();
        assertThat(Jo.isEqual(Map.of("a", 1), Map.of("a", 2))).isFalse();

        // Lists: different sizes, different elements
        assertThat(Jo.isEqual(List.of(1, 2), List.of(1))).isFalse();
        assertThat(Jo.isEqual(List.of(1, 2), List.of(1, 3))).isFalse();

        // Sets: structural equality with nested items
        Set<Object> set1 = Set.of(new int[]{1, 2}, Map.of("k", "v"));
        Set<Object> set2 = Set.of(new int[]{1, 2}, Map.of("k", "v"));
        Set<Object> set3 = Set.of(new int[]{1, 3}, Map.of("k", "v"));
        assertThat(Jo.isEqual(set1, set2)).isTrue();
        assertThat(Jo.isEqual(set1, set3)).isFalse();
        assertThat(Jo.isEqual(Set.of(1), Set.of(1, 2))).isFalse();

        Set<Object> duplicateDeepValues = new LinkedHashSet<>();
        duplicateDeepValues.add(new int[]{1});
        duplicateDeepValues.add(new int[]{1});
        duplicateDeepValues.add(new int[]{2});
        Set<Object> differentDeepMultiplicity = new LinkedHashSet<>();
        differentDeepMultiplicity.add(new int[]{1});
        differentDeepMultiplicity.add(new int[]{2});
        differentDeepMultiplicity.add(new int[]{2});
        assertThat(Jo.isEqual(duplicateDeepValues, differentDeepMultiplicity)).isFalse();

        // Null comparisons
        assertThat(Jo.isEqual(null, null)).isTrue();
        assertThat(Jo.isEqual("a", null)).isFalse();
        assertThat(Jo.isEqual(null, "b")).isFalse();
    }

    @Test
    @DisplayName("dict handles empty and null varargs")
    void testDictBoundaries() {
        assertThat(Jo.dict((Object[]) null)).isEmpty();
        assertThat(Jo.dict()).isEmpty();
    }
}
