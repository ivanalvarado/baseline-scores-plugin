package com.ivanalvarado.baselinescoresplugin.application

import com.ivanalvarado.baselinescoresplugin.BaselineFileDetector
import com.ivanalvarado.baselinescoresplugin.BaselineScoresExtension
import com.ivanalvarado.baselinescoresplugin.domain.BaselineScorer
import com.ivanalvarado.baselinescoresplugin.domain.ConsoleReporter
import org.gradle.api.Project

data class ValidationResult(
    val isValid: Boolean,
    val currentScore: Int,
    val threshold: Double,
    val message: String
)

class ValidateBaselineScoresUseCase(
    private val baselineFileDetector: BaselineFileDetector,
    private val baselineScorer: BaselineScorer,
    private val consoleReporter: ConsoleReporter
) {

    fun execute(project: Project, extension: BaselineScoresExtension): ValidationResult {
        val baselineFiles = baselineFileDetector.findAllBaselineFiles(project, extension)
        val scoringConfiguration = extension.getScoringConfiguration()

        println("Validating baseline scores...")
        println("Threshold: ${extension.minimumScoreThreshold.get()}")

        if (baselineFiles.isEmpty()) {
            val message = "No baseline files found - validation passed by default"
            println(message)
            return ValidationResult(true, 0, extension.minimumScoreThreshold.get(), message)
        }

        val moduleScores = baselineFiles.map { info ->
            baselineScorer.scoreBaselineWithFiles(info, scoringConfiguration)
        }

        val totalScore = moduleScores.sumOf { it.totalScore }
        val normalizedScore =
            calculateNormalizedScore(totalScore, moduleScores.sumOf { it.totalIssues })
        val isValid = normalizedScore >= extension.minimumScoreThreshold.get()

        val message = if (isValid) {
            "Validation PASSED: Score $normalizedScore meets threshold ${extension.minimumScoreThreshold.get()}"
        } else {
            "Validation FAILED: Score $normalizedScore below threshold ${extension.minimumScoreThreshold.get()}"
        }

        println("\nValidation Results:")
        println("Total raw score: $totalScore")
        println("Total issues: ${moduleScores.sumOf { it.totalIssues }}")
        println("Normalized score: $normalizedScore")
        println(message)

        return ValidationResult(isValid, totalScore, extension.minimumScoreThreshold.get(), message)
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
