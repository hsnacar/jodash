package io.github.jodash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class JsonTest {

    record Address(String city, String zip) {}
    record User(String name, int age, Address address, List<String> tags) {}

    enum Status { ACTIVE, PENDING }

    @Test
    @DisplayName("toJson serializes Maps, Collections, Records, and Primitives to compact JSON")
    void testToJsonSerialization() {
        Map<String, Object> map = Jo.dict(
                "title", "Hello \"World\"\nTest\tTab",
                "count", 42,
                "price", 19.99,
                "active", true,
                "missing", null,
                "opt", Optional.of("present"),
                "emptyOpt", Optional.empty(),
                "status", Status.ACTIVE,
                "items", List.of(1, 2, 3),
                "nested", Map.of("key", "val")
        );

        String json = Jo.toJson(map);
        assertThat(json).contains("\"title\":\"Hello \\\"World\\\"\\nTest\\tTab\"");
        assertThat(json).contains("\"count\":42");
        assertThat(json).contains("\"price\":19.99");
        assertThat(json).contains("\"active\":true");
        assertThat(json).contains("\"missing\":null");
        assertThat(json).contains("\"opt\":\"present\"");
        assertThat(json).contains("\"emptyOpt\":null");
        assertThat(json).contains("\"status\":\"ACTIVE\"");
        assertThat(json).contains("\"items\":[1,2,3]");
        assertThat(json).contains("\"nested\":{\"key\":\"val\"}");
    }

    @Test
    @DisplayName("toJson serializes Java Records and Arrays")
    void testRecordAndArraySerialization() {
        User user = new User("Hasan", 30, new Address("Istanbul", "34000"), List.of("java", "go"));
        String json = Jo.toJson(user);

        assertThat(json).contains("\"name\":\"Hasan\"");
        assertThat(json).contains("\"age\":30");
        assertThat(json).contains("\"city\":\"Istanbul\"");
        assertThat(json).contains("\"zip\":\"34000\"");
        assertThat(json).contains("\"tags\":[\"java\",\"go\"]");

        String[] arr = new String[]{"a", "b"};
        assertThat(Jo.toJson(arr)).isEqualTo("[\"a\",\"b\"]");
        assertThat(Jo.toJson(new int[]{})).isEqualTo("[]");
        assertThat(Jo.toJson(Map.of())).isEqualTo("{}");
    }

    @Test
    @DisplayName("toPrettyJson produces formatted multiline JSON with indentation")
    void testToPrettyJson() {
        Map<String, Object> map = Jo.dict("name", "Hasan", "skills", List.of("Java", "Go"));
        String pretty = Jo.toPrettyJson(map);

        assertThat(pretty).contains("\n");
        assertThat(pretty).contains("  \"name\": \"Hasan\"");
        assertThat(pretty).contains("  \"skills\": [");
    }

    @Test
    @DisplayName("FluentMap toJson and toPrettyJson delegation")
    void testFluentMapJson() {
        FluentMap obj = Jo.obj()
                .set("app", "jodash")
                .set("version", "0.1.0");

        assertThat(obj.toJson()).isEqualTo("{\"app\":\"jodash\",\"version\":\"0.1.0\"}");
        assertThat(obj.toPrettyJson()).contains("  \"app\": \"jodash\"");
    }
}
