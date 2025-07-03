package com.ivanalvarado.baselinescoresplugin.infrastructure

import com.ivanalvarado.baselinescoresplugin.BaselineFileInfo
import com.ivanalvarado.baselinescoresplugin.domain.BaselineParser
import com.ivanalvarado.baselinescoresplugin.domain.BaselineProcessingException
import org.dom4j.DocumentHelper

class DetektBaselineParser : BaselineParser {

    override fun supportsFileBasedParsing(): Boolean = true

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
            throw BaselineProcessingException.FileParsingFailed(baselineFileInfo.file.name, e)
        }
    }

    override fun parseIssuesWithFileNames(baselineFileInfo: BaselineFileInfo): Map<String, Map<String, Int>> {
        return try {
            val xmlContent = baselineFileInfo.file.readText()
            val document = DocumentHelper.parseText(xmlContent)

            val currentIssuesElement = document.rootElement.element("CurrentIssues")
            val issueElements = currentIssuesElement?.elements("ID") ?: emptyList()

            val fileIssueBreakdown = mutableMapOf<String, MutableMap<String, Int>>()

            issueElements.forEach { element ->
                val issueId = element.text
                val issueType = extractIssueType(issueId)
                val fileName = extractFileName(issueId)

                val fileMap = fileIssueBreakdown.getOrPut(fileName) { mutableMapOf() }
                fileMap[issueType] = fileMap.getOrDefault(issueType, 0) + 1
            }

            fileIssueBreakdown
        } catch (e: Exception) {
            throw BaselineProcessingException.FileParsingFailed(baselineFileInfo.file.name, e)
        }
    }

    private fun extractIssueType(issueId: String): String {
        // Extract issue type from ID format like "FunctionNaming:BookmarksScreen.kt$..."
        return issueId.substringBefore(':').takeIf { it.isNotEmpty() } ?: "Unknown"
    }

    private fun extractFileName(issueId: String): String {
        // Extract file name from ID format like "FunctionNaming:BookmarksScreen.kt$..."
        val afterColon = issueId.substringAfter(':', "")
        return afterColon.substringBefore('$').takeIf { it.isNotEmpty() } ?: "Unknown"
    }
}
