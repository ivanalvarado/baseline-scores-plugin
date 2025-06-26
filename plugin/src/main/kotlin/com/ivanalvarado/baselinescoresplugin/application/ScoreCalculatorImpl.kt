package com.ivanalvarado.baselinescoresplugin.application

import com.ivanalvarado.baselinescoresplugin.BaselineType
import com.ivanalvarado.baselinescoresplugin.domain.IssueScore
import com.ivanalvarado.baselinescoresplugin.domain.ScoreCalculator
import com.ivanalvarado.baselinescoresplugin.domain.ScoringConfiguration
import com.ivanalvarado.baselinescoresplugin.domain.ScoringResult

class ScoreCalculatorImpl : ScoreCalculator {

    override fun calculateScore(
        issueBreakdown: Map<String, Int>,
        configuration: ScoringConfiguration,
        module: String,
        type: BaselineType
    ): ScoringResult {
        val issueScores = mutableMapOf<String, IssueScore>()
        var totalScore = 0
        var totalIssues = 0

        issueBreakdown.forEach { (issueType, count) ->
            val pointsPerIssue = configuration.getPointsForIssue(issueType)
            val totalPoints = count * pointsPerIssue

            issueScores[issueType] = IssueScore(
                issueType = issueType,
                count = count,
                pointsPerIssue = pointsPerIssue,
                totalPoints = totalPoints
            )

            totalScore += totalPoints
            totalIssues += count
        }

        return ScoringResult(
            module = module,
            type = type,
            issueBreakdown = issueScores,
            totalScore = totalScore,
            totalIssues = totalIssues
        )
    }
}
