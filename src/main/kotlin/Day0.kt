object Day0 : Day(0) {
    override fun solvePart1(input: Sequence<String>):Int {
        val filtered = input.filter { it.isNotBlank() }
        return filtered.count()
    }
    override fun solvePart2(input: Sequence<String>): Int {
        return input.filter { it.isNotBlank() }.sumOf { it.toInt() }
    }
}