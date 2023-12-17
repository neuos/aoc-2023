package util

typealias Grid<T> = List<List<T>>
typealias CharGrid = Grid<Char>

fun Sequence<String>.toGrid(): CharGrid = toList().toGrid()
fun List<String>.toGrid(): CharGrid = toList().map { it.toList() }

fun Sequence<String>.toIntGrid(): Grid<Int> =
    filter { it.isNotEmpty() }.map { it.map(Char::digitToInt).toList() }.toList()

fun <E> Grid<E>.rotateAnticlockwise() = this.indices.map { i ->
    this.indices.map { j ->
        this[j][i]
    }
}.reversed()

fun <E> Grid<E>.rotateClockwise() = this.indices.map { i ->
    this.indices.map { j ->
        this[j][i]
    }.reversed()
}

data class Bounds(private val vertical: IntRange, private val horizontal: IntRange) {
    val topLeft = Coordinate(vertical.first, horizontal.first)
    val topRight = Coordinate(vertical.first, horizontal.last)
    val bottomLeft = Coordinate(vertical.last, horizontal.first)
    val bottomRight = Coordinate(vertical.last, horizontal.last)
}

val Grid<*>.bounds get() = Bounds(indices, first().indices)