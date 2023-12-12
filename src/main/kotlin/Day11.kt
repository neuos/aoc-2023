import kotlin.math.abs

private const val GALAXY = '#'

object Day11 : Day(11) {
    override val expected = DayResult(374, "TODO", "TODO", "TODO")
    override fun solvePart1(input: Sequence<String>): Any {
        val grid = input.map { it.toList() }.toList()
        val noGalaxyRows = grid.map { row -> row.all { it != GALAXY } }
        val noGalaxyColumns = grid.first().indices.map { column -> grid.all { row -> row[column] != GALAXY } }

        val rowOffsets = noGalaxyRows.mapIndexed { index, b -> if (b) index else null }.filterNotNull()
        val rowIndices = grid.indices.map { row -> row + rowOffsets.filter { it < row }.size }


        val columnOffsets = noGalaxyColumns.mapIndexed { index, b -> if (b) index else null }.filterNotNull()
        val columnIndices = grid.first().indices.map { column -> column + columnOffsets.filter { it < column }.size }

        println("noGalaxyRows: $noGalaxyRows")
        println("rowOffsets: $rowOffsets")
        println("rowIndices: $rowIndices")

        println("noGalaxyColumns: $noGalaxyColumns")
        println("columnOffsets: $columnOffsets")
        println("columnIndices: $columnIndices")

        val coordinates = grid.flatMapIndexed { x, row ->
            row.mapIndexedNotNull { y, c ->
                if (c == GALAXY) Coordinate(rowIndices[x], columnIndices[y]) else null
            }
        }

        println("coordinates: $coordinates")

        return coordinates.pairs().map { (a, b) ->
            manhattanDistance(a, b)
        }.reduce(Int::plus)
    }

    private fun manhattanDistance(a: Coordinate, b: Coordinate) = abs(a.x - b.x) + abs(a.y - b.y)

    override fun solvePart2(input: Sequence<String>): Any {
        return 0
    }

    private data class Coordinate(val x: Int, val y: Int) : Comparable<Coordinate> {
        override fun compareTo(other: Coordinate) = compareValuesBy(this, other, Coordinate::x, Coordinate::y)
        override fun toString() = "($x, $y)"
    }
}

private fun <E> List<E>.pairs(): List<Pair<E, E>> = indices.flatMap { i ->
    (i + 1..<size).map { j ->
        Pair(this[i], this[j])
    }
}
