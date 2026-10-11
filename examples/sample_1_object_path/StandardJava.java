package sample_1_object_path;

import java.util.*;

/**
 * SAMPLE 1: Reading and Mutating Nested Object Paths
 * Standard Java (Vanilla) Implementation
 */
public class StandardJava {

    public static void main(String[] args) {
        // Complex payload from API or loosely typed JSON map
        Map<String, Object> payload = new LinkedHashMap<>();
        Map<String, Object> user = new LinkedHashMap<>();
        Map<String, Object> profile = new LinkedHashMap<>();
        Map<String, Object> address = new LinkedHashMap<>();
        address.put("city", "Istanbul");
        address.put("zip", "34000");
        profile.put("address", address);
        user.put("profile", profile);
        user.put("roles", Arrays.asList("developer", "lead"));
        payload.put("user", user);

        // 1. Safe Deep Read with defensive null checks & casting
        String city = "Unknown";
        if (payload != null && payload.get("user") instanceof Map<?, ?> userMap) {
            if (userMap.get("profile") instanceof Map<?, ?> profileMap) {
                if (profileMap.get("address") instanceof Map<?, ?> addrMap) {
                    Object rawCity = addrMap.get("city");
                    if (rawCity instanceof String s) {
                        city = s;
                    }
                }
            }
        }
        System.out.println("City: " + city);

        // 2. Safe List Index Access
        String primaryRole = "GUEST";
        if (payload != null && payload.get("user") instanceof Map<?, ?> userMap) {
            if (userMap.get("roles") instanceof List<?> roleList && !roleList.isEmpty()) {
                Object role = roleList.get(0);
                if (role instanceof String s) {
                    primaryRole = s;
                }
            }
        }
        System.out.println("Primary Role: " + primaryRole);

        // 3. Mutating a nested property: requires manual intermediate map creation
        String path1 = "settings";
        String path2 = "theme";
        Object settingsObj = user.get(path1);
        Map<String, Object> settingsMap;
        if (settingsObj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> casted = (Map<String, Object>) settingsObj;
            settingsMap = casted;
        } else {
            settingsMap = new LinkedHashMap<>();
            user.put(path1, settingsMap);
        }
        settingsMap.put(path2, "dark");
        System.out.println("Updated Payload: " + payload);
    }
}
