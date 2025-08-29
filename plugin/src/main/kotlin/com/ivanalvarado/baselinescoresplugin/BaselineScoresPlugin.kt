package com.ivanalvarado.baselinescoresplugin

import com.ivanalvarado.baselinescoresplugin.tasks.FindBaselineFilesTask
import com.ivanalvarado.baselinescoresplugin.tasks.GenerateBaselineScoresTask
import com.ivanalvarado.baselinescoresplugin.tasks.ValidateBaselineScoresTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

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
