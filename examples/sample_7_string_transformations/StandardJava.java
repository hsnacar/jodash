package sample_7_string_transformations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SAMPLE 7: String Casing & Slugs
 * Standard Java (Vanilla) Implementation
 */
public class StandardJava {

    // Helper method required for converting string to kebab-case
    private static String toKebabCase(String input) {
        if (input == null || input.isBlank()) return "";
        String s = input.replaceAll("([a-z0-9])([A-Z])", "$1 $2")
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2")
                .replaceAll("[^a-zA-Z0-9]+", " ")
                .trim();
        return String.join("-", s.toLowerCase().split("\\s+"));
    }

    // Helper method for truncating
    private static String truncateString(String str, int maxLen, String omission) {
        if (str == null) return "";
        if (str.length() <= maxLen) return str;
        int subLen = maxLen - omission.length();
        return (subLen > 0 ? str.substring(0, subLen) : "") + omission;
    }

    public static void main(String[] args) {
        String title = "User Account Settings & Billing";

        // 1. Kebab-case URL slug
        String slug = toKebabCase(title);
        System.out.println("Slug: " + slug);

        // 2. Truncate
        String excerpt = truncateString("Long article title explaining the details of architecture", 25, "...");
        System.out.println("Excerpt: " + excerpt);
    }
}
