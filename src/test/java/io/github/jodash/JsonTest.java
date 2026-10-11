package io.github.jodash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonTest {

    record Address(String city, String zip) {}
    record User(String name, int age, Address address, List<String> tags) {}
    record Empty() {}

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
    @DisplayName("toJson serializes NaN and Infinities as null according to RFC 8259")
    void testNonFiniteNumbersSerializedAsNull() {
        Map<String, Object> numbers = Jo.dict(
                "nanDouble", Double.NaN,
                "posInfDouble", Double.POSITIVE_INFINITY,
                "negInfDouble", Double.NEGATIVE_INFINITY,
                "nanFloat", Float.NaN,
                "posInfFloat", Float.POSITIVE_INFINITY,
                "negInfFloat", Float.NEGATIVE_INFINITY,
                "validDouble", 3.14,
                "validFloat", 2.5f
        );

        String json = Jo.toJson(numbers);
        assertThat(json).contains("\"nanDouble\":null");
        assertThat(json).contains("\"posInfDouble\":null");
        assertThat(json).contains("\"negInfDouble\":null");
        assertThat(json).contains("\"nanFloat\":null");
        assertThat(json).contains("\"posInfFloat\":null");
        assertThat(json).contains("\"negInfFloat\":null");
        assertThat(json).contains("\"validDouble\":3.14");
        assertThat(json).contains("\"validFloat\":2.5");
    }

    static class CustomValidNumber extends Number {
        private final String val;
        CustomValidNumber(String val) { this.val = val; }
        @Override public int intValue() { return Integer.parseInt(val); }
        @Override public long longValue() { return Long.parseLong(val); }
        @Override public float floatValue() { return Float.parseFloat(val); }
        @Override public double doubleValue() { return Double.parseDouble(val); }
        @Override public String toString() { return val; }
    }

    static class CustomInvalidNumber extends Number {
        @Override public int intValue() { return 10; }
        @Override public long longValue() { return 10L; }
        @Override public float floatValue() { return 10f; }
        @Override public double doubleValue() { return 10.0; }
        @Override public String toString() { return "10 units"; }
    }

    @Test
    @DisplayName("toJson handles BigInteger, BigDecimal, and validates custom Number subclasses")
    void testNumberFormatsAndCustomNumbers() {
        Map<String, Object> map = Jo.dict(
                "bigDec", new java.math.BigDecimal("12345.6789"),
                "bigInt", new java.math.BigInteger("9876543210123456789"),
                "customValid", new CustomValidNumber("420"),
                "customInvalid", new CustomInvalidNumber()
        );

        String json = Jo.toJson(map);
        assertThat(json).contains("\"bigDec\":12345.6789");
        assertThat(json).contains("\"bigInt\":9876543210123456789");
        assertThat(json).contains("\"customValid\":420");
        // Non-standard numeric string representation is safely escaped as string
        assertThat(json).contains("\"customInvalid\":\"10 units\"");
    }

    @Test
    @DisplayName("toJson detects circular references and throws IllegalArgumentException")
    void testCircularReferenceDetection() {
        Map<String, Object> cycleMap = new HashMap<>();
        cycleMap.put("name", "loop");
        cycleMap.put("self", cycleMap);

        assertThatThrownBy(() -> Jo.toJson(cycleMap))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Circular reference detected");

        List<Object> cycleList = new ArrayList<>();
        cycleList.add("first");
        cycleList.add(cycleList);

        assertThatThrownBy(() -> Jo.toJson(cycleList))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Circular reference detected");
    }

    @Test
    @DisplayName("toJson allows diamond / shared sub-structures that are not circular")
    void testSharedSubstructureAllowed() {
        Map<String, Object> shared = Map.of("key", "val");
        List<Object> listWithDuplicates = List.of(shared, shared);

        String json = Jo.toJson(listWithDuplicates);
        assertThat(json).isEqualTo("[{\"key\":\"val\"},{\"key\":\"val\"}]");
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
        assertThat(Jo.toJson(new Empty())).isEqualTo("{}");
        assertThat(Jo.toJson(Map.of())).isEqualTo("{}");
        assertThat(Jo.toJson(List.of())).isEqualTo("[]");
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
