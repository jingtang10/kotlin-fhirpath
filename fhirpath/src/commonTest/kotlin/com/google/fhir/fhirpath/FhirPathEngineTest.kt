/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.fhir.fhirpath

import com.google.fhir.model.r4.Code
import com.google.fhir.model.r4.Date
import com.google.fhir.model.r4.Resource
import com.google.fhir.model.r4.configureR4
import evaluateFhirPath
import io.kotest.core.spec.style.FunSpec
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

private const val TEST_RESOURCE_DIR = "third_party/fhirpath-2.0.0/tests/r4"
private const val TEST_INPUT_DIR = "${TEST_RESOURCE_DIR}/input"

private val jsonR4 = Json {
    prettyPrint = true
    configureR4()
}

class FhirPathEngineTest :
    FunSpec({
        val inputMap: Map<String, Resource> =
            listJsonFiles(TEST_INPUT_DIR)
                .mapKeys { it.key.replace(".json$".toRegex(), ".xml") }
                .mapValues { jsonR4.decodeFromString(it.value) }
        val xmlContent = loadFile("${TEST_RESOURCE_DIR}/tests-fhir-r4-valid.xml")
        val testSuite = XML.decodeFromString<Tests>(xmlContent)

        testSuite.groups.forEach { group ->
            context(group.name) {
                group.tests
//                    .filter { it.name == "testSelect1" }
                    .forEach { test ->
                        test(test.name) {
                            if (test.expression.invalid == true) {
                                assertThrows<Exception> {
                                    evaluateFhirPath(
                                        test.expression.value,
                                        inputMap[test.inputfile]!!
                                    )
                                }
                            } else {
                                val results = evaluateFhirPath(
                                    test.expression.value,
                                    inputMap[test.inputfile]!!
                                )
                                com.google.fhir.fhirpath.assertEquals(test.outputs, results)
                            }
                        }
                    }
            }
        }
    })

private fun assertEquals(outputs: List<Output>, collection: Collection<Any>) {
    assertEquals(outputs.size, collection.size)
    outputs.zip(collection).forEach { com.google.fhir.fhirpath.assertEquals(it.first, it.second) }
}

private fun assertEquals(output: Output, any: Any) {
    when (output.type) {
        "date" -> assertEquals(output.value, (any as Date).value.toString())
        "code" -> assertEquals(output.value, (any as Code).value)
        "string" -> assertEquals(output.value, (any as com.google.fhir.model.r4.String).value)
        "boolean" -> assertEquals(output.value, (any as Boolean).toString())
        "integer" -> assertEquals(output.value, (any as Int).toString())
        else -> throw AssertionError("Unknown output type: ${output.type}")
    }
}

expect fun loadFile(file: String): String

expect fun listJsonFiles(dir: String): Map<String, String>
