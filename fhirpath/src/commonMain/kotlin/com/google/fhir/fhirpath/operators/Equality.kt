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

package com.google.fhir.fhirpath.operators

import com.google.fhir.fhirpath.asComparableOperands
import com.google.fhir.fhirpath.toEqualCanonicalized
import com.google.fhir.fhirpath.toEquivalentCanonicalized
import com.google.fhir.fhirpath.types.FhirPathDateTime
import com.google.fhir.fhirpath.types.FhirPathTime
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.Quantity

/** See [specification](https://hl7.org/fhirpath/N1/#equals). */
internal fun equals(left: Any, right: Any): Boolean? {
  val (leftFhirPath, rightFhirPath) = (left to right).asComparableOperands()

  return when {
    leftFhirPath is String && rightFhirPath is String -> {
      leftFhirPath == rightFhirPath
    }
    leftFhirPath is Number && rightFhirPath is Number -> {
      leftFhirPath.toDouble() == rightFhirPath.toDouble()
    }
    leftFhirPath is Boolean && rightFhirPath is Boolean -> {
      leftFhirPath == rightFhirPath
    }
    leftFhirPath is FhirDate && rightFhirPath is FhirDate -> {
      leftFhirPath == rightFhirPath
    }
    leftFhirPath is FhirPathDateTime && rightFhirPath is FhirPathDateTime -> {
      // TODO: Handle equivalent timezones (+00:00 = -00:00 = Z)
      leftFhirPath.compareTo(rightFhirPath)?.let { it == 0 }
    }
    leftFhirPath is FhirPathTime && rightFhirPath is FhirPathTime -> {
      leftFhirPath == rightFhirPath
    }
    leftFhirPath is Quantity && rightFhirPath is Quantity -> {
      with(leftFhirPath.toEqualCanonicalized() to rightFhirPath.toEqualCanonicalized()) {
        if (first.unit?.value!! != second.unit?.value!!) return null
        return first.value == second.value
      }
    }
    else -> {
      leftFhirPath == rightFhirPath
    }
  }
}

/** See [specification](https://hl7.org/fhirpath/N1/#equivalent). */
internal fun equivalent(left: Any, right: Any): Boolean {
  val (leftFhirPath, rightFhirPath) = (left to right).asComparableOperands()

  return when {
    leftFhirPath is String && rightFhirPath is String -> {
      leftFhirPath.normalize() == rightFhirPath.normalize()
    }
    leftFhirPath is Number && rightFhirPath is Number -> {
      leftFhirPath.toDouble() == rightFhirPath.toDouble()
    }
    leftFhirPath is Boolean && rightFhirPath is Boolean -> {
      leftFhirPath == rightFhirPath
    }
    leftFhirPath is FhirDate && rightFhirPath is FhirDate -> {
      leftFhirPath == rightFhirPath
    }
    leftFhirPath is FhirPathDateTime && rightFhirPath is FhirPathDateTime -> {
      // TODO: Handle timezone conversion
      // TODO: Handle equivalent timezones (+00:00 = -00:00 = Z)
      leftFhirPath == rightFhirPath
    }
    leftFhirPath is FhirPathTime && rightFhirPath is FhirPathTime -> {
      leftFhirPath == rightFhirPath
    }
    leftFhirPath is Quantity && rightFhirPath is Quantity -> {
      with(leftFhirPath.toEquivalentCanonicalized() to rightFhirPath.toEquivalentCanonicalized()) {
        return first.value == second.value && first.unit?.value!! == second.unit?.value!!
      }
    }
    // TODO: Handle implicit conversion from Date to DateTime
    // TODO: Handle implicit conversion from Integer and Decimal to Quantity
    else -> {
      leftFhirPath == rightFhirPath
    }
  }
}

/** See [specification](https://hl7.org/fhirpath/N1/#string-equivalence). */
private fun String.normalize() = lowercase().replace(Regex("\\s+"), " ").trim()

