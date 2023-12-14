import util.*
import java.io.FileWriter
import kotlin.math.ceil

object Day10 : Day(10) {
    override val expected = DayResult(4, 6870, 10, 287)
    override fun solvePart1(input: Sequence<String>): Int {
        val loop = input.toGrid().findLoop()
        return ceil(loop.size / 2.0).toInt()
    }

    override fun solvePart2(input: Sequence<String>): Int {
        val grid = input.toGrid()
        val loop = grid.findLoop()
        val start = loop.first()

        val loopGrid = grid.indices.map { row ->
            grid[row].indices.map { column ->
                when (val coordinate = Coordinate(row, column)) {
                    start -> getStartPipe(loop)
                    in loop -> grid[coordinate]
                    else -> ' '
                }
            }
        }
        return loopGrid.findEnclosed(loop)
    }

    private fun CharGrid.findLoop(): List<Coordinate> {
        val start = coordinates().first { this[it] == 'S' }
        var current = adjacent(start).first { c -> areConnected(start, c) }
        return buildList {
            add(start)
            while (current != start) {
                val next = next(current, last())
                add(current)
                current = next
            }
        }
    }

    private fun CharGrid.next(current: Coordinate, previous: Coordinate) = when (this[current]) {
        '|' -> if (current.isAboveOf(previous)) current.up() else current.down()
        '-' -> if (current.isLeftOf(previous)) current.left() else current.right()
        'L' -> if (current.isBelowOf(previous)) current.right() else current.up()
        'J' -> if (current.isBelowOf(previous)) current.left() else current.up()
        '7' -> if (current.isAboveOf(previous)) current.left() else current.down()
        'F' -> if (current.isAboveOf(previous)) current.right() else current.down()
        else -> error("not a pipe ${this[current]} at $current, previous: $previous '${this[previous]}'")
    }


    private fun CharGrid.findEnclosed(loop: List<Coordinate>): Int {
        val isInside = map { row ->
            var inside = false
            var corner: Char? = null
            row.map { c ->
                if (c == '|') inside = !inside
                else if (c in setOf('L', '7', 'F', 'J')) {
                    if (corner == 'L' && c == '7') inside = !inside
                    else if (corner == 'F' && c == 'J') inside = !inside
                    corner = c
                }
                inside
            }
        }
        val loopSet = loop.toSet()
        return coordinates().filter { it !in loopSet }.count { isInside[it] }
    }

    private fun getStartPipe(loop: List<Coordinate>): Char {
        val start = loop.first()
        val (a, b) = listOf(loop[1], loop.last()).sorted()
        when {
            a.isAboveOf(start) -> when {
                b.isBelowOf(start) -> return '|'
                b.isLeftOf(start) -> return 'J'
                b.isRightOf(start) -> return 'L'
            }

            b.isBelowOf(start) -> when {
                a.isLeftOf(start) -> return '7'
                a.isRightOf(start) -> return 'F'
            }

            a.isLeftOf(start) -> when {
                b.isRightOf(start) -> return '-'
            }
        }
        error("Case not covered for $a - $start - $b")
    }


    private fun CharGrid.areConnected(start: Coordinate, next: Coordinate) = when {
        start.isAboveOf(next) -> this[next] in setOf('|', 'L', 'J', 'S')
        start.isBelowOf(next) -> this[next] in setOf('|', '7', 'F', 'S')
        start.isLeftOf(next) -> this[next] in setOf('-', 'J', '7', 'S')
        start.isRightOf(next) -> this[next] in setOf('-', 'L', 'F', 'S')
        else -> false
    }




}

private fun CharGrid.toPicture() {
    val pixels = mapOf(
        ' ' to listOf(
            listOf(0, 0, 0), listOf(0, 0, 0), listOf(0, 0, 0)
        ),
        '|' to listOf(
            listOf(0, 1, 0), listOf(0, 1, 0), listOf(0, 1, 0)
        ),
        '-' to listOf(
            listOf(0, 0, 0), listOf(1, 1, 1), listOf(0, 0, 0)
        ),
        'L' to listOf(
            listOf(0, 1, 0), listOf(0, 1, 1), listOf(0, 0, 0)
        ),
        'J' to listOf(
            listOf(0, 1, 0), listOf(1, 1, 0), listOf(0, 0, 0)
        ),
        '7' to listOf(
            listOf(0, 0, 0), listOf(1, 1, 0), listOf(0, 1, 0)
        ),
        'F' to listOf(
            listOf(0, 0, 0), listOf(0, 1, 1), listOf(0, 1, 0)
        ),
        'S' to listOf(
            listOf(1, 1, 1),
            listOf(1, 1, 1),
            listOf(1, 1, 1),
        ),
    )
    val fallback = listOf(
        listOf(1, 0, 1),
        listOf(0, 1, 0),
        listOf(1, 0, 1),
    )


    val imageData = map { row -> row.map { c -> pixels[c] ?: fallback } }.flatMap { row: List<List<List<Int>>> ->
        row[0].indices.map { index ->
            row.map { pixel -> pixel[index] }.flatten()
        }
    }.map { it.toTypedArray() }.toTypedArray()

    fun createPBM(imageData: Array<Array<Int>>) {

        val height = imageData.size
        val width = imageData[0].size

        val content = buildString {
            appendLine("P1")
            appendLine("$width $height")
            imageData.forEach { row ->
                appendLine(row.joinToString(" "))
            }
        }

        val outputFile = FileWriter("output.pbm")
        outputFile.write(content)
        outputFile.close()

    }
    createPBM(imageData)
}


