package dev.maajid.stacksage.parser;

import dev.maajid.stacksage.models.ParsedStackTrace;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackTraceParser {
    private static final Pattern EXCEPTION_PATTERN = Pattern.compile(
            "(?:Exception in thread \"[^\"]+\"\\s+)?(?:Caused by:\\s+)?([\\w.$]+(?:Exception|Error))(?:\\s*:\\s*(.*))?"
    );
    private static final Pattern FRAME_PATTERN = Pattern.compile(
            "\\s*at\\s+([\\w.$<>$]+)\\(([^():]+)(?::(\\d+))?\\)"
    );

    public ParsedStackTrace parse(String input) {
        String rawInput = input == null ? "" : input;
        List<String> lines = Arrays.stream(rawInput.split("\\R"))
                .map(String::strip)
                .filter(line -> !line.isBlank())
                .toList();

        String exceptionType = null;
        String exceptionMessage = "";
        int exceptionLineIndex = -1;
        StackFrame rootFrame = null;

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index);
            Matcher exceptionMatcher = EXCEPTION_PATTERN.matcher(line);
            if (exceptionMatcher.find()) {
                exceptionType = simpleName(exceptionMatcher.group(1));
                exceptionMessage = exceptionMatcher.group(2) == null ? "" : exceptionMatcher.group(2).strip();
                exceptionLineIndex = index;
            }
        }

        for (int index = Math.max(0, exceptionLineIndex + 1); index < lines.size(); index++) {
            String line = lines.get(index);
            if (index > exceptionLineIndex && EXCEPTION_PATTERN.matcher(line).find()) {
                break;
            }

            Matcher frameMatcher = FRAME_PATTERN.matcher(line);
            if (frameMatcher.matches()) {
                StackFrame frame = new StackFrame(
                        frameMatcher.group(1),
                        frameMatcher.group(2),
                        frameMatcher.group(3) == null ? null : Integer.parseInt(frameMatcher.group(3)),
                        line
                );

                if (rootFrame == null || isApplicationFrame(frame, rootFrame)) {
                    rootFrame = frame;
                }
            }
        }

        return new ParsedStackTrace(
                exceptionType,
                exceptionMessage,
                rootFrame == null ? null : rootFrame.fileName(),
                rootFrame == null ? null : rootFrame.lineNumber(),
                rootFrame == null ? null : rootFrame.methodName(),
                rootFrame == null ? null : rootFrame.rawLine(),
                rawInput
        );
    }

    private boolean isApplicationFrame(StackFrame candidate, StackFrame current) {
        return isJdkFrame(current.methodName()) && !isJdkFrame(candidate.methodName());
    }

    private boolean isJdkFrame(String methodName) {
        return methodName.startsWith("java.")
                || methodName.startsWith("javax.")
                || methodName.startsWith("jdk.")
                || methodName.startsWith("sun.")
                || methodName.startsWith("com.sun.");
    }

    private String simpleName(String qualifiedName) {
        int dot = qualifiedName.lastIndexOf('.');
        return dot >= 0 ? qualifiedName.substring(dot + 1) : qualifiedName;
    }

    private record StackFrame(String methodName, String fileName, Integer lineNumber, String rawLine) {
    }
}
