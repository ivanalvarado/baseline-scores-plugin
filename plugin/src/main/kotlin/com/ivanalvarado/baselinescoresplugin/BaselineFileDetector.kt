package com.ivanalvarado.baselinescoresplugin

import org.gradle.api.Project
import java.io.File

data class BaselineFileInfo(
    val file: File,
    val type: BaselineType,
    val module: String
)

enum class BaselineType {
    DETEKT,
    LINT
}

class BaselineFileDetector {

    fun findAllBaselineFiles(
        rootProject: Project,
        extension: BaselineScoresExtension
    ): List<BaselineFileInfo> {
        val baselineFiles = mutableListOf<BaselineFileInfo>()

        // Process root project and all subprojects (but allprojects includes root, so use subprojects)
        val allProjects = listOf(rootProject) + rootProject.subprojects.toList()

        for (project in allProjects) {
            if (extension.detektEnabled.get()) {
                findDetektBaseline(project, extension.detektBaselineFileName.get())?.let { file ->
                    baselineFiles.add(BaselineFileInfo(file, BaselineType.DETEKT, project.name))
                }
            }

            if (extension.lintEnabled.get()) {
                findLintBaseline(project, extension.lintBaselineFileName.get())?.let { file ->
                    baselineFiles.add(BaselineFileInfo(file, BaselineType.LINT, project.name))
                }
            }
        }

        return baselineFiles
    }

    private fun findDetektBaseline(project: Project, fileName: String): File? {
        // Check for detekt plugin and custom baseline configuration
        project.plugins.findPlugin("io.gitlab.arturbosch.detekt")?.let {
            val detektExtension = project.extensions.findByName("detekt")
            detektExtension?.let { ext ->
                try {
                    val baselineProperty = ext.javaClass.getMethod("getBaseline")
                    val customBaseline = baselineProperty.invoke(ext) as? File
                    if (customBaseline?.exists() == true) {
                        return customBaseline
                    }
                } catch (e: Exception) {
                    // Fallback to default location if reflection fails
                }
            }
        }

        // Check default location only if the file name matches expected detekt pattern
        if (fileName.contains("detekt")) {
            val defaultBaseline = File(project.projectDir, fileName)
            return if (defaultBaseline.exists()) defaultBaseline else null
        }

        return null
    }

    private fun findLintBaseline(project: Project, fileName: String): File? {
        // Check for Android plugin
        val androidExtension = project.extensions.findByName("android")
        androidExtension?.let { android ->
            try {
                val lintMethod = android.javaClass.getMethod("getLintOptions")
                val lintOptions = lintMethod.invoke(android)
                val baselineMethod = lintOptions.javaClass.getMethod("getBaseline")
                val customBaseline = baselineMethod.invoke(lintOptions) as? File
                if (customBaseline?.exists() == true) {
                    return customBaseline
                }
            } catch (e: Exception) {
                // Fallback to default location if reflection fails
            }
        }

        // Check default location only if the file name matches expected lint pattern
        if (fileName.contains("lint")) {
            val defaultBaseline = File(project.projectDir, fileName)
            return if (defaultBaseline.exists()) defaultBaseline else null
        }

        return null
    }
}
