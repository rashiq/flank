package ftl.reports.xml.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

data class JUnitTestSuite(
    val name: String,
    val tests: String,
    val failures: String,
    val errors: String,
    val skipped: String?, // Android only
    val time: String,
    val timestamp: String?, // Android only
    val hostname: String,
    val testcase: List<JUnitTestCase>?,

    // not used
    val properties: Any?, // <properties />

    @JacksonXmlProperty(localName = "system-out")
    val systemOut: Any?, // <system-out />

    @JacksonXmlProperty(localName = "system-err")
    val systemErr: Any? // <system-err />
)
