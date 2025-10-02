package com.ivanalvarado.baselinescoresplugin

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BaselineScoresExtensionTest {

    private fun createExtension(): BaselineScoresExtension {
        val project = ProjectBuilder.builder().build()
        return project.objects.newInstance(BaselineScoresExtension::class.java)
    }

    private fun BaselineScoresExtension.toExtensionConfig(): ExtensionConfig {
        return ExtensionConfig(
            detektEnabled = detektEnabled.get(),
            lintEnabled = lintEnabled.get(),
            detektBaselineFileName = detektBaselineFileName.get(),
            lintBaselineFileName = lintBaselineFileName.get(),
            defaultIssuePoints = defaultIssuePoints.get(),
            minimumScoreThreshold = minimumScoreThreshold.get(),
            useDefaultDetektScoring = useDefaultDetektScoring.get(),
            useDefaultLintScoring = useDefaultLintScoring.get(),
            userScoringRules = userScoringRules.get()
        )
    }

    @Test
    fun `should have default configuration`() {
        val extension = createExtension()

        assertEquals("baseline-scores.json", extension.outputFile.get())
        assertEquals(0.8, extension.minimumScoreThreshold.get())
        assertEquals(true, extension.enabled.get())
        assertEquals(true, extension.detektEnabled.get())
        assertEquals(true, extension.lintEnabled.get())
        assertEquals(-5, extension.defaultIssuePoints.get())
    }

    @Test
    fun `should configure single issue score`() {
        val extension = createExtension()
        extension.issueScore("FunctionNaming", -10)

        val config = extension.toExtensionConfig().toScoringConfiguration()

        assertEquals(-10, config.getPointsForIssue("FunctionNaming"))
        assertEquals(-5, config.getPointsForIssue("UnknownIssue")) // still uses default
    }

    @Test
    fun `should configure multiple issue scores`() {
        val extension = createExtension()
        extension.issueScores(
            mapOf(
                "FunctionNaming" to -8,
                "LongParameterList" to -15,
                "MagicNumber" to -2
            )
        )

        val config = extension.toExtensionConfig().toScoringConfiguration()

        assertEquals(-8, config.getPointsForIssue("FunctionNaming"))
        assertEquals(-15, config.getPointsForIssue("LongParameterList"))
        assertEquals(-2, config.getPointsForIssue("MagicNumber"))
        assertEquals(-5, config.getPointsForIssue("UnknownIssue"))
    }

    @Test
    fun `should override default scoring rules`() {
        val extension = createExtension()

        // First check default value
        val defaultConfig = extension.toExtensionConfig().toScoringConfiguration()
        assertEquals(-5, defaultConfig.getPointsForIssue("FunctionNaming"))

        // Override with custom value
        extension.issueScore("FunctionNaming", -20)
        val customConfig = extension.toExtensionConfig().toScoringConfiguration()
        assertEquals(-20, customConfig.getPointsForIssue("FunctionNaming"))
    }

    @Test
    fun `should combine default rules with custom rules`() {
        val extension = createExtension()
        extension.issueScore("CustomIssue", -50)

        val config = extension.toExtensionConfig().toScoringConfiguration()

        // Should have default rules
        assertEquals(-5, config.getPointsForIssue("FunctionNaming"))
        assertEquals(-20, config.getPointsForIssue("LongParameterList"))
        assertEquals(-10, config.getPointsForIssue("MagicNumber"))

        // Should have custom rule
        assertEquals(-50, config.getPointsForIssue("CustomIssue"))
    }

    @Test
    fun `should handle custom default points`() {
        val extension = createExtension()
        extension.defaultIssuePoints.set(-12)

        val config = extension.toExtensionConfig().toScoringConfiguration()

        assertEquals(-12, config.getPointsForIssue("UnknownIssue"))
        // Default rules should still apply
        assertEquals(-5, config.getPointsForIssue("FunctionNaming"))
    }

    @Test
    fun `should have comprehensive default rules for common detekt issues`() {
        val extension = createExtension()
        val config = extension.toExtensionConfig().toScoringConfiguration()

        // Test some key default rules
        assertEquals(-5, config.getPointsForIssue("FunctionNaming"))
        assertEquals(-20, config.getPointsForIssue("LongParameterList"))
        assertEquals(-10, config.getPointsForIssue("MagicNumber"))
        assertEquals(-5, config.getPointsForIssue("UnusedPrivateMember"))
        assertEquals(-5, config.getPointsForIssue("ComplexMethod"))
        assertEquals(-20, config.getPointsForIssue("UnsafeCallOnNullableType"))
        assertEquals(-20, config.getPointsForIssue("ExceptionRaisedInUnexpectedLocation"))

        // Verify these are more severe than the default
        assertTrue(config.getPointsForIssue("UnsafeCallOnNullableType") < extension.defaultIssuePoints.get())
        assertTrue(config.getPointsForIssue("ExceptionRaisedInUnexpectedLocation") < extension.defaultIssuePoints.get())
    }

    @Test
    fun `should allow chaining configuration methods`() {
        val extension = createExtension()
        extension.apply {
            defaultIssuePoints.set(-8)
            issueScore("CustomIssue1", -10)
            issueScores(
                mapOf(
                    "CustomIssue2" to -15,
                    "CustomIssue3" to -20
                )
            )
        }

        val config = extension.toExtensionConfig().toScoringConfiguration()

        assertEquals(-8, config.defaultPoints)
        assertEquals(-10, config.getPointsForIssue("CustomIssue1"))
        assertEquals(-15, config.getPointsForIssue("CustomIssue2"))
        assertEquals(-20, config.getPointsForIssue("CustomIssue3"))
    }
}
