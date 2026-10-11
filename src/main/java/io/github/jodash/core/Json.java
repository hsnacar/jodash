package io.github.jodash.core;

import java.lang.reflect.Array;
import java.lang.reflect.RecordComponent;
import java.util.*;

/**
 * Zero-dependency JSON serializer for Maps, Collections, Arrays, Java Records, and Primitives.
 * Enables converting dynamic structures to RFC 8259-compliant JSON strings without Jackson or Gson.
 *
 * <p>Supported types:</p>
 * <ul>
 *   <li>{@link Map}: JSON Objects</li>
 *   <li>{@link Collection} and Arrays: JSON Arrays</li>
 *   <li>Java {@link Record}: JSON Objects (reflectively reads record components)</li>
 *   <li>{@link CharSequence}: Escaped JSON Strings</li>
 *   <li>{@link Number}: Valid finite numbers (NaN and Infinities are serialized as {@code null})</li>
 *   <li>{@link Boolean}: {@code true} / {@code false}</li>
 *   <li>{@link Enum}: String representation of enum name</li>
 *   <li>{@link Optional}: Value if present, {@code null} if empty</li>
 *   <li>Other custom objects: Serialized as their {@link Object#toString()} JSON string representation</li>
 * </ul>
 */
public final class Json {

    private Json() {}

    /**
     * Serializes an object to a compact JSON string.
     *
     * @param value object to serialize
     * @return compact JSON string
     * @throws IllegalArgumentException if a circular reference is detected
     */
    public static String stringify(Object value) {
        StringBuilder sb = new StringBuilder();
        Set<Object> activePath = Collections.newSetFromMap(new IdentityHashMap<>());
        appendJson(sb, value, false, 0, activePath);
        return sb.toString();
    }

    /**
     * Serializes an object to a pretty-printed (indented) JSON string.
     *
     * @param value object to serialize
     * @return 2-space indented JSON string
     * @throws IllegalArgumentException if a circular reference is detected
     */
    public static String pretty(Object value) {
        StringBuilder sb = new StringBuilder();
        Set<Object> activePath = Collections.newSetFromMap(new IdentityHashMap<>());
        appendJson(sb, value, true, 0, activePath);
        return sb.toString();
    }

    private static void appendJson(StringBuilder sb, Object value, boolean pretty, int indentLevel, Set<Object> activePath) {
        if (value == null) {
            sb.append("null");
            return;
        }

        if (value instanceof Optional<?> opt) {
            if (opt.isEmpty()) {
                sb.append("null");
            } else {
                appendJson(sb, opt.get(), pretty, indentLevel, activePath);
            }
            return;
        }

        if (value instanceof Boolean) {
            sb.append(value);
            return;
        }

        if (value instanceof Number n) {
            appendNumber(sb, n);
            return;
        }

        if (value instanceof CharSequence) {
            escapeString(sb, value.toString());
            return;
        }

        if (value instanceof Map<?, ?> map) {
            checkAndEnterContainer(value, activePath);
            try {
                appendMap(sb, map, pretty, indentLevel, activePath);
            } finally {
                activePath.remove(value);
            }
            return;
        }

        if (value instanceof Collection<?> col) {
            checkAndEnterContainer(value, activePath);
            try {
                appendCollection(sb, col, pretty, indentLevel, activePath);
            } finally {
                activePath.remove(value);
            }
            return;
        }

        if (value.getClass().isArray()) {
            checkAndEnterContainer(value, activePath);
            try {
                appendArray(sb, value, pretty, indentLevel, activePath);
            } finally {
                activePath.remove(value);
            }
            return;
        }

        if (value.getClass().isRecord()) {
            checkAndEnterContainer(value, activePath);
            try {
                appendRecord(sb, value, pretty, indentLevel, activePath);
            } finally {
                activePath.remove(value);
            }
            return;
        }

        // Fallback for enums and other custom objects
        if (value instanceof Enum<?> e) {
            escapeString(sb, e.name());
        } else {
            escapeString(sb, value.toString());
        }
    }

    private static void checkAndEnterContainer(Object value, Set<Object> activePath) {
        if (!activePath.add(value)) {
            throw new IllegalArgumentException("Circular reference detected in JSON serialization for object of type: " + value.getClass().getName());
        }
    }

    private static void appendMap(StringBuilder sb, Map<?, ?> map, boolean pretty, int indentLevel, Set<Object> activePath) {
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
            appendJson(sb, entry.getValue(), pretty, indentLevel + 1, activePath);

            if (count < size) {
                sb.append(",");
            }
            if (pretty) sb.append("\n");
        }

        if (pretty) indent(sb, indentLevel);
        sb.append("}");
    }

    private static void appendCollection(StringBuilder sb, Collection<?> col, boolean pretty, int indentLevel, Set<Object> activePath) {
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

            appendJson(sb, item, pretty, indentLevel + 1, activePath);

            if (count < size) {
                sb.append(",");
            }
            if (pretty) sb.append("\n");
        }

        if (pretty) indent(sb, indentLevel);
        sb.append("]");
    }

    private static void appendArray(StringBuilder sb, Object array, boolean pretty, int indentLevel, Set<Object> activePath) {
        int length = Array.getLength(array);
        if (length == 0) {
            sb.append("[]");
            return;
        }

        sb.append("[");
        if (pretty) sb.append("\n");

        for (int i = 0; i < length; i++) {
            if (pretty) indent(sb, indentLevel + 1);

            appendJson(sb, Array.get(array, i), pretty, indentLevel + 1, activePath);

            if (i < length - 1) {
                sb.append(",");
            }
            if (pretty) sb.append("\n");
        }

        if (pretty) indent(sb, indentLevel);
        sb.append("]");
    }

    private static void appendRecord(StringBuilder sb, Object record, boolean pretty, int indentLevel, Set<Object> activePath) {
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
            } catch (Exception e) {
                throw new IllegalStateException("Failed to read record component '" + rc.getName() + "' on " + record.getClass().getName(), e);
            }
        }
        appendMap(sb, map, pretty, indentLevel, activePath);
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

    private static final java.util.regex.Pattern JSON_NUMBER_PATTERN =
            java.util.regex.Pattern.compile("^-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?$");

    private static void appendNumber(StringBuilder sb, Number n) {
        if (n instanceof Double d) {
            if (!Double.isFinite(d)) {
                sb.append("null");
                return;
            }
            sb.append(d);
            return;
        }

        if (n instanceof Float f) {
            if (!Float.isFinite(f)) {
                sb.append("null");
                return;
            }
            sb.append(f);
            return;
        }

        if (n instanceof java.math.BigDecimal bd) {
            sb.append(bd.toPlainString());
            return;
        }

        if (n instanceof Integer || n instanceof Long || n instanceof Short || n instanceof Byte
                || n instanceof java.math.BigInteger
                || n instanceof java.util.concurrent.atomic.AtomicInteger
                || n instanceof java.util.concurrent.atomic.AtomicLong) {
            sb.append(n);
            return;
        }

        // Custom or non-standard Number implementations
        String str = n.toString();
        if (str != null && JSON_NUMBER_PATTERN.matcher(str).matches()) {
            sb.append(str);
        } else {
            escapeString(sb, str != null ? str : "");
        }
    }

    private static void indent(StringBuilder sb, int level) {
        sb.append("  ".repeat(Math.max(0, level)));
    }
}
