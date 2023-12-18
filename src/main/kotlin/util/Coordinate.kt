package util

import kotlin.math.abs

data class Coordinate(val vertical: Long, val horizontal: Long) : Comparable<Coordinate> {
    constructor(x: Int, y: Int) : this(x.toLong(), y.toLong())

    override fun compareTo(other: Coordinate) =
        compareValuesBy(this, other, Coordinate::vertical, Coordinate::horizontal)

    override fun toString() = "($vertical, $horizontal)"
    fun isAboveOf(other: Coordinate) = equals(other.up())
    fun isBelowOf(other: Coordinate) = equals(other.down())
    fun isLeftOf(other: Coordinate) = equals(other.left())
    fun isRightOf(other: Coordinate) = equals(other.right())

    fun up() = copy(vertical = vertical - 1)
    fun down() = copy(vertical = vertical + 1)
    fun left() = copy(horizontal = horizontal - 1)
    fun right() = copy(horizontal = horizontal + 1)
}

operator fun <T> Grid<T>.get(at: Coordinate) = get(at.vertical.toInt())[at.horizontal.toInt()]

fun <T> Grid<T>.coordinates(): Sequence<Coordinate> = sequence {
    for (x in indices) {
        for (y in get(x).indices) {
            yield(Coordinate(x, y))
        }
    }
}

fun Coordinate.adjacent() = listOf(up(), down(), left(), right())
fun <T> Grid<T>.adjacent(coordinate: Coordinate) = coordinate.adjacent().filter { contains(it) }

operator fun <T> Grid<T>.contains(coordinate: Coordinate) =
    coordinate.vertical in indices && coordinate.horizontal in get(coordinate.vertical.toInt()).indices

fun manhattanDistance(a: Coordinate, b: Coordinate) = abs(a.vertical - b.vertical) + abs(a.horizontal - b.horizontal)


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