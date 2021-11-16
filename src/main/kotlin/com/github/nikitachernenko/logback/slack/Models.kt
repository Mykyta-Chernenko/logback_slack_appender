package com.github.nikitachernenko.logback.slack

import kotlinx.serialization.Serializable

@Serializable
data class TextSection(val type: String, val text: String)

@Serializable
data class Block(val type: String, val text: TextSection)

@Serializable
data class Attachment(val color: String, val blocks: List<Block>)

@Serializable
data class Message(val channel: String, val attachments: List<Attachment>)