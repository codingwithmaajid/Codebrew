#!/usr/bin/env bash
set -euo pipefail

REPO_TARBALL_URL="https://github.com/codingwithmaajid/StackSageJVM/archive/refs/heads/main.tar.gz"
APP_DIR="$HOME/.local/share/stacksage"
BIN_DIR="$HOME/.local/bin"
JAR_PATH="$APP_DIR/stacksage.jar"
COMMAND_PATH="$BIN_DIR/stacksage"

require_command() {
    if ! command -v "$1" >/dev/null 2>&1; then
        echo "Missing required command: $1" >&2
        echo "Install $1 and run this installer again." >&2
        exit 1
    fi
}

require_command java
require_command mvn

WORK_DIR="$(mktemp -d)"
cleanup() {
    rm -rf "$WORK_DIR"
}
trap cleanup EXIT

if [ -f "pom.xml" ] && [ -d "src/main/java" ]; then
    PROJECT_DIR="$(pwd)"
else
    require_command curl
    require_command tar

    PROJECT_DIR="$WORK_DIR/StackSageJVM"
    mkdir -p "$PROJECT_DIR"
    curl -sSL "$REPO_TARBALL_URL" | tar -xz -C "$PROJECT_DIR" --strip-components=1
fi

(cd "$PROJECT_DIR" && mvn clean package)

mkdir -p "$APP_DIR" "$BIN_DIR"
cp "$PROJECT_DIR/target/stacksage.jar" "$JAR_PATH"

cat > "$COMMAND_PATH" << 'EOF'
#!/usr/bin/env bash
java -jar "$HOME/.local/share/stacksage/stacksage.jar" "$@"
EOF

chmod +x "$COMMAND_PATH"

echo "StackSage JVM installed successfully."
echo "Run: stacksage"

case ":$PATH:" in
    *":$BIN_DIR:"*) ;;
    *)
        echo
        echo "$BIN_DIR is not in your PATH."
        echo 'Run: export PATH="$HOME/.local/bin:$PATH"'
        ;;
esac
