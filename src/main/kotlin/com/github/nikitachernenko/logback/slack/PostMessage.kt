package com.github.nikitachernenko.logback.slack

import java.net.URL
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


typealias PostSlackMessageFn = (uri: String, message: Message) -> Unit


fun postSlackMessage(
    uri: String,
    message: Message
) {
    val json = Json.encodeToString(Message.serializer(), message)
    val jsonType: MediaType = "application/json; charset=utf-8".toMediaType()
    val body = json.toRequestBody(jsonType)
    val client = OkHttpClient();
    val request = Request.Builder()
        .url(URL(uri))
        .post(body)
        .build()

    val response = client.newCall(request).execute()
    if (response.code != 200) throw Exception("code: ${response.code}, body: ${response.body?.string()}")
}