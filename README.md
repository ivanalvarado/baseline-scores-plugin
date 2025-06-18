# Baseline Scores Plugin

A Gradle plugin for managing baseline scores in your projects.

## Usage

### Apply the plugin

```kotlin
plugins {
    id("com.ivanalvarado.baseline-scores") version "1.0.0"
}
```

### Configure the plugin

```kotlin
baselineScores {
    outputFile = "custom-baseline.json"
    threshold = 0.85
    enabled = true
    includePatterns = listOf("**/*.kt", "**/*.java")
    excludePatterns = listOf("**/test/**", "**/build/**")
}
```

### Available tasks

- `generateBaselineScores` - Generate baseline scores for the project
- `validateBaselineScores` - Validate current scores against baseline

## Configuration

| Property | Default | Description |
|----------|---------|-------------|
| `outputFile` | `baseline-scores.json` | Output file for baseline scores |
| `threshold` | `0.8` | Minimum threshold for validation |
| `enabled` | `true` | Enable/disable plugin functionality |
| `includePatterns` | `["**/*.kt", "**/*.java"]` | File patterns to include |
| `excludePatterns` | `["**/test/**", "**/build/**"]` | File patterns to exclude |

## Development

### Building the plugin

```bash
./gradlew build
```

### Running tests

```bash
./gradlew test
```

### Publishing locally

```bash
./gradlew publishToMavenLocal
```
