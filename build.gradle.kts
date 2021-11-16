val kotlinVersion = "1.4.30"

plugins {
    kotlin("jvm") version "1.4.30"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.4.30"
    `maven-publish`
}

java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
}

group = "com.github.nikitachernenko"
version = "1.0.9"

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
    // Instant serialization support
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.1")
}

tasks.test {
    useJUnitPlatform()
}