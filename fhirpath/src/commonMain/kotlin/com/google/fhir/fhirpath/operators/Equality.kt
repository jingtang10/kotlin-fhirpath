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
import kotlinx.datetime.LocalTime

/** A simplified comparison function. A real implementation needs to be more robust. */
internal fun equality(left: Any, right: Any): Boolean {
  val leftSystem = left.toSystemType()
  val rightSystem = right.toSystemType()

  return when {
    leftSystem is String && rightSystem is String -> {
      leftSystem == rightSystem
    }
    leftSystem is Number && rightSystem is Number -> {
      // Promote both to Double for a consistent comparison
      leftSystem.toDouble() == rightSystem.toDouble()
    }
    leftSystem is Boolean && rightSystem is Boolean -> {
      leftSystem == rightSystem
    }
    leftSystem is FhirDate && rightSystem is FhirDate -> {
      // Assumes FhirDate has a comparable representation
      leftSystem == rightSystem
    }
    leftSystem is FhirDateTime && rightSystem is FhirDateTime -> {
      // TODO: Handle cases where the two date time values are not the same but equivalent (e.g.
      // same instant in different timezones) and equavilant timezones (e.g. +00:00, -00:00 and Z)
      leftSystem == rightSystem
    }
    leftSystem is LocalTime && rightSystem is LocalTime -> {
      leftSystem == rightSystem
    }
    else -> {
      leftSystem == rightSystem
    }
  }
}
