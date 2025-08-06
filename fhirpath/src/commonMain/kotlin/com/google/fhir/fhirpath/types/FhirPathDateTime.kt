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

package com.google.fhir.fhirpath.types

import com.google.fhir.model.r4.FhirDateTime
import kotlin.text.get
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.toInstant

internal data class FhirPathDateTime(
  val year: Int,
  val month: Int? = null,
  val day: Int? = null,
  val hour: Int? = null,
  val minute: Int? = null,
  val second: Double? = null,
  val utcOffset: UtcOffset? = null,
) {

  enum class Precision {
    YEAR,
    MONTH,
    DAY,
    HOUR,
    MINUTE,
    SECOND,
  }

  val precision =
    when {
      second != null -> Precision.SECOND
      minute != null -> Precision.MINUTE
      hour != null -> Precision.HOUR
      day != null -> Precision.DAY
      month != null -> Precision.MONTH
      else -> Precision.YEAR
    }

  fun compareTo(other: FhirPathDateTime): Int? {
    if (precision != other.precision) {
      return null
    }
    if (precision > Precision.DAY) {
      return toInstant().compareTo(other.toInstant())
    }
    year.compareTo(other.year).let { if (it != 0) return it }
    month?.compareTo(other.month!!).let { if (it != 0) return it }
    day?.compareTo(other.day!!).let { if (it != 0) return it }
    return 0
  }

  /**
   * Converts the DateTime to an Instant, filling in minutes, seconds, and nanoseconds if they are
   * missing.
   *
   * This function is only used in this class for timezone-aware comparison of DateTime values.
   * Hence filling in the missing fields for two DateTime values with the same precision is fine.
   */
  private fun toInstant(): Instant {
    check(hour != null && utcOffset != null) {
      "Hour and UTC offset must be provided to convert DateTime to Instant"
    }
    val localDateTime =
      LocalDateTime(
        year,
        month!!,
        day!!,
        hour,
        minute ?: 0,
        second?.toInt() ?: 0,
        second?.rem(1)?.times(1_000_000_000.0)?.toInt() ?: 0,
      )
    return localDateTime.toInstant(utcOffset)
  }

  companion object {
    fun fromString(string: String): FhirPathDateTime {
      val regex =
        Regex(
          "^" +
            "(?<year>\\d{4})" +
            "(-(?<month>\\d{2})" +
            "(-(?<day>\\d{2})" +
            "(T(?<hour>\\d{2})" +
            "(:(?<minute>\\d{2})" +
            "(:(?<second>\\d{2}(?:\\.\\d+)?))?)?" +
            "(?<offset>Z|[+\\-]\\d{2}:\\d{2})?)?)?)?" +
            "$"
        )

      val match =
        regex.find(string)
          ?: throw IllegalArgumentException("Invalid FHIRPath DateTime format: $string")
      val groups = match.groups

      val year = groups["year"]!!.value.toInt()
      val month = groups["month"]?.value?.toInt()
      val day = groups["day"]?.value?.toInt()
      val hour = groups["hour"]?.value?.toInt()
      val minute = groups["minute"]?.value?.toInt()
      val second = groups["second"]?.value?.toDouble()
      val offset = groups["offset"]?.value?.let { UtcOffset.Companion.parse(it) }

      // Use kotlinx.datetime for robust validation of date and time components
      try {
        if (hour != null) {
          LocalDateTime(year, month!!, day!!, hour, minute ?: 0, second?.toInt() ?: 0)
        } else if (day != null) {
          LocalDate(year, month!!, day)
        }
        // TODO: Validate YearMonth using the new kotlinx.datetime.YearMonth class
      } catch (e: Exception) {
        throw IllegalArgumentException("Invalid date or time component in literal: $string", e)
      }

      return FhirPathDateTime(year, month, day, hour, minute, second, offset)
    }

    fun fromFhirDateTime(fhirDateTime: FhirDateTime): FhirPathDateTime {
      return fromString(fhirDateTime.toString())
    }
  }
}
