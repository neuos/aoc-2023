import util.allEquals
import util.leastCommonMultiple

object Day08 : Day(8) {
    override val expected = DayResult(2, 13939, 6L, 8906539031197L)
    override fun solvePart1(input: Sequence<String>): Any {
        fun isExit(node: Node) = node.name == "ZZZ"
        val (instructions, nodes) = parseInput(input)
        var current = nodes["AAA"]!!
        var steps = 0
        while (!isExit(current)) {
            val instruction = instructions[steps % instructions.size]
            current = applyInstruction(nodes, current, instruction)
            steps++
        }
        return steps
    }


    override fun solvePart2(input: Sequence<String>): Any {
        val (instructions, nodes) = parseInput(input)
        val startingNodes = nodes.values.filter { it.name.endsWith('A') }
        val loops = startingNodes.map { findPath(nodes, it, instructions) }
        val canUseLGM = loops.all { path -> path.loopExits.keys.singleOrNull() == path.loopLength }
        if (canUseLGM) {
            // works in actual input, but not in example
            return loops.map { it.exits.keys.single().toLong() }.reduce { acc, i -> leastCommonMultiple(acc, i) }
        } else {
            // brute force solver for example input
            var ends = loops.map { it.endIndices() }
            while (true) {
                val next = ends.map { it.first() }
                if (next.allEquals()) {
                    return next.first()
                }
                val max = next.max()
                ends = ends.map { end ->
                    end.dropWhile { it < max }
                }
            }
        }
    }

    private fun applyInstruction(
        nodes: Map<String, Node>, current: Node, instruction: Char
    ) = when (instruction) {
        'L' -> nodes[current.left]!!
        'R' -> nodes[current.right]!!
        else -> error("Unknown instruction $instruction")
    }


    private fun findPath(nodes: Map<String, Node>, start: Node, instructions: CharArray): Path {
        fun isExit(node: Node) = node.name.endsWith('Z')
        val visited = mutableSetOf<Pair<Node, Int>>()
        val exits = mutableMapOf<Int, Node>()
        var node = start
        var steps = 0
        while (true) {
            val index = steps % instructions.size
            val key = node to index
            if (key in visited) break
            if (isExit(node)) {
                exits[steps] = node
            }

            visited.add(key)
            node = applyInstruction(nodes, node, instructions[index])
            steps++
        }

        val inLoop = mutableSetOf<Pair<Node, Int>>()
        while (true) {
            val index = steps % instructions.size
            val key = node to index
            if (key in inLoop) break
            inLoop.add(key)
            node = applyInstruction(nodes, node, instructions[index])
            steps++
        }

        val loopLength = inLoop.size
        val preLoopLength = visited.size - loopLength
        return Path(preLoopLength, loopLength, exits)
    }

    data class Path(val preLoopLength: Int, val loopLength: Int, val exits: Map<Int, Node>) {
        val loopExits = exits.filter { it.key >= preLoopLength }

        override fun toString(): String {
            return "Path(preLoopLength=$preLoopLength, loopLength=$loopLength, ends=$exits, loopEnds=$loopExits)"
        }

        fun endIndices() = generateSequence(0L) { it + 1 }.flatMap { i ->
            exits.keys.map { it + loopLength * i }
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

    private fun parseInput(input: Sequence<String>): Pair<CharArray, Map<String, Node>> {
        val list = input.toList()
        val instructions = list.first().toCharArray()
        val nodes = list.drop(1).filter { it.isNotEmpty() }.map { Node.from(it) }.associateBy { it.name }
        return Pair(instructions, nodes)
    }
}

