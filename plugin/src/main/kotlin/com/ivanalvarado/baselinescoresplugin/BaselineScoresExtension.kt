package com.ivanalvarado.baselinescoresplugin

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * DSL for configuring baseline scores calculation.
 *
 * This extension uses Gradle's Provider API for:
 * - Lazy evaluation of configuration values
 * - Better configuration caching support
 * - Improved build performance
 * - Type-safe property declarations
 *
 * This extension provides configuration options for:
 * - Which static analysis tools to include.
 * - Whether to use the default scoring rules.
 * - How to override scores for specific rules.
 * - File patterns and score thresholds.
 */
abstract class BaselineScoresExtension @Inject constructor(private val objects: ObjectFactory) {

    /** The output file for the baseline scores report. */
    abstract val outputFile: Property<String>

    /**
     * The minimum score threshold (from 0.0 to 1.0) that the project must meet.
     * If the calculated score falls below this value, the build will fail.
     */
    abstract val minimumScoreThreshold: Property<Double>

    /**
     * @deprecated Use `minimumScoreThreshold` instead.
     */
    @Deprecated("Use minimumScoreThreshold", ReplaceWith("minimumScoreThreshold"))
    var threshold: Double
        get() = minimumScoreThreshold.get()
        set(value) {
            require(value in 0.0..1.0) { "threshold must be between 0.0 and 1.0, but was $value" }
            minimumScoreThreshold.set(value)
        }

    /** Master switch to enable or disable all plugin tasks. */
    abstract val enabled: Property<Boolean>

    /** Glob patterns for files to include in analysis. */
    abstract val includePatterns: ListProperty<String>

    /** Glob patterns for files to exclude from analysis. */
    abstract val excludePatterns: ListProperty<String>

    // --- Tool-specific Toggles ---

    /** Enables or disables Detekt analysis. */
    abstract val detektEnabled: Property<Boolean>

    /**
     * Whether to use default scoring for Detekt issues.
     * Set to false to provide all scoring rules yourself.
     */
    abstract val useDefaultDetektScoring: Property<Boolean>

    /** The Detekt baseline file to read from. */
    abstract val detektBaselineFileName: Property<String>

    /** Enables or disables Android Lint analysis. */
    abstract val lintEnabled: Property<Boolean>

    /**
     * Whether to use default scoring for Lint issues.
     * Set to false to provide all scoring rules yourself.
     */
    abstract val useDefaultLintScoring: Property<Boolean>

    /** The Lint baseline file to read from. */
    abstract val lintBaselineFileName: Property<String>

    // --- Scoring Configuration ---

    /**
     * The default point value for any issue that does not have a specific score defined.
     * Used as a fallback.
     */
    abstract val defaultIssuePoints: Property<Int>

    /**
     * A map holding all user-defined scoring rules, which override default scores.
     */
    abstract val userScoringRules: MapProperty<String, Int>

    init {
        // Set default values
        outputFile.convention("baseline-scores.json")
        minimumScoreThreshold.convention(0.8)
        enabled.convention(true)
        includePatterns.convention(listOf("**/*.kt", "**/*.java"))
        excludePatterns.convention(listOf("**/test/**", "**/build/**"))
        detektEnabled.convention(true)
        useDefaultDetektScoring.convention(true)
        detektBaselineFileName.convention("detekt-baseline.xml")
        lintEnabled.convention(true)
        useDefaultLintScoring.convention(false) // Defaults for lint not implemented yet
        lintBaselineFileName.convention("lint-baseline.xml")
        defaultIssuePoints.convention(-5)
    }

    /**
     * Assign a point value to a specific static analysis issue by its type.
     * This overrides any default value.
     *
     * Example:
     *     issueScore("MagicNumber", -15)
     */
    fun issueScore(issueType: String, points: Int) {
        userScoringRules.put(issueType, points)
    }

    /**
     * Assign point values for multiple issue types at once.
     * These override any default values.
     */
    fun issueScores(scores: Map<String, Int>) {
        userScoringRules.putAll(scores)
    }
}
