object Day12 : Day(12) {
    override val expected = DayResult(21, "TODO", "TODO", "TODO")
    override fun solvePart1(input: Sequence<String>): Any {
        return input.sumOf { line ->
            line.findArrangements().toLong()
        }
    }

    override fun solvePart2(input: Sequence<String>): Any {
        return 0
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
    println(this)
    valid.forEach {
        println(it.joinToString(""))
    }
    println()


    val correct = valid.count()
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