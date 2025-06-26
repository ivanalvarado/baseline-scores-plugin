package com.ivanalvarado.baselinescoresplugin

import org.dom4j.DocumentHelper
import java.io.File

data class BaselineScore(
    val module: String,
    val type: BaselineType,
    val issueCount: Int,
    val score: Int
)

class BaselineScoreCalculator {

    companion object {
        private const val ISSUE_PENALTY = -5
    }

    fun calculateScore(baselineFileInfo: BaselineFileInfo): BaselineScore {
        return when (baselineFileInfo.type) {
            BaselineType.DETEKT -> calculateDetektScore(baselineFileInfo)
            BaselineType.LINT -> calculateLintScore(baselineFileInfo)
        }
    }

    private fun calculateDetektScore(baselineFileInfo: BaselineFileInfo): BaselineScore {
        val file = baselineFileInfo.file
        val issueCount = parseDetektBaseline(file)
        val score = issueCount * ISSUE_PENALTY

        return BaselineScore(
            module = baselineFileInfo.module,
            type = BaselineType.DETEKT,
            issueCount = issueCount,
            score = score
        )
    }

    private fun calculateLintScore(baselineFileInfo: BaselineFileInfo): BaselineScore {
        // For now, return 0 for lint files since we're focusing on detekt
        return BaselineScore(
            module = baselineFileInfo.module,
            type = BaselineType.LINT,
            issueCount = 0,
            score = 0
        )
    }

    private fun parseDetektBaseline(file: File): Int {
        try {
            val xmlContent = file.readText()
            val document = DocumentHelper.parseText(xmlContent)

            // Navigate to CurrentIssues element and count ID elements
            val currentIssuesElement = document.rootElement.element("CurrentIssues")
            return currentIssuesElement?.elements("ID")?.size ?: 0

        } catch (e: Exception) {
            println("Error parsing detekt baseline file ${file.name}: ${e.message}")
            return 0
        }
    }
}
