package io.github.jodash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringsTest {

    @Test
    @DisplayName("Casing conversions work like Lodash")
    void testCasing() {
        assertThat(Jo.camelCase("Foo Bar")).isEqualTo("fooBar");
        assertThat(Jo.camelCase("--foo-bar--")).isEqualTo("fooBar");
        assertThat(Jo.camelCase("__FOO_BAR__")).isEqualTo("fooBar");

        assertThat(Jo.kebabCase("Foo Bar")).isEqualTo("foo-bar");
        assertThat(Jo.kebabCase("fooBar")).isEqualTo("foo-bar");
        assertThat(Jo.kebabCase("__FOO_BAR__")).isEqualTo("foo-bar");

        assertThat(Jo.snakeCase("Foo Bar")).isEqualTo("foo_bar");
        assertThat(Jo.snakeCase("fooBar")).isEqualTo("foo_bar");
        assertThat(Jo.snakeCase("foo-bar")).isEqualTo("foo_bar");

        assertThat(Jo.pascalCase("foo_bar")).isEqualTo("FooBar");
        assertThat(Jo.pascalCase("hello world")).isEqualTo("HelloWorld");

        assertThat(Jo.capitalize("hello")).isEqualTo("Hello");
        assertThat(Jo.uncapitalize("Hello")).isEqualTo("hello");
    }

    @Test
    @DisplayName("truncate trims strings with omission marker")
    void testTruncate() {
        assertThat(Jo.truncate("hi-diddly-ho there, neighborino", 24))
                .isEqualTo("hi-diddly-ho there, n...");

        assertThat(Jo.truncate("hello world", 8, " [..]"))
                .isEqualTo("hel [..]");
    }

    @Test
    @DisplayName("pad, padStart, padEnd pad characters")
    void testPadding() {
        assertThat(Jo.padStart("foo", 5, "0")).isEqualTo("00foo");
        assertThat(Jo.padEnd("foo", 5, "0")).isEqualTo("foo00");
        assertThat(Jo.pad("foo", 7, "-")).isEqualTo("--foo--");
    }

    @Test
    @DisplayName("escape and unescape handle HTML entities")
    void testHtmlEscaping() {
        String raw = "fred, barney, & pebbles < 10 > 5 'test' \"quotes\"";
        String escaped = Jo.escape(raw);
        assertThat(escaped).contains("&amp;").contains("&lt;").contains("&gt;").contains("&#39;").contains("&quot;");
        assertThat(Jo.unescape(escaped)).isEqualTo(raw);
    }
}
