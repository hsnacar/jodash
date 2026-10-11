package io.github.jodash;

import io.github.jodash.core.JoCollections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class JodashFacadeTest {

    @Test
    @DisplayName("Jodash facade should provide full coverage of delegated methods")
    void testJodashFacadeFullCoverage() {
        // Object building & path navigation
        FluentMap fluent1 = Jodash.obj();
        assertThat(fluent1).isNotNull();

        FluentMap fluent2 = Jodash.obj(Map.of("init", "val"));
        assertThat(fluent2.<String>get("init")).isEqualTo("val");

        Map<String, Object> map = Jodash.dict("user", Jodash.dict("name", "Hasan", "age", 30));
        assertThat(Jodash.<String>get(map, "user.name")).isEqualTo("Hasan");
        assertThat(Jodash.<String>get(map, "user.missing", "def")).isEqualTo("def");
        assertThat(Jodash.has(map, "user.name")).isTrue();

        Jodash.set(map, "user.role", "admin");
        assertThat(Jodash.<String>get(map, "user.role")).isEqualTo("admin");

        boolean unset = Jodash.unset(map, "user.role");
        assertThat(unset).isTrue();
        assertThat(Jodash.has(map, "user.role")).isFalse();

        // Object utilities
        Map<String, Object> picked = Jodash.pick(map, "user");
        assertThat(picked).containsKey("user");

        Map<String, Object> omitted = Jodash.omit(map, "user");
        assertThat(omitted).isEmpty();

        Map<String, Object> merged = Jodash.merge(Jodash.dict("a", 1), Jodash.dict("b", 2));
        assertThat(merged).containsEntry("a", 1).containsEntry("b", 2);

        Map<String, Object> defaults = Jodash.defaults(Jodash.dict("a", 1), Jodash.dict("a", 99, "b", 2));
        assertThat(defaults).containsEntry("a", 1).containsEntry("b", 2);

        Map<String, Object> cloned = Jodash.cloneDeep(merged);
        assertThat(cloned).isEqualTo(merged);
        assertThat(Jodash.isEqual(cloned, merged)).isTrue();
        assertThat(Jodash.isEmpty(cloned)).isFalse();
        assertThat(Jodash.isEmpty("")).isTrue();

        // Collection utilities
        List<String> list = Jodash.list("a", "b", "c");
        assertThat(list).containsExactly("a", "b", "c");

        Set<String> set = Jodash.set("x", "y");
        assertThat(set).containsExactly("x", "y");

        assertThat(Jodash.chunk(List.of(1, 2, 3, 4), 2)).hasSize(2);
        assertThat(Jodash.groupBy(list, String::length).get(1)).hasSize(3);
        assertThat(Jodash.keyBy(list, s -> s)).containsKey("a");
        assertThat(Jodash.compact(List.of("a", "", "b"))).containsExactly("a", "b");
        assertThat(Jodash.uniq(List.of(1, 1, 2))).containsExactly(1, 2);
        assertThat(Jodash.uniqBy(list, String::length)).hasSize(1);
        assertThat(Jodash.flatten(List.of(List.of(1), List.of(2)))).containsExactly(1, 2);
        assertThat(Jodash.flattenDeep(List.of(List.of(1, List.of(2))))).containsExactly(1, 2);

        JoCollections.Partition<Integer> part = Jodash.partition(List.of(1, 2, 3), n -> n % 2 == 0);
        assertThat(part.matches()).containsExactly(2);
        assertThat(part.rejects()).containsExactly(1, 3);

        assertThat(Jodash.sample(list)).isIn(list);
        assertThat(Jodash.sampleSize(list, 2)).hasSize(2);
        assertThat(Jodash.shuffle(list)).containsExactlyInAnyOrderElementsOf(list);

        assertThat(Jodash.difference(List.of(1, 2), List.of(2, 3))).containsExactly(1);
        assertThat(Jodash.intersection(List.of(1, 2), List.of(2, 3))).containsExactly(2);
        assertThat(Jodash.union(List.of(1), List.of(2))).containsExactly(1, 2);
        assertThat(Jodash.sortBy(List.of(3, 1, 2), n -> n)).containsExactly(1, 2, 3);

        // String utilities
        assertThat(Jodash.camelCase("foo_bar")).isEqualTo("fooBar");
        assertThat(Jodash.kebabCase("FooBar")).isEqualTo("foo-bar");
        assertThat(Jodash.snakeCase("fooBar")).isEqualTo("foo_bar");
        assertThat(Jodash.pascalCase("foo_bar")).isEqualTo("FooBar");
        assertThat(Jodash.capitalize("foo")).isEqualTo("Foo");
        assertThat(Jodash.truncate("hello world", 8)).isEqualTo("hello...");
        assertThat(Jodash.pad("ab", 4, "-")).isEqualTo("-ab-");
        assertThat(Jodash.escape("<b>&</b>")).isEqualTo("&lt;b&gt;&amp;&lt;/b&gt;");
        assertThat(Jodash.unescape("&lt;b&gt;&amp;&lt;/b&gt;")).isEqualTo("<b>&</b>");

        // Coercion utilities
        assertThat(Jodash.asInt("42", 0)).isEqualTo(42);
        assertThat(Jodash.asLong("12345678901", 0L)).isEqualTo(12345678901L);
        assertThat(Jodash.asDouble("3.14", 0.0)).isEqualTo(3.14);
        assertThat(Jodash.asBoolean("true", false)).isTrue();
        assertThat(Jodash.asString(123, "")).isEqualTo("123");
        assertThat(Jodash.asList("item")).containsExactly("item");
        assertThat(Jodash.asMap(Map.of("k", "v"))).containsEntry("k", "v");

        // JSON serialization
        assertThat(Jodash.toJson(Map.of("a", 1))).isEqualTo("{\"a\":1}");
        assertThat(Jodash.toPrettyJson(Map.of("a", 1))).contains("  \"a\": 1");
    }
}
