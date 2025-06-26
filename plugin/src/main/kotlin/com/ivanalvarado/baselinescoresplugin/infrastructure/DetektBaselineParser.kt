package com.ivanalvarado.baselinescoresplugin.infrastructure

import com.ivanalvarado.baselinescoresplugin.BaselineFileInfo
import com.ivanalvarado.baselinescoresplugin.domain.BaselineParser
import org.dom4j.DocumentHelper

class DetektBaselineParser : BaselineParser {

    override fun parseIssues(baselineFileInfo: BaselineFileInfo): Map<String, Int> {
        return try {
            val xmlContent = baselineFileInfo.file.readText()
            val document = DocumentHelper.parseText(xmlContent)

            val currentIssuesElement = document.rootElement.element("CurrentIssues")
            val issueElements = currentIssuesElement?.elements("ID") ?: emptyList()

            val issueBreakdown = mutableMapOf<String, Int>()

            issueElements.forEach { element ->
                val issueId = element.text
                val issueType = extractIssueType(issueId)
                issueBreakdown[issueType] = issueBreakdown.getOrDefault(issueType, 0) + 1
            }

            issueBreakdown
        } catch (e: Exception) {
            println("Error parsing detekt baseline file ${baselineFileInfo.file.name}: ${e.message}")
            emptyMap()
        }
    }

    private fun extractIssueType(issueId: String): String {
        // Extract issue type from ID format like "FunctionNaming:BookmarksScreen.kt$..."
        return issueId.substringBefore(':').takeIf { it.isNotEmpty() } ?: "Unknown"
    }
}
