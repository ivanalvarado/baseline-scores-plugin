package com.ivanalvarado.baselinescoresplugin.integration

import com.ivanalvarado.baselinescoresplugin.BaselineScoresExtension
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Integration test showing how to configure the plugin with custom scoring rules
 */
class PluginConfigurationExampleTest {

    @Test
    fun `should demonstrate basic plugin configuration`() {
        val extension = BaselineScoresExtension()

        // Basic configuration
        extension.apply {
            outputFile = "my-baseline-scores.json"
            defaultIssuePoints = -8

            // Configure specific issue scores
            issueScore("ComplexMethod", -20)
            issueScore("LongMethod", -15)

            // Configure multiple issues at once
            issueScores(
                mapOf(
                    "MagicNumber" to -2,
                    "UnusedPrivateMember" to -5,
                    "FunctionNaming" to -3
                )
            )
        }

        val config = extension.getScoringConfiguration()

        // Verify configuration
        assertEquals("my-baseline-scores.json", extension.outputFile)
        assertEquals(-8, extension.defaultIssuePoints)
        assertEquals(-20, config.getPointsForIssue("ComplexMethod"))
        assertEquals(-15, config.getPointsForIssue("LongMethod"))
        assertEquals(-2, config.getPointsForIssue("MagicNumber"))
        assertEquals(-5, config.getPointsForIssue("UnusedPrivateMember"))
        assertEquals(-3, config.getPointsForIssue("FunctionNaming"))
        assertEquals(-8, config.getPointsForIssue("UnknownIssueType"))
    }

    @Test
    fun `should demonstrate severity-based scoring configuration`() {
        val extension = BaselineScoresExtension()

        // Configure based on severity levels
        extension.apply {
            defaultIssuePoints = -5

            // Critical issues (security, bugs)
            issueScores(
                mapOf(
                    "UnsafeCallOnNullableType" to -25,
                    "ExceptionRaisedInUnexpectedLocation" to -30,
                    "TooGenericExceptionCaught" to -15
                )
            )

            // Major issues (maintainability)
            issueScores(
                mapOf(
                    "ComplexMethod" to -20,
                    "LongMethod" to -15,
                    "LargeClass" to -18,
                    "TooManyFunctions" to -12
                )
            )

            // Minor issues (style, convention)
            issueScores(
                mapOf(
                    "FunctionNaming" to -3,
                    "MagicNumber" to -2,
                    "UnnecessaryLet" to -1
                )
            )
        }

        val config = extension.getScoringConfiguration()

        // Critical issues should have the highest penalties
        assertEquals(-25, config.getPointsForIssue("UnsafeCallOnNullableType"))
        assertEquals(-30, config.getPointsForIssue("ExceptionRaisedInUnexpectedLocation"))

        // Major issues should have medium penalties
        assertEquals(-20, config.getPointsForIssue("ComplexMethod"))
        assertEquals(-15, config.getPointsForIssue("LongMethod"))

        // Minor issues should have low penalties
        assertEquals(-3, config.getPointsForIssue("FunctionNaming"))
        assertEquals(-2, config.getPointsForIssue("MagicNumber"))
        assertEquals(-1, config.getPointsForIssue("UnnecessaryLet"))

        // Unknown issues use default
        assertEquals(-5, config.getPointsForIssue("SomeNewIssueType"))
    }

    @Test
    fun `should demonstrate gradual improvement strategy`() {
        val extension = BaselineScoresExtension()

        // Strategy: Start with low penalties and gradually increase
        extension.apply {
            defaultIssuePoints = -1  // Very mild penalty for new issues

            // Phase 1: Focus on critical issues only
            issueScores(
                mapOf(
                    "UnsafeCallOnNullableType" to -50,
                    "ExceptionRaisedInUnexpectedLocation" to -50,
                    "NullPointerException" to -30
                )
            )

            // Phase 2: Address major maintainability issues
            issueScores(
                mapOf(
                    "ComplexMethod" to -10,  // Lower than usual to encourage gradual fixing
                    "LongMethod" to -8,
                    "LargeClass" to -12
                )
            )

            // Phase 3: Minor issues get very small penalties
            issueScores(
                mapOf(
                    "FunctionNaming" to -1,
                    "MagicNumber" to -1,
                    "EmptyFunctionBlock" to -1
                )
            )
        }

        val config = extension.getScoringConfiguration()

        // Critical issues should block builds
        assertEquals(-50, config.getPointsForIssue("UnsafeCallOnNullableType"))

        // Major issues discouraged but not blocking
        assertEquals(-10, config.getPointsForIssue("ComplexMethod"))

        // Minor issues barely penalized
        assertEquals(-1, config.getPointsForIssue("FunctionNaming"))
        assertEquals(-1, config.getPointsForIssue("MagicNumber"))

        // New issues get minimal penalty
        assertEquals(-1, config.getPointsForIssue("NewIssueType"))
    }
}
