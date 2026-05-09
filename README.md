# StackSage JVM

**Paste a JVM crash. Get the story, source, and fix.**

StackSage JVM is a small Java CLI that turns noisy JVM stack traces into a readable debugging report. It is built for fast terminal use during hackathons, demos, interviews, and everyday Java debugging.

## The Problem

JVM stack traces are useful, but they are often hard to read under pressure. Beginners see a wall of package names. Experienced developers still lose time finding the real failing line, the exception meaning, and the next fix to try.

StackSage keeps the workflow simple: pipe in a crash or pass a file, then get a clean explanation with the likely source location and practical fix suggestions.

## Features

- Reads stack traces from a file or stdin
- Detects common Java exceptions
- Highlights the likely failing file, line, and method
- Shows the crash path from stack frames
- Explains what happened in plain English
- Suggests focused fixes
- Installs with Java only from a prebuilt release jar
- No Spring Boot, no database, no background service

## Installation

```bash
curl -sSL https://raw.githubusercontent.com/codingwithmaajid/Codebrew/main/install.sh | bash
```

The installer downloads the latest release jar and creates:

```text
~/.local/share/stacksage/stacksage.jar
~/.local/bin/stacksage
```

Make sure `~/.local/bin` is on your `PATH`.

If your shell cannot find `stacksage` after install, run:

```bash
export PATH="$HOME/.local/bin:$PATH"
```

## Usage

Show the banner and usage:

```bash
stacksage
```

Analyze a stack trace file:

```bash
stacksage error.txt
```

Analyze piped input:

```bash
cat error.txt | stacksage
```

Show help:

```bash
stacksage --help
```

Run the jar directly:

```bash
java -jar target/stacksage.jar error.txt
cat error.txt | java -jar target/stacksage.jar
```

Build locally from source:

```bash
mvn clean package
```

## Example

Input:

```text
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.length()" because "name" is null
    at com.demo.UserService.createUser(UserService.java:42)
    at com.demo.App.main(App.java:12)
```

Output:

```text
Detected Error
--------------
NullPointerException

Severity
--------
High

Likely Location
---------------
UserService.java:42 in com.demo.UserService.createUser

Crash Path
----------
com.demo.UserService.createUser (UserService.java:42)
com.demo.App.main (App.java:12)

What Happened
-------------
Your code tried to use an object reference that currently points to null.
JVM message: Cannot invoke "String.length()" because "name" is null

Fix Mode
--------
- Inspect the variable used on the reported line.
- Add a null check before dereferencing.
- Initialize required objects before passing them into this method.

Learning Note
-------------
Null means the reference points to no object. The fix is usually at the assignment or method return before the crash line.
```

## Supported Errors

- `NullPointerException`
- `ArrayIndexOutOfBoundsException`
- `NumberFormatException`
- `ClassNotFoundException`
- `IllegalArgumentException`
- `ConcurrentModificationException`
- `IOException`
- `SQLException`

Unknown exceptions still produce a generic debugging report.

## Tech Stack

- Java 21
- Maven for local source builds
- Plain Java CLI

## Project Structure

```text
StackSageJVM/
├── pom.xml
├── README.md
├── install.sh
└── src/
    └── main/
        ├── java/
        │   ├── App.java
        │   ├── StackTraceAnalyzer.java
        │   └── ErrorKnowledgeBase.java
        └── resources/
```

## Roadmap

- More JVM exception guides
- Better `Caused by` chain detection
- Severity tuning by exception and crash context
- JSON output mode for CI logs
- GitHub Actions log examples
- IntelliJ and VS Code integrations later

## Contributing

Open-source contributions are welcome. Good first contributions include adding exception explanations, improving parsing for real-world stack traces, tightening README examples, and testing StackSage against logs from different Java frameworks.

Keep contributions aligned with the MVP goals: simple CLI, plain Java, no database, no Spring Boot, and no extra architecture until the tool earns it.
