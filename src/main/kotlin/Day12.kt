object Day12 : Day(12) {
    override val expected = DayResult(21L, 8270L, 525152L, 204640299929836L)
    override fun solvePart1(input: Sequence<String>) = input.sumOf { line ->
        val (springs, lengthString) = line.split(" ")
        val groups = lengthString.split(",").map { it.toInt() }
        findArrangements(springs, groups)
    }

    override fun solvePart2(input: Sequence<String>) = input.sumOf { line ->
        val (springs, lengthString) = line.split(" ")
        val unfoldedSprings = "$springs?".repeat(5).dropLast(1)
        val groups = (lengthString.split(",").map { it.toInt() }.repeat(5))
        findArrangements(unfoldedSprings, groups)
    }

    private data class Args(val groups: List<Int>, val index: Int = 0, val running: Int = 0)

    private fun findArrangements(springs: String, groups: List<Int>) =
        CachedRecursion<Args, Long> { findArrangements, (groups, i, running) ->
            val nextSprings = i + 1
            fun operationalCase() = findArrangements(Args(groups, nextSprings, running + 1))

            fun damagedCase() = when (running) {
                0 -> findArrangements(Args(groups, nextSprings, 0))
                groups.firstOrNull() -> findArrangements(Args(groups.drop(1), nextSprings, 0))
                else -> 0
            }

            when (springs.getOrNull(i)) {
                null -> when {
                    groups.isEmpty() && running == 0 -> 1
                    groups.singleOrNull() == running -> 1
                    else -> 0
                }

                '.' -> damagedCase()
                '#' -> operationalCase()
                '?' -> damagedCase() + operationalCase()

                else -> error("Invalid char ${springs.getOrNull(i)}")
            }
        }(Args(groups))
}
