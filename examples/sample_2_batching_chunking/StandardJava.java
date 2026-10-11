package sample_2_batching_chunking;

import java.util.*;

/**
 * SAMPLE 2: Batching & Chunking Collections
 * Standard Java (Vanilla) Implementation
 */
public class StandardJava {

    record Order(int id, String customer, double amount) {}

    public static void main(String[] args) {
        List<Order> orders = Arrays.asList(
                new Order(1, "Alice", 120.0),
                new Order(2, "Bob", 80.0),
                new Order(3, "Charlie", 250.0),
                new Order(4, "David", 310.0),
                new Order(5, "Eve", 95.0)
        );

        // Splitting into batches of size 2 for DB bulk save or API dispatch
        int batchSize = 2;
        List<List<Order>> batches = new ArrayList<>();
        if (orders != null && !orders.isEmpty() && batchSize > 0) {
            for (int i = 0; i < orders.size(); i += batchSize) {
                int end = Math.min(orders.size(), i + batchSize);
                batches.add(new ArrayList<>(orders.subList(i, end)));
            }
        }

        System.out.println("Total batches: " + batches.size());
        for (int i = 0; i < batches.size(); i++) {
            System.out.println("Batch " + (i + 1) + ": " + batches.get(i).size() + " orders");
        }
    }
}
