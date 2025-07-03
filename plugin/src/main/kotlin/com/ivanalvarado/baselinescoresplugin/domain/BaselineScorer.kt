package com.ivanalvarado.baselinescoresplugin.domain

import com.ivanalvarado.baselinescoresplugin.BaselineFileInfo

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
