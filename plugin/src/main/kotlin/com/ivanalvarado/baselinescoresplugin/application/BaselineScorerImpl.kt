package com.ivanalvarado.baselinescoresplugin.application

import com.ivanalvarado.baselinescoresplugin.BaselineFileInfo
import com.ivanalvarado.baselinescoresplugin.BaselineType
import com.ivanalvarado.baselinescoresplugin.domain.BaselineParser
import com.ivanalvarado.baselinescoresplugin.domain.BaselineProcessingException
import com.ivanalvarado.baselinescoresplugin.domain.BaselineScorer
import com.ivanalvarado.baselinescoresplugin.domain.ConsoleReporter
import com.ivanalvarado.baselinescoresplugin.domain.DomainServiceFactory
import com.ivanalvarado.baselinescoresplugin.domain.FileScoringResult
import com.ivanalvarado.baselinescoresplugin.domain.ReportGenerator
import com.ivanalvarado.baselinescoresplugin.domain.ScoreCalculator
import com.ivanalvarado.baselinescoresplugin.domain.ScoringConfiguration
import com.ivanalvarado.baselinescoresplugin.domain.ScoringResult
import com.ivanalvarado.baselinescoresplugin.infrastructure.DetektBaselineParser

class BaselineScorerImpl(
    private val scoreCalculator: ScoreCalculator,
    private val parsers: Map<BaselineType, BaselineParser>
) : BaselineScorer {

    override fun scoreBaseline(
        baselineFileInfo: BaselineFileInfo,
        configuration: ScoringConfiguration
    ): ScoringResult {
        val parser = parsers[baselineFileInfo.type]
            ?: throw BaselineProcessingException.ParserNotFound(baselineFileInfo.type.name)

        val issueBreakdown = parser.parseIssues(baselineFileInfo)

        return scoreCalculator.calculateScore(
            issueBreakdown = issueBreakdown,
            configuration = configuration,
            module = baselineFileInfo.module,
            type = baselineFileInfo.type
        )
    }

    override fun scoreBaselineWithFiles(
        baselineFileInfo: BaselineFileInfo,
        configuration: ScoringConfiguration
    ): FileScoringResult {
        val parser = parsers[baselineFileInfo.type]
            ?: throw BaselineProcessingException.ParserNotFound(baselineFileInfo.type.name)

        if (!parser.supportsFileBasedParsing()) {
            throw BaselineProcessingException.FileBasedParsingNotSupported(baselineFileInfo.type.name)
        }

        val fileIssueBreakdown = parser.parseIssuesWithFileNames(baselineFileInfo)

        return scoreCalculator.calculateFileBasedScore(
            fileIssueBreakdown = fileIssueBreakdown,
            configuration = configuration,
            module = baselineFileInfo.module,
            type = baselineFileInfo.type
        )
    }
}

/**
 * Default implementation of the domain service factory
 */
class DefaultDomainServiceFactory : DomainServiceFactory {
    override fun createBaselineScorer(): BaselineScorer {
        val scoreCalculator = createScoreCalculator()
        val parsers = mapOf<BaselineType, BaselineParser>(
            BaselineType.DETEKT to DetektBaselineParser()
            // Future: BaselineType.LINT to LintBaselineParser()
        )

        return BaselineScorerImpl(scoreCalculator, parsers)
    }

    override fun createScoreCalculator(): ScoreCalculator {
        return ScoreCalculatorImpl()
    }

    override fun createReportGenerator(): ReportGenerator {
        return JsonReportGenerator()
    }

    override fun createConsoleReporter(): ConsoleReporter {
        return ConsoleReporterImpl()
    }
}
