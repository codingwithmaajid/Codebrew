package dev.maajid.stacksage.models;

import java.util.List;

public record ExceptionInfo(
        String exceptionType,
        String category,
        String explanation,
        List<String> probableCauses,
        List<String> debuggingSteps,
        List<String> preventionTips
) {
}
