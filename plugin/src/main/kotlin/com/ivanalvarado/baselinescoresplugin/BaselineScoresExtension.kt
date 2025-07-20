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
     * https://detekt.dev/docs/rules/exceptions
     */
    private val exceptionsRuleSet = mapOf(
        "ExceptionRaisedInUnexpectedLocation" to -20,
        "InstanceOfCheckForException" to -20,
        "NotImplementedDeclaration" to -20,
        "ObjectExtendsThrowable" to -10,
        "PrintStackTrace" to -20,
        "RethrowCaughtException" to -5,
        "ReturnFromFinally" to -20,
        "SwallowedException" to -20,
        "ThrowingExceptionFromFinally" to -20,
        "ThrowingExceptionInMain" to -20,
        "ThrowingExceptionsWithoutMessageOrCause" to -5,
        "ThrowingNewInstanceOfSameException" to -5,
        "TooGenericExceptionCaught" to -20,
        "TooGenericExceptionThrown" to -20,
    )

    /**
     * https://detekt.dev/docs/rules/formatting
     */
    private val formattingRuleSet = mapOf(
        "AnnotationOnSeparateLine" to -5,
        "AnnotationSpacing" to -5,
        "ArgumentListWrapping" to -5,
        "BlockCommentInitialStarAlignment" to -5,
        "ChainWrapping" to -5,
        "ClassName" to -5,
        "CommentSpacing" to -5,
        "CommentWrapping" to -5,
        "ContextReceiverMapping" to -5,
        "DiscouragedCommentLocation" to -5,
        "EnumEntryNameCase" to -5,
        "EnumWrapping" to -5,
        "Filename" to -5,
        "FinalNewline" to -5,
        "FunKeywordSpacing" to -5,
        "FunctionName" to -5,
        "FunctionReturnTypeSpacing" to -5,
        "FunctionSignature" to -5,
        "FunctionStartOfBodySpacing" to -5,
        "FunctionTypeReferenceSpacing" to -5,
        "IfElseBracing" to -5,
        "IfElseWrapping" to -5,
        "ImportOrdering" to -5,
        "Indentation" to -5,
        "KdocWrapping" to -5,
        "MaximumLineLength" to -5,
        "ModifierListSpacing" to -5,
        "ModifierOrdering" to -5,
        "MultiLineIfElse" to -5,
        "MultilineExpressionWrapping" to -5,
        "NoBlankLineBeforeRbrace" to -5,
        "NoBlankLineInList" to -5,
        "NoBlankLinesInChainedMethodCalls" to -5,
        "NoConsecutiveBlankLines" to -5,
        "NoConsecutiveComments" to -5,
        "NoEmptyClassBody" to -5,
        "NoEmptyFirstLineInClassBody" to -5,
        "NoEmptyFirstLineInMethodBlock" to -5,
        "NoLineBreakAfterElse" to -5,
        "NoLineBreakBeforeAssignment" to -5,
        "NoMultipleSpaces" to -5,
        "NoSemicolons" to -5,
        "NoSingleLineBlockComment" to -5,
        "NoTrailingSpaces" to -5,
        "NoUnitReturn" to -5,
        "NoUnusedImports" to -5,
        "NoWildcardImports" to -5,
        "NullableTypeSpacing" to -5,
        "PackageName" to -5,
        "ParameterListSpacing" to -5,
        "ParameterListWrapping" to -5,
        "ParameterWrapping" to -5,
        "PropertyName" to -5,
        "PropertyWrapping" to -5,
        "SpacingAroundAngleBrackets" to -5,
        "SpacingAroundColon" to -5,
        "SpacingAroundComma" to -5,
        "SpacingAroundCurly" to -5,
        "SpacingAroundDot" to -5,
        "SpacingAroundDoubleColon" to -5,
        "SpacingAroundKeyword" to -5,
        "SpacingAroundOperators" to -5,
        "SpacingAroundParens" to -5,
        "SpacingAroundRangeOperator" to -5,
        "SpacingAroundUnaryOperator" to -5,
        "SpacingBetweenDeclarationsWithAnnotations" to -5,
        "SpacingBetweenDeclarationsWithComments" to -5,
        "SpacingBetweenFunctionNameAndOpeningParenthesis" to -5,
        "StringTemplate" to -5,
        "StringTemplateIndent" to -5,
        "TrailingCommaOnCallSite" to -5,
        "TrailingCommaOnDeclarationSite" to -5,
        "TryCatchFinallySpacing" to -5,
        "TypeArgumentListSpacing" to -5,
        "TypeParameterListSpacing" to -5,
        "UnnecessaryParenthesesBeforeTrailingLambda" to -5,
        "Wrapping" to -5,
    )

    /**
     * https://detekt.dev/docs/rules/libraries
     */
    private val librariesRuleSet = mapOf(
        "ForbiddenPublicDataClass" to -20,
        "LibraryCodeMustSpecifyReturnType" to -5,
        "LibraryEntitiesShouldNotBePublic" to -5,
    )

    private val namingRuleSet = mapOf(
        "BooleanPropertyNaming" to -5,
        "ClassNaming" to -5,
        "ConstructorParameterNaming" to -5,
        "EnumNaming" to -5,
        "ForbiddenClassName" to -5,
        "FunctionMaxLength" to -5,
        "FunctionMinLength" to -5,
        "FunctionNaming" to -5,
        "FunctionParameterNaming" to -5,
        "InvalidPackageDeclaration" to -5,
        "LambdaParameterNaming" to -5,
        "MatchingDeclarationName" to -5,
        "MemberNameEqualsClassName" to -5,
        "NoNameShadowing" to -5,
        "NonBooleanPropertyPrefixedWithIs" to -5,
        "ObjectPropertyNaming" to -5,
        "PackageNaming" to -5,
        "TopLevelPropertyNaming" to -5,
        "VariableMaxLength" to -5,
        "VariableMinLength" to -5,
        "VariableNaming" to -5,
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
        ) +
                commentsRuleSet +
                complexityRuleSet +
                coroutinesRuleSet +
                emptyBlocksRuleSet +
                exceptionsRuleSet +
                formattingRuleSet +
                librariesRuleSet +
                namingRuleSet
    }
}
