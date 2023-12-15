import util.Coordinate
import util.combine
import util.get
import util.toGrid

object Day03 : Day(3) {
    override val expected = DayResult(4361, 539433, 467835, 75847567)

    override fun solvePart1(input: Sequence<String>): Int {
        val lines = input.toList()
        val grid = lines.toGrid()
        return lines.flatMapIndexed { row, rowString ->
            rowString.findNumbers().filter { (_, range) ->
                grid.adjacent(row, range).any { it.isSymbol() }
            }.map { (number, _) -> number.toInt() }
        }.sum()
    }

    override fun solvePart2(input: Sequence<String>): Any {
        val lines = input.toList()
        val grid = lines.toGrid()
        return lines.flatMapIndexed { row, rowString ->
            rowString.findNumbers().map { (num, range) ->
                grid.adjacentIndices(row, range).filter {
                    grid[it].isGear()
                }.associateWith { _ -> listOf(num.toInt()) }
            }
        }.reduce { map1, map2 ->
            map1.combine(map2) { a: List<Int>, b: List<Int> -> a + b }
        }.values.filter { it.size == 2 }.sumOf { (a, b) -> a * b }
    }
}

private fun Char.isSymbol(): Boolean = !equals('.') and !isDigit()
private fun Char.isGear() = equals('*')

/**
 * Find all numbers in a string and return them with their start and end index
 */
private fun String.findNumbers(): List<Pair<String, IntRange>> {
    // find all numbers and their start and end index
    val numbers = split(Regex("\\D")).filter { it.isNotEmpty() }
    var index = 0
    return numbers.map { number ->
        val start = indexOf(number, index)
        assert(start >= 0)
        val end = start + number.length
        index = end
        number to start..<end
    }
}


/**
 * Find all adjacent indices of partial row
 */
private fun <E> List<List<E>>.adjacentIndices(row: Int, range: IntRange): List<Coordinate> =
    (row - 1..row + 1).flatMap { r ->
        (range.first - 1..range.last + 1).map { c ->
            Coordinate(r, c)
        }.filter { (r, c) ->
            r in indices && c in this[row].indices && !(r.toInt() == row && c in range)
        }
    }

/**
 * Find all adjacent elements of partial row
 */
private fun <E> List<List<E>>.adjacent(row: Int, columns: IntRange) = adjacentIndices(row, columns).map { this[it] }

