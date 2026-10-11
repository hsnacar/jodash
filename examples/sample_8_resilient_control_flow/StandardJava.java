package sample_8_resilient_control_flow;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * SAMPLE 8: Resilient Control Flow (Retry & Memoize)
 * Standard Java (Vanilla) Implementation
 */
public class StandardJava {

    // Helper method required for retry with backoff
    private static <T> T retryTask(int attempts, long delayMs, Callable<T> task) throws Exception {
        Exception lastException = null;
        for (int i = 1; i <= attempts; i++) {
            try {
                return task.call();
            } catch (Exception e) {
                lastException = e;
                if (i < attempts && delayMs > 0) {
                    Thread.sleep(delayMs);
                }
            }
        }
        throw lastException != null ? lastException : new IllegalStateException("Task failed");
    }

    // Helper method required for thread-safe memoization
    private static <T, R> Function<T, R> memoizeFunction(Function<T, R> fn) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return key -> cache.computeIfAbsent(key, fn);
    }

    public static void main(String[] args) throws Exception {
        // 1. Retry unreliable call
        AtomicInteger attempts = new AtomicInteger(0);
        String result = retryTask(3, 50, () -> {
            if (attempts.incrementAndGet() < 2) {
                throw new RuntimeException("Network timeout");
            }
            return "SUCCESS_DATA";
        });
        System.out.println("Result: " + result + " after " + attempts.get() + " attempts");

        // 2. Thread-safe memoize
        AtomicInteger computeCount = new AtomicInteger(0);
        Function<Integer, Integer> fib = memoizeFunction(n -> {
            computeCount.incrementAndGet();
            return n * 10;
        });

        System.out.println("Call 1: " + fib.apply(5));
        System.out.println("Call 2 (cached): " + fib.apply(5));
        System.out.println("Computations: " + computeCount.get());
    }
}
