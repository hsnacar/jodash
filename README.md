# ⚡ Jodash (`jo`)

> **The pragmatic, Lodash-inspired utility toolkit for modern Java (17+).**  
> *Zero external dependencies. Dynamic object ergonomics. Safe path navigation.*

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Java: 17+](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://openjdk.org/)
[![Zero Dependencies](https://img.shields.io/badge/Dependencies-Zero-brightgreen.svg)]()
[![Coverage: 95%](https://img.shields.io/badge/Coverage-95%25-brightgreen.svg)]()
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)]()

---

## 🎯 Why Jodash?

Coming from **JavaScript/TypeScript**, **Go** (`samber/lo`), or **Python**, writing simple data manipulation in Java often feels unnecessarily verbose:

- Accessing nested maps/JSON paths (`user.address.city`) requires nested null-checks, casting, or fragile chains.
- Java Streams are powerful, but writing `Collectors.groupingBy(...)` or `Collectors.mapping(...)` for simple tasks is exhausting.
- Java doesn't have native object/dict literals or safe deep merging.

**Jodash brings JavaScript / Lodash ergonomics to modern Java (Java 17, 21, 25+):**

```java
import static io.github.jodash.Jo.*;

// 1. Create dynamic objects effortlessly
var user = dict(
    "name", "Hasan",
    "address", dict("city", "Istanbul", "zip", "34000"),
    "tags", list("java", "go", "react")
);

// 2. Safe deep navigation (supports Maps, Records, POJOs, Lists, and Arrays)
String city = get(user, "address.city", "Unknown"); // "Istanbul"
int zip = asInt(get(user, "address.zip"), 0);        // 34000

// 3. Ergonomic collections in 1 line
List<List<Integer>> pages = chunk(list(1, 2, 3, 4, 5), 2); // [[1, 2], [3, 4], [5]]
Map<Role, List<User>> byRole = groupBy(users, User::role);
List<String> clean = compact(list("java", null, "", "go")); // ["java", "go"]

// 4. Case conversions & string helpers
String slug = kebabCase("Hello World From Java"); // "hello-world-from-java"
```

---

## ⚔️ Standard Java vs Jodash (Head-to-Head)

How much boilerplate do you actually eliminate? Here is the comparison:

| Task | Standard Java (Vanilla / Streams) | With Jodash (`jo`) | Lines Saved |
| :--- | :--- | :--- | :--- |
| **Deep Property Read** | 8-12 lines of null checks & casts | `get(data, "user.address.city")` | ~90% |
| **Dynamic Map Builder** | `Map.of()` (max 10 pairs, immutable, no nested path) | `dict("a", 1, "b.c", 2)` | ~75% |
| **List Chunking** | 7 lines (`IntStream.range` + subList) | `chunk(list, 100)` | 85% |
| **Grouping By Key** | `list.stream().collect(Collectors.groupingBy(...))` | `groupBy(list, User::dept)` | 60% |
| **Key By (Index)** | `list.stream().collect(Collectors.toMap(..., (a,b)->b))` | `keyBy(list, User::id)` | 70% |
| **Safe Type Cast** | Risky `(Integer) obj` (ClassCastException) | `asInt(obj, 0)` | 100% safer |
| **Deep Merge** | ~30 lines recursive helper | `merge(target, source)` | 95% |
| **String Casing** | Manual regex + StringBuilder loop | `kebabCase(str)`, `camelCase(str)` | ~90% |

<details>
<summary><b>🔍 View detailed side-by-side code comparisons</b></summary>

### 1. Reading Nested Property (`user.address.city`)
#### ❌ Standard Java:
```java
String city = null;
if (payload != null && payload.get("user") instanceof Map<?, ?> userMap) {
    if (userMap.get("address") instanceof Map<?, ?> addrMap) {
        Object rawCity = addrMap.get("city");
        if (rawCity instanceof String s) {
            city = s;
        }
    }
}
if (city == null) city = "Default City";
```
#### ✅ With Jodash:
```java
String city = get(payload, "user.address.city", "Default City");
```

---

### 2. Splitting a List into Batches (`chunk`)
#### ❌ Standard Java:
```java
int size = 50;
List<List<Order>> chunks = new ArrayList<>();
for (int i = 0; i < orders.size(); i += size) {
    chunks.add(new ArrayList<>(orders.subList(i, Math.min(orders.size(), i + size))));
}
```
#### ✅ With Jodash:
```java
List<List<Order>> chunks = chunk(orders, 50);
```

---

### 3. Safe Type Parsing (From Raw JSON / Dynamic Object)
#### ❌ Standard Java:
```java
// Might be String "42", Double 42.0, or null
int port = 8080;
Object rawPort = config.get("port");
if (rawPort instanceof Number n) {
    port = n.intValue();
} else if (rawPort instanceof String s) {
    try { port = Integer.parseInt(s.trim()); } catch (NumberFormatException ignored) {}
}
```
#### ✅ With Jodash:
```java
int port = asInt(config.get("port"), 8080);
```

---

### 4. Grouping & Indexing
#### ❌ Standard Java:
```java
Map<Department, List<Employee>> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::getDepartment));

Map<UUID, Employee> byId = employees.stream()
    .collect(Collectors.toMap(Employee::getId, Function.identity(), (prev, next) -> next, LinkedHashMap::new));
```
#### ✅ With Jodash:
```java
Map<Department, List<Employee>> byDept = groupBy(employees, Employee::getDepartment);
Map<UUID, Employee> byId = keyBy(employees, Employee::getId);
```

---

### 5. Deep Merging Two Maps / Configs
#### ❌ Standard Java:
Requires writing a custom 25-line recursive method traversing keys and checking instances of Map.
#### ✅ With Jodash:
```java
Map<String, Object> finalConfig = merge(defaultConfig, userConfig, envOverrides);
```

</details>

---

## 🤖 For AI Agents & LLMs

Jodash comes with specialized documentation designed for AI coding agents (Cursor, Copilot, Windsurf, Claude Code, Antigravity):

- **[AGENT_GUIDE.md](file:///Users/hasanacar/Documents/Projects/open-source-projects/jodash/AGENT_GUIDE.md)**: AI-optimized cheatsheet, anti-patterns, and zero-boilerplate recipes.
- **[llms.txt](file:///Users/hasanacar/Documents/Projects/open-source-projects/jodash/llms.txt)**: Fast machine-readable API manifest conforming to the [llmstxt.org](https://llmstxt.org/) standard.

---

## 📦 Installation

### Gradle
```groovy
implementation 'io.github.jodash:jodash:0.1.0'
```

### Maven
```xml
<dependency>
    <groupId>io.github.jodash</groupId>
    <artifactId>jodash</artifactId>
    <version>0.1.0</version>
</dependency>
```

---

## 🚀 Features & API Reference

Use static imports for maximum expressiveness:
```java
import static io.github.jodash.Jo.*;
// Or use Jodash class: import static io.github.jodash.Jodash.*;
```

---

### 1. 🗺️ Object & Safe Path Navigation

- **Reading (`get`, `has`):** Safely navigates nested hierarchies across `Map`, Java `Record`, `POJO` (via getters or fields), `List`, and Arrays without `NullPointerException` or `IndexOutOfBoundsException`.
- **Mutation (`set`, `unset`):** Mutates mutable `Map` roots (and nested `List`s within them), automatically creating intermediate `LinkedHashMap` and `ArrayList` nodes as needed. *(Note: Java Records are immutable by language design; mutation is designed for dynamic Maps and nested Lists).*

```java
// Safe property lookup
String city = get(data, "user.address.city");
String street = get(data, "user.address.street", "Main St."); // with fallback

// Array and list indexing
String firstRole = get(data, "user.roles[0]");
String tag = get(data, "items.1.name"); // dot-index also supported

// Path existence (returns true even if property value is null)
boolean exists = has(data, "user.profile.avatar");

// Safe deep mutation on mutable Map (creates intermediate maps and lists automatically)
set(data, "user.profile.theme", "dark");
set(data, "orders[0].status", "PENDING");

// Safe property removal
unset(data, "user.sensitiveToken");
```

#### Dynamic Map & Dict Builder
```java
// Mutable LinkedHashMap builder
Map<String, Object> map = dict("name", "Ali", "age", 30, "admin", true);

// Fluent Chained Object
var obj = obj()
    .set("name", "Hasan")
    .set("address.city", "Istanbul")
    .set("roles[0]", "admin");

Map<String, Object> rawMap = obj.toMap();
```

---

### 2. 🗃️ Object Utilities

```java
// Pick & Omit
Map<String, Object> publicProfile = pick(user, "id", "username", "avatar");
Map<String, Object> safeData = omit(requestBody, "password", "apiKey");

// Deep Merge (recursive nested map merge)
Map<String, Object> merged = merge(targetDefaults, customOverrides);

// Defaults (fills missing/null values from sources without overwriting existing ones)
Map<String, Object> resolved = defaults(userConfig, defaultConfig);

// Deep Clone (recursively clones Maps, Lists, Sets, and Arrays; custom class instances are copied by reference)
Map<String, Object> copy = cloneDeep(complexObject);

// Deep Structural Equality (deeply compares nested Maps, Lists, Sets, and Arrays)
boolean areEqual = isEqual(objA, objB);

// Universal Empty Check (checks null, empty string "" [length == 0], empty collection, map, array, optional)
// Note: Whitespace-only strings ("   ") have length > 0 and are NOT considered empty (matching Lodash).
isEmpty("");            // true
isEmpty("   ");         // false
isEmpty(List.of());     // true
isEmpty(new int[]{});   // true
isEmpty("hello");       // false
```

---

### 3. 📚 Collections & Arrays

#### `chunk(list, size)`
```java
List<Integer> numbers = list(1, 2, 3, 4, 5);
List<List<Integer>> chunks = chunk(numbers, 2); 
// [[1, 2], [3, 4], [5]]
```

#### `groupBy(items, keyFn)` & `keyBy(items, keyFn)`
```java
Map<String, List<Product>> byCategory = groupBy(products, Product::category);
Map<UUID, User> userIndex = keyBy(users, User::id);
Map<String, Long> categoryCounts = countBy(products, Product::category);
```

#### `compact(items)`
Removes `null`, empty strings (`""`, `"   "`), and empty `Optional`s:
```java
List<Object> clean = compact(list("apple", null, "", Optional.empty(), "banana"));
// ["apple", "banana"]
```

#### `uniq(items)` & `uniqBy(items, keyFn)`
Removes duplicates while preserving original insertion order:
```java
List<Integer> unique = uniq(list(1, 2, 2, 3, 1)); // [1, 2, 3]
List<Product> uniqueCategories = uniqBy(products, Product::category);
```

#### `flatten(nested)` & `flattenDeep(nested)`
```java
List<Integer> flat = flatten(list(list(1, 2), list(3, 4))); // [1, 2, 3, 4]
List<Object> deepFlat = flattenDeep(list(1, list(2, list(3, 4)))); // [1, 2, 3, 4]
```

#### `partition(items, predicate)`
```java
var part = partition(numbers, n -> n % 2 == 0);
part.matches(); // [2, 4, 6]
part.rejects(); // [1, 3, 5]
```

#### Set Operations (`difference`, `intersection`, `union`)
```java
difference(list(1, 2, 3), list(2, 3, 4));   // [1]
intersection(list(1, 2, 3), list(2, 3, 4)); // [2, 3]
union(list(1, 2), list(2, 3));              // [1, 2, 3]
```

#### Random & Sampling (`sample`, `sampleSize`, `shuffle`)
```java
User luckyWinner = sample(users);
List<User> fiveRandom = sampleSize(users, 5);
List<Card> deck = shuffle(cards);
```

#### Collection Analytics
```java
List<Product> sorted = sortBy(products, Product::price);
Optional<Product> cheapest = minBy(products, Product::price);
Optional<Product> highest = maxBy(products, Product::price);
double totalPrice = sumBy(products, Product::price);
double averagePrice = meanBy(products, Product::price);
```

---

### 4. 🔤 Strings

```java
camelCase("Foo Bar");       // "fooBar"
camelCase("__FOO_BAR__");   // "fooBar"
kebabCase("helloWorld");    // "hello-world"
snakeCase("helloWorld");    // "hello_world"
pascalCase("foo_bar");      // "FooBar"
capitalize("hello");        // "Hello"

// Truncate
truncate("hi-diddly-ho there, neighborino", 24); // "hi-diddly-ho there, n..."

// Padding
padStart("42", 5, "0"); // "00042"
pad("foo", 7, "-");     // "--foo--"

// HTML Escape & Unescape
escape("fred & barney < 10"); // "fred &amp; barney &lt; 10"
unescape("fred &amp; barney"); // "fred & barney"
```

---

### 5. 🛡️ Safe Type Coercion (`Coerce`)

Working with loosely typed data or JSON models in Java usually causes runtime `ClassCastException`. Jodash provides safe parsers:

```java
int port = asInt(data.get("port"), 8080);           // handles "8080", 8080.0, etc.
long id = asLong(data.get("id"), 0L);
double rate = asDouble(data.get("rate"), 1.0);
boolean isActive = asBoolean(data.get("active"), false); // handles "true", "1", 1, true
String name = asString(data.get("name"), "Anonymous");
List<Object> tags = asList(data.get("tags"));       // wraps arrays or singles safely
Map<String, Object> meta = asMap(data.get("meta")); // guaranteed Map<String, Object>
```

---

### 6. ⏱️ Control Flow & Functions

#### `memoize` (Thread-safe caching)
```java
Function<String, ExpensiveReport> cachedReport = memoize(service::buildReport);
Supplier<Config> lazyConfig = memoize(this::loadFromRemote);
```

#### `once` (Restricted execution)
```java
Runnable initializeOnce = once(() -> {
    setupDatabase();
});
```

#### `retry` (Resilient calls)
```java
String token = retry(3, Duration.ofMillis(500), () -> authClient.fetchToken());
```

---

## 🧪 Testing

Jodash has a comprehensive test suite covering all modules:

```bash
./gradlew test
```

---

## 📄 License

MIT License © 2026 Hasan Acar
