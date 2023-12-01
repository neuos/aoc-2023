import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
    kotlin("jvm") version "1.9.21"
    application
}

group = "eu.neuhuber"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events(FAILED, PASSED, SKIPPED)
        exceptionFormat = FULL
    }
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("MainKt")
}
