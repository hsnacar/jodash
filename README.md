# ⚡ Jodash (`jo`)

> **The pragmatic, Lodash-inspired utility toolkit for modern Java (17+).**  
> *Zero external dependencies. Dynamic object ergonomics. Safe path navigation.*

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Java: 17+](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://openjdk.org/)
[![Zero Dependencies](https://img.shields.io/badge/Dependencies-Zero-brightgreen.svg)]()
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

Safely navigate and mutate nested structures (`Map`, Java `Record`, `POJO`, `List`, Array) without worrying about `NullPointerException` or `IndexOutOfBoundsException`.

```java
// Safe property lookup
String city = get(data, "user.address.city");
String street = get(data, "user.address.street", "Main St."); // with fallback

// Array and list indexing
String firstRole = get(data, "user.roles[0]");
String tag = get(data, "items.1.name"); // dot-index also supported

// Path existence
boolean exists = has(data, "user.profile.avatar");

// Safe deep mutation (creates intermediate maps and lists automatically)
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

// Deep Clone & Deep Equality
Map<String, Object> copy = cloneDeep(complexObject);
boolean areEqual = isEqual(objA, objB);

// Universal Empty Check (checks null, empty string, collection, map, array, optional)
isEmpty("");            // true
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
