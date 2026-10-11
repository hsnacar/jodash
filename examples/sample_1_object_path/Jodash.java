package sample_1_object_path;

import static io.github.jodash.Jo.*;
import java.util.Map;

/**
 * SAMPLE 1: Reading and Mutating Nested Object Paths
 * Jodash Implementation
 */
public class Jodash {

    public static void main(String[] args) {
        // Construct nested payload cleanly in 1 readable block
        Map<String, Object> payload = dict(
                "user", dict(
                        "profile", dict(
                                "address", dict("city", "Istanbul", "zip", "34000")
                        ),
                        "roles", list("developer", "lead")
                )
        );

        // 1. Safe Deep Read with fallback (1 line!)
        String city = get(payload, "user.profile.address.city", "Unknown");
        System.out.println("City: " + city);

        // 2. Safe List Index Access (1 line!)
        String primaryRole = get(payload, "user.roles[0]", "GUEST");
        System.out.println("Primary Role: " + primaryRole);

        // 3. Mutating a nested property: creates intermediate paths automatically
        set(payload, "user.settings.theme", "dark");
        System.out.println("Updated Payload: " + payload);
    }
}
