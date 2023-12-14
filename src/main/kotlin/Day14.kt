object Day14 : Day(14) {
    override val expected = DayResult(136, 109596, 64, "TODO")
    override fun solvePart1(input: Sequence<String>) = input.map { it.toList() }.toList().rotateLeft().map {
        tilt(it)
    }.sumOf {
        weight(it)
    }

    override fun solvePart2(input: Sequence<String>): Any {
        var grid = input.map { it.toList() }.toList().rotateLeft()

        (0..<(4*1000000000L)).forEach { _ ->
            grid = grid.map {
                tilt(it)
            }.rotateRight()
        }

        return grid.sumOf {
            weight(it)
        }
    }

    private fun weight(it: List<Char>) = it.mapIndexed { index, c ->
        if (c == 'O') it.size - index else 0
    }.sum()

    private fun tilt(line: List<Char>): List<Char> {
        return line.joinToString("").split("#").joinToString("#") {
            it.toList().sortedDescending().joinToString("")
        }.toList()
    }


    private fun <E> List<List<E>>.rotateLeft(): List<List<E>> {
        val result = mutableListOf<List<E>>()
        for (i in this.indices) {
            val row = mutableListOf<E>()
            for (j in this.indices) {
                row.add(this[j][i])
            }
            result.add(row)
        }
        return result.reversed()
    }


    private fun <E> List<List<E>>.rotateRight(): List<List<E>> {
        val result = mutableListOf<List<E>>()
        for (i in this.indices) {
            val row = mutableListOf<E>()
            for (j in this.indices) {
                row.add(this[j][i])
            }
            result.add(row.reversed())
        }
        return result
    }
}

