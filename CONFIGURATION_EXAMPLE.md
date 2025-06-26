# Baseline Scores Plugin Configuration Examples

## Basic Configuration

```kotlin
baselineScores {
    // Output configuration
    outputFile = "baseline-scores.json"
    
    // Default score for any issue type not explicitly configured
    defaultIssuePoints = -5
    
    // Enable/disable specific baseline types
    detektEnabled = true
    lintEnabled = true
    
    // Custom baseline file names (optional)
    detektBaselineFileName = "detekt-baseline.xml"
    lintBaselineFileName = "lint-baseline.xml"
}
```

## Custom Scoring Rules

### Individual Issue Configuration

```kotlin
baselineScores {
    defaultIssuePoints = -5
    
    // Configure individual issue types
    issueScore("FunctionNaming", -3)
    issueScore("ComplexMethod", -20)
    issueScore("UnsafeCallOnNullableType", -50)
}
```

### Bulk Issue Configuration

```kotlin
baselineScores {
    defaultIssuePoints = -5
    
    // Configure multiple issues at once
    issueScores(mapOf(
        "FunctionNaming" to -3,
        "LongParameterList" to -10,
        "MagicNumber" to -2,
        "UnusedPrivateMember" to -7,
        "ComplexMethod" to -15
    ))
}
```

## Severity-Based Configuration

```kotlin
baselineScores {
    defaultIssuePoints = -5
    
    // Critical issues (security, potential bugs)
    issueScores(mapOf(
        "UnsafeCallOnNullableType" to -50,
        "ExceptionRaisedInUnexpectedLocation" to -30,
        "TooGenericExceptionCaught" to -15
    ))
    
    // Major issues (maintainability problems)
    issueScores(mapOf(
        "ComplexMethod" to -20,
        "LongMethod" to -15,
        "LargeClass" to -18,
        "TooManyFunctions" to -12
    ))
    
    // Minor issues (style and convention)
    issueScores(mapOf(
        "FunctionNaming" to -3,
        "MagicNumber" to -2,
        "UnnecessaryLet" to -1,
        "EmptyFunctionBlock" to -2
    ))
}
```

## Progressive Improvement Strategy

```kotlin
baselineScores {
    // Start with very low penalties for gradual improvement
    defaultIssuePoints = -1
    
    // Phase 1: Focus only on critical issues
    issueScores(mapOf(
        "UnsafeCallOnNullableType" to -100,  // Blocking
        "ExceptionRaisedInUnexpectedLocation" to -100,
        "NullPointerException" to -50
    ))
    
    // Phase 2: Address major maintainability issues (lower penalties)
    issueScores(mapOf(
        "ComplexMethod" to -10,
        "LongMethod" to -8,
        "LargeClass" to -12
    ))
    
    // Phase 3: Style issues get minimal penalties
    issueScores(mapOf(
        "FunctionNaming" to -1,
        "MagicNumber" to -1
    ))
}
```

## Example Output

When you run `./gradlew generateBaselineScores`, you'll see output like:

```
Generating baseline scores for 2 baseline file(s)...
Default issue points: -5

[DETEKT] Module: app
  Total issues: 15, Total score: -89
  Issue breakdown:
    FunctionNaming: 8 issues × -5 points = -40
    LongParameterList: 2 issues × -10 points = -20
    MagicNumber: 3 issues × -3 points = -9
    UnusedPrivateMember: 2 issues × -7 points = -14

[DETEKT] Module: core
  Total issues: 5, Total score: -35
  Issue breakdown:
    ComplexMethod: 1 issues × -15 points = -15
    LongMethod: 2 issues × -10 points = -20

==================================================
PROJECT SUMMARY
==================================================
Total project score: -124
Total modules analyzed: 2
Total issues: 20

Most common issues:
  FunctionNaming: 8 occurrences
  MagicNumber: 3 occurrences
  LongMethod: 2 occurrences
  LongParameterList: 2 occurrences
  UnusedPrivateMember: 2 occurrences

Output file: baseline-scores.json
```

## Default Scoring Rules

The plugin comes with sensible defaults for common Detekt issues:

| Issue Type | Default Points | Reasoning |
|------------|----------------|-----------|
| `UnsafeCallOnNullableType` | -20 | Security/safety concern |
| `ExceptionRaisedInUnexpectedLocation` | -25 | Critical error handling issue |
| `ComplexMethod` | -15 | Major maintainability issue |
| `LongMethod` | -10 | Maintainability issue |
| `LongParameterList` | -10 | API design issue |
| `UnusedPrivateMember` | -7 | Dead code |
| `FunctionNaming` | -5 | Style/convention |
| `MagicNumber` | -3 | Code clarity |
| `UnnecessaryLet` | -3 | Code clarity |

You can override any of these defaults using the configuration methods above.
