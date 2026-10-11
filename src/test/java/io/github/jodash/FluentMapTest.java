package io.github.jodash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class FluentMapTest {

    @Test
    @DisplayName("FluentMap provides dynamic chaining like JS objects")
    void testFluentChaining() {
        FluentMap obj = Jo.obj()
                .set("name", "Hasan")
                .set("profile.role", "admin")
                .set("tags[0]", "java")
                .set("tags[1]", "go");

        assertThat(obj.<String>get("name")).isEqualTo("Hasan");
        assertThat(obj.<String>get("profile.role")).isEqualTo("admin");
        assertThat(obj.<String>get("tags[1]")).isEqualTo("go");
        assertThat(obj.<String>get("profile.missing", "fallback")).isEqualTo("fallback");
        assertThat(obj.has("profile.role")).isTrue();

        obj.unset("profile.role");
        assertThat(obj.has("profile.role")).isFalse();

        Map<String, Object> map = obj.toMap();
        assertThat(map).containsKey("name").containsKey("tags");
    }

    @Test
    @DisplayName("FluentMap constructor variants and operations: pick, omit, merge, toString")
    void testFluentMapAdvancedOperations() {
        // Constructor with initial Map
        FluentMap fromMap = new FluentMap(Map.of("a", 1, "b", 2, "c", 3));
        assertThat(fromMap.<Integer>get("a")).isEqualTo(1);

        // Constructor with null initial Map
        FluentMap fromNull = new FluentMap(null);
        assertThat(fromNull.toMap()).isEmpty();

        // pick()
        fromMap.pick("a", "b");
        assertThat(fromMap.toMap()).containsOnlyKeys("a", "b");

        // omit()
        fromMap.omit("a");
        assertThat(fromMap.toMap()).containsOnlyKeys("b");

        // merge()
        fromMap.merge(Map.of("c", 30), Map.of("d", 40));
        assertThat(fromMap.toMap()).containsEntry("b", 2).containsEntry("c", 30).containsEntry("d", 40);

        // toString()
        assertThat(fromMap.toString()).contains("b=2").contains("c=30");
    }
}
