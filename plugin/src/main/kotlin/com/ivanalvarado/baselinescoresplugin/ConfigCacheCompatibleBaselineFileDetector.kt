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
        val allProjectsToScan = buildProjectListIncludingRoot(projectInfo)

        return allProjectsToScan.flatMap { projectData ->
            findBaselinesInProject(projectData, extensionConfig)
        }
    }

    private fun buildProjectListIncludingRoot(projectInfo: ProjectInfo): List<SubprojectInfo> {
        val rootProjectAsSubproject = SubprojectInfo(
            name = projectInfo.name,
            path = projectInfo.path,
            projectDir = projectInfo.projectDir,
            buildDir = projectInfo.buildDir,
            hasDetektPlugin = false,
            hasAndroidPlugin = false
        )

        return listOf(rootProjectAsSubproject) + projectInfo.subprojects
    }

    private fun findBaselinesInProject(
        projectData: SubprojectInfo,
        extensionConfig: ExtensionConfig
    ): List<BaselineFileInfo> {
        val baselines = mutableListOf<BaselineFileInfo>()

        if (extensionConfig.detektEnabled) {
            findDetektBaselineFile(projectData, extensionConfig.detektBaselineFileName)
                ?.let { file ->
                    baselines.add(
                        BaselineFileInfo(
                            file,
                            BaselineType.DETEKT,
                            projectData.name
                        )
                    )
                }
        }

        if (extensionConfig.lintEnabled) {
            findLintBaselineFile(projectData, extensionConfig.lintBaselineFileName)
                ?.let { file ->
                    baselines.add(
                        BaselineFileInfo(
                            file,
                            BaselineType.LINT,
                            projectData.name
                        )
                    )
                }
        }

        return baselines
    }

    private fun findDetektBaselineFile(projectData: SubprojectInfo, fileName: String): File? {
        return findBaselineFileIfNameMatches(projectData.projectDir, fileName, "detekt")
    }

    private fun findLintBaselineFile(projectData: SubprojectInfo, fileName: String): File? {
        return findBaselineFileIfNameMatches(projectData.projectDir, fileName, "lint")
    }

    private fun findBaselineFileIfNameMatches(
        projectDir: File,
        fileName: String,
        expectedPattern: String
    ): File? {
        if (!fileName.contains(expectedPattern)) {
            return null
        }

        val baselineFile = File(projectDir, fileName)
        return if (baselineFile.exists()) baselineFile else null
    }
}
