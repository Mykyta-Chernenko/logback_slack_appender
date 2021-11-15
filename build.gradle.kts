val kotlinVersion = "1.5.31"

plugins {
    kotlin("jvm") version "1.5.31"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
    `maven-publish`
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

group = "com.github.nikitachernenko"
version = "1.0.2"

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("lib") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/nikita-chernenko/logback_slack_appender")
            credentials {
                username = (findProperty("githubUsername") ?: System.getenv("USERNAME"))?.toString()
                password = (findProperty("githubToken") ?: System.getenv("TOKEN"))?.toString()
            }
        }
    }
}

dependencies {
    implementation(kotlin("stdlib", kotlinVersion))

    implementation("ch.qos.logback:logback-classic:1.2.7")
    implementation("com.squareup.okhttp3:okhttp:4.9.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
}
