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
    override fun solvePart1(input: Sequence<String>): Int {
        val grid: CharGrid = input.filter { it.isNotEmpty() }.toGrid()
        val start = Beam(Coordinate(0, -1), RIGHT)
        return energize(grid, start)
    }

    override fun solvePart2(input: Sequence<String>): Int {
        val grid: CharGrid = input.filter { it.isNotEmpty() }.toGrid()
        val startsLeft = grid.indices.map { i -> Beam(Coordinate(i, -1), RIGHT) }
        val startsRight = grid.indices.map { i -> Beam(Coordinate(i, grid[i].size), LEFT) }
        val startsTop = grid[0].indices.map { i -> Beam(Coordinate(-1, i), DOWN) }
        val startsBottom = grid[0].indices.map { i -> Beam(Coordinate(grid.size, i), UP) }
        val starts = startsLeft + startsRight + startsTop + startsBottom
        return starts.maxOf { energize(grid, it) }
    }



    private fun energize(grid: CharGrid, start: Beam): Int {
        val visited = mutableSetOf<Beam>()
        fun recurse(beam: Beam) {
    //            println(beam)
            if (beam in visited) return
            visited += beam
            val next = beam.step(grid)
            next.forEach { recurse(it) }
        }
        recurse(start)
        return (visited - start).map { it.pos }.toSet().size

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

