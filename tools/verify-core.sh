#!/usr/bin/env bash
#
# Verifies the pure-Kotlin core modules without Gradle.
#
# Why this exists: the core modules are deliberately Android-free, so they can be
# compiled and unit-tested by any Kotlin/JVM toolchain. That gives real verification
# evidence in environments where Gradle, the Android SDK or Google Maven are unavailable
# (for example a restricted CI sandbox). It is a supplement to — never a replacement for
# — `./gradlew build` and an APK build on a connected machine.
#
# Requirements: a JDK (17+), and either `kotlinc` on PATH or KOTLIN_HOME pointing at a
# Kotlin compiler distribution that contains lib/kotlin-test.jar.
#
# Usage:
#   ./tools/verify-core.sh                 # compile + run all core tests
#   KOTLIN_HOME=/path/to/kotlinc ./tools/verify-core.sh
#
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_DIR="$ROOT_DIR/build/verify"
LOG_FILE="$BUILD_DIR/verify-core.log"

KOTLIN_HOME="${KOTLIN_HOME:-}"
if [[ -z "$KOTLIN_HOME" ]] && command -v kotlinc >/dev/null 2>&1; then
  KOTLIN_HOME="$(cd "$(dirname "$(command -v kotlinc)")/.." && pwd)"
fi
if [[ -z "$KOTLIN_HOME" || ! -x "$KOTLIN_HOME/bin/kotlinc" ]]; then
  echo "ERROR: kotlinc not found. Set KOTLIN_HOME to a Kotlin compiler distribution." >&2
  exit 2
fi

KOTLINC="$KOTLIN_HOME/bin/kotlinc"
KOTLIN_TEST_JAR="$KOTLIN_HOME/lib/kotlin-test.jar"
KOTLIN_TEST_JUNIT_JAR="$KOTLIN_HOME/lib/kotlin-test-junit.jar"
KOTLIN_STDLIB_JAR="$KOTLIN_HOME/lib/kotlin-stdlib.jar"
if [[ ! -f "$KOTLIN_TEST_JAR" ]]; then
  echo "ERROR: $KOTLIN_TEST_JAR missing from the Kotlin distribution." >&2
  exit 2
fi

mkdir -p "$BUILD_DIR/classes" "$BUILD_DIR/test-classes" "$BUILD_DIR/runner" "$BUILD_DIR/stubs"

# Compile order and inter-module dependencies mirror settings.gradle.kts.
MODULES=(model entitlements trade estimation quotation)
declare -A DEPS=(
  [model]=""
  [entitlements]=""
  [trade]="entitlements"
  [estimation]="model trade entitlements"
  [quotation]=""
)

classpath_for() {
  local module="$1" cp=""
  for dep in ${DEPS[$module]}; do
    cp="$cp:$BUILD_DIR/classes/core/$dep"
  done
  echo "${cp#:}"
}

{
  echo "SmartView Pro — core verification"
  echo "Date: $(date -u +"%Y-%m-%dT%H:%M:%SZ")"
  echo "Kotlin home: $KOTLIN_HOME"
  echo "Java: $(java -version 2>&1 | head -n 1)"
  echo

  echo "=== Compiling modules ==="
  for module in "${MODULES[@]}"; do
    src="$ROOT_DIR/core/$module/src/main/kotlin"
    out="$BUILD_DIR/classes/core/$module"
    mkdir -p "$out"
    cp_arg="$(classpath_for "$module")"
    echo "--- core:$module"
    if [[ -n "$cp_arg" ]]; then
      "$KOTLINC" -nowarn -classpath "$cp_arg" -d "$out" "$src"
    else
      "$KOTLINC" -nowarn -d "$out" "$src"
    fi
  done

  echo
  echo "=== Compiling test sources and runner ==="
  "$KOTLINC" -nowarn -d "$BUILD_DIR/runner" "$ROOT_DIR/tools/jvmtest/TestRunner.kt"

  # kotlin.test's JVM annotations alias org.junit.Test, so a stub is compiled when the
  # real JUnit jar is unavailable in this environment. See tools/jvmtest/stubs/.
  if [[ -f "$KOTLIN_TEST_JUNIT_JAR" ]]; then
    "$KOTLINC" -nowarn -d "$BUILD_DIR/stubs" "$ROOT_DIR/tools/jvmtest/stubs"
  else
    echo "ERROR: $KOTLIN_TEST_JUNIT_JAR missing; cannot resolve kotlin.test annotations." >&2
    exit 2
  fi

  TEST_CLASSES=()
  for module in "${MODULES[@]}"; do
    test_src="$ROOT_DIR/core/$module/src/test/kotlin"
    [[ -d "$test_src" ]] || continue
    out="$BUILD_DIR/test-classes/core/$module"
    mkdir -p "$out"
    cp_arg="$KOTLIN_TEST_JAR:$KOTLIN_TEST_JUNIT_JAR:$BUILD_DIR/stubs:$BUILD_DIR/classes/core/$module:$(classpath_for "$module")"
    echo "--- core:$module tests"
    "$KOTLINC" -nowarn -classpath "$cp_arg" -d "$out" "$test_src"

    while IFS= read -r file; do
      class_name="${file#"$out/"}"
      class_name="${class_name%.class}"
      class_name="${class_name//\//.}"
      TEST_CLASSES+=("$class_name")
    done < <(find "$out" -name '*Test.class' | sort)
  done

  if [[ ${#TEST_CLASSES[@]} -eq 0 ]]; then
    echo "ERROR: no test classes were discovered." >&2
    exit 3
  fi

  echo
  echo "=== Running ${#TEST_CLASSES[@]} test classes ==="
  RUN_CP="$BUILD_DIR/runner:$BUILD_DIR/stubs:$KOTLIN_TEST_JAR:$KOTLIN_STDLIB_JAR"
  for module in "${MODULES[@]}"; do
    RUN_CP="$RUN_CP:$BUILD_DIR/classes/core/$module:$BUILD_DIR/test-classes/core/$module"
  done

  java -cp "$RUN_CP" com.walnus.smartview.tooling.TestRunner "${TEST_CLASSES[@]}"
} 2>&1 | tee "$LOG_FILE"

status="${PIPESTATUS[0]}"
echo
if [[ "$status" -eq 0 ]]; then
  echo "VERIFIED: core modules compiled and all tests passed. Log: $LOG_FILE"
else
  echo "FAILED: core verification failed (exit $status). Log: $LOG_FILE" >&2
fi
exit "$status"
