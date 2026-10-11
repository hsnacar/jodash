package io.github.jodash;

import io.github.jodash.core.JoObjects;
import io.github.jodash.core.ObjectPath;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fluent wrapper around a Map<String, Object> with dot-path navigation support.
 * Similar to dynamic JavaScript object handling.
 */
public class FluentMap {

    private final Map<String, Object> data;

    public FluentMap() {
        this.data = new LinkedHashMap<>();
    }

    public FluentMap(Map<String, Object> initial) {
        this.data = initial != null ? new LinkedHashMap<>(initial) : new LinkedHashMap<>();
    }

    /**
     * Sets a value at a dot-path (e.g. "user.address.city").
     */
    public FluentMap set(String path, Object value) {
        ObjectPath.set(this.data, path, value);
        return this;
    }

    /**
     * Gets a value at a dot-path.
     */
    public <T> T get(String path) {
        return ObjectPath.get(this.data, path);
    }

    /**
     * Gets a value at a dot-path with a default value.
     */
    public <T> T get(String path, T defaultValue) {
        return ObjectPath.get(this.data, path, defaultValue);
    }

    /**
     * Checks if a dot-path exists.
     */
    public boolean has(String path) {
        return ObjectPath.has(this.data, path);
    }

    /**
     * Unsets (removes) a property at a dot-path.
     */
    public FluentMap unset(String path) {
        ObjectPath.unset(this.data, path);
        return this;
    }

    /**
     * Picks only the specified top-level keys.
     */
    public FluentMap pick(String... keys) {
        Map<String, Object> picked = JoObjects.pick(this.data, keys);
        this.data.clear();
        this.data.putAll(picked);
        return this;
    }

    /**
     * Omits the specified top-level keys.
     */
    public FluentMap omit(String... keys) {
        Map<String, Object> omitted = JoObjects.omit(this.data, keys);
        this.data.clear();
        this.data.putAll(omitted);
        return this;
    }

    /**
     * Merges other maps deeply into this object.
     */
    @SafeVarargs
    public final FluentMap merge(Map<String, Object>... others) {
        JoObjects.merge(this.data, others);
        return this;
    }

    /**
     * Returns the underlying mutable map.
     */
    public Map<String, Object> toMap() {
        return this.data;
    }

    /**
     * Serializes this object to a compact JSON string without external dependencies.
     */
    public String toJson() {
        return io.github.jodash.core.Json.stringify(this.data);
    }

    /**
     * Serializes this object to a formatted (pretty-printed) JSON string.
     */
    public String toPrettyJson() {
        return io.github.jodash.core.Json.pretty(this.data);
    }

    @Override
    public String toString() {
        return this.data.toString();
    }
}
