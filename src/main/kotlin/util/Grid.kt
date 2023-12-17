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