package io.github.jodash.core;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Safe deep property access and mutation for Maps, Collections, Arrays, Records, and POJOs.
 */
public final class ObjectPath {

    private static final Pattern PATH_TOKEN_PATTERN = Pattern.compile("([^\\[\\].]+)|\\[(\\d+)\\]|\\[['\"]([^'\"]+)['\"]\\]");

    private ObjectPath() {}

    /**
     * Parses a dot/bracket path expression (e.g. "users[0].address.city", "items.0.name") into tokens.
     */
    public static List<String> parsePath(String path) {
        if (path == null || path.isBlank()) {
            return Collections.emptyList();
        }
        List<String> tokens = new ArrayList<>();
        Matcher matcher = PATH_TOKEN_PATTERN.matcher(path);
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                tokens.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                tokens.add(matcher.group(2));
            } else if (matcher.group(3) != null) {
                tokens.add(matcher.group(3));
            }
        }
        return tokens;
    }

    /**
     * Safely navigates through an object hierarchy and retrieves the value at the given path.
     * Returns null if any segment is missing.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object root, String path) {
        return get(root, path, null);
    }

    /**
     * Safely navigates through an object hierarchy and retrieves the value at the given path,
     * or returns defaultValue if not found or null.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(Object root, String path, T defaultValue) {
        if (root == null || path == null) {
            return defaultValue;
        }

        List<String> tokens = parsePath(path);
        if (tokens.isEmpty()) {
            return defaultValue;
        }

        Object current = root;
        for (String token : tokens) {
            if (current == null) {
                return defaultValue;
            }
            current = extractSegment(current, token);
        }

        if (current == null) {
            return defaultValue;
        }

        try {
            return (T) current;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    /**
     * Checks whether the path exists and is reachable in the root object.
     */
    public static boolean has(Object root, String path) {
        if (root == null || path == null) {
            return false;
        }

        List<String> tokens = parsePath(path);
        if (tokens.isEmpty()) {
            return false;
        }

        Object current = root;
        for (String token : tokens) {
            if (current == null) {
                return false;
            }
            if (!hasSegment(current, token)) {
                return false;
            }
            current = extractSegment(current, token);
        }
        return true;
    }

    /**
     * Sets a value at the specified path on a Map or nested structure, creating intermediate Maps/Lists as needed.
     *
     * @param root The root object (must be a mutable Map, or will throw IllegalArgumentException)
     * @param path The path string (e.g. "user.profile.age" or "items[1].name")
     * @param value The value to set
     * @return The root object
     */
    @SuppressWarnings("unchecked")
    public static <M extends Map<String, Object>> M set(M root, String path, Object value) {
        if (root == null) {
            throw new IllegalArgumentException("Root cannot be null for set operation");
        }
        List<String> tokens = parsePath(path);
        if (tokens.isEmpty()) {
            return root;
        }

        Object current = root;
        for (int i = 0; i < tokens.size() - 1; i++) {
            String token = tokens.get(i);
            String nextToken = tokens.get(i + 1);
            boolean nextIsIndex = isNumeric(nextToken);

            if (current instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) current;
                Object existing = map.get(token);
                if (existing == null) {
                    Object created = nextIsIndex ? new ArrayList<>() : new LinkedHashMap<String, Object>();
                    map.put(token, created);
                    current = created;
                } else {
                    current = existing;
                }
            } else if (current instanceof List) {
                List<Object> list = (List<Object>) current;
                int index = Integer.parseInt(token);
                ensureListCapacity(list, index);
                Object existing = list.get(index);
                if (existing == null) {
                    Object created = nextIsIndex ? new ArrayList<>() : new LinkedHashMap<String, Object>();
                    list.set(index, created);
                    current = created;
                } else {
                    current = existing;
                }
            } else {
                throw new IllegalStateException("Cannot navigate through non-container object of type: " + current.getClass());
            }
        }

        String lastToken = tokens.get(tokens.size() - 1);
        if (current instanceof Map) {
            ((Map<String, Object>) current).put(lastToken, value);
        } else if (current instanceof List) {
            List<Object> list = (List<Object>) current;
            int index = Integer.parseInt(lastToken);
            ensureListCapacity(list, index);
            list.set(index, value);
        } else {
            throw new IllegalStateException("Cannot set property on non-container object of type: " + current.getClass());
        }
        return root;
    }

    /**
     * Unsets (removes) a property at the specified path.
     */
    @SuppressWarnings("unchecked")
    public static boolean unset(Object root, String path) {
        if (root == null || path == null) {
            return false;
        }
        List<String> tokens = parsePath(path);
        if (tokens.isEmpty()) {
            return false;
        }

        Object current = root;
        for (int i = 0; i < tokens.size() - 1; i++) {
            if (current == null) {
                return false;
            }
            current = extractSegment(current, tokens.get(i));
        }

        if (current == null) {
            return false;
        }

        String lastToken = tokens.get(tokens.size() - 1);
        if (current instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) current;
            if (map.containsKey(lastToken)) {
                map.remove(lastToken);
                return true;
            }
        } else if (current instanceof List) {
            List<?> list = (List<?>) current;
            if (isNumeric(lastToken)) {
                int idx = Integer.parseInt(lastToken);
                if (idx >= 0 && idx < list.size()) {
                    list.remove(idx);
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasSegment(Object target, String token) {
        if (target == null) return false;

        if (target instanceof Map<?, ?> map) {
            return map.containsKey(token);
        }

        if (target instanceof List<?> list && isNumeric(token)) {
            int idx = Integer.parseInt(token);
            return idx >= 0 && idx < list.size();
        }

        if (target.getClass().isArray() && isNumeric(token)) {
            int idx = Integer.parseInt(token);
            return idx >= 0 && idx < Array.getLength(target);
        }

        if (target.getClass().isRecord()) {
            for (RecordComponent rc : target.getClass().getRecordComponents()) {
                if (rc.getName().equals(token)) return true;
            }
        }

        try {
            findGetterOrField(target.getClass(), token);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static Object extractSegment(Object target, String token) {
        if (target == null) return null;

        if (target instanceof Map<?, ?> map) {
            return map.get(token);
        }

        if (target instanceof List<?> list && isNumeric(token)) {
            int idx = Integer.parseInt(token);
            return (idx >= 0 && idx < list.size()) ? list.get(idx) : null;
        }

        if (target.getClass().isArray() && isNumeric(token)) {
            int idx = Integer.parseInt(token);
            int len = Array.getLength(target);
            return (idx >= 0 && idx < len) ? Array.get(target, idx) : null;
        }

        if (target.getClass().isRecord()) {
            try {
                for (RecordComponent rc : target.getClass().getRecordComponents()) {
                    if (rc.getName().equals(token)) {
                        return rc.getAccessor().invoke(target);
                    }
                }
            } catch (Exception ignored) {}
        }

        // POJO Reflection: check getter, boolean isGetter, or public/private field
        try {
            Method method = findGetter(target.getClass(), token);
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(target);
            }

            Field field = findField(target.getClass(), token);
            if (field != null) {
                field.setAccessible(true);
                return field.get(target);
            }
        } catch (Exception ignored) {}

        return null;
    }

    private static Method findGetter(Class<?> clazz, String propName) {
        String capitalized = Character.toUpperCase(propName.charAt(0)) + propName.substring(1);
        String getMethodName = "get" + capitalized;
        String isMethodName = "is" + capitalized;

        for (Method m : clazz.getMethods()) {
            if (m.getParameterCount() == 0) {
                if (m.getName().equals(getMethodName) || m.getName().equals(isMethodName) || m.getName().equals(propName)) {
                    return m;
                }
            }
        }
        return null;
    }

    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private static void findGetterOrField(Class<?> clazz, String name) throws NoSuchMethodException {
        if (findGetter(clazz, name) != null || findField(clazz, name) != null) {
            return;
        }
        throw new NoSuchMethodException(name);
    }

    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) return false;
        }
        return true;
    }

    private static void ensureListCapacity(List<Object> list, int index) {
        while (list.size() <= index) {
            list.add(null);
        }
    }
}
