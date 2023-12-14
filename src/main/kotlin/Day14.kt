object Day14 : Day(14) {
    override val expected = DayResult(136, 109596, 64, 96105)
    override fun solvePart1(input: Sequence<String>) = input.toGrid().rotateAnticlockwise().tiltGridLeft().weight()

    override fun solvePart2(input: Sequence<String>): Int {
        var grid = input.toGrid().rotateAnticlockwise()

        val seen = mutableMapOf<CharGrid, Int>()
        while (grid !in seen) {
            seen[grid] = seen.size
            repeat(4) {
                grid = grid.tiltGridLeft().rotateClockwise()
            }
        }

        val iterations = 1000000000L
        val preCycle = seen[grid]!!
        val cycleLength = seen.size - preCycle
        val resultIteration = ((iterations - preCycle) % cycleLength + preCycle).toInt()
        val resultGrid = seen.entries.single { (_, index) -> index == resultIteration }.key
        return resultGrid.weight()
    }

    private fun CharGrid.weight() = sumOf {
        it.mapIndexed { index, c ->
            if (c == 'O') it.size - index else 0
        }.sum()
    }

    private fun CharGrid.tiltGridLeft() = map { it.tiltLineLeft() }
    private fun List<Char>.tiltLineLeft() = joinToString("").split("#").joinToString("#") {
        it.toList().sortedDescending().joinToString("")
    }.toList()



}
