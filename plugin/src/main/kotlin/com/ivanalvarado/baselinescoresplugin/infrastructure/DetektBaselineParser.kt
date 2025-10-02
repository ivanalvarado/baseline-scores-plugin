package com.ivanalvarado.baselinescoresplugin.infrastructure

import com.ivanalvarado.baselinescoresplugin.BaselineFileInfo
import com.ivanalvarado.baselinescoresplugin.domain.BaselineParser
import com.ivanalvarado.baselinescoresplugin.domain.BaselineProcessingException
import org.dom4j.DocumentHelper

class DetektBaselineParser : BaselineParser {

    override fun supportsFileBasedParsing(): Boolean = true

    override fun parseIssues(baselineFileInfo: BaselineFileInfo): Map<String, Int> {
        return try {
            val document = parseXmlDocument(baselineFileInfo)
            val issueElements = extractIssueElements(document)

            countIssuesByType(issueElements)
        } catch (e: Exception) {
            throw BaselineProcessingException.FileParsingFailed(baselineFileInfo.file.name, e)
        }
    }

    override fun parseIssuesWithFileNames(baselineFileInfo: BaselineFileInfo): Map<String, Map<String, Int>> {
        return try {
            val document = parseXmlDocument(baselineFileInfo)
            val issueElements = extractIssueElements(document)

            groupIssuesByFileAndType(issueElements)
        } catch (e: Exception) {
            throw BaselineProcessingException.FileParsingFailed(baselineFileInfo.file.name, e)
        }
    }

    private fun parseXmlDocument(baselineFileInfo: BaselineFileInfo): org.dom4j.Document {
        val xmlContent = baselineFileInfo.file.readText()
        return DocumentHelper.parseText(xmlContent)
    }

    private fun extractIssueElements(document: org.dom4j.Document): List<org.dom4j.Element> {
        val currentIssuesElement = document.rootElement.element("CurrentIssues")
        return currentIssuesElement?.elements("ID") ?: emptyList()
    }

    private fun countIssuesByType(issueElements: List<org.dom4j.Element>): Map<String, Int> {
        val issueBreakdown = mutableMapOf<String, Int>()

        issueElements.forEach { element ->
            val issueId = element.text
            val issueType = extractIssueTypeFrom(issueId)

            val currentCount = issueBreakdown.getOrDefault(issueType, 0)
            issueBreakdown[issueType] = currentCount + 1
        }

        return issueBreakdown
    }

    private fun groupIssuesByFileAndType(issueElements: List<org.dom4j.Element>): Map<String, Map<String, Int>> {
        val fileIssueBreakdown = mutableMapOf<String, MutableMap<String, Int>>()

        issueElements.forEach { element ->
            val issueId = element.text
            val issueType = extractIssueTypeFrom(issueId)
            val fileName = extractFileNameFrom(issueId)

            val issuesForFile = fileIssueBreakdown.getOrPut(fileName) { mutableMapOf() }
            val currentCount = issuesForFile.getOrDefault(issueType, 0)
            issuesForFile[issueType] = currentCount + 1
        }

        return fileIssueBreakdown
    }

    private fun extractIssueTypeFrom(issueId: String): String {
        val issueTypeBeforeColon = issueId.substringBefore(':')

        return if (issueTypeIsValid(issueTypeBeforeColon)) {
            issueTypeBeforeColon
        } else {
            UNKNOWN_ISSUE_TYPE
        }
    }

    private fun extractFileNameFrom(issueId: String): String {
        val contentAfterColon = issueId.substringAfter(':', "")
        val fileNameBeforeDollar = contentAfterColon.substringBefore('$')

        return if (fileNameIsValid(fileNameBeforeDollar)) {
            fileNameBeforeDollar
        } else {
            UNKNOWN_FILE_NAME
        }
    }

    private fun issueTypeIsValid(issueType: String): Boolean {
        return issueType.isNotEmpty()
    }

    private fun fileNameIsValid(fileName: String): Boolean {
        return fileName.isNotEmpty()
    }

    private companion object {
        const val UNKNOWN_ISSUE_TYPE = "Unknown"
        const val UNKNOWN_FILE_NAME = "Unknown"
    }
}
