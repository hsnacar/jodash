package sample_5_deep_merge_defaults;

import java.util.*;

/**
 * SAMPLE 5: Deep Configuration Merging & Pick
 * Standard Java (Vanilla) Implementation
 */
public class StandardJava {

    // Helper method required in vanilla Java for recursive deep map merge
    @SuppressWarnings("unchecked")
    private static Map<String, Object> deepMerge(Map<String, Object> target, Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>(target);
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object srcVal = entry.getValue();
            Object tgtVal = result.get(key);

            if (tgtVal instanceof Map && srcVal instanceof Map) {
                result.put(key, deepMerge((Map<String, Object>) tgtVal, (Map<String, Object>) srcVal));
            } else {
                result.put(key, srcVal);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Map<String, Object> defaultConfig = new LinkedHashMap<>();
        Map<String, Object> server = new LinkedHashMap<>();
        server.put("host", "localhost");
        server.put("port", 8080);
        server.put("ssl", false);
        defaultConfig.put("server", server);
        defaultConfig.put("timeout", 5000);

        Map<String, Object> tenantOverrides = new LinkedHashMap<>();
        Map<String, Object> tenantServer = new LinkedHashMap<>();
        tenantServer.put("port", 9443);
        tenantServer.put("ssl", true);
        tenantOverrides.put("server", tenantServer);

        // 1. Recursive Deep Merge
        Map<String, Object> merged = deepMerge(defaultConfig, tenantOverrides);
        System.out.println("Merged Port: " + ((Map<?, ?>) merged.get("server")).get("port"));
        System.out.println("Merged Host: " + ((Map<?, ?>) merged.get("server")).get("host"));

        // 2. Pick specific keys (e.g., exposing safe public config)
        Set<String> allowedKeys = Set.of("server");
        Map<String, Object> picked = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : merged.entrySet()) {
            if (allowedKeys.contains(entry.getKey())) {
                picked.put(entry.getKey(), entry.getValue());
            }
        }
        System.out.println("Picked: " + picked.keySet());
    }
}
