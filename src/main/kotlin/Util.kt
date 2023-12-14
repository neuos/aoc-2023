import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

/**
 * Combine two maps, if a key is in both maps, the values are merged with the merge function
 */
fun <K, V> Map<K, V>.combine(other: Map<K, V>, merge: (V, V) -> V): Map<K, V> {
    val allKeys = this.keys + other.keys
    return allKeys.associateWith { key ->
        val a = this[key]
        val b = other[key]
        when {
            a == null -> b!!
            b == null -> a
            else -> merge(a, b)
        }
    }
}

/**
 * Convert a list of strings to a Grid of chars
 */
fun Iterable<String>.charGrid(): CharGrid = toList().map { it.toList() }


/**
 * Chunk a list at the elements where the condition is true
 * The elements where the condition is true are not included in the result
 */
fun <T> List<T>.chunkAt(condition: (T) -> Boolean): List<List<T>> = flatMapIndexed { index, x ->
    when {
        index == 0 || index == lastIndex -> listOf(index)
        condition(x) -> listOf(index - 1, index + 1)
        else -> emptyList()
    }
}.chunked(size = 2) { (from, to) -> slice(from..to) }

object RangeExtensions {
    fun LongRange.without(others: List<LongRange>): List<LongRange> = others.fold(listOf(this)) { difference, range ->
        difference.flatMap { it.without(range) }
    }

    fun LongRange.without(other: LongRange): List<LongRange> {
        val leading = start..<other.first
        val trailing = (other.last + 1)..last
        return listOf(leading, trailing).filter { !it.isEmpty() }
    }

    fun LongRange.overlaps(sourceRange: LongRange) = !overlap(sourceRange).isEmpty()
    fun LongRange.overlap(other: LongRange): LongRange {
        val start = maxOf(first, other.first)
        val end = minOf(last, other.last)
        return start..end
    }

    val LongRange.length: Long get() = last - first + 1
}

/**
 * Returns the Int value nearest to this value in direction of positive infinity.
 */
fun Double.nextUpInt() = ceil(this).let { if (it == this) it + 1 else it }.toInt()

/**
 * Returns the Int value nearest to this value in direction of negative infinity.
 */
fun Double.nextDownInt() = floor(this).let { if (it == this) it - 1 else it }.toInt()

/**
 * Returns the next representable floating-point value after this value in direction of positive infinity.
 */
fun solveQuadratic(p: Double, q: Double): Pair<Double, Double> {
    val root = sqrt((p * p) / 4 - q)
    val lower = -p / 2 - root
    val higher = -p / 2 + root
    return lower to higher
}

fun leastCommonMultiple(a: Long, b: Long) = a * b / greatestCommonDivisor(a, b)

tailrec fun greatestCommonDivisor(a: Long, b: Long): Long = if (b == 0L) a
else greatestCommonDivisor(b, a % b)


fun <T> Iterable<T>.allEquals() = firstOrNull()?.let { first -> all { it == first } } ?: true


typealias Grid<T> = List<List<T>>
typealias CharGrid = Grid<Char>


fun CharGrid.coordinates(): Sequence<Coordinate> = sequence {
    for (x in indices) {
        for (y in get(x).indices) {
            yield(Coordinate(x, y))
        }
    }
}

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

operator fun <T> Grid<T>.get(other: Coordinate) = get(other.x.toInt())[other.y.toInt()]

fun CharGrid.adjacent(coordinate: Coordinate) = listOf(
    coordinate.left(), coordinate.right(), coordinate.up(), coordinate.down()
).filter { contains(it) }

fun CharGrid.contains(coordinate: Coordinate) =
    coordinate.x in indices && coordinate.y in get(coordinate.x.toInt()).indices

fun Sequence<String>.toGrid(): CharGrid = map { line -> line.toList() }.toList()

fun manhattanDistance(a: Coordinate, b: Coordinate) = abs(a.x - b.x) + abs(a.y - b.y)

fun <E> List<E>.pairs(): List<Pair<E, E>> = indices.flatMap { i ->
    (i + 1..<size).map { j ->
        Pair(this[i], this[j])
    }
}

class CachedRecursion<Args, Res>(private val function: (rec: CachedRecursion<Args, Res>, Args) -> Res) : (Args) -> Res {
    private val cache: MutableMap<Args, Res> = mutableMapOf()
    override operator fun invoke(args: Args) = cache.getOrPut(args) { function(this, args) }
}

fun <E> List<E>.repeat(count: Int) = (1..count).flatMap { this }

fun List<Char>.parseBinary(one: Char = '1'): Long = mapIndexed { index, c ->
    if (c == one) 1L shl index else 0L
}.reduce { acc, l ->
    acc or l
}

fun isPowerOfTwo(n: Long) = n != 0L && (n and n - 1) == 0L
fun oneBitDiff(a: Long, b: Long): Boolean {
    val diff = a xor b
    return isPowerOfTwo(diff)
}


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