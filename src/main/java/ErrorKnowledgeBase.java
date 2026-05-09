import java.util.List;
import java.util.Map;

public class ErrorKnowledgeBase {
    private final Map<String, ErrorInfo> errors = Map.of(
            "NullPointerException", new ErrorInfo(
                    "NullPointerException",
                    "High",
                    "Your code tried to use an object reference that currently points to null.",
                    List.of("Inspect the variable used on the reported line.", "Add a null check before dereferencing.", "Initialize required objects before passing them into this method."),
                    "Null means the reference points to no object. The fix is usually at the assignment or method return before the crash line."
            ),
            "ArrayIndexOutOfBoundsException", new ErrorInfo(
                    "ArrayIndexOutOfBoundsException",
                    "Medium",
                    "Code tried to access an array position that does not exist.",
                    List.of("Compare the index with the array length on the failing line.", "Check loop start and end conditions.", "Guard empty arrays before reading from them."),
                    "Array indexes start at 0, so the last valid index is length - 1."
            ),
            "NumberFormatException", new ErrorInfo(
                    "NumberFormatException",
                    "Low",
                    "Java tried to convert text into a number, but the text was not a valid numeric value.",
                    List.of("Inspect the exact string being parsed.", "Trim whitespace before parsing.", "Validate user input before converting it."),
                    "Parsing errors are data-shape problems. Log the raw value before conversion."
            ),
            "ClassNotFoundException", new ErrorInfo(
                    "ClassNotFoundException",
                    "High",
                    "The JVM tried to load a class that is not available on the runtime classpath.",
                    List.of("Verify the fully qualified class name.", "Check Maven dependency scopes.", "Inspect the packaged artifact for missing classes."),
                    "Compile-time classpaths and runtime classpaths can differ. Check what is inside the jar or deployment image."
            ),
            "IllegalArgumentException", new ErrorInfo(
                    "IllegalArgumentException",
                    "Medium",
                    "A method received an argument value it does not accept.",
                    List.of("Read the exception message for the rejected value.", "Trace the caller that supplied the argument.", "Validate inputs before calling the failing method."),
                    "This usually means the caller and callee disagree about allowed input."
            ),
            "ConcurrentModificationException", new ErrorInfo(
                    "ConcurrentModificationException",
                    "Medium",
                    "A collection was modified while it was being iterated in an unsafe way.",
                    List.of("Find where the collection changes during iteration.", "Use Iterator.remove() when removing during iteration.", "Copy the collection before iterating if mutation is expected."),
                    "For-each loops use an iterator underneath. Mutating the collection directly breaks that iterator."
            ),
            "IOException", new ErrorInfo(
                    "IOException",
                    "Medium",
                    "An input or output operation failed while reading, writing, or communicating with an external resource.",
                    List.of("Check the path, permissions, and resource availability.", "Log the file or endpoint being accessed.", "Use try-with-resources for files and streams."),
                    "I/O failures often depend on the environment, not just the code. Reproduce with the same path, user, and machine."
            ),
            "SQLException", new ErrorInfo(
                    "SQLException",
                    "High",
                    "A database operation failed while executing SQL or communicating with the database.",
                    List.of("Read the SQL state and vendor error code.", "Check the query and bound parameters.", "Verify the database schema matches the code."),
                    "The SQL state and vendor code usually say whether this is syntax, connection, constraint, or schema related."
            )
    );

    public ErrorInfo find(String exceptionType) {
        if (exceptionType == null || exceptionType.isBlank()) {
            return unknown("UnknownException");
        }
        return errors.getOrDefault(exceptionType, unknown(exceptionType));
    }

    private ErrorInfo unknown(String exceptionType) {
        return new ErrorInfo(
                exceptionType,
                "Unknown",
                "StackSage does not have a specific guide for this exception yet, but the stack trace can still point you toward the failing code.",
                List.of("Start at the first application frame in the stack trace.", "Read any 'Caused by' section for the deeper failure.", "Search the project for the failing method and inspect its inputs."),
                "Unknown exceptions are still useful: the first application frame is usually the best place to start."
        );
    }

    public record ErrorInfo(
            String name,
            String severity,
            String explanation,
            List<String> fixes,
            String learningNote
    ) {
    }
}
