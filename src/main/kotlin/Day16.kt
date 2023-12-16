import util.*
import util.Direction.*

object Day16 : Day(16) {
    override val expected = DayResult(46, 7608, 51, 8221)
    override fun solvePart1(input: Sequence<String>): Int {
        val grid: CharGrid = input.filter { it.isNotEmpty() }.toGrid()
        val start = Beam(Coordinate(0, -1), RIGHT)
        return energize(grid, start)
    }

    override fun solvePart2(input: Sequence<String>): Int {
        val grid: CharGrid = input.filter { it.isNotEmpty() }.toGrid()
        return grid.outerEdges().maxOf { start -> energize(grid, start) }
    }

    private data class Beam(val pos: Coordinate, val direction: Direction) {
        override fun toString() = "$pos $direction"
    }

    private fun energize(grid: CharGrid, start: Beam): Int {
        val visited = mutableSetOf<Beam>()
        fun recurse(beam: Beam) {
            if (beam in visited) return
            visited += beam
            val next = beam.step(grid)
            next.forEach { recurse(it) }
        }
        recurse(start)
        return (visited - start).map { it.pos }.toSet().size
    }

    private fun Beam.step(grid: CharGrid): List<Beam> {
        val nextPos = pos + direction

        if (nextPos !in grid) return emptyList()

        return when (grid[nextPos]) {
            '.' -> listOf(copy(pos = nextPos))
            '|' -> when (direction) {
                UP, DOWN -> listOf(copy(pos = nextPos))
                LEFT, RIGHT -> listOf(copy(pos = nextPos, direction = UP), copy(pos = nextPos, direction = DOWN))
            }

            '-' -> when (direction) {
                UP, DOWN -> listOf(copy(pos = nextPos, direction = LEFT), copy(pos = nextPos, direction = RIGHT))
                LEFT, RIGHT -> listOf(copy(pos = nextPos))
            }

            '/' -> when (direction) {
                UP -> listOf(copy(pos = nextPos, direction = RIGHT))
                DOWN -> listOf(copy(pos = nextPos, direction = LEFT))
                LEFT -> listOf(copy(pos = nextPos, direction = DOWN))
                RIGHT -> listOf(copy(pos = nextPos, direction = UP))
            }

            '\\' -> when (direction) {
                UP -> listOf(copy(pos = nextPos, direction = LEFT))
                DOWN -> listOf(copy(pos = nextPos, direction = RIGHT))
                LEFT -> listOf(copy(pos = nextPos, direction = UP))
                RIGHT -> listOf(copy(pos = nextPos, direction = DOWN))
            }

            else -> error("Invalid character: ${grid[nextPos]}")
        }
    }

    private fun CharGrid.outerEdges(): List<Beam> {
        val horizontal = indices.flatMap { row ->
            listOf(Beam(Coordinate(row, -1), RIGHT), Beam(Coordinate(row, first().size), LEFT))
        }
        val vertical = first().indices.flatMap { column ->
            listOf(Beam(Coordinate(-1, column), DOWN), Beam(Coordinate(size, column), UP))
        }
        return horizontal + vertical
    }
}

