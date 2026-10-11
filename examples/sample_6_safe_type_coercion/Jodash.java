package sample_6_safe_type_coercion;

import static io.github.jodash.Jo.*;
import java.util.Map;

/**
 * SAMPLE 6: Safe Type Coercion from Loose JSON / Objects
 * Jodash Implementation
 */
public class Jodash {

    public static void main(String[] args) {
        Map<String, Object> rawData = dict(
                "userId", "10045",
                "active", "1",
                "score", 98.6,
                "missingKey", null
        );

        // 1. Parsing integer safely (1 line, handles string, double, null with fallback)
        int userId = asInt(rawData.get("userId"), 0);
        System.out.println("User ID: " + userId);

        // 2. Parsing boolean safely (1 line, handles "1", "true", 1, true)
        boolean active = asBoolean(rawData.get("active"), false);
        System.out.println("Active: " + active);

        // 3. Parsing double safely (1 line)
        double score = asDouble(rawData.get("score"), 0.0);
        System.out.println("Score: " + score);
    }
}
