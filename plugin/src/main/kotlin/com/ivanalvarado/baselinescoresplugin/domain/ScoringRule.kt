package com.ivanalvarado.baselinescoresplugin.domain

import com.ivanalvarado.baselinescoresplugin.BaselineType

/**
 * Represents a scoring rule for a specific issue type.
 *
 * This value object encapsulates the business rule for how many points
 * should be deducted for a specific type of code quality issue.
 *
 * @property issueType The type of issue (e.g., "FunctionNaming", "ComplexMethod")
 * @property points The number of points to deduct (typically negative)
 */
data class ScoringRule(
    val issueType: String,
    val points: Int
) {
    init {
        require(issueType.isNotBlank()) { "Issue type cannot be blank" }
    }
}

/**
 * Configuration for scoring rules.
 *
 * This class manages the mapping between issue types and their corresponding
 * point values, providing a centralized configuration for scoring logic.
 *
 * @property rules Map of issue types to their point values
 * @property defaultPoints Default points to assign when no specific rule exists
 */
data class ScoringConfiguration(
    val rules: Map<String, Int>,
    val defaultPoints: Int = -5
) {
    init {
        require(rules.keys.all { it.isNotBlank() }) { "Issue type keys cannot be blank" }
    }

    /**
     * Gets the point value for a specific issue type.
     *
     * @param issueType The type of issue to get points for
     * @return The point value for the issue type, or defaultPoints if not found
     */
    fun getPointsForIssue(issueType: String): Int {
        return rules[issueType] ?: defaultPoints
    }
}

/**
 * Represents the result of scoring a baseline file.
 *
 * This class contains the complete scoring breakdown for a single module,
 * including detailed information about each issue type found.
 *
 * @property module The name of the module that was scored
 * @property type The type of baseline file (DETEKT, LINT, etc.)
 * @property issueBreakdown Detailed breakdown of issues by type
 * @property totalScore The sum of all issue scores
 * @property totalIssues The count of all issues found
 */
data class ScoringResult(
    val module: String,
    val type: BaselineType,
    val issueBreakdown: Map<String, IssueScore>,
    val totalScore: Int,
    val totalIssues: Int
) {
    init {
        require(module.isNotBlank()) { "Module name cannot be blank" }
        require(totalIssues >= 0) { "Total issues cannot be negative" }
    }
}

/**
 * Represents file-based scoring results with issues grouped by file name.
 *
 * This class provides a more detailed view of scoring results, breaking down
 * issues by the specific files where they occur.
 *
 * @property module The name of the module that was scored
 * @property type The type of baseline file (DETEKT, LINT, etc.)
 * @property fileBreakdown Issues grouped by file name, then by issue type
 * @property totalScore The sum of all issue scores across all files
 * @property totalIssues The count of all issues found across all files
 */
data class FileScoringResult(
    val module: String,
    val type: BaselineType,
    val fileBreakdown: Map<String, Map<String, IssueScore>>,
    val totalScore: Int,
    val totalIssues: Int
) {
    init {
        require(module.isNotBlank()) { "Module name cannot be blank" }
        require(totalIssues >= 0) { "Total issues cannot be negative" }
    }
}

/**
 * Represents the score for a specific issue type.
 *
 * This value object encapsulates all the information needed to understand
 * how a specific issue type contributes to the overall score.
 *
 * @property issueType The type of issue (e.g., "FunctionNaming")
 * @property count The number of occurrences of this issue type
 * @property pointsPerIssue The points deducted per occurrence
 * @property totalPoints The total points for this issue type (count × pointsPerIssue)
 */
data class IssueScore(
    val issueType: String,
    val count: Int,
    val pointsPerIssue: Int,
    val totalPoints: Int
) {
    init {
        require(issueType.isNotBlank()) { "Issue type cannot be blank" }
        require(count >= 0) { "Count cannot be negative" }
        require(totalPoints == count * pointsPerIssue) { "Total points must equal count × pointsPerIssue" }
    }
}
