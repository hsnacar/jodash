package sample_9_zero_dependency_json;

import java.util.*;

/**
 * SAMPLE 9: Zero-Dependency JSON Serialization
 * Standard Java (Vanilla) Implementation
 *
 * Problem: Java standard library (java.base) has NO built-in JSON stringifier.
 * Without pulling in external dependencies like Jackson or Gson (~2MB JARs),
 * developers must write custom serialization logic handling quotes, escaping,
 * commas, nested maps, lists, nulls, and formatting.
 */
public class StandardJava {

    public static void main(String[] args) {
        System.out.println("=== Standard Java: Zero-Dependency JSON Serialization ===");

        Map<String, Object> payload = createPayload();

        // Standard Java approach: Manual recursive stringifier
        String compactJson = toJson(payload);
        System.out.println("\n--- Compact JSON ---");
        System.out.println(compactJson);

        String prettyJson = toPrettyJson(payload, 0);
        System.out.println("\n--- Pretty-Printed JSON ---");
        System.out.println(prettyJson);
    }

    private static Map<String, Object> createPayload() {
        Map<String, Object> user = new LinkedHashMap<>();
        user.put("id", 1001);
        user.put("name", "Hasan Acar");
        user.put("roles", List.of("admin", "developer"));

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("active", true);
        meta.put("score", 98.5);

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("status", "SUCCESS");
        root.put("user", user);
        root.put("meta", meta);
        return root;
    }

    // ~40 lines of boilerplate for basic JSON serialization
    private static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String s) return "\"" + escape(s) + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return String.valueOf(obj);
        if (obj instanceof Map<?, ?> map) {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(escape(String.valueOf(entry.getKey()))).append("\":");
                sb.append(toJson(entry.getValue()));
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        if (obj instanceof Iterable<?> iter) {
            StringBuilder sb = new StringBuilder("[");
            boolean first = true;
            for (Object item : iter) {
                if (!first) sb.append(",");
                sb.append(toJson(item));
                first = false;
            }
            sb.append("]");
            return sb.toString();
        }
        return "\"" + escape(String.valueOf(obj)) + "\"";
    }

    private static String toPrettyJson(Object obj, int indentLevel) {
        String indent = "  ".repeat(indentLevel);
        String nextIndent = "  ".repeat(indentLevel + 1);

        if (obj == null) return "null";
        if (obj instanceof String s) return "\"" + escape(s) + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return String.valueOf(obj);
        if (obj instanceof Map<?, ?> map) {
            if (map.isEmpty()) return "{}";
            StringBuilder sb = new StringBuilder("{\n");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(",\n");
                sb.append(nextIndent).append("\"").append(escape(String.valueOf(entry.getKey()))).append("\": ");
                sb.append(toPrettyJson(entry.getValue(), indentLevel + 1));
                first = false;
            }
            sb.append("\n").append(indent).append("}");
            return sb.toString();
        }
        if (obj instanceof Iterable<?> iter) {
            List<Object> list = new ArrayList<>();
            iter.forEach(list::add);
            if (list.isEmpty()) return "[]";
            StringBuilder sb = new StringBuilder("[\n");
            boolean first = true;
            for (Object item : list) {
                if (!first) sb.append(",\n");
                sb.append(nextIndent).append(toPrettyJson(item, indentLevel + 1));
                first = false;
            }
            sb.append("\n").append(indent).append("]");
            return sb.toString();
        }
        return "\"" + escape(String.valueOf(obj)) + "\"";
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
