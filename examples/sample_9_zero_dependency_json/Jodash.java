package sample_9_zero_dependency_json;

import static io.github.jodash.Jo.*;

import java.util.List;
import java.util.Map;

/**
 * SAMPLE 9: Zero-Dependency JSON Serialization
 * Jodash Implementation
 *
 * Solution: Jodash provides built-in toJson and toPrettyJson methods
 * requiring 0 external dependencies (no Jackson, no Gson, lightweight ~37KB verified JAR footprint).
 * Works directly on Maps, Records, Lists, Sets, Arrays, and Primitives.
 */
public class Jodash {

    public static void main(String[] args) {
        System.out.println("=== Jodash: Zero-Dependency JSON Serialization ===");

        // 1. Fluent Dynamic Map Construction
        var response = obj()
                .set("status", "SUCCESS")
                .set("user.id", 1001)
                .set("user.name", "Hasan Acar")
                .set("user.roles", List.of("admin", "developer"))
                .set("meta.active", true)
                .set("meta.score", 98.5);

        // 2. Compact JSON in 1 line
        String compactJson = response.toJson();
        System.out.println("\n--- Compact JSON (FluentMap.toJson) ---");
        System.out.println(compactJson);

        // 3. Pretty-Printed JSON for logging / debugging
        String prettyJson = response.toPrettyJson();
        System.out.println("\n--- Pretty-Printed JSON (FluentMap.toPrettyJson) ---");
        System.out.println(prettyJson);

        // 4. Static utility on any existing Map or Record
        Map<String, Object> regularMap = Map.of("code", 200, "message", "OK");
        System.out.println("\n--- Static Utility toJson(regularMap) ---");
        System.out.println(toJson(regularMap));
    }
}
