object Day08 : Day(8) {
    override val expected = DayResult(2, 13939, 6, "TODO")
    override fun solvePart1(input: Sequence<String>): Any {
        val list = input.toList()
        val instructions = list.first().toCharArray()
        val nodes = list.drop(1).filter { it.isNotEmpty() }.map { Node.from(it) }.associateBy { it.name }

        var i = 0
        var current = "AAA"
        val end = "ZZZ"

        while (current != end) {
            val node = nodes[current]!!
            val left = nodes[node.left]!!
            val right = nodes[node.right]!!
            val instruction = instructions[i % instructions.size]
            when (instruction) {
                'L' -> {
                    current = left.name
                }

                'R' -> {
                    current = right.name
                }
            }
            i++
        }

        println(i)
        return i
    }

    override fun solvePart2(input: Sequence<String>): Any {
        val list = input.toList()
        val instructions = list.first().toCharArray()
        val nodes = list.drop(1).filter { it.isNotEmpty() }.map { Node.from(it) }.associateBy { it.name }

        var i = 0
        fun isEnd(node: Node) = node.name.endsWith('Z')
        var current = nodes.values.filter { it.name.endsWith('A') }

        while (current.any { !isEnd(it) }) {
            current = current.map { node ->
                when (val instruction = instructions[i % instructions.size]) {
                    'L' -> nodes[node.left] ?: error("Unknown node ${node.left}")
                    'R' -> nodes[node.right] ?: error("Unknown node ${node.right}")
                    else -> error("Unknown instruction $instruction")
                }
            }
            i++
        }

        println(i)
        return i
    }
}

data class Node(val name: String, val left: String, val right: String) {
    companion object {
        fun from(line: String): Node {
            val split = line.split(' ', '=', '(', ',', ')').filter { it.isNotEmpty() }
            return Node(split[0], split[1], split[2])
        }
    }
}
