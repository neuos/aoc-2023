import Day16.Direction.*
import util.*

object Day16 : Day(16) {
    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    data class Beam(val pos: Coordinate, val direction: Direction){
        override fun toString() = "$pos $direction"
    }

    override val expected = DayResult(46, "TODO", "TODO", "TODO")
    override fun solvePart1(input: Sequence<String>): Any {
        val grid: CharGrid = input.filter { it.isNotEmpty() }.toGrid()

        val energized = energize(grid)
        return energized.size
    }

    private fun energize(grid: CharGrid): Set<Coordinate> {
        val start = Beam(Coordinate(0, -1), RIGHT)
        val visited = mutableSetOf<Beam>()
        fun recurse(beam: Beam) {
            println(beam)
            if (beam in visited) return
            visited += beam
            val next = beam.step(grid)
            next.forEach { recurse(it) }
        }
        recurse(start)
        return (visited-start).map { it.pos }.toSet()
    }

    override fun solvePart2(input: Sequence<String>): Any {
        return 0
    }

    private fun Beam.step(grid: CharGrid): List<Beam> {
        val nextPos = when (direction) {
            UP -> pos.up()
            DOWN -> pos.down()
            LEFT -> pos.left()
            RIGHT -> pos.right()
        }

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
}

