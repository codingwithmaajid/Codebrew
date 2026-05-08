package dev.maajid.stacksage.analyzer;

import dev.maajid.stacksage.kb.ExceptionKnowledgeBase;
import dev.maajid.stacksage.models.AnalysisResult;
import dev.maajid.stacksage.models.ExceptionInfo;
import dev.maajid.stacksage.models.ParsedStackTrace;
import dev.maajid.stacksage.parser.StackTraceParser;

import java.util.ArrayList;
import java.util.List;

public class ExceptionAnalyzer {
    private final StackTraceParser parser;
    private final ExceptionKnowledgeBase knowledgeBase;

    public ExceptionAnalyzer() {
        this(new StackTraceParser(), new ExceptionKnowledgeBase());
    }

    public ExceptionAnalyzer(StackTraceParser parser, ExceptionKnowledgeBase knowledgeBase) {
        this.parser = parser;
        this.knowledgeBase = knowledgeBase;
    }

    public AnalysisResult analyze(String input) {
        ParsedStackTrace parsed = parser.parse(input);
        ExceptionInfo info = knowledgeBase.find(parsed.exceptionType());
        return new AnalysisResult(parsed, info, rootCauseHints(parsed));
    }

    private List<String> rootCauseHints(ParsedStackTrace parsed) {
        List<String> hints = new ArrayList<>();

        parsed.location().ifPresent(location -> hints.add("Start debugging at " + location + "."));

        if (parsed.methodName() != null && !parsed.methodName().isBlank()) {
            hints.add("The probable failing method is " + parsed.methodName() + ".");
        }

        if (parsed.exceptionMessage() != null && !parsed.exceptionMessage().isBlank()) {
            hints.add("Exception message: " + parsed.exceptionMessage());
        }

        if (hints.isEmpty()) {
            hints.add("Paste a complete JVM stack trace with at least one 'at package.Class.method(File.java:line)' frame for precise location detection.");
        }

        return hints;
    }
}
