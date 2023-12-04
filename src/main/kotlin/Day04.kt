import kotlin.math.pow

object Day04 : Day(4) {
    override val expected = DayResult(13, 22193, 30, 5625994)
    override fun solvePart1(input: Sequence<String>): Any {
        return input.parseCards().map { it.winCount }.filter { it > 0 }.map {
            2.0.pow(it - 1).toInt()
        }.sum()
    }

    override fun solvePart2(input: Sequence<String>): Any {
        val cards = input.parseCards().map { (index, winCount) ->
            val winsCards = if (winCount > 0) (index + 1..index + winCount) else emptyList()
            index to winsCards.toSet()
        }.sortedBy { it.second.count() }.toList()

        val total = mutableMapOf<Int, Int>()
        do {
            val unprocessed = cards.filter { (index, _) -> index !in total }
            unprocessed.forEach { (index, cards) ->
                if ((cards - total.keys).isEmpty()) {
                    total[index] = cards.map { total[it] }.sumOf { it!! } + 1
                }
            }
        } while (unprocessed.isNotEmpty())
        return total.values.sumOf { it }
    }

    private fun Sequence<String>.parseCards() = filter { it.isNotBlank() }.map {
        it.split(':')[1]
    }.map { values ->
        values.split(" | ").map { numbers -> numbers.split(' ').filter { it.isNotEmpty() }.map { it.toInt() }.toSet() }
    }.map { (winning, have) ->
        winning.intersect(have).size
    }.mapIndexed { index, winCount ->
        Card(index, winCount)
    }

    data class Card(val index: Int, val winCount: Int)

}