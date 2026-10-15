package io.github.jodash.core;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

/**
 * Collection utilities inspired by Lodash (chunk, groupBy, keyBy, compact, flatten, uniq, partition, sample, shuffle, etc.).
 */
public final class JoCollections {

    private JoCollections() {}

    /**
     * Partition record containing matched and rejected elements.
     */
    public record Partition<T>(List<T> matches, List<T> rejects) {}

    /**
     * Creates a mutable ArrayList from elements.
     */
    @SafeVarargs
    public static <T> List<T> list(T... elements) {
        if (elements == null) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(elements));
    }

    /**
     * Creates a mutable LinkedHashSet from elements.
     */
    @SafeVarargs
    public static <T> Set<T> set(T... elements) {
        if (elements == null) return new LinkedHashSet<>();
        return new LinkedHashSet<>(Arrays.asList(elements));
    }

    /**
     * Splits a list into chunks of the given size.
     */
    public static <T> List<List<T>> chunk(List<T> list, int size) {
        if (list == null || list.isEmpty() || size <= 0) {
            return new ArrayList<>();
        }
        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            chunks.add(new ArrayList<>(list.subList(i, Math.min(list.size(), i + size))));
        }
        return chunks;
    }

    /**
     * Groups elements of an iterable by a key extracted via keyFn.
     */
    public static <T, K> Map<K, List<T>> groupBy(Iterable<T> items, Function<T, K> keyFn) {
        Map<K, List<T>> result = new LinkedHashMap<>();
        if (items == null || keyFn == null) return result;

        for (T item : items) {
            K key = keyFn.apply(item);
            result.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
        }
        return result;
    }

    /**
     * Creates a Map composed of keys generated from the results of running each element through keyFn.
     * Later items with duplicate keys overwrite earlier ones (like Lodash keyBy).
     */
    public static <T, K> Map<K, T> keyBy(Iterable<T> items, Function<T, K> keyFn) {
        Map<K, T> result = new LinkedHashMap<>();
        if (items == null || keyFn == null) return result;

        for (T item : items) {
            K key = keyFn.apply(item);
            result.put(key, item);
        }
        return result;
    }

    /**
     * Counts occurrences of each key produced by keyFn.
     */
    public static <T, K> Map<K, Long> countBy(Iterable<T> items, Function<T, K> keyFn) {
        Map<K, Long> result = new LinkedHashMap<>();
        if (items == null || keyFn == null) return result;

        for (T item : items) {
            K key = keyFn.apply(item);
            result.merge(key, 1L, Long::sum);
        }
        return result;
    }

    /**
     * Creates a list with all falsey/empty values removed (null, blank strings, empty Optionals).
     */
    public static <T> List<T> compact(Iterable<T> items) {
        List<T> result = new ArrayList<>();
        if (items == null) return result;

        for (T item : items) {
            if (item != null) {
                if (item instanceof String s && s.trim().isEmpty()) {
                    continue;
                }
                if (item instanceof Optional<?> opt && opt.isEmpty()) {
                    continue;
                }
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Creates a duplicate-free version of an iterable, preserving original insertion order.
     */
    public static <T> List<T> uniq(Iterable<T> items) {
        if (items == null) return new ArrayList<>();
        Set<T> seen = new LinkedHashSet<>();
        for (T item : items) {
            seen.add(item);
        }
        return new ArrayList<>(seen);
    }

    /**
     * Creates a duplicate-free version of an iterable based on a key function.
     */
    public static <T, K> List<T> uniqBy(Iterable<T> items, Function<T, K> keyFn) {
        List<T> result = new ArrayList<>();
        if (items == null || keyFn == null) return result;

        Set<K> seenKeys = new HashSet<>();
        for (T item : items) {
            K key = keyFn.apply(item);
            if (seenKeys.add(key)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Flattens an iterable a single level deep.
     */
    public static <T> List<T> flatten(Iterable<? extends Iterable<T>> nested) {
        List<T> result = new ArrayList<>();
        if (nested == null) return result;
        for (Iterable<T> inner : nested) {
            if (inner != null) {
                for (T item : inner) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    /**
     * Recursively flattens an iterable or array completely.
     */
    public static List<Object> flattenDeep(Iterable<?> nested) {
        List<Object> result = new ArrayList<>();
        flattenDeepRecursive(nested, result);
        return result;
    }

    private static void flattenDeepRecursive(Object item, List<Object> target) {
        if (item == null) return;
        if (item instanceof Iterable<?> iter) {
            for (Object sub : iter) {
                flattenDeepRecursive(sub, target);
            }
        } else if (item.getClass().isArray()) {
            int length = Array.getLength(item);
            for (int i = 0; i < length; i++) {
                flattenDeepRecursive(Array.get(item, i), target);
            }
        } else {
            target.add(item);
        }
    }

    /**
     * Splits an iterable into two lists: one whose elements satisfy the predicate, and one whose elements do not.
     */
    public static <T> Partition<T> partition(Iterable<T> items, Predicate<T> predicate) {
        List<T> matches = new ArrayList<>();
        List<T> rejects = new ArrayList<>();
        if (items == null || predicate == null) {
            return new Partition<>(matches, rejects);
        }
        for (T item : items) {
            if (predicate.test(item)) {
                matches.add(item);
            } else {
                rejects.add(item);
            }
        }
        return new Partition<>(matches, rejects);
    }

    /**
     * Gets a random element from a list. Returns null if list is empty or null.
     */
    public static <T> T sample(List<T> list) {
        if (list == null || list.isEmpty()) return null;
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    /**
     * Gets n random elements at unique keys from a list.
     */
    public static <T> List<T> sampleSize(List<T> list, int n) {
        if (list == null || list.isEmpty() || n <= 0) return new ArrayList<>();
        List<T> copy = new ArrayList<>(list);
        java.util.Collections.shuffle(copy, ThreadLocalRandom.current());
        return copy.subList(0, Math.min(n, copy.size()));
    }

    /**
     * Creates a new shuffled copy of a list using Fisher-Yates algorithm.
     */
    public static <T> List<T> shuffle(List<T> list) {
        if (list == null) return new ArrayList<>();
        List<T> copy = new ArrayList<>(list);
        java.util.Collections.shuffle(copy, ThreadLocalRandom.current());
        return copy;
    }

    /**
     * Creates an array of values from collection a not included in collection b.
     */
    public static <T> List<T> difference(Collection<T> a, Collection<T> b) {
        if (a == null) return new ArrayList<>();
        if (b == null) return new ArrayList<>(a);
        Set<T> exclude = new HashSet<>(b);
        List<T> result = new ArrayList<>();
        for (T item : a) {
            if (!exclude.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Creates an array of unique values that are included in all given collections.
     */
    public static <T> List<T> intersection(Collection<T> a, Collection<T> b) {
        if (a == null || b == null) return new ArrayList<>();
        Set<T> setB = new LinkedHashSet<>(b);
        Set<T> seen = new LinkedHashSet<>();
        for (T item : a) {
            if (setB.contains(item)) {
                seen.add(item);
            }
        }
        return new ArrayList<>(seen);
    }

    /**
     * Creates an array of unique values, in order, from all given collections.
     */
    @SafeVarargs
    public static <T> List<T> union(Collection<T>... collections) {
        Set<T> result = new LinkedHashSet<>();
        if (collections == null) return new ArrayList<>();
        for (Collection<T> col : collections) {
            if (col != null) {
                result.addAll(col);
            }
        }
        return new ArrayList<>(result);
    }

    /**
     * Sorts a copy of the list by a key extractor in natural order.
     */
    @SuppressWarnings("unchecked")
    public static <T, C extends Comparable<C>> List<T> sortBy(List<T> list, Function<T, C> keyFn) {
        if (list == null) return new ArrayList<>();
        List<T> copy = new ArrayList<>(list);
        copy.sort(Comparator.comparing(keyFn, Comparator.nullsLast(Comparator.naturalOrder())));
        return copy;
    }

    /**
     * Computes the maximum value of a collection according to a key extractor.
     */
    public static <T, C extends Comparable<C>> Optional<T> maxBy(Iterable<T> items, Function<T, C> keyFn) {
        if (items == null || keyFn == null) return Optional.empty();
        T maxItem = null;
        C maxVal = null;
        for (T item : items) {
            C val = keyFn.apply(item);
            if (val != null) {
                if (maxVal == null || val.compareTo(maxVal) > 0) {
                    maxVal = val;
                    maxItem = item;
                }
            }
        }
        return Optional.ofNullable(maxItem);
    }

    /**
     * Computes the minimum value of a collection according to a key extractor.
     */
    public static <T, C extends Comparable<C>> Optional<T> minBy(Iterable<T> items, Function<T, C> keyFn) {
        if (items == null || keyFn == null) return Optional.empty();
        T minItem = null;
        C minVal = null;
        for (T item : items) {
            C val = keyFn.apply(item);
            if (val != null) {
                if (minVal == null || val.compareTo(minVal) < 0) {
                    minVal = val;
                    minItem = item;
                }
            }
        }
        return Optional.ofNullable(minItem);
    }

    /**
     * Computes the sum of values produced by valueFn.
     */
    public static <T> double sumBy(Iterable<T> items, ToDoubleFunction<T> valueFn) {
        if (items == null || valueFn == null) return 0.0;
        double sum = 0.0;
        for (T item : items) {
            sum += valueFn.applyAsDouble(item);
        }
        return sum;
    }

    /**
     * Computes the mean (average) of values produced by valueFn.
     */
    public static <T> double meanBy(Iterable<T> items, ToDoubleFunction<T> valueFn) {
        if (items == null || valueFn == null) return 0.0;
        double sum = 0.0;
        int count = 0;
        for (T item : items) {
            sum += valueFn.applyAsDouble(item);
            count++;
        }
        return count == 0 ? 0.0 : sum / count;
    }
}
