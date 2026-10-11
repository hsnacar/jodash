package io.github.jodash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JodashFacadeTest {

    @Test
    @DisplayName("Jodash facade should provide identical behaviour to Jo")
    void testJodashFacadeEquivalence() {
        Map<String, Object> map = Jodash.dict("user", Jodash.dict("name", "Hasan", "age", 30));

        assertThat(Jodash.<String>get(map, "user.name")).isEqualTo("Hasan");
        assertThat(Jodash.has(map, "user.name")).isTrue();
        assertThat(Jodash.chunk(List.of(1, 2, 3), 2)).hasSize(2);
        assertThat(Jodash.camelCase("foo_bar")).isEqualTo("fooBar");
        assertThat(Jodash.kebabCase("FooBar")).isEqualTo("foo-bar");
        assertThat(Jodash.snakeCase("fooBar")).isEqualTo("foo_bar");
        assertThat(Jodash.asInt("42", 0)).isEqualTo(42);
        assertThat(Jodash.asBoolean("true", false)).isTrue();
        assertThat(Jodash.compact(List.of("a", ""))).containsExactly("a");
        assertThat(Jodash.uniq(List.of(1, 1, 2))).containsExactly(1, 2);
    }
}
