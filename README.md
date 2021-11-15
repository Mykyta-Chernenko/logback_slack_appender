# Simple Logback Slack appender

Integration between [Logback](http://logback.qos.ch/) appender that can push a message to Slack channel using webhook

## Setup

Add dependency to `com.github.nikitachernenko:logback-slack-appender:1.0.0`

add repository to this github package, example using gradle kotlin dsl
```kotlin
repositories {
    addRepository("logbackSlackAppender", "https://maven.pkg.github.com/nikita-chernenko/logack_slack_appender")
}
```


Change configuration to our logback settings
Example of Appender configuration to logback.xml. Filtering by "slack" marker
```
<configuration>
  <appender name="SLACK" class="com.github.nikitachernenko.logback.slack.Appender">
   <!-- Slack webhook uri https://api.slack.com/messaging/webhooks -->
    <webhookUri>${WEBHOOK_URI}</webhookUri>
    <!-- Channel that you want to post - default is #general -->
    <channel>mychannel</channel>
    <!-- Colors of different message -->
    <traceColor>#DCDCDC</traceColor>
    <debugColor>#C0C0C0</debugColor>
    <infoColor>#4169E1</infoColor>
    <warnColor>#FF8C00</warnColor>
    <errorColor>#FF0000</errorColor>
    <defaultColor>#F8F8FF</defaultColor> 
  </appender>
    
   <!-- Currently recommended way of using Slack appender -->
  <appender name="ASYNC_SLACK" class="ch.qos.logback.classic.AsyncAppender">
    <filter class="com.github.nikitachernenko.logback.slack.Filter">
        <!-- Specify which marker name to use to filter relevant messages-->
        <marker>slack</marker>
    </filter
    <appender-ref ref="SLACK" />
  </appender>

  <root level="info">
    <appender-ref ref="ASYNC_SLACK" />
  </root>
</configuration>
```
You can look up how to pass secret values as Webhook Uri safe here: [Variable substitution in Logback](http://logback.qos.ch/manual/configuration.html#variableSubstitution)

## Usage

typical usage will look something like that
```kotlin
import com.github.nikitachernenko.logback.slack.slackMarker
import com.github.nikitachernenko.logback.slack.makeParams
import com.github.nikitachernenko.logback.slack.makePayload

fun logAlert(message: String, vararg data: Any) {
    val payload = makePayload(*data) 
    val params = makeParams(payload) 
    logger.info(slackMarker, message, *params)
}

data class Action(val name: String)

val a = Action("test")
logAlert("new alert", mapOf("string" to "test"), a)
```
`makePaylod` maps all the objects passed in to `Map<String, String>`
`makeParams` maps `Map<String, String>` passed in to `Array<StringParameter>`

`slackMarker` is a type of marker we use to filter only relevant logs, all logs without a marker or with a different
marker than specified will be ignored
`StringParameter`  can be used to pass additional data that we will up in the Appender

so you can also use the logger without utils like this

```kotlin 
logger.info(slackMarker, "new alert", StringParameter("key", "value"))
```

the key and value will be used as a block in the message (see the picture below)
## Example of a message
![message_example.png](message_example.png)