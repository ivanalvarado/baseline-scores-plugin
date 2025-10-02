package com.ivanalvarado.baselinescoresplugin.application

import com.ivanalvarado.baselinescoresplugin.ConfigCacheCompatibleBaselineFileDetector
import com.ivanalvarado.baselinescoresplugin.ProjectInfo
import com.ivanalvarado.baselinescoresplugin.ExtensionConfig
import com.ivanalvarado.baselinescoresplugin.config.DetektDefaultScores
import com.ivanalvarado.baselinescoresplugin.domain.BaselineScorer
import com.ivanalvarado.baselinescoresplugin.domain.ConsoleReporter
import com.ivanalvarado.baselinescoresplugin.domain.ScoringConfiguration

data class ValidationResult(
    val isValid: Boolean,
    val currentScore: Int,
    val threshold: Double,
    val message: String
)

class ValidateBaselineScoresUseCase(
    private val baselineFileDetector: ConfigCacheCompatibleBaselineFileDetector,
    private val baselineScorer: BaselineScorer,
    private val consoleReporter: ConsoleReporter
) {

    fun execute(projectInfo: ProjectInfo, extensionConfig: ExtensionConfig): ValidationResult {
        val baselineFiles = baselineFileDetector.findAllBaselineFiles(projectInfo, extensionConfig)
        val scoringConfiguration = extensionConfig.toScoringConfiguration()

        println("Validating baseline scores...")
        println("Threshold: ${extensionConfig.minimumScoreThreshold}")

        if (baselineFiles.isEmpty()) {
            val message = "No baseline files found - validation passed by default"
            println(message)
            return ValidationResult(true, 0, extensionConfig.minimumScoreThreshold, message)
        }

        val moduleScores = baselineFiles.map { info ->
            baselineScorer.scoreBaselineWithFiles(info, scoringConfiguration)
        }

        val totalScore = moduleScores.sumOf { it.totalScore }
        val normalizedScore =
            calculateNormalizedScore(totalScore, moduleScores.sumOf { it.totalIssues })
        val isValid = normalizedScore >= extensionConfig.minimumScoreThreshold

        val message = if (isValid) {
            "Validation PASSED: Score $normalizedScore meets threshold ${extensionConfig.minimumScoreThreshold}"
        } else {
            "Validation FAILED: Score $normalizedScore below threshold ${extensionConfig.minimumScoreThreshold}"
        }

        println("\nValidation Results:")
        println("Total raw score: $totalScore")
        println("Total issues: ${moduleScores.sumOf { it.totalIssues }}")
        println("Normalized score: $normalizedScore")
        println(message)

        return ValidationResult(isValid, totalScore, extensionConfig.minimumScoreThreshold, message)
    }

    private fun calculateNormalizedScore(totalScore: Int, totalIssues: Int): Double {
        // Convert negative scores to a 0-1 scale where 0 issues = 1.0
        // This is a simple normalization - could be made more sophisticated
        return if (totalIssues == 0) 1.0 else maxOf(
            0.0,
            1.0 + (totalScore.toDouble() / (totalIssues * 100))
        )
    }
}
