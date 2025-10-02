package com.ivanalvarado.baselinescoresplugin.application

import com.ivanalvarado.baselinescoresplugin.BaselineFileInfo
import com.ivanalvarado.baselinescoresplugin.domain.ConsoleReporter
import com.ivanalvarado.baselinescoresplugin.domain.FileScoringResult
import com.ivanalvarado.baselinescoresplugin.domain.IssueScore

class ConsoleReporterImpl : ConsoleReporter {

    override fun printBaselineFilesList(baselineFiles: List<BaselineFileInfo>) {
        if (noBaselinesWereFound(baselineFiles)) {
            println("No baseline files found in the project.")
        } else {
            printFoundBaselineFiles(baselineFiles)
        }
    }

    private fun noBaselinesWereFound(baselineFiles: List<BaselineFileInfo>): Boolean {
        return baselineFiles.isEmpty()
    }

    private fun printFoundBaselineFiles(baselineFiles: List<BaselineFileInfo>) {
        println("Found ${baselineFiles.size} baseline file(s):")
        baselineFiles.forEach { info ->
            println("  [${info.type}] ${info.module}: ${info.file.absolutePath}")
        }
    }

    override fun printModuleScore(result: FileScoringResult) {
        printModuleHeader(result)
        printModuleTotals(result)

        if (moduleHasIssues(result)) {
            printFileBreakdown(result)
        }
    }

    private fun printModuleHeader(result: FileScoringResult) {
        println("\n[${result.type}] Module: ${result.module}")
    }

    private fun printModuleTotals(result: FileScoringResult) {
        println("  Total issues: ${result.totalIssues}, Total score: ${result.totalScore}")
    }

    private fun moduleHasIssues(result: FileScoringResult): Boolean {
        return result.fileBreakdown.isNotEmpty()
    }

    private fun printFileBreakdown(result: FileScoringResult) {
        println("  File breakdown:")
        result.fileBreakdown.forEach { (fileName, issuesInFile) ->
            printIssuesForFile(fileName, issuesInFile)
        }
    }

    private fun printIssuesForFile(fileName: String, issuesInFile: Map<String, IssueScore>) {
        println("    $fileName:")

        val issuesSortedByFrequency = issuesInFile.values.sortedByDescending { it.count }
        issuesSortedByFrequency.forEach { issue ->
            printIssueDetail(issue)
        }
    }

    private fun printIssueDetail(issue: IssueScore) {
        println("      ${issue.issueType}: ${issue.count} issues × ${issue.pointsPerIssue} points = ${issue.totalPoints}")
    }

    override fun printProjectSummary(totalScore: Int, moduleScores: List<FileScoringResult>) {
        printSectionSeparator()
        printSummaryHeader()
        printSectionSeparator()

        printOverallStatistics(totalScore, moduleScores)

        val issueFrequencyMap = calculateIssueFrequencies(moduleScores)
        printTopIssues(issueFrequencyMap)
    }

    private fun printSectionSeparator() {
        println("=".repeat(50))
    }

    private fun printSummaryHeader() {
        println("PROJECT SUMMARY")
    }

    private fun printOverallStatistics(totalScore: Int, moduleScores: List<FileScoringResult>) {
        val totalIssuesAcrossAllModules = moduleScores.sumOf { it.totalIssues }

        println("Total project score: $totalScore")
        println("Total modules analyzed: ${moduleScores.size}")
        println("Total issues: $totalIssuesAcrossAllModules")
    }

    private fun calculateIssueFrequencies(moduleScores: List<FileScoringResult>): Map<String, Int> {
        val frequencyMap = mutableMapOf<String, Int>()

        moduleScores.forEach { moduleResult ->
            moduleResult.fileBreakdown.forEach { (_, issuesInFile) ->
                issuesInFile.forEach { (issueType, issueScore) ->
                    val currentFrequency = frequencyMap.getOrDefault(issueType, 0)
                    frequencyMap[issueType] = currentFrequency + issueScore.count
                }
            }
        }

        return frequencyMap
    }

    private fun printTopIssues(issueFrequencyMap: Map<String, Int>) {
        if (noIssuesWereFound(issueFrequencyMap)) {
            return
        }

        println("\nMost common issues:")

        val topFiveIssues = issueFrequencyMap.toList()
            .sortedByDescending { (_, frequency) -> frequency }
            .take(5)

        topFiveIssues.forEach { (issueType, frequency) ->
            println("  $issueType: $frequency occurrences")
        }
    }

    private fun noIssuesWereFound(issueFrequencyMap: Map<String, Int>): Boolean {
        return issueFrequencyMap.isEmpty()
    }
}
