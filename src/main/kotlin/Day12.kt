object Day12 : Day(12) {
    override val expected = DayResult(21, 8270, "TODO", "TODO")
    override fun solvePart1(input: Sequence<String>) = input.sumOf { line ->
        line.findArrangements()
    }

    override fun solvePart2(input: Sequence<String>) = input.sumOf { line ->
        val (springs, lengthString) = line.split(" ")
        val unfolded = "$springs?".repeat(5).dropLast(1) + " " + "$lengthString,".repeat(5).dropLast(1)
        unfolded.findArrangements().toLong()
    }
}

private fun String.findArrangements(): Int {
    val (springs, lengthString) = this.split(" ")
    val requiredLengths = lengthString.split(",").map { it.toInt() }

    var arrangements = listOf("".toCharArray())
    springs.forEach { spring ->
        arrangements = when (spring) {
            '?' -> arrangements.flatMap { listOf(it + '.', it + '#') }
            else -> arrangements.map { it + spring }
        }
    }
    val valid = arrangements.filter { contiguousLengths(it) == requiredLengths }
    val correct = valid.count()
    println("$this: $correct")
    return correct
}

private fun contiguousLengths(chars: CharArray): List<Int> {
    val spring = '#'
    var count = 0
    return buildList {
        chars.forEach {
            if (it == spring) {
                count++
            } else {
                if (count != 0) add(count)
                count = 0
            }
        }
        if (count != 0) add(count)
    }
}

fun main() {
    println(Day12.part1())
}