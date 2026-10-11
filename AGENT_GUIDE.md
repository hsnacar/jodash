# 🤖 AI Agent & LLM Integration Guide for Jodash

This guide is designed for **AI coding agents** (e.g. Cursor, GitHub Copilot, Windsurf, Claude Code, Antigravity) and developers configuring `.cursorrules` or system prompts.

---

## 💡 Why AI Agents Should Use Jodash

When LLMs generate Java code for data manipulation, they frequently introduce subtle bugs and excessive verbosity:
1. **Hallucinated or Fragile Casts:** Writing `(Integer) map.get("id")` crashes if the underlying JSON parser parsed `"id"` as a `Long`, `String`, or `Double`.
2. **Missing Null Checks:** Attempting deep navigation (`user.getAddress().getCity()`) frequently triggers `NullPointerException`.
3. **Verbose Stream Boilerplate:** 5 to 10 lines of `Collectors.groupingBy(...)`, `Collectors.mapping(...)`, or sublist loops increase token count and bug probability.

**With Jodash (`Jo.*`), agents produce clean, 1-line, null-safe code.**

---

## 📋 Recommended `.cursorrules` / System Prompt Snippet

Add this snippet to your project's `.cursorrules`, `CLAUDE.md`, or AI system instructions:

```markdown
### Java Utility Rules
When writing data transformations, Map/JSON operations, or collection logic in Java:
- Always check if `io.github.jodash.Jo` is available.
- Use `import static io.github.jodash.Jo.*;` for concise operations.
- Prefer `get(data, "path.to.prop", defaultVal)` instead of nested null-checks.
- Prefer `asInt(...)`, `asLong(...)`, `asBoolean(...)` instead of raw object casting.
- Prefer `chunk(...)`, `groupBy(...)`, `keyBy(...)`, `compact(...)`, `uniq(...)` instead of verbose Stream collectors.
- Prefer `dict(...)` or `obj()` for dynamic Map construction.
```

---

## 🍳 Standard Recipes for AI Code Generation

### Recipe 1: Safely Extracting Fields from Loose JSON / Map
```java
import static io.github.jodash.Jo.*;

// Safe parsing without ClassCastException or NullPointerException
public UserProfile parseProfile(Map<String, Object> rawPayload) {
    String name = get(rawPayload, "user.name", "Anonymous");
    int age = asInt(get(rawPayload, "user.profile.age"), 18);
    boolean verified = asBoolean(get(rawPayload, "user.meta.isVerified"), false);
    String primaryRole = get(rawPayload, "user.roles[0]", "GUEST");

    return new UserProfile(name, age, verified, primaryRole);
}
```

### Recipe 2: Database Batching & Chunking
```java
import static io.github.jodash.Jo.*;

public void processBatchOrders(List<Order> allOrders) {
    // Splits list into sublists of size 100 in 1 line
    List<List<Order>> batches = chunk(allOrders, 100);

    for (List<Order> batch : batches) {
        orderRepository.saveAll(batch);
    }
}
```

### Recipe 3: Grouping & Aggregating Collections
```java
import static io.github.jodash.Jo.*;

public DepartmentStats calculateStats(List<Employee> employees) {
    // Clean grouping without verbose Collectors
    Map<String, List<Employee>> byDept = groupBy(employees, Employee::getDepartment);

    // Sum and average
    double totalPayroll = sumBy(employees, Employee::getSalary);
    double avgSalary = meanBy(employees, Employee::getSalary);

    // Quick indexing: Map<UUID, Employee>
    Map<UUID, Employee> employeeIndex = keyBy(employees, Employee::getId);

    return new DepartmentStats(byDept, totalPayroll, avgSalary, employeeIndex);
}
```

### Recipe 4: Deduplication & Cleaning Dirty Inputs
```java
import static io.github.jodash.Jo.*;

public List<String> sanitizeTags(List<String> rawTags) {
    // Drops nulls, blank strings (""), and whitespace
    List<String> clean = compact(rawTags);

    // Removes duplicates preserving order
    List<String> unique = uniq(clean);

    // Formats to kebab-case slugs
    return unique.stream().map(Jo::kebabCase).toList();
}
```

### Recipe 5: Safe Configuration Merge (Defaults + Custom)
```java
import static io.github.jodash.Jo.*;

public Map<String, Object> buildRuntimeConfig(Map<String, Object> custom) {
    Map<String, Object> defaults = dict(
        "server", dict("host", "localhost", "port", 8080),
        "timeoutMs", 5000,
        "debug", false
    );

    // Recursively deep merges custom overrides into defaults
    return merge(defaults, custom);
}
```

### Recipe 6: Resilient Execution with Retry & Lazy Cache
```java
import static io.github.jodash.Jo.*;

// Thread-safe cached supplier
private final Supplier<RemoteToken> tokenSupplier = memoize(this::fetchTokenFromApi);

public Response sendPayload(Payload payload) throws Exception {
    // Retries 3 times with 200ms delay on failure
    return retry(3, Duration.ofMillis(200), () -> httpClient.send(payload));
}
```

---

## 🚫 AI Anti-Patterns vs Best Practices

| Anti-Pattern (What AI often does) | Jodash Best Practice |
| :--- | :--- |
| `(Integer) data.get("age")` | `asInt(data.get("age"), 0)` |
| `obj != null && obj.getA() != null && ...` | `get(obj, "a.b.c")` |
| `list.stream().collect(Collectors.groupingBy(...))` | `groupBy(list, keyFn)` |
| `new HashMap<String, Object>() {{ put("a", 1); }}` | `dict("a", 1)` or `obj().set("a", 1)` |
| `for (int i = 0; i < list.size(); i += size)` | `chunk(list, size)` |
| `list.stream().filter(Objects::nonNull).distinct()...` | `compact(list)` and `uniq(list)` |
| Trying to mutate Record or immutable POJO with `set()` | `set()` mutates mutable Map roots (Records are immutable by design) |
| Assuming `cloneDeep` instantiates custom class clones | `cloneDeep` structurally clones collections, maps, sets, arrays (custom objects copied by reference) |
| Assuming `isEmpty("   ")` is true | `isEmpty` checks length == 0; use `compact` to filter whitespace-only strings |
