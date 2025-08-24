package com.ivanalvarado.baselinescoresplugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.*
import org.junit.Test

class BaselineScoresPluginTest {

    @Test
    fun `plugin applies successfully`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.ivanalvarado.baseline-scores")

        assertTrue(project.plugins.hasPlugin(BaselineScoresPlugin::class.java))
    }

    @Test
    fun `extension is created`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.ivanalvarado.baseline-scores")

        val extension = project.extensions.findByType(BaselineScoresExtension::class.java)
        assertNotNull(extension)
        assertEquals("baseline-scores.json", extension?.outputFile?.get())
        assertEquals(0.8, extension?.minimumScoreThreshold?.get() ?: 0.0, 0.001)
    }

    @Test
    fun `tasks are registered`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("com.ivanalvarado.baseline-scores")

        assertNotNull(project.tasks.findByName("generateBaselineScores"))
        assertNotNull(project.tasks.findByName("validateBaselineScores"))
    }
}
