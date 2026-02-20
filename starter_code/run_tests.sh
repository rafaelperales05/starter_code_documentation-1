#!/bin/bash
#
# run_tests.sh — Compile and run all 5 tests for Lab 3
#
# Usage:
#   ./run_tests.sh              Run tests and compare against expected output
#   ./run_tests.sh --generate   Generate expected output from current code
#
# The first time you use this, run with --generate using the solution code
# to create the expected output files. After that, run without flags to
# test your implementation against those expected outputs.

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
TEST_DIR="$SCRIPT_DIR/tests"

# Compile all Java files
echo "Compiling..."
javac "$SCRIPT_DIR"/*.java 2>&1
if [ $? -ne 0 ]; then
    echo "COMPILATION FAILED"
    exit 1
fi
echo "Compilation successful."
echo ""

PASS_COUNT=0
FAIL_COUNT=0

if [ "$1" = "--generate" ]; then
    echo "=== Generating expected output files ==="
    for i in 1 2 3 4 5; do
        INPUT="$TEST_DIR/test${i}_input.txt"
        EXPECTED="$TEST_DIR/test${i}_expected.txt"
        echo "Generating test ${i}..."
        java -cp "$SCRIPT_DIR" Main "$INPUT" > "$EXPECTED" 2>&1
    done
    echo ""
    echo "Expected output files generated in $TEST_DIR/"
    echo "Review them to make sure they look correct, then run"
    echo "  ./run_tests.sh"
    echo "to test against them."
    exit 0
fi

echo "=== Running Tests ==="
echo ""

for i in 1 2 3 4 5; do
    INPUT="$TEST_DIR/test${i}_input.txt"
    EXPECTED="$TEST_DIR/test${i}_expected.txt"
    ACTUAL="$TEST_DIR/test${i}_actual.txt"

    # Determine test description
    case $i in
        1) DESC="Basic commands, entity creation, and invalid input" ;;
        2) DESC="World display and border formatting" ;;
        3) DESC="Population dynamics over 100 steps" ;;
        4) DESC="Custom entities (Commander, Engineer)" ;;
        5) DESC="Error handling for malformed commands" ;;
    esac

    printf "Test %d: %s... " "$i" "$DESC"

    if [ ! -f "$EXPECTED" ]; then
        echo "SKIP (no expected output — run with --generate first)"
        continue
    fi

    # Run the program with the test input
    java -cp "$SCRIPT_DIR" Main "$INPUT" > "$ACTUAL" 2>&1

    # Compare
    if diff -q "$EXPECTED" "$ACTUAL" > /dev/null 2>&1; then
        echo "PASSED"
        PASS_COUNT=$((PASS_COUNT + 1))
        rm -f "$ACTUAL"
    else
        echo "FAILED"
        FAIL_COUNT=$((FAIL_COUNT + 1))
        echo "  Differences (expected vs actual):"
        diff --color=auto "$EXPECTED" "$ACTUAL" | head -20
        echo "  (Full output saved to $ACTUAL)"
        echo ""
    fi
done

echo ""
echo "=== Results: $PASS_COUNT passed, $FAIL_COUNT failed ==="

# Clean up .class files
rm -f "$SCRIPT_DIR"/*.class

exit $FAIL_COUNT
