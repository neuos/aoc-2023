object Day15 : Day(15) {
    override val expected = DayResult(1320, 504036, "TODO", "TODO")
    override fun solvePart1(input: Sequence<String>): Any {
        return input.first().split(",").sumOf { it.hashAlgorithm() }
    }

    override fun solvePart2(input: Sequence<String>): Any {
        return 0
    }
}

private fun String.hashAlgorithm(): Long {
    var hash = 0L
    for (i in indices) {
        hash = ((hash + get(i).code) * 17).rem(256)
    }
    return hash
}
