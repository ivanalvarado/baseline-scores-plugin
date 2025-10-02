package com.ivanalvarado.baselinescoresplugin.application

import com.ivanalvarado.baselinescoresplugin.ConfigCacheCompatibleBaselineFileDetector
import com.ivanalvarado.baselinescoresplugin.ProjectInfo
import com.ivanalvarado.baselinescoresplugin.ExtensionConfig
import com.ivanalvarado.baselinescoresplugin.domain.BaselineScorer
import com.ivanalvarado.baselinescoresplugin.domain.ConsoleReporter

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

        printValidationHeader(extensionConfig)

        if (noBaselinesWereFound(baselineFiles)) {
            return createPassingValidationForNoBaselines(extensionConfig)
        }

        val moduleScores = baselineFiles.map { info ->
            baselineScorer.scoreBaselineWithFiles(info, scoringConfiguration)
        }

        val totalScore = moduleScores.sumOf { it.totalScore }
        val totalIssues = moduleScores.sumOf { it.totalIssues }
        val normalizedScore = calculateNormalizedScore(totalScore, totalIssues)
        val validationPassed =
            scoreMetsThreshold(normalizedScore, extensionConfig.minimumScoreThreshold)

        val validationMessage = buildValidationMessage(
            validationPassed,
            normalizedScore,
            extensionConfig.minimumScoreThreshold
        )

        printValidationResults(totalScore, totalIssues, normalizedScore, validationMessage)

        return ValidationResult(
            isValid = validationPassed,
            currentScore = totalScore,
            threshold = extensionConfig.minimumScoreThreshold,
            message = validationMessage
        )
    }

    private fun printValidationHeader(extensionConfig: ExtensionConfig) {
        println("Validating baseline scores...")
        println("Threshold: ${extensionConfig.minimumScoreThreshold}")
    }

    private fun noBaselinesWereFound(baselineFiles: List<com.ivanalvarado.baselinescoresplugin.BaselineFileInfo>): Boolean {
        return baselineFiles.isEmpty()
    }

    private fun createPassingValidationForNoBaselines(extensionConfig: ExtensionConfig): ValidationResult {
        val message = "No baseline files found - validation passed by default"
        println(message)

        return ValidationResult(
            isValid = true,
            currentScore = 0,
            threshold = extensionConfig.minimumScoreThreshold,
            message = message
        )
    }

    private fun scoreMetsThreshold(normalizedScore: Double, threshold: Double): Boolean {
        return normalizedScore >= threshold
    }

    private fun buildValidationMessage(
        validationPassed: Boolean,
        normalizedScore: Double,
        threshold: Double
    ): String {
        return if (validationPassed) {
            "Validation PASSED: Score $normalizedScore meets threshold $threshold"
        } else {
            "Validation FAILED: Score $normalizedScore below threshold $threshold"
        }
    }

    private fun printValidationResults(
        totalScore: Int,
        totalIssues: Int,
        normalizedScore: Double,
        message: String
    ) {
        println("\nValidation Results:")
        println("Total raw score: $totalScore")
        println("Total issues: $totalIssues")
        println("Normalized score: $normalizedScore")
        println(message)
    }

    private fun calculateNormalizedScore(totalScore: Int, totalIssues: Int): Double {
        return when {
            noIssuesWereFound(totalIssues) -> PERFECT_SCORE
            else -> calculateScoreWithPenalties(totalScore, totalIssues)
        }
    }

    private fun noIssuesWereFound(totalIssues: Int): Boolean {
        return totalIssues == 0
    }

    private fun calculateScoreWithPenalties(totalScore: Int, totalIssues: Int): Double {
        val penaltyFactor = totalScore.toDouble() / (totalIssues * SCORE_NORMALIZATION_FACTOR)
        val scoreWithPenalties = PERFECT_SCORE + penaltyFactor

        return maxOf(MINIMUM_SCORE, scoreWithPenalties)
    }

    private companion object {
        const val PERFECT_SCORE = 1.0
        const val MINIMUM_SCORE = 0.0
        const val SCORE_NORMALIZATION_FACTOR = 100
    }
}
