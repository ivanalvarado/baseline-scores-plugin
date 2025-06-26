package com.ivanalvarado.baselinescoresplugin.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class ScoringConfigurationTest {

    @Test
    fun `should return configured points for known issue type`() {
        val rules = mapOf("FunctionNaming" to -10)
        val config = ScoringConfiguration(rules, defaultPoints = -5)

        assertEquals(-10, config.getPointsForIssue("FunctionNaming"))
    }

    @Test
    fun `should return default points for unknown issue type`() {
        val rules = mapOf("FunctionNaming" to -10)
        val config = ScoringConfiguration(rules, defaultPoints = -5)

        assertEquals(-5, config.getPointsForIssue("UnknownIssue"))
    }

    @Test
    fun `should handle empty rules map`() {
        val config = ScoringConfiguration(emptyMap(), defaultPoints = -3)

        assertEquals(-3, config.getPointsForIssue("AnyIssue"))
    }

    @Test
    fun `should override default points with specific rule`() {
        val rules = mapOf("MagicNumber" to -2)
        val config = ScoringConfiguration(rules, defaultPoints = -5)

        assertEquals(-2, config.getPointsForIssue("MagicNumber"))
        assertEquals(-5, config.getPointsForIssue("OtherIssue"))
    }
}
