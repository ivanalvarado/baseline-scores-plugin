package com.ivanalvarado.baselinescoresplugin

import java.io.File

/**
 * Information about a baseline file discovered in the project.
 *
 * @property file The baseline file on the filesystem
 * @property type The type of baseline (Detekt, Lint, etc.)
 * @property module The name of the module where this baseline was found
 */
data class BaselineFileInfo(
    val file: File,
    val type: BaselineType,
    val module: String
)

/**
 * Types of baseline files supported by this plugin.
 */
enum class BaselineType {
    /** Detekt static analysis baseline */
    DETEKT,

    /** Android Lint baseline */
    LINT
}
