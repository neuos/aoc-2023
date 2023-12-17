package util

import java.util.*

fun <Node> findShortestPath(
    start: Node,
    isEnd: (Node) -> Boolean,
    next: (Node) -> Iterable<Node>,
    cost: (from: Node, to: Node) -> Int,
): List<Node>? {
    data class NodeCost<Node>(val node: Node, val cost: Int) : Comparable<NodeCost<Node>> {
        override fun compareTo(other: NodeCost<Node>): Int = compareValuesBy(this, other, NodeCost<Node>::cost)
    }

    val toVisit = PriorityQueue(listOf(NodeCost(start, 0)))
    val previous: MutableMap<Node, Node?> = mutableMapOf(start to null)

    while (toVisit.isNotEmpty()) {
        val (currentNode, currentScore) = toVisit.remove()
        if (isEnd(currentNode)) {
            return reconstructPath(currentNode, previous)
        }

        next(currentNode).filter {
            it !in previous
        }.map {
            NodeCost(it, currentScore + cost(currentNode, it))
        }.forEach {
            toVisit.add(it)
            previous[it.node] = currentNode
        }
    }
    return null
}

private fun <Node> reconstructPath(end: Node, previous: MutableMap<Node, Node?>): List<Node> {
    var current: Node? = end
    return buildList {
        while (current != null) {
            add(current!!)
            current = previous[current]
        }
    }.reversed()
}

