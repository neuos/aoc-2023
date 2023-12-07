import Hand.Type.*

object Day07 : Day(7) {
    override val expected = DayResult(6440, 246424613, 5905, 248256639)
    override fun solvePart1(input: Sequence<String>) = input.totalWinnings(::Hand1)
    override fun solvePart2(input: Sequence<String>) = input.totalWinnings(::Hand2)

    private fun Sequence<String>.totalWinnings(handMapper: (String) -> Hand) =
        filter { it.isNotEmpty() }.map { handMapper(it) }.sorted().mapIndexed { index, hand ->
            hand.winnings(index + 1)
        }.sum()
}


@JvmInline
private value class Card(val value: Int)

private abstract class Hand(line: String) : Comparable<Hand> {
    protected enum class Type {
        HIGH_CARD, ONE_PAIR, TWO_PAIRS, THREE_OF_A_KIND, FULL_HOUSE, FOUR_OF_A_KIND, FIVE_OF_A_KIND;
    }

    protected abstract val cardOrder: List<Char>
    protected val cards = line.split(" ")[0].map { it.toCard() }
    private val bid = line.split(" ")[1].toInt()

    protected abstract val type: Type
    fun winnings(rank: Int) = rank * bid

    override fun compareTo(other: Hand) = comparator.compare(this, other)


    private fun Char.toCard() = Card(cardOrder.indexOf(this))

    protected companion object {
        private val comparator = (0..<5).fold(Comparator.comparing { h: Hand -> h.type }) { comp, i ->
            comp.thenComparingInt { h: Hand -> h.cards[i].value }
        }

        fun List<Card>.handType() = groupBy { it }.map { it.value.size }.sortedDescending().let {
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
    }
}

private class Hand1(line: String) : Hand(line) {
    override val type = cards.handType()

    private companion object {
        private val cardOrder = listOf('2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A')
    }

    override val cardOrder: List<Char> get() = Hand1.cardOrder
}


private class Hand2(line: String) : Hand(line) {
    // finds the card that is present the most times, but not a joker
    private val maxCard = cards.groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.map { it.key }
        .firstOrNull { !it.isJoker } ?: Card(0)

    override val type = cards.replaceJokers().handType()
    private fun List<Card>.replaceJokers(): List<Card> = map {
        if (it.isJoker) maxCard else it
    }

    private val Card.isJoker get() = value == 0

    private companion object {
        private val cardOrder = listOf('J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A')
    }

    override val cardOrder: List<Char> get() = Hand2.cardOrder
}
