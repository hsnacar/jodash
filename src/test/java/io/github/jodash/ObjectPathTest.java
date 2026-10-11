package io.github.jodash;

import io.github.jodash.core.ObjectPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectPathTest {

    record Address(String city, String zip) {}
    record UserRecord(String name, Address address, List<String> roles) {}

    static class PojoUser {
        private String username;
        private int age;
        public PojoUser(String username, int age) {
            this.username = username;
            this.age = age;
        }
        public String getUsername() { return username; }
        public int getAge() { return age; }
    }

    @Test
    @DisplayName("Should safely get nested property from Map")
    void testGetNestedMap() {
        Map<String, Object> data = Jo.dict(
                "user", Jo.dict(
                        "name", "Hasan",
                        "profile", Jo.dict("role", "admin")
                )
        );

        assertThat(ObjectPath.<String>get(data, "user.name")).isEqualTo("Hasan");
        assertThat(ObjectPath.<String>get(data, "user.profile.role")).isEqualTo("admin");
        assertThat(ObjectPath.<String>get(data, "user.missing.property", "defaultVal")).isEqualTo("defaultVal");
        assertThat(ObjectPath.<String>get(null, "any.path", "fallback")).isEqualTo("fallback");
    }

    @Test
    @DisplayName("Should safely navigate arrays and lists with bracket and dot index")
    void testGetListAndArray() {
        Map<String, Object> data = Jo.dict(
                "users", Jo.list(
                        Jo.dict("id", 1, "name", "Alice"),
                        Jo.dict("id", 2, "name", "Bob")
                ),
                "tags", new String[]{"java", "go", "react"}
        );

        assertThat(ObjectPath.<String>get(data, "users[0].name")).isEqualTo("Alice");
        assertThat(ObjectPath.<String>get(data, "users.1.name")).isEqualTo("Bob");
        assertThat(ObjectPath.<Integer>get(data, "users[1].id")).isEqualTo(2);
        assertThat(ObjectPath.<String>get(data, "tags[0]")).isEqualTo("java");
        assertThat(ObjectPath.<String>get(data, "tags[2]")).isEqualTo("react");
        assertThat(ObjectPath.<String>get(data, "tags[99]", "none")).isEqualTo("none");
    }

    @Test
    @DisplayName("Should extract properties from Java Records and POJOs")
    void testGetRecordsAndPojos() {
        UserRecord record = new UserRecord("Hasan", new Address("Istanbul", "34000"), List.of("admin", "dev"));
        PojoUser pojo = new PojoUser("hasan_dev", 28);

        assertThat(ObjectPath.<String>get(record, "name")).isEqualTo("Hasan");
        assertThat(ObjectPath.<String>get(record, "address.city")).isEqualTo("Istanbul");
        assertThat(ObjectPath.<String>get(record, "roles[1]")).isEqualTo("dev");

        assertThat(ObjectPath.<String>get(pojo, "username")).isEqualTo("hasan_dev");
        assertThat(ObjectPath.<Integer>get(pojo, "age")).isEqualTo(28);
    }

    @Test
    @DisplayName("Should check path existence with has()")
    void testHas() {
        Map<String, Object> map = Jo.dict("a", Jo.dict("b", Jo.list(10, 20)));

        assertThat(ObjectPath.has(map, "a.b[0]")).isTrue();
        assertThat(ObjectPath.has(map, "a.b[1]")).isTrue();
        assertThat(ObjectPath.has(map, "a.b[2]")).isFalse();
        assertThat(ObjectPath.has(map, "a.c")).isFalse();
        assertThat(ObjectPath.has(null, "a.b")).isFalse();
    }

    @Test
    @DisplayName("Should safely set nested properties creating intermediate structures")
    void testSet() {
        Map<String, Object> root = new LinkedHashMap<>();

        ObjectPath.set(root, "user.profile.name", "Hasan");
        ObjectPath.set(root, "user.roles[0]", "admin");
        ObjectPath.set(root, "user.roles[1]", "editor");

        assertThat(ObjectPath.<String>get(root, "user.profile.name")).isEqualTo("Hasan");
        assertThat(ObjectPath.<String>get(root, "user.roles[0]")).isEqualTo("admin");
        assertThat(ObjectPath.<String>get(root, "user.roles[1]")).isEqualTo("editor");
    }

    @Test
    @DisplayName("Should unset property cleanly")
    void testUnset() {
        Map<String, Object> map = Jo.dict("a", Jo.dict("b", 123, "c", 456));

        boolean removed = ObjectPath.unset(map, "a.b");
        assertThat(removed).isTrue();
        assertThat(ObjectPath.has(map, "a.b")).isFalse();
        assertThat(ObjectPath.has(map, "a.c")).isTrue();
    }
}
