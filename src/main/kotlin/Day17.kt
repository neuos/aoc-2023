import util.*
import util.Direction.*

object Day17 : Day(17) {
    override val expected = DayResult(102, 694, 94, 829)


    override fun solvePart1(input: Sequence<String>): Any {
        val grid = input.toIntGrid()
        val maxStraight = 3

        return grid.findCruciblePath { current ->
            listOf(
                LEFT, RIGHT, UP,
            ).map { dir -> current.plusRelative(dir) }.filter { (coordinate, line) ->
                coordinate in grid && line.length <= maxStraight
            }
        }
    }

    override fun solvePart2(input: Sequence<String>): Any {
        val grid: Grid<Int> = input.toIntGrid()
        val minStraight = 4
        val maxStraight = 10

        return grid.findCruciblePath { current ->
            current.line.length.let { length ->
                when {
                    length == 0 -> listOf(UP, DOWN, LEFT, RIGHT)
                    length < minStraight -> listOf(UP)
                    else -> listOf(LEFT, RIGHT, UP)
                }.map { dir -> current.plusRelative(dir) }.filter { (coordinate, line) ->
                    coordinate in grid && line.length <= maxStraight
                }
            }
        }
    }



    private fun Grid<Int>.findCruciblePath(
        next: (DirectedCoordinate) -> Iterable<DirectedCoordinate>
    ): Int {
        val start = Coordinate(0, 0)
        val end = Coordinate(size - 1, last().size - 1)

        val path = findShortestPath(
            DirectedCoordinate(start, Line.empty),
            { it.coordinate == end },
            next,
            { _, to -> this[to.coordinate] },
        ) ?: error("No path found")

        return path.drop(1).sumOf { this[it.coordinate] }
    }

    data class DirectedCoordinate(val coordinate: Coordinate, val line: Line) {
        override fun toString() = "$coordinate - $line"

        fun plusRelative(direction: Direction) = plus(line.direction.relative(direction))
        private fun plus(direction: Direction) = DirectedCoordinate(coordinate + direction, line + direction)
    }

    data class Line(val length: Int, val direction: Direction) {

        companion object {
            val empty = Line(0, RIGHT)
        }

        operator fun plus(direction: Direction) = if (direction == this.direction) {
            copy(length = length + 1)
        } else {
            Line(1, direction)
        }

        override fun toString(): String {
            return "$length $direction"
        }
    }

}

