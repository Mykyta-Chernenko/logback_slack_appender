package com.github.nikitachernenko.logback.slack

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper

fun makePayload(vararg data: Any): Map<String, String> {
    val objectMapper = ObjectMapper().findAndRegisterModules()
    return data.map {
        objectMapper.convertValue(it, object : TypeReference<Map<String, String>>() {})
    }.takeIf { it.isNotEmpty() }?.reduce { acc, v -> acc + v } ?: mapOf()
}

fun makeParams(payload: Map<String, String>) = payload.entries.map { StringParameter(it.key, it.value) }.toTypedArray()