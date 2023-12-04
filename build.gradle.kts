import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import java.net.HttpURLConnection
import java.net.URI
import java.time.LocalDate

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
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

abstract class InitializeDay : DefaultTask() {
    @get:Input
    abstract val day: Property<Int>

    @get:Input
    abstract val year: Property<Int>

    private val srcDir: File = project.sourceSets.main.get().kotlin.sourceDirectories.first()
    private val inputDir: File = project.sourceSets.main.get().resources.sourceDirectories.first()

    @TaskAction
    fun action() {
        println("Initializing day ${day.get()} of year ${year.get()}")
        createCodeFile()
        createInput()
    }

    @OutputFile
    fun getOutputFile(): File {
        return srcDir.resolve("${day.get().dayName}.kt")
    }

    @OutputDirectory
    fun getOutputDirectory(): File {
        return inputDir.resolve(day.get().toString())
    }

    private fun createCodeFile() {
        val file = srcDir.resolve("${day.get().dayName}.kt")
        val exists = file.exists()
        if (exists) {
            println("Code file $file already exists")
            return
        }
        val template = sourceTemplate(day.get())
        file.writeText(template)
    }


    private fun createInput() {
        val directory = inputDir.resolve(day.get().toString())
        if (!directory.exists()) directory.mkdirs()
        downloadInput(directory)
        directory.createExampleInput()
    }

    private fun File.createExampleInput() = resolve("example.txt").createNewFile()

    private fun downloadInput(directory: File) {
        val inputFile = directory.resolve("input.txt")
        val exists = inputFile.exists()
        if (exists) {
            println("Input file $inputFile already exists")
            return
        }
        val cookie = System.getenv("AOC_SESSION")
        val url = URI("https://adventofcode.com/${year.get()}/day/${day.get()}/input").toURL()
        val connection = url.openConnection() as HttpURLConnection
        connection.setRequestProperty("Cookie", "session=$cookie")
        val input = connection.inputStream
        inputFile.writeText(input.reader().readText())
    }

    private val Int.dayName: String
        get() = "Day${toString().padStart(2, '0')}"

    private fun sourceTemplate(day: Int) = """
                    object ${day.dayName} : Day($day) {
                        override val expected = DayResult("TODO", "TODO", "TODO", "TODO")
                        override fun solvePart1(input: Sequence<String>): Any {
                            return 0
                        }
                    
                        override fun solvePart2(input: Sequence<String>): Any {
                            return 0
                        }
                    }
                """.trimIndent()
}

tasks.register<InitializeDay>("initializeDay") {
    val today = LocalDate.now()
    day = today.dayOfMonth
    year = today.year
}
