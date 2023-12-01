data object Day0 : Day(0) {
    override fun solvePart1(input: List<String>):Int {
        val filtered = input.filter { it.isNotBlank() }
        return filtered.size
    }
    override fun solvePart2(input: List<String>): Int {
        return input.filter { it.isNotBlank() }.sumOf { it.toInt() }
    }
}