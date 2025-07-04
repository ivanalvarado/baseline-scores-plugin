package com.ivanalvarado.baselinescoresplugin.application

import com.ivanalvarado.baselinescoresplugin.BaselineFileDetector
import com.ivanalvarado.baselinescoresplugin.BaselineScoresExtension
import com.ivanalvarado.baselinescoresplugin.domain.ConsoleReporter
import org.gradle.api.Project

class FindBaselineFilesUseCase(
    private val baselineFileDetector: BaselineFileDetector,
    private val consoleReporter: ConsoleReporter
) {

    fun execute(project: Project, extension: BaselineScoresExtension) {
        val baselineFiles = baselineFileDetector.findAllBaselineFiles(project, extension)
        consoleReporter.printBaselineFilesList(baselineFiles)
    }
}
