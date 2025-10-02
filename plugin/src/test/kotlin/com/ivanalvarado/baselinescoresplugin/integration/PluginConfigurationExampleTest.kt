package com.ivanalvarado.baselinescoresplugin.integration

import com.ivanalvarado.baselinescoresplugin.BaselineScoresExtension
import com.ivanalvarado.baselinescoresplugin.ExtensionConfig
import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Integration test showing how to configure the plugin with custom scoring rules
 */
class PluginConfigurationExampleTest {

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
    fun `should demonstrate basic plugin configuration`() {
        val extension = createExtension()

        // Basic configuration
        extension.apply {
            outputFile.set("my-baseline-scores.json")
            defaultIssuePoints.set(-8)

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

        val config = extension.toExtensionConfig().toScoringConfiguration()

        // Verify configuration
        assertEquals("my-baseline-scores.json", extension.outputFile.get())
        assertEquals(-8, extension.defaultIssuePoints.get())
        assertEquals(-20, config.getPointsForIssue("ComplexMethod"))
        assertEquals(-15, config.getPointsForIssue("LongMethod"))
        assertEquals(-2, config.getPointsForIssue("MagicNumber"))
        assertEquals(-5, config.getPointsForIssue("UnusedPrivateMember"))
        assertEquals(-3, config.getPointsForIssue("FunctionNaming"))
        assertEquals(-8, config.getPointsForIssue("UnknownIssueType"))
    }

    @Test
    fun `should demonstrate severity-based scoring configuration`() {
        val extension = createExtension()

        // Configure based on severity levels
        extension.apply {
            defaultIssuePoints.set(-5)

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

        val config = extension.toExtensionConfig().toScoringConfiguration()

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
        val extension = createExtension()

        // Strategy: Start with low penalties and gradually increase
        extension.apply {
            defaultIssuePoints.set(-1)  // Very mild penalty for new issues

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

        val config = extension.toExtensionConfig().toScoringConfiguration()

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
