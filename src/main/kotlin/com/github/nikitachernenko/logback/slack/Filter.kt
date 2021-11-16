package com.github.nikitachernenko.logback.slack

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker
import org.slf4j.MarkerFactory

val slackMarker: Marker = MarkerFactory.getMarker("slack")

class Filter : Filter<ILoggingEvent?>() {
    var marker: String = slackMarker.name
    override fun decide(eventObject: ILoggingEvent?): FilterReply {
        return if (eventObject?.marker?.name == marker) {
            FilterReply.ACCEPT
        } else {
            FilterReply.DENY
        }
    }
}