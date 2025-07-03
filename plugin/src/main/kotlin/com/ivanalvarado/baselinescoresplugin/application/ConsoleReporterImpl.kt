package com.ivanalvarado.baselinescoresplugin.application

import com.ivanalvarado.baselinescoresplugin.BaselineFileInfo
import com.ivanalvarado.baselinescoresplugin.domain.ConsoleReporter
import com.ivanalvarado.baselinescoresplugin.domain.FileScoringResult

class ConsoleReporterImpl : ConsoleReporter {

    override fun printBaselineFilesList(baselineFiles: List<BaselineFileInfo>) {
        if (baselineFiles.isEmpty()) {
            println("No baseline files found in the project.")
        } else {
            println("Found ${baselineFiles.size} baseline file(s):")
            baselineFiles.forEach { info ->
                println("  [${info.type}] ${info.module}: ${info.file.absolutePath}")
            }
        }
    }

    override fun printModuleScore(result: FileScoringResult) {
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

    override fun printProjectSummary(totalScore: Int, moduleScores: List<FileScoringResult>) {
        println("\n" + "=".repeat(50))
        println("PROJECT SUMMARY")
        println("=".repeat(50))
        println("Total project score: $totalScore")
        println("Total modules analyzed: ${moduleScores.size}")
        println("Total issues: ${moduleScores.sumOf { it.totalIssues }}")

        val issueTypeSummary = buildIssueTypeSummary(moduleScores)
        printMostCommonIssues(issueTypeSummary)
    }

    private fun buildIssueTypeSummary(moduleScores: List<FileScoringResult>): Map<String, Int> {
        val issueTypeSummary = mutableMapOf<String, Int>()

        moduleScores.forEach { result ->
            result.fileBreakdown.forEach { (_, issueBreakdown) ->
                issueBreakdown.forEach { (issueType, issueScore) ->
                    issueTypeSummary[issueType] =
                        issueTypeSummary.getOrDefault(issueType, 0) + issueScore.count
                }
            }
        }

        return issueTypeSummary
    }

    private fun printMostCommonIssues(issueTypeSummary: Map<String, Int>) {
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
