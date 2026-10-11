package sample_7_string_transformations;

import static io.github.jodash.Jo.*;

/**
 * SAMPLE 7: String Casing & Slugs
 * Jodash Implementation
 */
public class Jodash {

    public static void main(String[] args) {
        String title = "User Account Settings & Billing";

        // 1. Kebab-case URL slug in 1 line!
        String slug = kebabCase(title);
        System.out.println("Slug: " + slug);

        // 2. Truncate in 1 line!
        String excerpt = truncate("Long article title explaining the details of architecture", 25);
        System.out.println("Excerpt: " + excerpt);
    }
}
