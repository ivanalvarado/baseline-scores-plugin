package com.ivanalvarado.baselinescoresplugin

import java.io.File
import com.ivanalvarado.baselinescoresplugin.config.DetektDefaultScores
import com.ivanalvarado.baselinescoresplugin.domain.ScoringConfiguration

/**
 * Contains project information extracted from Gradle Project for configuration cache compatibility.
 */
data class ProjectInfo(
    val name: String,
    val path: String,
    val projectDir: File,
    val buildDir: File,
    val subprojects: List<SubprojectInfo>
) : java.io.Serializable

/**
 * Contains subproject information extracted from Gradle Project for configuration cache compatibility.
 */
data class SubprojectInfo(
    val name: String,
    val path: String,
    val projectDir: File,
    val buildDir: File,
    val hasDetektPlugin: Boolean,
    val hasAndroidPlugin: Boolean
) : java.io.Serializable

/**
 * Contains extension configuration extracted from BaselineScoresExtension for configuration cache compatibility.
 */
data class ExtensionConfig(
    val detektEnabled: Boolean,
    val lintEnabled: Boolean,
    val detektBaselineFileName: String,
    val lintBaselineFileName: String,
    val defaultIssuePoints: Int = 1,
    val minimumScoreThreshold: Double = 0.8,
    val useDefaultDetektScoring: Boolean = true,
    val useDefaultLintScoring: Boolean = false,
    val userScoringRules: Map<String, Int> = emptyMap()
) : java.io.Serializable {

    /**
     * Builds a ScoringConfiguration by merging default scoring rules with user overrides.
     *
     * The merge strategy prioritizes user rules over defaults:
     * 1. Start with default Detekt rules (if enabled)
     * 2. Add default Lint rules (if enabled and available)
     * 3. Apply user rules last, overriding any defaults
     */
    fun toScoringConfiguration(): ScoringConfiguration {
        val mergedRules = buildMergedScoringRules()

        return ScoringConfiguration(
            rules = mergedRules,
            defaultPoints = defaultIssuePoints
        )
    }

    private fun buildMergedScoringRules(): Map<String, Int> {
        val rules = mutableMapOf<String, Int>()

        if (shouldIncludeDetektDefaultScores()) {
            rules.putAll(DetektDefaultScores.rules)
        }

        if (shouldIncludeLintDefaultScores()) {
            // Future: rules.putAll(LintDefaultScores.rules)
        }

        // User rules take precedence over defaults
        rules.putAll(userScoringRules)

        return rules
    }

    private fun shouldIncludeDetektDefaultScores(): Boolean {
        return detektEnabled && useDefaultDetektScoring
    }

    private fun shouldIncludeLintDefaultScores(): Boolean {
        return lintEnabled && useDefaultLintScoring
    }
}
