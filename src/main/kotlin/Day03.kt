object Day03 : Day(3) {
    override val expected = DayResult(4361L, 0L)

    override fun solvePart1(input: Sequence<String>): Long {
        val list = input.toList()
        val grid = list.charGrid()
        return list.flatMapIndexed { row, s ->

            val numbers = s.split(Regex("\\D")).filter { it.isNotEmpty() }
            println("row: $row numbers: $numbers")
            var index = 0
            numbers.filter { num ->
                index = s.indexOf(num, index)
                assert(index >= 0)
                val range = index..<index + num.length
                val adjacent = grid.adjacent(row, range)
                //    println("row: $row num: $num range: $range adjacent: ${adjacent.filter { it.isSymbol() }.toList()}")
                adjacent.any { it.isSymbol() }
            }.map { it.toLong() }.also {
                println("row: $row taking: $it")
            }

        }.also { println("taking $it") }.sum()
    }


    override fun solvePart2(input: Sequence<String>): Any {
        TODO("Not yet implemented")
    }


}

// too low 538335
// too high 539518

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