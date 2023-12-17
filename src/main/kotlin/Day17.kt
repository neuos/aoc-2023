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
            start = DirectedCoordinate(start, DirectedLine.empty),
            isEnd = { it.coordinate == end && it.line.length >= minStraight },
            next = {
                it.line.length.let { length ->
                    when {
                        length == 0 -> listOf(UP, DOWN, LEFT, RIGHT)
                        length < minStraight -> listOf(UP)
                        else -> listOf(LEFT, RIGHT, UP)
                    }.map { dir -> it.plusRelative(dir) }.filter { (coordinate, line) ->
                        coordinate in this && line.length <= maxStraight
                    }
                }
            },
            cost = { _, to -> this[to.coordinate] },
        ) ?: error("No path found")

        return path.drop(1).sumOf { this[it.coordinate] }
    }

    data class DirectedCoordinate(val coordinate: Coordinate, val line: DirectedLine) {
        override fun toString() = "$coordinate $line"

        fun plusRelative(direction: Direction) = plus(line.direction.relative(direction))
        private fun plus(direction: Direction) = DirectedCoordinate(coordinate + direction, line + direction)
    }

    data class DirectedLine(val length: Int, val direction: Direction) {

        companion object {
            val empty = DirectedLine(0, RIGHT)
        }

        operator fun plus(direction: Direction) = if (direction == this.direction) {
            copy(length = length + 1)
        } else {
            DirectedLine(1, direction)
        }

        override fun toString() = "$length $direction"
    }

}

