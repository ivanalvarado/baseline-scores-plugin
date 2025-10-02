package com.ivanalvarado.baselinescoresplugin.tasks

import com.ivanalvarado.baselinescoresplugin.application.DefaultDomainServiceFactory
import com.ivanalvarado.baselinescoresplugin.application.ValidateBaselineScoresUseCase
import com.ivanalvarado.baselinescoresplugin.ConfigCacheCompatibleBaselineFileDetector
import com.ivanalvarado.baselinescoresplugin.ProjectInfo
import com.ivanalvarado.baselinescoresplugin.SubprojectInfo
import com.ivanalvarado.baselinescoresplugin.ExtensionConfig
import com.ivanalvarado.baselinescoresplugin.domain.BaselineProcessingException
import com.ivanalvarado.baselinescoresplugin.HasProjectProperties
import com.ivanalvarado.baselinescoresplugin.HasSubprojectProperties
import com.ivanalvarado.baselinescoresplugin.HasExtensionProperties
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * Task for validating current scores against baseline thresholds.
 *
 * This task is configuration cache compatible as it only depends on serializable
 * properties and doesn't hold references to Gradle Project objects.
 */
abstract class ValidateBaselineScoresTask : DefaultTask(),
    HasProjectProperties,
    HasSubprojectProperties,
    HasExtensionProperties {

    @get:InputDirectory
    abstract override val projectDir: DirectoryProperty

    @get:Input
    abstract override val projectName: Property<String>

    @get:Input
    abstract override val projectPath: Property<String>

    @get:InputDirectory
    abstract override val buildDir: DirectoryProperty

    @get:Input
    abstract override val subprojects: ListProperty<SubprojectInfo>

    @get:Input
    abstract override val detektEnabled: Property<Boolean>

    @get:Input
    abstract override val lintEnabled: Property<Boolean>

    @get:Input
    abstract override val detektBaselineFileName: Property<String>

    @get:Input
    abstract override val lintBaselineFileName: Property<String>

    @get:Input
    abstract val defaultIssuePoints: Property<Int>

    @get:Input
    abstract val minimumScoreThreshold: Property<Double>

    @get:Input
    abstract override val useDefaultDetektScoring: Property<Boolean>

    @get:Input
    abstract override val useDefaultLintScoring: Property<Boolean>

    @get:Input
    abstract override val userScoringRules: MapProperty<String, Int>

    @TaskAction
    fun execute() {
        try {
            val useCase = createValidateBaselineScoresUseCase()
            val projectInfo = extractProjectInfo()
            val extensionConfig = extractExtensionConfig()

            val result = useCase.execute(projectInfo, extensionConfig)

            if (!result.isValid) {
                throw RuntimeException("Baseline validation failed: ${result.message}")
            }
        } catch (e: BaselineProcessingException) {
            logger.error("Failed to validate baseline scores: ${e.message}", e)
            throw e
        }
    }

    private fun createValidateBaselineScoresUseCase(): ValidateBaselineScoresUseCase {
        val serviceFactory = DefaultDomainServiceFactory()
        val baselineFileDetector = ConfigCacheCompatibleBaselineFileDetector()

        return ValidateBaselineScoresUseCase(
            baselineFileDetector,
            serviceFactory.createBaselineScorer(),
            serviceFactory.createConsoleReporter()
        )
    }

    private fun extractProjectInfo(): ProjectInfo {
        return ProjectInfo(
            name = projectName.get(),
            path = projectPath.get(),
            projectDir = projectDir.get().asFile,
            buildDir = buildDir.get().asFile,
            subprojects = subprojects.get()
        )
    }

    private fun extractExtensionConfig(): ExtensionConfig {
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
}
