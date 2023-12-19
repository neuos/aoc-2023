import util.Coordinate
import util.Direction
import util.Direction.*
import util.adjacent
import util.plus

object Day18 : Day(18) {
    override val expected = DayResult(62, 39039, 952408144115L, "TODO")

    data class Instruction(val direction: Direction, val length: Int) {

        companion object {
            fun parse(input: String): Instruction {
                val (dir, len, _) = input.split(" ")
                val direction = when (dir) {
                    "R" -> RIGHT
                    "D" -> DOWN
                    "L" -> LEFT
                    "U" -> UP
                    else -> throw IllegalArgumentException("Unknown direction $dir")
                }
                val length = len.toInt()
                return Instruction(direction, length)
            }

            fun parseHex(input: String): Instruction {
                val hex = input.substringAfter('#').substringBefore(')')
                val length = hex.substring(0, 4).toInt(16)
                val direction = when (hex.last()) {
                    '0' -> RIGHT
                    '1' -> DOWN
                    '2' -> LEFT
                    '3' -> UP
                    else -> throw IllegalArgumentException("Unknown direction in $hex")
                }
                return Instruction(direction, length)
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

    override fun solvePart1(input: Sequence<String>): Int {
        val instructions = input.map(Instruction.Companion::parse).toList()
        val border = buildList<Coordinate> {
            add(Coordinate(0, 0))
            instructions.forEach {
                val last = last()
                val next = last + it
                addAll(next)
            }
        }
        val count = floodFill(border.toSet())
        println("count: $count")
        return count
    }

    private fun floodFill(border: Set<Coordinate>): Int {
        val firstInside = border.min() + DOWN + RIGHT
        var next = listOf(firstInside)
        val counted = mutableSetOf(firstInside)
        while (next.isNotEmpty()) {
            counted += next
            next = next.flatMap { it.adjacent().filter { it !in border && it !in counted } }.distinct()
        }
        return counted.size + border.size
    }

    override fun solvePart2(input: Sequence<String>): Long {
        val instructions = input.map(Instruction.Companion::parseHex).toList()
        val coordinates = buildList<Coordinate> {
            add(Coordinate(0, 0))
            instructions.forEach {
                val last = last()
                val next = last + it
                addAll(next)
            }
        }
        val count = floodFill(coordinates.toSet())
        println("count: $count")
        return count.toLong()

    }
}