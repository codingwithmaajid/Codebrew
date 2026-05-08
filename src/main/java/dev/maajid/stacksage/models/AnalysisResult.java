package dev.maajid.stacksage.models;

import java.util.List;

public record AnalysisResult(
        ParsedStackTrace parsed,
        ExceptionInfo info,
        List<String> rootCauseHints
) {
    public String summary() {
        if (!parsed.hasException()) {
            return "StackSage could not find a JVM exception in the input.";
        }

        return "Detected " + parsed.exceptionType()
                + parsed.location().map(location -> " at " + location).orElse("");
    }
}
