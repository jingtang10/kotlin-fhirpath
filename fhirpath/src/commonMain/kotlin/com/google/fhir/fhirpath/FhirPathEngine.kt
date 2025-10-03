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

import com.google.fhir.fhirpath.FhirPathEvaluator
import com.google.fhir.fhirpath.parsers.fhirpathLexer
import com.google.fhir.fhirpath.parsers.fhirpathParser
import com.google.fhir.model.r4.Resource
import org.antlr.v4.kotlinruntime.BailErrorStrategy
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.Token

/**
 * Evaluates a FHIRPath expression against a single FHIR resource.
 *
 * @param expression The FHIRPath string to evaluate (e.g., "Patient.name.given").
 * @param resource The initial FHIR resource to run the expression against.
 * @return A com.google.fhir.fhirpath.codegen.collection of `Base` FHIR elements that result from
 *   the evaluation. Non-FHIR results (literals) are filtered out.
 */
fun evaluateFhirPath(expression: String, resource: Resource?): Collection<Any> {
  val lexer = fhirpathLexer(CharStreams.fromString(expression))
  val tokenStream = CommonTokenStream(lexer)
  val parser =
    fhirpathParser(tokenStream).apply {
      // Make sure the parser fails for invalid expressions instead of trying to recover
      errorHandler = BailErrorStrategy()
    }

  val parsedExpression = parser.expression()
  // ANTLR attempts to parse the entire expression but does not throw an error when it cannot. In
  // such cases, explicitly check that the entire expression has been consumed to ensure that the
  // expression is valid.
  if (tokenStream.LA(1) != Token.EOF) {
    error(
      "Expression contains extraneous input that could not be parsed: '${tokenStream[parser.currentToken!!.tokenIndex + 1].text}'"
    )
  }

  val evaluator = FhirPathEvaluator(initialContext = resource)
  val result = evaluator.visit(parsedExpression)
  return result
}
