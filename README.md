# Kotlin FHIRPath

[![stability-wip](https://img.shields.io/badge/stability-wip-lightgrey.svg)](https://guidelines.denpa.pro/stability#work-in-progress) [![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Kotlin FHIRPath is an implementation of [HL7® FHIR®](https://www.hl7.org/fhir/overview.html)'s
[FHIRPath](https://hl7.org/fhirpath/N1/) on
[Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html).

**Warning:** The library is WIP. DO NOT USE.

## Implementation

This project uses [ANTLR Kotlin](https://github.com/Strumenta/antlr-kotlin) to generate the parser.

It's located in `fhirpath/build/generatedAntlr`.

To run the antlr task:

```shell
./gradlew generateKotlinGrammarSource
```

A codegen in `fhirpath-codegen` generates code that is used by the implementation to access fields.
To run the codegen separately, run

```shell
./gradlew r4
```

It's located in `fhirpath/build/generated`.

No reflection is used due to jvm dependency.

Internal types

| FHIRPath type <img src="images/fhir.png" alt="kotlin" style="height: 1em"/> | Internal type <img src="images/kotlin.png" alt="kotlin" style="height: 1em"/> |
|-----------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| Boolean                                                                     | kotlin.Boolean                                                                |
| String                                                                      | kotlin.String                                                                 |
| Integer                                                                     | kotlin.Int                                                                    |
| Long                                                                        | kotlin.Long                                                                   |
| Decimal                                                                     | kotlin.Double                                                                 |
| Date                                                                        | com.google.fhir.model.r4.FhirDate                                             |
| DateTime                                                                    | com.google.fhir.model.r4.FhirDateTime                                         |
| Time                                                                        | kotlinx.datetime.LocalTime                                                    |
| Quantity                                                                    | com.google.fhir.model.r4.Quantity                                             |

## User Guide

## Developer Guide

### Tests

[XmlUtil](https://github.com/pdvrieze/xmlutil) is used to load the XML test cases.

To test, run:

```shell
./gradlew :fhirpath:jvmTest
```

### Third Party

The [third_party](third_party/) directory includes resources from the FHIRPath specification for
code generation and testing purposes.
- [`fhirpath-2.0.0`](third_party/fhirpath-2.0.0/): content from FHIRPath Normative Release
[N1 (v2.0.0)](https://hl7.org/fhirpath/N1/)
- [`grammar`](third_party/fhirpath-2.0.0/grammar): the formal
[antlr grammar](https://hl7.org/fhirpath/N1/grammar.html)
- [`tests`](third_party/fhirpath-2.0.0/tests): the [test cases](https://hl7.org/fhirpath/N1/tests.html),
with
- modifications to make the test case file a valid XML document
- JSON versions of the test resources generated using [Anton V.](https://www.antvaset.com/)'s
[FHIR Converter](https://www.antvaset.com/fhir-converter) alongside the XML versions
- [`hl7.fhir.r4.core`](third_party/hl7.fhir.r4.core/): content from
[FHIR R4](https://hl7.org/fhir/R4/) for code generation
