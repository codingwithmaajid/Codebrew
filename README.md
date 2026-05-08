# StackSage JVM

StackSage JVM is an open-source Java debugging assistant that transforms intimidating JVM stack traces into understandable explanations and actionable debugging guidance.

## Features

- Paste JVM exceptions, stack traces, or runtime logs
- Detect common Java exceptions
- Extract probable root file, line, and method
- Explain exceptions in beginner-friendly language
- Suggest debugging steps and prevention tips
- Runs locally with no code execution or network dependency at runtime
- Dark, terminal-inspired JavaFX UI

## Supported MVP Exceptions

- `NullPointerException`
- `IllegalArgumentException`
- `NumberFormatException`
- `ArrayIndexOutOfBoundsException`
- `ConcurrentModificationException`
- `ClassNotFoundException`
- `IOException`
- `SQLException`

Unknown exceptions still get a generic stack-trace debugging guide.

## Requirements

- Java 21
- Maven 3.9+

## Run

```bash
mvn javafx:run
```

## Test

```bash
mvn test
```

## Usage

1. Paste a JVM stack trace into the input panel.
2. Click `Analyze`.
3. Review the detected exception, source location, explanation, and fix suggestions.
4. Use `Copy` to copy the formatted output.

Example input:

```text
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.length()" because "name" is null
    at dev.example.UserService.createUser(UserService.java:42)
    at dev.example.Main.main(Main.java:12)
```

## Project Structure

```text
src/main/java/dev/maajid/stacksage
├── App.java
├── analyzer
├── kb
├── models
├── parser
└── ui
```

## Roadmap

- AI Explain Mode using Gemini or OpenAI
- GitHub Actions log analysis
- Community knowledge base for new exceptions
- IntelliJ and VSCode plugins
- Localization for classroom use

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for setup and contribution guidelines.

## License

MIT License. See [LICENSE](LICENSE).
