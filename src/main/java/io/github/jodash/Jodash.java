package io.github.jodash;

/**
 * Synonym alias for {@link Jo}.
 * Allows using {@code Jodash.chunk(...)} or {@code import static io.github.jodash.Jodash.*;}.
 */
public final class Jodash {

    private Jodash() {}

    // Inherits/delegates to Jo for maximum flexibility
    public static FluentMap obj() { return Jo.obj(); }
    public static FluentMap obj(java.util.Map<String, Object> initial) { return Jo.obj(initial); }
    public static java.util.Map<String, Object> dict(Object... keyValues) { return Jo.dict(keyValues); }

    public static <T> T get(Object root, String path) { return Jo.get(root, path); }
    public static <T> T get(Object root, String path, T defaultValue) { return Jo.get(root, path, defaultValue); }
    public static boolean has(Object root, String path) { return Jo.has(root, path); }
    public static <M extends java.util.Map<String, Object>> M set(M root, String path, Object value) { return Jo.set(root, path, value); }
    public static boolean unset(Object root, String path) { return Jo.unset(root, path); }

    public static <V> java.util.Map<String, V> pick(java.util.Map<String, V> source, String... keys) { return Jo.pick(source, keys); }
    public static <V> java.util.Map<String, V> omit(java.util.Map<String, V> source, String... keys) { return Jo.omit(source, keys); }

    @SafeVarargs
    public static java.util.Map<String, Object> merge(java.util.Map<String, Object> target, java.util.Map<String, Object>... sources) { return Jo.merge(target, sources); }
    @SafeVarargs
    public static java.util.Map<String, Object> defaults(java.util.Map<String, Object> target, java.util.Map<String, Object>... sources) { return Jo.defaults(target, sources); }

    public static <T> T cloneDeep(T obj) { return Jo.cloneDeep(obj); }
    public static boolean isEqual(Object a, Object b) { return Jo.isEqual(a, b); }
    public static boolean isEmpty(Object value) { return Jo.isEmpty(value); }

    @SafeVarargs
    public static <T> java.util.List<T> list(T... elements) { return Jo.list(elements); }
    @SafeVarargs
    public static <T> java.util.Set<T> set(T... elements) { return Jo.set(elements); }
    public static <T> java.util.List<java.util.List<T>> chunk(java.util.List<T> list, int size) { return Jo.chunk(list, size); }
    public static <T, K> java.util.Map<K, java.util.List<T>> groupBy(Iterable<T> items, java.util.function.Function<T, K> keyFn) { return Jo.groupBy(items, keyFn); }
    public static <T, K> java.util.Map<K, T> keyBy(Iterable<T> items, java.util.function.Function<T, K> keyFn) { return Jo.keyBy(items, keyFn); }
    public static <T> java.util.List<T> compact(Iterable<T> items) { return Jo.compact(items); }
    public static <T> java.util.List<T> uniq(Iterable<T> items) { return Jo.uniq(items); }
    public static <T, K> java.util.List<T> uniqBy(Iterable<T> items, java.util.function.Function<T, K> keyFn) { return Jo.uniqBy(items, keyFn); }
    public static <T> java.util.List<T> flatten(Iterable<? extends Iterable<T>> nested) { return Jo.flatten(nested); }
    public static java.util.List<Object> flattenDeep(Iterable<?> nested) { return Jo.flattenDeep(nested); }
    public static <T> io.github.jodash.core.JoCollections.Partition<T> partition(Iterable<T> items, java.util.function.Predicate<T> predicate) { return Jo.partition(items, predicate); }
    public static <T> T sample(java.util.List<T> list) { return Jo.sample(list); }
    public static <T> java.util.List<T> sampleSize(java.util.List<T> list, int n) { return Jo.sampleSize(list, n); }
    public static <T> java.util.List<T> shuffle(java.util.List<T> list) { return Jo.shuffle(list); }
    public static <T> java.util.List<T> difference(java.util.Collection<T> a, java.util.Collection<T> b) { return Jo.difference(a, b); }
    public static <T> java.util.List<T> intersection(java.util.Collection<T> a, java.util.Collection<T> b) { return Jo.intersection(a, b); }
    @SafeVarargs
    public static <T> java.util.List<T> union(java.util.Collection<T>... collections) { return Jo.union(collections); }
    public static <T, C extends Comparable<C>> java.util.List<T> sortBy(java.util.List<T> list, java.util.function.Function<T, C> keyFn) { return Jo.sortBy(list, keyFn); }

    public static String camelCase(String string) { return Jo.camelCase(string); }
    public static String kebabCase(String string) { return Jo.kebabCase(string); }
    public static String snakeCase(String string) { return Jo.snakeCase(string); }
    public static String pascalCase(String string) { return Jo.pascalCase(string); }
    public static String capitalize(String string) { return Jo.capitalize(string); }
    public static String truncate(String string, int length) { return Jo.truncate(string, length); }
    public static String pad(String string, int length, String chars) { return Jo.pad(string, length, chars); }
    public static String escape(String string) { return Jo.escape(string); }
    public static String unescape(String string) { return Jo.unescape(string); }

    public static int asInt(Object value, int defaultValue) { return Jo.asInt(value, defaultValue); }
    public static long asLong(Object value, long defaultValue) { return Jo.asLong(value, defaultValue); }
    public static double asDouble(Object value, double defaultValue) { return Jo.asDouble(value, defaultValue); }
    public static boolean asBoolean(Object value, boolean defaultValue) { return Jo.asBoolean(value, defaultValue); }
    public static String asString(Object value, String defaultValue) { return Jo.asString(value, defaultValue); }
    public static java.util.List<Object> asList(Object value) { return Jo.asList(value); }
    public static java.util.Map<String, Object> asMap(Object value) { return Jo.asMap(value); }

    public static String toJson(Object value) { return Jo.toJson(value); }
    public static String toPrettyJson(Object value) { return Jo.toPrettyJson(value); }
}
