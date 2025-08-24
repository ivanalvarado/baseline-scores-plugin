/*
 * Copyright 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivanalvarado.baselinescoresplugin.config

object DetektDefaultScores {
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

    /**
     * https://detekt.dev/docs/rules/naming
     */
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
     * https://detekt.dev/docs/rules/performance
     */
    private val performanceRuleSet = mapOf(
        "ArrayPrimitive" to -5,
        "CouldBeSequence" to -5,
        "ForEachOnRange" to -5,
        "SpreadOperator" to -20,
        "UnnecessaryPartOfBinaryExpression" to -5,
        "UnnecessaryTemporaryInstantiation" to -5,
    )

    /**
     * https://detekt.dev/docs/rules/potential-bugs
     */
    private val potentialBugsRuleSet = mapOf(
        "AvoidReferentialEquality" to -5,
        "CastNullableToNonNullableType" to -5,
        "CastToNullableType" to -5,
        "Deprecation" to -20,
        "DontDowncastCollectionTypes" to -10,
        "DoubleMutabilityForCollection" to -5,
        "DuplicateCaseInWhenExpression" to -10,
        "ElseCaseInsteadOfExhaustiveWhen" to -5,
        "EqualsAlwaysReturnsTrueOrFalse" to -20,
        "EqualsWithHashCodeExist" to -5,
        "ExitOutsideMain" to -10,
        "ExplicitGarbageCollectionCall" to -20,
        "HasPlatformType" to -5,
        "IgnoredReturnValue" to -20,
        "ImplicitDefaultLocale" to -5,
        "ImplicitUnitReturnType" to -5,
        "InvalidRange" to -10,
        "IteratorHasNextCallsNextMethod" to -10,
        "IteratorNotThrowingNoSuchElementException" to -10,
        "LateinitUsage" to -20,
        "MapGetWithNotNullAssertionOperator" to -5,
        "MissingPackageDeclaration" to -5,
        "MissingWhenCase" to -20,
        "NullCheckOnMutableProperty" to -10,
        "NullableToStringCall" to -5,
        "PropertyUsedBeforeDeclaration" to -5,
        "RedundantElseInWhen" to -5,
        "UnconditionalJumpStatementInLoop" to -10,
        "UnnecessaryNotNullCheck" to -5,
        "UnnecessaryNotNullOperator" to -5,
        "UnnecessarySafeCall" to -5,
        "UnreachableCatchBlock" to -5,
        "UnreachableCode" to -10,
        "UnsafeCallOnNullableType" to -20,
        "UnsafeCast" to -20,
        "UnusedUnaryOperator" to -5,
        "UselessPostfixExpression" to -20,
        "WrongEqualsTypeParameter" to -10,
    )

    /**
     * https://detekt.dev/docs/rules/ruleauthors
     */
    private val ruleauthorsRuleSet = mapOf(
        "UseEntityAtName" to -5,
        "ViolatesTypeResolutionRequirements" to -5,
    )

    /**
     * https://detekt.dev/docs/rules/style
     */
    private val styleRuleSet = mapOf(
        "AlsoCouldBeApply" to -5,
        "BracesOnIfStatements" to -5,
        "BracesOnWhenStatements" to -5,
        "CanBeNonNullable" to -10,
        "CascadingCallWrapping" to -5,
        "ClassOrdering" to -5,
        "CollapsibleIfStatements" to -5,
        "DataClassContainsFunctions" to -20,
        "DataClassShouldBeImmutable" to -20,
        "DestructuringDeclarationWithTooManyEntries" to -10,
        "DoubleNegativeLambda" to -5,
        "EqualsNullCall" to -5,
        "EqualsOnSignatureLine" to -5,
        "ExplicitCollectionElementAccessMethod" to -5,
        "ExplicitItLambdaParameter" to -5,
        "ExpressionBodySyntax" to -5,
        "ForbiddenAnnotation" to -5,
        "ForbiddenComment" to -10,
        "ForbiddenImport" to -10,
        "ForbiddenMethodCall" to -10,
        "ForbiddenSuppress" to -10,
        "ForbiddenVoid" to -5,
        "FunctionOnlyReturningConstant" to -10,
        "LoopWithTooManyJumpStatements" to -10,
        "MagicNumber" to -10,
        "MandatoryBracesLoops" to -5,
        "MaxChainedCallsOnSameLine" to -5,
        "MaxLineLength" to -5,
        "MayBeConst" to -5,
        "ModifierOrder" to -5,
        "MultilineLambdaItParameter" to -5,
        "MultilineRawStringIndentation" to -5,
        "NestedClassesVisibility" to -5,
        "NewLineAtEndOfFile" to -5,
        "NoTabs" to -5,
        "NullableBooleanCheck" to -5,
        "ObjectLiteralToLambda" to -5,
        "OptionalAbstractKeyword" to -5,
        "OptionalUnit" to -5,
        "OptionalWhenBraces" to -5,
        "PreferToOverPairSyntax" to -5,
        "ProtectedMemberInFinalClass" to -5,
        "RedundantExplicitType" to -5,
        "RedundantHigherOrderMapUsage" to -5,
        "RedundantVisibilityModifierRule" to -5,
        "ReturnCount" to -10,
        "SafeCast" to -5,
        "SerialVersionUIDInSerializableClass" to -5,
        "SpacingBetweenPackageAndImports" to -5,
        "StringShouldBeRawString" to -5,
        "ThrowsCount" to -10,
        "TrailingWhitespace" to -5,
        "TrimMultilineRawString" to -5,
        "UnderscoresInNumericLiterals" to -5,
        "UnnecessaryAbstractClass" to -5,
        "UnnecessaryAnnotationUseSiteTarget" to -5,
        "UnnecessaryApply" to -5,
        "UnnecessaryBackticks" to -5,
        "UnnecessaryBracesAroundTrailingLambda" to -5,
        "UnnecessaryFilter" to -5,
        "UnnecessaryInheritance" to -5,
        "UnnecessaryInnerClass" to -5,
        "UnnecessaryLet" to -5,
        "UnnecessaryParentheses" to -5,
        "UntilInsteadOfRangeTo" to -5,
        "UnusedImports" to -5,
        "UnusedParameter" to -5,
        "UnusedPrivateClass" to -5,
        "UnusedPrivateMember" to -5,
        "UnusedPrivateProperty" to -5,
        "UseAnyOrNoneInsteadOfFind" to -5,
        "UseArrayLiteralsInAnnotations" to -5,
        "UseCheckNotNull" to -5,
        "UseCheckOrError" to -5,
        "UseDataClass" to -5,
        "UseEmptyCounterpart" to -5,
        "UseIfEmptyOrIfBlank" to -5,
        "UseIfInsteadOfWhen" to -5,
        "UseIsNullOrEmpty" to -5,
        "UseLet" to -5,
        "UseOrEmpty" to -5,
        "UseRequire" to -5,
        "UseRequireNotNull" to -5,
        "UseSumOfInsteadOfFlatMapSize" to -5,
        "UselessCallOnNotNull" to -5,
        "UtilityClassWithPublicConstructor" to -5,
        "VarCouldBeVal" to -5,
        "WildcardImport" to -5,
    )

    /**
     * Default scoring rules for common detekt issues
     */
    val rules: Map<String, Int> = commentsRuleSet +
            complexityRuleSet +
            coroutinesRuleSet +
            emptyBlocksRuleSet +
            exceptionsRuleSet +
            formattingRuleSet +
            librariesRuleSet +
            namingRuleSet +
            performanceRuleSet +
            potentialBugsRuleSet +
            ruleauthorsRuleSet +
            styleRuleSet
}
