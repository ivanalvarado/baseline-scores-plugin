# Baseline Scores Plugin

A Gradle plugin that assigns scores to each file that has baselined issues in specific gradle
modules of a project. This plugin helps track technical debt by analyzing baseline files from
linting tools like Detekt and Android Lint.

## Features

- **Detekt Baseline Detection**: Automatically finds `detekt-baseline.xml` files in project modules
- **Android Lint Baseline Detection**: Automatically finds `lint-baseline.xml` files in project
  modules
- **Multi-Module Support**: Scans root project and all subprojects for baseline files
- **Configurable Scoring**: Customize scoring rules for different issue types with severity-based
  penalties
- **Smart Defaults**: Pre-configured scoring rules for common Detekt issues
- **Separate Scoring**: Maintains separate scores for Detekt and Lint issues

## Quick Start

### Apply the Plugin

```kotlin
plugins {
    id("com.ivanalvarado.baseline-scores")
}
```

### Basic Configuration

```kotlin
baselineScores {
    outputFile = "baseline-scores.json"
    threshold = 0.8
    
    // Detekt configuration
    detektEnabled = true
    detektBaselineFileName = "detekt-baseline.xml"
    
    // Android Lint configuration
    lintEnabled = true
    lintBaselineFileName = "lint-baseline.xml"
    
    // Scoring configuration
    defaultIssuePoints = -5
    issueScore("ComplexMethod", -20)
    issueScore("UnsafeCallOnNullableType", -50)
}
```

## Available Tasks

### `findBaselineFiles`

Discovers all baseline files in the current project and its subprojects.

```bash
./gradlew findBaselineFiles
```

**Example Output:**

```
Found 3 baseline file(s):
  [DETEKT] app: /path/to/project/app/detekt-baseline.xml
  [LINT] app: /path/to/project/app/lint-baseline.xml
  [DETEKT] feature-login: /path/to/project/feature-login/detekt-baseline.xml
```

### `generateBaselineScores`

Generates baseline scores for all discovered baseline files and outputs results to JSON.

```bash
./gradlew generateBaselineScores
```

For specific modules:

```bash
./gradlew :feature:bookmarks:generateBaselineScores
```

**Console Output:**

```
> Task :feature:bookmarks:generateBaselineScores
Generating baseline scores for 1 baseline file(s)...
Default issue points: -5

[DETEKT] Module: bookmarks
  Total issues: 14, Total score: -130
  File breakdown:
    BookmarksScreen.kt:
      FunctionNaming: 8 issues × -10 points = -80
      UnusedPrivateMember: 3 issues × -5 points = -15
      LongParameterList: 1 issues × -20 points = -20
    BookmarksViewModel.kt:
      MagicNumber: 1 issues × -10 points = -10
    BookmarksNavigation.kt:
      MatchingDeclarationName: 1 issues × -5 points = -5

==================================================
PROJECT SUMMARY
==================================================
Total project score: -130
Total modules analyzed: 1
Total issues: 14

Most common issues:
  FunctionNaming: 8 occurrences
  UnusedPrivateMember: 3 occurrences
  LongParameterList: 1 occurrences
  MagicNumber: 1 occurrences
  MatchingDeclarationName: 1 occurrences
Output file: /Users/ivanalvarado/Developer/nowinandroid/feature/bookmarks/build/baseline-scores/baseline-scores-results.json
```

**JSON Output Location:**

- `{module}/build/baseline-scores/baseline-scores-results.json`

**Example JSON Output:**

For a baseline file containing:

```xml
<?xml version="1.0" ?>
<SmellBaseline>
  <CurrentIssues>
    <ID>FunctionNaming:BookmarksScreen.kt$@Composable internal fun BookmarksRoute(...)</ID>
    <ID>FunctionNaming:BookmarksScreen.kt$@Composable private fun BookmarksGrid(...)</ID>
    <ID>FunctionNaming:BookmarksScreen.kt$@Composable private fun EmptyState(...)</ID>
    <ID>FunctionNaming:BookmarksScreen.kt$@Composable private fun LoadingState(...)</ID>
    <ID>FunctionNaming:BookmarksScreen.kt$@Preview @Composable private fun BookmarksGridPreview(...)</ID>
    <ID>FunctionNaming:BookmarksScreen.kt$@Preview @Composable private fun EmptyStatePreview()</ID>
    <ID>FunctionNaming:BookmarksScreen.kt$@Preview @Composable private fun LoadingStatePreview()</ID>
    <ID>FunctionNaming:BookmarksScreen.kt$@VisibleForTesting @Composable internal fun BookmarksScreen(...)</ID>
    <ID>LongParameterList:BookmarksScreen.kt$( feedState: NewsFeedUiState, ... )</ID>
    <ID>MagicNumber:BookmarksViewModel.kt$BookmarksViewModel$5_000</ID>
  </CurrentIssues>
</SmellBaseline>
```

The plugin generates:

```json
{
  "generatedAt": "2025-01-07T15:30:45.123456",
  "projectTotalScore": -79,
  "totalIssues": 10,
  "results": [
    {
      "class": "BookmarksScreen.kt",
      "issues": [
        {
          "issue": "FunctionNaming",
          "occurrences": 8,
          "debt": -5,
          "score": -40
        },
        {
          "issue": "LongParameterList",
          "occurrences": 1,
          "debt": -10,
          "score": -10
        }
      ]
    },
    {
      "class": "BookmarksViewModel.kt",
      "issues": [
        {
          "issue": "MagicNumber",
          "occurrences": 1,
          "debt": -3,
          "score": -3
        }
      ]
    }
  ]
}
```

**JSON Structure Explanation:**

- `generatedAt`: Timestamp when the report was generated
- `projectTotalScore`: Sum of all issue scores across all files
- `totalIssues`: Total number of individual issues found
- `results`: Array of files with their associated issues
  - `class`: File name where issues were found
  - `issues`: Array of issue types in that file
    - `issue`: Type of issue (e.g., "FunctionNaming")
    - `occurrences`: Number of times this issue appears in this file
    - `debt`: Points deducted per occurrence (negative value)
    - `score`: Total points for this issue type in this file (occurrences × debt)

### `validateBaselineScores`

Validates current scores against baseline thresholds.

```bash
./gradlew validateBaselineScores
```

## Multi-Module Behavior

The plugin now operates on the project where the task is executed:

- **Root project execution**: `./gradlew generateBaselineScores` processes baseline files in the 
  all its subprojects
- **Submodule execution**: `./gradlew :feature:bookmarks:generateBaselineScores` processes only
  baseline files in the `feature:bookmarks` module and its subprojects
- **Output location**: JSON results are always written to the `build` directory of the project where
  the task was executed

## Configuration

### Basic Configuration Options

| Property                 | Default                         | Description                            |
|--------------------------|---------------------------------|----------------------------------------|
| `outputFile`             | `"baseline-scores.json"`        | Output file for generated scores       |
| `threshold`              | `0.8`                           | Score threshold for validation         |
| `enabled`                | `true`                          | Enable/disable the plugin              |
| `includePatterns`        | `["**/*.kt", "**/*.java"]`      | File patterns to include               |
| `excludePatterns`        | `["**/test/**", "**/build/**"]` | File patterns to exclude               |
| `detektEnabled`          | `true`                          | Enable Detekt baseline detection       |
| `detektBaselineFileName` | `"detekt-baseline.xml"`         | Detekt baseline file name              |
| `lintEnabled`            | `true`                          | Enable Android Lint baseline detection |
| `lintBaselineFileName`   | `"lint-baseline.xml"`           | Lint baseline file name                |
| `defaultIssuePoints`     | `-5`                            | Default score for any issue type       |

### Custom Scoring Rules

#### Individual Issue Configuration

```kotlin
baselineScores {
    defaultIssuePoints = -5
    
    // Configure individual issue types
    issueScore("FunctionNaming", -3)
    issueScore("ComplexMethod", -20)
    issueScore("UnsafeCallOnNullableType", -50)
}
```

#### Bulk Issue Configuration

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

#### Severity-Based Configuration

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
        "FunctionNaming" to -1,
        "MagicNumber" to -1
    ))
}
```

#### Progressive Improvement Strategy

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

## Default Scoring Rules

The plugin comes with sensible defaults for common Detekt issues:

| Issue Type                            | Default Points | Reasoning                     |
|---------------------------------------|----------------|-------------------------------|
| `UnsafeCallOnNullableType`            | -20            | Security/safety concern       |
| `ExceptionRaisedInUnexpectedLocation` | -25            | Critical error handling issue |
| `ComplexMethod`                       | -15            | Major maintainability issue   |
| `LongMethod`                          | -10            | Maintainability issue         |
| `LongParameterList`                   | -10            | API design issue              |
| `UnusedPrivateMember`                 | -7             | Dead code                     |
| `FunctionNaming`                      | -5             | Style/convention              |
| `MagicNumber`                         | -3             | Code clarity                  |
| `UnnecessaryLet`                      | -3             | Code clarity                  |

You can override any of these defaults using the configuration methods above.

## Baseline File Detection

The plugin automatically detects baseline files using these strategies:

### Detekt Baselines

1. **Plugin Configuration**: Checks for custom baseline path in `detekt { baseline = ... }`
2. **Default Location**: Looks for `detekt-baseline.xml` in module root directory
3. **Name Pattern**: Only detects files containing "detekt" in the filename

### Android Lint Baselines

1. **Plugin Configuration**: Checks for custom baseline path in
   `android.lintOptions { baseline = ... }`
2. **Default Location**: Looks for `lint-baseline.xml` in module root directory
3. **Name Pattern**: Only detects files containing "lint" in the filename

## Why Separate Scores?

The plugin maintains separate scores for Detekt and Android Lint because:

- **Different Scope**: Detekt analyzes Kotlin code style/quality, while Lint focuses on
  Android-specific issues
- **Independent Progress**: Teams may want to track improvements in code quality vs Android best
  practices separately
- **Different Priorities**: Some teams prioritize fixing Android issues over code style, or vice
  versa
- **Tool-Specific Goals**: Different quality gates and thresholds may apply to each tool

## Development

### Building the Plugin

```bash
./gradlew build
```

### Running Tests

```bash
./gradlew test
```

### Publishing locally

```bash
./gradlew publishToMavenLocal
```
