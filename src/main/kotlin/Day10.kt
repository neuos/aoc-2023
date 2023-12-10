import kotlin.math.ceil

object Day10 : Day(10) {
    override val expected = DayResult(4, 6870, "TODO", "TODO")
    override fun solvePart1(input: Sequence<String>): Any {
        val grid = input.map { line -> line.toCharArray().toList() }.toList()

        val start = grid.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { column, c ->
                if (c == 'S') Coordinate(
                    row, column
                ) else null
            }
        }.first()

        val (first, last) = grid.adjacent(start).filter { c -> grid.areConnected(start, c) }


        val loop = mutableListOf<Coordinate>()
        var current = first
        var previous = start
        while (current != last) {
            println("current $current")
            println("previous $previous")
            loop += current
            val next = grid.next(current, previous)
            previous = current
            current = next
        }
        loop += current
        println("loop $loop")
        return ceil(loop.size / 2.0).toInt()
    }


    /**
     *  | is a vertical pipe connecting north and south.
     *  - is a horizontal pipe connecting east and west.
     *  L is a 90-degree bend connecting north and east.
     *  J is a 90-degree bend connecting north and west.
     *  7 is a 90-degree bend connecting south and west.
     *  F is a 90-degree bend connecting south and east.
     */
    private fun Grid.areConnected(a: Coordinate, b: Coordinate): Boolean {
        val connectsUp = setOf('|', 'L', 'J', 'S')
        val connectsDown = setOf('|', '7', 'F', 'S')
        val connectsLeft = setOf('-', 'L', '7', 'S')
        val connectsRight = setOf('-', 'J', 'F', 'S')

        return when {
            a.isAboveOf(b) -> this[a] in connectsDown && this[b] in connectsUp
            a.isBelowOf(b) -> this[a] in connectsUp && this[b] in connectsDown
            a.isLeftOf(b) -> this[a] in connectsRight && this[b] in connectsLeft
            a.isRightOf(b) -> this[a] in connectsLeft && this[b] in connectsRight
            else -> false.also { println("not adjacent $a $b") }
        }
    }

    override fun solvePart2(input: Sequence<String>): Any {
        return 0
    }

    data class Coordinate(val x: Int, val y: Int) {
        override fun toString() = "($x, $y)"
        fun isAboveOf(other: Coordinate) = equals(other.up())
        fun isBelowOf(other: Coordinate) = equals(other.down())
        fun isLeftOf(other: Coordinate) = equals(other.left())
        fun isRightOf(other: Coordinate) = equals(other.right())

        fun up() = copy(x = x - 1)
        fun down() = copy(x = x + 1)
        fun left() = copy(y = y - 1)
        fun right() = copy(y = y + 1)
    }

    private operator fun Grid.get(other: Coordinate) = get(other.x)[other.y]

    private fun Grid.adjacent(coordinate: Coordinate): List<Coordinate> {
        val (x, y) = coordinate
        return listOf(
            Coordinate(x - 1, y),
            Coordinate(x, y - 1),
            Coordinate(x, y + 1),
            Coordinate(x + 1, y),
        ).filter { (x, y) -> x >= 0 && y >= 0 && x < size && y < get(x).size }
    }

    private fun Grid.next(current: Coordinate, previous: Coordinate): Coordinate {
        val startChar = this[current]
        println("finding next from '$startChar' at $current, previous: $previous")
        val next = when (startChar) {
            '|' -> if (current.isAboveOf(previous)) current.up() else if (current.isBelowOf(previous)) current.down() else error(
                "not a pipe $startChar at $current"
            )

            '-' -> if (current.isLeftOf(previous)) current.left() else if (current.isRightOf(previous)) current.right() else error(
                "not a pipe $startChar at $current"
            )

            'L' -> if (current.isBelowOf(previous)) current.right() else if (current.isLeftOf(previous)) current.up() else error(
                "not a pipe $startChar at $current"
            )

            'J' -> if (current.isBelowOf(previous)) current.left() else if (current.isRightOf(previous)) current.up() else error(
                "not a pipe $startChar at $current"
            )

            '7' -> if (current.isAboveOf(previous)) current.left() else if (current.isRightOf(previous)) current.down() else error(
                "not a pipe $startChar at $current"
            )

            'F' -> if (current.isAboveOf(previous)) current.right() else if (current.isLeftOf(previous)) current.down() else error(
                "not a pipe $startChar at $current"
            )

            else -> error("not a pipe $startChar at $current")
        }
        println("moving from $current = '${this[current]}' to $next")
        return next
    }

}
private typealias Grid = List<List<Char>>


fun main() {
    Day10.part1Example()
}