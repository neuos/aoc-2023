object Day14 : Day(14) {
    override val expected = DayResult(136, 109596, 64, "todo")
    override fun solvePart1(input: Sequence<String>) = input.map { it.toList() }.toList().rotateAnticlockwise().map {
        it.tiltLineLeft()
    }.sumOf {
        weight(it)
    }

    override fun solvePart2(input: Sequence<String>): Any {

        val seen = mutableMapOf<List<List<Char>>, Int>()

        var grid = input.map { it.toList() }.toList().rotateAnticlockwise()

        var cycle = 0
        while (grid !in seen) {
            seen[grid] = cycle
            cycle++
            repeat(4) {
                grid = grid.tiltGridLeft().rotateClockwise()
            }
            println("cycle $cycle")
            grid.rotateClockwise().forEach { println(it.joinToString("")) }
            println("weight: ${grid.sumOf { weight(it) }}")
            println()
        }
        println("found cycle at $cycle")
        println("first: ${seen[grid]}")

        val iterations = 1000000000L
        val preCycle = seen[grid]!!
        val cycleLength = cycle - preCycle

        println("cycle length: $cycleLength")


        val resultIteration = (iterations - preCycle) % cycleLength + preCycle
        println("result cycle at: $resultIteration")

        val resultGrid = seen.entries.first { (_, index) -> index == resultIteration.toInt() }.key

        return resultGrid.sumOf {
            weight(it)
        }
    }

    private fun weight(it: List<Char>) = it.mapIndexed { index, c ->
        if (c == 'O') it.size - index else 0
    }.sum()

    private fun List<List<Char>>.tiltGridLeft() = map { it.tiltLineLeft() }
    private fun List<Char>.tiltLineLeft(): List<Char> {
        return joinToString("").split("#").joinToString("#") {
            it.toList().sortedDescending().joinToString("")
        }.toList()
    }


    private fun <E> List<List<E>>.rotateAnticlockwise(): List<List<E>> {
        val result = mutableListOf<List<E>>()
        for (i in this.indices) {
            val row = mutableListOf<E>()
            for (j in this.indices) {
                row.add(this[j][i])
            }
            result.add(row)
        }
        return result.reversed()
    }


    private fun <E> List<List<E>>.rotateClockwise(): List<List<E>> {
        val result = mutableListOf<List<E>>()
        for (i in this.indices) {
            val row = mutableListOf<E>()
            for (j in this.indices) {
                row.add(this[j][i])
            }
            result.add(row.reversed())
        }
        return result
    }
}

fun main() {
    print(Day14.part2())
}