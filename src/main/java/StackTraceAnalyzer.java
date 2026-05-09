import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StackTraceAnalyzer {
    private static final Pattern EXCEPTION_PATTERN = Pattern.compile(
            "(?:Exception in thread \"[^\"]+\"\\s+)?(?:Caused by:\\s+)?([\\w.$]+(?:Exception|Error))(?:\\s*:\\s*(.*))?"
    );
    private static final Pattern FRAME_PATTERN = Pattern.compile(
            "\\s*at\\s+([\\w.$<>$]+)\\(([^():]+)(?::(\\d+))?\\)"
    );

    private final ErrorKnowledgeBase knowledgeBase;

    public StackTraceAnalyzer(ErrorKnowledgeBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }

    public static String analyze(String input) {
        return new StackTraceAnalyzer(new ErrorKnowledgeBase()).analyzeStackTrace(input);
    }

    public String analyzeStackTrace(String input) {
        return buildReport(input);
    }

    private String buildReport(String input) {
        ParsedStackTrace parsed = parse(input);
        ErrorKnowledgeBase.ErrorInfo info = knowledgeBase.find(parsed.exceptionType());

        StringBuilder output = new StringBuilder();
        appendSection(output, "Detected Error", info.name());
        appendSection(output, "Severity", info.severity());
        appendSection(output, "Likely Location", parsed.location());
        appendSection(output, "Crash Path", parsed.crashPath());

        String whatHappened = info.explanation();
        if (!parsed.exceptionMessage().isBlank()) {
            whatHappened += System.lineSeparator() + "JVM message: " + parsed.exceptionMessage();
        }
        appendSection(output, "What Happened", whatHappened);
        appendSection(output, "Fix Mode", bulletList(info.fixes()));
        appendSection(output, "Learning Note", info.learningNote());

        return output.toString().stripTrailing();
    }

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
        List<String> crashPath = new ArrayList<>();

        for (int index = 0; index < lines.size(); index++) {
            Matcher exceptionMatcher = EXCEPTION_PATTERN.matcher(lines.get(index));
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
                        frameMatcher.group(3) == null ? null : Integer.parseInt(frameMatcher.group(3))
                );
                crashPath.add(frame.format());

                if (rootFrame == null || isBetterRootFrame(frame, rootFrame)) {
                    rootFrame = frame;
                }
            }
        }

        return new ParsedStackTrace(
                exceptionType == null ? "UnknownException" : exceptionType,
                exceptionMessage,
                rootFrame == null ? "" : rootFrame.fileName(),
                rootFrame == null || rootFrame.lineNumber() == null ? "" : rootFrame.lineNumber().toString(),
                rootFrame == null ? "" : rootFrame.methodName(),
                crashPath
        );
    }

    private boolean isBetterRootFrame(StackFrame candidate, StackFrame current) {
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

    private void appendSection(StringBuilder output, String title, String body) {
        output.append(title).append(System.lineSeparator());
        output.append("-".repeat(title.length())).append(System.lineSeparator());
        output.append(body.isBlank() ? "Not found" : body).append(System.lineSeparator()).append(System.lineSeparator());
    }

    private String bulletList(List<String> items) {
        StringBuilder output = new StringBuilder();
        for (String item : items) {
            output.append("- ").append(item).append(System.lineSeparator());
        }
        return output.toString().stripTrailing();
    }

    public record ParsedStackTrace(
            String exceptionType,
            String exceptionMessage,
            String fileName,
            String lineNumber,
            String methodName,
            List<String> crashFrames
    ) {
        String location() {
            if (fileName.isBlank() || lineNumber.isBlank() || methodName.isBlank()) {
                return "Not found";
            }
            return fileName + ":" + lineNumber + " in " + methodName;
        }

        String crashPath() {
            if (crashFrames.isEmpty()) {
                return "No stack frames found.";
            }
            return String.join(System.lineSeparator(), crashFrames);
        }
    }

    private record StackFrame(String methodName, String fileName, Integer lineNumber) {
        String format() {
            if (lineNumber == null) {
                return methodName + " (" + fileName + ")";
            }
            return methodName + " (" + fileName + ":" + lineNumber + ")";
        }
    }
}
