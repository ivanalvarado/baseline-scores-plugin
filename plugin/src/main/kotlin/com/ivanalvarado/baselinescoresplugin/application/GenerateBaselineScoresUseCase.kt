package com.ivanalvarado.baselinescoresplugin.application

import com.ivanalvarado.baselinescoresplugin.ConfigCacheCompatibleBaselineFileDetector
import com.ivanalvarado.baselinescoresplugin.BaselineFileInfo
import com.ivanalvarado.baselinescoresplugin.ProjectInfo
import com.ivanalvarado.baselinescoresplugin.ExtensionConfig
import com.ivanalvarado.baselinescoresplugin.config.DetektDefaultScores
import com.ivanalvarado.baselinescoresplugin.domain.BaselineScorer
import com.ivanalvarado.baselinescoresplugin.domain.ConsoleReporter
import com.ivanalvarado.baselinescoresplugin.domain.FileScoringResult
import com.ivanalvarado.baselinescoresplugin.domain.ReportGenerator

class GenerateBaselineScoresUseCase(
    private val baselineFileDetector: ConfigCacheCompatibleBaselineFileDetector,
    private val baselineScorer: BaselineScorer,
    private val reportGenerator: ReportGenerator,
    private val consoleReporter: ConsoleReporter
) {

    fun execute(projectInfo: ProjectInfo, extensionConfig: ExtensionConfig): String {
        val baselineFiles = baselineFileDetector.findAllBaselineFiles(projectInfo, extensionConfig)
        val scoringConfiguration = extensionConfig.toScoringConfiguration()

        printInitialInfo(baselineFiles, extensionConfig)

        val moduleScores = generateModuleScores(baselineFiles, scoringConfiguration)
        val totalProjectScore = moduleScores.sumOf { it.totalScore }

        printResults(moduleScores, totalProjectScore)

        return reportGenerator.generateJsonReport(projectInfo, moduleScores, totalProjectScore)
    }

    private fun printInitialInfo(
        baselineFiles: List<BaselineFileInfo>,
        extensionConfig: ExtensionConfig
    ) {
        println("Generating baseline scores for ${baselineFiles.size} baseline file(s)...")
        println("Default issue points: ${extensionConfig.defaultIssuePoints}")
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
