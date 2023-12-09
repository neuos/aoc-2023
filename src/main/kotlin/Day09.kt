object Day09 : Day(9) {
    override val expected = DayResult(114, 2038472161, 2, 1091)
    override fun solvePart1(input: Sequence<String>): Any {
        return input.toIntLists().sumOf {
            it.findDiffs().extrapolateNext()
        }
    }

    override fun solvePart2(input: Sequence<String>): Any {
        return input.toIntLists().sumOf {
            it.findDiffs().extrapolatePrevious()
        }
    }

    private fun List<Int>.findDiffs() = generateSequence(this) { list ->
        list.windowed(2).map { (a, b) -> b - a }.takeIf { diff -> diff.any { it != 0 } }
    }.toList()

    private fun List<List<Int>>.extrapolateNext() = this.sumOf { it.last() }

    private fun List<List<Int>>.extrapolatePrevious() =
        this.reversed().fold(0) { previous, list -> list.first() - previous }

    private fun Sequence<String>.toIntLists() = map { line -> line.split(' ').map { it.toInt() } }
}
