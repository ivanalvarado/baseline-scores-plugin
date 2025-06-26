package com.ivanalvarado.baselinescoresplugin.domain

import com.ivanalvarado.baselinescoresplugin.BaselineType

/**
 * Represents a scoring rule for a specific issue type
 */
data class ScoringRule(
    val issueType: String,
    val points: Int
)

/**
 * Configuration for scoring rules
 */
data class ScoringConfiguration(
    val rules: Map<String, Int>,
    val defaultPoints: Int = -5
) {
    fun getPointsForIssue(issueType: String): Int {
        return rules[issueType] ?: defaultPoints
    }
}

/**
 * Represents the result of scoring a baseline file
 */
data class ScoringResult(
    val module: String,
    val type: BaselineType,
    val issueBreakdown: Map<String, IssueScore>,
    val totalScore: Int,
    val totalIssues: Int
)

/**
 * Represents the score for a specific issue type
 */
data class IssueScore(
    val issueType: String,
    val count: Int,
    val pointsPerIssue: Int,
    val totalPoints: Int
)
