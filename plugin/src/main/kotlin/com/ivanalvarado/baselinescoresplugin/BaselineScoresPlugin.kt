package com.ivanalvarado.baselinescoresplugin

import com.ivanalvarado.baselinescoresplugin.application.BaselineScorerImpl
import com.ivanalvarado.baselinescoresplugin.domain.ScoringResult
import org.gradle.api.Plugin
import org.gradle.api.Project

class BaselineScoresPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register the extension
        val extension =
            project.extensions.create("baselineScores", BaselineScoresExtension::class.java)

        val detector = BaselineFileDetector()
        val scorer = BaselineScorerImpl()

        // Register tasks
        project.task("findBaselineFiles") {
            it.group = "baseline"
            it.description = "Find all baseline files in the project and its modules"
            it.doLast {
                val baselineFiles = detector.findAllBaselineFiles(project.rootProject, extension)

                if (baselineFiles.isEmpty()) {
                    println("No baseline files found in the project.")
                } else {
                    println("Found ${baselineFiles.size} baseline file(s):")
                    baselineFiles.forEach { info ->
                        println("  [${info.type}] ${info.module}: ${info.file.absolutePath}")
                    }
                }
            }
        }

        project.task("generateBaselineScores") {
            it.group = "baseline"
            it.description = "Generate baseline scores for the project"
            it.doLast {
                val baselineFiles = detector.findAllBaselineFiles(project.rootProject, extension)
                val scoringConfiguration = extension.getScoringConfiguration()

                println("Generating baseline scores for ${baselineFiles.size} baseline file(s)...")
                println("Default issue points: ${extension.defaultIssuePoints}")

                var totalProjectScore = 0
                val moduleScores = mutableListOf<ScoringResult>()

                baselineFiles.forEach { info ->
                    val result = scorer.scoreBaseline(info, scoringConfiguration)
                    moduleScores.add(result)
                    totalProjectScore += result.totalScore

                    printModuleScore(result)
                }

                printSummary(totalProjectScore, moduleScores)
                println("Output file: ${extension.outputFile}")
            }
        }

        project.task("validateBaselineScores") {
            it.group = "baseline"
            it.description = "Validate current scores against baseline"
            it.doLast {
                println("Validating baseline scores...")
                println("Threshold: ${extension.threshold}")
            }
        }
    }

    private fun printModuleScore(result: ScoringResult) {
        println("\n[${result.type}] Module: ${result.module}")
        println("  Total issues: ${result.totalIssues}, Total score: ${result.totalScore}")

        if (result.issueBreakdown.isNotEmpty()) {
            println("  Issue breakdown:")
            result.issueBreakdown.values
                .sortedByDescending { it.count }
                .forEach { issue ->
                    println("    ${issue.issueType}: ${issue.count} issues × ${issue.pointsPerIssue} points = ${issue.totalPoints}")
                }
        }
    }

    private fun printSummary(totalScore: Int, moduleScores: List<ScoringResult>) {
        println("\n" + "=".repeat(50))
        println("PROJECT SUMMARY")
        println("=".repeat(50))
        println("Total project score: $totalScore")
        println("Total modules analyzed: ${moduleScores.size}")
        println("Total issues: ${moduleScores.sumOf { it.totalIssues }}")

        val issueTypeSummary = mutableMapOf<String, Int>()
        moduleScores.forEach { result ->
            result.issueBreakdown.forEach { (issueType, issueScore) ->
                issueTypeSummary[issueType] =
                    issueTypeSummary.getOrDefault(issueType, 0) + issueScore.count
            }
        }

        if (issueTypeSummary.isNotEmpty()) {
            println("\nMost common issues:")
            issueTypeSummary.toList()
                .sortedByDescending { it.second }
                .take(5)
                .forEach { (issueType, count) ->
                    println("  $issueType: $count occurrences")
                }
        }
    }
}
