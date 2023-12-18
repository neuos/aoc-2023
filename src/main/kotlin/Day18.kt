import util.Coordinate
import util.Direction
import util.Direction.*
import util.adjacent
import util.plus

object Day18 : Day(18) {
    override val expected = DayResult(62, 39039, "TODO", "TODO")

    data class Instruction(val direction: Direction, val length: Int, val color: String) {

        companion object {
            fun parse(input: String): Instruction {
                val (dir, len, col) = input.split(" ")
                val direction = when (dir) {
                    "U" -> UP
                    "D" -> DOWN
                    "L" -> LEFT
                    "R" -> RIGHT
                    else -> throw IllegalArgumentException("Unknown direction $dir")
                }
                val length = len.toInt()
                return Instruction(direction, length, col)
            }
        }
        override fun toString(): String {
            return "$direction $length"
        }
    }

    operator fun Coordinate.plus(instruction: Instruction): List<Coordinate> {
        val (horRange, vertRange) = when (instruction.direction) {
            UP -> (this.horizontal)..this.horizontal to ((this.vertical - 1) downTo this.vertical - instruction.length)
            DOWN -> (this.horizontal)..this.horizontal to ((this.vertical + 1)..this.vertical + instruction.length)
            LEFT -> ((this.horizontal - 1) downTo this.horizontal - instruction.length) to (this.vertical..this.vertical)
            RIGHT -> ((this.horizontal + 1)..this.horizontal + instruction.length) to (this.vertical..this.vertical)
        }
        return buildList {
            horRange.forEach { hor ->
                vertRange.forEach { vert ->
                    add(Coordinate(vert, hor))
                }
            }
        }
    }

    data class DirectionalCoordinate(val direction: Direction, val coordinate: Coordinate) {}

    override fun solvePart1(input: Sequence<String>): Any {
        val instructions = input.map(Instruction.Companion::parse).toList()


        val rawCoordinates = buildList<Coordinate> {
            add(Coordinate(0, 0))
            instructions.forEach {
                val last = last()
                val next = last + it
//                println("instruction: $it, last: $last, next: $next")
                addAll(next)
            }
        }

        val firstInner = rawCoordinates.first().let { Coordinate(it.vertical + 1, it.horizontal + 1) }

        val minWidth = rawCoordinates.minOf { it.horizontal }.toInt()
        val minHeight = rawCoordinates.minOf { it.vertical }.toInt()
        val coordinates = rawCoordinates.map { Coordinate(it.vertical - minHeight, it.horizontal - minWidth) }
        val maxWidth = coordinates.maxOf { it.horizontal }.toInt()
        val maxHeight = coordinates.maxOf { it.vertical }.toInt()
        println("maxWidth: $maxWidth, maxHeight: $maxHeight")
        val grid = List(maxHeight + 1) { MutableList(maxWidth + 1) { false } }


        val count = floodFill(coordinates)
        println("count: $count")


        coordinates.forEach { coord ->
            grid[coord.vertical.toInt()][coord.horizontal.toInt()] = true
        }

        grid.forEach { row ->
            row.forEach { cell ->
                print(if (cell) '#' else ' ')
            }
            println()
        }

        return count
    }

    private fun floodFill(coordinates: List<Coordinate>): Int {
        val firstInside = coordinates.min() + DOWN + RIGHT
        var next = listOf(firstInside)
        val counted = mutableSetOf(firstInside)
        while (next.isNotEmpty()) {
            counted += next
            next = next.flatMap { it.adjacent().filter { it !in coordinates && it !in counted }}.distinct()
        }
        return counted.size + coordinates.distinct().size
    }

    override fun solvePart2(input: Sequence<String>): Any {
        return 0
    }
}