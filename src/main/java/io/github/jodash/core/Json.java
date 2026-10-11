package io.github.jodash.core;

import java.lang.reflect.Array;
import java.lang.reflect.RecordComponent;
import java.util.*;

/**
 * Zero-dependency JSON serializer for Maps, Collections, Arrays, Records, and Primitives.
 * Enables converting dynamic structures to valid JSON strings without Jackson or Gson.
 */
public final class Json {

    private Json() {}

    /**
     * Serializes an object to a compact JSON string.
     */
    public static String stringify(Object value) {
        StringBuilder sb = new StringBuilder();
        appendJson(sb, value, false, 0);
        return sb.toString();
    }

    /**
     * Serializes an object to a pretty-printed (indented) JSON string.
     */
    public static String pretty(Object value) {
        StringBuilder sb = new StringBuilder();
        appendJson(sb, value, true, 0);
        return sb.toString();
    }

    private static void appendJson(StringBuilder sb, Object value, boolean pretty, int indentLevel) {
        if (value == null) {
            sb.append("null");
            return;
        }

        if (value instanceof Optional<?> opt) {
            if (opt.isEmpty()) {
                sb.append("null");
            } else {
                appendJson(sb, opt.get(), pretty, indentLevel);
            }
            return;
        }

        if (value instanceof Boolean || value instanceof Number) {
            sb.append(value);
            return;
        }

        if (value instanceof CharSequence) {
            escapeString(sb, value.toString());
            return;
        }

        if (value instanceof Map<?, ?> map) {
            appendMap(sb, map, pretty, indentLevel);
            return;
        }

        if (value instanceof Collection<?> col) {
            appendCollection(sb, col, pretty, indentLevel);
            return;
        }

        if (value.getClass().isArray()) {
            appendArray(sb, value, pretty, indentLevel);
            return;
        }

        if (value.getClass().isRecord()) {
            appendRecord(sb, value, pretty, indentLevel);
            return;
        }

        // Fallback for custom objects / enums
        if (value instanceof Enum<?> e) {
            escapeString(sb, e.name());
        } else {
            escapeString(sb, value.toString());
        }
    }

    private static void appendMap(StringBuilder sb, Map<?, ?> map, boolean pretty, int indentLevel) {
        if (map.isEmpty()) {
            sb.append("{}");
            return;
        }

        sb.append("{");
        if (pretty) sb.append("\n");

        int size = map.size();
        int count = 0;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            count++;
            if (pretty) indent(sb, indentLevel + 1);

            escapeString(sb, String.valueOf(entry.getKey()));
            sb.append(pretty ? ": " : ":");
            appendJson(sb, entry.getValue(), pretty, indentLevel + 1);

            if (count < size) {
                sb.append(",");
            }
            if (pretty) sb.append("\n");
        }

        if (pretty) indent(sb, indentLevel);
        sb.append("}");
    }

    private static void appendCollection(StringBuilder sb, Collection<?> col, boolean pretty, int indentLevel) {
        if (col.isEmpty()) {
            sb.append("[]");
            return;
        }

        sb.append("[");
        if (pretty) sb.append("\n");

        int size = col.size();
        int count = 0;
        for (Object item : col) {
            count++;
            if (pretty) indent(sb, indentLevel + 1);

            appendJson(sb, item, pretty, indentLevel + 1);

            if (count < size) {
                sb.append(",");
            }
            if (pretty) sb.append("\n");
        }

        if (pretty) indent(sb, indentLevel);
        sb.append("]");
    }

    private static void appendArray(StringBuilder sb, Object array, boolean pretty, int indentLevel) {
        int length = Array.getLength(array);
        if (length == 0) {
            sb.append("[]");
            return;
        }

        sb.append("[");
        if (pretty) sb.append("\n");

        for (int i = 0; i < length; i++) {
            if (pretty) indent(sb, indentLevel + 1);

            appendJson(sb, Array.get(array, i), pretty, indentLevel + 1);

            if (i < length - 1) {
                sb.append(",");
            }
            if (pretty) sb.append("\n");
        }

        if (pretty) indent(sb, indentLevel);
        sb.append("]");
    }

    private static void appendRecord(StringBuilder sb, Object record, boolean pretty, int indentLevel) {
        RecordComponent[] components = record.getClass().getRecordComponents();
        if (components == null || components.length == 0) {
            sb.append("{}");
            return;
        }

        Map<String, Object> map = new LinkedHashMap<>();
        for (RecordComponent rc : components) {
            try {
                java.lang.reflect.Method accessor = rc.getAccessor();
                accessor.setAccessible(true);
                map.put(rc.getName(), accessor.invoke(record));
            } catch (Exception ignored) {}
        }
        appendMap(sb, map, pretty, indentLevel);
    }

    private static void escapeString(StringBuilder sb, String s) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 32) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        sb.append('"');
    }

    private static void indent(StringBuilder sb, int level) {
        sb.append("  ".repeat(Math.max(0, level)));
    }
}
