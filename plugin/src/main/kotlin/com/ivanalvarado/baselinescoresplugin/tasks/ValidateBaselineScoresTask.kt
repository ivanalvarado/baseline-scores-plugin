package com.ivanalvarado.baselinescoresplugin.tasks

import com.ivanalvarado.baselinescoresplugin.application.DefaultDomainServiceFactory
import com.ivanalvarado.baselinescoresplugin.application.ValidateBaselineScoresUseCase
import com.ivanalvarado.baselinescoresplugin.ConfigCacheCompatibleBaselineFileDetector
import com.ivanalvarado.baselinescoresplugin.ProjectInfo
import com.ivanalvarado.baselinescoresplugin.SubprojectInfo
import com.ivanalvarado.baselinescoresplugin.ExtensionConfig
import com.ivanalvarado.baselinescoresplugin.domain.BaselineProcessingException
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
abstract class ValidateBaselineScoresTask : DefaultTask() {

    @get:InputDirectory
    abstract val projectDir: DirectoryProperty

    @get:Input
    abstract val projectName: Property<String>

    @get:Input
    abstract val projectPath: Property<String>

    @get:InputDirectory
    abstract val buildDir: DirectoryProperty

    @get:Input
    abstract val subprojects: ListProperty<SubprojectInfo>

    @get:Input
    abstract val detektEnabled: Property<Boolean>

    @get:Input
    abstract val lintEnabled: Property<Boolean>

    @get:Input
    abstract val detektBaselineFileName: Property<String>

    @get:Input
    abstract val lintBaselineFileName: Property<String>

    @get:Input
    abstract val defaultIssuePoints: Property<Int>

    @get:Input
    abstract val minimumScoreThreshold: Property<Double>

    @get:Input
    abstract val useDefaultDetektScoring: Property<Boolean>

    @get:Input
    abstract val useDefaultLintScoring: Property<Boolean>

    @get:Input
    abstract val userScoringRules: MapProperty<String, Int>

    @TaskAction
    fun execute() {
        try {
            val serviceFactory = DefaultDomainServiceFactory()
            val baselineFileDetector = ConfigCacheCompatibleBaselineFileDetector()
            val useCase = ValidateBaselineScoresUseCase(
                baselineFileDetector,
                serviceFactory.createBaselineScorer(),
                serviceFactory.createConsoleReporter()
            )

            val projectInfo = ProjectInfo(
                name = projectName.get(),
                path = projectPath.get(),
                projectDir = projectDir.get().asFile,
                buildDir = buildDir.get().asFile,
                subprojects = subprojects.get()
            )

            val extensionConfig = ExtensionConfig(
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

            val result = useCase.execute(projectInfo, extensionConfig)

            if (!result.isValid) {
                throw RuntimeException("Baseline validation failed: ${result.message}")
            }
        } catch (e: BaselineProcessingException) {
            handleError("Error validating baseline scores", e)
        }
    }

    private fun handleError(message: String, exception: BaselineProcessingException) {
        println("$message: ${exception.message}")
        exception.cause?.let { cause ->
            println("Caused by: ${cause.message}")
        }
        throw exception
    }
}
