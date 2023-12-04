import kotlin.math.pow

object Day04 : Day(4) {
    override val expected = DayResult(13, 22193, 30, 0)
    override fun solvePart1(input: Sequence<String>): Any {
        return input.filter { it.isNotBlank() }.map {
            it.split(':')
        }.map { (name, values) ->
            values.split(" | ").map { it.split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet() }
        }.map { (winning, have) ->
            println("$have $winning")
            val filter = have.filter { it in winning }
            println(filter)
            filter.size
        }.filter { it > 0 }.map {
            println(it)
            2.0.pow(it - 1).toInt()
        }.sum()
    }

    override fun solvePart2(input: Sequence<String>): Any {
        val cards = input.filter { it.isNotBlank() }.map {
            it.split(':')
        }.map { (name, values) ->
            values.split(" | ").map { it.split(" ").filter { it.isNotEmpty() }.map { it.toInt() }.toSet() }
        }.map { (winning, have) ->
//            println("$have $winning")
            val filter = have.filter { it in winning }
//            println(filter)
            filter.size
        }.mapIndexed { index, winCount ->
            index to if (winCount > 0) (index + 1..index + winCount) else emptyList()
        }.sortedBy { it.second.count() }.toList()

        val total = mutableMapOf<Int, Int?>()
        while (cards.any { it.first !in total }){
            cards.filter { it.first !in total }.forEach { (index, cards) ->
                val winnings = cards.map { total[it] }
                println("card $index cards $cards winnings $winnings")
                if (!winnings.any { it == null }) {
                    total[index] = winnings.filterNotNull().sum() + 1
                    println("$index ${total[index]}")
                }
            }
        }

        println(total)
        println(total.values.sumOf { it!! })
        return total.values.sumOf { it!! }
    }


}