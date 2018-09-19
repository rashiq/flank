package ftl.reports.xml.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

// https://android.googlesource.com/platform/tools/base/+/tools_r22/ddmlib/src/main/java/com/android/ddmlib/testrunner/XmlTestRunListener.java#256
data class JUnitTestCase(
    val name: String,
    val classname: String,
    val time: String,

    // iOS contains multiple failures for a single test.
    // JUnit XML allows arbitrary amounts of failure/error tags
    @JacksonXmlProperty(localName = "failure")
    val failures: List<String>?,

    @JacksonXmlProperty(localName = "error")
    val errors: List<String>?
)
