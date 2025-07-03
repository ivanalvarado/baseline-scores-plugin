package com.ivanalvarado.baselinescoresplugin

import com.ivanalvarado.baselinescoresplugin.application.DefaultDomainServiceFactory
import com.ivanalvarado.baselinescoresplugin.application.FindBaselineFilesUseCase
import com.ivanalvarado.baselinescoresplugin.application.GenerateBaselineScoresUseCase
import com.ivanalvarado.baselinescoresplugin.application.ValidateBaselineScoresUseCase
import com.ivanalvarado.baselinescoresplugin.domain.BaselineProcessingException
import com.ivanalvarado.baselinescoresplugin.domain.DomainServiceFactory
import org.gradle.api.Plugin
import org.gradle.api.Project

class BaselineScoresPlugin : Plugin<Project> {

    private val serviceFactory: DomainServiceFactory = DefaultDomainServiceFactory()
    private val baselineFileDetector = BaselineFileDetector()

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
        project.task("findBaselineFiles") {
            it.group = "baseline"
            it.description = "Find all baseline files in the project and its modules"
            it.doLast {
                try {
                    val useCase = FindBaselineFilesUseCase(
                        baselineFileDetector,
                        serviceFactory.createConsoleReporter()
                    )
                    useCase.execute(project, extension)
                } catch (e: BaselineProcessingException) {
                    handleError("Error finding baseline files", e)
                }
            }
        }
    }

    private fun registerGenerateBaselineScoresTask(
        project: Project,
        extension: BaselineScoresExtension
    ) {
        project.task("generateBaselineScores") {
            it.group = "baseline"
            it.description = "Generate baseline scores for the project"
            it.doLast {
                try {
                    val useCase = GenerateBaselineScoresUseCase(
                        baselineFileDetector,
                        serviceFactory.createBaselineScorer(),
                        serviceFactory.createReportGenerator(),
                        serviceFactory.createConsoleReporter()
                    )

                    val outputPath = useCase.execute(project, extension)
                    println("Output file: $outputPath")
                } catch (e: BaselineProcessingException) {
                    handleError("Error generating baseline scores", e)
                }
            }
        }
    }

    private fun registerValidateBaselineScoresTask(
        project: Project,
        extension: BaselineScoresExtension
    ) {
        project.task("validateBaselineScores") {
            it.group = "baseline"
            it.description = "Validate current scores against baseline"
            it.doLast {
                try {
                    val useCase = ValidateBaselineScoresUseCase(
                        baselineFileDetector,
                        serviceFactory.createBaselineScorer(),
                        serviceFactory.createConsoleReporter()
                    )

                    val result = useCase.execute(project, extension)

                    if (!result.isValid) {
                        throw RuntimeException("Baseline validation failed: ${result.message}")
                    }
                } catch (e: BaselineProcessingException) {
                    handleError("Error validating baseline scores", e)
                }
            }
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
