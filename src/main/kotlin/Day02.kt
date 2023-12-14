import util.combine
import kotlin.math.max

private data class Game(val id: Int, val rounds: List<Round>)
private typealias Color = String
private typealias Round = Map<Color, Int>

object Day02 : Day(2) {
    override val expected = DayResult(8, 2101, 2286, 58269)
    override fun solvePart1(input: Sequence<String>): Int {
        val available = mapOf(
            "red" to 12, "green" to 13, "blue" to 14
        )
        return input.parseGames().sumOf { game ->
            game.rounds.all { round ->
                round.all { (color, amount) ->
                    // the reveal is only possible if there are enough cubes available
                    amount <= (available[color] ?: 0)
                }
            }.let { possible -> if (possible) game.id else 0 }
        }
    }

    override fun solvePart2(input: Sequence<String>) = input.parseGames().sumOf { game ->
        // find the maximum amount of cubes for each color
        game.rounds.reduce { acc, round -> acc.combine(round, ::max) }.values.reduce(Int::times)
    }


    private fun Sequence<String>.parseGames(): Sequence<Game> =
        filter { it.isNotBlank() }.map { it.split(':') }.map { (gamename, roundsString) ->
            val gameId = gamename.split(' ').last().toInt()
            val rounds = roundsString.split(';').map { round ->
                round.split(',').map {
                    val (amount, color) = it.trim().split(' ')
                    color to amount.toInt()
                }.toMap()
            }
            Game(gameId, rounds)
        }
}

