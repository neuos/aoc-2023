object Day12 : Day(12) {
    override val expected = DayResult(21, 8270, 525152, "TODO")
    override fun solvePart1(input: Sequence<String>) = input.sumOf { line ->
//        line.findArrangements()
        Finder.find(line)
    }

    override fun solvePart2(input: Sequence<String>) = input.sumOf { line ->
        val (springs, lengthString) = line.split(" ")
        val unfolded = "$springs?".repeat(5).dropLast(1) + " " + "$lengthString,".repeat(5).dropLast(1)
        Finder.find(unfolded)
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

class Finder private constructor(val string: String, private val groups: List<Int>) {
    companion object {
        fun find(line: String): Long {
            val (springs, lengthString) = line.split(" ")
            val requiredLengths = lengthString.split(",").map { it.toInt() }
            val arrangements = Finder(springs, requiredLengths).search()
            println("found $arrangements arrangements for $line")
            return arrangements
        }
    }

    private val minSprings = string.countRemaining('#')
    private val maxSprings = minSprings.zip(string.countRemaining('?'), Int::plus)

    private data class Args(val i: Int, val groups: List<Int>, val running: Int)

    fun search() = findArrangements(Args(0, groups, 0))

    private val cache: MutableMap<Args, Long> = mutableMapOf()
    private fun findArrangements(args: Args) = cache.getOrPut(args) { findUncached(args) }

    private fun findUncached(args: Args): Long {
        fun debug(msg: Any) {
            if (DEBUG) println("${"  ".repeat(args.i)} $msg")
        }
        val (i, groups, running) = args
        debug("$args checking '${string.getOrNull(i)}'")
        debug("minSprings: ${minSprings.getOrNull(i)}")
        debug("maxSprings: ${maxSprings.getOrNull(i)}")
        if (i == string.length) {
            if (groups.isEmpty() && running == 0 || groups.singleOrNull() == running) {
                debug("found a valid arrangement")
                return 1
            } else {
                debug("end of string but still groups left")
                return 0
            }
        }
        val remaining = groups.sum() - running
        debug("remaining: $remaining")
        if (remaining !in minSprings[i]..maxSprings[i]) {
            debug("not enough springs left");
            return 0
        }
//        if (groups.isEmpty()) if (minSprings[i] == 0 && running == 0) {
//            debug("found a valid arrangement: $str")
//            return 1
//        } else {
//            debug("too many springs left");
//            return 0
//        }

        val damagedCase = {
            debug("damagedCase")
            if (running == 0) {
                findArrangements(Args(i + 1, groups, 0))
            } else {
                // check if a group just finished
                if (running == groups.firstOrNull()) {
                    debug("group finished")
                    findArrangements(Args(i + 1, groups.drop(1), 0))
                } else {
                    debug("group is wrong size")
                    0
                }
            }
        }
        val operationalCase = {
            debug("operationalCase")
            findArrangements(Args(i + 1, groups, running + 1))
        }
        return when (string[i]) {
            '.' -> damagedCase()
            '#' -> operationalCase()
            else -> {
                debug("optional, trying both")
                damagedCase() + operationalCase()
            }
        }
    }
}

fun String.countRemaining(char: Char) = buildList {
    var count = 0
    this@countRemaining.reversed().forEach {
        if (it == char) count++
        add(count)
    }
}.reversed()


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

const val DEBUG = false
fun main() {
//    println(Finder.find("?###???????? 3,2,1"))
    println(Day12.part1())
}