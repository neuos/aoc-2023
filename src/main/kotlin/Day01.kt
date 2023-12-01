object Day01 : Day(1) {
    override val expected = DayResult(142, 54990, 281, 54473)
    override fun solvePart1(input: Sequence<String>) = solve(input) { line ->
        line.filter { it.isDigit() }.toList()
    }

    override fun solvePart2(input: Sequence<String>) = solve(input) { line ->
        line.mapIndexedNotNull { index, c ->
            when {
                c.isDigit() -> c
                // check if any of the digitStrings starts at current index
                else -> digitStrings.find { line.startsWith(it, index) }?.let {
                    val number = digitStrings.indexOf(it) + 1
                    number.digitToChar()
                }
            }
        }.toList()
    }

    private fun solve(input: Sequence<String>, extractDigits: (String) -> List<Char>) =
        input.map(extractDigits).filter { it.isNotEmpty() }.map {
            "${it.first()}${it.last()}"
        }.sumOf { it.toInt() }

    private val digitStrings = arrayOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
}