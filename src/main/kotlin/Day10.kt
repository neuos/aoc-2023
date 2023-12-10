import java.io.FileWriter
import kotlin.math.ceil

object Day10 : Day(10) {
    override val expected = DayResult(4, 6870, 10, "TODO")
    override fun solvePart1(input: Sequence<String>): Any {
        val grid = input.toGrid()
        val loop = findLoop(grid)
        println("loop $loop")
        return ceil(loop.size / 2.0).toInt()
    }


    // 8301 too high
    // not 302
    override fun solvePart2(input: Sequence<String>): Any {
        val grid = input.toGrid()
        val loop = findLoop(grid)
        val start = loop.first()
        val startPipe = getStartPipe(loop)

        println("loop $loop")
        val loopGrid = grid.indices.map { row ->
            grid[row].indices.map { column ->
                when (val coordinate = Coordinate(row, column)) {
                    start -> startPipe
                    in loop -> grid[coordinate]
                    else -> ' '
                }
            }
        }
        loopGrid.forEach { println(it.joinToString("")) }
        loopGrid.toPicture()

        return grid.coordinates().count { isEnclosed(loopGrid, loop, it) }
    }

    private fun getStartPipe(loop: List<Coordinate>): Char {
        val last = loop.last()
        val first = loop[1]
        val start = loop.first()
        return if (first.isAboveOf(start) || last.isAboveOf(start)) {
            if (first.isBelowOf(start) || last.isBelowOf(start)) {
                '|'
            } else if (first.isLeftOf(start) || last.isLeftOf(start)) {
                'J'
            } else if (first.isRightOf(start) || last.isRightOf(start)) {
                'L'
            } else error("unknown start")
        } else if (first.isBelowOf(start) || last.isBelowOf(start)) {
            if (first.isLeftOf(start) || last.isLeftOf(start)) {
                '7'
            } else if (first.isRightOf(start) || last.isRightOf(start)) {
                'F'
            } else error("unknown start")
        } else if (first.isLeftOf(start) || last.isLeftOf(start)) {
            if (first.isRightOf(start) || last.isRightOf(start)) {
                '-'
            } else error("unknown start")
        } else if (first.isRightOf(start) || last.isRightOf(start)) {
            error("unknown start")
        } else error("unknown start")
    }

    private fun isEnclosed(grid: Grid, loop: List<Coordinate>, coordinate: Coordinate): Boolean {
        if (coordinate in loop) return false

        val leftRange = (0..coordinate.y).map { Coordinate(coordinate.x, it) }
        val leftChars = leftRange.map { grid[it] }
        val straight = leftChars.count { it == '|' }

        var count = 0
        var corner = ' '
        leftChars.forEach { c ->
            if (c == '|') count++
            else if (c in setOf('L', '7', 'F', 'J')) {
                if (corner == 'L' && c == '7') count++
                else if (corner == 'F' && c == 'J') count++
                corner = c
            }
        }
        val enclosed = count % 2 == 1
        if (enclosed) println("enclosed $coordinate count $count")
        return enclosed


//        val pipesLeft = leftRange.count { Coordinate(coordinate.x, it) in loop }

        val pipesRight = (coordinate.y..<grid[coordinate.x].size).count { Coordinate(coordinate.x, it) in loop }

        val pipesAbove = (0..coordinate.x).count { Coordinate(it, coordinate.y) in loop }

        val pipesBelow = (coordinate.x..<grid.size).count { Coordinate(it, coordinate.y) in loop }

//        val enclosed = pipesLeft % 2 == 1 && pipesRight % 2 == 1 && pipesAbove % 2 == 1 && pipesBelow % 2 == 1
//        if (enclosed) println("enclosed $coordinate")
//        return enclosed
        return false
    }

    private fun Sequence<String>.toGrid(): Grid = map { line -> line.toCharArray().toList() }.toList()


    private fun findLoop(grid: Grid): MutableList<Coordinate> {

        val start = grid.flatMapIndexed { row, line ->
            line.mapIndexedNotNull { column, c ->
                if (c == 'S') Coordinate(
                    row, column
                ) else null
            }
        }.first()

        val connected = grid.adjacent(start).filter { c -> grid.areConnected(start, c) }
        println("connected $connected")
        val (first, last) = connected
        println("first $first")
        println("last $last")

        val loop = mutableListOf(start)
        var current = first
        var previous = start
        while (current != start) {
//            println("current $current")
//            println("previous $previous")
            loop += current
            val next = grid.next(current, previous)
            previous = current
            current = next
        }
        return loop
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
        val connectsLeft = setOf('-', 'J', '7', 'S')
        val connectsRight = setOf('-', 'L', 'F', 'S')

        return when {
            a.isAboveOf(b) -> this[a] in connectsDown && this[b] in connectsUp
            a.isBelowOf(b) -> this[a] in connectsUp && this[b] in connectsDown
            a.isLeftOf(b) -> this[a] in connectsRight && this[b] in connectsLeft
            a.isRightOf(b) -> this[a] in connectsLeft && this[b] in connectsRight
            else -> false.also { println("not adjacent $a $b") }
        }
    }

    private fun Grid.coordinates(): Sequence<Coordinate> = sequence {
        for (x in indices) {
            for (y in get(x).indices) {
                yield(Coordinate(x, y))
            }
        }
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

    private fun Grid.adjacent(coordinate: Coordinate) = listOf(
        coordinate.left(), coordinate.right(), coordinate.up(), coordinate.down()
    ).filter { contains(it) }

    private fun Grid.contains(coordinate: Coordinate) =
        coordinate.x in indices && coordinate.y in get(coordinate.x).indices

    private fun Grid.next(current: Coordinate, previous: Coordinate): Coordinate {
        val startChar = this[current]
//        println("finding next from '$startChar' at $current, previous: $previous")
        val error = {
            error(
                "not a pipe $startChar at $current, previous: $previous '${this[previous]}'"
            )
        }
        val next = when (startChar) {
            '|' -> if (current.isAboveOf(previous)) current.up() else if (current.isBelowOf(previous)) current.down() else error()
            '-' -> if (current.isLeftOf(previous)) current.left() else if (current.isRightOf(previous)) current.right() else error()
            'L' -> if (current.isBelowOf(previous)) current.right() else if (current.isLeftOf(previous)) current.up() else error()
            'J' -> if (current.isBelowOf(previous)) current.left() else if (current.isRightOf(previous)) current.up() else error()
            '7' -> if (current.isAboveOf(previous)) current.left() else if (current.isRightOf(previous)) current.down() else error()
            'F' -> if (current.isAboveOf(previous)) current.right() else if (current.isLeftOf(previous)) current.down() else error()

            else -> error()
        }
//        println("moving from $current = '${this[current]}' to $next")
        return next
    }

}

private fun Grid.toPicture() {
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

    createPBM(imageData)
}

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

private typealias Grid = List<List<Char>>


fun main() {
//    println(Day10.part2Example())
    println(Day10.part2())
}