package com.ivanalvarado.baselinescoresplugin.application

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.ivanalvarado.baselinescoresplugin.BaselineType
import com.ivanalvarado.baselinescoresplugin.domain.BaselineProcessingException
import com.ivanalvarado.baselinescoresplugin.domain.FileScoringResult
import com.ivanalvarado.baselinescoresplugin.domain.IssueScore
import org.gradle.testfixtures.ProjectBuilder
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class JsonReportGeneratorTest {

    private val generator = JsonReportGenerator()
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    @Test
    fun `should generate valid JSON report with correct structure`() {
        val project = ProjectBuilder.builder().build()
        val moduleScores = createTestModuleScores()
        val totalProjectScore = -45

        val outputPath = generator.generateJsonReport(project, moduleScores, totalProjectScore)

        try {
            val outputFile = File(outputPath)
            assertTrue(outputFile.exists())

            val jsonContent = objectMapper.readTree(outputFile)

            assertEquals(project.path, jsonContent["projectPath"].asText())
            assertEquals(totalProjectScore, jsonContent["projectTotalScore"].asInt())
            assertEquals(6, jsonContent["totalIssues"].asInt())
            assertTrue(jsonContent["generatedAt"].asText().isNotEmpty())

            val results = jsonContent["results"]
            assertEquals(2, results.size())

            val firstResult = results[0]
            assertEquals("TestClass.kt", firstResult["class"].asText())
            assertEquals(2, firstResult["issues"].size())

            val firstIssue = firstResult["issues"][0]
            assertEquals("FunctionNaming", firstIssue["issue"].asText())
            assertEquals(2, firstIssue["occurrences"].asInt())
            assertEquals(-5, firstIssue["debt"].asInt())
            assertEquals(-10, firstIssue["score"].asInt())
        } finally {
            File(outputPath).delete()
        }
    }

    @Test
    fun `should create output directory if it does not exist`() {
        val project = ProjectBuilder.builder().build()
        val moduleScores = createTestModuleScores()

        val outputPath = generator.generateJsonReport(project, moduleScores, -45)

        try {
            val outputFile = File(outputPath)
            val parentDir = outputFile.parentFile
            assertTrue(parentDir.exists())
            assertTrue(parentDir.isDirectory())
            assertEquals("baseline-scores", parentDir.name)
        } finally {
            File(outputPath).delete()
        }
    }

    @Test
    fun `should handle empty module scores`() {
        val project = ProjectBuilder.builder().build()
        val moduleScores = emptyList<FileScoringResult>()
        val totalProjectScore = 0

        val outputPath = generator.generateJsonReport(project, moduleScores, totalProjectScore)

        try {
            val outputFile = File(outputPath)
            assertTrue(outputFile.exists())

            val jsonContent = objectMapper.readTree(outputFile)

            assertEquals(0, jsonContent["projectTotalScore"].asInt())
            assertEquals(0, jsonContent["totalIssues"].asInt())
            assertEquals(0, jsonContent["results"].size())
        } finally {
            File(outputPath).delete()
        }
    }

    @Test
    fun `should handle single file with multiple issues`() {
        val project = ProjectBuilder.builder().build()
        val moduleScores = listOf(
            FileScoringResult(
                module = "app",
                type = BaselineType.DETEKT,
                fileBreakdown = mapOf(
                    "MainActivity.kt" to mapOf(
                        "ComplexMethod" to IssueScore("ComplexMethod", 3, -8, -24),
                        "LongParameterList" to IssueScore("LongParameterList", 1, -10, -10),
                        "MagicNumber" to IssueScore("MagicNumber", 5, -2, -10)
                    )
                ),
                totalScore = -44,
                totalIssues = 9
            )
        )

        val outputPath = generator.generateJsonReport(project, moduleScores, -44)

        try {
            val outputFile = File(outputPath)
            val jsonContent = objectMapper.readTree(outputFile)

            assertEquals(9, jsonContent["totalIssues"].asInt())
            assertEquals(-44, jsonContent["projectTotalScore"].asInt())

            val results = jsonContent["results"]
            assertEquals(1, results.size())

            val result = results[0]
            assertEquals("MainActivity.kt", result["class"].asText())
            assertEquals(3, result["issues"].size())

            val complexMethodIssue =
                result["issues"].find { it["issue"].asText() == "ComplexMethod" }!!
            assertEquals(3, complexMethodIssue["occurrences"].asInt())
            assertEquals(-8, complexMethodIssue["debt"].asInt())
            assertEquals(-24, complexMethodIssue["score"].asInt())
        } finally {
            File(outputPath).delete()
        }
    }

    @Test
    fun `should handle multiple modules with different baseline types`() {
        val project = ProjectBuilder.builder().build()
        val moduleScores = listOf(
            FileScoringResult(
                module = "app",
                type = BaselineType.DETEKT,
                fileBreakdown = mapOf(
                    "DetektFile.kt" to mapOf(
                        "FunctionNaming" to IssueScore("FunctionNaming", 1, -5, -5)
                    )
                ),
                totalScore = -5,
                totalIssues = 1
            ),
            FileScoringResult(
                module = "core",
                type = BaselineType.LINT,
                fileBreakdown = mapOf(
                    "LintFile.kt" to mapOf(
                        "UnusedResources" to IssueScore("UnusedResources", 2, -3, -6)
                    )
                ),
                totalScore = -6,
                totalIssues = 2
            )
        )

        val outputPath = generator.generateJsonReport(project, moduleScores, -11)

        try {
            val outputFile = File(outputPath)
            val jsonContent = objectMapper.readTree(outputFile)

            assertEquals(3, jsonContent["totalIssues"].asInt())
            assertEquals(-11, jsonContent["projectTotalScore"].asInt())

            val results = jsonContent["results"]
            assertEquals(2, results.size())

            val detektResult = results.find { it["class"].asText() == "DetektFile.kt" }!!
            assertEquals(1, detektResult["issues"].size())

            val lintResult = results.find { it["class"].asText() == "LintFile.kt" }!!
            assertEquals(1, lintResult["issues"].size())
        } finally {
            File(outputPath).delete()
        }
    }

    @Test
    fun `should throw BaselineProcessingException when file write fails`() {
        val project = ProjectBuilder.builder().build()
        val moduleScores = createTestModuleScores()

        // Create the build directory as a file to cause write failure
        val buildDir = File(project.buildDir, "baseline-scores")
        buildDir.parentFile.mkdirs()
        buildDir.createNewFile()

        try {
            assertFailsWith<BaselineProcessingException.ReportGenerationFailed> {
                generator.generateJsonReport(project, moduleScores, -45)
            }
        } finally {
            buildDir.delete()
        }
    }

    private fun createTestModuleScores(): List<FileScoringResult> {
        return listOf(
            FileScoringResult(
                module = "app",
                type = BaselineType.DETEKT,
                fileBreakdown = mapOf(
                    "TestClass.kt" to mapOf(
                        "FunctionNaming" to IssueScore("FunctionNaming", 2, -5, -10),
                        "ComplexMethod" to IssueScore("ComplexMethod", 1, -8, -8)
                    ),
                    "AnotherClass.kt" to mapOf(
                        "LongParameterList" to IssueScore("LongParameterList", 2, -10, -20),
                        "MagicNumber" to IssueScore("MagicNumber", 1, -7, -7)
                    )
                ),
                totalScore = -45,
                totalIssues = 6
            )
        )
    }
}
