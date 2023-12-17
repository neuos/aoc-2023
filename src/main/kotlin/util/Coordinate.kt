package util

import kotlin.math.abs

data class Coordinate(val x: Long, val y: Long) : Comparable<Coordinate> {
    constructor(x: Int, y: Int) : this(x.toLong(), y.toLong())

    override fun compareTo(other: Coordinate) = compareValuesBy(this, other, Coordinate::x, Coordinate::y)
    override fun toString() = "($x, $y)"
    fun isAboveOf(other: Coordinate) = equals(other.up())
    fun isBelowOf(other: Coordinate) = equals(other.down())
    fun isLeftOf(other: Coordinate) = equals(other.left())
    fun isRightOf(other: Coordinate) = equals(other.right())

    fun up() = copy(x = x - 1)
    fun down() = copy(x = x + 1)
    fun left() = copy(y = y - 1)
    fun right() = copy(y = y + 1)
}

operator fun <T> Grid<T>.get(at: Coordinate) = get(at.x.toInt())[at.y.toInt()]

fun <T> Grid<T>.coordinates(): Sequence<Coordinate> = sequence {
    for (x in indices) {
        for (y in get(x).indices) {
            yield(Coordinate(x, y))
        }
    }
}

fun <T> Grid<T>.adjacent(coordinate: Coordinate) = listOf(
    coordinate.left(), coordinate.right(), coordinate.up(), coordinate.down()
).filter { contains(it) }

operator fun <T> Grid<T>.contains(coordinate: Coordinate) =
    coordinate.x in indices && coordinate.y in get(coordinate.x.toInt()).indices

fun manhattanDistance(a: Coordinate, b: Coordinate) = abs(a.x - b.x) + abs(a.y - b.y)


enum class Direction {
    UP, DOWN, LEFT, RIGHT;

    val opposite: Direction
        get() = when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }

    fun relative(other: Direction) = when (other) {
        UP -> this
        DOWN -> opposite
        LEFT -> when (this) {
            UP -> LEFT
            DOWN -> RIGHT
            LEFT -> DOWN
            RIGHT -> UP
        }

        RIGHT -> when (this) {
            UP -> RIGHT
            DOWN -> LEFT
            LEFT -> UP
            RIGHT -> DOWN
        }
    }
}

operator fun Coordinate.plus(direction: Direction) = when (direction) {
    Direction.UP -> up()
    Direction.DOWN -> down()
    Direction.LEFT -> left()
    Direction.RIGHT -> right()
}