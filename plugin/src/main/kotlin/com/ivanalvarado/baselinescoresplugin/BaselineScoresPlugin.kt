package com.ivanalvarado.baselinescoresplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class BaselineScoresPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Register the extension
        val extension =
            project.extensions.create("baselineScores", BaselineScoresExtension::class.java)

        // Register tasks
        project.task("generateBaselineScores") {
            it.group = "baseline"
            it.description = "Generate baseline scores for the project"
            it.doLast {
                println("Generating baseline scores...")
                println("Configuration: ${extension.outputFile}")
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
