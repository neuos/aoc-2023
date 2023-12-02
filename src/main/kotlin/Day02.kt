object Day02 : Day(2) {
    override fun solvePart1(input: Sequence<String>): Any {
        return input.filter { it.isNotBlank() }.map { it.split(':') }.map { (gamename, roundsString) ->
            val gameId = gamename.split(' ').last().toInt()
            val rounds = roundsString.split(';').map { round ->
                round.split(',').map {
                    val (amount, color) = it.trim().split(' ')
                    color to amount.toInt()
                }
            }
            val red = 12
            val green = 13
            val blue = 14

            val isPossible = rounds.all { round ->
                round.all { (color, amount) ->

                        val possible = when (color) {
                            "red" -> amount <= red
                            "green" -> amount <= green
                            "blue" -> amount <= blue
                            else -> error("Unknown color $color")
                        }
                        if (!possible) {
                            println("Game $gameId is not possible because $color has $amount")
                        }
                        possible

                }
            }
            println(roundsString)
            println(rounds)
            if (isPossible) {
                println("Game $gameId is possible")

                gameId
            } else {
                println("Game $gameId is not possible")
                0
            }
        }.sum()
    }

    override fun solvePart2(input: Sequence<String>): Any {
        TODO("Not yet implemented")
    }

    override val expected = DayResult(8, 2101, 2286, 0)
}