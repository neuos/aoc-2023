import kotlin.math.pow

object Day04 : Day(4) {
    override val expected = DayResult(13, 0)
    override fun solvePart1(input: Sequence<String>): Any {
        return input.filter { it.isNotBlank() }.map {
            it.split(':')
        }.map { (name, values)->
            values.split(" | ").map { it.split(" ").filter {it.isNotEmpty() }.map { it.toInt() }.toSet() }
        }.map { (winning, have) ->
            println("$have $winning")
            val filter = have.filter { it in winning }
            println(filter)
            filter.size
        }.filter { it > 0 }.map {
            println(it)
            2.0.pow(it-1).toInt() }.sum()
    }

    override fun solvePart2(input: Sequence<String>): Any {
        TODO("Not yet implemented")
    }


}