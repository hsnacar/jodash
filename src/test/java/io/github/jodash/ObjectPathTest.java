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

    static class UserWithFlag {
        private final boolean active;
        public UserWithFlag(boolean active) { this.active = active; }
        public boolean isActive() { return active; }
    }

    static class UserWithPrivateField {
        private final String secret = "hidden_val";
    }

    @Test
    @DisplayName("ObjectPath edge branches: empty paths, null values, list expansion, list unset, reflection variations")
    void testObjectPathEdgeBranches() {
        // Empty / blank paths
        assertThat(ObjectPath.parsePath(null)).isEmpty();
        assertThat(ObjectPath.parsePath("   ")).isEmpty();
        assertThat(ObjectPath.<String>get("any", "", "def")).isEqualTo("def");
        assertThat(ObjectPath.has("any", "")).isFalse();
        assertThat(ObjectPath.unset("any", "")).isFalse();

        Map<String, Object> root = new LinkedHashMap<>();
        assertThat(ObjectPath.set(root, "", "val")).isSameAs(root);

        // has() on map with explicit null value
        root.put("nullKey", null);
        assertThat(ObjectPath.has(root, "nullKey")).isTrue();
        assertThat(ObjectPath.has(root, "nonExistentKey")).isFalse();

        // set() navigating through non-container throws IllegalStateException
        root.put("scalar", "primitive");
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> ObjectPath.set(root, "scalar.child", 123))
                .isInstanceOf(IllegalStateException.class);

        // set() expanding list capacity
        Map<String, Object> listContainer = new LinkedHashMap<>();
        ObjectPath.set(listContainer, "arr[3]", "third");
        assertThat(listContainer.get("arr")).asList().hasSize(4);
        assertThat(ObjectPath.<String>get(listContainer, "arr[3]")).isEqualTo("third");
        assertThat(ObjectPath.<String>get(listContainer, "arr[0]")).isNull();

        // unset() on list elements
        Map<String, Object> usersMap = Jo.dict("items", Jo.list("x", "y", "z"));
        assertThat(ObjectPath.unset(usersMap, "items[1]")).isTrue();
        assertThat(usersMap.get("items")).asList().containsExactly("x", "z");
        assertThat(ObjectPath.unset(usersMap, "items[99]")).isFalse();
        assertThat(ObjectPath.unset(usersMap, "missing.node.leaf")).isFalse();

        // POJO boolean getter (is...)
        assertThat(ObjectPath.<Boolean>get(new UserWithFlag(true), "active")).isTrue();
        assertThat(ObjectPath.has(new UserWithFlag(true), "active")).isTrue();

        // POJO private field without getter
        assertThat(ObjectPath.<String>get(new UserWithPrivateField(), "secret")).isEqualTo("hidden_val");
        assertThat(ObjectPath.has(new UserWithPrivateField(), "secret")).isTrue();
        assertThat(ObjectPath.has(new UserWithPrivateField(), "nonExistentField")).isFalse();
    }
}
