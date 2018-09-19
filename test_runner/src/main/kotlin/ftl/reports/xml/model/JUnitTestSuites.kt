package ftl.reports.xml.model

/**
 * iOS only
 * .xctestrun file may contain multiple test bundles (each one is a testsuite) */
data class JUnitTestSuites(
    val testsuite: List<JUnitTestSuite>?
)
