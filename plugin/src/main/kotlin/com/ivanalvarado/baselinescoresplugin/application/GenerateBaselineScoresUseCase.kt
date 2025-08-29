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
        val scoringConfiguration = createScoringConfiguration(extensionConfig)

        printInitialInfo(baselineFiles, extensionConfig)

        val moduleScores = generateModuleScores(baselineFiles, scoringConfiguration)
        val totalProjectScore = moduleScores.sumOf { it.totalScore }

        printResults(moduleScores, totalProjectScore)

        return reportGenerator.generateJsonReport(projectInfo, moduleScores, totalProjectScore)
    }

    private fun createScoringConfiguration(extensionConfig: ExtensionConfig): com.ivanalvarado.baselinescoresplugin.domain.ScoringConfiguration {
        val mergedRules = mutableMapOf<String, Int>()

        if (extensionConfig.detektEnabled && extensionConfig.useDefaultDetektScoring) {
            // Add default detekt scores when available
            mergedRules.putAll(DetektDefaultScores.rules)
        }

        if (extensionConfig.lintEnabled && extensionConfig.useDefaultLintScoring) {
            // Add default lint scores when available
            // mergedRules.putAll(LintDefaultScores.rules)
        }

        // User scoring rules override defaults
        mergedRules.putAll(extensionConfig.userScoringRules)

        return com.ivanalvarado.baselinescoresplugin.domain.ScoringConfiguration(
            rules = mergedRules,
            defaultPoints = extensionConfig.defaultIssuePoints
        )
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
