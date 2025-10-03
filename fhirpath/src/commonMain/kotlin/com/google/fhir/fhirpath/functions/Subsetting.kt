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

/** See [specification](https://hl7.org/fhirpath/N1/#single-collection). */
internal fun Collection<Any>.singleFun(): Collection<Any> =
  if (isEmpty()) {
    emptyList()
  } else {
    listOf(this.single())
  }

/** See [specification](https://hl7.org/fhirpath/N1/#first-collection). */
internal fun Collection<Any>.firstFun(): Collection<Any> =
  if (isEmpty()) {
    emptyList()
  } else {
    listOf(this.first())
  }

/** See [specification](https://hl7.org/fhirpath/N1/#last-collection). */
internal fun Collection<Any>.lastFun(): Collection<Any> =
  if (isEmpty()) {
    emptyList()
  } else {
    listOf(this.last())
  }

/** See [specification](https://hl7.org/fhirpath/N1/#intersectother-collection-collection). */
internal fun Collection<Any>.intersectFun(other: Collection<Any>): Collection<Any> {
  return this.toSet().intersect(other.toSet())
}

/** See [specification](https://hl7.org/fhirpath/N1/#excludeother-collection-collection). */
internal fun Collection<Any>.exclude(other: Collection<Any>): Collection<Any> {
  return this.toMutableList().apply { removeAll(other) }
}
