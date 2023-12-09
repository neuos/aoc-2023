object Day09 : Day(9) {
    override val expected = DayResult(114, 2038472161, 2, "TODO")
    override fun solvePart1(input: Sequence<String>): Any {
        return input.map {
            it.split(" ").map { it.toInt() }
        }.map {
            val diffs = mutableListOf<List<Int>>(it)
            do {
                val diff = diffs.last().windowed(2).map { it[1] - it[0] }
                println("diff: $diff")
                diffs.add(diff)
            } while (diff.any { it != 0 })
            println(diffs)

            var next = 0
            diffs.indices.reversed().forEach { i ->
                val diff = diffs[i]
                next += diff.last()
            }
            println(next)
            next
        }.sum()
    }

    override fun solvePart2(input: Sequence<String>): Any {
        return input.map {
            it.split(" ").map { it.toInt() }
        }.map {
            val diffs = mutableListOf<List<Int>>(it)
            do {
                val diff = diffs.last().windowed(2).map { it[1] - it[0] }
                println("diff: $diff")
                diffs.add(diff)
            } while (diff.any { it != 0 })
            println(diffs)

            var previous = 0
            diffs.indices.reversed().forEach { i ->
                val diff = diffs[i]
                previous = diff.first() - previous
            }
            println(previous)
            previous
        }.sum()
    }
}