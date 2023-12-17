import util.*
import util.Direction.*

object Day17 : Day(17) {
    override val expected = DayResult(102, 694, 94, 829)
    override fun solvePart1(input: Sequence<String>) = input.toIntGrid().minHeatLoss(3)
    override fun solvePart2(input: Sequence<String>) = input.toIntGrid().minHeatLoss(10, 4)

    private fun Grid<Int>.minHeatLoss(maxStraight: Int, minStraight: Int = 0): Int {
        val start = bounds.topLeft
        val end = bounds.bottomRight
        val path = findShortestPath(
            start = DirectedCoordinate(start, 0, RIGHT),
            isEnd = { it.coordinate == end && it.length >= minStraight },
            next = {
                it.length.let { length ->
                    when {
                        length == 0 -> listOf(UP, DOWN, LEFT, RIGHT)
                        length < minStraight -> listOf(UP)
                        else -> listOf(LEFT, RIGHT, UP)
                    }.map { dir -> it.plusRelative(dir) }.filter { (coordinate, line) ->
                        coordinate in this && line <= maxStraight
                    }
                }
            },
            cost = { _, to -> this[to.coordinate] },
        ) ?: error("No path found")

        return path.drop(1).sumOf { this[it.coordinate] }
    }

    data class DirectedCoordinate(val coordinate: Coordinate, val length: Int, val direction: Direction) {
        override fun toString() = "$coordinate $length $direction"

        fun plusRelative(relative: Direction): DirectedCoordinate {
            val dir = this.direction.relative(relative)
            val length = if (dir == this.direction) {
                this.length + 1
            } else 1
            return DirectedCoordinate(coordinate + dir, length, dir)
        }
    }
}

