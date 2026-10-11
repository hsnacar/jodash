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
        assertThat(obj.has("profile.role")).isTrue();

        obj.unset("profile.role");
        assertThat(obj.has("profile.role")).isFalse();

        Map<String, Object> map = obj.toMap();
        assertThat(map).containsKey("name").containsKey("tags");
    }
}
