# Baseline Scores Plugin

A Gradle plugin that assigns scores to each file that has baselined issues in specific gradle
modules of a project. This plugin helps track technical debt by analyzing baseline files from
linting tools like Detekt and Android Lint.

## Features

- **Detekt Baseline Detection**: Automatically finds `detekt-baseline.xml` files in project modules
- **Android Lint Baseline Detection**: Automatically finds `lint-baseline.xml` files in project
  modules
- **Multi-Module Support**: Scans root project and all subprojects for baseline files
- **Configurable**: Enable/disable detection for specific tools and customize baseline file names
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
}
```

## Available Tasks

### `findBaselineFiles`

Discovers all baseline files in the project and its modules.

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

Generates baseline scores for all discovered baseline files.

```bash
./gradlew generateBaselineScores
```

### `validateBaselineScores`

Validates current scores against baseline thresholds.

```bash
./gradlew validateBaselineScores
```

## Configuration Options

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

