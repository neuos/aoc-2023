object Day03 : Day(3) {
    override val expected = DayResult(4361L, 539433L, 467835, 75847567)

    override fun solvePart1(input: Sequence<String>): Long {
        val list = input.toList()
        val grid = list.charGrid()
        return list.flatMapIndexed { row, s ->

            s.indexedNumbers()
            val numbers = s.split(Regex("\\D")).filter { it.isNotEmpty() }
            println("row: $row numbers: $numbers")
            var index = 0
            numbers.filter { num ->
                println("searching for $num in $s starting at $index")
                index = s.indexOf(num, index)
                println("found at $index")
                assert(index >= 0)
                val range = index..<index + num.length
                index += num.length

                val adjacent = grid.adjacent(row, range)
                //    println("row: $row num: $num range: $range adjacent: ${adjacent.filter { it.isSymbol() }.toList()}")

                adjacent.any { it.isSymbol() }

            }.map { it.toLong() }.also {
                println("row: $row taking: $it")
            }

        }.also { println("taking $it") }.sum()
    }


    override fun solvePart2(input: Sequence<String>): Any {
        val list = input.toList()
        val grid = list.charGrid()
        val gears = mutableMapOf<Pair<Int, Int>, List<Int>>()
        list.flatMapIndexed { row, s ->

            s.indexedNumbers()
            val numbers = s.split(Regex("\\D")).filter { it.isNotEmpty() }
            println("row: $row numbers: $numbers")
            var index = 0
            numbers.map { num ->
                index = s.indexOf(num, index)
                println("found at $index")
                assert(index >= 0)
                val range = index..<index + num.length
                index += num.length

                val numberGears: Sequence<Pair<Int, Int>> = grid.adjacentIndices(row, range).filter { (row, col) ->
                    grid[row][col].isGear()
                }
                numberGears.forEach { (row, col) ->
                    gears[row to col] = gears.getOrDefault(row to col, emptyList()) + num.toInt()
                }
            }
        }
        println(gears)
        return gears.values.filter { it.size == 2 }.map { (a,b) ->a*b  }.sum()
    }


}

private fun Char.isGear() = equals('*')


private fun String.indexedNumbers() {
    // find all numbers and their start and end index
    val numbers = split(Regex("\\D")).filter { it.isNotEmpty() }
    val numberRanges = numbers.map { number ->
        val start = indexOf(number)
        val end = start + number.length
        start..end
    }
}

// too low 538335

// too high 539518


private fun <E> List<List<E>>.adjacentIndices(row: Int, range: IntRange) = sequence {
    // left and right
    yield(row to range.first - 1)
    yield(row to range.last + 1)

    // top and bottom
    for (i in listOf(row - 1, row + 1)) {
        for (j in range.first - 1..range.last + 1) {
            yield(i to j)
        }
    }
}.filter { (row, col) ->
    row in indices && col in this[row].indices
}

private fun <E> List<List<E>>.adjacent(row: Int, columns: IntRange) = sequence<E> {
    // left and right
    this@adjacent.getOrNull(row)?.getOrNull(columns.first - 1)?.let { yield(it) }
    this@adjacent.getOrNull(row)?.getOrNull(columns.last + 1)?.let { yield(it) }

    // top and bottom
    for (i in listOf(row - 1, row + 1)) {
        for (j in columns.first - 1..columns.last + 1) {
            this@adjacent.getOrNull(i)?.getOrNull(j)?.let { yield(it) }
        }
    }
}

private fun Char.isSymbol(): Boolean = !isDigit() and !equals('.') and !isWhitespace()

private fun <E> List<List<E>>.adjacent(row: Int, col: Int) = sequence<E> {
    for (i in row - 1..row + 1) {
        for (j in col - 1..col + 1) {
            this@adjacent.getOrNull(i)?.getOrNull(j)?.let { yield(it) }
        }
    }
}

private fun Iterable<String>.charGrid() = toList().map { it.toList() }

fun main() {
    println(Day03.part1Example())
}