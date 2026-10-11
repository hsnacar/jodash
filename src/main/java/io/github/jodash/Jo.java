package io.github.jodash;

import io.github.jodash.core.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.*;

/**
 * Jodash (Jo) - Pragmatic Lodash-inspired utility toolkit for modern Java (Java 17+).
 * <p>
 * Use static imports for concise, expressive syntax:
 * <pre>{@code
 * import static io.github.jodash.Jo.*;
 * }</pre>
 */
public final class Jo {

    private Jo() {}

    // ==========================================
    // OBJECT BUILDERS & SAFE PATH NAVIGATION
    // ==========================================

    /**
     * Creates a new fluent dynamic object builder.
     */
    public static FluentMap obj() {
        return new FluentMap();
    }

    /**
     * Creates a new fluent dynamic object builder with initial map.
     */
    public static FluentMap obj(Map<String, Object> initial) {
        return new FluentMap(initial);
    }

    /**
     * Creates a mutable LinkedHashMap from key-value pairs.
     * Example: dict("name", "Hasan", "age", 30)
     */
    public static Map<String, Object> dict(Object... keyValues) {
        return JoObjects.dict(keyValues);
    }

    /**
     * Safely reads a nested path from Maps, Lists, Arrays, Records, and POJOs.
     */
    public static <T> T get(Object root, String path) {
        return ObjectPath.get(root, path);
    }

    /**
     * Safely reads a nested path or returns defaultValue if not found or null.
     */
    public static <T> T get(Object root, String path, T defaultValue) {
        return ObjectPath.get(root, path, defaultValue);
    }

    /**
     * Checks if a path exists and is reachable.
     */
    public static boolean has(Object root, String path) {
        return ObjectPath.has(root, path);
    }

    /**
     * Sets a value at the specified path on a mutable Map, creating intermediate paths as needed.
     */
    public static <M extends Map<String, Object>> M set(M root, String path, Object value) {
        return ObjectPath.set(root, path, value);
    }

    /**
     * Removes a property at the specified path.
     */
    public static boolean unset(Object root, String path) {
        return ObjectPath.unset(root, path);
    }

    // ==========================================
    // OBJECT UTILITIES
    // ==========================================

    /**
     * Picks specified keys from a map.
     */
    public static <V> Map<String, V> pick(Map<String, V> source, String... keys) {
        return JoObjects.pick(source, keys);
    }

    /**
     * Picks entries matching the predicate.
     */
    public static <K, V> Map<K, V> pick(Map<K, V> source, BiPredicate<K, V> predicate) {
        return JoObjects.pick(source, predicate);
    }

    /**
     * Omits specified keys from a map.
     */
    public static <V> Map<String, V> omit(Map<String, V> source, String... keys) {
        return JoObjects.omit(source, keys);
    }

    /**
     * Omits entries matching the predicate.
     */
    public static <K, V> Map<K, V> omit(Map<K, V> source, BiPredicate<K, V> predicate) {
        return JoObjects.omit(source, predicate);
    }

    /**
     * Deep-merges source maps into target map.
     */
    @SafeVarargs
    public static Map<String, Object> merge(Map<String, Object> target, Map<String, Object>... sources) {
        return JoObjects.merge(target, sources);
    }

    /**
     * Fills in null/missing properties from defaults without overwriting existing non-null ones.
     */
    @SafeVarargs
    public static Map<String, Object> defaults(Map<String, Object> target, Map<String, Object>... sources) {
        return JoObjects.defaults(target, sources);
    }

    /**
     * Inverts a map (keys become values, values become keys).
     */
    public static <K, V> Map<V, K> invert(Map<K, V> map) {
        return JoObjects.invert(map);
    }

    /**
     * Performs structural deep clone of maps, lists, sets, and arrays.
     * Primitive wrappers, strings, and custom objects/POJOs are returned by reference.
     */
    public static <T> T cloneDeep(T obj) {
        return JoObjects.cloneDeep(obj);
    }

    /**
     * Deep equality check between two objects (handles nested collections, maps, arrays, sets).
     */
    public static boolean isEqual(Object a, Object b) {
        return JoObjects.isEqual(a, b);
    }

    /**
     * Checks if a value is empty (null, empty string with length == 0, empty collection, empty map, empty array, empty optional).
     * Whitespace-only strings ("   ") are not considered empty.
     */
    public static boolean isEmpty(Object value) {
        return JoObjects.isEmpty(value);
    }

    // ==========================================
    // COLLECTIONS & ARRAYS
    // ==========================================

    /**
     * Creates a mutable ArrayList from elements.
     */
    @SafeVarargs
    public static <T> List<T> list(T... elements) {
        return JoCollections.list(elements);
    }

    /**
     * Creates a mutable LinkedHashSet from elements.
     */
    @SafeVarargs
    public static <T> Set<T> set(T... elements) {
        return JoCollections.set(elements);
    }

    /**
     * Chunks a list into sublists of the given size.
     */
    public static <T> List<List<T>> chunk(List<T> list, int size) {
        return JoCollections.chunk(list, size);
    }

    /**
     * Groups elements by a key extractor function.
     */
    public static <T, K> Map<K, List<T>> groupBy(Iterable<T> items, Function<T, K> keyFn) {
        return JoCollections.groupBy(items, keyFn);
    }

    /**
     * Indexes elements into a map by a key extractor function.
     */
    public static <T, K> Map<K, T> keyBy(Iterable<T> items, Function<T, K> keyFn) {
        return JoCollections.keyBy(items, keyFn);
    }

    /**
     * Counts occurrences of keys produced by keyFn.
     */
    public static <T, K> Map<K, Long> countBy(Iterable<T> items, Function<T, K> keyFn) {
        return JoCollections.countBy(items, keyFn);
    }

    /**
     * Removes nulls, blank strings, and empty Optionals.
     */
    public static <T> List<T> compact(Iterable<T> items) {
        return JoCollections.compact(items);
    }

    /**
     * Returns a duplicate-free version of items preserving insertion order.
     */
    public static <T> List<T> uniq(Iterable<T> items) {
        return JoCollections.uniq(items);
    }

    /**
     * Returns a duplicate-free version of items based on keyFn.
     */
    public static <T, K> List<T> uniqBy(Iterable<T> items, Function<T, K> keyFn) {
        return JoCollections.uniqBy(items, keyFn);
    }

    /**
     * Flattens a collection one level deep.
     */
    public static <T> List<T> flatten(Iterable<? extends Iterable<T>> nested) {
        return JoCollections.flatten(nested);
    }

    /**
     * Recursively flattens deeply nested collections and arrays.
     */
    public static List<Object> flattenDeep(Iterable<?> nested) {
        return JoCollections.flattenDeep(nested);
    }

    /**
     * Partitions elements into matching and rejecting lists.
     */
    public static <T> JoCollections.Partition<T> partition(Iterable<T> items, Predicate<T> predicate) {
        return JoCollections.partition(items, predicate);
    }

    /**
     * Picks a random element from a list.
     */
    public static <T> T sample(List<T> list) {
        return JoCollections.sample(list);
    }

    /**
     * Picks n random elements from a list.
     */
    public static <T> List<T> sampleSize(List<T> list, int n) {
        return JoCollections.sampleSize(list, n);
    }

    /**
     * Returns a shuffled copy of the list.
     */
    public static <T> List<T> shuffle(List<T> list) {
        return JoCollections.shuffle(list);
    }

    /**
     * Computes difference (elements in a not in b).
     */
    public static <T> List<T> difference(Collection<T> a, Collection<T> b) {
        return JoCollections.difference(a, b);
    }

    /**
     * Computes intersection of two collections.
     */
    public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
        return JoCollections.intersection(a, b);
    }

    /**
     * Computes distinct union of collections.
     */
    @SafeVarargs
    public static <T> List<T> union(Collection<T>... collections) {
        return JoCollections.union(collections);
    }

    /**
     * Returns a sorted copy of list by key extractor.
     */
    public static <T, C extends Comparable<C>> List<T> sortBy(List<T> list, Function<T, C> keyFn) {
        return JoCollections.sortBy(list, keyFn);
    }

    /**
     * Finds maximum element based on keyFn.
     */
    public static <T, C extends Comparable<C>> Optional<T> maxBy(Iterable<T> items, Function<T, C> keyFn) {
        return JoCollections.maxBy(items, keyFn);
    }

    /**
     * Finds minimum element based on keyFn.
     */
    public static <T, C extends Comparable<C>> Optional<T> minBy(Iterable<T> items, Function<T, C> keyFn) {
        return JoCollections.minBy(items, keyFn);
    }

    /**
     * Computes sum based on numeric extractor.
     */
    public static <T> double sumBy(Iterable<T> items, ToDoubleFunction<T> valueFn) {
        return JoCollections.sumBy(items, valueFn);
    }

    /**
     * Computes average based on numeric extractor.
     */
    public static <T> double meanBy(Iterable<T> items, ToDoubleFunction<T> valueFn) {
        return JoCollections.meanBy(items, valueFn);
    }

    // ==========================================
    // STRINGS
    // ==========================================

    /**
     * Splits string into its individual words.
     */
    public static List<String> words(String string) {
        return Strings.words(string);
    }

    /**
     * Converts string to camelCase ("fooBar").
     */
    public static String camelCase(String string) {
        return Strings.camelCase(string);
    }

    /**
     * Converts string to kebab-case ("foo-bar").
     */
    public static String kebabCase(String string) {
        return Strings.kebabCase(string);
    }

    /**
     * Converts string to snake_case ("foo_bar").
     */
    public static String snakeCase(String string) {
        return Strings.snakeCase(string);
    }

    /**
     * Converts string to PascalCase ("FooBar").
     */
    public static String pascalCase(String string) {
        return Strings.pascalCase(string);
    }

    /**
     * Capitalizes first character of string.
     */
    public static String capitalize(String string) {
        return Strings.capitalize(string);
    }

    /**
     * Uncapitalizes first character of string.
     */
    public static String uncapitalize(String string) {
        return Strings.uncapitalize(string);
    }

    /**
     * Truncates string to length with "...".
     */
    public static String truncate(String string, int length) {
        return Strings.truncate(string, length);
    }

    /**
     * Truncates string with custom omission.
     */
    public static String truncate(String string, int length, String omission) {
        return Strings.truncate(string, length, omission);
    }

    /**
     * Pads string on left and right sides.
     */
    public static String pad(String string, int length, String chars) {
        return Strings.pad(string, length, chars);
    }

    /**
     * Pads string on left side.
     */
    public static String padStart(String string, int length, String chars) {
        return Strings.padStart(string, length, chars);
    }

    /**
     * Pads string on right side.
     */
    public static String padEnd(String string, int length, String chars) {
        return Strings.padEnd(string, length, chars);
    }

    /**
     * Repeats string n times.
     */
    public static String repeat(String string, int n) {
        return Strings.repeat(string, n);
    }

    /**
     * Escapes HTML characters.
     */
    public static String escape(String string) {
        return Strings.escape(string);
    }

    /**
     * Unescapes HTML characters.
     */
    public static String unescape(String string) {
        return Strings.unescape(string);
    }

    // ==========================================
    // TYPE COERCION & CASTING
    // ==========================================

    /**
     * Safely converts value to int, or default if null/invalid.
     */
    public static int asInt(Object value, int defaultValue) {
        return Coerce.asInt(value, defaultValue);
    }

    /**
     * Safely converts value to long, or default if null/invalid.
     */
    public static long asLong(Object value, long defaultValue) {
        return Coerce.asLong(value, defaultValue);
    }

    /**
     * Safely converts value to double, or default if null/invalid.
     */
    public static double asDouble(Object value, double defaultValue) {
        return Coerce.asDouble(value, defaultValue);
    }

    /**
     * Safely converts value to boolean.
     */
    public static boolean asBoolean(Object value, boolean defaultValue) {
        return Coerce.asBoolean(value, defaultValue);
    }

    /**
     * Safely converts value to String, or default if null.
     */
    public static String asString(Object value, String defaultValue) {
        return Coerce.asString(value, defaultValue);
    }

    /**
     * Safely converts/wraps value into a List.
     */
    public static List<Object> asList(Object value) {
        return Coerce.asList(value);
    }

    /**
     * Safely casts to Map<String, Object> or empty Map.
     */
    public static Map<String, Object> asMap(Object value) {
        return Coerce.asMap(value);
    }

    // ==========================================
    // CONTROL FLOW & FUNCTIONS
    // ==========================================

    /**
     * Creates a function that memoizes results based on input.
     */
    public static <T, R> Function<T, R> memoize(Function<T, R> fn) {
        return Functions.memoize(fn);
    }

    /**
     * Creates a supplier that lazily caches its result.
     */
    public static <T> Supplier<T> memoize(Supplier<T> supplier) {
        return Functions.memoize(supplier);
    }

    /**
     * Creates a Runnable that executes at most once.
     */
    public static Runnable once(Runnable runnable) {
        return Functions.once(runnable);
    }

    /**
     * Creates a Supplier that computes at most once and caches.
     */
    public static <T> Supplier<T> once(Supplier<T> supplier) {
        return Functions.once(supplier);
    }

    /**
     * Retries task up to attempts times with a delay.
     */
    public static <T> T retry(int attempts, Duration delay, Callable<T> task) throws Exception {
        return Functions.retry(attempts, delay, task);
    }

    // ==========================================
    // ZERO-DEPENDENCY JSON SERIALIZATION
    // ==========================================

    /**
     * Serializes any Map, Collection, Array, Record, or Object to a compact JSON string without Jackson or Gson.
     */
    public static String toJson(Object value) {
        return Json.stringify(value);
    }

    /**
     * Serializes any Map, Collection, Array, Record, or Object to a pretty-printed JSON string without external dependencies.
     */
    public static String toPrettyJson(Object value) {
        return Json.pretty(value);
    }
}
