object Day06 : Day(6) {
    override val expected = DayResult(288, 316800, 71503, 45647654)
    override fun solvePart1(input: Sequence<String>): Any {
        val (timeLine, distanceLine) = input.toList()
        val times = timeLine.split(Regex("\\D")).filter { it.isNotEmpty() }.map { it.toLong() }
        val distances = distanceLine.split(Regex("\\D")).filter { it.isNotEmpty() }.map { it.toLong() }

        return times.zip(distances).map { (time, distance) ->
            waysToWin(time, distance)
        }.reduce(Int::times)
    }

    override fun solvePart2(input: Sequence<String>): Int {
        val (timeLine, distanceLine) = input.toList()
        val time = timeLine.filter { it.isDigit() }.toLong()
        val distance = distanceLine.filter { it.isDigit() }.toLong()
        return waysToWin(time, distance)
    }

    private fun waysToWin(time: Long, distance: Long) = (1..<time).count { v ->
        (v * (time - v)) > distance
    }
}
