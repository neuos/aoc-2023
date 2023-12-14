object Day13 : Day(13) {
    override val expected = DayResult(405, "TODO", 400, "TODO")

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


    override fun solvePart2(input: Sequence<String>): Int {
        val chunks = input.toList().chunkAt { it.isBlank() }
        return chunks.map { valley ->
            val grid = valley.map { it.toList() }.toList()
            val lines = grid.map { it.toNumber() }
            val columns = (0..<grid[0].size).map { i -> grid.map { it[i] }.toNumber() }
            val colSym = findSymmetry2(columns)
            val lineSym = findSymmetry2(lines)
            if (lineSym == null && colSym == null) {
                println("no symmetry found in")
                valley.forEach { println(it) }
                println("lines: $lines")
                println("columns: $columns")
                error("no symmetry found")
            }
            if (lineSym != null && colSym != null) {
                println("both symmetry found in")
                valley.forEach { println(it) }
                println("lines: $lines")
                println("columns: $columns")
                println("lineSym: $lineSym colSym: $colSym")

                println("noSmudge Lines: "+findSymmetry(lines))
                println("noSmudge Columns: "+findSymmetry(columns))




                error("both symmetry found")
            }
            lineSym to colSym
        }.sumOf { (lineSym, colSym) ->
            println("lineSym: $lineSym colSym: $colSym")
            (lineSym ?: 0) * 100 + (colSym ?: 0)
        }
    }

    private fun findSymmetry2(lines: List<Long>): Int? {
        val leftCandidates = lines.indices.drop(1).filter {
            lines[it] == lines.first() || oneBitDiff(lines[it], lines.first())
        }
        val rightCandidates = lines.indices.toList().dropLast(1).filter {
            lines[it] == lines.last() || oneBitDiff(lines[it], lines.last())
        }

        fun f(l: Int, r: Int): Int? {
            var smudgeAvailable = true
            var (left, right) = l to r
            while (left < right) {
                if (lines[left] != lines[right]) {
                    if (!smudgeAvailable) return null

                    if (oneBitDiff(lines[left], lines[right])) {
                        smudgeAvailable = false
                    } else {
                        return null
                    }
                }

                left++
                right--
            }
            return if (left==right || smudgeAvailable) null
            else left
        }


        leftCandidates.forEach { right ->
            f(0, right)?.let { return it }
        }

        rightCandidates.forEach { left ->
            f(left, lines.size - 1)?.let { return it }
        }
        return null
    }

    fun oneBitDiff(a: Long, b: Long): Boolean {
        fun oneBitSet(n: Long) = n != 0L && (n and n - 1) == 0L
        val diff = a xor b
        return oneBitSet(diff)
    }


}

private fun List<Char>.toNumber(): Long {
    return mapIndexed { index, c ->
        if (c == '#') 1L shl index else 0L
    }.reduce { acc, l ->
        acc or l
    }
}

fun main() {
//    println(Day13.part1Example())
//    println(Day13.part1())

//    println(Day13.oneBitDiff("#.##..##.".toList().toNumber(), "#.##..##.".toList().toNumber()))
    println(Day13.part2Example())
    println(Day13.part2())
}