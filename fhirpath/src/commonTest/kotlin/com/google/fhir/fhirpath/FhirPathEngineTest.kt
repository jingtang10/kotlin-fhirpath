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

import com.google.fhir.model.r4.Date
import com.google.fhir.model.r4.Enumeration
import com.google.fhir.model.r4.Resource
import com.google.fhir.model.r4.configureR4
import evaluateFhirPath
import io.kotest.core.spec.style.FunSpec
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.serialization.XML

private const val TEST_RESOURCE_DIR = "third_party/fhir-test-cases/r4"
private const val TEST_INPUT_DIR = "${TEST_RESOURCE_DIR}/resources"

private val jsonR4 = Json {
  prettyPrint = true
  configureR4()
}

val testCasesToSkip =
  listOf(
    // It is not clear the behaviour of `as()` function should differ from the `as` operator
    // according to the specification https://hl7.org/fhirpath/#as-type-specifier. Our
    // implementation throws an error here since Quantity cannot be converted to Period.
    "testPolymorphismAsBFunction",
    // The comparison result should be an empty set since the two values have different precisions.
    "testDateTimeGreaterThanDate1",
    // No conversion between calendar durations and definite durations.
    "testStringQuantityDayLiteralToQuantity",
    "testStringQuantityWeekLiteralToQuantity",
  )

class FhirPathEngineTest :
  FunSpec({
    val inputMap: Map<String, Resource> =
      listJsonFiles(TEST_INPUT_DIR)
        .mapKeys { it.key.replace(".json$".toRegex(), ".xml") }
        .mapValues { jsonR4.decodeFromString(it.value) }
    val xmlContent = loadFile("${TEST_RESOURCE_DIR}/tests-fhir-r4.xml")
    val testSuite = XML.decodeFromString<Tests>(xmlContent)

    testSuite.groups.forEach { group ->
      context(group.name) {
        group.tests
          .filterNot { testCasesToSkip.contains(it.name) }
          //          .filter { it.name == "testStringQuantityMonthLiteralToQuantity" }
          .forEach { testCase ->
            test(testCase.name) {
              if (testCase.expression.invalid != null) {
                assertFailsWith<Exception> {
                  evaluateFhirPath(
                    testCase.expression.value,
                    testCase.inputfile?.let { inputMap[it] },
                  )
                }
              } else {
                val results =
                  evaluateFhirPath(
                    testCase.expression.value,
                    testCase.inputfile?.let { inputMap[it] },
                  )
                com.google.fhir.fhirpath.assertEquals(testCase.outputs, results)
              }
            }
          }
      }
    }
  })

private fun assertEquals(expected: List<Output>, actual: Collection<Any>) {
  assertEquals(expected.size, actual.size)
  expected.zip(actual).forEach { com.google.fhir.fhirpath.assertEquals(it.first, it.second) }
}

private fun assertEquals(expected: Output, actual: Any) {
  when (expected.type) {
    "date" -> assertEquals(expected.value, "@${(actual as Date).value.toString()}")
    "code" -> assertEquals(expected.value, (actual as Enumeration<*>).value.toString())
    "string" -> {
      when (actual) {
        is String -> {
          assertEquals(expected.value, actual)
        }
        is com.google.fhir.model.r4.String -> {
          assertEquals(expected.value, actual.value)
        }
      }
    }
    "boolean" -> {
      when (actual) {
        is Boolean -> {
          assertEquals(expected.value, actual.toString())
        }
        else -> {
          assertEquals(expected.value, "true") // Single items are considered true
        }
      }
    }
    "integer" -> assertEquals(expected.value, (actual as Int).toString())
    "decimal" -> assertEquals(expected.value, (actual as Double).toString())
    "Quantity" ->
      assertEquals(
        expected.value,
        (actual as com.google.fhir.model.r4.Quantity).let {
          "${it.value!!.value} ${it.unit!!.value}"
        },
      )
    else -> throw AssertionError("Unknown output type: ${expected.type}")
  }
}

expect fun loadFile(file: String): String

expect fun listJsonFiles(dir: String): Map<String, String>
