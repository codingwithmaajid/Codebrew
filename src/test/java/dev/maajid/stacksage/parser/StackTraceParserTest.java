package dev.maajid.stacksage.parser;

import dev.maajid.stacksage.models.ParsedStackTrace;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StackTraceParserTest {
    private final StackTraceParser parser = new StackTraceParser();

    @Test
    void parsesExceptionAndFirstApplicationFrame() {
        String input = """
                Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.length()" because "name" is null
                    at dev.example.UserService.createUser(UserService.java:42)
                    at dev.example.Main.main(Main.java:12)
                """;

        ParsedStackTrace parsed = parser.parse(input);

        assertEquals("NullPointerException", parsed.exceptionType());
        assertEquals("UserService.java", parsed.fileName());
        assertEquals(42, parsed.lineNumber());
        assertEquals("dev.example.UserService.createUser", parsed.methodName());
    }

    @Test
    void parsesCausedByException() {
        String input = """
                java.lang.RuntimeException: save failed
                    at dev.example.Main.main(Main.java:9)
                Caused by: java.sql.SQLException: duplicate key
                    at dev.example.UserRepository.save(UserRepository.java:31)
                    at org.postgresql.Driver.connect(Driver.java:44)
                """;

        ParsedStackTrace parsed = parser.parse(input);

        assertEquals("SQLException", parsed.exceptionType());
        assertEquals("UserRepository.java", parsed.fileName());
        assertEquals(31, parsed.lineNumber());
    }
}
