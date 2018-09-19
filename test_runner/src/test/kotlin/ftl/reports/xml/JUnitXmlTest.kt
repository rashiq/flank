package ftl.reports.xml

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class JUnitXmlTest {
    private val xmlRoot = "./src/test/kotlin/ftl/fixtures/ftl_junit_xml"
    private val androidPassXml = "$xmlRoot/android_pass.xml"
    private val androidFailXml = "$xmlRoot/android_fail.xml"
    private val iosPassXml = "$xmlRoot/ios_pass.xml"
    private val iosFailXml = "$xmlRoot/ios_fail.xml"

    @Test
    fun parse_androidPassXml() {
        val testSuite = parseAndroidXml(androidPassXml)

        with(testSuite) {
            assertThat(name).isEqualTo("")
            assertThat(tests).isEqualTo("1")
            assertThat(failures).isEqualTo("0")
            assertThat(errors).isEqualTo("0")
            assertThat(skipped).isEqualTo("0")
            assertThat(time).isEqualTo("2.278")
            assertThat(timestamp).isEqualTo("2018-09-14T20:45:55")
            assertThat(hostname).isEqualTo("localhost")
        }

        val test = testSuite.testcase?.first()
        assertThat(test).isNotNull()
        if (test != null) {
            with(test) {
                assertThat(name).isEqualTo("testPasses")
                assertThat(classname).isEqualTo("com.example.app.ExampleUiTest")
                assertThat(time).isEqualTo("0.328")
                assertThat(failures).isNull()
                assertThat(errors).isNull()
            }
        }
    }

    @Test
    fun parse_androidFailXml() {
        val testSuite = parseAndroidXml(androidFailXml)

        with(testSuite) {
            assertThat(name).isEqualTo("")
            assertThat(tests).isEqualTo("2")
            assertThat(failures).isEqualTo("1")
            assertThat(errors).isEqualTo("0")
            assertThat(skipped).isEqualTo("0")
            assertThat(time).isEqualTo("3.87")
            assertThat(timestamp).isEqualTo("2018-09-09T00:16:36")
            assertThat(hostname).isEqualTo("localhost")
        }

        val test = testSuite.testcase?.first()
        assertThat(test).isNotNull()
        if (test != null) {
            with(test) {
                assertThat(name).isEqualTo("testFails")
                assertThat(classname).isEqualTo("com.example.app.ExampleUiTest")
                assertThat(time).isEqualTo("0.857")

                assertThat(failures).isNotNull()

                val firstFailure = failures?.first() ?: "null"
                assertThat(failures?.size).isEqualTo(1)
                assertThat(firstFailure).contains("junit.framework.Assert.fail(Assert.java:50)")

                assertThat(errors).isNull()
            }
        }
    }

    @Test
    fun parse_iosPassXml() {
        val testSuites = parseIosXml(iosPassXml)
        val testSuite = testSuites.testsuite?.first()
        assertThat(testSuite).isNotNull()

        if (testSuite != null) {
            with(testSuite) {
                assertThat(name).isEqualTo("EarlGreyExampleSwiftTests")
                assertThat(tests).isEqualTo("16")
                assertThat(failures).isEqualTo("0")
                assertThat(errors).isEqualTo("0")
                assertThat(skipped).isNull()
                assertThat(time).isEqualTo("25.892")
                assertThat(timestamp).isNull()
                assertThat(hostname).isEqualTo("localhost")
            }
        }

        val test = testSuite?.testcase?.first()
        assertThat(test).isNotNull()
        if (test != null) {
            with(test) {
                assertThat(name).isEqualTo("testBasicSelection()")
                assertThat(classname).isEqualTo("EarlGreyExampleSwiftTests")
                assertThat(time).isEqualTo("2.0")
                assertThat(failures).isNull()
                assertThat(errors).isNull()
            }
        }
    }

    @Test
    fun parse_iosFailXml() {
        val testSuites = parseIosXml(iosFailXml)
        val testSuite = testSuites.testsuite?.first()
        assertThat(testSuite).isNotNull()

        if (testSuite != null) {
            with(testSuite) {
                assertThat(name).isEqualTo("EarlGreyExampleSwiftTests")
                assertThat(tests).isEqualTo("17")
                assertThat(failures).isEqualTo("1")
                assertThat(errors).isEqualTo("0")
                assertThat(skipped).isNull()
                assertThat(time).isEqualTo("25.881")
                assertThat(timestamp).isNull()
                assertThat(hostname).isEqualTo("localhost")
            }
        }

        val test = testSuite?.testcase?.get(2)
        assertThat(test).isNotNull()
        if (test != null) {
            with(test) {
                assertThat(name).isEqualTo("testBasicSelectionAndAction()")
                assertThat(classname).isEqualTo("EarlGreyExampleSwiftTests")
                assertThat(time).isEqualTo("0.584")

                assertThat(failures).isNotNull()

                val firstFailure = failures?.first() ?: "null"
                assertThat(failures?.size).isEqualTo(2)
                assertThat(firstFailure).contains("Exception: NoMatchingElementException")

                assertThat(errors).isNull()
            }
        }
    }
}
