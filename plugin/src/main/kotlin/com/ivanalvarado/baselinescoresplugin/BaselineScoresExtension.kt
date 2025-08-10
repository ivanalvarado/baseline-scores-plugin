package com.ivanalvarado.baselinescoresplugin

import com.ivanalvarado.baselinescoresplugin.config.DetektDefaultScores
import com.ivanalvarado.baselinescoresplugin.domain.ScoringConfiguration

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

    // Scoring configuration
    var defaultIssuePoints: Int = -5
    private val scoringRules = mutableMapOf<String, Int>()

    /**
     * Configure scoring for a specific issue type
     */
    fun issueScore(issueType: String, points: Int) {
        scoringRules[issueType] = points
    }

    /**
     * Configure scoring for multiple issue types
     */
    fun issueScores(scores: Map<String, Int>) {
        scoringRules.putAll(scores)
    }

    /**
     * Get the scoring configuration
     */
    fun getScoringConfiguration(): ScoringConfiguration {
        return ScoringConfiguration(
            rules = DetektDefaultScores.rules + scoringRules,
            defaultPoints = defaultIssuePoints
        )
    }
}
