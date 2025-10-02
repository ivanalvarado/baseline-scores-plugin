package com.ivanalvarado.baselinescoresplugin

import com.ivanalvarado.baselinescoresplugin.tasks.FindBaselineFilesTask
import com.ivanalvarado.baselinescoresplugin.tasks.GenerateBaselineScoresTask
import com.ivanalvarado.baselinescoresplugin.tasks.ValidateBaselineScoresTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Gradle plugin for calculating baseline scores from static analysis tools.
 *
 * This plugin is configuration cache compatible, meaning it extracts all necessary
 * information from the Project during configuration time and stores it as serializable
 * task properties.
 */
class BaselineScoresPlugin : Plugin<Project> {

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
            configureTaskWithBaselineGroup(task)
            task.description = "Find all baseline files in the project and its modules"

            configureTaskWithProjectInformation(task, project)
            configureTaskWithSubprojectInformation(task, project)
            configureTaskWithExtensionSettings(task, extension)
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
            configureTaskWithBaselineGroup(task)
            task.description = "Generate baseline scores for the project"

            configureTaskWithProjectInformation(task, project)
            configureTaskWithSubprojectInformation(task, project)
            configureTaskWithExtensionSettings(task, extension)
            task.defaultIssuePoints.set(extension.defaultIssuePoints)
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
            configureTaskWithBaselineGroup(task)
            task.description = "Validate current scores against baseline"

            configureTaskWithProjectInformation(task, project)
            configureTaskWithSubprojectInformation(task, project)
            configureTaskWithExtensionSettings(task, extension)
            task.defaultIssuePoints.set(extension.defaultIssuePoints)
            task.minimumScoreThreshold.set(extension.minimumScoreThreshold)
        }
    }

    private fun configureTaskWithBaselineGroup(task: Task) {
        task.group = "baseline"
    }

    private fun configureTaskWithProjectInformation(task: Task, project: Project) {
        val taskWithProjectProps = task as? HasProjectProperties ?: return

        taskWithProjectProps.projectDir.set(project.projectDir)
        taskWithProjectProps.projectName.set(project.name)
        taskWithProjectProps.projectPath.set(project.path)
        taskWithProjectProps.buildDir.set(project.layout.buildDirectory.get().asFile)
    }

    private fun configureTaskWithSubprojectInformation(task: Task, project: Project) {
        val taskWithSubprojectProps = task as? HasSubprojectProperties ?: return

        val subprojectInformation = extractSubprojectInformation(project)
        taskWithSubprojectProps.subprojects.set(subprojectInformation)
    }

    private fun extractSubprojectInformation(project: Project): List<SubprojectInfo> {
        return project.subprojects.map { subproject ->
            SubprojectInfo(
                name = subproject.name,
                path = subproject.path,
                projectDir = subproject.projectDir,
                buildDir = subproject.layout.buildDirectory.get().asFile,
                hasDetektPlugin = subproject.plugins.hasPlugin("io.gitlab.arturbosch.detekt"),
                hasAndroidPlugin = subproject.extensions.findByName("android") != null
            )
        }
    }

    private fun configureTaskWithExtensionSettings(task: Task, extension: BaselineScoresExtension) {
        val taskWithExtensionProps = task as? HasExtensionProperties ?: return

        taskWithExtensionProps.detektEnabled.set(extension.detektEnabled)
        taskWithExtensionProps.lintEnabled.set(extension.lintEnabled)
        taskWithExtensionProps.detektBaselineFileName.set(extension.detektBaselineFileName)
        taskWithExtensionProps.lintBaselineFileName.set(extension.lintBaselineFileName)
        taskWithExtensionProps.useDefaultDetektScoring.set(extension.useDefaultDetektScoring)
        taskWithExtensionProps.useDefaultLintScoring.set(extension.useDefaultLintScoring)
        taskWithExtensionProps.userScoringRules.set(extension.userScoringRules)
    }
}

/**
 * Interface for tasks that need project information.
 * This makes the configuration methods type-safe and self-documenting.
 */
interface HasProjectProperties {
    val projectDir: org.gradle.api.file.DirectoryProperty
    val projectName: org.gradle.api.provider.Property<String>
    val projectPath: org.gradle.api.provider.Property<String>
    val buildDir: org.gradle.api.file.DirectoryProperty
}

/**
 * Interface for tasks that need subproject information.
 */
interface HasSubprojectProperties {
    val subprojects: org.gradle.api.provider.ListProperty<SubprojectInfo>
}

/**
 * Interface for tasks that need extension configuration.
 */
interface HasExtensionProperties {
    val detektEnabled: org.gradle.api.provider.Property<Boolean>
    val lintEnabled: org.gradle.api.provider.Property<Boolean>
    val detektBaselineFileName: org.gradle.api.provider.Property<String>
    val lintBaselineFileName: org.gradle.api.provider.Property<String>
    val useDefaultDetektScoring: org.gradle.api.provider.Property<Boolean>
    val useDefaultLintScoring: org.gradle.api.provider.Property<Boolean>
    val userScoringRules: org.gradle.api.provider.MapProperty<String, Int>
}
