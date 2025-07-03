package com.ivanalvarado.baselinescoresplugin

import com.ivanalvarado.baselinescoresplugin.application.BaselineScorerImpl
import com.ivanalvarado.baselinescoresplugin.domain.FileScoringResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
                val baselineFiles = detector.findAllBaselineFiles(project, extension)

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
                val baselineFiles = detector.findAllBaselineFiles(project, extension)
                val scoringConfiguration = extension.getScoringConfiguration()

                println("Generating baseline scores for ${baselineFiles.size} baseline file(s)...")
                println("Default issue points: ${extension.defaultIssuePoints}")

                var totalProjectScore = 0
                val moduleScores = mutableListOf<FileScoringResult>()

                baselineFiles.forEach { info ->
                    val result = scorer.scoreBaselineWithFiles(info, scoringConfiguration)
                    moduleScores.add(result)
                    totalProjectScore += result.totalScore

                    printModuleScore(result)
                }

                printSummary(totalProjectScore, moduleScores)

                // Generate JSON output
                generateJsonOutput(project, moduleScores, totalProjectScore)

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

    private fun generateJsonOutput(
        project: Project,
        moduleScores: List<FileScoringResult>,
        totalProjectScore: Int
    ) {
        val buildDir = File(project.buildDir, "baseline-scores")
        buildDir.mkdirs()

        val outputFile = File(buildDir, "baseline-scores-results.json")

        val results = mutableListOf<Map<String, Any>>()

        moduleScores.forEach { result ->
            result.fileBreakdown.forEach { (fileName, issueBreakdown) ->
                val issues = issueBreakdown.map { (_, issueScore) ->
                    mapOf(
                        "issue" to issueScore.issueType,
                        "occurrences" to issueScore.count,
                        "debt" to issueScore.pointsPerIssue,
                        "score" to issueScore.totalPoints
                    )
                }

                results.add(
                    mapOf(
                        "class" to fileName,
                        "issues" to issues
                    )
                )
            }
        }

        val finalOutput = mapOf(
            "generatedAt" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "projectTotalScore" to totalProjectScore,
            "totalIssues" to moduleScores.sumOf { it.totalIssues },
            "results" to results
        )

        val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, finalOutput)

        println("JSON results written to: ${outputFile.absolutePath}")
    }

    private fun printModuleScore(result: FileScoringResult) {
        println("\n[${result.type}] Module: ${result.module}")
        println("  Total issues: ${result.totalIssues}, Total score: ${result.totalScore}")

        if (result.fileBreakdown.isNotEmpty()) {
            println("  File breakdown:")
            result.fileBreakdown.forEach { (fileName, issueBreakdown) ->
                println("    $fileName:")
                issueBreakdown.values
                    .sortedByDescending { it.count }
                    .forEach { issue ->
                        println("      ${issue.issueType}: ${issue.count} issues × ${issue.pointsPerIssue} points = ${issue.totalPoints}")
                    }
            }
        }
    }

    private fun printSummary(totalScore: Int, moduleScores: List<FileScoringResult>) {
        println("\n" + "=".repeat(50))
        println("PROJECT SUMMARY")
        println("=".repeat(50))
        println("Total project score: $totalScore")
        println("Total modules analyzed: ${moduleScores.size}")
        println("Total issues: ${moduleScores.sumOf { it.totalIssues }}")

        val issueTypeSummary = mutableMapOf<String, Int>()
        moduleScores.forEach { result ->
            result.fileBreakdown.forEach { (_, issueBreakdown) ->
                issueBreakdown.forEach { (issueType, issueScore) ->
                    issueTypeSummary[issueType] =
                        issueTypeSummary.getOrDefault(issueType, 0) + issueScore.count
                }
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
