object Day14 : Day(14) {
    override val expected = DayResult(136, "TODO", "TODO", "TODO")
    override fun solvePart1(input: Sequence<String>): Any {
        return input.map { it.toList() }.toList().transposed().map {
            tilt(it)
        }.sumOf {
            weight(it)
        }
    }

    private fun weight(it: List<Char>) = it.mapIndexed { index, c ->
        if (c == 'O') it.size - index else 0
    }.sum()

    private fun tilt(line: List<Char>): List<Char> {
        val joinToString = line.joinToString("")
//        println(joinToString)
        val split = joinToString.split("#")
//        println(split)
        return split.joinToString("#") {
            it.toList().sortedDescending().joinToString("")
        }.toList()
    }

    override fun solvePart2(input: Sequence<String>): Any {
        return 0
    }
}

private fun <E> List<List<E>>.transposed(): List<List<E>> {
    val result = mutableListOf<List<E>>()
    for (i in this.indices) {
        val row = mutableListOf<E>()
        for (j in this.indices) {
            row.add(this[j][i])
        }
        result.add(row)
    }
    return result
}
