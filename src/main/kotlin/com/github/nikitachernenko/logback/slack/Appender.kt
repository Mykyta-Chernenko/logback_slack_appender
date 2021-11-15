package com.github.nikitachernenko.logback.slack

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AsyncAppenderBase
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Appender(protected val postSlackMessage: PostSlackMessageFn = ::postSlackMessage) :
    AsyncAppenderBase<ILoggingEvent?>() {
    private val logger: Logger = LoggerFactory.getLogger(Appender::class.java)

    lateinit var webhookUri: String
    lateinit var channel: String

    var traceColor = "#DCDCDC"
    var debugColor = "#C0C0C0"
    var infoColor = "#4169E1"
    var warnColor = "#FF8C00"
    var errorColor = "#FF0000"
    var defaultColor = "#F8F8FF"

    override fun doAppend(eventObject: ILoggingEvent?) {
        try {
            eventObject?.let { processMessage(it.formattedMessage, it.argumentArray, it.level) }
        } catch (ex: Exception) {
            logger.error("Error posting log to Slack.com ($channel): $eventObject", ex)
        }
    }

    internal fun processMessage(message: String, arguments: Array<Any>, level: Level) {
        val payload = extractPayload(arguments)
        val blocks = makeBlocks(message, payload)
        val attachments = makeAttachments(blocks, level)
        val formattedMessage = makeMessage(channel, attachments)

        postSlackMessage(webhookUri, formattedMessage)
    }

    internal fun extractPayload(arguments: Array<Any>): Map<String, String> {
        return arguments.filterIsInstance<StringParameter>().associate { Pair(it.key, it.value) }
    }

    internal fun makeBlocks(message: String, payload: Map<String, String>): List<Block> =
        listOf(
            Block(
                "header",
                TextSection(
                    "plain_text",
                    message,
                )
            )
        ) + payload.map {
            Block(
                "section",
                TextSection(
                    "mrkdwn",
                    "*${it.key}*: ${it.value}",
                )
            )
        }

    internal fun makeAttachments(blocks: List<Block>, level: Level): List<Attachment> {
        val color = selectColor(level)
        return listOf(
            Attachment(
                color,
                blocks
            )
        )
    }

    internal fun selectColor(level: Level): String {
        return when (level) {
            Level.TRACE -> traceColor
            Level.DEBUG -> debugColor
            Level.INFO -> infoColor
            Level.WARN -> warnColor
            Level.ERROR -> errorColor
            else -> defaultColor
        }
    }

    internal fun makeMessage(
        channel: String,
        attachments: List<Attachment>
    ): Message = Message(channel, attachments)
}