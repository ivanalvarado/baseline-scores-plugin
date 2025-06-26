package com.ivanalvarado.baselinescoresplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class BaselineScoresPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register the extension
        val extension =
            project.extensions.create("baselineScores", BaselineScoresExtension::class.java)

        val detector = BaselineFileDetector()

        // Register tasks
        project.task("findBaselineFiles") {
            it.group = "baseline"
            it.description = "Find all baseline files in the project and its modules"
            it.doLast {
                val baselineFiles = detector.findAllBaselineFiles(project.rootProject, extension)

                if (baselineFiles.isEmpty()) {
                    println("No baseline files found in the project.")
                } else {
                    println("Found ${baselineFiles.size} baseline file(s):")
                    baselineFiles.forEach { info ->
                        println("  [${info.type}] ${info.module}: ${info.file.absolutePath}")
                    }
                }
            }
        }

        project.task("generateBaselineScores") {
            it.group = "baseline"
            it.description = "Generate baseline scores for the project"
            it.doLast {
                val baselineFiles = detector.findAllBaselineFiles(project.rootProject, extension)
                println("Generating baseline scores for ${baselineFiles.size} baseline file(s)...")

                baselineFiles.forEach { info ->
                    println("Processing ${info.type} baseline for module '${info.module}': ${info.file.name}")
                }

                println("Output file: ${extension.outputFile}")
            }
        }

        project.task("validateBaselineScores") {
            it.group = "baseline"
            it.description = "Validate current scores against baseline"
            it.doLast {
                println("Validating baseline scores...")
                println("Threshold: ${extension.threshold}")
            }
        }
    }
}
