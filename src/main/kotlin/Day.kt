import java.io.FileNotFoundException

sealed class Day(private val dayNumber: Int) : Comparable<Day> {
    fun part1Example(): Any = solvePart1(getExampleInput(1))
    fun part1(): Any = solvePart1(getInput())
    fun part2Example(): Any = solvePart2(getExampleInput(2))
    fun part2(): Any = solvePart2(getInput())
    private fun getInput() = loadLines("$dayNumber/input.txt")
    private fun getExampleInput(i: Int) = loadLines("$dayNumber/example$i.txt")
    private fun loadLines(name: String) = resource(name).bufferedReader().lineSequence()
    private fun resource(name: String) =
        this::class.java.getResourceAsStream(name) ?: throw FileNotFoundException("Resource $name not found")

    abstract val expected: DayResult
    protected abstract fun solvePart1(input: Sequence<String>): Any
    protected abstract fun solvePart2(input: Sequence<String>): Any
    override fun toString() = "Day $dayNumber"
    override fun compareTo(other: Day) = dayNumber.compareTo(other.dayNumber)
}

data class DayResult(
    val part1Example: Any? = null, val part1: Any? = null, val part2Example: Any? = null, val part2: Any? = null
)