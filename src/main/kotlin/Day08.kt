object Day08 : Day(8) {
    override val expected = DayResult(2, 13939, 6L, "TODO")
    override fun solvePart1(input: Sequence<String>): Any {
        val (instructions, nodes) = parseInput(input)

        var i = 0
        var current = nodes["AAA"]!!
        val end = nodes["ZZZ"]!!

        while (current != end) {
            current = applyInstruction(nodes, current, instructions[i % instructions.size])
            i++
        }

        println(i)
        return i
    }

    private fun applyInstruction(
        nodes: Map<String, Node>, current: Node, instruction: Char
    ) = when (instruction) {
        'L' -> nodes[current.left]!!
        'R' -> nodes[current.right]!!
        else -> error("Unknown instruction $instruction")
    }

    override fun solvePart2(input: Sequence<String>): Any {
        val (instructions, nodes) = parseInput(input)


        val startingNodes = nodes.values.filter { it.name.endsWith('A') }
        val loops = startingNodes.map { findPath(nodes, it, instructions) }

        val canUseLGM = loops.all { path -> path.loopEnds.keys.singleOrNull() == path.loopLength }
        if (canUseLGM) {
            return loops.map { it.ends.keys.single().toLong() }.reduce { acc, i -> leastCommonMultiple(acc, i) }

        } else {
            val ends = loops.map { it.endIndices().asIterable().iterator() }
            while (true) {
                val next = ends.map { it.next() }
//            if (next.first() > 1000) return 0
                println("next $next")
                if (next.allEquals()) {
                    println(next.first())
                    return next.first()
                }
                val max = next.max()
                ends.filterIndexed { index, _ -> next[index] < max }.forEach { end ->
                    end.takeWhile { it < max }
                }
            }
        }


    }

    private fun leastCommonMultiple(a: Long, b: Long) = a * b / greatestCommonDivisor(a, b)

    private tailrec fun greatestCommonDivisor(a: Long, b: Long): Long = if (b == 0L) a
    else greatestCommonDivisor(b, a % b)

    private fun <T> Iterator<T>.takeWhile(condition: (T) -> Boolean) {
        while (condition(next())) {
        }
    }

    private fun <T> Iterable<T>.allEquals() = firstOrNull()?.let { first -> drop(1).all { it == first } } ?: true

    fun isEnd(node: Node) = node.name.endsWith('Z')
    fun findPath(nodes: Map<String, Node>, start: Node, instructions: CharArray): Path {
        var i = 0
        var current = start
        val visited = mutableSetOf<Pair<Node, Int>>()
        val ends = mutableMapOf<Int, Node>()

        while (current to i.mod(instructions.size) !in visited) {
            if (isEnd(current)) {
                ends[i] = current
            }

            val instruction = instructions[i.mod(instructions.size)]
            visited.add(current to i.mod(instructions.size))
            current = applyInstruction(nodes, current, instruction)
            i++
        }

        i = i.mod(instructions.size)
        val inLoop = mutableSetOf<Pair<Node, Int>>()
        while (current to i !in inLoop) {
            inLoop.add(current to i)
            current = applyInstruction(nodes, current, instructions[i])
            i = (i + 1).mod(instructions.size)
        }

        val loopLength = inLoop.size
        val preLoopLength = visited.size - loopLength
//        println("visited $visited")
//        println("inLoop $inLoop")
        return Path(preLoopLength, loopLength, ends)
    }

    data class Path(val preLoopLength: Int, val loopLength: Int, val ends: Map<Int, Node>) {
        val loopEnds = ends.filter { it.key >= preLoopLength }
        val length = preLoopLength + loopLength

        override fun toString(): String {
            return "Path(preLoopLength=$preLoopLength, loopLength=$loopLength, ends=$ends, loopEnds=$loopEnds, length=$length)"
        }

        fun endIndices() = generateSequence(0L) { it + 1 }.flatMap { i ->
            ends.keys.map { it + loopLength * i }
        }
    }

    private fun parseInput(input: Sequence<String>): Pair<CharArray, Map<String, Node>> {
        val list = input.toList()
        val instructions = list.first().toCharArray()
        val nodes = list.drop(1).filter { it.isNotEmpty() }.map { Node.from(it) }.associateBy { it.name }
        return Pair(instructions, nodes)
    }
}

data class Node(val name: String, val left: String, val right: String) {
    companion object {
        fun from(line: String): Node {
            val split = line.split(' ', '=', '(', ',', ')').filter { it.isNotEmpty() }
            return Node(split[0], split[1], split[2])
        }
    }

    override fun toString() = "$name($left, $right)"
}

fun main() {

    Day08.part2Example()
    print(Day08.part2())
}