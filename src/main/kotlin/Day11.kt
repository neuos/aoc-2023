private const val GALAXY = '#'

object Day11 : Day(11) {
    override val expected = DayResult(374L, 10289334L, 82000210L, 649862989626)
    override fun solvePart1(input: Sequence<String>) = findDistances(input, 2L)
    override fun solvePart2(input: Sequence<String>) = findDistances(input, 1000000L)
    private fun findDistances(input: Sequence<String>, age: Long): Long {
        val grid = input.map { it.toList() }.toList()
        val expandingRows = grid.map { row -> row.all { it != GALAXY } }
        val expandingColumns = grid.first().indices.map { column -> grid.all { row -> row[column] != GALAXY } }

        val rowOffsets = expandingRows.mapIndexed { index, b -> if (b) index else null }.filterNotNull()
        val rowIndices = grid.indices.map { row -> row + rowOffsets.filter { it < row }.size * (age - 1) }

        val columnOffsets = expandingColumns.mapIndexed { index, b -> if (b) index else null }.filterNotNull()
        val columnIndices =
            grid.first().indices.map { column -> column + columnOffsets.filter { it < column }.size * (age - 1) }

        val galaxies = grid.flatMapIndexed { x, row ->
            row.mapIndexedNotNull { y, c ->
                if (c == GALAXY) Coordinate(rowIndices[x], columnIndices[y]) else null
            }
        }

        return galaxies.pairs().sumOf { (a, b) ->
            manhattanDistance(a, b)
        }
    }
}

