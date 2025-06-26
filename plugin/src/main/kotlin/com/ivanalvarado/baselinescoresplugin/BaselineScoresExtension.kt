package com.ivanalvarado.baselinescoresplugin

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
            rules = getDefaultScoringRules() + scoringRules,
            defaultPoints = defaultIssuePoints
        )
    }

    /**
     * Default scoring rules for common detekt issues
     */
    private fun getDefaultScoringRules(): Map<String, Int> {
        return mapOf(
            "FunctionNaming" to -5,
            "LongParameterList" to -10,
            "MagicNumber" to -3,
            "MatchingDeclarationName" to -5,
            "UnusedPrivateMember" to -7,
            "ComplexMethod" to -15,
            "LongMethod" to -10,
            "TooManyFunctions" to -12,
            "LargeClass" to -15,
            "EmptyFunctionBlock" to -5,
            "UnnecessaryApply" to -3,
            "UnsafeCallOnNullableType" to -20,
            "LateinitUsage" to -8,
            "ForEachOnRange" to -5,
            "SpreadOperator" to -5,
            "UnnecessaryLet" to -3,
            "DataClassContainsFunctions" to -8,
            "UseDataClass" to -5,
            "ExceptionRaisedInUnexpectedLocation" to -25,
            "TooGenericExceptionCaught" to -10
        )
    }
}
