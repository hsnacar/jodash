# 📂 Jodash vs Standard Java: Side-by-Side Comparison Samples

This directory contains standalone, runnable side-by-side comparison samples demonstrating how common software engineering tasks are solved in **Standard Java** versus with **Jodash (`jo`)**.

Each sample directory contains:
- `StandardJava.java`: How the task is traditionally implemented in Java (verbose loops, nested null checks, complex Streams, manual boilerplate).
- `Jodash.java`: How the exact same task is solved in clean, readable, 1-line operations using Jodash.

---

## 🗂️ Sample Catalog

| Sample | Feature / Topic | Core Methods Compared |
| :--- | :--- | :--- |
| **[`sample_1_object_path`](./sample_1_object_path/)** | Nested Object & Safe Path Navigation | `Jo.get`, `Jo.set`, `Jo.dict` vs nested null-check blocks |
| **[`sample_2_batching_chunking`](./sample_2_batching_chunking/)** | Pagination & Batch Chunking | `Jo.chunk` vs `subList` indexing loops |
| **[`sample_3_grouping_indexing`](./sample_3_grouping_indexing/)** | Grouping, Indexing & Counting | `Jo.groupBy`, `Jo.keyBy`, `Jo.countBy` vs `Collectors.groupingBy / toMap` |
| **[`sample_4_cleaning_deduplication`](./sample_4_cleaning_deduplication/)** | Input Sanitization & Deduplication | `Jo.compact`, `Jo.uniq`, `Jo.partition` vs manual Set/List filtering |
| **[`sample_5_deep_merge_defaults`](./sample_5_deep_merge_defaults/)** | Deep Config Merging & Pick | `Jo.merge`, `Jo.pick` vs recursive merge and manual filtering |
| **[`sample_6_safe_type_coercion`](./sample_6_safe_type_coercion/)** | Safe Dynamic Type Parsing | `Jo.asInt`, `Jo.asBoolean`, `Jo.asDouble` vs try-catch / instanceof |
| **[`sample_7_string_transformations`](./sample_7_string_transformations/)** | String Casing & Slugs | `Jo.kebabCase`, `Jo.camelCase`, `Jo.truncate` vs Regex / StringBuilder |
| **[`sample_8_resilient_control_flow`](./sample_8_resilient_control_flow/)** | Resilient Retry & Lazy Caching | `Jo.retry`, `Jo.memoize` vs retry loops & ConcurrentHashMap |
| **[`sample_9_zero_dependency_json`](./sample_9_zero_dependency_json/)** | Zero-Dependency JSON Serialization | `Jo.toJson`, `Jo.toPrettyJson`, `obj().toJson()` vs manual stringifier / Jackson |

---

## 🚀 Running Any Sample

From the repository root, compile the library and both sides of a sample:

```bash
./gradlew classes
mkdir -p build/examples
javac -cp build/classes/java/main -d build/examples examples/sample_1_object_path/*.java

# Run either side of the comparison:
java -cp build/classes/java/main:build/examples sample_1_object_path.StandardJava
java -cp build/classes/java/main:build/examples sample_1_object_path.Jodash
```

Replace `sample_1_object_path` with another sample package to run a different comparison. On Windows, use `;` instead of `:` in the Java classpath.
