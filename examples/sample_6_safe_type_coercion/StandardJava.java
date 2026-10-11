package sample_6_safe_type_coercion;

import java.util.*;

/**
 * SAMPLE 6: Safe Type Coercion from Loose JSON / Objects
 * Standard Java (Vanilla) Implementation
 */
public class StandardJava {

    public static void main(String[] args) {
        // Raw incoming map with loose types (numbers as strings, booleans as "true", decimals)
        Map<String, Object> rawData = new LinkedHashMap<>();
        rawData.put("userId", "10045");
        rawData.put("active", "1");
        rawData.put("score", 98.6);
        rawData.put("missingKey", null);

        // 1. Parsing integer safely (risk of ClassCastException or NumberFormatException)
        int userId = 0;
        Object rawUserId = rawData.get("userId");
        if (rawUserId instanceof Number n) {
            userId = n.intValue();
        } else if (rawUserId != null) {
            try {
                userId = Integer.parseInt(rawUserId.toString().trim());
            } catch (NumberFormatException ignored) {}
        }
        System.out.println("User ID: " + userId);

        // 2. Parsing boolean safely (from "1", "true", true, 1)
        boolean active = false;
        Object rawActive = rawData.get("active");
        if (rawActive instanceof Boolean b) {
            active = b;
        } else if (rawActive instanceof Number n) {
            active = n.intValue() != 0;
        } else if (rawActive != null) {
            String s = rawActive.toString().trim().toLowerCase();
            active = "true".equals(s) || "1".equals(s) || "yes".equals(s);
        }
        System.out.println("Active: " + active);

        // 3. Parsing double safely
        double score = 0.0;
        Object rawScore = rawData.get("score");
        if (rawScore instanceof Number n) {
            score = n.doubleValue();
        } else if (rawScore != null) {
            try {
                score = Double.parseDouble(rawScore.toString().trim());
            } catch (NumberFormatException ignored) {}
        }
        System.out.println("Score: " + score);
    }
}
