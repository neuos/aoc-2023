object Day06 : Day(6) {
    override val expected = DayResult(288, 0, "TODO", "TODO")
    override fun solvePart1(input: Sequence<String>): Any {
        val list = input.toList()
        val times = list.first().split(Regex("\\D")).filter { it.isNotEmpty() }.map { it.toInt() }
        val distances = list.last().split(Regex("\\D")).filter { it.isNotEmpty() }.map { it.toInt() }

        return times.zip(distances).map { (time, distance) ->
            println("$time $distance")
            (1..<time).map {v->
                v*(time-v)
            }.filter { it > distance }.count()
        }.reduce { a, b ->a*b  }

        return 0
    }

    override fun solvePart2(input: Sequence<String>): Any {
        return 0
    }
}