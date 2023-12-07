import Type.*

object Day07 : Day(7) {
    override val expected = DayResult(6440, 246424613, 5905L, "TODO")
    override fun solvePart1(input: Sequence<String>): Any {
        return input.toList().filter { it.isNotEmpty() }.map { Hand1(it) }.sorted()
            .also { println(it.joinToString("\n")) }.mapIndexed { index, hand ->
                (index + 1) * hand.bid
            }.sum()
    }

    override fun solvePart2(input: Sequence<String>): Long {
        return input.toList().filter { it.isNotEmpty() }.map { Hand2(it) }.sorted()
            .also { println(it.joinToString("\n")) }.mapIndexed { index, hand ->
                (index + 1) * hand.bid.toLong()
            }.sum()
    }
}

class Hand1(line: String) : Comparable<Hand1> {
    val cards = line.split(" ")[0].map { it.toCard() }
    val bid = line.split(" ")[1].toInt()
    val type = cards.groupBy { it }.map { it.value.size }.sortedDescending().let {
        println("get type for $cards: $it")
        when (it) {
            listOf(5) -> FIVE_OF_A_KIND
            listOf(4, 1) -> FOUR_OF_A_KIND
            listOf(3, 2) -> FULL_HOUSE
            listOf(3, 1, 1) -> THREE_OF_A_KIND
            listOf(2, 2, 1) -> TWO_PAIRS
            listOf(2, 1, 1, 1) -> ONE_PAIR
            else -> HIGH_CARD
        }
    }

    override fun compareTo(other: Hand1): Int {
        val byType = type.compareTo(other.type)
        return if (byType != 0) {
            byType
        } else {
            cards.zip(other.cards).map { it.first.compareTo(it.second) }.firstOrNull { it != 0 } ?: 0
        }
    }

    override fun toString(): String {
        return "Hand(cards=${
            cards.map { Companion.cardOrder[it] }.sorted().joinToString("")
        }, bid=$bid, type=${type.name})"
    }

    private fun Char.toCard() = Companion.cardOrder.indexOf(this)

    companion object {
        private val cardOrder = listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')
    }
}

enum class Type {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIRS,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND;
}


class Hand2(line: String) : Comparable<Hand2> {
    val cards = line.split(" ")[0].map { it.toCard() }
    val bid = line.split(" ")[1].toInt()
    private val cardCounts = cards.groupBy { it }.map { it.value.size to it.key }.sortedByDescending { it.first }

    private val maxCard = cardCounts.map { it.second }.firstOrNull { !it.isJoker() } ?: 0


    val type =
        cards.map {
            if (it.isJoker()) {
                println("replace joker with $maxCard in $cards")
                maxCard
            } else it
        }.groupBy { it }.map { it.value.size }.sortedDescending().let {
            println("get type for $cards: $it")
            when (it) {
                listOf(5) -> FIVE_OF_A_KIND
                listOf(4, 1) -> FOUR_OF_A_KIND
                listOf(3, 2) -> FULL_HOUSE
                listOf(3, 1, 1) -> THREE_OF_A_KIND
                listOf(2, 2, 1) -> TWO_PAIRS
                listOf(2, 1, 1, 1) -> ONE_PAIR
                else -> HIGH_CARD
            }
        }

    private fun Int.isJoker() = this == 0

    override fun compareTo(other: Hand2): Int {
        val byType = type.compareTo(other.type)
        return if (byType != 0) {
            byType
        } else {
            cards.zip(other.cards).map { it.first.compareTo(it.second) }.firstOrNull { it != 0 } ?: 0
        }
    }

    override fun toString(): String {
        return "Hand(cards=${cards.map { Companion.cardOrder[it] }.sorted().joinToString("")}, bid=$bid, type=$type)"
    }

    private fun Char.toCard() = Companion.cardOrder.indexOf(this)

    companion object {
        private val cardOrder = listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A')
    }
}


