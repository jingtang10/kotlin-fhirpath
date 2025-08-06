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

package com.google.fhir.fhirpath.functions

import com.google.fhir.fhirpath.types.FhirPathDateTime
import com.google.fhir.fhirpath.types.FhirPathTime
import com.google.fhir.model.r4.Date
import com.google.fhir.model.r4.Decimal
import com.google.fhir.model.r4.FhirDate
import com.google.fhir.model.r4.FhirDateTime
import com.google.fhir.model.r4.Quantity
import kotlinx.datetime.LocalTime

val DEFAULT_UNIT = com.google.fhir.model.r4.String(value = "1")

/** See [specification](https://hl7.org/fhirpath/N1/#toboolean-boolean). */
internal fun Collection<Any>.toBoolean(): Collection<Boolean> {
  check(size <= 1) { "toBoolean() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return when (val value = single()) {
    is Boolean -> listOf(value)
    is Int ->
      when (value) {
        1 -> listOf(true)
        0 -> listOf(false)
        else -> emptyList()
      }
    is Double ->
      when (value) {
        1.0 -> listOf(true)
        0.0 -> listOf(false)
        else -> emptyList()
      }
    is String ->
      when (value.lowercase()) {
        "true",
        "t",
        "yes",
        "y",
        "1",
        "1.0" -> listOf(true)
        "false",
        "f",
        "no",
        "n",
        "0",
        "0.0" -> listOf(false)
        else -> emptyList()
      }
    else -> emptyList()
  }
}

/** See [specification](https://hl7.org/fhirpath/N1/#convertstoboolean-boolean). */
internal fun Collection<Any>.convertsToBoolean(): Collection<Boolean> {
  check(size <= 1) { "convertsToBoolean() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return listOf(toBoolean().isNotEmpty())
}

/** See [specification](https://hl7.org/fhirpath/N1/#tointeger-integer). */
internal fun Collection<Any>.toInteger(): Collection<Int> {
  check(size <= 1) { "toInteger() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return when (val value = single()) {
    is Int -> listOf(value)
    is String -> {
      value.toIntOrNull()?.let { listOf(it) } ?: emptyList()
    }
    is Boolean -> if (value) listOf(1) else listOf(0)
    else -> emptyList()
  }
}

/** See [specification](https://hl7.org/fhirpath/N1/#convertstointeger-boolean). */
internal fun Collection<Any>.convertsToInteger(): Collection<Boolean> {
  check(size <= 1) { "convertsToInteger() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return listOf(toInteger().isNotEmpty())
}

/** See [specification](https://hl7.org/fhirpath/N1/#todate-date). */
internal fun Collection<Any>.toDate(): Collection<FhirDate> {
  check(size <= 1) { "toDate() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return when (val value = single()) {
    is FhirDate -> listOf(value)
    is FhirDateTime -> TODO("Clarify the requirement in the specification")
    is String ->
      try {
        listOf(FhirDate.fromString(value)!!)
      } catch (_: Exception) {
        emptyList()
      }
    else -> emptyList()
  }
}

/** See [specification](https://hl7.org/fhirpath/N1/#convertstodate-boolean). */
internal fun Collection<Any>.convertsToDate(): Collection<Boolean> {
  check(size <= 1) { "convertsToDate() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return when (val value = single()) {
    is FhirDate -> listOf(true)
    is FhirDateTime -> listOf(true)
    is String ->
      try {
        FhirDate.fromString(value)
        listOf(true)
      } catch (_: Exception) {
        listOf(false)
      }
    else -> listOf(false)
  }
}

/** See [specification](https://hl7.org/fhirpath/N1/#todatetime-datetime). */
internal fun Collection<Any>.toDateTime(): Collection<FhirPathDateTime> {
  check(size <= 1) { "toDateTime() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return when (val value = single()) {
    is FhirPathDateTime -> listOf(value)
    is FhirDateTime -> listOf(FhirPathDateTime.fromFhirDateTime(value))
    is FhirDate -> listOf(FhirPathDateTime.fromString(value.toString()))
    is String ->
      try {
        listOf(FhirPathDateTime.fromString(value))
      } catch (_: Exception) {
        emptyList()
      }
    else -> emptyList()
  }
}

/** See [specification](https://hl7.org/fhirpath/N1/#convertstodatetime-boolean). */
internal fun Collection<Any>.convertsToDateTime(): Collection<Boolean> {
  check(size <= 1) { "convertsToDateTime() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return when (val value = single()) {
    is FhirPathDateTime -> listOf(true)
    is FhirDateTime -> listOf(true)
    is FhirDate -> listOf(true)
    is String ->
      try {
        FhirPathDateTime.fromString(value)
        listOf(true)
      } catch (_: Exception) {
        listOf(false)
      }
    else -> listOf(false)
  }
}

/** See [specification](https://hl7.org/fhirpath/N1/#todecimal-decimal). */
internal fun Collection<Any>.toDecimal(): Collection<Double> {
  check(size <= 1) { "toDecimal() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return when (val value = single()) {
    is Double -> listOf(value)
    is Int -> listOf(value.toDouble())
    is Boolean -> listOf(if (value) 1.0 else 0.0)
    is String -> {
      value.toDoubleOrNull()?.let { listOf(it) } ?: emptyList()
    }
    else -> emptyList()
  }
}

/** See [specification](https://hl7.org/fhirpath/N1/#convertstodecimal-boolean). */
internal fun Collection<Any>.convertsToDecimal(): Collection<Boolean> {
  check(size <= 1) { "convertsToDecimal() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return listOf(toDecimal().isNotEmpty())
}

/** See [specification](https://hl7.org/fhirpath/N1/#toquantityunit-string-quantity). */
internal fun Collection<Any>.toQuantity(unit: String?): Collection<Quantity> {
  check(size <= 1) { "toQuantity() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return when (val item = single()) {
    is Quantity -> listOf(item)
    is Int -> listOf(Quantity(value = Decimal(value = item.toDouble()), unit = DEFAULT_UNIT))
    is Double -> listOf(Quantity(value = Decimal(value = item), unit = DEFAULT_UNIT))
    is Boolean ->
      listOf(Quantity(value = Decimal(value = if (item) 1.0 else 0.0), unit = DEFAULT_UNIT))
    is String -> {
      val regex =
        """(?<value>[-+]?\d+(\.\d+)?)\s*('(?<unit>[^']+)'|(?<time>[a-zA-Z]+))?""".toRegex()
      val match = regex.find(item.trim())

      if (match != null) {
        listOf(
          Quantity(
            value = Decimal(value = match.groups["value"]!!.value.toDouble()),
            unit =
              com.google.fhir.model.r4.String(
                value = match.groups["unit"]?.value ?: match.groups["time"]?.value ?: "1"
              ),
          )
        )
      } else {
        emptyList()
      }
    }
    else -> emptyList()
  }
}

/** See [specification](https://hl7.org/fhirpath/N1/#convertstoquantityunit-string-boolean). */
internal fun Collection<Any>.convertsToQuantity(): Collection<Boolean> {
  check(size <= 1) { "convertsToQuantity() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return listOf(toQuantity(unit = null).isNotEmpty())
}

/** See [specification](https://hl7.org/fhirpath/N1/#tostring-string). */
internal fun Collection<Any>.toStringFun(): Collection<String> {
  check(size <= 1) { "toString() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return when (val item = single()) {
    // TODO: Correct this
    is String -> listOf(item)
    is Boolean -> listOf(item.toString())
    is Int -> listOf(item.toString())
    is Double -> listOf(item.toString())
    is Date -> listOf(item.toString())
    else -> emptyList() // Per the spec, if not one of the convertible types, the result is empty.
  }
}

/**
 * See [specification](https://hl7.org/fhirpath/N1/#convertstostring-string).
 *
 * NB: The URL is inconsistent with other functions due to a function signature error in the
 * documentation.
 *
 * TODO: Correct URL once https://jira.hl7.org/browse/FHIR-52051 is addressed.
 */
internal fun Collection<Any>.convertsToString(): Collection<Boolean> {
  check(size <= 1) { "convertsToString() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  // Per the spec, this returns true if the item is one of the convertible types.
  val isConvertible =
    when (single()) {
      is String,
      is Int,
      is Double,
      is Boolean -> true
      // TODO: Add classes for Date, Time, DateTime, and Quantity here.
      // For example: is MyDateClass, is MyQuantityClass -> true
      else -> false
    }

  return listOf(isConvertible)
}

/** See [specification](https://hl7.org/fhirpath/N1/#totime-time). */
internal fun Collection<Any>.toTime(): Collection<FhirPathTime> {
  check(size <= 1) { "toTime() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return when (val item = single()) {
    is LocalTime -> listOf(FhirPathTime.fromLocalTime(item))
    is FhirPathTime -> listOf(item)
    is String ->
      try {
        listOf(FhirPathTime.fromString(item))
      } catch (_: Exception) {
        emptyList()
      }
    else -> emptyList()
  }
}

/** See [specification](https://hl7.org/fhirpath/N1/#convertstotime-boolean). */
internal fun Collection<Any>.convertsToTime(): Collection<Boolean> {
  check(size <= 1) { "convertsToTime() cannot be called on a collection with more than 1 item" }

  if (isEmpty()) return emptyList()

  return when (val item = single()) {
    is LocalTime -> listOf(true)
    is FhirPathTime -> listOf(true)
    is String ->
      try {
        FhirPathTime.fromString(item)
        listOf(true)
      } catch (_: Exception) {
        listOf(false)
      }

    else -> listOf(false)
  }
}
