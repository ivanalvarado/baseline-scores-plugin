package com.ivanalvarado.baselinescoresplugin

open class BaselineScoresExtension {
    var outputFile: String = "baseline-scores.json"
    var threshold: Double = 0.8
    var enabled: Boolean = true
    var includePatterns: List<String> = listOf("**/*.kt", "**/*.java")
    var excludePatterns: List<String> = listOf("**/test/**", "**/build/**")

    // Detekt-specific configuration
    var detektEnabled: Boolean = true
    var detektBaselineFileName: String = "detekt-baseline.xml"

    // Android Lint-specific configuration
    var lintEnabled: Boolean = true
    var lintBaselineFileName: String = "lint-baseline.xml"
}
