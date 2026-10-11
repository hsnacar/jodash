package sample_3_grouping_indexing;

import static io.github.jodash.Jo.*;
import java.util.List;
import java.util.Map;

/**
 * SAMPLE 3: Grouping, Indexing, and Counting Collections
 * Jodash Implementation
 */
public class Jodash {

    record Employee(int id, String name, String department, double salary) {}

    public static void main(String[] args) {
        List<Employee> employees = list(
                new Employee(101, "Alice", "Engineering", 95000),
                new Employee(102, "Bob", "Design", 75000),
                new Employee(103, "Charlie", "Engineering", 110000),
                new Employee(104, "David", "Marketing", 65000),
                new Employee(105, "Eve", "Design", 80000)
        );

        // 1. Group By Department in 1 line
        Map<String, List<Employee>> byDept = groupBy(employees, Employee::department);
        System.out.println("Engineering count: " + byDept.get("Engineering").size());

        // 2. Index By ID in 1 line
        Map<Integer, Employee> byId = keyBy(employees, Employee::id);
        System.out.println("Employee 101: " + byId.get(101).name());

        // 3. Count By Department in 1 line
        Map<String, Long> countByDept = countBy(employees, Employee::department);
        System.out.println("Counts: " + countByDept);
    }
}
