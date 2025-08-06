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

import com.google.fhir.fhirpath.functions.combining.combine
import com.google.fhir.fhirpath.functions.conversion.convertsToBoolean
import com.google.fhir.fhirpath.functions.conversion.convertsToDecimal
import com.google.fhir.fhirpath.functions.conversion.convertsToInteger
import com.google.fhir.fhirpath.functions.conversion.convertsToQuantity
import com.google.fhir.fhirpath.functions.conversion.convertsToString
import com.google.fhir.fhirpath.functions.conversion.toBoolean
import com.google.fhir.fhirpath.functions.conversion.toDecimal
import com.google.fhir.fhirpath.functions.conversion.toInteger
import com.google.fhir.fhirpath.functions.conversion.toQuantity
import com.google.fhir.fhirpath.functions.conversion.toStringFun
import com.google.fhir.fhirpath.functions.existence.count
import com.google.fhir.fhirpath.functions.existence.empty
import com.google.fhir.fhirpath.functions.existence.exists
import com.google.fhir.fhirpath.functions.subsetting.exclude
import com.google.fhir.fhirpath.functions.subsetting.firstFun
import com.google.fhir.fhirpath.functions.subsetting.lastFun
import com.google.fhir.fhirpath.functions.subsetting.singleFun

fun invoke(functionName: String, context: Collection<Any>, params: List<Any>): Collection<Any> =
  when (functionName) {
    // Existence
    // https://hl7.org/fhirpath/N1/#existence
    "empty" -> {
      context.empty()
    }
    "exists" -> {
      context.exists()
    }
    "count" -> {
      context.count()
    }

    // Filtering and projection
    // https://hl7.org/fhirpath/N1/#filtering-and-projection

    // Subsetting
    // https://hl7.org/fhirpath/N1/#subsetting
    "single" -> {
      context.singleFun()
    }
    "first" -> {
      context.firstFun()
    }
    "last" -> {
      context.lastFun()
    }
    "tail" -> {
      context.drop(1)  // Use kotlin's drop()
    }
    "skip" -> {
      context.drop(params[0] as Int)  // Use kotlin's drop()
    }
    "take" -> {
      context.take(params[0] as Int)  // Use kotlin's take()
    }
    "intersect" -> {
      context.intersect(params)
    }
    "exclude" -> {
      context.exclude(params)
    }

    // Combining
    // https://hl7.org/fhirpath/N1/#combining
    "union" -> {
      context.union(params)
    }
    "combine" -> {
      context.combine(params)
    }

    // Conversion
    // https://hl7.org/fhirpath/N1/#conversion
    "toBoolean" -> {
      context.toBoolean()
    }
    "convertsToBoolean" -> {
      context.convertsToBoolean()
    }
    "toInteger" -> {
      context.toInteger()
    }
    "convertsToInteger" -> {
      context.convertsToInteger()
    }
    "toDecimal" -> {
      context.toDecimal()
    }
    "convertsToDecimal" -> {
      context.convertsToDecimal()
    }
    "toQuantity" -> {
      context.toQuantity(params.firstOrNull()?.toString())
    }
    "convertsToQuantity" -> {
      context.convertsToQuantity()
    }
    "toString" -> {
      context.toStringFun()
    }
    "convertsToString" -> {
      context.convertsToString()
    }

    // String manipulation
    // https://hl7.org/fhirpath/N1/#string-manipulation

    // Math
    // https://hl7.org/fhirpath/N1/#math
    "abs" -> {
      context.abs()
    }

    // Tree navigation
    // https://hl7.org/fhirpath/N1/#tree-navigation

    // Utility functions
    // https://hl7.org/fhirpath/N1/#utility-functions

    // TODO: move the following to operations
    "is" -> {
      context.`is`(params)
    }
    "not" -> {
      context.not()
    }
    else -> {
      error("Function '$functionName' is not implemented.")
    }
  }
