package com.ivanalvarado.baselinescoresplugin.tasks

import com.ivanalvarado.baselinescoresplugin.application.DefaultDomainServiceFactory
import com.ivanalvarado.baselinescoresplugin.application.FindBaselineFilesUseCase
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
 * Task for finding all baseline files in the project and its modules.
 *
 * This task is configuration cache compatible as it only depends on serializable
 * properties and doesn't hold references to Gradle Project objects.
 */
abstract class FindBaselineFilesTask : DefaultTask(),
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
    abstract override val useDefaultDetektScoring: Property<Boolean>

    @get:Input
    abstract override val useDefaultLintScoring: Property<Boolean>

    @get:Input
    abstract override val userScoringRules: MapProperty<String, Int>

    @TaskAction
    fun execute() {
        try {
            val useCase = createFindBaselineFilesUseCase()
            val projectInfo = extractProjectInfo()
            val extensionConfig = extractExtensionConfig()

            useCase.execute(projectInfo, extensionConfig)
        } catch (e: BaselineProcessingException) {
            handleError("Error finding baseline files", e)
        }
    }

    private fun createFindBaselineFilesUseCase(): FindBaselineFilesUseCase {
        val serviceFactory = DefaultDomainServiceFactory()
        val baselineFileDetector = ConfigCacheCompatibleBaselineFileDetector()

        return FindBaselineFilesUseCase(
            baselineFileDetector,
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
            defaultIssuePoints = 1,
            minimumScoreThreshold = 0.8,
            useDefaultDetektScoring = useDefaultDetektScoring.get(),
            useDefaultLintScoring = useDefaultLintScoring.get(),
            userScoringRules = userScoringRules.get()
        )
    }

    private fun handleError(message: String, exception: BaselineProcessingException) {
        println("$message: ${exception.message}")
        exception.cause?.let { cause ->
            println("Caused by: ${cause.message}")
        }
        throw exception
    }
}
