package dev.maajid.stacksage.models;

import java.util.Optional;

public record ParsedStackTrace(
        String exceptionType,
        String exceptionMessage,
        String fileName,
        Integer lineNumber,
        String methodName,
        String rootFrame,
        String rawInput
) {
    public Optional<String> location() {
        if (fileName == null || fileName.isBlank()) {
            return Optional.empty();
        }

        if (lineNumber == null) {
            return Optional.of(fileName);
        }

        return Optional.of(fileName + ":" + lineNumber);
    }

    public boolean hasException() {
        return exceptionType != null && !exceptionType.isBlank();
    }
}
