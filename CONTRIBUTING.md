# Contributing to StackSage JVM

Thanks for helping improve StackSage JVM.

## Local Setup

```bash
mvn test
mvn javafx:run
```

## Good First Contributions

- Add a new exception entry in `ExceptionKnowledgeBase`
- Improve explanation wording for beginners
- Add parser tests for real-world stack traces
- Improve JavaFX styling or accessibility
- Add screenshots to the README

## Code Guidelines

- Keep the app offline-first.
- Do not execute pasted code or logs.
- Prefer simple Java 21 code and focused tests.
- Keep exception explanations concise and practical.

## Pull Request Checklist

- Tests pass with `mvn test`
- New exception handlers include tests where useful
- README changes are included for user-facing behavior
