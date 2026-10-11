package sample_5_deep_merge_defaults;

import static io.github.jodash.Jo.*;
import java.util.Map;

/**
 * SAMPLE 5: Deep Configuration Merging, Defaults, Pick & Omit
 * Jodash Implementation
 */
public class Jodash {

    public static void main(String[] args) {
        Map<String, Object> defaultConfig = dict(
                "server", dict("host", "localhost", "port", 8080, "ssl", false),
                "timeout", 5000
        );

        Map<String, Object> tenantOverrides = dict(
                "server", dict("port", 9443, "ssl", true)
        );

        // 1. Recursive Deep Merge in 1 line!
        Map<String, Object> merged = merge(defaultConfig, tenantOverrides);
        System.out.println("Merged Port: " + get(merged, "server.port"));
        System.out.println("Merged Host: " + get(merged, "server.host"));

        // 2. Pick specific keys in 1 line!
        Map<String, Object> picked = pick(merged, "server");
        System.out.println("Picked: " + picked.keySet());
    }
}
