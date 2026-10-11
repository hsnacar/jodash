package io.github.jodash.core;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BiPredicate;

/**
 * Object manipulation utilities inspired by Lodash (pick, omit, merge, cloneDeep, isEqual, isEmpty, defaults, dict).
 */
public final class JoObjects {

    private JoObjects() {}

    /**
     * Creates a mutable LinkedHashMap from alternating key-value pairs.
     * Example: dict("name", "Hasan", "age", 30)
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> dict(Object... keyValues) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (keyValues == null || keyValues.length == 0) {
            return map;
        }
        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("dict requires an even number of arguments (key-value pairs)");
        }
        for (int i = 0; i < keyValues.length; i += 2) {
            String key = String.valueOf(keyValues[i]);
            Object value = keyValues[i + 1];
            map.put(key, value);
        }
        return map;
    }

    /**
     * Creates a new Map picking only the specified keys from the source map.
     */
    public static <V> Map<String, V> pick(Map<String, V> source, String... keys) {
        return pick(source, Arrays.asList(keys));
    }

    /**
     * Creates a new Map picking only the specified keys from the source map.
     */
    public static <V> Map<String, V> pick(Map<String, V> source, Collection<String> keys) {
        if (source == null) return new LinkedHashMap<>();
        Set<String> keySet = new HashSet<>(keys);
        Map<String, V> result = new LinkedHashMap<>();
        for (Map.Entry<String, V> entry : source.entrySet()) {
            if (keySet.contains(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * Creates a new Map picking entries matching the predicate.
     */
    public static <K, V> Map<K, V> pick(Map<K, V> source, BiPredicate<K, V> predicate) {
        if (source == null) return new LinkedHashMap<>();
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : source.entrySet()) {
            if (predicate.test(entry.getKey(), entry.getValue())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * Creates a new Map omitting the specified keys from the source map.
     */
    public static <V> Map<String, V> omit(Map<String, V> source, String... keys) {
        return omit(source, Arrays.asList(keys));
    }

    /**
     * Creates a new Map omitting the specified keys from the source map.
     */
    public static <V> Map<String, V> omit(Map<String, V> source, Collection<String> keys) {
        if (source == null) return new LinkedHashMap<>();
        Set<String> keySet = new HashSet<>(keys);
        Map<String, V> result = new LinkedHashMap<>();
        for (Map.Entry<String, V> entry : source.entrySet()) {
            if (!keySet.contains(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * Creates a new Map omitting entries matching the predicate.
     */
    public static <K, V> Map<K, V> omit(Map<K, V> source, BiPredicate<K, V> predicate) {
        if (source == null) return new LinkedHashMap<>();
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : source.entrySet()) {
            if (!predicate.test(entry.getKey(), entry.getValue())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    /**
     * Recursively deep-merges source maps into target map.
     * Nested maps are merged recursively; other values in source overwrite target values.
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static Map<String, Object> merge(Map<String, Object> target, Map<String, Object>... sources) {
        if (target == null) {
            target = new LinkedHashMap<>();
        }
        if (sources == null) return target;

        for (Map<String, Object> source : sources) {
            if (source == null) continue;
            for (Map.Entry<String, Object> entry : source.entrySet()) {
                String key = entry.getKey();
                Object sourceVal = entry.getValue();
                Object targetVal = target.get(key);

                if (targetVal instanceof Map && sourceVal instanceof Map) {
                    merge((Map<String, Object>) targetVal, (Map<String, Object>) sourceVal);
                } else {
                    target.put(key, cloneDeep(sourceVal));
                }
            }
        }
        return target;
    }

    /**
     * Fills in undefined or null properties in the target map from the default sources.
     * Unlike merge, defaults does not override existing non-null properties.
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static Map<String, Object> defaults(Map<String, Object> target, Map<String, Object>... sources) {
        if (target == null) {
            target = new LinkedHashMap<>();
        }
        if (sources == null) return target;

        for (Map<String, Object> source : sources) {
            if (source == null) continue;
            for (Map.Entry<String, Object> entry : source.entrySet()) {
                String key = entry.getKey();
                Object sourceVal = entry.getValue();
                Object targetVal = target.get(key);

                if (targetVal == null) {
                    target.put(key, cloneDeep(sourceVal));
                } else if (targetVal instanceof Map && sourceVal instanceof Map) {
                    defaults((Map<String, Object>) targetVal, (Map<String, Object>) sourceVal);
                }
            }
        }
        return target;
    }

    /**
     * Inverts a map (keys become values, values become keys).
     */
    public static <K, V> Map<V, K> invert(Map<K, V> map) {
        if (map == null) return new LinkedHashMap<>();
        Map<V, K> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            result.put(entry.getValue(), entry.getKey());
        }
        return result;
    }

    /**
     * Checks if a value is empty (null, empty string with length == 0, empty collection, empty map, empty array, or empty optional).
     * Note: Whitespace-only strings ("   ") are not considered empty (consistent with Lodash).
     */
    public static boolean isEmpty(Object value) {
        if (value == null) return true;
        if (value instanceof CharSequence cs) return cs.isEmpty();
        if (value instanceof Collection<?> c) return c.isEmpty();
        if (value instanceof Map<?, ?> m) return m.isEmpty();
        if (value instanceof Optional<?> opt) return opt.isEmpty();
        if (value.getClass().isArray()) return Array.getLength(value) == 0;
        return false;
    }

    /**
     * Performs a deep clone of maps, lists, sets, and arrays.
     * Primitive wrappers, strings, and custom objects/POJOs are returned by reference.
     */
    @SuppressWarnings("unchecked")
    public static <T> T cloneDeep(T obj) {
        if (obj == null) return null;

        if (obj instanceof Map<?, ?> map) {
            Map<Object, Object> copy = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                copy.put(cloneDeep(entry.getKey()), cloneDeep(entry.getValue()));
            }
            return (T) copy;
        }

        if (obj instanceof List<?> list) {
            List<Object> copy = new ArrayList<>(list.size());
            for (Object item : list) {
                copy.add(cloneDeep(item));
            }
            return (T) copy;
        }

        if (obj instanceof Set<?> set) {
            Set<Object> copy = new LinkedHashSet<>(set.size());
            for (Object item : set) {
                copy.add(cloneDeep(item));
            }
            return (T) copy;
        }

        if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            Object copy = Array.newInstance(obj.getClass().getComponentType(), length);
            for (int i = 0; i < length; i++) {
                Array.set(copy, i, cloneDeep(Array.get(obj, i)));
            }
            return (T) copy;
        }

        // Immutable primitives, strings, records, etc.
        return obj;
    }

    /**
     * Deep equality check between two objects (handles nested collections, maps, arrays).
     */
    public static boolean isEqual(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;

        if (a.getClass().isArray() && b.getClass().isArray()) {
            int lenA = Array.getLength(a);
            int lenB = Array.getLength(b);
            if (lenA != lenB) return false;
            for (int i = 0; i < lenA; i++) {
                if (!isEqual(Array.get(a, i), Array.get(b, i))) return false;
            }
            return true;
        }

        if (a instanceof Map<?, ?> mapA && b instanceof Map<?, ?> mapB) {
            if (mapA.size() != mapB.size()) return false;
            for (Map.Entry<?, ?> entry : mapA.entrySet()) {
                if (!mapB.containsKey(entry.getKey())) return false;
                if (!isEqual(entry.getValue(), mapB.get(entry.getKey()))) return false;
            }
            return true;
        }

        if (a instanceof List<?> listA && b instanceof List<?> listB) {
            if (listA.size() != listB.size()) return false;
            for (int i = 0; i < listA.size(); i++) {
                if (!isEqual(listA.get(i), listB.get(i))) return false;
            }
            return true;
        }

        if (a instanceof Set<?> setA && b instanceof Set<?> setB) {
            if (setA.size() != setB.size()) return false;
            for (Object itemA : setA) {
                boolean matchFound = false;
                for (Object itemB : setB) {
                    if (isEqual(itemA, itemB)) {
                        matchFound = true;
                        break;
                    }
                }
                if (!matchFound) return false;
            }
            return true;
        }

        return java.util.Objects.equals(a, b);
    }
}
