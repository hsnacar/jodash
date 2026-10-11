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
}
