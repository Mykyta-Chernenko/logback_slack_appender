package com.github.nikitachernenko.logback.slack

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

class UtilsTest {
    @Test
    fun `test makePayload`() {
        data class Test(val f1: String, val f2: String)

        val t = Test("v1", "v2")
        assertEquals(mapOf("key" to "value", "f1" to "v1", "f2" to "v2"), makePayload(mapOf("key" to "value"), t))
        assertEquals(mapOf(), makePayload())
        assertEquals(mapOf("key" to "value"), makePayload(mapOf("key" to "value"), mapOf("key" to "value")))
    }

    @Test
    fun `test makeParams`() {
        assertTrue(
            arrayOf(
                StringParameter("k1", "v1"),
                StringParameter("k2", "v2")
            ) contentEquals makeParams(mapOf("k1" to "v1", "k2" to "v2"))
        )
        assertTrue(arrayOf<StringParameter>() contentEquals makeParams(mapOf()))
    }
}