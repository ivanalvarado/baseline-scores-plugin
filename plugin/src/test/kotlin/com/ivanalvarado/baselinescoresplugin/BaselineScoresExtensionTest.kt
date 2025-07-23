package com.ivanalvarado.baselinescoresplugin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BaselineScoresExtensionTest {

    @Test
    fun `should have default configuration`() {
        val extension = BaselineScoresExtension()

        assertEquals("baseline-scores.json", extension.outputFile)
        assertEquals(0.8, extension.threshold)
        assertEquals(true, extension.enabled)
        assertEquals(true, extension.detektEnabled)
        assertEquals(true, extension.lintEnabled)
        assertEquals(-5, extension.defaultIssuePoints)
    }

    @Test
    fun `should configure single issue score`() {
        val extension = BaselineScoresExtension()
        extension.issueScore("FunctionNaming", -10)

        val config = extension.getScoringConfiguration()

        assertEquals(-10, config.getPointsForIssue("FunctionNaming"))
        assertEquals(-5, config.getPointsForIssue("UnknownIssue")) // still uses default
    }

    @Test
    fun `should configure multiple issue scores`() {
        val extension = BaselineScoresExtension()
        extension.issueScores(
            mapOf(
                "FunctionNaming" to -8,
                "LongParameterList" to -15,
                "MagicNumber" to -2
            )
        )

        val config = extension.getScoringConfiguration()

        assertEquals(-8, config.getPointsForIssue("FunctionNaming"))
        assertEquals(-15, config.getPointsForIssue("LongParameterList"))
        assertEquals(-2, config.getPointsForIssue("MagicNumber"))
        assertEquals(-5, config.getPointsForIssue("UnknownIssue"))
    }

    @Test
    fun `should override default scoring rules`() {
        val extension = BaselineScoresExtension()

        // First check default value
        val defaultConfig = extension.getScoringConfiguration()
        assertEquals(-5, defaultConfig.getPointsForIssue("FunctionNaming"))

        // Override with custom value
        extension.issueScore("FunctionNaming", -20)
        val customConfig = extension.getScoringConfiguration()
        assertEquals(-20, customConfig.getPointsForIssue("FunctionNaming"))
    }

    @Test
    fun `should combine default rules with custom rules`() {
        val extension = BaselineScoresExtension()
        extension.issueScore("CustomIssue", -50)

        val config = extension.getScoringConfiguration()

        // Should have default rules
        assertEquals(-5, config.getPointsForIssue("FunctionNaming"))
        assertEquals(-20, config.getPointsForIssue("LongParameterList"))
        assertEquals(-10, config.getPointsForIssue("MagicNumber"))

        // Should have custom rule
        assertEquals(-50, config.getPointsForIssue("CustomIssue"))
    }

    @Test
    fun `should handle custom default points`() {
        val extension = BaselineScoresExtension()
        extension.defaultIssuePoints = -12

        val config = extension.getScoringConfiguration()

        assertEquals(-12, config.getPointsForIssue("UnknownIssue"))
        // Default rules should still apply
        assertEquals(-5, config.getPointsForIssue("FunctionNaming"))
    }

    @Test
    fun `should have comprehensive default rules for common detekt issues`() {
        val extension = BaselineScoresExtension()
        val config = extension.getScoringConfiguration()

        // Test some key default rules
        assertEquals(-5, config.getPointsForIssue("FunctionNaming"))
        assertEquals(-20, config.getPointsForIssue("LongParameterList"))
        assertEquals(-10, config.getPointsForIssue("MagicNumber"))
        assertEquals(-5, config.getPointsForIssue("UnusedPrivateMember"))
        assertEquals(-5, config.getPointsForIssue("ComplexMethod"))
        assertEquals(-20, config.getPointsForIssue("UnsafeCallOnNullableType"))
        assertEquals(-20, config.getPointsForIssue("ExceptionRaisedInUnexpectedLocation"))

        // Verify these are more severe than the default
        assertTrue(config.getPointsForIssue("UnsafeCallOnNullableType") < extension.defaultIssuePoints)
        assertTrue(config.getPointsForIssue("ExceptionRaisedInUnexpectedLocation") < extension.defaultIssuePoints)
    }

    @Test
    fun `should allow chaining configuration methods`() {
        val extension = BaselineScoresExtension()
        extension.apply {
            defaultIssuePoints = -8
            issueScore("CustomIssue1", -10)
            issueScores(
                mapOf(
                    "CustomIssue2" to -15,
                    "CustomIssue3" to -20
                )
            )
        }

        val config = extension.getScoringConfiguration()

        assertEquals(-8, config.defaultPoints)
        assertEquals(-10, config.getPointsForIssue("CustomIssue1"))
        assertEquals(-15, config.getPointsForIssue("CustomIssue2"))
        assertEquals(-20, config.getPointsForIssue("CustomIssue3"))
    }
}
