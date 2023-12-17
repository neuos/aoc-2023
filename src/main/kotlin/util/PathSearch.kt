package util

import java.util.*

fun <Node> findShortestPath(
    start: Node,
    isEnd: (Node) -> Boolean,
    next: (Node) -> Iterable<Node>,
    cost: (from: Node, to: Node) -> Int,
): List<Node>? {
    val toVisit = PriorityQueue(listOf(NodeCost(start, 0)))
    val seenPoints: MutableMap<Node, SeenNode<Node>> = mutableMapOf(start to SeenNode(0, null))

    while (toVisit.isNotEmpty()) {
        val (currentNode, currentScore) = toVisit.remove()
        if (isEnd(currentNode)) {
            val path = reconstructPath(currentNode, seenPoints)
            return path
        }

        val nextPoints = next(currentNode).filter {
            it !in seenPoints
        }.map { NodeCost(it, currentScore + cost(currentNode, it)) }

        toVisit.addAll(nextPoints)
        seenPoints.putAll(nextPoints.associate { it.node to SeenNode(it.cost, currentNode) })
    }

    return null
}

private fun <Node> reconstructPath(end: Node, seenPoints: MutableMap<Node, SeenNode<Node>>): List<Node> {
    var current: Node? = end
    return buildList<Node> {
        while (current != null) {
            add(current!!)
            current = seenPoints[current]?.prev
        }
    }.reversed()
}

data class SeenNode<Node>(private val cost: Int, val prev: Node?)

data class NodeCost<Node>(val node: Node, val cost: Int) : Comparable<NodeCost<Node>> {
    override fun compareTo(other: NodeCost<Node>): Int = compareValuesBy(this, other, NodeCost<Node>::cost)
}
