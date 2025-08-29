package com.ivanalvarado.baselinescoresplugin

import java.io.File

/**
 * Contains project information extracted from Gradle Project for configuration cache compatibility.
 */
data class ProjectInfo(
    val name: String,
    val path: String,
    val projectDir: File,
    val buildDir: File,
    val subprojects: List<SubprojectInfo>
) : java.io.Serializable

/**
 * Contains subproject information extracted from Gradle Project for configuration cache compatibility.
 */
data class SubprojectInfo(
    val name: String,
    val path: String,
    val projectDir: File,
    val buildDir: File,
    val hasDetektPlugin: Boolean,
    val hasAndroidPlugin: Boolean
) : java.io.Serializable

/**
 * Contains extension configuration extracted from BaselineScoresExtension for configuration cache compatibility.
 */
data class ExtensionConfig(
    val detektEnabled: Boolean,
    val lintEnabled: Boolean,
    val detektBaselineFileName: String,
    val lintBaselineFileName: String,
    val defaultIssuePoints: Int = 1,
    val minimumScoreThreshold: Double = 0.8,
    val useDefaultDetektScoring: Boolean = true,
    val useDefaultLintScoring: Boolean = false,
    val userScoringRules: Map<String, Int> = emptyMap()
) : java.io.Serializable
