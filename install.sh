#!/usr/bin/env bash
set -e

APP_NAME="stacksage"
APP_DIR="$HOME/.local/share/stacksage"
BIN_DIR="$HOME/.local/bin"
JAR_URL="https://github.com/codingwithmaajid/Codebrew/releases/latest/download/stacksage.jar"
JAR_PATH="$APP_DIR/stacksage.jar"

command_exists() {
  command -v "$1" >/dev/null 2>&1
}

echo "☕ Installing StackSage JVM..."

if ! command_exists java; then
  echo "❌ Java is required but not installed."
  echo "Install Java 21 or newer, then run this installer again."
  exit 1
fi

if ! command_exists curl; then
  echo "❌ curl is required to download StackSage JVM."
  echo "Install curl, then run this installer again."
  exit 1
fi

mkdir -p "$APP_DIR"
mkdir -p "$BIN_DIR"

echo "⬇️ Downloading StackSage JVM..."
curl -fL "$JAR_URL" -o "$JAR_PATH"

cat > "$BIN_DIR/$APP_NAME" << 'EOF'
#!/usr/bin/env bash
java -jar "$HOME/.local/share/stacksage/stacksage.jar" "$@"
EOF

chmod +x "$BIN_DIR/$APP_NAME"

echo "✅ StackSage JVM installed successfully!"
echo ""
echo "Run:"
echo "  stacksage"
echo "  stacksage error.txt"
echo "  cat error.txt | stacksage"

if [[ ":$PATH:" != *":$BIN_DIR:"* ]]; then
  echo ""
  echo "⚠️ Add this to your shell config if 'stacksage' is not found:"
  echo "  export PATH=\"\$HOME/.local/bin:\$PATH\""
fi
