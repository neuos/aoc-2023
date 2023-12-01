import java.net.URL

abstract class Day(private val dayNumber: Int) {
    fun part1Example(): Any = solvePart1(getExampleInput())
    fun part1(): Any = solvePart1(getInput())
    fun part2Example(): Any = solvePart2(getExampleInput())
    fun part2(): Any = solvePart2(getInput())

    private fun getInput(): List<String> = loadLines("$dayNumber/input.txt")
    private fun getExampleInput(): List<String> = loadLines("$dayNumber/example.txt")

    private fun loadLines(name: String) = resource(name).readText().lines()

    private fun resource(name: String): URL = this::class.java.getResource(name) ?: error("Resource $name not found")
    protected abstract fun solvePart1(input: List<String>): Any
    protected abstract fun solvePart2(input: List<String>): Any

}

