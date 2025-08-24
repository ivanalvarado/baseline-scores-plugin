package com.ivanalvarado.baselinescoresplugin

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.File

class BaselineFileDetectorTest {

    private lateinit var detector: BaselineFileDetector
    private lateinit var extension: BaselineScoresExtension

    @Before
    fun setup() {
        detector = BaselineFileDetector()
        val project = ProjectBuilder.builder().build()
        extension = project.objects.newInstance(BaselineScoresExtension::class.java)
    }

    @Test
    fun `findAllBaselineFiles returns empty list when no baseline files exist`() {
        val project = ProjectBuilder.builder().build()

        val result = detector.findAllBaselineFiles(project, extension)

        assertTrue("Expected empty list when no baseline files exist", result.isEmpty())
    }

    @Test
    fun `findAllBaselineFiles finds detekt baseline in project root`() {
        val project = ProjectBuilder.builder().build()

        // Create a detekt baseline file
        val detektBaseline = File(project.projectDir, "detekt-baseline.xml")
        detektBaseline.writeText("<baseline></baseline>")

        // Disable lint to test only detekt
        extension.lintEnabled.set(false)

        val result = detector.findAllBaselineFiles(project, extension)

        assertEquals("Expected one baseline file", 1, result.size)
        assertEquals("Expected DETEKT type", BaselineType.DETEKT, result[0].type)
        assertEquals("Expected project name", project.name, result[0].module)
        assertTrue("Expected file to exist", result[0].file.exists())

        // Cleanup
        detektBaseline.delete()
    }

    @Test
    fun `findAllBaselineFiles finds lint baseline in project root`() {
        val project = ProjectBuilder.builder().build()

        // Create a lint baseline file
        val lintBaseline = File(project.projectDir, "lint-baseline.xml")
        lintBaseline.writeText("<baseline></baseline>")

        // Disable detekt to test only lint
        extension.detektEnabled.set(false)

        val result = detector.findAllBaselineFiles(project, extension)

        assertEquals("Expected one baseline file", 1, result.size)
        assertEquals("Expected LINT type", BaselineType.LINT, result[0].type)
        assertEquals("Expected project name", project.name, result[0].module)
        assertTrue("Expected file to exist", result[0].file.exists())

        // Cleanup
        lintBaseline.delete()
    }

    @Test
    fun `findAllBaselineFiles finds both detekt and lint baselines`() {
        val project = ProjectBuilder.builder().build()

        // Create both baseline files
        val detektBaseline = File(project.projectDir, "detekt-baseline.xml")
        detektBaseline.writeText("<baseline></baseline>")

        val lintBaseline = File(project.projectDir, "lint-baseline.xml")
        lintBaseline.writeText("<baseline></baseline>")

        val result = detector.findAllBaselineFiles(project, extension)

        assertEquals("Expected two baseline files", 2, result.size)

        val detektResult = result.find { it.type == BaselineType.DETEKT }
        val lintResult = result.find { it.type == BaselineType.LINT }

        assertNotNull("Expected detekt baseline to be found", detektResult)
        assertNotNull("Expected lint baseline to be found", lintResult)

        // Cleanup
        detektBaseline.delete()
        lintBaseline.delete()
    }
}
