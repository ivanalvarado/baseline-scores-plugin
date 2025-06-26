package com.ivanalvarado.baselinescoresplugin.application

import com.ivanalvarado.baselinescoresplugin.BaselineType
import com.ivanalvarado.baselinescoresplugin.domain.ScoringConfiguration
import kotlin.test.Test
import kotlin.test.assertEquals

class ScoreCalculatorImplTest {

    private val calculator = ScoreCalculatorImpl()

    @Test
    fun `should calculate score for empty issue breakdown`() {
        val config = ScoringConfiguration(emptyMap(), defaultPoints = -5)

        val result = calculator.calculateScore(
            issueBreakdown = emptyMap(),
            configuration = config,
            module = "test-module",
            type = BaselineType.DETEKT
        )

        assertEquals("test-module", result.module)
        assertEquals(BaselineType.DETEKT, result.type)
        assertEquals(0, result.totalScore)
        assertEquals(0, result.totalIssues)
        assertEquals(0, result.issueBreakdown.size)
    }

    @Test
    fun `should calculate score for single issue type`() {
        val config = ScoringConfiguration(
            rules = mapOf("FunctionNaming" to -8),
            defaultPoints = -5
        )

        val issueBreakdown = mapOf("FunctionNaming" to 3)

        val result = calculator.calculateScore(
            issueBreakdown = issueBreakdown,
            configuration = config,
            module = "test-module",
            type = BaselineType.DETEKT
        )

        assertEquals(-24, result.totalScore) // 3 * -8
        assertEquals(3, result.totalIssues)
        assertEquals(1, result.issueBreakdown.size)

        val functionNamingScore = result.issueBreakdown["FunctionNaming"]!!
        assertEquals("FunctionNaming", functionNamingScore.issueType)
        assertEquals(3, functionNamingScore.count)
        assertEquals(-8, functionNamingScore.pointsPerIssue)
        assertEquals(-24, functionNamingScore.totalPoints)
    }

    @Test
    fun `should calculate score for multiple issue types`() {
        val config = ScoringConfiguration(
            rules = mapOf(
                "FunctionNaming" to -5,
                "LongParameterList" to -10
            ),
            defaultPoints = -3
        )

        val issueBreakdown = mapOf(
            "FunctionNaming" to 2,
            "LongParameterList" to 1,
            "UnknownIssue" to 1
        )

        val result = calculator.calculateScore(
            issueBreakdown = issueBreakdown,
            configuration = config,
            module = "test-module",
            type = BaselineType.DETEKT
        )

        assertEquals(-23, result.totalScore) // (2 * -5) + (1 * -10) + (1 * -3)
        assertEquals(4, result.totalIssues)
        assertEquals(3, result.issueBreakdown.size)

        assertEquals(-10, result.issueBreakdown["FunctionNaming"]!!.totalPoints)
        assertEquals(-10, result.issueBreakdown["LongParameterList"]!!.totalPoints)
        assertEquals(-3, result.issueBreakdown["UnknownIssue"]!!.totalPoints) // uses default
    }

    @Test
    fun `should use default points for unknown issue types`() {
        val config = ScoringConfiguration(
            rules = mapOf("KnownIssue" to -15),
            defaultPoints = -7
        )

        val issueBreakdown = mapOf(
            "KnownIssue" to 1,
            "UnknownIssue1" to 2,
            "UnknownIssue2" to 1
        )

        val result = calculator.calculateScore(
            issueBreakdown = issueBreakdown,
            configuration = config,
            module = "test-module",
            type = BaselineType.DETEKT
        )

        assertEquals(-36, result.totalScore) // (1 * -15) + (2 * -7) + (1 * -7)
        assertEquals(4, result.totalIssues)

        assertEquals(-15, result.issueBreakdown["KnownIssue"]!!.pointsPerIssue)
        assertEquals(-7, result.issueBreakdown["UnknownIssue1"]!!.pointsPerIssue)
        assertEquals(-7, result.issueBreakdown["UnknownIssue2"]!!.pointsPerIssue)
    }

    @Test
    fun `should handle zero issue counts`() {
        val config = ScoringConfiguration(emptyMap(), defaultPoints = -5)

        val issueBreakdown = mapOf(
            "IssueType1" to 0,
            "IssueType2" to 0
        )

        val result = calculator.calculateScore(
            issueBreakdown = issueBreakdown,
            configuration = config,
            module = "test-module",
            type = BaselineType.DETEKT
        )

        assertEquals(0, result.totalScore)
        assertEquals(0, result.totalIssues)
        assertEquals(2, result.issueBreakdown.size)

        result.issueBreakdown.values.forEach { issueScore ->
            assertEquals(0, issueScore.count)
            assertEquals(0, issueScore.totalPoints)
            assertEquals(-5, issueScore.pointsPerIssue)
        }
    }
}
