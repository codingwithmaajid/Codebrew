package dev.maajid.stacksage.kb;

import dev.maajid.stacksage.models.ExceptionInfo;

import java.util.List;
import java.util.Map;

public class ExceptionKnowledgeBase {
    private final Map<String, ExceptionInfo> exceptions = Map.of(
            "NullPointerException", new ExceptionInfo(
                    "NullPointerException",
                    "Object state",
                    "Your code tried to use an object reference that currently points to null.",
                    List.of("A variable was never initialized.", "A method returned null unexpectedly.", "A nested field was accessed before checking its parent object."),
                    List.of("Inspect the variable used on the reported line.", "Add a null check or guard clause before dereferencing.", "Initialize the object before passing it into this method."),
                    List.of("Prefer constructor injection for required values.", "Use Optional only for values that are truly optional.", "Add focused tests for missing or empty inputs.")
            ),
            "IllegalArgumentException", new ExceptionInfo(
                    "IllegalArgumentException",
                    "Input validation",
                    "A method received an argument value it does not accept.",
                    List.of("Caller passed an invalid value.", "Validation rules were not checked before calling an API.", "A library rejected a parameter format."),
                    List.of("Read the exception message for the rejected value.", "Trace the caller that supplied the argument.", "Validate inputs before calling the failing method."),
                    List.of("Document valid ranges and formats.", "Fail fast with clear validation messages.", "Add tests for boundary values.")
            ),
            "NumberFormatException", new ExceptionInfo(
                    "NumberFormatException",
                    "Parsing",
                    "Java tried to convert text into a number, but the text was not a valid numeric value.",
                    List.of("Input contains letters, spaces, or symbols.", "An empty string was parsed.", "The number is outside the target type range."),
                    List.of("Print or inspect the exact string being parsed.", "Trim whitespace before parsing.", "Validate with a numeric pattern before conversion."),
                    List.of("Handle user input separately from parsing logic.", "Use clear error messages for invalid numbers.", "Add tests for empty and malformed strings.")
            ),
            "ArrayIndexOutOfBoundsException", new ExceptionInfo(
                    "ArrayIndexOutOfBoundsException",
                    "Collection bounds",
                    "Code tried to access an array position that does not exist.",
                    List.of("Loop boundary uses <= instead of <.", "Index was calculated incorrectly.", "Code assumes the array has at least one element."),
                    List.of("Compare the index with the array length on the failing line.", "Check loop start and end conditions.", "Guard empty arrays before reading from them."),
                    List.of("Use enhanced for loops where indexes are unnecessary.", "Centralize index calculations.", "Test empty, one-item, and many-item arrays.")
            ),
            "ConcurrentModificationException", new ExceptionInfo(
                    "ConcurrentModificationException",
                    "Collection mutation",
                    "A collection was modified while it was being iterated in an unsafe way.",
                    List.of("Item removed inside a for-each loop.", "Multiple threads changed the same collection.", "Iterator contract was violated."),
                    List.of("Find where the collection is changed during iteration.", "Use Iterator.remove() when removing during iteration.", "Copy the collection before iterating if mutation is expected."),
                    List.of("Use concurrent collections for shared mutable state.", "Keep iteration and mutation in separate phases.", "Add tests for removal and update flows.")
            ),
            "ClassNotFoundException", new ExceptionInfo(
                    "ClassNotFoundException",
                    "Classpath",
                    "The JVM tried to load a class that is not available on the runtime classpath.",
                    List.of("Dependency is missing at runtime.", "Class name is misspelled.", "Build packaging excluded a required jar."),
                    List.of("Verify the fully qualified class name.", "Check Maven or Gradle runtime dependencies.", "Inspect the packaged artifact contents."),
                    List.of("Keep dependency scopes accurate.", "Avoid stringly typed class names where possible.", "Run integration tests against packaged builds.")
            ),
            "IOException", new ExceptionInfo(
                    "IOException",
                    "I/O",
                    "An input or output operation failed while reading, writing, or communicating with an external resource.",
                    List.of("File path does not exist.", "Permission denied.", "Network or disk operation failed."),
                    List.of("Check the path, permissions, and resource availability.", "Log the file or endpoint being accessed.", "Handle retries only when the operation is safe to retry."),
                    List.of("Validate paths before use.", "Close resources with try-with-resources.", "Surface actionable error messages to users.")
            ),
            "SQLException", new ExceptionInfo(
                    "SQLException",
                    "Database",
                    "A database operation failed while executing SQL or communicating with the database.",
                    List.of("Invalid SQL syntax.", "Connection or credential issue.", "Constraint violation or schema mismatch."),
                    List.of("Read the SQL state and vendor error code.", "Check the query and bound parameters.", "Verify the database schema matches the code."),
                    List.of("Use migrations for schema changes.", "Prefer prepared statements.", "Add integration tests for important queries.")
            )
    );

    public ExceptionInfo find(String exceptionType) {
        if (exceptionType == null || exceptionType.isBlank()) {
            return unknown();
        }

        return exceptions.getOrDefault(exceptionType, unknownFor(exceptionType));
    }

    private ExceptionInfo unknownFor(String exceptionType) {
        return new ExceptionInfo(
                exceptionType,
                "Unknown JVM exception",
                "StackSage does not have a specific guide for this exception yet, but the stack trace still points to where the failure surfaced.",
                List.of("The exception is outside the current MVP knowledge base.", "A library or framework may be wrapping the original failure."),
                List.of("Start at the first application frame in the stack trace.", "Read any 'Caused by' section for the deeper failure.", "Search the project for the failing method and inspect inputs."),
                List.of("Add this exception to the knowledge base if it appears often.", "Capture a minimal failing test case.")
        );
    }

    private ExceptionInfo unknown() {
        return unknownFor("UnknownException");
    }
}
