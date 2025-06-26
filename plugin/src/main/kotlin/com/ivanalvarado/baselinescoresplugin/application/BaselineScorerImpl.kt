package com.ivanalvarado.baselinescoresplugin.application

import com.ivanalvarado.baselinescoresplugin.BaselineFileInfo
import com.ivanalvarado.baselinescoresplugin.BaselineType
import com.ivanalvarado.baselinescoresplugin.domain.BaselineParser
import com.ivanalvarado.baselinescoresplugin.domain.BaselineScorer
import com.ivanalvarado.baselinescoresplugin.domain.ScoreCalculator
import com.ivanalvarado.baselinescoresplugin.domain.ScoringConfiguration
import com.ivanalvarado.baselinescoresplugin.domain.ScoringResult
import com.ivanalvarado.baselinescoresplugin.infrastructure.DetektBaselineParser

class BaselineScorerImpl(
    private val scoreCalculator: ScoreCalculator = ScoreCalculatorImpl()
) : BaselineScorer {

    private val parsers = mapOf<BaselineType, BaselineParser>(
        BaselineType.DETEKT to DetektBaselineParser()
        // Future: BaselineType.LINT to LintBaselineParser()
    )

    override fun scoreBaseline(
        baselineFileInfo: BaselineFileInfo,
        configuration: ScoringConfiguration
    ): ScoringResult {
        val parser = parsers[baselineFileInfo.type]
            ?: throw IllegalArgumentException("No parser available for baseline type: ${baselineFileInfo.type}")

        val issueBreakdown = parser.parseIssues(baselineFileInfo)

        return scoreCalculator.calculateScore(
            issueBreakdown = issueBreakdown,
            configuration = configuration,
            module = baselineFileInfo.module,
            type = baselineFileInfo.type
        )
    }
}
