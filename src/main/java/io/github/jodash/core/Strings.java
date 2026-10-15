package io.github.jodash.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String utilities inspired by Lodash (camelCase, kebabCase, snakeCase, pascalCase, truncate, pad, words, etc.).
 */
public final class Strings {

    private Strings() {}

    /**
     * Splits string into an array of its words.
     */
    public static List<String> words(String string) {
        if (string == null || string.isBlank()) {
            return Collections.emptyList();
        }
        // Insert space before uppercase preceded by lowercase (e.g. fooBar -> foo Bar)
        String s = string.replaceAll("([a-z0-9])([A-Z])", "$1 $2");
        // Insert space between consecutive uppercase followed by lowercase (e.g. XMLHttp -> XML Http)
        s = s.replaceAll("([A-Z]+)([A-Z][a-z])", "$1 $2");
        // Replace non-alphanumerics with space
        s = s.replaceAll("[^a-zA-Z0-9]+", " ").trim();

        if (s.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.asList(s.split("\\s+"));
    }

    /**
     * Converts string to camelCase.
     * Example: "Foo Bar", "foo_bar", "foo-bar" -> "fooBar"
     */
    public static String camelCase(String string) {
        List<String> wordList = words(string);
        if (wordList.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordList.size(); i++) {
            String word = wordList.get(i).toLowerCase();
            if (i == 0) {
                sb.append(word);
            } else {
                sb.append(capitalize(word));
            }
        }
        return sb.toString();
    }

    /**
     * Converts string to kebab-case.
     * Example: "fooBar", "foo_bar" -> "foo-bar"
     */
    public static String kebabCase(String string) {
        List<String> wordList = words(string);
        if (wordList.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordList.size(); i++) {
            if (i > 0) sb.append("-");
            sb.append(wordList.get(i).toLowerCase());
        }
        return sb.toString();
    }

    /**
     * Converts string to snake_case.
     * Example: "fooBar", "foo-bar" -> "foo_bar"
     */
    public static String snakeCase(String string) {
        List<String> wordList = words(string);
        if (wordList.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordList.size(); i++) {
            if (i > 0) sb.append("_");
            sb.append(wordList.get(i).toLowerCase());
        }
        return sb.toString();
    }

    /**
     * Converts string to PascalCase.
     * Example: "foo_bar" -> "FooBar"
     */
    public static String pascalCase(String string) {
        List<String> wordList = words(string);
        if (wordList.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (String word : wordList) {
            sb.append(capitalize(word.toLowerCase()));
        }
        return sb.toString();
    }

    /**
     * Capitalizes the first letter of string.
     */
    public static String capitalize(String string) {
        if (string == null || string.isEmpty()) return "";
        return Character.toUpperCase(string.charAt(0)) + (string.length() > 1 ? string.substring(1) : "");
    }

    /**
     * Uncapitalizes the first letter of string.
     */
    public static String uncapitalize(String string) {
        if (string == null || string.isEmpty()) return "";
        return Character.toLowerCase(string.charAt(0)) + (string.length() > 1 ? string.substring(1) : "");
    }

    /**
     * Truncates string if it's longer than the given maximum string length using default omission "...".
     */
    public static String truncate(String string, int length) {
        return truncate(string, length, "...");
    }

    /**
     * Truncates string if it's longer than the given maximum string length with custom omission.
     */
    public static String truncate(String string, int length, String omission) {
        if (string == null) return "";
        if (omission == null) omission = "...";
        if (string.length() <= length) return string;
        if (length <= omission.length()) return omission.substring(0, Math.min(length, omission.length()));
        return string.substring(0, length - omission.length()) + omission;
    }

    /**
     * Pads string on the left and right sides if it's shorter than length.
     */
    public static String pad(String string, int length, String chars) {
        if (string == null) string = "";
        int strLen = string.length();
        if (strLen >= length) return string;

        int padLen = length - strLen;
        int leftPad = padLen / 2;
        int rightPad = padLen - leftPad;

        return padStart("", leftPad, chars) + string + padEnd("", rightPad, chars);
    }

    /**
     * Pads string on the left side if it's shorter than length.
     */
    public static String padStart(String string, int length, String chars) {
        if (string == null) string = "";
        if (chars == null || chars.isEmpty()) chars = " ";
        int strLen = string.length();
        if (strLen >= length) return string;

        int needed = length - strLen;
        StringBuilder sb = new StringBuilder(needed);
        int i = 0;
        int charsLen = chars.length();
        while (sb.length() < needed) {
            sb.append(chars.charAt(i % charsLen));
            i++;
        }
        return sb.toString() + string;
    }

    /**
     * Pads string on the right side if it's shorter than length.
     */
    public static String padEnd(String string, int length, String chars) {
        if (string == null) string = "";
        if (chars == null || chars.isEmpty()) chars = " ";
        int strLen = string.length();
        if (strLen >= length) return string;

        int needed = length - strLen;
        StringBuilder sb = new StringBuilder(needed);
        int i = 0;
        int charsLen = chars.length();
        while (sb.length() < needed) {
            sb.append(chars.charAt(i % charsLen));
            i++;
        }
        return string + sb.toString();
    }

    /**
     * Repeats the given string n times.
     */
    public static String repeat(String string, int n) {
        if (string == null || n <= 0) return "";
        return string.repeat(n);
    }

    /**
     * Escapes HTML characters: {@code &amp;}, {@code <}, {@code >}, {@code "}, and {@code '}.
     */
    public static String escape(String string) {
        if (string == null) return "";
        return string.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Unescapes HTML characters.
     */
    public static String unescape(String string) {
        if (string == null) return "";
        return string.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&#47;", "/")
                .replace("&sol;", "/")
                .replace("&amp;", "&");
    }
}
