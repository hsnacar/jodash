package sample_8_resilient_control_flow;

import static io.github.jodash.Jo.*;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * SAMPLE 8: Resilient Control Flow (Retry & Memoize)
 * Jodash Implementation
 */
public class Jodash {

    public static void main(String[] args) throws Exception {
        // 1. Retry unreliable call in 1 line!
        AtomicInteger attempts = new AtomicInteger(0);
        String result = retry(3, Duration.ofMillis(50), () -> {
            if (attempts.incrementAndGet() < 2) {
                throw new RuntimeException("Network timeout");
            }
            return "SUCCESS_DATA";
        });
        System.out.println("Result: " + result + " after " + attempts.get() + " attempts");

        // 2. Thread-safe & null-safe memoize in 1 line!
        AtomicInteger computeCount = new AtomicInteger(0);
        Function<Integer, Integer> fib = memoize(n -> {
            computeCount.incrementAndGet();
            return n * 10;
        });

        System.out.println("Call 1: " + fib.apply(5));
        System.out.println("Call 2 (cached): " + fib.apply(5));
        System.out.println("Computations: " + computeCount.get());
    }
}
