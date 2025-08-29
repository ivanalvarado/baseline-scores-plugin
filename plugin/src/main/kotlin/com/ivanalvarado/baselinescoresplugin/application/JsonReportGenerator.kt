package com.ivanalvarado.baselinescoresplugin.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.ivanalvarado.baselinescoresplugin.domain.BaselineProcessingException
import com.ivanalvarado.baselinescoresplugin.domain.FileScoringResult
import com.ivanalvarado.baselinescoresplugin.domain.ReportGenerator
import com.ivanalvarado.baselinescoresplugin.ProjectInfo
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class JsonReportGenerator : ReportGenerator {

    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    override fun generateJsonReport(
        projectInfo: ProjectInfo,
        moduleScores: List<FileScoringResult>,
        totalProjectScore: Int
    ): String {
        return try {
            val buildDir = File(projectInfo.buildDir, "baseline-scores")
            buildDir.mkdirs()

            val outputFile = File(buildDir, "baseline-scores-results.json")
            val results = buildResultsStructure(moduleScores)

            val finalOutput =
                buildFinalOutput(projectInfo.path, totalProjectScore, moduleScores, results)

            objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputFile, finalOutput)

            outputFile.absolutePath
        } catch (e: Exception) {
            throw BaselineProcessingException.ReportGenerationFailed(e)
        }
    }

    private fun buildResultsStructure(moduleScores: List<FileScoringResult>): List<Map<String, Any>> {
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

        return results
    }

    private fun buildFinalOutput(
        projectPath: String,
        totalProjectScore: Int,
        moduleScores: List<FileScoringResult>,
        results: List<Map<String, Any>>
    ): Map<String, Any> {
        return mapOf(
            "generatedAt" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "projectPath" to projectPath,
            "projectTotalScore" to totalProjectScore,
            "totalIssues" to moduleScores.sumOf { it.totalIssues },
            "results" to results
        )
    }
}
