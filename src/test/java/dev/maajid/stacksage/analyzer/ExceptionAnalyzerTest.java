package dev.maajid.stacksage.analyzer;

import dev.maajid.stacksage.models.AnalysisResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionAnalyzerTest {
    @Test
    void returnsKnowledgeBaseExplanationForKnownException() {
        ExceptionAnalyzer analyzer = new ExceptionAnalyzer();

        AnalysisResult result = analyzer.analyze("""
                java.lang.NumberFormatException: For input string: "abc"
                    at dev.example.Parser.parseAge(Parser.java:18)
                """);

        assertEquals("NumberFormatException", result.parsed().exceptionType());
        assertEquals("Parsing", result.info().category());
        assertTrue(result.info().explanation().contains("convert text into a number"));
        assertTrue(result.rootCauseHints().getFirst().contains("Parser.java:18"));
    }
}
