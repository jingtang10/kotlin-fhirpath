package com.google.fhir.fhirpath

import com.google.fhir.model.r4.Date
import com.google.fhir.model.r4.Resource
import com.google.fhir.model.r4.configureR4
import evaluateFhirPath
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.serialization.XML
import java.io.File
import kotlin.test.assertEquals

private const val TEST_RESOURCE_DIR = "third_party/fhirpath-2.0.0/tests/r4"
private const val TEST_INPUT_DIR = "${TEST_RESOURCE_DIR}/input"

private val jsonR4 = Json {
    prettyPrint = true
    configureR4()
}

class FhirPathEngineTest : FunSpec({
    val inputMap: Map<String, Resource> = listJsonFiles(TEST_INPUT_DIR)
        .mapKeys {
            it.key.replace(".json$".toRegex(), ".xml")
        }
        .mapValues {
            jsonR4.decodeFromString(it.value)
        }
    val xmlContent = loadFile("${TEST_RESOURCE_DIR}/tests-fhir-r4-valid.xml")
    val testSuite = XML.decodeFromString<Tests>(xmlContent)

    testSuite.groups.forEach { group ->
        context(group.name) {
            group.tests.forEach { test ->
                test(test.name) {
                    val results =
                        evaluateFhirPath(test.expression.value, inputMap[test.inputfile]!!)
                    com.google.fhir.fhirpath.assertEquals(test.outputs, results)
                }
            }
        }
    }
})

private fun assertEquals(
    outputs: List<Output>,
    collection: Collection<Any>,
) {
    assertEquals(outputs.size, collection.size)
    outputs.zip(collection).forEach {
        com.google.fhir.fhirpath.assertEquals(it.first, it.second)
    }
}

private fun assertEquals(output: Output, any: Any) {
    when (output.type) {
        "date" -> assertEquals(output.value, (any as Date).value.toString())
        else -> throw AssertionError("Unknown output type: ${output.type}")
    }
}

private fun loadFile(
    file: String,
): String {
    return File("${System.getProperty("projectRootDir")}/${file}").readText()
}

private fun listJsonFiles(dir: String): Map<String, String> {
    return File("${System.getProperty("projectRootDir")}/${dir}")
        .listFiles()!!
        .asSequence()
        .filter { it.name.endsWith(".json") }
        .map {
            it.name to it.readText()
        }.toMap()
}