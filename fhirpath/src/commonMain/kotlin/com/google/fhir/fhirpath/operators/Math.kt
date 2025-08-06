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

import com.google.fhir.model.r4.Quantity

/** See [specification](https://hl7.org/fhirpath/N1/#multiplication). */
internal fun multiplication(left: Collection<Any>, right: Collection<Any>): Collection<Any> {
  TODO("Implement multiplication")
}

/** See [specification](https://hl7.org/fhirpath/N1/#division). */
internal fun division(left: Collection<Any>, right: Collection<Any>): Collection<Any> {
  val leftItem = left.single()
  val rightItem = right.single()
  val leftDouble =
    when (leftItem) {
      is Int -> leftItem.toDouble()
      is Long -> leftItem.toDouble()
      is Double -> leftItem
      else -> error("Operand of division must be a number")
    }
  val rightDouble =
    when (rightItem) {
      is Int -> rightItem.toDouble()
      is Long -> rightItem.toDouble()
      is Double -> rightItem
      else -> error("Operand of division must be a number")
    }

  val result = leftDouble / rightDouble
  if (result.isInfinite()) return emptyList()
  return listOf(result)
}

/** See [specification](https://hl7.org/fhirpath/N1/#addition). */
internal fun addition(left: Collection<Any>, right: Collection<Any>): Collection<Any> {
  val leftItem = left.single()
  val rightItem = right.single()
  return when {
    leftItem is Int && rightItem is Int -> listOf(leftItem + rightItem)
    leftItem is Double && rightItem is Double -> listOf(leftItem + rightItem)
    leftItem is String && rightItem is String -> listOf(leftItem + rightItem)
    leftItem is Quantity && rightItem is Quantity -> TODO("Implement adding two quantities")
    else -> error("Cannot add $leftItem and $rightItem")
  }
}

/** See [specification](https://hl7.org/fhirpath/N1/#subtraction). */
internal fun subtraction(left: Collection<Any>, right: Collection<Any>): Collection<Any> {
  val leftItem = left.single()
  val rightItem = right.single()
  return when {
    leftItem is Int && rightItem is Int -> listOf(leftItem - rightItem)
    leftItem is Double && rightItem is Double -> listOf(leftItem - rightItem)
    leftItem is Quantity && rightItem is Quantity -> TODO("Implement subtracting two quantities")
    else -> error("Cannot subtract $rightItem from $leftItem")
  }
}

/** See [specification](https://hl7.org/fhirpath/N1/#div). */
internal fun div(left: Collection<Any>, right: Collection<Any>): Collection<Any> {
  val leftDouble =
    when (val leftItem = left.single()) {
      is Int -> leftItem.toDouble()
      is Long -> leftItem.toDouble()
      is Double -> leftItem
      else -> error("Operand of div must be a number")
    }
  val rightDouble =
    when (val rightItem = right.single()) {
      is Int -> rightItem.toDouble()
      is Long -> rightItem.toDouble()
      is Double -> rightItem
      else -> error("Operand of div must be a number")
    }
  val result = leftDouble / rightDouble

  if (result.isInfinite()) return emptyList()
  return listOf(result.toInt())
}

/** See [specification](https://hl7.org/fhirpath/N1/#mod). */
internal fun mod(left: Collection<Any>, right: Collection<Any>): Collection<Any> {
  val leftItem = left.single()
  val leftDouble =
    when (leftItem) {
      is Int -> leftItem.toDouble()
      is Long -> leftItem.toDouble()
      is Double -> leftItem
      else -> error("Operand of mod must be a number")
    }
  val rightItem = right.single()
  val rightDouble =
    when (rightItem) {
      is Int -> rightItem.toDouble()
      is Long -> rightItem.toDouble()
      is Double -> rightItem
      else -> error("Operand of mod must be a number")
    }
  val result = leftDouble % rightDouble

  if (result.isNaN()) return emptyList()
  if (leftItem is Int && rightItem is Int) return listOf(result.toInt())
  if (
    (leftItem is Long && rightItem is Long) ||
      (leftItem is Int && rightItem is Long) ||
      (leftItem is Long && rightItem is Int)
  ) {
    return listOf(result.toLong())
  }
  return listOf(result)
}

/** See [specification](https://hl7.org/fhirpath/N1/#string-concatenation) */
internal fun concat(left: Collection<Any>, right: Collection<Any>): Collection<Any> {
  check(left.size <= 1) { "& cannot be called on a collection with more than 1 item" }
  check(right.size <= 1) { "& cannot be called on a collection with more than 1 item" }

  val leftString = (left.singleOrNull() as String?) ?: ""
  val rightString: String = (right.singleOrNull() as String?) ?: ""
  return listOf(leftString + rightString)
}
