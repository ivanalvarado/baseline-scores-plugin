package com.ivanalvarado.baselinescoresplugin

import com.ivanalvarado.baselinescoresplugin.config.DetektDefaultScores
import com.ivanalvarado.baselinescoresplugin.domain.ScoringConfiguration

/**
 * DSL for configuring baseline scores calculation.
 *
 * This extension provides configuration options for:
 * - Which static analysis tools to include.
 * - Whether to use the default scoring rules.
 * - How to override scores for specific rules.
 * - File patterns and score thresholds.
 */
open class BaselineScoresExtension {
    /** The output file for the baseline scores report. */
    var outputFile: String = "baseline-scores.json"

    /**
     * The minimum score threshold (from 0.0 to 1.0) that the project must meet.
     * If the calculated score falls below this value, the build will fail.
     */
    var minimumScoreThreshold: Double = 0.8
        set(value) {
            require(value in 0.0..1.0) { "minimumScoreThreshold must be between 0.0 and 1.0, but was $value" }
            field = value
        }

    /**
     * @deprecated Use `minimumScoreThreshold` instead.
     */
    @Deprecated("Use minimumScoreThreshold", ReplaceWith("minimumScoreThreshold"))
    var threshold: Double
        get() = minimumScoreThreshold
        set(value) {
            minimumScoreThreshold = value
        }

    /** Master switch to enable or disable all plugin tasks. */
    var enabled: Boolean = true

    /** Glob patterns for files to include in analysis. */
    var includePatterns: List<String> = listOf("**/*.kt", "**/*.java")

    /** Glob patterns for files to exclude from analysis. */
    var excludePatterns: List<String> = listOf("**/test/**", "**/build/**")

    // --- Tool-specific Toggles ---

    /** Enables or disables Detekt analysis. */
    var detektEnabled: Boolean = true

    /**
     * Whether to use default scoring for Detekt issues.
     * Set to false to provide all scoring rules yourself.
     */
    var useDefaultDetektScoring: Boolean = true

    /** The Detekt baseline file to read from. */
    var detektBaselineFileName: String = "detekt-baseline.xml"

    /** Enables or disables Android Lint analysis. */
    var lintEnabled: Boolean = true

    /**
     * Whether to use default scoring for Lint issues.
     * Set to false to provide all scoring rules yourself.
     */
    var useDefaultLintScoring: Boolean = false // Defaults for lint not implemented yet

    /** The Lint baseline file to read from. */
    var lintBaselineFileName: String = "lint-baseline.xml"

    // --- Scoring Configuration ---

    /**
     * The default point value for any issue that does not have a specific score defined.
     * Used as a fallback.
     */
    var defaultIssuePoints: Int = -5

    /**
     * A map holding all user-defined scoring rules, which override default scores.
     */
    private val userScoringRules = mutableMapOf<String, Int>()

    /**
     * Assign a point value to a specific static analysis issue by its type.
     * This overrides any default value.
     *
     * Example:
     *     issueScore("MagicNumber", -15)
     */
    fun issueScore(issueType: String, points: Int) {
        userScoringRules[issueType] = points
    }

    /**
     * Assign point values for multiple issue types at once.
     * These override any default values.
     */
    fun issueScores(scores: Map<String, Int>) {
        userScoringRules.putAll(scores)
    }

    /**
     * Build and merge the scoring configuration.
     *
     * The logic is as follows:
     *   1. Start with an empty set of rules.
     *   2. If Detekt is enabled and default Detekt scoring is enabled, add all defaults.
     *   3. If Lint is enabled and default Lint scoring is enabled, add all defaults (future).
     *   4. User scoring rules are applied last, overwriting any defaults.
     *
     * @return ScoringConfiguration with merged rules and fallback score.
     */
    fun getScoringConfiguration(): ScoringConfiguration {
        val mergedRules = mutableMapOf<String, Int>()

        if (detektEnabled && useDefaultDetektScoring) {
            mergedRules.putAll(DetektDefaultScores.rules)
        }

        if (lintEnabled && useDefaultLintScoring) {
            // Future: mergedRules.putAll(LintDefaultScores.rules)
        }

        mergedRules.putAll(userScoringRules)

        return ScoringConfiguration(
            rules = mergedRules,
            defaultPoints = defaultIssuePoints
        )
    }
}
