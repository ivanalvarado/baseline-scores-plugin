package com.ivanalvarado.baselinescoresplugin.application

import com.ivanalvarado.baselinescoresplugin.ConfigCacheCompatibleBaselineFileDetector
import com.ivanalvarado.baselinescoresplugin.ProjectInfo
import com.ivanalvarado.baselinescoresplugin.ExtensionConfig
import com.ivanalvarado.baselinescoresplugin.domain.ConsoleReporter

class FindBaselineFilesUseCase(
    private val baselineFileDetector: ConfigCacheCompatibleBaselineFileDetector,
    private val consoleReporter: ConsoleReporter
) {

    fun execute(projectInfo: ProjectInfo, extensionConfig: ExtensionConfig) {
        val baselineFiles = baselineFileDetector.findAllBaselineFiles(projectInfo, extensionConfig)
        consoleReporter.printBaselineFilesList(baselineFiles)
    }
}
