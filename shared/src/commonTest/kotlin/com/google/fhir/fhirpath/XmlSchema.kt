package com.google.fhir.fhirpath

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@XmlSerialName("tests")
data class Tests(
    val name: String,
    val description: String,
    val reference: String,
    val groups: List<Group>,
)

@Serializable
@XmlSerialName("group")
data class Group(
    val name: String,
    val description: String? = null,
    val tests: List<Test>,
)

@Serializable
@XmlSerialName("test")
data class Test(
    val name: String,
    val description: String? = null,
    val inputfile: String,
    val invalid: Boolean? = null,
    val ordered: Boolean? = null,
    val mode: String? = null,
    val checkOrderedFunctions: Boolean? = null,
    val isPredicate: Boolean? = null,
    val expression: Expression,
    val outputs: List<Output>,
    val predicate: Boolean? = null,
)

@Serializable
@XmlSerialName("expression")
data class Expression(
    val invalid: String? = null,
    @XmlValue val value: String,
)

@Serializable
@XmlSerialName("output")
data class Output(
    val type: String,
    @XmlValue val value: String,
)