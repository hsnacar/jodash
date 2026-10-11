package io.github.jodash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CoerceTest {

    @Test
    @DisplayName("asInt safely parses ints, doubles, booleans, strings")
    void testAsInt() {
        assertThat(Jo.asInt(42, 0)).isEqualTo(42);
        assertThat(Jo.asInt("123", 0)).isEqualTo(123);
        assertThat(Jo.asInt("45.8", 0)).isEqualTo(45);
        assertThat(Jo.asInt(true, 0)).isEqualTo(1);
        assertThat(Jo.asInt(false, 0)).isEqualTo(0);
        assertThat(Jo.asInt("invalid", -1)).isEqualTo(-1);
        assertThat(Jo.asInt(null, 99)).isEqualTo(99);
    }

    @Test
    @DisplayName("asBoolean handles strings, numbers, booleans")
    void testAsBoolean() {
        assertThat(Jo.asBoolean(true, false)).isTrue();
        assertThat(Jo.asBoolean("true", false)).isTrue();
        assertThat(Jo.asBoolean("yes", false)).isTrue();
        assertThat(Jo.asBoolean("1", false)).isTrue();
        assertThat(Jo.asBoolean(1, false)).isTrue();

        assertThat(Jo.asBoolean(false, true)).isFalse();
        assertThat(Jo.asBoolean("false", true)).isFalse();
        assertThat(Jo.asBoolean("0", true)).isFalse();
        assertThat(Jo.asBoolean(null, true)).isTrue();
    }

    @Test
    @DisplayName("asDouble and asLong convert numeric representations safely")
    void testNumbers() {
        assertThat(Jo.asDouble("3.1415", 0.0)).isEqualTo(3.1415);
        assertThat(Jo.asDouble(100, 0.0)).isEqualTo(100.0);
        assertThat(Jo.asLong("9876543210", 0L)).isEqualTo(9876543210L);
    }

    @Test
    @DisplayName("asList and asMap safely convert containers")
    void testContainers() {
        List<Object> fromArray = Jo.asList(new String[]{"a", "b"});
        assertThat(fromArray).containsExactly("a", "b");

        List<Object> fromSingle = Jo.asList("single");
        assertThat(fromSingle).containsExactly("single");

        Map<String, Object> map = Jo.asMap(Map.of("key", "val"));
        assertThat(map).containsEntry("key", "val");

        Map<String, Object> fallback = Jo.asMap("not-a-map");
        assertThat(fallback).isEmpty();
    }
}
