package com.ivanalvarado.baselinescoresplugin.application

import com.ivanalvarado.baselinescoresplugin.BaselineFileDetector
import com.ivanalvarado.baselinescoresplugin.BaselineFileInfo
import com.ivanalvarado.baselinescoresplugin.BaselineScoresExtension
import com.ivanalvarado.baselinescoresplugin.domain.BaselineScorer
import com.ivanalvarado.baselinescoresplugin.domain.ConsoleReporter
import com.ivanalvarado.baselinescoresplugin.domain.FileScoringResult
import com.ivanalvarado.baselinescoresplugin.domain.ReportGenerator
import org.gradle.api.Project

class GenerateBaselineScoresUseCase(
    private val baselineFileDetector: BaselineFileDetector,
    private val baselineScorer: BaselineScorer,
    private val reportGenerator: ReportGenerator,
    private val consoleReporter: ConsoleReporter
) {

    fun execute(project: Project, extension: BaselineScoresExtension): String {
        val baselineFiles = baselineFileDetector.findAllBaselineFiles(project, extension)
        val scoringConfiguration = extension.getScoringConfiguration()

        printInitialInfo(baselineFiles, extension)

        val moduleScores = generateModuleScores(baselineFiles, scoringConfiguration)
        val totalProjectScore = moduleScores.sumOf { it.totalScore }

        printResults(moduleScores, totalProjectScore)

        return reportGenerator.generateJsonReport(project, moduleScores, totalProjectScore)
    }

    private fun printInitialInfo(
        baselineFiles: List<BaselineFileInfo>,
        extension: BaselineScoresExtension
    ) {
        println("Generating baseline scores for ${baselineFiles.size} baseline file(s)...")
        println("Default issue points: ${extension.defaultIssuePoints.get()}")
    }

    private fun generateModuleScores(
        baselineFiles: List<BaselineFileInfo>,
        scoringConfiguration: com.ivanalvarado.baselinescoresplugin.domain.ScoringConfiguration
    ): List<FileScoringResult> {
        return baselineFiles.map { info ->
            baselineScorer.scoreBaselineWithFiles(info, scoringConfiguration)
        }
    }

    private fun printResults(moduleScores: List<FileScoringResult>, totalProjectScore: Int) {
        moduleScores.forEach { result ->
            consoleReporter.printModuleScore(result)
        }

        consoleReporter.printProjectSummary(totalProjectScore, moduleScores)
    }
}
