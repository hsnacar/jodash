package sample_2_batching_chunking;

import static io.github.jodash.Jo.*;
import java.util.List;

/**
 * SAMPLE 2: Batching & Chunking Collections
 * Jodash Implementation
 */
public class Jodash {

    record Order(int id, String customer, double amount) {}

    public static void main(String[] args) {
        List<Order> orders = list(
                new Order(1, "Alice", 120.0),
                new Order(2, "Bob", 80.0),
                new Order(3, "Charlie", 250.0),
                new Order(4, "David", 310.0),
                new Order(5, "Eve", 95.0)
        );

        // Splitting into batches of size 2 in 1 simple line!
        List<List<Order>> batches = chunk(orders, 2);

        System.out.println("Total batches: " + batches.size());
        for (int i = 0; i < batches.size(); i++) {
            System.out.println("Batch " + (i + 1) + ": " + batches.get(i).size() + " orders");
        }
    }
}
