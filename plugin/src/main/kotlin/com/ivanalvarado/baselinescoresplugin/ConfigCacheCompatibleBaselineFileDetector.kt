package com.ivanalvarado.baselinescoresplugin

import java.io.File

/**
 * Configuration cache compatible baseline file detector.
 *
 * This detector works with serializable data structures instead of Gradle Project objects,
 * making it compatible with Gradle's configuration cache feature.
 */
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
