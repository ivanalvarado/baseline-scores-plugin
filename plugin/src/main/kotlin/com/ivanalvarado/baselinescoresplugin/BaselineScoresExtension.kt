package com.ivanalvarado.baselinescoresplugin

import com.ivanalvarado.baselinescoresplugin.domain.ScoringConfiguration

open class BaselineScoresExtension {
    var outputFile: String = "baseline-scores.json"
    var threshold: Double = 0.8
    var enabled: Boolean = true
    var includePatterns: List<String> = listOf("**/*.kt", "**/*.java")
    var excludePatterns: List<String> = listOf("**/test/**", "**/build/**")

    // Detekt-specific configuration
    var detektEnabled: Boolean = true
    var detektBaselineFileName: String = "detekt-baseline.xml"

    // Android Lint-specific configuration
    var lintEnabled: Boolean = true
    var lintBaselineFileName: String = "lint-baseline.xml"

    // Scoring configuration
    var defaultIssuePoints: Int = -5
    private val scoringRules = mutableMapOf<String, Int>()

    /**
     * Configure scoring for a specific issue type
     */
    fun issueScore(issueType: String, points: Int) {
        scoringRules[issueType] = points
    }

    /**
     * Configure scoring for multiple issue types
     */
    fun issueScores(scores: Map<String, Int>) {
        scoringRules.putAll(scores)
    }

    /**
     * Get the scoring configuration
     */
    fun getScoringConfiguration(): ScoringConfiguration {
        return ScoringConfiguration(
            rules = getDefaultScoringRules() + scoringRules,
            defaultPoints = defaultIssuePoints
        )
    }

    /**
     * https://detekt.dev/docs/rules/comments
     */
    private val commentsRuleSet = mapOf(
        "AbsentOrWrongFileLicense" to -5,
        "CommentOverPrivateFunction" to -20,
        "CommentOverPrivateProperty" to -20,
        "DeprecatedBlockTag" to -5,
        "EndOfSentenceFormat" to -5,
        "KDocReferencesNonPublicProperty" to -5,
        "OutdatedDocumentation" to -10,
        "UndocumentedPublicClass" to -20,
        "UndocumentedPublicFunction" to -20,
        "UndocumentedPublicProperty" to -20,
    )

    /**
     * https://detekt.dev/docs/rules/complexity
     */
    private val complexityRuleSet = mapOf(
        "CognitiveComplexMethod" to -20,
        "ComplexCondition" to -20,
        "ComplexInterface" to -20,
        "CyclomaticComplexMethod" to -20,
        "LabeledExpression" to -20,
        "LargeClass" to -20,
        "LongMethod" to -20,
        "LongParameterList" to -20,
        "MethodOverloading" to -20,
        "NamedArguments" to -5,
        "NestedBlockDepth" to -20,
        "NestedScopeFunctions" to -5,
        "ReplaceSafeCallChainWithRun" to -10,
        "StringLiteralDuplication" to -5,
        "TooManyFunctions" to -20,
    )

    /**
     * https://detekt.dev/docs/rules/coroutines
     */
    private val coroutinesRuleSet = mapOf(
        "GlobalCoroutineUsage" to -10,
        "InjectDispatcher" to -5,
        "RedundantSuspendModifier" to -5,
        "SleepInsteadOfDelay" to -5,
        "SuspendFunSwallowedCancellation" to -10,
        "SuspendFunWithCoroutineScopeReceiver" to -10,
        "SuspendFunWithFlowReturnType" to -10,
    )

    /**
     * https://detekt.dev/docs/rules/empty-blocks
     */
    private val emptyBlocksRuleSet = mapOf(
        "EmptyCatchBlock" to -5,
        "EmptyClassBlock" to -5,
        "EmptyDefaultConstructor" to -5,
        "EmptyDoWhileBlock" to -5,
        "EmptyElseBlock" to -5,
        "EmptyFinallyBlock" to -5,
        "EmptyForBlock" to -5,
        "EmptyFunctionBlock" to -5,
        "EmptyIfBlock" to -5,
        "EmptyInitBlock" to -5,
        "EmptyKtFile" to -5,
        "EmptySecondaryConstructor" to -5,
        "EmptyTryBlock" to -5,
        "EmptyWhenBlock" to -5,
        "EmptyWhileBlock" to -5,
    )

    /**
     * Default scoring rules for common detekt issues
     */
    private fun getDefaultScoringRules(): Map<String, Int> {
        return mapOf(
            "FunctionNaming" to -5,
            "LongParameterList" to -10,
            "MagicNumber" to -3,
            "MatchingDeclarationName" to -5,
            "UnusedPrivateMember" to -7,
            "ComplexMethod" to -15,
            "LongMethod" to -10,
            "TooManyFunctions" to -12,
            "LargeClass" to -15,
            "EmptyFunctionBlock" to -5,
            "UnnecessaryApply" to -3,
            "UnsafeCallOnNullableType" to -20,
            "LateinitUsage" to -8,
            "ForEachOnRange" to -5,
            "SpreadOperator" to -5,
            "UnnecessaryLet" to -3,
            "DataClassContainsFunctions" to -8,
            "UseDataClass" to -5,
            "ExceptionRaisedInUnexpectedLocation" to -25,
            "TooGenericExceptionCaught" to -10
        ) + commentsRuleSet + complexityRuleSet + coroutinesRuleSet + emptyBlocksRuleSet
    }
}
