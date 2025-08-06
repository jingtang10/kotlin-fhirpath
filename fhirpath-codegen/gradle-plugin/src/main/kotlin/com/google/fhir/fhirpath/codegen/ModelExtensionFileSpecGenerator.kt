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

package com.google.fhir.fhirpath.codegen

import com.google.fhir.fhirpath.codegen.schema.StructureDefinition
import com.google.fhir.fhirpath.codegen.schema.backboneElements
import com.google.fhir.fhirpath.codegen.schema.capitalized
import com.google.fhir.fhirpath.codegen.schema.getElementName
import com.google.fhir.fhirpath.codegen.schema.getNestedClassName
import com.google.fhir.fhirpath.codegen.schema.rootElements
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.asTypeName

object ModelExtensionFileSpecGenerator {
  fun generate(packageName: String, structureDefinition: StructureDefinition): FileSpec {
    val modelExtensionClassName =
      ClassName(packageName, "More${structureDefinition.name.capitalized()}s")
    val modelClassName = ClassName(packageName, structureDefinition.name.capitalized())

    return FileSpec.builder(modelExtensionClassName)
      .addFunction(
        FunSpec.builder("getProperty")
          .addModifiers(KModifier.INTERNAL)
          .receiver(modelClassName)
          .addParameter(name = "name", type = String::class)
          .returns(Any::class.asTypeName().copy(nullable = true))
          .beginControlFlow("return when(name)")
          .apply {
            for (element in structureDefinition.rootElements) {
              addStatement("%S -> this.%N", element.getElementName(), element.getElementName())
            }
            addStatement("else -> null")
          }
          .endControlFlow()
          .build()
      )
      .apply {
        for (backboneElement in structureDefinition.backboneElements) {
          addFunction(
            FunSpec.builder("getProperty")
              .addModifiers(KModifier.INTERNAL)
              .receiver(backboneElement.key.getNestedClassName(modelClassName))
              .addParameter(name = "name", type = String::class)
              .returns(Any::class.asTypeName().copy(nullable = true))
              .beginControlFlow("return when(name)")
              .apply {
                for (element in backboneElement.value) {
                  addStatement("%S -> %N", element.getElementName(), element.getElementName())
                }
                addStatement("else -> null")
              }
              .endControlFlow()
              .build()
          )
        }
      }
      .build()
  }
}
