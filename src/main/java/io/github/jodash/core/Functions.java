package io.github.jodash.core;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Functional utilities inspired by Lodash (memoize, once, retry).
 */
public final class Functions {

    private Functions() {}

    /**
     * Creates a function that memoizes the result of fn using input as key.
     */
    public static <T, R> Function<T, R> memoize(Function<T, R> fn) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return key -> cache.computeIfAbsent(key, fn);
    }

    /**
     * Creates a supplier that caches its result on first call.
     */
    public static <T> Supplier<T> memoize(Supplier<T> supplier) {
        return new Supplier<>() {
            private volatile boolean computed = false;
            private T value;

            @Override
            public T get() {
                if (!computed) {
                    synchronized (this) {
                        if (!computed) {
                            value = supplier.get();
                            computed = true;
                        }
                    }
                }
                return value;
            }
        };
    }

    /**
     * Creates a Runnable that is restricted to invoking the original runnable only once.
     */
    public static Runnable once(Runnable runnable) {
        AtomicBoolean ran = new AtomicBoolean(false);
        return () -> {
            if (ran.compareAndSet(false, true)) {
                runnable.run();
            }
        };
    }

    /**
     * Creates a Supplier that is restricted to invoking the original supplier only once, returning the cached result thereafter.
     */
    public static <T> Supplier<T> once(Supplier<T> supplier) {
        return memoize(supplier);
    }

    /**
     * Retries a callable up to attempts times with a fixed delay between failures.
     */
    public static <T> T retry(int attempts, Duration delay, Callable<T> callable) throws Exception {
        Exception lastException = null;
        for (int i = 1; i <= attempts; i++) {
            try {
                return callable.call();
            } catch (Exception e) {
                lastException = e;
                if (i < attempts && delay != null && !delay.isZero() && !delay.isNegative()) {
                    try {
                        Thread.sleep(delay.toMillis());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw ie;
                    }
                }
            }
        }
        throw lastException != null ? lastException : new IllegalStateException("Retry failed");
    }
}
