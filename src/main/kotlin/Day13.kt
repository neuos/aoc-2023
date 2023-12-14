object Day13 : Day(13) {
    override val expected = DayResult(405, 27742, 400, 32728)
    override fun solvePart1(input: Sequence<String>) = sumSymmetries(input, false)
    override fun solvePart2(input: Sequence<String>) = sumSymmetries(input, true)

    private fun sumSymmetries(input: Sequence<String>, smudge: Boolean) =
        input.toList().chunkAt { it.isBlank() }.sumOf { valley ->
            val grid = valley.map { it.toList() }.toList()
            val lines = grid.map { it.toNumber() }
            val lineSym = lines.findSymmetry(smudge)
            if (lineSym != null) {
                lineSym * 100
            } else {
                val columns = (0..<grid[0].size).map { i -> grid.map { it[i] }.toNumber() }
                val columnSym = columns.findSymmetry(smudge)
                columnSym ?: 0
            }
        }


    private fun List<Long>.findSymmetry(smudge: Boolean): Int? {
        val leftCandidates = indices.drop(1).filter {
            this[it] == first() || (smudge && oneBitDiff(this[it], first()))
        }

        leftCandidates.firstNotNullOfOrNull { right ->
            checkSymmetry(0, right, smudge)
        }?.let { return it }

        val rightCandidates = indices.toList().dropLast(1).filter {
            this[it] == last() || (smudge && oneBitDiff(this[it], last()))
        }

        rightCandidates.firstNotNullOfOrNull { left ->
            checkSymmetry(left, size - 1, smudge)
        }?.let { return it }

        return null
    }

    private fun List<Long>.checkSymmetry(left: Int, right: Int, smudge: Boolean): Int? {
        var smudgeAvailable = smudge
        var l = left
        var r = right
        while (l < r) {
            if (this[l] != this[r]) {
                if (!smudgeAvailable) return null
                else if (!oneBitDiff(this[l], this[r])) return null
                else {
                    smudgeAvailable = false
                }
            }
            l++
            r--
        }
        return when {
            l == r -> null
            smudgeAvailable -> null
            else -> l
        }
    }


    private fun oneBitDiff(a: Long, b: Long): Boolean {
        fun oneBitSet(n: Long) = n != 0L && (n and n - 1) == 0L
        val diff = a xor b
        return oneBitSet(diff)
    }

    private fun List<Char>.toNumber(): Long {
        return mapIndexed { index, c ->
            if (c == '#') 1L shl index else 0L
        }.reduce { acc, l ->
            acc or l
        }
    }
}

