object Day07 : Day(7) {
    override val expected = DayResult(6440, "TODO", "TODO", "TODO")
    override fun solvePart1(input: Sequence<String>): Any {
        return input.toList().filter { it.isNotEmpty() }.map { Hand(it) }.sorted()
            .also { println(it.joinToString("\n")) }.mapIndexed { index, hand ->
            (index + 1) * hand.bid
        }.sum()
    }

    override fun solvePart2(input: Sequence<String>): Any {
        return 0
    }
}

class Hand(line: String) : Comparable<Hand> {
    val cards = line.split(" ")[0].map { it.toCard() }
    val bid = line.split(" ")[1].toInt()
    val type = cards.groupBy { it }.map { it.value.size }.sortedDescending().let {
        println("get type for $cards: $it")
        when (it) {
            listOf(5) -> 6
            listOf(4, 1) -> 5
            listOf(3, 2) -> 4
            listOf(3, 1, 1) -> 3
            listOf(2, 2, 1) -> 2
            listOf(2, 1, 1, 1) -> 1
            else -> 0
        }
    }

    override fun compareTo(other: Hand): Int {
        val byType = type.compareTo(other.type)
        return if (byType != 0) {
            byType
        } else {
            cards.zip(other.cards).map { it.first.compareTo(it.second) }.firstOrNull { it != 0 } ?: 0
        }
    }

    override fun toString(): String {
        return "Hand(cards=${cards.map { cardOrder[it] }.sorted().joinToString("")}, bid=$bid, type=$type)"
    }
}

private val cardOrder = listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')

private fun Char.toCard() = cardOrder.indexOf(this)
