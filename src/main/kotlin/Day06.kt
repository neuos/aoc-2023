object Day06 : Day(6) {
    override val expected = DayResult(288, 316800, 71503, 45647654L)
    override fun solvePart1(input: Sequence<String>): Any {
        val list = input.toList()
        val times = list.first().split(Regex("\\D")).filter { it.isNotEmpty() }.map { it.toLong() }
        val distances = list.last().split(Regex("\\D")).filter { it.isNotEmpty() }.map { it.toLong() }

        return times.zip(distances).map { (time, distance) ->
            waysToWin(time, distance)
        }.reduce { a, b -> a * b }
    }

    override fun solvePart2(input: Sequence<String>): Int {
        val list = input.toList()
        val time = list.first().filter { it.isDigit() }.toLong()
        val distance = list.last().filter { it.isDigit() }.toLong()
        return waysToWin(time, distance)
    }

    private fun waysToWin(time: Long, distance: Long) = (1..<time).map { v ->
        v * (time - v)
    }.count { it > distance }
}

fun main() {
    print(Day06.part2())
}