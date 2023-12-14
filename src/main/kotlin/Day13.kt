object Day13 : Day(13) {
    override val expected = DayResult(405, "TODO", "TODO", "TODO")

    override fun solvePart1(input: Sequence<String>): Int {
        val chunks = input.toList().chunkAt { it.isBlank() }
        return chunks.map { valley ->
            val grid = valley.map { it.toList() }.toList()
            val lines = grid.map { it.toNumber() }
            val columns = (0..<grid[0].size).map { i -> grid.map { it[i] }.toNumber() }
            val pair = findSymmetry(lines) to findSymmetry(columns)
            if (pair.first == null && pair.second == null) {
                println("no symmetry found in")
                valley.forEach { println(it) }
                println("lines: $lines")
                println("columns: $columns")
                error("no symmetry found")
            }
            pair
        }.sumOf { (lineSym, colSym) ->
            println("lineSym: $lineSym colSym: $colSym")
            (lineSym ?: 0) * 100 + (colSym ?: 0)
        }
    }

    private fun findSymmetry(lines: List<Long>): Int? {
        val checkFromLeft = lines.filter { it == lines.first() }.size > 1
        val checkFromRight = lines.filter { it == lines.last() }.size > 1

        if (checkFromLeft) {
            var left = 0
            val candidates = lines.indices.filter { lines[it] == lines.first() }.drop(1)
            candidates.forEach {
                var right = it
                while (left < right && lines[left] == lines[right]) {
                    left++
                    right--
                }
                if (left > right) {
                    return left
                }
            }
        }

        if (checkFromRight) {
            val candidates = lines.indices.filter { lines[it] == lines.last() }.dropLast(1)
            candidates.forEach {
                var left = it
                var right = lines.size - 1
                while (left < right && lines[left] == lines[right]) {
                    left++
                    right--
                }
                if (left > right) {
                    return left
                }
            }

        }

        return null
    }

    override fun solvePart2(input: Sequence<String>) = 0

}

private fun List<Char>.toNumber(): Long {
    return mapIndexed { index, c ->
        if (c == '#') 1L shl index else 0L
    }.reduce { acc, l ->
        acc or l
    }
}

fun main() {
    println(Day13.part1Example())
    println(Day13.part1())
}