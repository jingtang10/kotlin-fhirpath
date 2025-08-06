package com.google.fhir.fhirpath

enum class FhirPathType {
    BOOLEAN,
    STRING,
    INTEGER,
    LONG,
    DECIMAL,
    DATE,
    DATETIME,
    TIME,
    QUANTITY;

    companion object {
        fun fromString(value: String): FhirPathType? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}