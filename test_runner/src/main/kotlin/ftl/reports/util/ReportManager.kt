package ftl.reports.util

import com.sun.org.apache.xerces.internal.dom.DeferredElementImpl
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.json.MatrixMap
import ftl.reports.CostReport
import ftl.reports.HtmlErrorReport
import ftl.reports.MatrixErrorReport
import ftl.reports.MatrixResultsReport
import ftl.reports.TestErrorCountReport
import ftl.util.ArtifactRegex
import ftl.util.resolveLocalRunPath
import org.w3c.dom.NodeList
import java.io.File
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory

object ReportManager {

    private fun findXmlFiles(matrices: MatrixMap): List<File> {
        val xmlFiles = mutableListOf<File>()
        val rootFolder = File(resolveLocalRunPath(matrices))

        rootFolder.walk().forEach {
            if (it.name.matches(ArtifactRegex.testResultRgx)) {
                xmlFiles.add(it)
            }
        }

        return xmlFiles
    }

    private fun readAndroidXml(matrices: MatrixMap): TestSuite {
        return readIosXml(matrices)
    }

    private fun readIosXml(matrices: MatrixMap): TestSuite {
        val testCases = mutableMapOf<String, TestResults>()
        val matrixMapValues = matrices.map.values

        val xmlFiles = findXmlFiles(matrices)
        var failureCount = 0
        var successCount = 0

        // TODO: parse files into JUnitTestSuites
        // TODO: update reports to use JUnitTestSuites - replace TestData
        // TODO: attach webLink to JUnitTestCase for reporting?

        xmlFiles.forEach { file ->
            val xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
            xml.normalizeDocument()
            xml.documentElement.normalize()

            val nodes: NodeList = xml.getElementsByTagName("testcase")

            val matrixFolder = file.parentFile.parentFile.name
            val matrixPath = Paths.get(matrices.runPath).fileName.resolve(matrixFolder).toString()

            repeat(nodes.length) { index ->
                val node: DeferredElementImpl = nodes.item(index) as DeferredElementImpl

                val attr = node.attributes
                val className = attr.getNamedItem("classname").nodeValue
                val testName = attr.getNamedItem("name").nodeValue
                val key = "$className#$testName"

                if (testCases[key] == null) {
                    testCases[key] = TestResults()
                }
                val testResults = testCases[key]!!

                var webLink = ""
                val savedMatrix = matrixMapValues.firstOrNull { it.gcsPath.endsWith(matrixPath) }
                if (savedMatrix != null) {
                    webLink = savedMatrix.webLink
                } else {
                    println("WARNING: Matrix path not found in JSON. $matrixPath")
                }

                val failureNode = node.getElementsByTagName("failure").item(0)

                if (failureNode == null) {
                    successCount += 1
                    testResults.successes.add(TestSuccess(webLink = webLink))
                } else {
                    failureCount += 1
                    val stackTrace = failureNode.firstChild.nodeValue
                    testResults.failures.add(
                        TestFailure(
                            stackTrace = stackTrace,
                            webLink = webLink
                        )
                    )
                }
            }
        }

        val sortedTestCases = testCases.toList()
            .sortedByDescending { (_, value) -> value.failures.size }.toMap()

        return TestSuite(
            totalTests = failureCount + successCount,
            failures = failureCount,
            successes = successCount,
            testCases = sortedTestCases
        )
    }

    /** Returns true if there were no test failures */
    fun generate(matrices: MatrixMap, config: IArgs): Boolean {
        val iosXml = config is IosArgs
        val testSuite = if (iosXml) {
            readIosXml(matrices)
        } else {
            readAndroidXml(matrices)
        }
        val testSuccessful = matrices.allSuccessful()

        listOf(
            CostReport,
            MatrixResultsReport
        ).map {
            it.run(matrices, testSuite, printToStdout = true)
        }

        if (!testSuccessful) {
            listOf(
                HtmlErrorReport,
                MatrixErrorReport,
                TestErrorCountReport
            ).map { it.run(matrices, testSuite) }
        }

        return testSuccessful
    }
}
