import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

object Day06 : Day(6) {
    override val expected = DayResult(288, 316800, 71503, 45647654)
    override fun solvePart1(input: Sequence<String>): Any {
        val (timeLine, distanceLine) = input.toList()
        val times = timeLine.split(Regex("\\D")).filter { it.isNotEmpty() }.map { it.toLong() }
        val distances = distanceLine.split(Regex("\\D")).filter { it.isNotEmpty() }.map { it.toLong() }

        return times.zip(distances).map { (time, distance) ->
            waysToWinMath(time, distance)
        }.reduce(Int::times)
    }

    override fun solvePart2(input: Sequence<String>): Int {
        val (timeLine, distanceLine) = input.toList()
        val time = timeLine.filter { it.isDigit() }.toLong()
        val distance = distanceLine.filter { it.isDigit() }.toLong()
        return waysToWinMath(time, distance)
    }

    private fun waysToWinBruteForce(time: Long, distance: Long) = (1..<time).count { v ->
        (v * (time - v)) > distance
    }

    private fun waysToWinMath(time: Long, distance: Long): Int {
        // (v * (time - v)) > distance
        // v * time - v^2 > distance
        // v^2 - v * time + distance < 0
        val (lower, higher) = solveQuadratic(-time.toDouble(), distance.toDouble())
        // if a solution is an integer, it does not win, but draws
        return higher.nextDownInt() - lower.nextUpInt() + 1
    }

    /**
     * Returns the Int value nearest to this value in direction of positive infinity.
     */
    private fun Double.nextUpInt() = ceil(this).let { if (it == this) it + 1 else it }.toInt()

    /**
     * Returns the Int value nearest to this value in direction of negative infinity.
     */
    private fun Double.nextDownInt() = floor(this).let { if (it == this) it - 1 else it }.toInt()

    /**
     * Returns the next representable floating-point value after this value in direction of positive infinity.
     */
    fun solveQuadratic(p: Double, q: Double): Pair<Double, Double> {
        val root = sqrt((p * p) / 4 - q)
        val lower = -p / 2 - root
        val higher = -p / 2 + root
        return lower to higher
    }
}

fun main() {
    println(Day06.solveQuadratic(-30.0, 200.0))
}