package com.github.nikitachernenko.logback.slack

import ch.qos.logback.classic.Level
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test


class AppenderTest {
    val appender = Appender { _, _ -> }
    val blocks: List<Block> = listOf(Block("header", TextSection("plain_text", "test")))
    val params = listOf(StringParameter("k1", "v1"), StringParameter("k2", "v2"))


    @Test
    fun `test message colors`() {
        assertEquals(appender.traceColor, appender.selectColor(Level.TRACE))
        assertEquals(appender.debugColor, appender.selectColor(Level.DEBUG))
        assertEquals(appender.infoColor, appender.selectColor(Level.INFO))
        assertEquals(appender.warnColor, appender.selectColor(Level.WARN))
        assertEquals(appender.errorColor, appender.selectColor(Level.ERROR))
        assertEquals(appender.defaultColor, appender.selectColor(Level.ALL))
        assertEquals(appender.defaultColor, appender.selectColor(Level.OFF))
    }

    @Test
    fun `test changing colors`() {
        val appender = Appender { _, _ -> }
        appender.warnColor = "testColor"
        assertEquals("testColor", appender.selectColor(Level.WARN))
    }

    @Test
    fun `test make attachments`() {
        val attachments = appender.makeAttachments(blocks, Level.WARN)
        assertEquals(1, attachments.size)
        val attachment = attachments[0]
        assertEquals(appender.warnColor, attachment.color)
        assertEquals(blocks, attachment.blocks)
    }

    @Test
    fun `test extract payload`() {
        val invalidParams = listOf(Object(), "string", 1)
        val allParams: Array<Any> = (params + invalidParams).toTypedArray()
        val payload = appender.extractPayload(allParams)
        assertEquals(mapOf("k1" to "v1", "k2" to "v2"), payload)
    }

    @Test
    fun `test processMessage`() {
        val uri = "uri"
        val channel = "channel"
        val text = "messageText"
        var postMessageCalled = false
        val postMessage: PostSlackMessageFn = { passedUri, message ->
            postMessageCalled = true
            assertEquals(uri, passedUri)
            val expectedMessage = mapOf(
                "channel" to channel,
                "attachments" to listOf(
                    mapOf(
                        "color" to "#FF8C00",
                        "blocks" to listOf(
                            mapOf(
                                "type" to "header",
                                "text" to mapOf(
                                    "type" to "plain_text",
                                    "text" to text
                                )
                            ),
                            mapOf(
                                "type" to "section",
                                "text" to mapOf(
                                    "type" to "mrkdwn",
                                    "text" to "*${params[0].key}*: ${params[0].value}"
                                )
                            ),
                            mapOf(
                                "type" to "section",
                                "text" to mapOf(
                                    "type" to "mrkdwn",
                                    "text" to "*${params[1].key}*: ${params[1].value}"
                                )
                            )
                        )
                    )
                )
            )
            val resultedJson = Json.encodeToString(Message.serializer(), message)
            val expectedJson = ObjectMapper().writeValueAsString(expectedMessage)
            assertEquals(expectedJson, resultedJson)
        }
        val appender = Appender(postMessage)
        appender.webhookUri = uri
        appender.channel = channel
        appender.processMessage(text, params.toTypedArray(), Level.WARN)
        assertTrue(postMessageCalled)
    }
}