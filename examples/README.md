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
| **[`sample_5_deep_merge_defaults`](./sample_5_deep_merge_defaults/)** | Config Merging, Defaults, Pick & Omit | `Jo.merge`, `Jo.defaults`, `Jo.pick` vs 25-line recursive method |
| **[`sample_6_safe_type_coercion`](./sample_6_safe_type_coercion/)** | Safe Dynamic Type Parsing | `Jo.asInt`, `Jo.asBoolean`, `Jo.asDouble` vs try-catch / instanceof |
| **[`sample_7_string_transformations`](./sample_7_string_transformations/)** | String Casing & Slugs | `Jo.kebabCase`, `Jo.camelCase`, `Jo.truncate` vs Regex / StringBuilder |
| **[`sample_8_resilient_control_flow`](./sample_8_resilient_control_flow/)** | Resilient Retry & Lazy Caching | `Jo.retry`, `Jo.memoize` vs retry loops & ConcurrentHashMap |

---

## 🚀 Running Any Sample

You can compile and run any sample using standard `java` with the jodash jar or classes:

```bash
# Example running sample 1:
javac -cp build/classes/java/main examples/sample_1_object_path/Jodash.java
java -cp build/classes/java/main:examples/sample_1_object_path sample_1_object_path.Jodash
```
