package io.github.jodash.core;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Safe pragmatic type coercion and casting utilities.
 * Prevents runtime ClassCastException when working with dynamic objects, JSON, or Map values.
 */
public final class Coerce {

    private Coerce() {}

    /**
     * Safely converts value to int, or returns defaultValue if null or unparseable.
     */
    public static int asInt(Object value, int defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Number n) return n.intValue();
        if (value instanceof Boolean b) return b ? 1 : 0;
        try {
            return Integer.parseInt(value.toString().trim());
        } catch (NumberFormatException e) {
            try {
                return (int) Double.parseDouble(value.toString().trim());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
    }

    /**
     * Safely converts value to long, or returns defaultValue.
     */
    public static long asLong(Object value, long defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Number n) return n.longValue();
        if (value instanceof Boolean b) return b ? 1L : 0L;
        try {
            return Long.parseLong(value.toString().trim());
        } catch (NumberFormatException e) {
            try {
                return (long) Double.parseDouble(value.toString().trim());
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
    }

    /**
     * Safely converts value to double, or returns defaultValue.
     */
    public static double asDouble(Object value, double defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(value.toString().trim());
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    /**
     * Safely converts value to boolean.
     * Evaluates "true", "1", 1, true as true; everything else as false unless default provided.
     */
    public static boolean asBoolean(Object value, boolean defaultValue) {
        if (value == null) return defaultValue;
        if (value instanceof Boolean b) return b;
        if (value instanceof Number n) return !Double.isNaN(n.doubleValue()) && n.doubleValue() != 0.0;
        String s = value.toString().trim().toLowerCase();
        if ("true".equals(s) || "1".equals(s) || "yes".equals(s) || "y".equals(s)) return true;
        if ("false".equals(s) || "0".equals(s) || "no".equals(s) || "n".equals(s)) return false;
        return defaultValue;
    }

    /**
     * Safely converts value to string, or returns defaultValue if null.
     */
    public static String asString(Object value, String defaultValue) {
        if (value == null) return defaultValue;
        return value.toString();
    }

    /**
     * Safely casts or converts value to a List of objects.
     */
    @SuppressWarnings("unchecked")
    public static List<Object> asList(Object value) {
        if (value == null) return new ArrayList<>();
        if (value instanceof List<?> list) return (List<Object>) list;
        if (value instanceof Collection<?> col) return new ArrayList<>(col);
        if (value.getClass().isArray()) {
            int len = Array.getLength(value);
            List<Object> list = new ArrayList<>(len);
            for (int i = 0; i < len; i++) {
                list.add(Array.get(value, i));
            }
            return list;
        }
        return new ArrayList<>(Collections.singletonList(value));
    }

    /**
     * Safely casts value to {@code Map<String, Object>} or returns an empty LinkedHashMap.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> result = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                result.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            return result;
        }
        return new LinkedHashMap<>();
    }
}
