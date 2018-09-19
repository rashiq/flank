package ftl.reports.xml

import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import ftl.reports.xml.model.JUnitTestSuite
import ftl.reports.xml.model.JUnitTestSuites
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

private val xmlModule = JacksonXmlModule().apply { setDefaultUseWrapper(false) }
private val xmlMapper = XmlMapper(xmlModule).registerModules(KotlinModule())

private fun xmlBytes(path: String): ByteArray {
    if (!File(path).exists()) RuntimeException("$path doesn't exist!")
    return Files.readAllBytes(Paths.get(path))
}

fun parseAndroidXml(path: String) : JUnitTestSuite {
    return xmlMapper.readValue(xmlBytes(path), JUnitTestSuite::class.java)
}

fun parseIosXml(path: String) : JUnitTestSuites {
    return xmlMapper.readValue(xmlBytes(path), JUnitTestSuites::class.java)
}
