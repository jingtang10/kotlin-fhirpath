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
import com.google.fhir.fhirpath.codegen.schema.getNestedClassName
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.asTypeName

object BackboneElementExtensionFileSpecGenerator {
  fun generate(packageName: String, structureDefinitions: List<StructureDefinition>): FileSpec {
    val backboneElementExtensionClassName = ClassName(packageName, "MoreBackboneElements")
    val backboneElementClassName = ClassName(packageName, "BackboneElement")

    return FileSpec.builder(backboneElementExtensionClassName)
      .addFunction(
        FunSpec.builder("getProperty")
          .addModifiers(KModifier.INTERNAL)
          .receiver(backboneElementClassName)
          .returns(Any::class.asTypeName().copy(nullable = true))
          .addParameter(name = "name", type = String::class)
          .beginControlFlow("return when(this)")
          .apply {
            for (structureDefinition in structureDefinitions) {
              val modelClassName = ClassName(packageName, structureDefinition.name.capitalized())
              for (backboneElement in structureDefinition.backboneElements) {
                addStatement(
                  "is %T -> getProperty(name)",
                  backboneElement.key.getNestedClassName(modelClassName),
                )
              }
            }
            addStatement("else -> null")
          }
          .endControlFlow()
          .build()
      )
      .build()
  }
}
