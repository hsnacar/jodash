package sample_3_grouping_indexing;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * SAMPLE 3: Grouping, Indexing, and Counting Collections
 * Standard Java (Vanilla / Streams) Implementation
 */
public class StandardJava {

    record Employee(int id, String name, String department, double salary) {}

    public static void main(String[] args) {
        List<Employee> employees = Arrays.asList(
                new Employee(101, "Alice", "Engineering", 95000),
                new Employee(102, "Bob", "Design", 75000),
                new Employee(103, "Charlie", "Engineering", 110000),
                new Employee(104, "David", "Marketing", 65000),
                new Employee(105, "Eve", "Design", 80000)
        );

        // 1. Group By Department (verbose Collector chain)
        Map<String, List<Employee>> byDept = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        System.out.println("Engineering count: " + byDept.get("Engineering").size());

        // 2. Index By ID (keyBy: last element wins on conflict)
        Map<Integer, Employee> byId = employees.stream()
                .collect(Collectors.toMap(
                        Employee::id,
                        Function.identity(),
                        (existing, replacement) -> replacement,
                        LinkedHashMap::new
                ));
        System.out.println("Employee 101: " + byId.get(101).name());

        // 3. Count By Department
        Map<String, Long> countByDept = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::department,
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
        System.out.println("Counts: " + countByDept);
    }
}
