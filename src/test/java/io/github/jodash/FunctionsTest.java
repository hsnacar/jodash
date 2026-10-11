package io.github.jodash;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FunctionsTest {

    @Test
    @DisplayName("memoize function caches results for identical inputs")
    void testMemoizeFunction() {
        AtomicInteger calls = new AtomicInteger(0);
        Function<Integer, Integer> square = Jo.memoize(n -> {
            calls.incrementAndGet();
            return n * n;
        });

        assertThat(square.apply(4)).isEqualTo(16);
        assertThat(square.apply(4)).isEqualTo(16);
        assertThat(square.apply(5)).isEqualTo(25);

        assertThat(calls.get()).isEqualTo(2);
    }

    @Test
    @DisplayName("memoize supplier evaluates only once")
    void testMemoizeSupplier() {
        AtomicInteger calls = new AtomicInteger(0);
        Supplier<String> supplier = Jo.memoize(() -> {
            calls.incrementAndGet();
            return "cached-value";
        });

        assertThat(supplier.get()).isEqualTo("cached-value");
        assertThat(supplier.get()).isEqualTo("cached-value");
        assertThat(calls.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("once executes runnable exactly once")
    void testOnce() {
        AtomicInteger count = new AtomicInteger(0);
        Runnable runner = Jo.once(() -> { count.incrementAndGet(); });

        runner.run();
        runner.run();
        runner.run();

        assertThat(count.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("retry retries until success or max attempts")
    void testRetry() throws Exception {
        AtomicInteger attempts = new AtomicInteger(0);

        String result = Jo.retry(3, Duration.ofMillis(10), () -> {
            if (attempts.incrementAndGet() < 2) {
                throw new RuntimeException("Temporary failure");
            }
            return "success";
        });

        assertThat(result).isEqualTo("success");
        assertThat(attempts.get()).isEqualTo(2);

        // Fail test
        assertThatThrownBy(() -> Jo.retry(2, Duration.ofMillis(5), () -> {
            throw new IllegalStateException("Always fails");
        })).isInstanceOf(IllegalStateException.class);

        // Immediate retry with zero delay
        AtomicInteger immediateCalls = new AtomicInteger(0);
        String immediateResult = Jo.retry(1, Duration.ZERO, () -> {
            immediateCalls.incrementAndGet();
            return "immediate";
        });
        assertThat(immediateResult).isEqualTo("immediate");
        assertThat(immediateCalls.get()).isEqualTo(1);
    }

    @Test
    @DisplayName("once supplier evaluates only once and returns cached value")
    void testOnceSupplier() {
        AtomicInteger calls = new AtomicInteger(0);
        Supplier<Integer> onceSupplier = Jo.once(() -> calls.incrementAndGet());

        assertThat(onceSupplier.get()).isEqualTo(1);
        assertThat(onceSupplier.get()).isEqualTo(1);
        assertThat(onceSupplier.get()).isEqualTo(1);
        assertThat(calls.get()).isEqualTo(1);
    }
}
