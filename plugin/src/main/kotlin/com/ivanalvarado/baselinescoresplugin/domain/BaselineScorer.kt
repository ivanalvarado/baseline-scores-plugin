package com.ivanalvarado.baselinescoresplugin.domain

import com.ivanalvarado.baselinescoresplugin.BaselineFileInfo
import com.ivanalvarado.baselinescoresplugin.ProjectInfo

/**
 * Exception thrown when baseline processing fails
 */
sealed class BaselineProcessingException(message: String, cause: Throwable? = null) :
    Exception(message, cause) {
    class ParserNotFound(baselineType: String) :
        BaselineProcessingException("No parser available for baseline type: $baselineType")

    class FileBasedParsingNotSupported(baselineType: String) :
        BaselineProcessingException("File-based parsing not supported for baseline type: $baselineType")

    class FileParsingFailed(fileName: String, cause: Throwable) :
        BaselineProcessingException("Failed to parse baseline file: $fileName", cause)

    class ReportGenerationFailed(cause: Throwable) :
        BaselineProcessingException("Failed to generate report", cause)
}

/**
 * Interface for scoring baseline files
 */
interface BaselineScorer {
    fun scoreBaseline(
        baselineFileInfo: BaselineFileInfo,
        configuration: ScoringConfiguration
    ): ScoringResult

    fun scoreBaselineWithFiles(
        baselineFileInfo: BaselineFileInfo,
        configuration: ScoringConfiguration
    ): FileScoringResult
}

/**
 * Interface for parsing baseline files to extract issue information
 */
interface BaselineParser {
    fun parseIssues(baselineFileInfo: BaselineFileInfo): Map<String, Int>
    fun parseIssuesWithFileNames(baselineFileInfo: BaselineFileInfo): Map<String, Map<String, Int>>
    fun supportsFileBasedParsing(): Boolean
}

/**
 * Interface for calculating scores from issue data
 */
interface ScoreCalculator {
    fun calculateScore(
        issueBreakdown: Map<String, Int>,
        configuration: ScoringConfiguration,
        module: String,
        type: com.ivanalvarado.baselinescoresplugin.BaselineType
    ): ScoringResult

    fun calculateFileBasedScore(
        fileIssueBreakdown: Map<String, Map<String, Int>>,
        configuration: ScoringConfiguration,
        module: String,
        type: com.ivanalvarado.baselinescoresplugin.BaselineType
    ): FileScoringResult
}

/**
 * Interface for generating output reports
 */
interface ReportGenerator {
    fun generateJsonReport(
        projectInfo: ProjectInfo,
        moduleScores: List<FileScoringResult>,
        totalProjectScore: Int
    ): String
}

/**
 * Interface for formatting console output
 */
interface ConsoleReporter {
    fun printModuleScore(result: FileScoringResult)
    fun printProjectSummary(totalScore: Int, moduleScores: List<FileScoringResult>)
    fun printBaselineFilesList(baselineFiles: List<BaselineFileInfo>)
}

/**
 * Factory interface for creating domain services
 */
interface DomainServiceFactory {
    fun createBaselineScorer(): BaselineScorer
    fun createScoreCalculator(): ScoreCalculator
    fun createReportGenerator(): ReportGenerator
    fun createConsoleReporter(): ConsoleReporter
}
