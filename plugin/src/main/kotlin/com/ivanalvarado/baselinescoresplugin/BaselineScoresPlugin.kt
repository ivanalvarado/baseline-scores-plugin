package com.ivanalvarado.baselinescoresplugin

import com.ivanalvarado.baselinescoresplugin.application.DefaultDomainServiceFactory
import com.ivanalvarado.baselinescoresplugin.application.FindBaselineFilesUseCase
import com.ivanalvarado.baselinescoresplugin.application.GenerateBaselineScoresUseCase
import com.ivanalvarado.baselinescoresplugin.application.ValidateBaselineScoresUseCase
import com.ivanalvarado.baselinescoresplugin.domain.BaselineProcessingException
import com.ivanalvarado.baselinescoresplugin.domain.DomainServiceFactory
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.*

class BaselineScoresPlugin : Plugin<Project> {

    private val serviceFactory: DomainServiceFactory = DefaultDomainServiceFactory()
    private val baselineFileDetector = ConfigCacheCompatibleBaselineFileDetector()

    override fun apply(project: Project) {
        val extension =
            project.extensions.create("baselineScores", BaselineScoresExtension::class.java)

        registerFindBaselineFilesTask(project, extension)
        registerGenerateBaselineScoresTask(project, extension)
        registerValidateBaselineScoresTask(project, extension)
    }

    private fun registerFindBaselineFilesTask(
        project: Project,
        extension: BaselineScoresExtension
    ) {
        project.tasks.register("findBaselineFiles", FindBaselineFilesTask::class.java) { task ->
            task.group = "baseline"
            task.description = "Find all baseline files in the project and its modules"

            // Extract project information into task properties
            task.projectDir.set(project.projectDir)
            task.projectName.set(project.name)
            task.projectPath.set(project.path)
            task.buildDir.set(project.layout.buildDirectory.get().asFile)

            // Extract subproject information
            val subprojectInfo = project.subprojects.map { subproject ->
                SubprojectInfo(
                    name = subproject.name,
                    path = subproject.path,
                    projectDir = subproject.projectDir,
                    buildDir = subproject.layout.buildDirectory.get().asFile,
                    hasDetektPlugin = subproject.plugins.hasPlugin("io.gitlab.arturbosch.detekt"),
                    hasAndroidPlugin = subproject.extensions.findByName("android") != null
                )
            }
            task.subprojects.set(subprojectInfo)

            // Extract extension configuration
            task.detektEnabled.set(extension.detektEnabled)
            task.lintEnabled.set(extension.lintEnabled)
            task.detektBaselineFileName.set(extension.detektBaselineFileName)
            task.lintBaselineFileName.set(extension.lintBaselineFileName)
            task.useDefaultDetektScoring.set(extension.useDefaultDetektScoring)
            task.useDefaultLintScoring.set(extension.useDefaultLintScoring)
            task.userScoringRules.set(extension.userScoringRules)
        }
    }

    private fun registerGenerateBaselineScoresTask(
        project: Project,
        extension: BaselineScoresExtension
    ) {
        project.tasks.register(
            "generateBaselineScores",
            GenerateBaselineScoresTask::class.java
        ) { task ->
            task.group = "baseline"
            task.description = "Generate baseline scores for the project"

            // Extract project information into task properties
            task.projectDir.set(project.projectDir)
            task.projectName.set(project.name)
            task.projectPath.set(project.path)
            task.buildDir.set(project.layout.buildDirectory.get().asFile)

            // Extract subproject information
            val subprojectInfo = project.subprojects.map { subproject ->
                SubprojectInfo(
                    name = subproject.name,
                    path = subproject.path,
                    projectDir = subproject.projectDir,
                    buildDir = subproject.layout.buildDirectory.get().asFile,
                    hasDetektPlugin = subproject.plugins.hasPlugin("io.gitlab.arturbosch.detekt"),
                    hasAndroidPlugin = subproject.extensions.findByName("android") != null
                )
            }
            task.subprojects.set(subprojectInfo)

            // Extract extension configuration
            task.detektEnabled.set(extension.detektEnabled)
            task.lintEnabled.set(extension.lintEnabled)
            task.detektBaselineFileName.set(extension.detektBaselineFileName)
            task.lintBaselineFileName.set(extension.lintBaselineFileName)
            task.defaultIssuePoints.set(extension.defaultIssuePoints)
            task.useDefaultDetektScoring.set(extension.useDefaultDetektScoring)
            task.useDefaultLintScoring.set(extension.useDefaultLintScoring)
            task.userScoringRules.set(extension.userScoringRules)
        }
    }

    private fun registerValidateBaselineScoresTask(
        project: Project,
        extension: BaselineScoresExtension
    ) {
        project.tasks.register(
            "validateBaselineScores",
            ValidateBaselineScoresTask::class.java
        ) { task ->
            task.group = "baseline"
            task.description = "Validate current scores against baseline"

            // Extract project information into task properties
            task.projectDir.set(project.projectDir)
            task.projectName.set(project.name)
            task.projectPath.set(project.path)
            task.buildDir.set(project.layout.buildDirectory.get().asFile)

            // Extract subproject information
            val subprojectInfo = project.subprojects.map { subproject ->
                SubprojectInfo(
                    name = subproject.name,
                    path = subproject.path,
                    projectDir = subproject.projectDir,
                    buildDir = subproject.layout.buildDirectory.get().asFile,
                    hasDetektPlugin = subproject.plugins.hasPlugin("io.gitlab.arturbosch.detekt"),
                    hasAndroidPlugin = subproject.extensions.findByName("android") != null
                )
            }
            task.subprojects.set(subprojectInfo)

            // Extract extension configuration
            task.detektEnabled.set(extension.detektEnabled)
            task.lintEnabled.set(extension.lintEnabled)
            task.detektBaselineFileName.set(extension.detektBaselineFileName)
            task.lintBaselineFileName.set(extension.lintBaselineFileName)
            task.defaultIssuePoints.set(extension.defaultIssuePoints)
            task.minimumScoreThreshold.set(extension.minimumScoreThreshold)
            task.useDefaultDetektScoring.set(extension.useDefaultDetektScoring)
            task.useDefaultLintScoring.set(extension.useDefaultLintScoring)
            task.userScoringRules.set(extension.userScoringRules)
        }
    }
}

data class SubprojectInfo(
    val name: String,
    val path: String,
    val projectDir: File,
    val buildDir: File,
    val hasDetektPlugin: Boolean,
    val hasAndroidPlugin: Boolean
) : java.io.Serializable

abstract class FindBaselineFilesTask : DefaultTask() {

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
            val useCase = FindBaselineFilesUseCase(
                baselineFileDetector,
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
                defaultIssuePoints = 1,
                minimumScoreThreshold = 0.8,
                useDefaultDetektScoring = useDefaultDetektScoring.get(),
                useDefaultLintScoring = useDefaultLintScoring.get(),
                userScoringRules = userScoringRules.get()
            )

            useCase.execute(projectInfo, extensionConfig)
        } catch (e: BaselineProcessingException) {
            handleError("Error finding baseline files", e)
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

abstract class GenerateBaselineScoresTask : DefaultTask() {

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
            val useCase = GenerateBaselineScoresUseCase(
                baselineFileDetector,
                serviceFactory.createBaselineScorer(),
                serviceFactory.createReportGenerator(),
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
                minimumScoreThreshold = 0.8,
                useDefaultDetektScoring = useDefaultDetektScoring.get(),
                useDefaultLintScoring = useDefaultLintScoring.get(),
                userScoringRules = userScoringRules.get()
            )

            val outputPath = useCase.execute(projectInfo, extensionConfig)
            println("Output file: $outputPath")
        } catch (e: BaselineProcessingException) {
            handleError("Error generating baseline scores", e)
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

data class ProjectInfo(
    val name: String,
    val path: String,
    val projectDir: File,
    val buildDir: File,
    val subprojects: List<SubprojectInfo>
) : java.io.Serializable

data class ExtensionConfig(
    val detektEnabled: Boolean,
    val lintEnabled: Boolean,
    val detektBaselineFileName: String,
    val lintBaselineFileName: String,
    val defaultIssuePoints: Int = 1,
    val minimumScoreThreshold: Double = 0.8,
    val useDefaultDetektScoring: Boolean = true,
    val useDefaultLintScoring: Boolean = false,
    val userScoringRules: Map<String, Int> = emptyMap()
) : java.io.Serializable

class ConfigCacheCompatibleBaselineFileDetector {

    fun findAllBaselineFiles(
        projectInfo: ProjectInfo,
        extensionConfig: ExtensionConfig
    ): List<BaselineFileInfo> {
        val baselineFiles = mutableListOf<BaselineFileInfo>()

        // Process root project and all subprojects
        val allProjects = listOf(
            SubprojectInfo(
                name = projectInfo.name,
                path = projectInfo.path,
                projectDir = projectInfo.projectDir,
                buildDir = projectInfo.buildDir,
                hasDetektPlugin = false, // Will be checked through file system
                hasAndroidPlugin = false // Will be checked through file system
            )
        ) + projectInfo.subprojects

        for (projectData in allProjects) {
            if (extensionConfig.detektEnabled) {
                findDetektBaseline(
                    projectData,
                    extensionConfig.detektBaselineFileName
                )?.let { file ->
                    baselineFiles.add(BaselineFileInfo(file, BaselineType.DETEKT, projectData.name))
                }
            }

            if (extensionConfig.lintEnabled) {
                findLintBaseline(projectData, extensionConfig.lintBaselineFileName)?.let { file ->
                    baselineFiles.add(BaselineFileInfo(file, BaselineType.LINT, projectData.name))
                }
            }
        }

        return baselineFiles
    }

    private fun findDetektBaseline(projectData: SubprojectInfo, fileName: String): File? {
        // Check default location only if the file name matches expected detekt pattern
        if (fileName.contains("detekt")) {
            val defaultBaseline = File(projectData.projectDir, fileName)
            return if (defaultBaseline.exists()) defaultBaseline else null
        }

        return null
    }

    private fun findLintBaseline(projectData: SubprojectInfo, fileName: String): File? {
        // Check default location only if the file name matches expected lint pattern
        if (fileName.contains("lint")) {
            val defaultBaseline = File(projectData.projectDir, fileName)
            return if (defaultBaseline.exists()) defaultBaseline else null
        }

        return null
    }
}
