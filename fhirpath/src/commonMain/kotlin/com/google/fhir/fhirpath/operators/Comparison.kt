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

import com.google.fhir.fhirpath.utilities.toSystemType
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.FhirDateTime
import com.google.fhir.model.r4.Quantity
import kotlinx.datetime.LocalTime

internal fun compare(left: Any, right: Any): Int? {
  val leftSystem = left.toSystemType()
  val rightSystem = right.toSystemType()

  return when {
    leftSystem is Number && rightSystem is Number -> {
      leftSystem.toDouble().compareTo(rightSystem.toDouble())
    }
    leftSystem is String && rightSystem is String -> {
      leftSystem.compareTo(rightSystem)
    }
    leftSystem is FhirDateTime && rightSystem is FhirDateTime -> leftSystem.compareTo(rightSystem)
    leftSystem is FhirDate && rightSystem is FhirDate -> leftSystem.compareTo(rightSystem)
    leftSystem is LocalTime && rightSystem is LocalTime -> leftSystem.compareTo(rightSystem)

    // TODO: handle units
    leftSystem is Quantity && rightSystem is Quantity -> {
      if (leftSystem.unit == rightSystem.unit) {
        leftSystem.value!!.value!!.compareTo(rightSystem.value!!.value!!)
      } else {
        null
      }
    }

    else -> null
  }
}

internal fun FhirDate.compareTo(other: FhirDate): Int? {
  return when (this) {
    is FhirDate.Year -> if (other is FhirDate.Year) this.value.compareTo(other.value) else null
    is FhirDate.YearMonth ->
      if (other is FhirDate.YearMonth) this.toString().compareTo(other.toString()) else null
    is FhirDate.Date -> if (other is FhirDate.Date) this.date.compareTo(other.date) else null
  }
}

internal fun FhirDateTime.compareTo(other: FhirDateTime): Int? {
  return when (this) {
    is FhirDateTime.Year ->
      if (other is FhirDateTime.Year) this.value.compareTo(other.value) else null
    is FhirDateTime.YearMonth ->
      if (other is FhirDateTime.YearMonth) this.toString().compareTo(other.toString()) else null
    is FhirDateTime.Date ->
      if (other is FhirDateTime.Date) this.date.compareTo(other.date) else null
    is FhirDateTime.DateTime ->
      if (other is FhirDateTime.DateTime) this.dateTime.compareTo(other.dateTime) else null
  }
}
